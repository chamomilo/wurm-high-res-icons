package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Keeps unfinished tool heads distinct from their assembled tools. */
final class ToolPartIcons {
    static final String WOODEN_SHOVEL = "img.wurmhighres.toolpart.shovel.blade.wood";
    private static final short[] ICONS = {720, 720, 721, 722, 723, 725, 726};
    private static final String[] NAMES = {"chisel blade", "leather knife blade", "mallet head",
            "hammer head", "pickaxe head", "rake blade", "shovel blade"};
    private static final String[] MODELS = {"model.tool.blade.chisel", "model.tool.blade.leatherknife",
            "model.tool.blade.hammer", "model.tool.blade.hammer", "model.tool.pickaxe.blade",
            "model.tool.blade.rake", "model.tool.shovel.blade"};
    private static final String[] MATERIALS = materialNames();
    private static final String[] RESOURCES = {
            "img.wurmhighres.toolpart.chisel.blade", "img.wurmhighres.toolpart.leather.knife.blade",
            "img.wurmhighres.toolpart.mallet.head", "img.wurmhighres.toolpart.hammer.head",
            "img.wurmhighres.toolpart.pickaxe.head", "img.wurmhighres.toolpart.rake.blade",
            "img.wurmhighres.toolpart.shovel.blade"};

    private ToolPartIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        boolean supported = false;
        for (short id : ICONS) if (id == iconId) supported = true;
        if (!supported) return null;
        String model = normalize(modelDescriptor);
        int separator = model.lastIndexOf(' ');
        if (separator >= 0) model = model.substring(separator + 1);
        if (!model.isEmpty()) {
            // Crude stone parts belong to separate templates and designs.
            if (model.contains(".crude")) return null;
            for (int i = 0; i < ICONS.length; i++) {
                if (iconId == ICONS[i] && (model.equals(MODELS[i]) || model.startsWith(MODELS[i] + "."))) {
                    return materialVariant(resource(i), model);
                }
            }
            return null;
        }
        String resource = byName(baseName);
        if (iconId == 726 && WOODEN_SHOVEL.equals(resource)) return resource;
        for (int i = 0; i < ICONS.length; i++) {
            if (iconId == ICONS[i] && resource(i).equals(resource)) return resource;
        }
        return null;
    }

    static String inventoryResource(short iconId, String baseName, byte material) {
        String resource = resource(iconId, baseName, null);
        return RESOURCES[6].equals(resource) && MaterialUtilities.isWood(material) ? WOODEN_SHOVEL : resource;
    }

    static String byName(String value) {
        String name = normalize(value);
        for (String rarity : new String[]{"rare ", "supreme ", "fantastic "}) {
            if (name.startsWith(rarity)) {
                name = name.substring(rarity.length());
                break;
            }
        }
        int quality = name.indexOf(" (");
        if (quality >= 0) name = name.substring(0, quality);
        name = name.replace(',', ' ').trim().replaceAll("\\s+", " ");
        String exact = byBareName(name);
        if (exact != null) return materialVariant(exact, value);
        for (String material : MATERIALS) {
            if (name.startsWith(material + " ")) {
                String candidate = byBareName(name.substring(material.length() + 1));
                if (candidate != null) return materialVariant(candidate, value);
            }
            if (name.endsWith(" " + material)) {
                String candidate = byBareName(name.substring(0, name.length() - material.length() - 1));
                if (candidate != null) return materialVariant(candidate, value);
            }
        }
        return null;
    }

    private static String byBareName(String name) {
        if (name.equals("leather knife head")) return resource(1);
        if (name.equals("wooden shovel blade")) return WOODEN_SHOVEL;
        for (int i = 0; i < NAMES.length; i++) if (NAMES[i].equals(name)) return resource(i);
        return null;
    }

    private static String resource(int index) {
        return RESOURCES[index];
    }

    private static String materialVariant(String resource, String descriptor) {
        if (RESOURCES[6].equals(resource) && MaterialUtilities.isWood(
                ShieldIcons.materialFromDescriptor(descriptor, null))) return WOODEN_SHOVEL;
        return resource;
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
