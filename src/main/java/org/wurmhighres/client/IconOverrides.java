package org.wurmhighres.client;

import com.wurmonline.client.game.inventory.InventoryMetaItem;
import com.wurmonline.client.renderer.GroundItemData;
import com.wurmonline.client.renderer.cell.CellRenderable;
import com.wurmonline.client.renderer.cell.GroundItemCellRenderable;
import com.wurmonline.client.resources.textures.ResourceTextureLoader;
import com.wurmonline.client.resources.textures.Texture;
import com.wurmonline.shared.util.MaterialUtilities;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Provides separate client-only icons for items that share a stock atlas ID. */
public final class IconOverrides {
    private static final String CARVING_KNIFE_NAME = "carving knife";
    private static final String CARVING_KNIFE_RESOURCE = "img.wurmhighres.carvingknife";
    private static final String PRAYER_CHARM_NAME = "prayer charm";
    private static final String PRAYER_CHARM_RESOURCE = "img.wurmhighres.prayercharm";
    private static final String LARGE_ANVIL_RESOURCE = "img.wurmhighres.largeanvil";
    private static final String PRESS_RESOURCE = "img.wurmhighres.press";
    private static final String SMALL_BARREL_RESOURCE = "img.wurmhighres.smallbarrel";
    private static final String LARGE_BARREL_RESOURCE = "img.wurmhighres.largebarrel";
    private static final String HUGE_TUB_RESOURCE = "img.wurmhighres.hugetub";
    private static final String HUGE_OIL_BARREL_RESOURCE = "img.wurmhighres.hugeoilbarrel";
    private static final String WINE_BARREL_RESOURCE = "img.wurmhighres.winebarrel";
    private static final String SMELTING_POT_RESOURCE = "img.wurmhighres.smeltingpot";
    private static final String BRANDING_IRON_RESOURCE = "img.wurmhighres.brandingiron";
    private static final String HIDE_RESOURCE = "img.wurmhighres.hide";
    private static final String LEATHER_RESOURCE = "img.wurmhighres.leather";
    private static final String DRAKE_HIDE_RESOURCE_PREFIX = "img.wurmhighres.drakehide.";
    private static final String DRAGON_SCALE_RESOURCE_PREFIX = "img.wurmhighres.dragonscale.";

    private static final short DRAGON_SCALE_ICON_ID = 554;
    private static final short LEATHER_HIDE_PELT_ICON_ID = 602;
    private static final short ANVIL_ICON_ID = 791;
    private static final short SHARED_BARREL_ICON_ID = 245;
    private static final short POTTERY_BOWL_SMELTING_POT_ICON_ID = 511;
    private static final short PLIERS_BRANDING_IRON_ICON_ID = 780;

    private static final Map<String, Texture> ICON_CACHE =
            new ConcurrentHashMap<String, Texture>();
    private static volatile Field groundItemDataField;

    private IconOverrides() {
    }

    public static Texture override(InventoryMetaItem item, Texture original) {
        if (!WurmHighresSettings.replaceIcons || item == null) {
            return original;
        }
        String baseName = item.getBaseName();
        if (isLargeAnvil(item.getType(), item.getWeight(), baseName)) {
            return icon(LARGE_ANVIL_RESOURCE, original);
        }
        if (isSmeltingPot(item.getType(), itemDescriptor(item))) {
            return icon(SMELTING_POT_RESOURCE, original);
        }
        if (isBrandingIron(item.getType(), itemDescriptor(item))) {
            return icon(BRANDING_IRON_RESOURCE, original);
        }
        String containerResource = containerResource(
                item.getType(), itemDescriptor(item), item.getWeight());
        if (containerResource != null) {
            return icon(containerResource, original);
        }

        String materialResource = materialResource(
                item.getType(), baseName, item.getR(), item.getG(), item.getB());
        if (materialResource != null) {
            return icon(materialResource, original);
        }
        if (isPrayerCharm(baseName)) {
            return icon(PRAYER_CHARM_RESOURCE, original);
        }
        if (isPress(baseName)) {
            return icon(PRESS_RESOURCE, original);
        }
        if (isCarvingKnife(baseName)) {
            return icon(CARVING_KNIFE_RESOURCE, original);
        }
        return original;
    }

