package org.wurmhighres.client;

import org.junit.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class ToolPartIconsTest {
    private static final String[] NAMES = {"chisel blade", "leather knife blade", "mallet head",
            "hammer head", "pickaxe head", "rake blade", "shovel blade"};
    private static final short[] IDS = {720, 720, 721, 722, 723, 725, 726};

    @Test
    public void sevenPartsKeepDistinctArtworkAcrossNamesRarityAndMaterials() {
        Set<String> distinct = new HashSet<String>();
        for (int i = 0; i < NAMES.length; i++) {
            String expected = "img.wurmhighres.toolpart." + NAMES[i].replace(' ', '.');
            distinct.add(ToolPartIcons.resource(IDS[i], NAMES[i], null));
            for (String name : new String[]{NAMES[i], "Rare " + NAMES[i],
                    "Fantastic " + NAMES[i] + ", gold", "seryll " + NAMES[i], NAMES[i] + " (90.0 quality)"}) {
                assertEquals(name, expected, ToolPartIcons.resource(IDS[i], name, null));
                assertEquals(name, expected, ToolPartIcons.byName(name));
            }
            assertTrue(IconOverrides.hasCustomIcon(NAMES[i]));
            assertNull(ToolPartIcons.byName(NAMES[i] + " fragment"));
            assertNull(ToolPartIcons.resource((short) 743, NAMES[i], null));
        }
        assertEquals(7, distinct.size());
        for (String assembled : new String[]{"stone chisel", "leather knife", "mallet", "hammer", "pickaxe", "rake", "shovel"}) {
            assertNull(ToolPartIcons.byName(assembled));
        }
    }

    @Test
    public void groundModelsDisambiguateSharedBladesAndRejectCrudeParts() {
        assertEquals("img.wurmhighres.toolpart.chisel.blade",
                ToolPartIcons.resource((short) 720, "leather knife blade", "model.tool.blade.chisel.iron"));
        assertEquals("img.wurmhighres.toolpart.leather.knife.blade",
                ToolPartIcons.resource((short) 720, "chisel blade", "renamed model.tool.blade.leatherknife.steel"));
        String[] models = {"model.tool.blade.chisel", "model.tool.blade.leatherknife", "model.tool.blade.hammer",
                "model.tool.blade.hammer", "model.tool.pickaxe.blade", "model.tool.blade.rake", "model.tool.shovel.blade"};
        for (int i = 0; i < models.length; i++) {
            String expected = "img.wurmhighres.toolpart." + NAMES[i].replace(' ', '.');
            assertEquals(expected, ToolPartIcons.resource(IDS[i], "renamed", models[i] + ".iron"));
            assertNull(ToolPartIcons.resource(IDS[i], "renamed", models[i] + "fragment.iron"));
        }
        assertNull(ToolPartIcons.resource((short) 720, "renamed", "model.tool.blade.unknown.iron"));
        assertNull(ToolPartIcons.resource((short) 723, "pickaxe head", "model.tool.pickaxe.blade.crude.stone"));
        assertNull(ToolPartIcons.resource((short) 726, "shovel blade", "model.tool.shovel.blade.crude.stone"));
        assertNull(ToolPartIcons.byName("crude pickaxe head"));
        assertNull(ToolPartIcons.byName("crude shovel blade"));
    }

    @Test
    public void woodenShovelTemplateUsesItsWoodBaseInEveryContext() {
        assertEquals(ToolPartIcons.WOODEN_SHOVEL, ToolPartIcons.inventoryResource((short) 726, "shovel blade", (byte) 14));
        assertEquals(ToolPartIcons.WOODEN_SHOVEL, ToolPartIcons.byName("Rare shovel blade, birchwood"));
        assertEquals(ToolPartIcons.WOODEN_SHOVEL, ToolPartIcons.byName("wooden shovel blade"));
        assertEquals(ToolPartIcons.WOODEN_SHOVEL,
                ToolPartIcons.resource((short) 726, "renamed", "model.tool.shovel.blade.birchwood"));
        assertEquals("img.wurmhighres.toolpart.shovel.blade",
                ToolPartIcons.inventoryResource((short) 726, "shovel blade", (byte) 11));
        assertNull(ToolPartIcons.resource((short) 720, "wooden shovel blade", null));
    }
}
