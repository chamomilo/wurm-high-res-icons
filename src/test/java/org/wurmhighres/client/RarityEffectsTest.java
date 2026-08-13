package org.wurmhighres.client;

import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RarityEffectsTest {
    @Test
    public void auraTextureIsCircularRaggedAndTransparentAtCorners() {
        BufferedImage aura = RarityEffects.createAuraImage(96);
        int centerAlpha = alpha(aura, 48, 48);
        int ringAlpha = alpha(aura, 82, 48);

        assertTrue(centerAlpha > 150);
        assertTrue(ringAlpha > centerAlpha);
        assertEquals(0, alpha(aura, 0, 0));
        assertEquals(0, alpha(aura, 95, 95));
        assertTrue(alpha(aura, 88, 48) != alpha(aura, 48, 88));
    }

    @Test
    public void glowBreathesAndWhiteShimmerIsAnOccasionalSeparateCycle() {
        assertEquals(0.0, RarityEffects.animationPhase(0L), 0.000001);
        assertTrue(RarityEffects.animationPhase(-1L) > 0.99);
        assertEquals(22.0f, RarityEffects.glowDiameter(16.0f, 3, 0.0), 0.001f);
        assertEquals(28.0f, RarityEffects.glowDiameter(16.0f, 3, 0.5), 0.001f);
        assertEquals(0.0f, RarityEffects.whiteShimmerAtPhase(0.0), 0.001f);
        assertEquals(0.0f, RarityEffects.whiteShimmerAtPhase(0.69), 0.001f);
        assertEquals(1.0f, RarityEffects.whiteShimmerAtPhase(0.79), 0.001f);
        assertEquals(0.0f, RarityEffects.whiteShimmerAtPhase(0.89), 0.001f);
        assertEquals(0.5f, RarityEffects.shimmerProgressAtPhase(0.79), 0.001f);
    }

    @Test
    public void shimmerUsesAVisibleMovingArc() {
        BufferedImage rightArc = RarityEffects.createShimmerImage(96, 0.0);
        BufferedImage topArc = RarityEffects.createShimmerImage(96, -Math.PI / 2.0);
        assertTrue(alpha(rightArc, 82, 48) > alpha(rightArc, 48, 14));
        assertTrue(alpha(topArc, 48, 14) > alpha(topArc, 82, 48));
    }

    @Test
    public void starTextureHasFivePointsFacetsAndTransparentGaps() {
        BufferedImage star = RarityEffects.createStarImage(96, 0.0);
        assertTrue(alpha(star, 48, 4) > 100);
        assertTrue(alpha(star, 48, 48) > 100);
        assertEquals(0, alpha(star, 0, 0));
        assertEquals(0, alpha(star, 95, 95));
        assertNotEquals(star.getRGB(48, 30), star.getRGB(62, 40));
    }

    @Test
    public void starRotatesContinuouslyThroughPrecomputedFrames() {
        assertEquals(0.0, RarityEffects.starPhase(0L), 0.000001);
        assertEquals(0.5, RarityEffects.starPhase(2000000000L), 0.000001);
        assertEquals(0, RarityEffects.starFrameAtPhase(0.0));
        assertEquals(15, RarityEffects.starFrameAtPhase(0.5));
        assertEquals(25.0f, RarityEffects.starDiameter(16.0f, 3), 0.001f);
        BufferedImage first = RarityEffects.createStarImage(96, 0.0);
        BufferedImage rotated = RarityEffects.createStarImage(96, Math.PI / 10.0);
        assertNotEquals(first.getRGB(48, 4), rotated.getRGB(48, 4));
    }

    private static int alpha(BufferedImage image, int x, int y) {
        return (image.getRGB(x, y) >>> 24) & 255;
    }
}
