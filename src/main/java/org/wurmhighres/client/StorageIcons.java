package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Resolves the different containers in stock cells 243, 244 and 245. */
final class StorageIcons {
    static final String GIFT = "img.wurmhighres.storage.gift";
    static final String GIFT_BOX = "img.wurmhighres.storage.gift.box";
    static final String CHEST = "img.wurmhighres.storage.chest";
    static final String TACKLE_BOX = "img.wurmhighres.storage.tackle.box";
    static final String COMPARTMENT = "img.wurmhighres.storage.compartment";
    static final String BUOY = "img.wurmhighres.storage.buoy";
    private static final String[] MATERIAL_PREFIXES = materialPrefixes();

    private StorageIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        String model = normalize(modelDescriptor);
        String named = byName(baseName);
        if (iconId == 243) {
            if (model.contains("model.container.giftbox")) return GIFT_BOX;
            if (model.contains("model.container.presentchristmas")) return GIFT;
            return GIFT_BOX.equals(named) ? GIFT_BOX : GIFT;
        }
        if (iconId == 244) {
            if (model.contains("model.furniture.wooden.shelf")) return COMPARTMENT;
            if (model.contains("model.container.tacklebox")) return TACKLE_BOX;
            if (model.contains("model.container.chest")) return CHEST;
            if (TACKLE_BOX.equals(named) || COMPARTMENT.equals(named)) return named;
            return CHEST;
        }
        if (iconId == 245) {
            if (model.contains("model.light.buoy")) return BUOY;
            if (model.contains("model.container.barrel")
                    || model.contains("model.container.winebarrel")) return null;
            return BUOY.equals(named) ? BUOY : null;
        }
        return null;
    }

    /** Recipe names have no model/ID; only accept the actual item name. */
    static String byName(String value) {
        String name = normalize(value);
        for (String rarity : new String[]{"rare ", "supreme ", "fantastic "}) {
            if (name.startsWith(rarity)) {
                name = name.substring(rarity.length());
                break;
            }
        }
        int comma = name.indexOf(',');
        if (comma >= 0) name = name.substring(0, comma).trim();
        int suffix = name.indexOf(" (");
        if (suffix >= 0) name = name.substring(0, suffix).trim();
        for (String prefix : MATERIAL_PREFIXES) {
            if (name.startsWith(prefix + " ")) {
                name = name.substring(prefix.length()+1);
                break;
            }
        }
        if (name.equals("gift box") || name.equals("gift boxes")) return GIFT_BOX;
        if (name.equals("gift") || name.equals("gifts")) return GIFT;
        if (name.equals("tackle box") || name.equals("tackle boxes")) return TACKLE_BOX;
        if (name.equals("compartment") || name.equals("compartments")) return COMPARTMENT;
        if (name.equals("buoy") || name.equals("buoys")) return BUOY;
        if (name.equals("chest") || name.equals("chests") || name.equals("small chest")
                || name.equals("large chest") || name.equals("small chests")
                || name.equals("large chests")) return CHEST;
        return null;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private static String[] materialPrefixes() {
        Set<String> names = new LinkedHashSet<String>();
        for (int id = 1; id < 128; id++) {
            byte material = (byte) id;
            if (MaterialUtilities.isMetal(material) || MaterialUtilities.isWood(material)) {
                names.add(normalize(MaterialUtilities.getMaterialString(material)));
                names.add(normalize(MaterialUtilities.getClientMaterialString(material, true)));
            }
        }
        names.remove("");
        return names.toArray(new String[names.size()]);
    }
}
