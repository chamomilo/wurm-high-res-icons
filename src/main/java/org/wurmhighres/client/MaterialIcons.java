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
            metal(764, "bowl", "bowl"),
            metal(804, "cauldron", "cauldron"),
            metal(251, "candelabra", "candelabra"),
            metal(823, "oil-lamp", "oil lamp", "brass oil lamp"),
            metal(825, "hanging-lamp", "hanging lamp"),
            metal(825, "torch-lamp", "torch lamp"),
            metal(825, "imperial-street-lamp", "imperial street lamp", "imperial lamp"),
            metal(825, "lamp", "lamp"),
            metal(282, "praying-statuette", "statuette"),
            metal(520, "chain", "chain"),
            metal(755, "butchering-knife", "butchering knife"),
            either(767, "fork", "fork"),
            either(768, "spoon", "spoon"),
            metal(754, "awl", "awl"),
            wood(802, "clay-shaper", "clay shaper"),
            metal(882, "metal-brush", "metal brush"),
            wood(902, "grooming-brush", "grooming brush"),
            metal(940, "knife", "knife", "leather knife"),
            metal(1201, "stone-chisel", "stone chisel", "chisel"),
            metal(738, "crowbar", "crowbar"),
            wood(741, "mallet", "mallet"),
            metal(742, "hammer", "hammer"),
            metal(743, "pickaxe", "pickaxe", "pick axe"),
            metal(745, "rake", "rake"),
            metal(746, "shovel", "shovel"),
            metal(747, "saw", "saw"),
            metal(748, "scissors", "scissors"),
            metal(749, "file", "file"),
            metal(750, "trowel", "trowel"),
            metal(752, "sickle", "sickle"),
            metal(753, "scythe", "scythe"),
            metal(780, "pliers", "pliers"),
            wood(787, "spindle", "spindle"),
            metal(791, "small-anvil", "anvil", "small anvil"),
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
            metal(1207, "hatchet", "hatchet", "axe", "small axe")
    };

    private static final Asset[] CUSTOM_ASSETS = {
            customMetal(FishingIcons.OLD_FINE_HOOK, "old-fine-fishing-hook", "old fine fishing hook"),
            customMetal(HardwareIcons.LARGE_NAILS, "large-nails", "large nails", "nails large"),
            customMetal(HardwareIcons.SMALL_NAILS, "small-nails", "small nails", "nails small"),
            customMetal(HardwareIcons.KEY, "key", "key", "keys"),
            customMetal(HardwareIcons.KEY_COPY, "key", "key copy"),
            customMetal(HardwareIcons.SMALL_LOCK, "small-lock", "door lock", "small padlock", "boat lock"),
            customMetal(HardwareIcons.LARGE_LOCK, "large-lock", "large padlock", "gate lock"),
            customMetal(HardwareIcons.HOOKS, "hooks", "hooks", "hook"),
            customMetal(HardwareIcons.EYELET, "eyelet", "eyelet"),
            customMetal(HardwareIcons.LOCK_PICKS, "lock-picks", "lock picks", "lock pick"),
            customMetal(HardwareIcons.RIVETS, "rivets", "rivets", "rivet"),
            customMetal(CookwareIcons.FRYING_PAN, "frying-pan", "frying pan"),
            customMetal(CookwareIcons.SAUCE_PAN, "sauce-pan", "sauce pan"),
            customWood(StorageIcons.BUOY, "buoy", "buoy"),
            customWood(StorageIcons.CHEST, "chest", "chest", "small chest", "large chest"),
            customMetal(StorageIcons.TACKLE_BOX, "tackle-box", "tackle box"),
            customWood(StorageIcons.COMPARTMENT, "compartment", "compartment"),
            customMetal("img.wurmhighres.brandingiron", "branding-iron", "branding iron"),
            customMetal("img.wurmhighres.smeltingpot", "smelting-pot", "smelting pot"),
            customWood("img.wurmhighres.smallbarrel", "small-barrel"),
            customWood("img.wurmhighres.largebarrel", "large-barrel"),
            customWood("img.wurmhighres.hugetub", "huge-tub"),
            customWood("img.wurmhighres.hugeoilbarrel", "huge-oil-barrel"),
            customWood("img.wurmhighres.winebarrel", "wine-barrel"),
            customWood("img.wurmhighres.press", "press"),
            customMetal("img.wurmhighres.largeanvil", "large-anvil", "large anvil"),
            customMetal("img.wurmhighres.prayercharm", "prayer-charm", "prayer charm"),
            customMetal("img.wurmhighres.carvingknife", "carving-knife", "carving knife")
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
        Asset asset = inventoryAsset(item);
        return asset == null ? original : texture(asset, item.getMaterialId(), original);
    }

    private static Asset inventoryAsset(InventoryMetaItem item) {
        String fishing = FishingIcons.resource(item.getType(), item.getBaseName(), null);
        if (fishing != null) return customAsset(fishing);
        String hardware = HardwareIcons.resource(item.getType(), item.getBaseName(), null);
        if (hardware != null) return customAsset(hardware);
        // Servers can assign a separate atlas cell to a material-specific tool.
        // Use only the server's base name here: hover text and player names can
        // mention other tools. Resolve custom artwork before shared atlas cells
        // so a rare carving knife does not inherit a stone chisel's artwork.
        Asset asset = inventoryAssetByName(item.getBaseName(), item.getMaterialId());
        return asset != null ? asset
                : atlasAsset(item.getType(), IconOverrides.itemDescriptor(item));
    }

    static String inventoryBaseResource(InventoryMetaItem item) {
        Asset asset = inventoryAsset(item);
        return asset == null ? null : resourceName(asset, false);
    }

    private static Asset inventoryAssetByName(String baseName, byte material) {
        String normalized = words(baseName);
        for (String rarity : new String[]{"rare ", "supreme ", "fantastic "}) {
            if (normalized.startsWith(rarity)) {
                normalized = normalized.substring(rarity.length());
                break;
            }
        }
        String materialName = words(MaterialUtilities.getMaterialString(material));
        String clientMaterialName = words(MaterialUtilities.getClientMaterialString(material, true));
        Asset asset = namedAsset(ATLAS_ASSETS, normalized, material, materialName, clientMaterialName);
        return asset != null ? asset
                : namedAsset(CUSTOM_ASSETS, normalized, material, materialName, clientMaterialName);
    }

    private static Asset namedAsset(Asset[] candidates, String normalized, byte material,
                                     String materialName, String clientMaterialName) {
        for (Asset candidate : candidates) {
            if (!candidate.accepts(material)) {
                continue;
            }
            for (String name : candidate.names) {
                if (normalized.equals(name)
                        || normalized.equals(materialName + " " + name)
                        || normalized.equals(name + " " + materialName)
                        || normalized.equals(clientMaterialName + " " + name)
                        || normalized.equals(name + " " + clientMaterialName)) {
                    return candidate;
                }
            }
        }
        return null;
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
        float[] colour = ToolMaterialPalette.colourMultipliers(material);
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
                if ("seryll".equals(MaterialUtilities.getMaterialString(material))) {
                    LOGGER.info("Seryll material icon generated: artwork=" + asset.slug
                            + ", materialId=" + (material & 255)
                            + ", rgb=" + colour[0] + "," + colour[1] + "," + colour[2]);
                }
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
        if (iconId == 823 || iconId == 825) {
            String slug = lightingArtwork(iconId, descriptor);
            for (Asset candidate : ATLAS_ASSETS) {
                if (candidate.iconId == iconId && candidate.slug.equals(slug)) {
                    return candidate;
                }
            }
            return null;
        }
        for (Asset asset : ATLAS_ASSETS) {
            if (asset.iconId == iconId && asset.matches(normalized)) {
                if (iconId == 791 && containsPhrase(normalized, "large anvil")) {
                    continue;
                }
                if (iconId == 753 && containsPhrase(normalized, "scythe blade")) {
                    continue;
                }
                return asset;
            }
        }
        return null;
    }

    /** Four assembled lamps share cell 825; their heads/unfinished parts do not. */
    static String lightingArtwork(short iconId, String descriptor) {
        String normalized = words(descriptor);
        if (containsPhrase(normalized, "lamp head")
                || containsPhrase(normalized, "unfinished")
                || containsPhrase(normalized, "fragment")) {
            return null;
        }
        if (iconId == 823) {
            return containsPhrase(normalized, "oil lamp")
                    || containsPhrase(normalized, "model light oillamp") ? "oil-lamp" : null;
        }
        if (iconId != 825) {
            return null;
        }
        if (containsPhrase(normalized, "imperial street lamp")
                || containsPhrase(normalized, "imperial lamp")
                || containsPhrase(normalized, "model light lamp street metal")) {
            return "imperial-street-lamp";
        }
        if (containsPhrase(normalized, "hanging lamp")
                || containsPhrase(normalized, "model light lamp street pole")) {
            return "hanging-lamp";
        }
        if (containsPhrase(normalized, "torch lamp")
                || containsPhrase(normalized, "model light lamp street torch")) {
            return "torch-lamp";
        }
        return containsPhrase(normalized, "lamp") ? "lamp" : null;
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

    private static Asset customMetal(String resource, String slug, String... names) {
        return new Asset((short) -1, resource, slug, Family.METAL, names);
    }

    private static Asset customWood(String resource, String slug, String... names) {
        return new Asset((short) -1, resource, slug, Family.WOOD, names);
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
