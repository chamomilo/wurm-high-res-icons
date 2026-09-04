package org.wurmhighres.client;

import com.wurmonline.client.game.inventory.InventoryMetaItem;
import com.wurmonline.client.WurmClientBase;
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

/** Applies the item's real metal or wood material to a semantic part mask. */
public final class MaterialIcons {
    private static final Logger LOGGER = Logger.getLogger(MaterialIcons.class.getName());
    private static final String RESOURCE_PREFIX = "img.wurmhighres.material.";

    private static final Asset[] ATLAS_ASSETS = {
            metal(282, "praying-statuette", "statuette"),
            metal(520, "chain", "chain"),
            metal(755, "butchering-knife", "butchering knife"),
            either(767, "fork", "fork"),
            either(768, "spoon", "spoon"),
            metal(754, "awl", "awl"),
            wood(802, "clay-shaper", "clay shaper"),
            metal(882, "metal-brush", "metal brush"),
            wood(902, "grooming-brush", "grooming brush"),
            metal(940, "knife", "knife"),
            metal(1201, "stone-chisel", "stone chisel"),
            metal(738, "crowbar", "crowbar"),
            wood(741, "mallet", "mallet"),
            metal(742, "hammer", "hammer"),
            metal(743, "pickaxe", "pickaxe"),
            metal(745, "rake", "rake"),
            metal(746, "shovel", "shovel"),
            metal(747, "saw", "saw"),
            metal(748, "scissors", "scissors"),
            metal(749, "file", "file"),
            metal(750, "trowel", "trowel"),
            metal(752, "sickle", "sickle"),
            metal(780, "pliers", "pliers"),
            wood(787, "spindle", "spindle"),
            metal(791, "small-anvil", "anvil"),
            wood(808, "spatula", "spatula"),
            metal(783, "steel-and-flint", "steel and flint"),
            wood(246, "fruit-press", "fruit press"),
            metal(736, "bee-smoker", "bee smoker"),
            wood(266, "cheese-drill", "cheese drill"),
            wood(265, "small-bucket", "bucket"),
            metal(788, "needle", "needle"),
            metal(792, "compass", "compass"),
            metal(822, "lantern", "lantern", "lamp head", "metal torch"),
            metal(860, "spyglass", "spyglass"),
            wood(880, "rope-tool", "rope tool"),
            metal(1207, "hatchet", "hatchet", "axe")
    };

    private static final Asset[] CUSTOM_ASSETS = {
            customMetal("img.wurmhighres.brandingiron", "branding-iron"),
            customMetal("img.wurmhighres.smeltingpot", "smelting-pot"),
            customWood("img.wurmhighres.smallbarrel", "small-barrel"),
            customWood("img.wurmhighres.largebarrel", "large-barrel"),
            customWood("img.wurmhighres.hugetub", "huge-tub"),
            customWood("img.wurmhighres.hugeoilbarrel", "huge-oil-barrel"),
            customWood("img.wurmhighres.winebarrel", "wine-barrel"),
            customWood("img.wurmhighres.press", "press"),
            customMetal("img.wurmhighres.largeanvil", "large-anvil"),
            customMetal("img.wurmhighres.prayercharm", "prayer-charm"),
            customMetal("img.wurmhighres.carvingknife", "carving-knife")
    };

    private static final Map<String, Texture> TEXTURE_CACHE =
            new ConcurrentHashMap<String, Texture>();
    private static final Map<String, BufferedImage> IMAGE_CACHE =
            new ConcurrentHashMap<String, BufferedImage>();
    private static volatile boolean resourceFailureLogged;
    private static volatile boolean textureSuccessLogged;

    private MaterialIcons() {
    }

    public static Texture override(InventoryMetaItem item, Texture original) {
        if (item == null) {
            return original;
        }
        Asset asset = atlasAsset(item.getType(), IconOverrides.itemDescriptor(item));
        return asset == null ? original : texture(asset, item.getMaterialId(), original);
    }

    public static Texture overrideByDescriptor(
            short iconId, String descriptor, Texture original) {
        Asset asset = atlasAsset(iconId, descriptor);
        if (asset == null) {
            return original;
        }
        byte material = materialFromDescriptor(descriptor, asset.family);
        return material == 0 ? original : texture(asset, material, original);
    }

    public static Texture overrideCustom(
            String resource, byte material, Texture original) {
        Asset asset = customAsset(resource);
        return asset == null ? original : texture(asset, material, original);
    }

    public static Texture overrideCustomByDescriptor(
            String resource, String descriptor, Texture original) {
        Asset asset = customAsset(resource);
        if (asset == null) {
            return original;
        }
        byte material = materialFromDescriptor(descriptor, asset.family);
        return material == 0 ? original : texture(asset, material, original);
    }

    static boolean recognizes(short iconId, String descriptor) {
        return atlasAsset(iconId, descriptor) != null;
    }

    static String baseResource(short iconId, String descriptor) {
        Asset asset = atlasAsset(iconId, descriptor);
        return asset == null ? null : resourceName(asset, false);
    }

