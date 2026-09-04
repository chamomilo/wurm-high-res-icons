package org.wurmhighres.client;

import com.wurmonline.client.WurmClientBase;
import com.wurmonline.client.game.inventory.InventoryMetaItem;
import com.wurmonline.client.renderer.Color;
import com.wurmonline.client.renderer.ItemColorsXml;
import com.wurmonline.client.resources.Resources;
import com.wurmonline.client.resources.textures.ImageTextureLoader;
import com.wurmonline.client.resources.textures.Texture;
import com.wurmonline.shared.util.MaterialUtilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Builds material-coloured shield icons from one base image and mask per shield shape. */
public final class ShieldIcons {
    private static final Logger LOGGER = Logger.getLogger(ShieldIcons.class.getName());
    private static final String RESOURCE_ROOT = "/org/wurmhighres/client/icons/";
    private static final String GAME_RESOURCE_ROOT = "img.wurmhighres.";
    private static final int MATERIAL_MAX = 96;

    private static final Asset[] ASSETS = {
            new Asset((short) 970, "shield-small-wood", Family.WOOD),
            new Asset((short) 971, "shield-medium-wood", Family.WOOD),
            new Asset((short) 972, "shield-large-wood", Family.WOOD),
            new Asset((short) 1010, "shield-small-metal", Family.METAL),
            new Asset((short) 1011, "shield-medium-metal", Family.METAL),
            new Asset((short) 1012, "shield-large-metal", Family.METAL)
    };

    private static final Map<String, Texture> TEXTURE_CACHE =
            new ConcurrentHashMap<String, Texture>();
    private static final Map<String, BufferedImage> IMAGE_CACHE =
            new ConcurrentHashMap<String, BufferedImage>();
    private static volatile boolean resourceFailureLogged;
    private static volatile boolean textureSuccessLogged;

    private ShieldIcons() {
    }

    public static Texture override(InventoryMetaItem item, Texture original) {
        if (item == null) {
            return original;
        }
        Asset asset = asset(item.getType());
        if (asset == null || !asset.accepts(item.getMaterialId())) {
            return original;
        }
        return texture(asset, item.getMaterialId(), original);
    }

    /** Handles recipe rows where only the rendered item name is available. */
    public static Texture overrideByName(String name, Texture original) {
        byte material = materialFromDescriptor(name, null);
        Asset asset = assetFromName(name, material);
        if (asset == null || material == 0 || !asset.accepts(material)) {
            return original;
        }
        return texture(asset, material, original);
    }

    /** Uses the recipe's exact atlas ID; recipe display names omit shield size for some templates. */
    public static Texture overrideByDescriptor(
            short iconId, String descriptor, Texture original) {
        Asset asset = asset(iconId);
        if (asset == null) {
            return original;
        }
        byte material = materialFromDescriptor(descriptor, asset.family);
        return material == 0 ? original : texture(asset, material, original);
    }

    /** Handles ground build slots, whose model descriptor normally contains the material name. */
    public static Texture overrideGround(short iconId, String descriptor, Texture original) {
        Asset asset = asset(iconId);
        if (asset == null) {
            return original;
        }
        byte material = materialFromDescriptor(descriptor, asset.family);
        return material == 0 ? original : texture(asset, material, original);
    }

    static boolean isShieldIcon(short iconId) {
        return asset(iconId) != null;
    }

    static boolean recognizesName(String name) {
        byte material = materialFromDescriptor(name, null);
        return assetFromName(name, material) != null;
    }

    static byte materialFromDescriptor(String descriptor, Family requiredFamily) {
        String normalized = words(descriptor);
        if (normalized.length() == 0) {
            return 0;
        }
        for (int value = 1; value <= MATERIAL_MAX; value++) {
            // Generic fragment categories are not actual craft materials and
            // would make the word "metal" look like a concrete material.
            if (value >= 93 && value <= 95) {
                continue;
            }
            byte material = (byte) value;
            if (!isFamily(material, requiredFamily)) {
                continue;
            }
            String materialName = MaterialUtilities.getMaterialString(material);
            if (hasMaterialWord(normalized, materialName)) {
                return material;
            }
            String clientName = MaterialUtilities.getClientMaterialString(material, true);
            if (hasMaterialWord(normalized, clientName)) {
                return material;
            }
        }
        return 0;
    }

