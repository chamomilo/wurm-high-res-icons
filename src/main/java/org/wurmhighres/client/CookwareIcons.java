package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Selects the two different pans that share stock cell 784. */
final class CookwareIcons {
    static final String FRYING_PAN = "img.wurmhighres.cookware.frying.pan";
    static final String SAUCE_PAN = "img.wurmhighres.cookware.sauce.pan";
    private static final String[] METALS = metalNames();

    private CookwareIcons() { }

    static String resource(short iconId, String baseName, String modelDescriptor) {
        if (iconId != 784) return null;
        String model = normalize(modelDescriptor);
        // Ground descriptors contain a display name followed by getModelName().
        // Only inspect the final model token, so a renamed item cannot win.
        int separator = model.lastIndexOf(' ');
        if (separator >= 0) model = model.substring(separator+1);
        if (isModel(model, "model.tool.saucepan")) return SAUCE_PAN;
        if (isModel(model, "model.tool.fryingpan")) return FRYING_PAN;
        return byName(baseName);
    }

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
        int quality = name.indexOf(" (");
        if (quality >= 0) name = name.substring(0, quality).trim();
        for (String metal : METALS) {
            if (name.startsWith(metal + " ")) {
                name = name.substring(metal.length()+1);
                break;
            }
        }
        if (name.equals("frying pan") || name.equals("frying pans")) return FRYING_PAN;
        if (name.equals("sauce pan") || name.equals("sauce pans")) return SAUCE_PAN;
        return null;
    }

    private static boolean isModel(String descriptor, String model) {
        return descriptor.equals(model) || descriptor.startsWith(model + ".");
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
