package org.wurmhighres.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class HardwareIconsTest {
    @Test
    public void nailSizesSurviveNativeNamesRarityAndMaterialPlacement() {
        for (String name : new String[]{"large nails", "Rare large iron nails", "seryll large nails",
                "Fantastic nails, large, copper", "large nails (99.0 quality)"}) {
            assertEquals(name, HardwareIcons.LARGE_NAILS, HardwareIcons.resource((short) 781, name, null));
        }
        for (String name : new String[]{"small nails", "Supreme small steel nails", "nails, small, tin"}) {
            assertEquals(name, HardwareIcons.SMALL_NAILS, HardwareIcons.resource((short) 781, name, null));
        }
        assertNull(HardwareIcons.byName("nails"));
        assertNull(HardwareIcons.byName("large nails fragment"));
        assertNull(HardwareIcons.byName("a picture of small nails"));
    }

    @Test
    public void groundModelSelectsSizeDespiteRenamingAndGuardsSharedNeighbours() {
        assertEquals(HardwareIcons.LARGE_NAILS,
                HardwareIcons.resource((short) 781, "small nails", "small nails model.tool.nails.large.iron"));
        assertEquals(HardwareIcons.SMALL_NAILS,
                HardwareIcons.resource((short) 781, "large nails", "model.tool.nails.large.iron model.tool.nails.small.gold"));
        assertNull(HardwareIcons.resource((short) 770, "large nails", "model.tool.nails.large.iron"));
        assertNull(HardwareIcons.resource((short) 781, "unfinished nails", "model.tool.nails.largehandle.iron"));
    }

    @Test
    public void ordinaryAndArtifactKeysKeepSeparateIdentities() {
        assertEquals(HardwareIcons.KEY, HardwareIcons.byName("Rare key, iron"));
        assertEquals(HardwareIcons.KEY, HardwareIcons.resource((short) 789, "renamed", "model.key.steel"));
        assertEquals(HardwareIcons.KEY_OF_THE_HEAVENS, HardwareIcons.byName("key of the heavens"));
        assertEquals(HardwareIcons.KEY_OF_THE_HEAVENS, HardwareIcons.byName("Rare key of the heavens, gold"));
        assertNull(HardwareIcons.byName("key form"));
        assertNull(HardwareIcons.byName("key mould"));
        assertEquals(HardwareIcons.KEY_OF_THE_HEAVENS,
                HardwareIcons.resource((short) 789, "key", "model.artifact.keyheavens.gold"));
        assertEquals(HardwareIcons.KEY_OF_THE_HEAVENS,
                HardwareIcons.resource((short) 789, "key of the heavens", null));
        assertNull(HardwareIcons.resource((short) 789, "key", "model.artifact.keyheavensfragment.gold"));
        assertNull(HardwareIcons.resource((short) 809, "key of the heavens", null));
        assertNull(HardwareIcons.byName("key of the heavens fragment"));
        assertTrue(IconOverrides.hasCustomIcon("key of the heavens"));
    }

    @Test
    public void hardwareHasDistinctMaterialResources() {
        assertTrue(IconOverrides.hasCustomIcon("large nails"));
        assertTrue(IconOverrides.hasCustomIcon("small nails"));
        assertTrue(IconOverrides.hasCustomIcon("lock picks"));
        assertTrue(IconOverrides.hasCustomIcon("rivets"));
        assertEquals(HardwareIcons.LOCK_PICKS, HardwareIcons.resource((short) 769, "lock picks", null));
        assertEquals(HardwareIcons.RIVETS, HardwareIcons.resource((short) 770, "rivets", null));
        assertNull(HardwareIcons.resource((short) 789, "rivets", null));
    }

    @Test
    public void fiveLockTypesKeepTheirApprovedTwoModelGroups() {
        for (String name : new String[]{"door lock", "small padlock", "Rare boat lock, copper", "small iron padlock"}) {
            assertEquals(name, HardwareIcons.SMALL_LOCK, HardwareIcons.resource((short) 790, name, null));
        }
        for (String name : new String[]{"large padlock", "gate lock", "Supreme large steel padlock"}) {
            assertEquals(name, HardwareIcons.LARGE_LOCK, HardwareIcons.resource((short) 790, name, null));
        }
        assertEquals(HardwareIcons.LARGE_LOCK, HardwareIcons.resource((short) 790, "small padlock", "model.lock.gate.iron"));
        assertEquals(HardwareIcons.SMALL_LOCK, HardwareIcons.resource((short) 790, "large padlock", "model.lock.boat.iron"));
        assertEquals(HardwareIcons.SMALL_LOCK, HardwareIcons.resource((short) 790, "renamed", "model.lock.iron"));
        assertNull(HardwareIcons.resource((short) 790, "gate lock", "model.lock.gatefragment.iron"));
        assertNull(HardwareIcons.byName("padlock"));
        assertNull(HardwareIcons.resource((short) 789, "gate lock", null));
    }

    @Test
    public void keyCopyKeepsTheSameShapeAndSeparateIdentity() {
        assertEquals(HardwareIcons.KEY_COPY, HardwareIcons.resource((short) 809, "Rare key copy, copper", null));
        assertEquals(HardwareIcons.KEY_COPY, HardwareIcons.resource((short) 809, "renamed", "model.key.copper"));
        assertNull(HardwareIcons.resource((short) 809, "key copy", "model.artifact.keyheavens.gold"));
        assertNull(HardwareIcons.resource((short) 789, "key copy", null));
        assertNull(HardwareIcons.byName("key copy fragment"));
    }

    @Test
    public void hooksAndEyeletDoNotCaptureFishingHooksOrOtherParts() {
        assertEquals(HardwareIcons.HOOKS, HardwareIcons.resource((short) 845, "hooks", null));
        assertEquals(HardwareIcons.HOOKS, HardwareIcons.resource((short) 845, "renamed", "model.part.hooks.iron"));
        assertEquals(HardwareIcons.EYELET, HardwareIcons.resource((short) 1488, "Rare eyelet, gold", null));
        assertEquals(HardwareIcons.EYELET, HardwareIcons.resource((short) 1488, "renamed", "model.part.islet.iron"));
        assertNull(HardwareIcons.byName("fine fishing hook"));
        assertNull(HardwareIcons.byName("old fine fishing hook"));
        assertNull(HardwareIcons.byName("eyelet fragment"));
        assertNull(HardwareIcons.resource((short) 785, "hooks", null));
    }
}
