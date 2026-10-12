package org.wurmhighres.client;

import com.wurmonline.shared.util.MaterialUtilities;

/** Reference-calibrated icon colours; world materials keep the client's palette. */
final class ToolMaterialPalette {
    private ToolMaterialPalette() {
    }

    static float[] colourMultipliers(byte material) {
        float[] colour = referenceColour(MaterialUtilities.getMaterialString(material));
        return colour != null ? colour : ShieldIcons.colourMultipliers(material);
    }

    static float[] referenceColour(String material) {
        // Linear-light multipliers preserve the neutral master's shading and
        // use only its semantic metal mask. Zinc is the whitest material;
        // silver is slightly greyer, and iron stays darker than steel.
        // Fantasy metals retain the game's XML colours.
        if ("gold".equals(material)) return rgb(1.45f, 1.10f, 0.12f);
        if ("silver".equals(material)) return rgb(1.65f, 1.70f, 1.72f);
        if ("zinc".equals(material)) return rgb(2.10f, 2.12f, 2.08f);
        if ("copper".equals(material)) return rgb(1.30f, 0.37f, 0.16f);
        if ("steel".equals(material)) return rgb(1.12f, 1.17f, 1.24f);
        if ("iron".equals(material)) return rgb(0.62f, 0.66f, 0.70f);
        if ("brass".equals(material)) return rgb(1.12f, 0.84f, 0.27f);
        if ("tin".equals(material)) return rgb(1.30f, 1.38f, 1.50f);
        if ("lead".equals(material)) return rgb(0.48f, 0.50f, 0.54f);
        if ("electrum".equals(material)) return rgb(1.30f, 1.36f, 0.25f);
        return null;
    }

    private static float[] rgb(float red, float green, float blue) {
        return new float[]{red, green, blue};
    }
}