    /** Overrides recipe rows where the client exposes a name but no InventoryMetaItem. */
    public static Texture overrideByName(String name, Texture original) {
        if (!WurmHighresSettings.replaceIcons) {
            return original;
        }
        if (isLargeAnvilName(name)) {
            return icon(LARGE_ANVIL_RESOURCE, original);
        }
        if (isSmeltingPot(POTTERY_BOWL_SMELTING_POT_ICON_ID, name)) {
            return icon(SMELTING_POT_RESOURCE, original);
        }
        if (isBrandingIron(PLIERS_BRANDING_IRON_ICON_ID, name)) {
            return icon(BRANDING_IRON_RESOURCE, original);
        }
        String containerResource = containerResource(SHARED_BARREL_ICON_ID, name, 0.0f);
        if (containerResource != null) {
            return icon(containerResource, original);
        }
        if (isPrayerCharm(name)) {
            return icon(PRAYER_CHARM_RESOURCE, original);
        }
        if (isPress(name)) {
            return icon(PRESS_RESOURCE, original);
        }
        if (isCarvingKnife(name)) {
            return icon(CARVING_KNIFE_RESOURCE, original);
        }
        String materialResource = materialResourceFromName(name);
        return materialResource == null ? original : icon(materialResource, original);
    }

    /** Handles Build slots populated from the ground, which expose ID and name but no RGB. */
    public static Texture overrideGround(
            short iconId, long groundItemId, String name, Texture original) {
        if (!WurmHighresSettings.replaceIcons) {
            return original;
        }
        if (isLargeAnvil(iconId, 0.0f, name)) {
            return icon(LARGE_ANVIL_RESOURCE, original);
        }
        if (isSmeltingPot(iconId, name + " " + groundDescriptor(groundItemId))) {
            return icon(SMELTING_POT_RESOURCE, original);
        }
        if (isBrandingIron(iconId, name + " " + groundDescriptor(groundItemId))) {
            return icon(BRANDING_IRON_RESOURCE, original);
        }
        String containerResource = containerResource(
                iconId, name + " " + groundDescriptor(groundItemId), 0.0f);
        if (containerResource != null) {
            return icon(containerResource, original);
        }
        float[] colour = groundColour(groundItemId);
        float r = colour == null ? 0.0f : colour[0];
        float g = colour == null ? 0.0f : colour[1];
        float b = colour == null ? 0.0f : colour[2];
        String materialResource = materialResource(iconId, name, r, g, b);
        return materialResource == null ? overrideByName(name, original)
                : icon(materialResource, original);
    }

    static boolean hasCustomIcon(String baseName) {
        return isCarvingKnife(baseName)
                || isPrayerCharm(baseName)
                || isPress(baseName)
                || isSmeltingPot(POTTERY_BOWL_SMELTING_POT_ICON_ID, baseName)
                || isBrandingIron(PLIERS_BRANDING_IRON_ICON_ID, baseName)
                || containerResource(SHARED_BARREL_ICON_ID, baseName, 0.0f) != null
                || materialResourceFromName(baseName) != null;
    }

    static String containerResource(short iconId, String name, float weight) {
        if (iconId != SHARED_BARREL_ICON_ID) {
            return null;
        }
        String normalized = normalize(name).replace('.', ' ').replace('_', ' ');
        boolean mentionsBarrel = normalized.contains("barrel")
                || normalized.contains("winebarrel");
        if (normalized.contains("tub")) {
            return HUGE_TUB_RESOURCE;
        }
        if (!mentionsBarrel) {
            return null;
        }
        if (normalized.contains("oil")) {
            return HUGE_OIL_BARREL_RESOURCE;
        }
        if (normalized.contains("wine barrel") || normalized.contains("winebarrel")) {
            return WINE_BARREL_RESOURCE;
        }
        if (normalized.contains("large barrel") || normalized.contains("barrel large")) {
            return LARGE_BARREL_RESOURCE;
        }
        if (normalized.contains("small barrel") || normalized.contains("barrel small")) {
            return SMALL_BARREL_RESOURCE;
        }
        // Some inventory packets use only "barrel". Empty small/large barrels
        // are 2/10 kg; names and ground model names take precedence over this fallback.
        boolean grams = weight >= 100.0f;
        return grams ? (weight > 5000.0f ? LARGE_BARREL_RESOURCE : SMALL_BARREL_RESOURCE)
                : (weight > 5.0f ? LARGE_BARREL_RESOURCE : SMALL_BARREL_RESOURCE);
    }