    static BufferedImage tint(
            BufferedImage base, BufferedImage mask, float red, float green, float blue) {
        if (base.getWidth() != mask.getWidth() || base.getHeight() != mask.getHeight()) {
            throw new IllegalArgumentException("Shield base image and mask dimensions differ");
        }
        BufferedImage result = new BufferedImage(
                base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        float[] multiplier = {clampMultiplier(red), clampMultiplier(green), clampMultiplier(blue)};
        for (int y = 0; y < base.getHeight(); y++) {
            for (int x = 0; x < base.getWidth(); x++) {
                int argb = base.getRGB(x, y);
                int alpha = (argb >>> 24) & 255;
                if (alpha == 0) {
                    continue;
                }
                float amount = ((mask.getRGB(x, y) >>> 16) & 255) / 255.0f;
                int output = alpha << 24;
                for (int channel = 0; channel < 3; channel++) {
                    int shift = 16 - channel * 8;
                    float srgb = ((argb >>> shift) & 255) / 255.0f;
                    double linear = Math.pow(srgb, 2.2);
                    linear *= 1.0f - amount + amount * multiplier[channel];
                    int value = Math.round((float) Math.pow(
                            Math.max(0.0, Math.min(1.0, linear)), 1.0 / 2.2) * 255.0f);
                    output |= value << shift;
                }
                result.setRGB(x, y, output);
            }
        }
        return result;
    }

    static String baseResource(short iconId) {
        Asset asset = asset(iconId);
        return asset == null ? null : RESOURCE_ROOT + asset.slug + ".png";
    }

    static String maskResource(short iconId) {
        Asset asset = asset(iconId);
        return asset == null ? null : RESOURCE_ROOT + asset.slug + "-mask.png";
    }

    static float[] colourMultipliers(byte material) {
        String materialName = MaterialUtilities.getMaterialString(material);
        Color colour = ItemColorsXml.findItemColor(materialName);
        if (colour != null) {
            return new float[]{colour.r, colour.g, colour.b};
        }
        return fallbackColour(materialName);
    }

    private static Texture texture(Asset asset, byte material, Texture original) {
        float[] colour = colourMultipliers(material);
        if (colour == null || isNeutral(colour)) {
            return original;
        }
        String key = asset.iconId + ":" + (material & 255);
        Texture cached = TEXTURE_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        synchronized (TEXTURE_CACHE) {
            cached = TEXTURE_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            BufferedImage base = image(asset.slug + ".png");
            BufferedImage mask = image(asset.slug + "-mask.png");
            if (base == null || mask == null) {
                return original;
            }
            Texture generated = ImageTextureLoader.loadNowrapNearestTexture(
                    tint(base, mask, colour[0], colour[1], colour[2]), false);
            if (generated != null) {
                TEXTURE_CACHE.put(key, generated);
                if (!textureSuccessLogged) {
                    textureSuccessLogged = true;
                    LOGGER.info("Dynamic shield recolouring active; first=" + asset.slug
                            + ", material=" + MaterialUtilities.getMaterialString(material)
                            + ", rgb=" + colour[0] + "," + colour[1] + "," + colour[2]);
                }
                return generated;
            }
        }
        return original;
    }

    private static BufferedImage image(String name) {
        BufferedImage cached = IMAGE_CACHE.get(name);
        if (cached != null) {
            return cached;
        }
        try (InputStream stream = openImage(name)) {
            BufferedImage loaded = ImageIO.read(stream);
            if (loaded == null) {
                throw new IOException("Unreadable classpath image " + RESOURCE_ROOT + name);
            }
            IMAGE_CACHE.put(name, loaded);
            return loaded;
        } catch (IOException exception) {
            if (!resourceFailureLogged) {
                resourceFailureLogged = true;
                LOGGER.log(Level.WARNING, "Unable to load dynamic shield icon assets", exception);
            }
            return null;
        }
    }

    private static InputStream openImage(String name) throws IOException {
        Resources resources = WurmClientBase.getResourceManager();
        if (resources != null) {
            try {
                return resources.getResourceAsStream(gameResourceName(name));
            } catch (IOException ignored) {
                // Fall through to the bundled copy for tests and unusual launchers.
            }
        }

        String classpathName = "org/wurmhighres/client/icons/" + name;
        InputStream stream = ShieldIcons.class.getResourceAsStream(RESOURCE_ROOT + name);
        if (stream == null) {
            ClassLoader ownLoader = ShieldIcons.class.getClassLoader();
            if (ownLoader != null) {
                stream = ownLoader.getResourceAsStream(classpathName);
            }
        }
        if (stream == null) {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                stream = contextLoader.getResourceAsStream(classpathName);
            }
        }
        if (stream == null) {
            throw new IOException("Missing shield resource " + gameResourceName(name));
        }
        return stream;
    }

