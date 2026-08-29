package org.wurmhighres.client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IconOverridesTest {
    @Test
    public void carvingKnifeGetsASeparateClientOnlyTexture() {
        assertTrue(IconOverrides.hasCustomIcon("carving knife"));
        assertTrue(IconOverrides.hasCustomIcon("CARVING KNIFE"));
        assertTrue(IconOverrides.hasCustomIcon("carving knife, iron"));
    }

    @Test
    public void prayerCharmGetsTheFormerPrayerChainTexture() {
        assertTrue(IconOverrides.hasCustomIcon("prayer charm"));
        assertTrue(IconOverrides.hasCustomIcon("prayer charm of Vynora"));
        assertTrue(IconOverrides.hasCustomIcon("PRAYER CHARM OF FO"));
        assertTrue(IconOverrides.hasCustomIcon(" prayer charm of Libila "));
    }

    @Test
    public void otherItemsKeepTheirStockTexture() {
        assertFalse(IconOverrides.hasCustomIcon("stone chisel"));
        assertFalse(IconOverrides.hasCustomIcon("butchering knife"));
        assertFalse(IconOverrides.hasCustomIcon("chain"));
        assertFalse(IconOverrides.hasCustomIcon("prayer charmed pendant"));
        assertFalse(IconOverrides.hasCustomIcon(null));
    }

    @Test
    public void pressIsSeparatedFromFruitPressSharingIcon246() {
        assertTrue(IconOverrides.hasCustomIcon("press"));
        assertTrue(IconOverrides.hasCustomIcon("press, oakenwood"));
        assertTrue(IconOverrides.isPress("press (unfinished)"));
        assertFalse(IconOverrides.hasCustomIcon("fruit press"));
        assertFalse(IconOverrides.isPress("papyrus press"));
    }

    @Test
    public void barrelFamilySharingIcon245GetsDistinctResources() {
        assertEquals("img.wurmhighres.smallbarrel", IconOverrides.containerResource(
                (short) 245, "small barrel, cedarwood", 2.0f));
        assertEquals("img.wurmhighres.largebarrel", IconOverrides.containerResource(
                (short) 245, "large barrel, oakenwood", 10.0f));
        assertEquals("img.wurmhighres.hugetub", IconOverrides.containerResource(
                (short) 245, "tub, pinewood", 60.0f));
        assertEquals("img.wurmhighres.hugeoilbarrel", IconOverrides.containerResource(
                (short) 245, "model.container.barrel.huge.oil.", 60.0f));
        assertEquals("img.wurmhighres.winebarrel", IconOverrides.containerResource(
                (short) 245, "model.container.winebarrel.small.", 2.0f));
        assertEquals("img.wurmhighres.smallbarrel", IconOverrides.containerResource(
                (short) 245, "barrel", 2.0f));
        assertEquals("img.wurmhighres.largebarrel", IconOverrides.containerResource(
                (short) 245, "barrel", 10000.0f));
        assertNull(IconOverrides.containerResource((short) 246, "large barrel", 10.0f));
        assertNull(IconOverrides.containerResource((short) 245, "press", 10.0f));
    }

    @Test
    public void smeltingPotIsSeparatedFromPotteryBowlSharingIcon511() {
        assertTrue(IconOverrides.isSmeltingPot((short) 511, "smelting pot"));
        assertTrue(IconOverrides.isSmeltingPot(
                (short) 511, "model.container.smeltingpot."));
        assertTrue(IconOverrides.isSmeltingPot((short) 511,
                "smelting pot smelting pot, pottery smelting pot null"));
        assertTrue(IconOverrides.hasCustomIcon("smelting pot"));
        assertFalse(IconOverrides.isSmeltingPot((short) 511, "pottery bowl"));
        assertFalse(IconOverrides.isSmeltingPot((short) 491, "clay smelting pot"));
    }

    @Test
    public void brandingIronIsSeparatedFromPliersSharingIcon780() {
        assertTrue(IconOverrides.isBrandingIron((short) 780, "branding iron"));
        assertTrue(IconOverrides.isBrandingIron(
                (short) 780, "model.tool.brandingiron."));
        assertTrue(IconOverrides.isBrandingIron((short) 780,
                "branding iron branding iron, iron"));
        assertTrue(IconOverrides.hasCustomIcon("branding iron"));
        assertFalse(IconOverrides.isBrandingIron((short) 780, "pliers"));
        assertFalse(IconOverrides.isBrandingIron((short) 781, "branding iron"));
    }

    @Test
    public void anatomyItemsSharingAtlasCellsGetDistinctResources() {
        assertEquals("img.wurmhighres.twistedhorn", IconOverrides.anatomyResource(
                (short) 496, "twisted horn"));
        assertNull(IconOverrides.anatomyResource((short) 496, "horn"));
        assertNull(IconOverrides.anatomyResource((short) 495, "twisted horn"));

        assertEquals("img.wurmhighres.gland", IconOverrides.anatomyResource(
                (short) 515, "gland"));
        assertNull(IconOverrides.anatomyResource((short) 515, "bladder"));
        assertNull(IconOverrides.anatomyResource((short) 514, "gland"));

        assertTrue(IconOverrides.hasCustomIcon("twisted horn"));
        assertTrue(IconOverrides.hasCustomIcon("gland"));
        assertFalse(IconOverrides.hasCustomIcon("horn"));
        assertFalse(IconOverrides.hasCustomIcon("bladder"));
    }

    @Test
    public void largeAnvilCanBeSeparatedFromSmallAnvilSharingIcon791() {
        assertTrue(IconOverrides.isLargeAnvil((short) 791, 10.0f, "anvil"));
        assertTrue(IconOverrides.isLargeAnvil((short) 791, 10000.0f, "anvil"));
        assertTrue(IconOverrides.isLargeAnvil((short) 791, 2.0f, "large anvil"));
        assertFalse(IconOverrides.isLargeAnvil((short) 791, 2.0f, "anvil"));
        assertFalse(IconOverrides.isLargeAnvil((short) 791, 2000.0f, "small anvil"));
        assertFalse(IconOverrides.isLargeAnvil((short) 790, 10000.0f, "large anvil"));
        assertTrue(IconOverrides.isLargeAnvilName("large anvil"));
        assertFalse(IconOverrides.isLargeAnvilName("small anvil"));
    }

    @Test
    public void sharedPeltAtlasIdIsSeparatedByItemName() {
        assertNull(IconOverrides.materialResource((short) 602,
                "large rat pelt", 0.0f, 0.0f, 0.0f));
        assertEquals("img.wurmhighres.hide", IconOverrides.materialResource(
                (short) 602, "crocodile hide", 0.0f, 0.0f, 0.0f));
        assertEquals("img.wurmhighres.leather", IconOverrides.materialResource(
                (short) 602, "leather", 0.0f, 0.0f, 0.0f));
        assertEquals("img.wurmhighres.drakehide.red", IconOverrides.materialResource(
                (short) 602, "drake hide", 215.0f, 40.0f, 40.0f));
        assertNull(IconOverrides.materialResource((short) 603,
                "leather glove", 0.0f, 0.0f, 0.0f));
    }

    @Test
    public void dragonColoursUseTheClientsExactRgbSignatures() {
        assertEquals("green", IconOverrides.dragonColour(10.0f, 210.0f, 10.0f));
        assertEquals("black", IconOverrides.dragonColour(10.0f, 10.0f, 10.0f));
        assertEquals("white", IconOverrides.dragonColour(255.0f, 255.0f, 255.0f));
        assertEquals("red", IconOverrides.dragonColour(215.0f, 40.0f, 40.0f));
        assertEquals("blue", IconOverrides.dragonColour(40.0f, 40.0f, 215.0f));
        assertEquals("white", IconOverrides.dragonColour(0.0f, 0.0f, 0.0f));
    }

    @Test
    public void everyDragonScaleColourGetsItsOwnResource() {
        assertEquals("img.wurmhighres.dragonscale.black", IconOverrides.materialResource(
                (short) 554, "scale", 10.0f, 10.0f, 10.0f));
        assertEquals("img.wurmhighres.dragonscale.blue", IconOverrides.materialResource(
                (short) 554, "dragon scale", 40.0f, 40.0f, 215.0f));
        assertEquals("img.wurmhighres.dragonscale.green", IconOverrides.materialResource(
                (short) 554, "scale", 10.0f, 210.0f, 10.0f));
        assertEquals("img.wurmhighres.dragonscale.red", IconOverrides.materialResource(
                (short) 554, "scale", 215.0f, 40.0f, 40.0f));
        assertEquals("img.wurmhighres.dragonscale.white", IconOverrides.materialResource(
                (short) 554, "scale", 255.0f, 255.0f, 255.0f));
    }
}
