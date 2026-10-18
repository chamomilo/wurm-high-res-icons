package org.wurmhighres.client;

import com.wurmonline.client.game.inventory.InventoryMetaItem;
import java.lang.reflect.Field;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

public class WorkshopIconsTest {
    private static String resource(String slug) { return "img.wurmhighres.workshop."+slug.replace('-','.'); }

    @Test
    public void newToolBlanksDoNotReplaceTheirFinishedToolsOrSharedNeighbours() {
        String[] names={"scissor blade","file blade","trowel blade","sickle blade","scythe blade","awl blade","butchering knife blade"};
        short[] ids={728,729,730,732,733,734,735};
        for(int i=0;i<names.length;i++) {
            assertEquals(resource(names[i].replace(' ','-')),WorkshopIcons.resource(ids[i],"Rare "+names[i]+", gold",null));
            assertTrue(IconOverrides.hasCustomIcon(names[i]));
            assertNull(WorkshopIcons.resource((short)(ids[i]+100),names[i],null));
            assertNull(WorkshopIcons.byName(names[i]+" fragment"));
        }
        assertNull(WorkshopIcons.byName("scissors"));
        assertNull(WorkshopIcons.resource((short)735,"sacrificial knife blade",null));
        assertNull(WorkshopIcons.resource((short)1215,"ballista dart",null));
        assertNull(WorkshopIcons.resource((short)1215,"fragile arrow",null));
        assertNull(WorkshopIcons.resource((short)1216,"battering ram head",null));
    }

    @Test
    public void authoritativeGroundModelsKeepAllThreeAxeHeadsDistinct() {
        String[] models={"model.weapon.hatchet.blade.iron","model.weapon.axe.small.blade.iron","model.weapon.axe.medium.blade.iron"};
        String[] slugs={"hatchet-head","small-axe-head","medium-axe-head"};
        for(int i=0;i<models.length;i++) assertEquals(resource(slugs[i]),WorkshopIcons.resource((short)1206,"renamed",models[i]));
        assertNull(WorkshopIcons.resource((short)1206,"hatchet head","model.weapon.axe.large.blade.iron"));
        assertNull(WorkshopIcons.resource((short)1206,"hatchet head","model.weapon.hatchet.blade.crude.stone"));
        assertNull(WorkshopIcons.byName("axe head"));
    }

    @Test
    public void exactInventoryWeightsDisambiguateBothUnitsWithoutUsingCustomNames() throws Exception {
        Object[][] cases={{1206,"axe head",.5f,"small-axe-head"},{1206,"axe head",1000f,"medium-axe-head"},
            {1212,"maul head",2f,"small-maul-head"},{1212,"maul head",3000f,"medium-maul-head"},
            {1213,"maul",3f,"small-maul"},{1213,"maul",4000f,"medium-maul"}};
        for(Object[] c:cases) {
            InventoryMetaItem item=item((Integer)c[0],(String)c[1],(Float)c[2]);
            set(item,"customName","different medium maul");
            assertEquals(resource((String)c[3]),WorkshopIcons.inventoryResource(item));
            assertEquals("img.wurmhighres.material."+((String)c[3]).replace('-','.'),MaterialIcons.inventoryBaseResource(item));
        }
        assertNull(WorkshopIcons.inventoryResource(item(1213,"maul",3.5f)));
        assertNull(WorkshopIcons.inventoryResource(item(1206,"axe head",.75f)));
        assertNull(WorkshopIcons.inventoryResource(item(1212,"renamed",2f)));
    }

    @Test
    public void maulHeadsUseTheirMatchingModelsForRenamedGroundItems() {
        assertEquals(resource("small-maul-head"),WorkshopIcons.resource((short)1212,"renamed","model.weapon.head.small.maul.iron"));
        assertEquals(resource("medium-maul-head"),WorkshopIcons.resource((short)1212,"renamed","model.weapon.head.medium.maul.steel"));
        assertEquals(resource("small-maul"),WorkshopIcons.resource((short)1213,"renamed","model.weapon.maul.small.iron"));
        assertEquals(resource("medium-maul"),WorkshopIcons.resource((short)1213,"renamed","model.weapon.maul.medium.gold"));
        assertNull(WorkshopIcons.resource((short)1213,"medium maul","model.weapon.maul.large.iron"));
    }