    static String maskResource(short iconId, String descriptor) {
        Asset asset = atlasAsset(iconId, descriptor);
        return asset == null ? null : resourceName(asset, true);
    }

    private static byte materialFromDescriptor(String descriptor, Family family) {
        ShieldIcons.Family shieldFamily = family == Family.METAL
                ? ShieldIcons.Family.METAL
                : family == Family.WOOD ? ShieldIcons.Family.WOOD : null;
        return ShieldIcons.materialFromDescriptor(descriptor, shieldFamily);
    }

    private static Texture texture(Asset asset, byte material, Texture original) {
        if (!asset.accepts(material)) {
            return original;
        }
        float[] colour = ShieldIcons.colourMultipliers(material);
        if (colour == null) {
            return original;
        }
        String key = asset.slug + ":" + (material & 255);
        Texture cached = TEXTURE_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        synchronized (TEXTURE_CACHE) {
            cached = TEXTURE_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            BufferedImage base = image(resourceName(asset, false));
            BufferedImage mask = image(resourceName(asset, true));
            if (base == null || mask == null) {
                return original;
            }
            Texture generated = ImageTextureLoader.loadNowrapNearestTexture(
                    ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]), false);
            if (generated != null) {
                TEXTURE_CACHE.put(key, generated);
                if (!textureSuccessLogged) {
                    textureSuccessLogged = true;
                    LOGGER.info("Dynamic item-material recolouring active; first=" + asset.slug
                            + ", material=" + MaterialUtilities.getMaterialString(material));
                }
                return generated;
            }
        }
        return original;
    }

    private static BufferedImage image(String resource) {
        BufferedImage cached = IMAGE_CACHE.get(resource);
        if (cached != null) {
            return cached;
        }
        try (InputStream stream = openImage(resource)) {
            BufferedImage loaded = ImageIO.read(stream);
            if (loaded == null) {
                throw new IOException("Unreadable material icon resource " + resource);
            }
            IMAGE_CACHE.put(resource, loaded);
            return loaded;
        } catch (IOException exception) {
            if (!resourceFailureLogged) {
                resourceFailureLogged = true;
                LOGGER.log(Level.WARNING, "Unable to load dynamic material icon assets", exception);
            }
            return null;
        }
    }

    private static InputStream openImage(String resource) throws IOException {
        Resources resources = WurmClientBase.getResourceManager();
        if (resources == null) {
            throw new IOException("Wurm resource manager is unavailable for " + resource);
        }
        InputStream stream = resources.getResourceAsStream(resource);
        if (stream == null) {
            throw new IOException("Missing material icon resource " + resource);
        }
        return stream;
    }

    private static Asset atlasAsset(short iconId, String descriptor) {
        String normalized = words(descriptor);
        for (Asset asset : ATLAS_ASSETS) {
            if (asset.iconId == iconId && asset.matches(normalized)) {
                if (iconId == 791 && containsPhrase(normalized, "large anvil")) {
                    continue;
                }
                return asset;
            }
        }
        return null;
    }

    private static Asset customAsset(String resource) {
        for (Asset asset : CUSTOM_ASSETS) {
            if (asset.customResource.equals(resource)) {
                return asset;
            }
        }
        return null;
    }

    private static String resourceName(Asset asset, boolean mask) {
        return RESOURCE_PREFIX + asset.slug.replace('-', '.') + (mask ? ".mask" : "");
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

    private static boolean containsPhrase(String value, String phrase) {
        return (" " + value + " ").contains(" " + phrase + " ");
    }

    private static Asset metal(int iconId, String slug, String... names) {
        return new Asset((short) iconId, null, slug, Family.METAL, names);
    }

    private static Asset wood(int iconId, String slug, String... names) {
        return new Asset((short) iconId, null, slug, Family.WOOD, names);
    }

    private static Asset either(int iconId, String slug, String... names) {
        return new Asset((short) iconId, null, slug, Family.EITHER, names);
    }

    private static Asset customMetal(String resource, String slug) {
        return new Asset((short) -1, resource, slug, Family.METAL);
    }

    private static Asset customWood(String resource, String slug) {
        return new Asset((short) -1, resource, slug, Family.WOOD);
    }

    private enum Family {
        METAL,
        WOOD,
        EITHER
    }

    private static final class Asset {
        private final short iconId;
        private final String customResource;
        private final String slug;
        private final Family family;
        private final String[] names;

        private Asset(short iconId, String customResource, String slug,
                      Family family, String... names) {
            this.iconId = iconId;
            this.customResource = customResource;
            this.slug = slug;
            this.family = family;
            this.names = names;
        }

        private boolean matches(String descriptor) {
            for (String name : names) {
                if (containsPhrase(descriptor, name)) {
                    return true;
                }
            }
            return false;
        }

        private boolean accepts(byte material) {
            if (family == Family.METAL) {
                return MaterialUtilities.isMetal(material);
            }
            if (family == Family.WOOD) {
                return MaterialUtilities.isWood(material);
            }
            return MaterialUtilities.isMetal(material) || MaterialUtilities.isWood(material);
        }
    }
}