    static boolean isSmeltingPot(short iconId, String descriptor) {
        if (iconId != POTTERY_BOWL_SMELTING_POT_ICON_ID) {
            return false;
        }
        String normalized = normalize(descriptor).replace('.', ' ').replace('_', ' ');
        // Inventory and Toolbelt concatenate base/display/group/hover names,
        // producing values such as "smelting pot smelting pot ...". The icon
        // ID guard keeps pottery bowl (the other item in cell 511) untouched.
        return normalized.contains("smelting pot")
                || normalized.contains("smeltingpot");
    }

    static boolean isBrandingIron(short iconId, String descriptor) {
        if (iconId != PLIERS_BRANDING_IRON_ICON_ID) {
            return false;
        }
        String normalized = normalize(descriptor).replace('.', ' ').replace('_', ' ');
        return normalized.contains("branding iron")
                || normalized.contains("brandingiron");
    }

    static String materialResource(short iconId, String name, float r, float g, float b) {
        String normalized = normalize(name);
        if (iconId == DRAGON_SCALE_ICON_ID) {
            return DRAGON_SCALE_RESOURCE_PREFIX + dragonColour(normalized, r, g, b);
        }
        if (iconId != LEATHER_HIDE_PELT_ICON_ID) {
            return null;
        }
        if (isPelt(normalized)) {
            return null; // ID 602 remains the redesigned pelt in the stock atlas.
        }
        if (isDragonHide(normalized)) {
            return DRAKE_HIDE_RESOURCE_PREFIX + dragonColour(normalized, r, g, b);
        }
        if (isLeather(normalized)) {
            return LEATHER_RESOURCE;
        }
        if (isHide(normalized)) {
            return HIDE_RESOURCE;
        }
        return null;
    }

    private static String materialResourceFromName(String name) {
        String normalized = normalize(name);
        if (isDragonScale(normalized)) {
            return DRAGON_SCALE_RESOURCE_PREFIX + dragonColour(normalized, 0.0f, 0.0f, 0.0f);
        }
        if (isDragonHide(normalized)) {
            return DRAKE_HIDE_RESOURCE_PREFIX + dragonColour(normalized, 0.0f, 0.0f, 0.0f);
        }
        if (isLeather(normalized)) {
            return LEATHER_RESOURCE;
        }
        if (isHide(normalized)) {
            return HIDE_RESOURCE;
        }
        return null;
    }

    static String dragonColour(float r, float g, float b) {
        return dragonColour("", r, g, b);
    }

    private static String dragonColour(String normalizedName, float r, float g, float b) {
        String material = MaterialUtilities.getDragonLeatherMaterialNameFromColour(r, g, b);
        if (material != null && material.length() > 1 && material.charAt(0) == '.') {
            return material.substring(1);
        }
        for (String colour : new String[]{"black", "blue", "green", "red", "white"}) {
            if (containsWord(normalizedName, colour)) {
                return colour;
            }
        }
        // Ground Build items do not carry their RGB through CreationFrame.
        // White is the brightest and safest neutral fallback on the dark UI.
        return "white";
    }

    private static boolean isCarvingKnife(String name) {
        String normalized = normalize(name);
        return CARVING_KNIFE_NAME.equals(normalized)
                || normalized.startsWith(CARVING_KNIFE_NAME + ",")
                || normalized.startsWith(CARVING_KNIFE_NAME + " (");
    }

