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
    static final String MAGIC_WATER_SKIN_RESOURCE = "img.wurmhighres.magicwaterskin";
    static final String SANTA_SACK_RESOURCE = "img.wurmhighres.santasack";
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
    private static final String GLAND_RESOURCE = "img.wurmhighres.gland";
    private static final String TWISTED_HORN_RESOURCE = "img.wurmhighres.twistedhorn";
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
    private static final short HORN_TWISTED_HORN_ICON_ID = 496;
    private static final short BLADDER_GLAND_ICON_ID = 515;

    private static final Map<String, Texture> ICON_CACHE =
            new ConcurrentHashMap<String, Texture>();
    private static volatile Field groundItemDataField;

    private IconOverrides() {
    }

    public static Texture override(InventoryMetaItem item, Texture original) {
        if (!WurmHighresSettings.replaceIcons || item == null) {
            return original;
        }
        String descriptor = itemDescriptor(item);
        Texture shield = ShieldIcons.override(item, original);
        if (shield != original) {
            return shield;
        }
        String magicalResource = magicalContainerResource(descriptor);
        if (magicalResource != null) {
            return icon(magicalResource, original);
        }
        String baseName = item.getBaseName();
        if (isLargeAnvil(item.getType(), item.getWeight(), baseName)) {
            return materialIcon(LARGE_ANVIL_RESOURCE, item.getMaterialId(), original);
        }
        if (isSmeltingPot(item.getType(), descriptor)) {
            return materialIcon(SMELTING_POT_RESOURCE, item.getMaterialId(), original);
        }
        if (isBrandingIron(item.getType(), descriptor)) {
            return materialIcon(BRANDING_IRON_RESOURCE, item.getMaterialId(), original);
        }
        String anatomyResource = anatomyResource(item.getType(), descriptor);
        if (anatomyResource != null) {
            return icon(anatomyResource, original);
        }
        String containerResource = containerResource(
                item.getType(), descriptor, item.getWeight());
        if (containerResource != null) {
            return materialIcon(containerResource, item.getMaterialId(), original);
        }

        String materialResource = materialResource(
                item.getType(), baseName, item.getR(), item.getG(), item.getB());
        if (materialResource != null) {
            return icon(materialResource, original);
        }
        if (isPrayerCharm(baseName)) {
            return materialIcon(PRAYER_CHARM_RESOURCE, item.getMaterialId(), original);
        }
        if (isPress(baseName)) {
            return materialIcon(PRESS_RESOURCE, item.getMaterialId(), original);
        }
        if (isCarvingKnife(baseName)) {
            return materialIcon(CARVING_KNIFE_RESOURCE, item.getMaterialId(), original);
        }
        return MaterialIcons.override(item, original);
    }

    /** Overrides recipe rows where the client exposes a name but no InventoryMetaItem. */
    public static Texture overrideByName(String name, Texture original) {
        if (!WurmHighresSettings.replaceIcons) {
            return original;
        }
        Texture shield = ShieldIcons.overrideByName(name, original);
        if (shield != original) {
            return shield;
        }
        String magicalResource = magicalContainerResource(name);
        if (magicalResource != null) {
            return icon(magicalResource, original);
        }
        if (isLargeAnvilName(name)) {
            return materialIcon(LARGE_ANVIL_RESOURCE, name, original);
        }
        if (isSmeltingPot(POTTERY_BOWL_SMELTING_POT_ICON_ID, name)) {
            return materialIcon(SMELTING_POT_RESOURCE, name, original);
        }
        if (isBrandingIron(PLIERS_BRANDING_IRON_ICON_ID, name)) {
            return materialIcon(BRANDING_IRON_RESOURCE, name, original);
        }
        String anatomyResource = anatomyResourceFromName(name);
        if (anatomyResource != null) {
            return icon(anatomyResource, original);
        }
        String containerResource = containerResource(SHARED_BARREL_ICON_ID, name, 0.0f);
        if (containerResource != null) {
            return materialIcon(containerResource, name, original);
        }
        if (isPrayerCharm(name)) {
            return materialIcon(PRAYER_CHARM_RESOURCE, name, original);
        }
        if (isPress(name)) {
            return materialIcon(PRESS_RESOURCE, name, original);
        }
        if (isCarvingKnife(name)) {
            return materialIcon(CARVING_KNIFE_RESOURCE, name, original);
        }
        String materialResource = materialResourceFromName(name);
        return materialResource == null ? original : icon(materialResource, original);
    }

    /** Overrides recipe rows while retaining their exact atlas cell ID. */
    public static Texture overrideByName(short iconId, String name, Texture original) {
        if (!WurmHighresSettings.replaceIcons) {
            return original;
        }
        Texture shield = ShieldIcons.overrideByDescriptor(iconId, name, original);
        if (shield != original) {
            return shield;
        }
        Texture named = overrideByName(name, original);
        return named != original ? named
                : MaterialIcons.overrideByDescriptor(iconId, name, original);
    }

    /** Handles Build slots populated from the ground, which expose ID and name but no RGB. */
    public static Texture overrideGround(
            short iconId, long groundItemId, String name, Texture original) {
        if (!WurmHighresSettings.replaceIcons) {
            return original;
        }
        String descriptor = name + " " + groundDescriptor(groundItemId);
        Texture shield = ShieldIcons.overrideGround(iconId, descriptor, original);
        if (shield != original) {
            return shield;
        }
        String magicalResource = magicalContainerResource(descriptor);
        if (magicalResource != null) {
            return icon(magicalResource, original);
        }
        if (isLargeAnvil(iconId, 0.0f, name)) {
            return materialIcon(LARGE_ANVIL_RESOURCE, descriptor, original);
        }
        if (isSmeltingPot(iconId, descriptor)) {
            return materialIcon(SMELTING_POT_RESOURCE, descriptor, original);
        }
        if (isBrandingIron(iconId, descriptor)) {
            return materialIcon(BRANDING_IRON_RESOURCE, descriptor, original);
        }
        String anatomyResource = anatomyResource(
                iconId, descriptor);
        if (anatomyResource != null) {
            return icon(anatomyResource, original);
        }
        String containerResource = containerResource(
                iconId, descriptor, 0.0f);
        if (containerResource != null) {
            return materialIcon(containerResource, descriptor, original);
        }
        float[] colour = groundColour(groundItemId);
        float r = colour == null ? 0.0f : colour[0];
        float g = colour == null ? 0.0f : colour[1];
        float b = colour == null ? 0.0f : colour[2];
        String materialResource = materialResource(iconId, name, r, g, b);
        if (materialResource != null) {
            return icon(materialResource, original);
        }
        Texture named = overrideByName(name, original);
        return named != original ? named
                : MaterialIcons.overrideByDescriptor(iconId, descriptor, original);
    }

    static boolean hasCustomIcon(String baseName) {
        return ShieldIcons.recognizesName(baseName)
                || magicalContainerResource(baseName) != null
                || isCarvingKnife(baseName)
                || isPrayerCharm(baseName)
                || isPress(baseName)
                || isSmeltingPot(POTTERY_BOWL_SMELTING_POT_ICON_ID, baseName)
                || isBrandingIron(PLIERS_BRANDING_IRON_ICON_ID, baseName)
                || anatomyResourceFromName(baseName) != null
                || containerResource(SHARED_BARREL_ICON_ID, baseName, 0.0f) != null
                || materialResourceFromName(baseName) != null;
    }

    static String magicalContainerResource(String descriptor) {
        String normalized = normalize(descriptor).replace('\u2019', '\'');
        if (containsPhrase(normalized, "magic water skin")
                || containsPhrase(normalized, "magic water skins")
                || containsPhrase(normalized, "magical water skin")
                || containsPhrase(normalized, "magical water skins")
                || containsPhrase(normalized, "magic waterskin")
                || containsPhrase(normalized, "magic waterskins")
                || containsPhrase(normalized, "magical waterskin")
                || containsPhrase(normalized, "magical waterskins")) {
            return MAGIC_WATER_SKIN_RESOURCE;
        }
        if (containsPhrase(normalized, "santa sack")
                || containsPhrase(normalized, "santa sacks")
                || containsPhrase(normalized, "santa's sack")
                || containsPhrase(normalized, "santa's sacks")
                || containsPhrase(normalized, "santas sack")
                || containsPhrase(normalized, "santas sacks")
                || containsPhrase(normalized, "christmas sack")
                || containsPhrase(normalized, "christmas sacks")) {
            return SANTA_SACK_RESOURCE;
        }
        return null;
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

    static String anatomyResource(short iconId, String descriptor) {
        String normalized = normalize(descriptor).replace('.', ' ').replace('_', ' ');
        if (iconId == HORN_TWISTED_HORN_ICON_ID && normalized.contains("twisted horn")) {
            return TWISTED_HORN_RESOURCE;
        }
        if (iconId == BLADDER_GLAND_ICON_ID && containsWord(normalized, "gland")) {
            return GLAND_RESOURCE;
        }
        return null;
    }

    private static String anatomyResourceFromName(String name) {
        String normalized = normalize(name).replace('.', ' ').replace('_', ' ');
        if (normalized.equals("twisted horn")
                || normalized.startsWith("twisted horn,")
                || normalized.startsWith("twisted horn (")) {
            return TWISTED_HORN_RESOURCE;
        }
        if (normalized.equals("gland")
                || normalized.equals("glands")
                || normalized.startsWith("gland,")
                || normalized.startsWith("gland (")) {
            return GLAND_RESOURCE;
        }
        return null;
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

    private static boolean containsPhrase(String value, String phrase) {
        int fromIndex = 0;
        while (fromIndex <= value.length() - phrase.length()) {
            int index = value.indexOf(phrase, fromIndex);
            if (index < 0) {
                return false;
            }
            int after = index + phrase.length();
            boolean beginsAtBoundary = index == 0
                    || !Character.isLetterOrDigit(value.charAt(index - 1));
            boolean endsAtBoundary = after == value.length()
                    || !Character.isLetterOrDigit(value.charAt(after));
            if (beginsAtBoundary && endsAtBoundary) {
                return true;
            }
            fromIndex = index + 1;
        }
        return false;
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

    static String itemDescriptor(InventoryMetaItem item) {
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

    private static Texture materialIcon(String resource, byte material, Texture original) {
        Texture base = icon(resource, original);
        return MaterialIcons.overrideCustom(resource, material, base);
    }

    private static Texture materialIcon(String resource, String descriptor, Texture original) {
        Texture base = icon(resource, original);
        return MaterialIcons.overrideCustomByDescriptor(resource, descriptor, base);
    }

    private static Texture icon(String resource, Texture original) {
        if (MagicEffects.supports(resource) && WurmHighresSettings.magicShimmer) {
            Texture animated = MagicEffects.icon(resource, original);
            if (animated != null && animated != original) {
                return animated;
            }
        }
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
