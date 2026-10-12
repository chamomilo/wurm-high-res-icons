package org.wurmhighres.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class StorageIconsTest {
    @Test
    public void buoyUsesItsOwnShapeWithoutReplacingBarrelsOrTubs() {
        assertEquals(StorageIcons.BUOY, StorageIcons.resource((short) 245, "buoy", null));
        assertEquals(StorageIcons.BUOY, StorageIcons.resource((short) 245, "Rare birchwood buoy", null));
        assertEquals(StorageIcons.BUOY, StorageIcons.byName("Supreme buoys, cedarwood"));
        assertEquals(StorageIcons.BUOY, StorageIcons.resource((short) 245, "barrel", "model.light.buoy.planted.birchwood.lit"));
        assertNull(StorageIcons.resource((short) 245, "buoy", "model.container.barrel.large.birchwood"));
        assertNull(StorageIcons.resource((short) 245, "tub", null));
        assertNull(StorageIcons.resource((short) 245, "small barrel", null));
        assertNull(StorageIcons.resource((short) 246, "buoy", null));
        assertNull(StorageIcons.byName("buoy anchor"));
        assertTrue(IconOverrides.hasCustomIcon("buoy"));
        assertEquals("img.wurmhighres.smallbarrel", IconOverrides.containerResource((short) 245, "small barrel", 2.0f));
        assertEquals("img.wurmhighres.hugetub", IconOverrides.containerResource((short) 245, "tub", 60.0f));
    }

    @Test
    public void separatesSharedCellsWithDecoratedInventoryAndRecipeNames() {
        assertEquals(StorageIcons.GIFT, StorageIcons.resource((short) 243, "gift", null));
        assertEquals(StorageIcons.GIFT_BOX, StorageIcons.resource((short) 243, "Rare gift box, birchwood", null));
        assertEquals(StorageIcons.CHEST, StorageIcons.resource((short) 244, "large chest", null));
        assertEquals(StorageIcons.TACKLE_BOX, StorageIcons.resource((short) 244, "Supreme tackle box, tin", null));
        assertEquals(StorageIcons.COMPARTMENT, StorageIcons.resource((short) 244, "compartment", null));
        assertEquals(StorageIcons.GIFT_BOX, StorageIcons.byName("Fantastic gift boxes (99.0 quality)"));
        assertEquals(StorageIcons.COMPARTMENT, StorageIcons.byName("Rare compartments, cedarwood"));
        assertEquals(StorageIcons.TACKLE_BOX, StorageIcons.resource((short) 244, "Rare tin tackle box", null));
        assertEquals(StorageIcons.COMPARTMENT, StorageIcons.byName("cedarwood compartment"));
        assertTrue(IconOverrides.hasCustomIcon("tackle box"));
        assertTrue(IconOverrides.hasCustomIcon("gift box"));
    }

    @Test
    public void groundModelTakesPrecedenceOverPlayerNamesAndHonoursCellIds() {
        assertEquals(StorageIcons.GIFT_BOX, StorageIcons.resource((short) 243, "gift", "model.container.giftbox.green."));
        assertEquals(StorageIcons.TACKLE_BOX, StorageIcons.resource((short) 244, "chest", "model.container.tacklebox."));
        assertEquals(StorageIcons.COMPARTMENT, StorageIcons.resource((short) 244, "chest", "model.furniture.wooden.shelf."));
        assertEquals(StorageIcons.CHEST, StorageIcons.resource((short) 244, "tackle box", "model.container.chest.small.birchwood."));
        assertNull(StorageIcons.resource((short) 245, "compartment", "model.furniture.wooden.shelf."));
        assertNull(StorageIcons.byName("treasure chest"));
        assertNull(StorageIcons.byName("gift box key"));
        assertNull(StorageIcons.byName("a picture of a chest"));
        assertNull(StorageIcons.byName(null));
    }
}
