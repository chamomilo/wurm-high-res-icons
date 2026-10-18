package org.wurmhighres.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class CuttingToolIconsTest {
    @Test
    public void ordinaryKnifeLeatherKnifeAndItsBladeStaySeparate() {
        for (String name : new String[]{"leather knife", "Rare leather knife, iron", "Fantastic seryll leather knife"}) {
            assertEquals(CuttingToolIcons.LEATHER_KNIFE, CuttingToolIcons.byName(name));
            assertEquals(CuttingToolIcons.LEATHER_KNIFE, CuttingToolIcons.resource((short) 766, name, null));
            assertTrue(IconOverrides.hasCustomIcon(name));
        }
        assertNull(CuttingToolIcons.byName("knife"));
        assertNull(CuttingToolIcons.byName("butchering knife"));
        assertNull(CuttingToolIcons.byName("leather knife blade"));
        assertNull(CuttingToolIcons.byName("leather knife head"));
        assertNull(CuttingToolIcons.byName("leather knife fragment"));
        assertNull(CuttingToolIcons.resource((short) 940, "knife", null));
        assertNull(CuttingToolIcons.resource((short) 720, "leather knife", null));
        assertEquals(ToolPartIcons.byName("leather knife blade"), ToolPartIcons.byName("Rare leather knife head, iron"));
        assertEquals(CuttingToolIcons.STONE_CHISEL, CuttingToolIcons.byName("Supreme stone chisel, steel"));
        assertNull(CuttingToolIcons.byName("chisel blade"));
    }

    @Test
    public void exactGroundModelsKeepToolAndPartIdentities() {
        assertEquals(CuttingToolIcons.LEATHER_KNIFE,
                CuttingToolIcons.resource((short) 766, "renamed", "model.tool.leatherknife.iron"));
        assertEquals(CuttingToolIcons.STONE_CHISEL,
                CuttingToolIcons.resource((short) 1201, "renamed", "name model.tool.chisel.steel"));
        assertNull(CuttingToolIcons.resource((short) 766, "leather knife", "model.tool.food.knife.iron"));
        assertNull(CuttingToolIcons.resource((short) 766, "leather knife", "model.tool.leatherknifefragment.iron"));
        assertNull(CuttingToolIcons.resource((short) 1201, "stone chisel", "model.tool.blade.chisel.iron"));
        assertNull(CuttingToolIcons.resource((short) 720, "stone chisel", "model.tool.chisel.iron"));
        assertNull(CuttingToolIcons.resource((short) 940, "leather knife", "model.tool.leatherknife.iron"));
    }
}
