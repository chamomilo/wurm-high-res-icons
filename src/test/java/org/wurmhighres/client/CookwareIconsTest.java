package org.wurmhighres.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class CookwareIconsTest {
    @Test
    public void separatesInventoryAndRecipePansWithRarityAndMetalNames() {
        assertEquals(CookwareIcons.FRYING_PAN, CookwareIcons.resource((short) 784, "frying pan", null));
        assertEquals(CookwareIcons.SAUCE_PAN, CookwareIcons.resource((short) 784, "Rare tin sauce pan", null));
        assertEquals(CookwareIcons.FRYING_PAN, CookwareIcons.byName("Fantastic frying pans, seryll"));
        assertEquals(CookwareIcons.SAUCE_PAN, CookwareIcons.byName("Supreme sauce pans (99.0 quality)"));
        assertTrue(IconOverrides.hasCustomIcon("sauce pan"));
        assertTrue(IconOverrides.hasCustomIcon("frying pan"));
    }

    @Test
    public void groundModelOverridesRenamedItemsButKeepsTheCellGuard() {
        assertEquals(CookwareIcons.SAUCE_PAN,
                CookwareIcons.resource((short) 784, "frying pan", "frying pan model.tool.saucepan.iron"));
        assertEquals(CookwareIcons.FRYING_PAN,
                CookwareIcons.resource((short) 784, "sauce pan", "model.tool.saucepan.iron model.tool.fryingpan.seryll"));
        assertNull(CookwareIcons.resource((short) 804, "sauce pan", "model.tool.saucepan.iron"));
        assertNull(CookwareIcons.resource((short) 784, "unfinished pan", "model.tool.saucepanhandle.iron"));
    }

    @Test
    public void ingredientAndPartNamesDoNotAcquirePanArtwork() {
        for (String name : new String[]{null,"pan","frying pan handle","sauce pan fragment",
                "unfinished sauce pan","a picture of a frying pan","pottery bowl"}) {
            assertNull(name,CookwareIcons.byName(name));
        }
    }
}
