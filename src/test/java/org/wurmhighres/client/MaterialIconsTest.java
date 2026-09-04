package org.wurmhighres.client;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MaterialIconsTest {
    private static final String[] SLUGS = {
            "praying-statuette", "chain", "butchering-knife", "fork", "spoon",
            "awl", "clay-shaper", "metal-brush", "grooming-brush", "knife",
            "stone-chisel", "crowbar", "mallet", "hammer", "pickaxe", "rake",
            "shovel", "saw", "scissors", "file", "trowel", "sickle", "pliers",
            "spindle", "small-anvil", "spatula", "steel-and-flint", "fruit-press",
            "bee-smoker", "cheese-drill", "small-bucket", "needle", "compass",
            "lantern", "spyglass", "rope-tool", "hatchet", "branding-iron",
            "smelting-pot", "small-barrel", "large-barrel", "huge-tub",
            "huge-oil-barrel", "wine-barrel", "press", "large-anvil",
            "prayer-charm", "carving-knife"
    };

    @Test
    public void resolvesRedesignedItemsWithoutCatchingSharedAtlasNeighbours() {
        assertTrue(MaterialIcons.recognizes((short) 1207, "hatchet, copper"));
        assertTrue(MaterialIcons.recognizes((short) 1207, "small axe, steel"));
        assertTrue(MaterialIcons.recognizes((short) 882, "metal brush, steel"));
        assertTrue(MaterialIcons.recognizes((short) 791, "anvil, gold"));
        assertFalse(MaterialIcons.recognizes((short) 791, "large anvil, gold"));
        assertFalse(MaterialIcons.recognizes((short) 743, "reed plants"));
        assertFalse(MaterialIcons.recognizes((short) 746, "rice"));
        assertFalse(MaterialIcons.recognizes((short) 748, "papyrus sheet"));
        assertFalse(MaterialIcons.recognizes((short) 750, "strawberry seeds"));
    }

    @Test
    public void exposesDeterministicLogicalResourceNames() {
        assertEquals("img.wurmhighres.material.hatchet",
                MaterialIcons.baseResource((short) 1207, "hatchet, iron"));
        assertEquals("img.wurmhighres.material.hatchet.mask",
                MaterialIcons.maskResource((short) 1207, "hatchet, iron"));
        assertEquals("img.wurmhighres.material.small.bucket",
                MaterialIcons.baseResource((short) 265, "bucket, birchwood"));
    }

    @Test
    public void packContainsOneBaseAndOneMaskPerArtwork() throws Exception {
        File project = projectDirectory();
        for (String packName : new String[]{"resource-pack"}) {
            File pack = new File(project, packName);
            String mappings = new String(Files.readAllBytes(
                    new File(pack, "mappings.txt").toPath()), StandardCharsets.UTF_8);
            File directory = new File(pack, "gui/custom/materials");
            assertEquals(packName + " material PNG count", SLUGS.length * 2,
                    directory.listFiles((dir, name) -> name.endsWith(".png")).length);
            for (String slug : SLUGS) {
                File baseFile = new File(directory, slug + ".png");
                File maskFile = new File(directory, slug + "-mask.png");
                assertTrue(baseFile.toString(), baseFile.isFile());
                assertTrue(maskFile.toString(), maskFile.isFile());
                BufferedImage base = ImageIO.read(baseFile);
                BufferedImage mask = ImageIO.read(maskFile);
                assertNotNull(base);
                assertNotNull(mask);
                assertEquals(32, base.getWidth());
                assertEquals(32, base.getHeight());
                assertEquals(base.getWidth(), mask.getWidth());
                assertEquals(base.getHeight(), mask.getHeight());
                assertTrue(slug + " mask is empty", selected(mask) > 0);
                assertTrue(mappings.contains("img.wurmhighres.material."
                        + slug.replace('-', '.') + " = "));
                assertTrue(mappings.contains("img.wurmhighres.material."
                        + slug.replace('-', '.') + ".mask = "));
            }
        }
    }

    @Test
    public void compoundMasksLeaveHandlesHoopsAndSupportsUntinted() throws Exception {
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        assertPartial(directory, "hatchet");
        assertPartial(directory, "hammer");
        assertPartial(directory, "pickaxe");
        assertPartial(directory, "metal-brush");
        assertPartial(directory, "small-barrel");
        assertPartial(directory, "large-anvil");

        BufferedImage base = ImageIO.read(new File(directory, "hatchet.png"));
        BufferedImage mask = ImageIO.read(new File(directory, "hatchet-mask.png"));
        BufferedImage copper = ShieldIcons.tint(base, mask, 0.72f, 0.45f, 0.218f);
        int changed = 0;
        int preserved = 0;
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                if (((base.getRGB(x, y) >>> 24) & 255) == 0) {
                    continue;
                }
                int amount = (mask.getRGB(x, y) >>> 16) & 255;
                if (amount > 200 && base.getRGB(x, y) != copper.getRGB(x, y)) {
                    changed++;
                }
                if (amount == 0 && base.getRGB(x, y) == copper.getRGB(x, y)) {
                    preserved++;
                }
            }
        }
        assertTrue("hatchet blade did not recolour", changed > 20);
        assertTrue("hatchet handle was not preserved", preserved > 20);
    }

    private static void assertPartial(File directory, String slug) throws Exception {
        BufferedImage base = ImageIO.read(new File(directory, slug + ".png"));
        BufferedImage mask = ImageIO.read(new File(directory, slug + "-mask.png"));
        assertTrue(slug + " has no selected material pixels", selected(mask) > 20);
        assertTrue(slug + " mask incorrectly covers the whole icon",
                selected(mask) < opaque(base));
    }

    private static int selected(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (((image.getRGB(x, y) >>> 16) & 255) > 16) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int opaque(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (((image.getRGB(x, y) >>> 24) & 255) > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static File projectDirectory() {
        return new File(System.getProperty("wurmHighresProjectDir"));
    }
}
