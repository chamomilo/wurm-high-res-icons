package org.wurmhighres.client;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AtlasContractTest {
    @Test
    public void replacementAtlasesHaveExpectedGeometryAndPixels() throws Exception {
        File project = new File(System.getProperty("wurmHighresProjectDir"));
        verifyPack(project, "resource-pack");
        verifyPack(project, "resource-pack-realistic");
    }

    @Test
    public void realisticPackChangesToolsButSharesNonToolItems() throws Exception {
        File project = new File(System.getProperty("wurmHighresProjectDir"));
        BufferedImage readableTools = readAtlas(project, "resource-pack", "tools.png", 640, 384);
        BufferedImage realisticTools = readAtlas(project, "resource-pack-realistic", "tools.png", 640, 384);
        int[] toolIds = {738, 741, 742, 743, 745, 746, 747, 749, 750, 752, 780};
        for (int id : toolIds) {
            assertTrue("styles are identical for icon " + id,
                    differentPixels(readableTools, realisticTools, id) > 50);
        }
        assertEquals(0, differentPixels(readableTools, realisticTools, 755));
        assertEquals(0, differentPixels(readableTools, realisticTools, 803));

        BufferedImage readableWeapons = readAtlas(project, "resource-pack", "weapons.png", 640, 400);
        BufferedImage realisticWeapons = readAtlas(project, "resource-pack-realistic", "weapons.png", 640, 400);
        assertTrue(differentPixels(readableWeapons, realisticWeapons, 1207) > 50);
        assertEquals(0, differentPixels(readableWeapons, realisticWeapons, 1201));

        BufferedImage readableResource = readAtlas(project, "resource-pack", "resource.png", 640, 384);
        BufferedImage realisticResource = readAtlas(project, "resource-pack-realistic", "resource.png", 640, 384);
        assertEquals(0, differentPixels(readableResource, realisticResource, 520));
        assertEquals(0, differentPixels(readableResource, realisticResource, 602));
        assertEquals(0, differentPixels(readableResource, realisticResource, 621));
    }

    private static void verifyPack(File project, String packDirectory) throws Exception {
        BufferedImage misc = readAtlas(project, packDirectory, "misc.png", 640, 400);
        BufferedImage resource = readAtlas(project, packDirectory, "resource.png", 640, 384);
        BufferedImage resource2 = readAtlas(project, packDirectory, "resource2.png", 640, 384);
        BufferedImage tools = readAtlas(project, packDirectory, "tools.png", 640, 384);
        BufferedImage weapons = readAtlas(project, packDirectory, "weapons.png", 640, 400);

        int[] miscIds = {240, 241, 242, 282};
        for (int id : miscIds) {
            assertCellVisible(misc, id);
        }
        assertCellVisible(resource, 520);
        assertCellVisible(resource, 602);
        assertCellVisible(resource, 621);
        int[] anatomyIds = {495, 496, 498, 514, 515, 534, 535, 603};
        for (int id : anatomyIds) {
            assertCellVisible(resource, id);
        }
        assertCellVisible(resource2, 1489);

        int[] toolIds = {738, 741, 742, 743, 745, 746, 747, 749, 750, 752,
                754, 755, 760, 766, 780, 802, 803, 882, 902};
        for (int id : toolIds) {
            assertCellVisible(tools, id);
        }

        assertCellVisible(weapons, 1201);
        assertCellVisible(weapons, 1207);

        String[] customMaterials = {
                "gland.png", "twisted-horn.png",
                "hide.png", "leather.png",
                "drake-hide-black.png", "drake-hide-blue.png",
                "drake-hide-green.png", "drake-hide-red.png",
                "drake-hide-white.png",
                "dragon-scale-black.png", "dragon-scale-blue.png",
                "dragon-scale-green.png", "dragon-scale-red.png",
                "dragon-scale-white.png"
        };
        for (String name : customMaterials) {
            BufferedImage image = ImageIO.read(new File(project,
                    packDirectory + "/gui/custom/" + name));
            assertEquals(name, 32, image.getWidth());
            assertEquals(name, 32, image.getHeight());
            assertTrue(name + " is empty",
                    nonTransparentPixels(image, 0, 0, 32, 32) > 100);
            assertEquals(name + " top-left corner is opaque", 0,
                    (image.getRGB(0, 0) >>> 24) & 255);
        }
    }

    private static BufferedImage readAtlas(
            File project, String packDirectory, String name, int width, int height) throws Exception {
        BufferedImage atlas = ImageIO.read(new File(project, packDirectory + "/gui/" + name));
        assertEquals(width, atlas.getWidth());
        assertEquals(height, atlas.getHeight());
        return atlas;
    }

    private static int differentPixels(BufferedImage first, BufferedImage second, int iconId) {
        int local = iconId % 240;
        int left = (local % 20) * 32;
        int top = (local / 20) * 32;
        int count = 0;
        for (int y = top; y < top + 32; y++) {
            for (int x = left; x < left + 32; x++) {
                if (first.getRGB(x, y) != second.getRGB(x, y)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void assertCellVisible(BufferedImage atlas, int iconId) {
        int local = iconId % 240;
        int x = (local % 20) * 32;
        int y = (local / 20) * 32;
        assertTrue("icon " + iconId + " is empty",
                nonTransparentPixels(atlas, x, y, 32, 32) > 100);
    }

    private static int nonTransparentPixels(BufferedImage image, int x, int y, int width, int height) {
        int count = 0;
        for (int row = y; row < y + height; row++) {
            for (int column = x; column < x + width; column++) {
                if (((image.getRGB(column, row) >>> 24) & 255) != 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
