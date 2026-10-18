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
    static final String OLD_FINE_LINE = "img.wurmhighres.fishing.old.fine.line";
    static final String OLD_LINE = "img.wurmhighres.fishing.old.line";
    static final String METAL_HOOK = "img.wurmhighres.fishing.metal.hook";
    static final String OLD_FINE_ROD = "img.wurmhighres.fishing.old.fine.rod";
    static final String OLD_UNSTRUNG_ROD = "img.wurmhighres.fishing.old.unstrung.rod";
    static final String OLD_ROD = "img.wurmhighres.fishing.old.rod";
    static final String POLE = "img.wurmhighres.fishing.pole";
    static final String OLD_HOOK = "img.wurmhighres.fishing.old.fishing.hook";
    static final String WOOD_HOOK = "img.wurmhighres.fishing.wooden.fishing.hook";
    static final String BONE_HOOK = "img.wurmhighres.fishing.bone.fishing.hook";
    static final String ROD = "img.wurmhighres.fishing.fishing.rod";
    static final String NET = "img.wurmhighres.fishing.fishing.net";
    static final String WOOD_REEL = "img.wurmhighres.fishing.wood.reel";
    static final String METAL_REEL = "img.wurmhighres.fishing.metal.reel";
    static final String PRO_REEL = "img.wurmhighres.fishing.professional.reel";
    static final String LIGHT_REEL = "img.wurmhighres.fishing.light.fishing.reel";
    static final String MEDIUM_REEL = "img.wurmhighres.fishing.medium.fishing.reel";
    static final String DEEP_REEL = "img.wurmhighres.fishing.deep.water.fishing.reel";
    static final String PRO_FISH_REEL = "img.wurmhighres.fishing.professional.fishing.reel";
    private static final String[] LINE_TYPES = {"basic", "light", "medium", "heavy", "braided"};
    private static final String[] MATERIALS = materialNames();
    private static final String[] WOOD_MODELS = materialModels(true);
    private static final String[] METAL_MODELS = materialModels(false);
    private static final String[] ASSEMBLED_REELS = {LIGHT_REEL, MEDIUM_REEL, DEEP_REEL, PRO_FISH_REEL};

    private FishingIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        if (iconId != 343 && iconId != 620 && iconId != 785 && iconId != 786 && iconId != 805
                && iconId != 846 && iconId != 865 && iconId != 885 && (iconId < 1494 || iconId > 1498)) return null;
        String resource = byName(baseName);
        if (matchesIcon(iconId, resource)) return resource;
        String model = normalize(modelDescriptor);
        int separator = model.lastIndexOf(' ');
        if (separator >= 0) model = model.substring(separator + 1);
        if (iconId == 343 && isModel(model, "model.container.net.keep")) return KEEP_NET;
        if (iconId == 620) {
            for (String type : LINE_TYPES) {
                if (isModel(model, "model.fishline." + type)) return MODERN_LINE;
            }
        }
        if (iconId == 786) {
            if (isModel(model, "model.tool.fish.rod.ironhook")) return OLD_FINE_ROD;
            if (isModel(model, "model.tool.fish.rod.unstrung")) return OLD_UNSTRUNG_ROD;
            if (isModel(model, "model.tool.fish.pole")) return POLE;
            if (model.equals("model.tool.fish.rod")
                    || materialModel(model, "model.tool.fish.rod", true)) return OLD_ROD;
        }
        if (iconId == 785) {
            if (materialModel(model, "model.tool.fish.hookstring", false)) return OLD_FINE_LINE;
            if (materialModel(model, "model.tool.fish.hookstring", true)) return OLD_LINE;
        }
        if (iconId == 846 && (model.equals("model.tool.fish.rod")
                || materialModel(model, "model.tool.fish.rod", true))) return ROD;
        if (iconId == 865 && isModel(model, "model.tool.fish.hook")) return BONE_HOOK;
        if (iconId == 885 && isModel(model, "model.container.net.fish")) return NET;
        if (iconId >= 1495 && iconId <= 1498 && isModel(model, "model.fishingreel")) {
            return ASSEMBLED_REELS[iconId - 1495];
        }
        // The two hook templates share exactly the same model. A generic
        // hookstring model also omits the iron/wood distinction. Keep their
        // existing image when a renamed ground item cannot be identified.
        return null;
    }

    private static boolean matchesIcon(short iconId, String resource) {
        if (resource == null) return false;
        if (iconId == 343) return KEEP_NET.equals(resource);
        if (iconId == 620) return MODERN_LINE.equals(resource);
        if (iconId == 785) return OLD_FINE_HOOK.equals(resource) || METAL_HOOK.equals(resource)
                || OLD_FINE_LINE.equals(resource) || OLD_LINE.equals(resource);
        if (iconId == 805) return OLD_HOOK.equals(resource) || WOOD_HOOK.equals(resource);
        if (iconId == 846) return ROD.equals(resource);
        if (iconId == 865) return BONE_HOOK.equals(resource);
        if (iconId == 885) return NET.equals(resource);
        if (iconId == 1494) return WOOD_REEL.equals(resource) || METAL_REEL.equals(resource) || PRO_REEL.equals(resource);
        if (iconId == 1495) return LIGHT_REEL.equals(resource);
        if (iconId == 1496) return MEDIUM_REEL.equals(resource);
        if (iconId == 1497) return DEEP_REEL.equals(resource);
        if (iconId == 1498) return PRO_FISH_REEL.equals(resource);
        return iconId == 786 && (OLD_FINE_ROD.equals(resource) || OLD_UNSTRUNG_ROD.equals(resource)
                || OLD_ROD.equals(resource) || POLE.equals(resource));
    }

    private static boolean materialModel(String model, String prefix, boolean wood) {
        if (!model.startsWith(prefix + ".")) return false;
        for (String material : wood ? WOOD_MODELS : METAL_MODELS) {
            if (isModel(model, prefix + "." + material)) return true;
        }
        return false;
    }

    private static String[] materialModels(boolean wood) {
        Set<String> names = new LinkedHashSet<String>();
        for (int id = 1; id < 128; id++) {
            byte material = (byte) id;
            if (wood ? MaterialUtilities.isWood(material) : MaterialUtilities.isMetal(material)) {
                names.add(normalize(MaterialUtilities.getMaterialString(material)));
            }
        }
        names.remove("");
        return names.toArray(new String[names.size()]);
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
        String exact = byBareName(name);
        if (exact != null) return exact;
        for (String material : MATERIALS) {
            // "Metal" is part of the hook's actual type, as well as a material
            // label. Try each complete candidate without stripping type words.
            if (name.startsWith(material + " ")) {
                String resource = byBareName(name.substring(material.length() + 1));
                if (resource != null) return resource;
            }
            if (name.endsWith(" " + material)) {
                String resource = byBareName(name.substring(0, name.length() - material.length() - 1));
                if (resource != null) return resource;
            }
        }
        return null;
    }

    private static String byBareName(String name) {
        if (name.equals("old fishing hook")) return OLD_HOOK;
        if (name.equals("wooden fishing hook")) return WOOD_HOOK;
        if (name.equals("bone fishing hook")) return BONE_HOOK;
        if (name.equals("fishing rod")) return ROD;
        if (name.equals("fishing net")) return NET;
        if (name.equals("wood reel")) return WOOD_REEL;
        if (name.equals("metal reel")) return METAL_REEL;
        if (name.equals("professional reel")) return PRO_REEL;
        if (name.equals("light fishing reel")) return LIGHT_REEL;
        if (name.equals("medium fishing reel")) return MEDIUM_REEL;
        if (name.equals("deep water fishing reel")) return DEEP_REEL;
        if (name.equals("professional fishing reel")) return PRO_FISH_REEL;
        if (name.equals("fish keep net")) return KEEP_NET;
        if (name.equals("old fine fishing hook")) return OLD_FINE_HOOK;
        if (name.equals("old fine fishing line")) return OLD_FINE_LINE;
        if (name.equals("old fishing line")) return OLD_LINE;
        if (name.equals("metal fishing hook")) return METAL_HOOK;
        if (name.equals("old fine fishing rod")) return OLD_FINE_ROD;
        if (name.equals("old unstrung fishing rod")) return OLD_UNSTRUNG_ROD;
        if (name.equals("old fishing rod")) return OLD_ROD;
        if (name.equals("fishing pole")) return POLE;
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
