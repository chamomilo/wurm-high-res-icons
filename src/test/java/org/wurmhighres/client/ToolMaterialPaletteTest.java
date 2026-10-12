package org.wurmhighres.client;

import org.junit.Test;
import java.awt.image.BufferedImage;
import static org.junit.Assert.*;

public class ToolMaterialPaletteTest {
    @Test
    public void referenceMetalsHaveTheRequestedRelativeBrightnessAndHue() {
        int silver = sample("silver", 160), zinc = sample("zinc", 160);
        int steel = sample("steel", 160), iron = sample("iron", 160);
        assertTrue("zinc must be whiter than silver", brightness(zinc) > brightness(silver));
        assertTrue("zinc should be almost white", brightness(zinc) > 220);
        assertTrue("silver must stay light grey", brightness(silver) > 195);
        assertTrue("zinc should be neutral", maxChannel(zinc) - minChannel(zinc) < 5);
        assertTrue("steel must be visibly lighter than iron", brightness(steel) - brightness(iron) > 30);
        int copper = sample("copper", 160), gold = sample("gold", 160);
        assertTrue("copper must be saturated orange-red", r(copper) > g(copper) + 55 && g(copper) > b(copper) + 20);
        assertTrue("gold must be yellow", r(gold) > b(gold) + 80 && g(gold) > b(gold) + 70);
        int electrum = sample("electrum", 160);
        assertTrue("electrum needs its yellow-green tint", g(electrum) > r(electrum) && r(electrum) > b(electrum) + 70);
        assertTrue("lead must stay dark", brightness(sample("lead", 160)) < brightness(iron));
    }

    @Test
    public void brightMetalsPreserveShadingAlphaAndUnselectedPixels() {
        for (String material : new String[]{"gold", "silver", "zinc", "copper", "steel", "iron", "brass", "tin", "lead", "electrum"}) {
            BufferedImage base = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
            base.setRGB(0, 0, 0x45787878);
            base.setRGB(1, 0, 0xFFD2D2D2);
            base.setRGB(2, 0, 0xFFBC8042);
            BufferedImage mask = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
            mask.setRGB(0, 0, 0xFFFFFFFF);
            mask.setRGB(1, 0, 0xFFFFFFFF);
            float[] colour = ToolMaterialPalette.referenceColour(material);
            BufferedImage result = ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]);
            assertEquals(material + " alpha changed", 0x45, result.getRGB(0, 0) >>> 24);
            assertEquals(material + " wooden handle changed", base.getRGB(2, 0), result.getRGB(2, 0));
            assertTrue(material + " lost shading", brightness(result.getRGB(1, 0)) > brightness(result.getRGB(0, 0)) + 35);
        }
    }

    @Test
    public void runtimeUsesReferenceColoursAndFantasyMetalsKeepTheGamePalette() {
        assertArrayEquals(ToolMaterialPalette.referenceColour("gold"), ToolMaterialPalette.colourMultipliers((byte) 7), 0.0001f);
        assertArrayEquals(ToolMaterialPalette.referenceColour("iron"), ToolMaterialPalette.colourMultipliers((byte) 11), 0.0001f);
        assertArrayEquals(ToolMaterialPalette.referenceColour("zinc"), ToolMaterialPalette.colourMultipliers((byte) 13), 0.0001f);
        assertNull(ToolMaterialPalette.referenceColour("adamantine"));
        assertNull(ToolMaterialPalette.referenceColour("seryll"));
        assertNull(ToolMaterialPalette.referenceColour("oak"));
        float[] adamantine = ShieldIcons.fallbackColour("adamantine");
        assertTrue(adamantine[2] > adamantine[1] && adamantine[1] > adamantine[0]);
        float[] seryll = ShieldIcons.fallbackColour("seryll");
        assertTrue(seryll[0] > seryll[1] && seryll[1] > seryll[2]);
    }

    private static int sample(String material, int grey) {
        BufferedImage base = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        base.setRGB(0, 0, 0xFF000000 | grey << 16 | grey << 8 | grey);
        BufferedImage mask = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        mask.setRGB(0, 0, 0xFFFFFFFF);
        float[] colour = ToolMaterialPalette.referenceColour(material);
        return ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]).getRGB(0, 0);
    }
    private static int r(int p) { return (p >>> 16) & 255; }
    private static int g(int p) { return (p >>> 8) & 255; }
    private static int b(int p) { return p & 255; }
    private static int brightness(int p) { return (r(p) + g(p) + b(p)) / 3; }
    private static int maxChannel(int p) { return Math.max(r(p), Math.max(g(p), b(p))); }
    private static int minChannel(int p) { return Math.min(r(p), Math.min(g(p), b(p))); }
}
