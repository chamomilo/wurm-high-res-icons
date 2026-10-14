package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Selects fishing artwork without changing unrelated shared atlas items. */
final class FishingIcons {
    static final String KEEP_NET = "img.wurmhighres.fishing.keep.net";
    static final String MODERN_LINE = "img.wurmhighres.fishing.modern.line";
    static final String OLD_FINE_HOOK = "img.wurmhighres.fishing.old.fine.hook";
    private static final String[] LINE_TYPES = {"basic", "light", "medium", "heavy", "braided"};
    private static final String[] MATERIALS = materialNames();

    private FishingIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        if (iconId != 343 && iconId != 620 && iconId != 785) return null;
        String model = normalize(modelDescriptor);
        int separator = model.lastIndexOf(' ');
        if (separator >= 0) model = model.substring(separator + 1);
        if (iconId == 343 && isModel(model, "model.container.net.keep")) return KEEP_NET;
        if (iconId == 620) {
            for (String type : LINE_TYPES) {
                if (isModel(model, "model.fishline." + type)) return MODERN_LINE;
            }
        }
        // Both hook templates share model.tool.fish.hook, so it cannot identify
        // the old fine hook. Ambiguous renamed ground hooks retain their icon.
        String resource = byName(baseName);
        if (iconId == 343 && KEEP_NET.equals(resource)) return resource;
        if (iconId == 620 && MODERN_LINE.equals(resource)) return resource;
        if (iconId == 785 && OLD_FINE_HOOK.equals(resource)) return resource;
        return null;
    }

    static String byName(String value) {
        String name = normalize(value);
        for (String rarity : new String[]{"rare ", "supreme ", "fantastic "}) {
            if (name.startsWith(rarity)) {
                name = name.substring(rarity.length());
                break;
            }
        }
        int suffix = name.indexOf(" (");
        if (suffix >= 0) name = name.substring(0, suffix);
        name = name.replace(',', ' ').trim().replaceAll("\\s+", " ");
        for (String material : MATERIALS) {
            if (name.startsWith(material + " ")) name = name.substring(material.length() + 1);
            else if (name.endsWith(" " + material)) name = name.substring(0, name.length() - material.length() - 1);
        }
        if (name.equals("fish keep net")) return KEEP_NET;
        if (name.equals("old fine fishing hook")) return OLD_FINE_HOOK;
        for (String type : LINE_TYPES) {
            if (name.equals(type + " fishing line")) return MODERN_LINE;
        }
        return null;
    }

    private static boolean isModel(String value, String model) {
        return value.equals(model) || value.startsWith(model + ".");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private static String[] materialNames() {
        Set<String> names = new LinkedHashSet<String>();
        for (int id = 1; id < 128; id++) {
            names.add(normalize(MaterialUtilities.getMaterialString((byte) id)));
            names.add(normalize(MaterialUtilities.getClientMaterialString((byte) id, true)));
        }
        names.remove("");
        return names.toArray(new String[names.size()]);
    }
}