    private static boolean isPrayerCharm(String name) {
        String normalized = normalize(name);
        return PRAYER_CHARM_NAME.equals(normalized)
                || normalized.startsWith(PRAYER_CHARM_NAME + " of ");
    }

    static boolean isPress(String name) {
        String normalized = normalize(name);
        return normalized.equals("press")
                || normalized.startsWith("press,")
                || normalized.startsWith("press (");
    }

    private static boolean isPelt(String normalized) {
        return containsWord(normalized, "pelt") || containsWord(normalized, "pelts");
    }

    private static boolean isDragonHide(String normalized) {
        return normalized.contains("drake hide") || normalized.contains("dragon hide");
    }

    private static boolean isDragonScale(String normalized) {
        return normalized.equals("scale")
                || normalized.equals("scales")
                || normalized.startsWith("scale,")
                || normalized.startsWith("scale (")
                || normalized.contains("dragon scale");
    }

    private static boolean isLeather(String normalized) {
        return normalized.equals("leather")
                || normalized.startsWith("leather,")
                || normalized.startsWith("leather (");
    }

    private static boolean isHide(String normalized) {
        return normalized.equals("hide")
                || normalized.startsWith("hide,")
                || normalized.startsWith("hide (")
                || normalized.contains(" hide");
    }

    private static boolean containsWord(String value, String word) {
        return value.equals(word)
                || value.startsWith(word + " ")
                || value.endsWith(" " + word)
                || value.contains(" " + word + " ")
                || value.contains(" " + word + ",")
                || value.contains(" " + word + " (");
    }

    private static float[] groundColour(long groundItemId) {
        GroundItemData data = groundItemData(groundItemId);
        return data == null ? null : new float[]{data.getR(), data.getG(), data.getB()};
    }

    private static String groundDescriptor(long groundItemId) {
        GroundItemData data = groundItemData(groundItemId);
        if (data == null) {
            return "";
        }
        return data.getName() + " " + data.getModelName();
    }

    private static GroundItemData groundItemData(long groundItemId) {
        try {
            if (CellRenderable.getWorld() == null
                    || CellRenderable.getWorld().getClient() == null
                    || CellRenderable.getWorld().getClient().getConnectionListener() == null) {
                return null;
            }
            GroundItemCellRenderable renderable = CellRenderable.getWorld().getClient()
                    .getConnectionListener().findGroundItem(groundItemId);
            if (renderable == null) {
                return null;
            }
            Field field = groundItemDataField;
            if (field == null) {
                synchronized (IconOverrides.class) {
                    field = groundItemDataField;
                    if (field == null) {
                        field = GroundItemCellRenderable.class.getDeclaredField("item");
                        field.setAccessible(true);
                        groundItemDataField = field;
                    }
                }
            }
            return (GroundItemData) field.get(renderable);
        } catch (ReflectiveOperationException exception) {
            return null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static String itemDescriptor(InventoryMetaItem item) {
        return item.getBaseName() + " " + item.getDisplayName() + " "
                + item.getGroupName() + " " + item.getHoverText() + " "
                + item.getCustomName();
    }

    static boolean isLargeAnvilName(String name) {
        String normalized = normalize(name);
        return normalized.contains("large") && normalized.contains("anvil");
    }

    static boolean isLargeAnvil(short iconId, float weight, String baseName) {
        if (iconId != ANVIL_ICON_ID) {
            return false;
        }
        if (isLargeAnvilName(baseName)) {
            return true;
        }
        // Different client/server paths expose inventory weight either in kg
        // or grams. Small/large anvils are 2/10 kg respectively.
        return weight >= 100.0f ? weight > 5000.0f : weight > 5.0f;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private static Texture icon(String resource, Texture original) {
        Texture texture = ICON_CACHE.get(resource);
        if (texture == null) {
            synchronized (ICON_CACHE) {
                texture = ICON_CACHE.get(resource);
                if (texture == null) {
                    texture = ResourceTextureLoader.getNearestTextureNonScaling(resource);
                    if (texture != null) {
                        ICON_CACHE.put(resource, texture);
                    }
                }
            }
        }
        return texture == null ? original : texture;
    }
}
