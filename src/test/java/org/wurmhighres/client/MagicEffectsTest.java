package org.wurmhighres.client;

import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class MagicEffectsTest {
    @Test
    public void animationWrapsAndSelectsPrecomputedFrames() {
        assertEquals(0.0, MagicEffects.animationPhase(0L), 0.000001);
        assertEquals(0.5, MagicEffects.animationPhase(MagicEffects.PERIOD_NANOS / 2L), 0.000001);
        assertTrue(MagicEffects.animationPhase(-1L) > 0.99);
        assertEquals(0, MagicEffects.frameIndex(0L));
        assertEquals(18, MagicEffects.frameIndex(MagicEffects.PERIOD_NANOS / 2L));
        assertEquals(MagicEffects.FRAME_COUNT - 1, MagicEffects.frameIndex(-1L));
    }

    @Test
    public void wholeOpaqueSurfaceChangesWhileAlphaIsPreserved() {
        BufferedImage base = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 2; y < 14; y++) {
            for (int x = 2; x < 14; x++) {
                int alpha = x == 2 ? 96 : 255;
                base.setRGB(x, y, (alpha << 24) | (126 << 16) | (54 << 8) | 28);
            }
        }

        BufferedImage first = MagicEffects.createFrame(base, 0.0);
        BufferedImage later = MagicEffects.createFrame(base, 0.47);
        assertEquals(0, alpha(first, 0, 0));
        assertEquals(96, alpha(first, 2, 8));
        assertEquals(255, alpha(first, 8, 8));
        assertEquals(alpha(first, 8, 8), alpha(later, 8, 8));
        assertNotEquals(first.getRGB(8, 8), later.getRGB(8, 8));

        int changed = 0;
        for (int y = 2; y < 14; y++) {
            for (int x = 2; x < 14; x++) {
                if (first.getRGB(x, y) != later.getRGB(x, y)) {
                    changed++;
                }
            }
        }
        assertTrue("the prismatic pass does not cover the whole item", changed > 130);
    }

    @Test
    public void fullCycleReturnsToTheSamePixels() {
        BufferedImage base = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        base.setRGB(1, 1, 0xff8c3a24);
        assertEquals(MagicEffects.createFrame(base, 0.0).getRGB(1, 1),
                MagicEffects.createFrame(base, 1.0).getRGB(1, 1));
    }

    private static int alpha(BufferedImage image, int x, int y) {
        return (image.getRGB(x, y) >>> 24) & 255;
    }
}
