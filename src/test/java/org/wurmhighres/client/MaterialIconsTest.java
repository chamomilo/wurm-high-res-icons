package org.wurmhighres.client;

import com.wurmonline.client.game.inventory.InventoryMetaItem;
import com.wurmonline.shared.util.MaterialUtilities;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MaterialIconsTest {
    @Test
    public void buoyWoodTintKeepsLanternAndHoopsUntouched() throws Exception {
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        BufferedImage base = ImageIO.read(new File(directory, "buoy.png"));
        BufferedImage mask = ImageIO.read(new File(directory, "buoy-mask.png"));
        BufferedImage tinted = ShieldIcons.tint(base, mask, 0.25f, 1.8f, 0.3f);
        for (int y : new int[]{6,12,16,22,28}) {
            assertEquals("Buoy lantern/hoop selected at row " + y, 0, (mask.getRGB(16,y) >>> 16) & 255);
            assertEquals("Buoy lantern/hoop tinted at row " + y, base.getRGB(16,y), tinted.getRGB(16,y));
        }
        for (int y : new int[]{19,25}) {
            assertTrue("Buoy wood did not change", base.getRGB(16,y) != tinted.getRGB(16,y));
            assertEquals(base.getRGB(16,y) >>> 24, tinted.getRGB(16,y) >>> 24);
        }
    }

    private static final String[] SLUGS = {
            "bowl", "frying-pan", "sauce-pan", "cauldron",
            "buoy",
            "chest", "tackle-box", "compartment",
            "candelabra", "oil-lamp", "lamp", "hanging-lamp", "torch-lamp", "imperial-street-lamp",
            "praying-statuette", "chain", "butchering-knife", "fork", "spoon",
            "awl", "clay-shaper", "metal-brush", "grooming-brush", "knife",
            "stone-chisel", "crowbar", "mallet", "hammer", "pickaxe", "rake",
            "shovel", "saw", "scissors", "file", "trowel", "sickle", "scythe", "pliers",
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
        assertTrue(MaterialIcons.recognizes((short) 753, "scythe, gold"));
        assertFalse(MaterialIcons.recognizes((short) 753, "scythe blade, gold"));
        assertFalse(MaterialIcons.recognizes((short) 733, "scythe blade, gold"));
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
    public void materialSpecificServerToolsUseTheSameNeutralArtwork() throws Exception {
        for (String name : new String[]{"pickaxe", "seryll pickaxe", "pickaxe, seryll", "pick axe",
                "Rare pickaxe, seryll", "Supreme seryll pickaxe", "Fantastic pickaxe, seryll"}) {
            assertEquals("img.wurmhighres.material.pickaxe",
                    MaterialIcons.inventoryBaseResource(item(1600, name, 67)));
        }
        assertEquals("img.wurmhighres.material.shovel",
                MaterialIcons.inventoryBaseResource(item(1601, "shovel, seryll", 67)));
        assertEquals("img.wurmhighres.material.pickaxe",
                MaterialIcons.inventoryBaseResource(item(743, "pickaxe", 67)));
        assertNull(MaterialIcons.inventoryBaseResource(item(1600, "pickaxe head", 67)));
        assertNull(MaterialIcons.inventoryBaseResource(item(1600, "pickaxe fragment", 67)));
        assertNull(MaterialIcons.inventoryBaseResource(item(1600, "a statue of a pickaxe", 67)));
        assertNull(MaterialIcons.inventoryBaseResource(item(1600, "pickaxe", 38)));
        // Recipe rows still use their exact atlas IDs to protect shared cells.
        assertFalse(MaterialIcons.recognizes((short) 1600, "seryll pickaxe"));
    }

    @Test
    public void everySeryllArtworkResolvesForAllRaritiesAndServerIconIds() throws Exception {
        Object[][] tools = {
                {764, "bowl", "bowl"}, {784, "frying pan", "frying-pan"},
                {784, "sauce pan", "sauce-pan"}, {804, "cauldron", "cauldron"},
                {251, "candelabra", "candelabra"},
                {823, "oil lamp", "oil-lamp"}, {825, "lamp", "lamp"},
                {825, "hanging lamp", "hanging-lamp"}, {825, "torch lamp", "torch-lamp"},
                {825, "imperial street lamp", "imperial-street-lamp"},
                {282, "statuette", "praying-statuette"}, {520, "chain", "chain"},
                {755, "butchering knife", "butchering-knife"}, {767, "fork", "fork"},
                {768, "spoon", "spoon"}, {754, "awl", "awl"},
                {882, "metal brush", "metal-brush"}, {940, "leather knife", "knife"},
                {1201, "stone chisel", "stone-chisel"}, {738, "crowbar", "crowbar"},
                {742, "hammer", "hammer"}, {743, "pickaxe", "pickaxe"},
                {745, "rake", "rake"}, {746, "shovel", "shovel"}, {747, "saw", "saw"},
                {748, "scissors", "scissors"}, {749, "file", "file"},
                {750, "trowel", "trowel"}, {752, "sickle", "sickle"},
                {753, "scythe", "scythe"}, {780, "pliers", "pliers"},
                {791, "small anvil", "small-anvil"}, {783, "steel and flint", "steel-and-flint"},
                {736, "bee smoker", "bee-smoker"}, {788, "needle", "needle"},
                {792, "compass", "compass"}, {822, "lantern", "lantern"},
                {860, "spyglass", "spyglass"}, {1207, "small axe", "hatchet"},
                {780, "branding iron", "branding-iron"}, {511, "smelting pot", "smelting-pot"},
                {791, "large anvil", "large-anvil"}, {282, "prayer charm", "prayer-charm"},
                {1201, "carving knife", "carving-knife"}
        };
        String[] rarities = {"", "Rare ", "Supreme ", "Fantastic "};
        for (Object[] tool : tools) {
            String name = (String) tool[1];
            String expected = "img.wurmhighres.material." + ((String) tool[2]).replace('-', '.');
            for (int iconId : new int[]{(Integer) tool[0], 1600}) {
                for (int rarity = 0; rarity < rarities.length; rarity++) {
                    for (String materialName : new String[]{name, "seryll " + name, name + ", seryll"}) {
                        InventoryMetaItem item = item(iconId, rarities[rarity] + materialName, 67);
                        set(item, "rarity", (byte) rarity);
                        // Display text, a player name and item RGB must not choose
                        // a different material/artwork for an actual seryll item.
                        set(item, "hoverText", "adamantine pickaxe stone chisel");
                        set(item, "customName", "gold hammer");
                        set(item, "r", 0.26f);
                        set(item, "g", 0.60f);
                        set(item, "b", 0.88f);
                        assertEquals(name + " / " + rarity + " / " + iconId,
                                expected, MaterialIcons.inventoryBaseResource(item));
                    }
                }
            }
        }
        assertNull(MaterialIcons.inventoryBaseResource(item(1600, "carving knife blade", 67)));
        assertNull(MaterialIcons.inventoryBaseResource(item(1600, "Rare branding iron fragment", 67)));
    }

    @Test
    public void seryllCoversMetalAtBothEndsOfEveryCompoundTool() throws Exception {
        // Independently chosen artwork landmarks: slug, two metal points and a
        // point in another material. Catch clipped heads/sockets and tinted glass.
        Object[][] tools = {
                {"butchering-knife", 23, 10, 17, 18, 7, 20},
                {"awl", 24, 7, 17, 16, 9, 23},
                {"metal-brush", 20, 6, 22, 6, 9, 21},
                {"stone-chisel", 24, 11, 16, 16, 9, 19},
                {"hammer", 10, 6, 24, 16, 7, 24},
                {"pickaxe", 5, 1, 28, 16, 6, 23},
                {"rake", 5, 5, 31, 20, 8, 20},
                {"shovel", 6, 16, 22, 21, 9, 27},
                {"saw", 26, 6, 15, 19, 7, 25},
                {"file", 25, 6, 13, 17, 8, 24},
                {"trowel", 25, 4, 16, 18, 7, 25},
                {"sickle", 9, 7, 30, 17, 3, 20},
                {"scythe", 19, 2, 28, 7, 12, 16},
                {"hatchet", 12, 5, 25, 19, 8, 19},
                {"carving-knife", 27, 6, 23, 11, 13, 20},
                {"steel-and-flint", 24, 8, 18, 22, 9, 7},
                {"bee-smoker", 24, 4, 20, 25, 10, 17},
                {"compass", 3, 17, 27, 17, 13, 14},
                {"lantern", 12, 2, 20, 20, 16, 18},
                {"spyglass", 10, 9, 17, 16, 25, 23},
                {"large-anvil", 8, 5, 17, 13, 17, 23}
        };
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        float[] colour = ShieldIcons.fallbackColour(MaterialUtilities.getMaterialString((byte) 67));
        assertNotNull(colour);
        assertTrue("seryll unexpectedly became blue", colour[0] > colour[1] && colour[1] > colour[2]);
        for (Object[] tool : tools) {
            String slug = (String) tool[0];
            BufferedImage base = ImageIO.read(new File(directory, slug + ".png"));
            BufferedImage mask = ImageIO.read(new File(directory, slug + "-mask.png"));
            BufferedImage tinted = ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]);
            for (int index = 1; index <= 3; index += 2) {
                int x = (Integer) tool[index];
                int y = (Integer) tool[index + 1];
                int pixel = tinted.getRGB(x, y);
                assertTrue(slug + " missing metal at " + x + "," + y,
                        ((base.getRGB(x, y) >>> 24) & 255) >= 128
                                && ((mask.getRGB(x, y) >>> 16) & 255) > 200);
                int red = (pixel >>> 16) & 255;
                int green = (pixel >>> 8) & 255;
                int blue = pixel & 255;
                assertTrue(slug + " metal has the wrong colour", red > green && green > blue);
            }
            int x = (Integer) tool[5];
            int y = (Integer) tool[6];
            assertTrue(slug + " non-metal landmark is transparent", ((base.getRGB(x, y) >>> 24) & 255) >= 128);
            assertEquals(slug + " other material got tinted", base.getRGB(x, y), tinted.getRGB(x, y));
        }
    }

    @Test
    public void entireMetalToolsKeepSeryllColourThroughTransparentEdges() throws Exception {
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        float[] colour = ShieldIcons.fallbackColour(MaterialUtilities.getMaterialString((byte) 67));
        for (String slug : new String[]{"praying-statuette", "chain", "fork", "spoon", "knife",
                "crowbar", "scissors", "pliers", "small-anvil", "needle", "branding-iron",
                "smelting-pot", "prayer-charm", "bowl", "frying-pan", "sauce-pan", "cauldron"}) {
            BufferedImage base = ImageIO.read(new File(directory, slug + ".png"));
            BufferedImage mask = ImageIO.read(new File(directory, slug + "-mask.png"));
            BufferedImage tinted = ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]);
            int edges = 0;
            for (int y = 0; y < 32; y++) {
                for (int x = 0; x < 32; x++) {
                    int pixel = base.getRGB(x, y);
                    int alpha = (pixel >>> 24) & 255;
                    assertEquals(slug + " opacity changed", alpha, (tinted.getRGB(x, y) >>> 24) & 255);
                    if (alpha == 0) continue;
                    assertEquals(slug + " uncoloured metal edge", 255, (mask.getRGB(x, y) >>> 16) & 255);
                    assertEquals(slug + " non-neutral metal", (pixel >>> 16) & 255, pixel & 255);
                    if (alpha < 255) edges++;
                }
            }
            assertTrue(slug + " has no antialiased edges", edges > 8);
        }
    }

    @Test
    public void candelabraRecoloursCupStemAndScrollsWithoutRecolouringWax() throws Exception {
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        BufferedImage base = ImageIO.read(new File(directory, "candelabra.png"));
        BufferedImage mask = ImageIO.read(new File(directory, "candelabra-mask.png"));
        // The mask check needs the game's seryll colour, without starting the
        // client's XML/options stack in this headless artwork test.
        float[] colour = ShieldIcons.fallbackColour(MaterialUtilities.getMaterialString((byte) 67));
        BufferedImage tinted = ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]);
        for (int[] metal : new int[][]{{15, 5}, {9, 8}, {15, 18}}) {
            int x = metal[0], y = metal[1];
            assertTrue(((base.getRGB(x, y) >>> 24) & 255) >= 80);
            assertEquals(255, (mask.getRGB(x, y) >>> 16) & 255);
            int pixel = tinted.getRGB(x, y);
            assertTrue(((pixel >>> 16) & 255) > ((pixel >>> 8) & 255));
            assertEquals(base.getRGB(x, y) >>> 24, pixel >>> 24);
        }
        for (int[] wax : new int[][]{{15, 2}, {11, 5}, {20, 5}}) {
            int x = wax[0], y = wax[1];
            assertEquals(0, (mask.getRGB(x, y) >>> 16) & 255);
            assertEquals(base.getRGB(x, y), tinted.getRGB(x, y));
        }
    }

    @Test
    public void sharedLampCellChoosesFourDistinctAssembliesAndKeepsHeadsSeparate() {
        String[][] cases = {
                {"lamp", "lamp"}, {"Rare lamp, iron", "lamp"},
                {"hanging lamp", "hanging-lamp"}, {"Fantastic hanging lamp, gold", "hanging-lamp"},
                {"torch lamp", "torch-lamp"}, {"Supreme torch lamp, seryll", "torch-lamp"},
                {"imperial street lamp", "imperial-street-lamp"}, {"imperial lamp, zinc", "imperial-street-lamp"},
                {"renamed model.light.lamp.street.pole.iron.lit", "hanging-lamp"},
                {"renamed model.light.lamp.street.torch", "torch-lamp"},
                {"renamed model.light.lamp.street.metal.iron", "imperial-street-lamp"},
                {"renamed model.light.lamp.street.iron", "lamp"}
        };
        for (String[] value : cases) {
            assertEquals(value[0], "img.wurmhighres.material." + value[1].replace('-', '.'),
                    MaterialIcons.baseResource((short) 825, value[0]));
        }
        assertEquals("img.wurmhighres.material.oil.lamp",
                MaterialIcons.baseResource((short) 823, "oil lamp"));
        assertEquals("img.wurmhighres.material.oil.lamp",
                MaterialIcons.baseResource((short) 823, "model.light.oillamp.brass"));
        for (String name : new String[]{"lamp head", "hanging lamp head", "imperial lamp head",
                "unfinished hanging lamp", "lamp fragment"}) {
            assertNull(name, MaterialIcons.baseResource((short) 825, name));
        }
        assertNull(MaterialIcons.baseResource((short) 822, "hanging lamp"));
        assertNull(MaterialIcons.baseResource((short) 824, "torch lamp"));
    }

    @Test
    public void lampMasksRecolourFramesMountsAndPostsWhileKeepingLightAndWood() throws Exception {
        Object[][] cases = {
                {"oil-lamp", 5, 5, 15, 21, 28, 13},
                {"lamp", 17, 7, 17, 14, 15, 14, 16, 28},
                {"hanging-lamp", 15, 5, 17, 21, 15, 21, 10, 15},
                {"torch-lamp", 12, 15, 22, 20, 10, 7, 22, 12},
                {"imperial-street-lamp", 17, 7, 16, 27, 17, 14}
        };
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        float[] colour = ShieldIcons.fallbackColour("seryll");
        for (Object[] value : cases) {
            String slug = (String) value[0];
            BufferedImage base = ImageIO.read(new File(directory, slug + ".png"));
            BufferedImage mask = ImageIO.read(new File(directory, slug + "-mask.png"));
            BufferedImage tinted = ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]);
            for (int index : new int[]{1, 3}) {
                int x = (Integer) value[index], y = (Integer) value[index + 1];
                assertTrue(slug + " missing metal", ((base.getRGB(x, y) >>> 24) & 255) >= 200);
                assertEquals(slug + " incomplete metal mask", 255, (mask.getRGB(x, y) >>> 16) & 255);
                int pixel = tinted.getRGB(x, y);
                assertTrue(slug + " incorrect seryll", ((pixel >>> 16) & 255) > ((pixel >>> 8) & 255));
                assertEquals(base.getRGB(x, y) >>> 24, pixel >>> 24);
            }
            for (int index = 5; index < value.length; index += 2) {
                int x = (Integer) value[index], y = (Integer) value[index + 1];
                assertEquals(slug + " tinted light or wood", 0, (mask.getRGB(x, y) >>> 16) & 255);
                assertEquals(base.getRGB(x, y), tinted.getRGB(x, y));
            }
        }
    }

    private static InventoryMetaItem item(int iconId, String baseName, int material) throws Exception {
        // The real constructor initializes the entire client options/UI stack.
        // Populate the network metadata fields without opening that stack.
        Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        InventoryMetaItem item = (InventoryMetaItem) ((sun.misc.Unsafe) unsafeField.get(null))
                .allocateInstance(InventoryMetaItem.class);
        set(item, "type", (short) iconId);
        set(item, "baseName", baseName);
        set(item, "displayName", baseName);
        set(item, "hoverText", "");
        set(item, "groupName", "");
        set(item, "customName", "");
        set(item, "materialId", (byte) material);
        set(item, "rarity", (byte) (baseName.startsWith("Rare ") ? 1 : 0));
        return item;
    }

    private static void set(InventoryMetaItem item, String name, Object value) throws Exception {
        Field field = InventoryMetaItem.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(item, value);
    }

    @Test
    public void seryllRecoloursBothArmsOfThePickaxeHeadAndLeavesTheShaftAlone() throws Exception {
        File directory = new File(projectDirectory(), "resource-pack/gui/custom/materials");
        BufferedImage base = ImageIO.read(new File(directory, "pickaxe.png"));
        BufferedImage mask = ImageIO.read(new File(directory, "pickaxe-mask.png"));
        float[] colour = ShieldIcons.fallbackColour("seryll");
        BufferedImage seryll = ShieldIcons.tint(base, mask, colour[0], colour[1], colour[2]);
        int leftHead = 0;
        int rightHead = 0;
        int shaft = 0;
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                int pixel = base.getRGB(x, y);
                if (((pixel >>> 24) & 255) == 0) continue;
                int amount = (mask.getRGB(x, y) >>> 16) & 255;
                int red = (pixel >>> 16) & 255;
                int green = (pixel >>> 8) & 255;
                int blue = pixel & 255;
                if (((pixel >>> 24) & 255) >= 128 && y <= 23 && Math.max(red, Math.max(green, blue))
                        - Math.min(red, Math.min(green, blue)) < 24) {
                    assertTrue("uncoloured metal at " + x + "," + y, amount > 0);
                    if (x < 11) leftHead++; else rightHead++;
                }
                if (y >= 24) {
                    assertEquals(pixel, seryll.getRGB(x, y));
                    shaft++;
                }
            }
        }
        assertTrue("left arm missing", leftHead > 8);
        assertTrue("right arm missing", rightHead > 8);
        assertTrue("shaft missing", shaft > 8);
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
