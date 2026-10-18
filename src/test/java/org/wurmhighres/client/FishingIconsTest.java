package org.wurmhighres.client;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FishingIconsTest {
    @Test
    public void remainingFishingItemsKeepTwelveDistinctTypesAndMaterialPrefixes() {
        Object[][] cases = {
                {805, "old fishing hook", FishingIcons.OLD_HOOK},
                {805, "wooden fishing hook", FishingIcons.WOOD_HOOK},
                {846, "fishing rod", FishingIcons.ROD},
                {865, "bone fishing hook", FishingIcons.BONE_HOOK},
                {885, "fishing net", FishingIcons.NET},
                {1494, "wood reel", FishingIcons.WOOD_REEL},
                {1494, "metal reel", FishingIcons.METAL_REEL},
                {1494, "professional reel", FishingIcons.PRO_REEL},
                {1495, "light fishing reel", FishingIcons.LIGHT_REEL},
                {1496, "medium fishing reel", FishingIcons.MEDIUM_REEL},
                {1497, "deep water fishing reel", FishingIcons.DEEP_REEL},
                {1498, "professional fishing reel", FishingIcons.PRO_FISH_REEL}
        };
        java.util.Set<String> resources = new java.util.HashSet<String>();
        for (Object[] value : cases) {
            short icon = ((Integer) value[0]).shortValue();
            String name = (String) value[1], expected = (String) value[2];
            assertEquals(expected, FishingIcons.resource(icon, name, null));
            assertEquals(expected, FishingIcons.byName("Rare " + name + ", steel (99.0)"));
            assertEquals(expected, FishingIcons.byName("Fantastic birchwood " + name));
            assertTrue(IconOverrides.hasCustomIcon(name));
            assertNull(FishingIcons.resource((short) 343, name, null));
            assertNull(FishingIcons.byName(name + " fragment"));
            resources.add(expected);
        }
        assertEquals(12, resources.size());
    }

    @Test
    public void distinctFishingSlotsResolveRenamedModelsWithoutGuessingSharedTypes() {
        assertEquals(FishingIcons.ROD, FishingIcons.resource((short) 846, "My rod", "model.tool.fish.rod.birchwood"));
        assertEquals(FishingIcons.NET, FishingIcons.resource((short) 885, "My net", "model.container.net.fish.cotton"));
        assertEquals(FishingIcons.BONE_HOOK, FishingIcons.resource((short) 865, "My hook", "model.tool.fish.hook.bone"));
        String[] expected = {FishingIcons.LIGHT_REEL, FishingIcons.MEDIUM_REEL, FishingIcons.DEEP_REEL, FishingIcons.PRO_FISH_REEL};
        for (int i = 0; i < 4; i++) {
            assertEquals(expected[i], FishingIcons.resource((short) (1495 + i), "My reel", "model.fishingreel.birchwood"));
        }
        assertNull(FishingIcons.resource((short) 805, "My hook", "model.tool.fish.hook.birchwood"));
        assertNull(FishingIcons.resource((short) 1494, "My part", "model.part.reel.steel"));
        assertNull(FishingIcons.resource((short) 846, "My item", "model.tool.fish.rod.unstrung.birchwood"));
        assertNull(FishingIcons.resource((short) 885, "My item", "model.container.net.fishpart.cotton"));
        assertNull(FishingIcons.resource((short) 1495, "My item", "model.fishingreelpart.birchwood"));
    }
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
        for (String name : new String[]{"string of cloth", "bow string", "yarn", "basic fishing line fragment"}) {
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
        for (String name : new String[]{"Renamed hook", "old fine fishing hook fragment", "hooks"}) {
            assertNull(FishingIcons.resource((short) 785, name, "model.tool.fish.hook.iron"));
        }
        assertNull(FishingIcons.resource((short) 845, "old fine fishing hook", null));
    }

    @Test
    public void sevenOldFishingItemsKeepIndependentInventoryAndRecipeIdentities() {
        Object[][] cases = {
                {785, "old fine fishing line", FishingIcons.OLD_FINE_LINE},
                {785, "old fishing line", FishingIcons.OLD_LINE},
                {785, "metal fishing hook", FishingIcons.METAL_HOOK},
                {786, "old fine fishing rod", FishingIcons.OLD_FINE_ROD},
                {786, "old unstrung fishing rod", FishingIcons.OLD_UNSTRUNG_ROD},
                {786, "old fishing rod", FishingIcons.OLD_ROD},
                {786, "fishing pole", FishingIcons.POLE}
        };
        java.util.Set<String> resources = new java.util.HashSet<String>();
        for (Object[] value : cases) {
            short icon = ((Integer) value[0]).shortValue();
            String name = (String) value[1];
            String expected = (String) value[2];
            assertEquals(expected, FishingIcons.resource(icon, name, null));
            assertEquals(expected, FishingIcons.byName("Rare " + name + ", birchwood (99.0)"));
            assertEquals(expected, FishingIcons.byName("Fantastic seryll " + name));
            assertTrue(IconOverrides.hasCustomIcon(name));
            assertNull(FishingIcons.resource((short) 620, name, null));
            assertNull(FishingIcons.resource((short) 805, name, null));
            assertNull(FishingIcons.byName(name + " fragment"));
            resources.add(expected);
        }
        assertEquals(7, resources.size());
        assertEquals(FishingIcons.ROD, FishingIcons.byName("fishing rod"));
    }

    @Test
    public void renamedGroundRodsResolveSpecificTypesBeforeThePlainRod() {
        String[][] cases = {
                {"model.tool.fish.rod.ironhook.birchwood", FishingIcons.OLD_FINE_ROD},
                {"model.tool.fish.rod.unstrung.oakenwood", FishingIcons.OLD_UNSTRUNG_ROD},
                {"model.tool.fish.rod.birchwood", FishingIcons.OLD_ROD},
                {"model.tool.fish.rod", FishingIcons.OLD_ROD},
                {"model.tool.fish.pole.birchwood", FishingIcons.POLE}
        };
        for (String[] value : cases) {
            assertEquals(value[1], FishingIcons.resource((short) 786, "My renamed rod", "renamed " + value[0]));
        }
        for (String model : new String[]{"model.tool.fish.rodpiece.birchwood", "model.tool.fish.rod.ironhooked.birchwood",
                "model.tool.fish.rod.unknown", "model.tool.fish.polepart.birchwood"}) {
            assertNull(FishingIcons.resource((short) 786, "My renamed item", model));
        }
        // The authoritative item name retains its precise type if the model is shared.
        assertEquals(FishingIcons.OLD_UNSTRUNG_ROD, FishingIcons.resource((short) 786,
                "old unstrung fishing rod", "model.tool.fish.rod.birchwood"));
    }

    @Test
    public void oldLineMaterialsDisambiguateRigsWhileSharedHooksRemainUncertain() {
        assertEquals(FishingIcons.OLD_FINE_LINE, FishingIcons.resource((short) 785,
                "My renamed line", "model.tool.fish.hookstring.iron"));
        assertEquals(FishingIcons.OLD_LINE, FishingIcons.resource((short) 785,
                "My renamed line", "model.tool.fish.hookstring.birchwood"));
        assertNull(FishingIcons.resource((short) 785, "My renamed line", "model.tool.fish.hookstring"));
        assertNull(FishingIcons.resource((short) 785, "My renamed hook", "model.tool.fish.hook.iron"));
        assertNull(FishingIcons.resource((short) 785, "My renamed hook", "model.tool.fish.hookstringpart.iron"));
        assertEquals(FishingIcons.METAL_HOOK, FishingIcons.resource((short) 785,
                "metal fishing hook", "model.tool.fish.hook.iron"));
        assertEquals(FishingIcons.OLD_FINE_HOOK, FishingIcons.resource((short) 785,
                "old fine fishing hook", "model.tool.fish.hook.iron"));
    }

    @Test
    public void keepNetResolvesInInventoryRecipesAndRenamedGroundSlots() {
        assertEquals(FishingIcons.KEEP_NET, FishingIcons.resource((short) 343, "fish keep net", null));
        assertEquals(FishingIcons.KEEP_NET, FishingIcons.byName("Rare fish keep net, cotton"));
        assertEquals(FishingIcons.KEEP_NET, FishingIcons.resource((short) 343, "My fish keeper",
                "model.container.net.keep.planted.cotton"));
        assertTrue(IconOverrides.hasCustomIcon("fish keep net"));
        assertEquals(FishingIcons.NET, FishingIcons.resource((short) 885, "fishing net", "model.tool.fishing.net.cotton"));
        assertNull(FishingIcons.resource((short) 343, "net fragment", "model.container.net.keeper.cotton"));
    }
}
