package org.wurmhighres.client;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FishingIconsTest {
    @Test
    public void fiveModernLinesShareOneSpoolWithoutCatchingTheirNeighbours() {
        for (String type : new String[]{"basic", "light", "medium", "heavy", "braided"}) {
            String name = type + " fishing line";
            assertEquals(FishingIcons.MODERN_LINE, FishingIcons.resource((short) 620, name, null));
            assertEquals(FishingIcons.MODERN_LINE, FishingIcons.byName("Rare " + name + ", cotton (20.0)"));
            assertEquals(FishingIcons.MODERN_LINE, FishingIcons.resource((short) 620, "Renamed spool",
                    "model.fishline." + type + ".cotton"));
            assertTrue(IconOverrides.hasCustomIcon(name));
        }
        for (String name : new String[]{"string of cloth", "bow string", "yarn", "old fine fishing line",
                "old fishing line", "basic fishing line fragment"}) {
            assertNull(FishingIcons.resource((short) 620, name, null));
            assertNull(FishingIcons.byName(name));
        }
        for (String model : new String[]{"model.resource.string.cotton", "model.resource.yarn.cotton",
                "model.tool.fish.hookstring.cotton", "model.fishline.basicpart.cotton"}) {
            assertNull(FishingIcons.resource((short) 620, "Renamed item", model));
        }
        assertNull(FishingIcons.resource((short) 785, "basic fishing line", null));
    }

    @Test
    public void oldFineHookUsesItsNameBecauseTheMetalHookSharesItsModel() {
        for (String name : new String[]{"old fine fishing hook", "Rare old fine fishing hook, iron",
                "Supreme seryll old fine fishing hook", "Fantastic old fine fishing hook, gold (99.0)"}) {
            assertEquals(FishingIcons.OLD_FINE_HOOK, FishingIcons.resource((short) 785, name,
                    "model.tool.fish.hook.iron"));
            assertTrue(IconOverrides.hasCustomIcon(name));
        }
        for (String name : new String[]{"metal fishing hook", "old fine fishing line", "old fishing line",
                "Renamed hook", "old fine fishing hook fragment", "hooks"}) {
            assertNull(FishingIcons.resource((short) 785, name, "model.tool.fish.hook.iron"));
        }
        assertNull(FishingIcons.resource((short) 845, "old fine fishing hook", null));
    }

    @Test
    public void keepNetResolvesInInventoryRecipesAndRenamedGroundSlots() {
        assertEquals(FishingIcons.KEEP_NET, FishingIcons.resource((short) 343, "fish keep net", null));
        assertEquals(FishingIcons.KEEP_NET, FishingIcons.byName("Rare fish keep net, cotton"));
        assertEquals(FishingIcons.KEEP_NET, FishingIcons.resource((short) 343, "My fish keeper",
                "model.container.net.keep.planted.cotton"));
        assertTrue(IconOverrides.hasCustomIcon("fish keep net"));
        assertNull(FishingIcons.resource((short) 885, "fishing net", "model.tool.fishing.net.cotton"));
        assertNull(FishingIcons.resource((short) 343, "net fragment", "model.container.net.keeper.cotton"));
    }
}