    @Test
    public void bowVariantsRequireAnExplicitMarkerWhenClientMetadataIsIdentical() throws Exception {
        assertEquals(resource("short-bow"),WorkshopIcons.byName("strung short bow"));
        assertEquals(resource("short-bow-unstrung"),WorkshopIcons.byName("Rare unstringed short bow, birchwood"));
        assertEquals(resource("short-bow-unstrung"),WorkshopIcons.resource((short)1214,"renamed","model.bow.short.unfinished.birchwood"));
        assertNull(WorkshopIcons.inventoryResource(item(1214,"short bow",1f)));
        assertNull(WorkshopIcons.resource((short)1214,"short bow","model.bow.short.birchwood"));
        assertNull(WorkshopIcons.byName("short bow"));
    }

    @Test
    public void swordArrowAndSicklePairsStaySeparateAndSupportMaterialNames() {
        for(Object[] c:new Object[][]{{1203,"short sword blade"},{1204,"short sword"},{1215,"war arrow"},{1216,"war arrow head"},{752,"sickle"}}) {
            String name=(String)c[1];assertEquals(resource(name.replace(' ','-')),WorkshopIcons.resource(((Integer)c[0]).shortValue(),"Fantastic "+name+", steel",null));
        }
        assertNull(WorkshopIcons.resource((short)1204,"short sword blade",null));
        assertNull(WorkshopIcons.resource((short)1216,"renamed","model.arrow.head.ballista.iron"));
    }

    @Test
    public void masksPreserveLeatherAndPumpkinWhileTintingTheAdjacentMaterial() throws Exception {
        Object[][] samples={{"small-maul",22,21,8,8},{"medium-maul",23,21,10,9},
            {"short-sword",23,23,10,9},{"sickle",27,23,5,6},
            {"short-bow",20,16,18,9},{"practice-doll",16,4,16,20}};
        File root=new File(System.getProperty("user.dir"),"resource-pack/gui/custom/materials");
        for(Object[] s:samples) {
            String slug=(String)s[0];BufferedImage base=ImageIO.read(new File(root,slug+".png"));
            BufferedImage mask=ImageIO.read(new File(root,slug+"-mask.png"));
            BufferedImage tinted=ShieldIcons.tint(base,mask,.3f,1.2f,.6f);
            int x=(Integer)s[1],y=(Integer)s[2];
            assertTrue(slug,(base.getRGB(x,y)>>>24)>=128);
            assertEquals(slug,0,(mask.getRGB(x,y)>>>16)&255);
            assertEquals(slug,base.getRGB(x,y),tinted.getRGB(x,y));
            x=(Integer)s[3];y=(Integer)s[4];
            assertTrue(slug,(base.getRGB(x,y)>>>24)>=128);
            assertTrue(slug,((mask.getRGB(x,y)>>>16)&255)>200);
            assertTrue(slug,base.getRGB(x,y)!=tinted.getRGB(x,y));
        }
    }

    private static InventoryMetaItem item(int id,String name,float weight) throws Exception {
        Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);
        InventoryMetaItem item=(InventoryMetaItem)((sun.misc.Unsafe)f.get(null)).allocateInstance(InventoryMetaItem.class);
        set(item,"type",(short)id);set(item,"baseName",name);set(item,"displayName",name);set(item,"customName","");
        set(item,"groupName","");set(item,"hoverText","");set(item,"weight",weight);set(item,"materialId",(byte)11);return item;
    }
    private static void set(InventoryMetaItem item,String name,Object value) throws Exception {
        Field f=InventoryMetaItem.class.getDeclaredField(name);f.setAccessible(true);f.set(item,value);
    }
}
