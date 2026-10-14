package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Resolves nail sizes and separate key artwork without repainting shared neighbours. */
final class HardwareIcons {
    static final String LARGE_NAILS = "img.wurmhighres.hardware.large.nails";
    static final String SMALL_NAILS = "img.wurmhighres.hardware.small.nails";
    static final String KEY = "img.wurmhighres.hardware.key";
    static final String KEY_OF_THE_HEAVENS = "img.wurmhighres.hardware.key.of.the.heavens";
    static final String LOCK_PICKS = "img.wurmhighres.hardware.lock.picks";
    static final String RIVETS = "img.wurmhighres.hardware.rivets";
    static final String SMALL_LOCK = "img.wurmhighres.hardware.small.lock";
    static final String LARGE_LOCK = "img.wurmhighres.hardware.large.lock";
    static final String KEY_COPY = "img.wurmhighres.hardware.key.copy";
    static final String HOOKS = "img.wurmhighres.hardware.hooks";
    static final String EYELET = "img.wurmhighres.hardware.eyelet";
    private static final String[] METALS = metalNames();

    private HardwareIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        if (iconId != 781 && iconId != 789 && iconId != 769 && iconId != 770
                && iconId != 790 && iconId != 809 && iconId != 845 && iconId != 1488) return null;
        String model = normalize(modelDescriptor);
        int separator = model.lastIndexOf(' ');
        if (separator >= 0) model = model.substring(separator + 1);
        if (iconId == 781) {
            if (isModel(model, "model.tool.nails.large")) return LARGE_NAILS;
            if (isModel(model, "model.tool.nails.small")) return SMALL_NAILS;
        } else if (iconId == 789) {
            if (isModel(model, "model.artifact.keyheavens")) return KEY_OF_THE_HEAVENS;
            if (model.startsWith("model.artifact.")) return null;
            if (isModel(model, "model.key")) return KEY;
        } else if (iconId == 769 && isModel(model, "model.key.lockpicks")) {
            return LOCK_PICKS;
        } else if (iconId == 770 && isModel(model, "model.part.studs")) {
            return RIVETS;
        } else if (iconId == 790) {
            if (isModel(model, "model.lock.large.padlock") || isModel(model, "model.lock.gate")) return LARGE_LOCK;
            if (isModel(model, "model.lock.small.padlock") || isModel(model, "model.lock.door")
                    || isModel(model, "model.lock.boat") || model.equals("model.lock")) return SMALL_LOCK;
            for (String metal : METALS) {
                if (model.equals("model.lock." + metal)) return SMALL_LOCK;
            }
            if (model.startsWith("model.lock.")) return null;
        } else if (iconId == 809) {
            if (model.startsWith("model.artifact.")) return null;
            if (isModel(model, "model.key")) return KEY_COPY;
        } else if (iconId == 845 && isModel(model, "model.part.hooks")) {
            return HOOKS;
        } else if (iconId == 1488 && isModel(model, "model.part.islet")) {
            return EYELET;
        }
        String resource = byName(baseName);
        if (iconId == 781 && (LARGE_NAILS.equals(resource) || SMALL_NAILS.equals(resource))) return resource;
        if (iconId == 789 && (KEY.equals(resource) || KEY_OF_THE_HEAVENS.equals(resource))) return resource;
        if (iconId == 769 && LOCK_PICKS.equals(resource)) return resource;
        if (iconId == 770 && RIVETS.equals(resource)) return resource;
        if (iconId == 790 && (SMALL_LOCK.equals(resource) || LARGE_LOCK.equals(resource))) return resource;
        if (iconId == 809 && KEY_COPY.equals(resource)) return resource;
        if (iconId == 845 && HOOKS.equals(resource)) return resource;
        if (iconId == 1488 && EYELET.equals(resource)) return resource;
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
        for (String metal : METALS) {
            if (name.startsWith(metal + " ")) name = name.substring(metal.length() + 1);
            else if (name.endsWith(" " + metal)) name = name.substring(0, name.length() - metal.length() - 1);
            else if (name.startsWith("large " + metal + " ")) name = "large " + name.substring(7 + metal.length());
            else if (name.startsWith("small " + metal + " ")) name = "small " + name.substring(7 + metal.length());
        }
        if (name.equals("large nails") || name.equals("nails large") || name.equals("large nail")) return LARGE_NAILS;
        if (name.equals("small nails") || name.equals("nails small") || name.equals("small nail")) return SMALL_NAILS;
        if (name.equals("key") || name.equals("keys")) return KEY;
        if (name.equals("key of the heavens")) return KEY_OF_THE_HEAVENS;
        if (name.equals("lock picks") || name.equals("lock pick")) return LOCK_PICKS;
        if (name.equals("rivets") || name.equals("rivet")) return RIVETS;
        if (name.equals("door lock") || name.equals("small padlock") || name.equals("boat lock")) return SMALL_LOCK;
        if (name.equals("large padlock") || name.equals("gate lock")) return LARGE_LOCK;
        if (name.equals("key copy")) return KEY_COPY;
        if (name.equals("hooks") || name.equals("hook")) return HOOKS;
        if (name.equals("eyelet")) return EYELET;
        return null;
    }

    private static boolean isModel(String value, String model) {
        return value.equals(model) || value.startsWith(model + ".");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private static String[] metalNames() {
        Set<String> names = new LinkedHashSet<String>();
        for (int id = 1; id < 128; id++) {
            byte material = (byte) id;
            if (MaterialUtilities.isMetal(material)) {
                names.add(normalize(MaterialUtilities.getMaterialString(material)));
                names.add(normalize(MaterialUtilities.getClientMaterialString(material, true)));
            }
        }
        names.remove("");
        return names.toArray(new String[names.size()]);
    }
}