    static String gameResourceName(String name) {
        String stem = name.endsWith(".png")
                ? name.substring(0, name.length() - 4) : name;
        return GAME_RESOURCE_ROOT + stem.replace('-', '.');
    }

    private static Asset asset(short iconId) {
        for (Asset candidate : ASSETS) {
            if (candidate.iconId == iconId) {
                return candidate;
            }
        }
        return null;
    }

    private static Asset assetFromName(String name, byte material) {
        String normalized = words(name);
        if (!containsWord(normalized, "shield")) {
            return null;
        }
        Family family = null;
        if (containsWord(normalized, "metal")) {
            family = Family.METAL;
        } else if (containsWord(normalized, "wood") || containsWord(normalized, "wooden")) {
            family = Family.WOOD;
        } else if (material != 0) {
            family = MaterialUtilities.isMetal(material) ? Family.METAL
                    : MaterialUtilities.isWood(material) ? Family.WOOD : null;
        }
        if (family == null) {
            return null;
        }
        String size;
        if (containsWord(normalized, "small")) {
            size = "small";
        } else if (containsWord(normalized, "medium")) {
            size = "medium";
        } else if (containsWord(normalized, "large")) {
            size = "large";
        } else if (family == Family.METAL) {
            // The client calls the medium metal template simply "shield".
            size = "medium";
        } else {
            return null;
        }
        String slug = "shield-" + size + "-" + family.slug;
        for (Asset candidate : ASSETS) {
            if (candidate.slug.equals(slug)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean hasMaterialWord(String normalizedDescriptor, String materialName) {
        String normalizedMaterial = words(materialName);
        if (normalizedMaterial.length() == 0) {
            return false;
        }
        if (containsPhrase(normalizedDescriptor, normalizedMaterial)) {
            return true;
        }
        String alias = woodAlias(normalizedMaterial);
        return !alias.equals(normalizedMaterial) && containsPhrase(normalizedDescriptor, alias);
    }

    private static String woodAlias(String materialName) {
        if ("oakenwood".equals(materialName)) {
            return "oak";
        }
        return materialName.endsWith("wood") && materialName.length() > 4
                ? materialName.substring(0, materialName.length() - 4) : materialName;
    }

    private static boolean isFamily(byte material, Family family) {
        if (family == null) {
            return MaterialUtilities.isWood(material) || MaterialUtilities.isMetal(material);
        }
        return family == Family.WOOD
                ? MaterialUtilities.isWood(material) : MaterialUtilities.isMetal(material);
    }

    static float[] fallbackColour(String material) {
        if ("applewood".equals(material)) return rgb(1.10f, 0.99f, 0.67f);
        if ("birchwood".equals(material)) return rgb(1.20f, 1.24f, 1.30f);
        if ("cherrywood".equals(material)) return rgb(1.25f, 1.00f, 1.02f);
        if ("chestnut".equals(material)) return rgb(0.97f, 0.85f, 0.90f);
        if ("cedarwood".equals(material)) return rgb(1.228f, 1.141f, 1.005f);
        if ("firwood".equals(material)) return rgb(1.37f, 1.34f, 1.43f);
        if ("lavenderwood".equals(material)) return rgb(0.84f, 0.67f, 0.96f);
        if ("rosewood".equals(material)) return rgb(0.98f, 0.65f, 0.47f);
        if ("thorn".equals(material)) return rgb(0.33f, 0.43f, 0.33f);
        if ("grapewood".equals(material)) return rgb(0.64f, 0.80f, 0.50f);
        if ("ivy".equals(material)) return rgb(0.81f, 0.984f, 0.67f);
        if ("camelliawood".equals(material)) return rgb(0.69f, 0.63f, 0.48f);
        if ("hazelnutwood".equals(material)) return rgb(0.86f, 0.67f, 0.36f);
        if ("raspberrywood".equals(material)) return rgb(0.861f, 0.476f, 0.578f);
        if ("blueberrywood".equals(material)) return rgb(0.51f, 0.56f, 0.85f);
        if ("lingonberrywood".equals(material)) return rgb(0.79f, 0.29f, 0.27f);
        if ("lemonwood".equals(material)) return rgb(1.24f, 1.04f, 0.876f);
        if ("lindenwood".equals(material)) return rgb(1.11f, 1.15f, 1.03f);
        if ("maplewood".equals(material)) return rgb(1.15f, 1.09f, 0.87f);
        if ("oakenwood".equals(material)) return rgb(0.77f, 0.75f, 0.68f);
        if ("oleanderwood".equals(material)) return rgb(0.59f, 0.63f, 0.67f);
        if ("olivewood".equals(material)) return rgb(1.40f, 1.29f, 1.03f);
        if ("orangewood".equals(material)) return rgb(1.44f, 1.14f, 0.976f);
        if ("pinewood".equals(material)) return rgb(1.50f, 1.53f, 1.57f);
        if ("walnut".equals(material)) return rgb(0.70f, 0.65f, 0.70f);
        if ("willow".equals(material)) return rgb(1.00f, 1.15f, 1.28f);
        if ("brass".equals(material)) return rgb(0.823f, 0.69f, 0.553f);
        if ("bronze".equals(material)) return rgb(0.588f, 0.384f, 0.215f);
        if ("copper".equals(material)) return rgb(0.72f, 0.45f, 0.218f);
        if ("gold".equals(material)) return rgb(0.968f, 0.721f, 0.208f);
        if ("lead".equals(material)) return rgb(0.51f, 0.54f, 0.59f);
        if ("silver".equals(material)) return rgb(0.68f, 0.756f, 0.776f);
        if ("steel".equals(material)) return rgb(0.57f, 0.60f, 0.65f);
        if ("seryll".equals(material)) return rgb(0.65f, 0.17f, 0.06f);
        if ("tin".equals(material)) return rgb(0.68f, 0.66f, 0.60f);
        if ("zinc".equals(material)) return rgb(0.91f, 0.87f, 0.77f);
        if ("glimmersteel".equals(material)) return rgb(0.86f, 0.82f, 0.59f);
        if ("adamantine".equals(material)) return rgb(0.55f, 0.67f, 0.86f);
        if ("electrum".equals(material)) return rgb(0.886f, 0.862f, 0.425f);
        if ("iron".equals(material)) return rgb(1.0f, 1.0f, 1.0f);
        return null;
    }

    private static float[] rgb(float red, float green, float blue) {
        return new float[]{red, green, blue};
    }

    private static boolean isNeutral(float[] colour) {
        return Math.abs(colour[0] - 1.0f) < 0.0001f
                && Math.abs(colour[1] - 1.0f) < 0.0001f
                && Math.abs(colour[2] - 1.0f) < 0.0001f;
    }

    private static float clampMultiplier(float value) {
        return Math.max(0.0f, Math.min(4.0f, value));
    }

    private static String words(String value) {
        if (value == null) {
            return "";
        }
        String lower = value.toLowerCase(Locale.ENGLISH);
        StringBuilder result = new StringBuilder(lower.length());
        boolean previousSpace = true;
        for (int index = 0; index < lower.length(); index++) {
            char character = lower.charAt(index);
            if (Character.isLetterOrDigit(character)) {
                result.append(character);
                previousSpace = false;
            } else if (!previousSpace) {
                result.append(' ');
                previousSpace = true;
            }
        }
        return result.toString().trim();
    }

    private static boolean containsWord(String value, String word) {
        return containsPhrase(value, word);
    }

    private static boolean containsPhrase(String value, String phrase) {
        return (" " + value + " ").contains(" " + phrase + " ");
    }

    enum Family {
        WOOD("wood"),
        METAL("metal");

        private final String slug;

        Family(String slug) {
            this.slug = slug;
        }
    }

    private static final class Asset {
        private final short iconId;
        private final String slug;
        private final Family family;

        private Asset(short iconId, String slug, Family family) {
            this.iconId = iconId;
            this.slug = slug;
            this.family = family;
        }

        private boolean accepts(byte material) {
            return isFamily(material, family);
        }
    }
}
