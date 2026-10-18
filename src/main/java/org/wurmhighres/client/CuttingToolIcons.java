package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Separates the leatherworking knife and stone cutter from ordinary knives. */
final class CuttingToolIcons {
    static final String LEATHER_KNIFE = "img.wurmhighres.cutting.leather.knife";
    static final String STONE_CHISEL = "img.wurmhighres.cutting.stone.chisel";
    private static final String[] MATERIALS = materialNames();

    private CuttingToolIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        if (iconId != 766 && iconId != 1201) return null;
        String model = normalize(modelDescriptor);
        int space = model.lastIndexOf(' ');
        if (space >= 0) model = model.substring(space + 1);
        if (!model.isEmpty()) {
            if (iconId == 766 && model(model, "model.tool.leatherknife")) return LEATHER_KNIFE;
            if (iconId == 1201 && model(model, "model.tool.chisel")) return STONE_CHISEL;
            return null;
        }
        String resource = byName(baseName);
        return iconId == 766 && LEATHER_KNIFE.equals(resource)
                || iconId == 1201 && STONE_CHISEL.equals(resource) ? resource : null;
    }

    static String byName(String value) {
        String name = normalize(value);
        for (String rarity : new String[]{"rare ", "supreme ", "fantastic "}) {
            if (name.startsWith(rarity)) { name = name.substring(rarity.length()); break; }
        }
        int quality = name.indexOf(" (");
        if (quality >= 0) name = name.substring(0, quality);
        name = name.replace(',', ' ').trim().replaceAll("\\s+", " ");
        String exact = bare(name);
        if (exact != null) return exact;
        for (String material : MATERIALS) {
            if (name.startsWith(material + " ")) {
                String candidate = bare(name.substring(material.length() + 1));
                if (candidate != null) return candidate;
            }
            if (name.endsWith(" " + material)) {
                String candidate = bare(name.substring(0, name.length() - material.length() - 1));
                if (candidate != null) return candidate;
            }
        }
        return null;
    }

    private static String bare(String name) {
        if (name.equals("leather knife")) return LEATHER_KNIFE;
        if (name.equals("stone chisel")) return STONE_CHISEL;
        return null;
    }

    private static boolean model(String value, String prefix) {
        return value.equals(prefix) || value.startsWith(prefix + ".");
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
