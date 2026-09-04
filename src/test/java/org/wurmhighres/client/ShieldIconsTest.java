package org.wurmhighres.client;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ShieldIconsTest {
    private static final int[] SHIELD_IDS = {970, 971, 972, 1010, 1011, 1012};

    @Test
    public void allSixShieldCellsHaveOneBaseAndOneMask() throws Exception {
        for (int id : SHIELD_IDS) {
            assertTrue(ShieldIcons.isShieldIcon((short) id));
            BufferedImage base = read(ShieldIcons.baseResource((short) id));
            BufferedImage mask = read(ShieldIcons.maskResource((short) id));
            assertEquals(32, base.getWidth());
            assertEquals(32, base.getHeight());
            assertEquals(32, mask.getWidth());
            assertEquals(32, mask.getHeight());
            assertTrue("base icon " + id + " is empty", visiblePixels(base) > 100);
            assertTrue("mask " + id + " has no protected pixels", countMask(mask, 32) > 20);
            assertTrue("mask " + id + " has no tintable pixels", countMask(mask, 160) > 20);
        }
        assertFalse(ShieldIcons.isShieldIcon((short) 969));
        assertFalse(ShieldIcons.isShieldIcon((short) 1013));
    }

    @Test
    public void materialNamesResolveForWoodAndMetalDescriptors() {
        assertEquals(38, ShieldIcons.materialFromDescriptor(
                "large wooden shield, oak", ShieldIcons.Family.WOOD));
        assertEquals(39, ShieldIcons.materialFromDescriptor(
                "model.shield.medium.cedarwood", ShieldIcons.Family.WOOD));
        assertEquals(7, ShieldIcons.materialFromDescriptor(
                "model.shield.small.gold", ShieldIcons.Family.METAL));
        assertEquals(56, ShieldIcons.materialFromDescriptor(
                "small metal shield, adamantine", ShieldIcons.Family.METAL));
        assertEquals(0, ShieldIcons.materialFromDescriptor(
                "small metal shield", ShieldIcons.Family.METAL));
        assertEquals(0, ShieldIcons.materialFromDescriptor(
                "small wooden shield, oak", ShieldIcons.Family.METAL));
    }

    @Test
    public void shieldNamesResolveWithoutMatchingUnrelatedItems() {
        assertTrue(ShieldIcons.recognizesName("small wooden shield"));
        assertTrue(ShieldIcons.recognizesName("medium metal shield, iron"));
        assertTrue(ShieldIcons.recognizesName("model.shield.large.gold"));
        assertTrue(ShieldIcons.recognizesName("shield, brass"));
        assertTrue(ShieldIcons.recognizesName("large shield, seryll"));
        assertFalse(ShieldIcons.recognizesName("large metal bowl"));
        assertFalse(ShieldIcons.recognizesName("shield fragment"));
    }

    @Test
    public void bundledFallbackUsesTheGamesMaterialMultipliers() {
        assertArrayEquals(new float[]{0.823f, 0.69f, 0.553f},
                ShieldIcons.fallbackColour("brass"), 0.0001f);
        assertArrayEquals(new float[]{0.968f, 0.721f, 0.208f},
                ShieldIcons.fallbackColour("gold"), 0.0001f);
        assertArrayEquals(new float[]{0.77f, 0.75f, 0.68f},
                ShieldIcons.fallbackColour("oakenwood"), 0.0001f);
    }

    @Test
    public void dynamicAssetsUseMappedGameResourceKeys() {
        assertEquals("img.wurmhighres.shield.small.wood",
                ShieldIcons.gameResourceName("shield-small-wood.png"));
        assertEquals("img.wurmhighres.shield.large.metal.mask",
                ShieldIcons.gameResourceName("shield-large-metal-mask.png"));
    }

    @Test
    public void tintUsesTheMaskAndPreservesAlphaAndProtectedMetalwork() {
        BufferedImage base = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        base.setRGB(0, 0, 0xff806040);
        base.setRGB(1, 0, 0xff806040);
        BufferedImage mask = new BufferedImage(2, 1, BufferedImage.TYPE_BYTE_GRAY);
        mask.setRGB(0, 0, 0xff000000);
        mask.setRGB(1, 0, 0xffffffff);

        BufferedImage tinted = ShieldIcons.tint(base, mask, 0.25f, 1.0f, 1.0f);
        assertEquals(base.getRGB(0, 0), tinted.getRGB(0, 0));
        assertTrue(((tinted.getRGB(1, 0) >>> 16) & 255) < 128);
        assertEquals(96, (tinted.getRGB(1, 0) >>> 8) & 255);
        assertEquals(64, tinted.getRGB(1, 0) & 255);
        assertEquals(255, (tinted.getRGB(1, 0) >>> 24) & 255);
    }

    @Test
    public void kiteShieldsPointDownInTheStoredGameTexture() throws Exception {
        for (int id : new int[]{1011, 1012}) {
            BufferedImage shield = read(ShieldIcons.baseResource((short) id));
            assertTrue("shield " + id + " is vertically inverted",
                    opaqueWidth(shield, 4) > opaqueWidth(shield, 28));
        }
    }

    private static BufferedImage read(String resource) throws Exception {
        assertNotNull(resource);
        try (InputStream stream = ShieldIconsTest.class.getResourceAsStream(resource)) {
            assertNotNull(resource, stream);
            return ImageIO.read(stream);
        }
    }

    private static int visiblePixels(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (((image.getRGB(x, y) >>> 24) & 255) != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int countMask(BufferedImage mask, int threshold) {
        int count = 0;
        for (int y = 0; y < mask.getHeight(); y++) {
            for (int x = 0; x < mask.getWidth(); x++) {
                if (((mask.getRGB(x, y) >>> 16) & 255) >= threshold) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int opaqueWidth(BufferedImage image, int row) {
        int count = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            if (((image.getRGB(x, row) >>> 24) & 255) != 0) {
                count++;
            }
        }
        return count;
    }
}
