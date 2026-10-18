package org.wurmhighres.client;

import com.wurmonline.client.game.inventory.InventoryMetaItem;
import com.wurmonline.shared.util.MaterialUtilities;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Tool blanks and training weapons, including shared-icon size variants. */
final class WorkshopIcons {
    private static final short[] IDS = {728,729,730,732,733,734,735,255,881,1200,1203,1204,
            1206,1206,1206,1212,1212,1213,1213,1214,1214,1215,1216,1220,752};
    private static final String[] SLUGS = {"scissor-blade","file-blade","trowel-blade","sickle-blade","scythe-blade","awl-blade",
            "butchering-knife-blade","archery-target","practice-doll","carving-knife-blade","short-sword-blade","short-sword",
            "hatchet-head","small-axe-head","medium-axe-head","small-maul-head","medium-maul-head","small-maul","medium-maul",
            "short-bow","short-bow-unstrung","war-arrow","war-arrow-head","spear-tip","sickle"};
    private static final String[] MODELS = {"model.tool.blade.scissors","model.tool.blade.file","model.tool.blade.trowel",
            "model.weapon.blade.sickle","model.weapon.blade.scythe","model.tool.blade.awl","model.weapon.blade.knife.butchering",
            "model.decoration.archerytarget","model.decoration.practicedoll","model.weapon.blade.knife.carving",
            "model.weapon.blade.swordshort","model.weapon.swordshort","model.weapon.hatchet.blade","model.weapon.axe.small.blade",
            "model.weapon.axe.medium.blade","model.weapon.head.small.maul","model.weapon.head.medium.maul",
            "model.weapon.maul.small","model.weapon.maul.medium","model.bow.short","model.bow.short",
            "model.arrow.war","model.arrow.head.war","model.weapon.blade.spear.long","model.weapon.sickle"};
    private static final String[] MATERIALS = materialNames();
    private WorkshopIcons() { }

    static String resource(short iconId, String name, String descriptor) {
        String model=normalize(descriptor);int space=model.lastIndexOf(' ');
        if(space>=0) model=model.substring(space+1);
        if(!model.isEmpty()) {
            if(model.contains(".crude")) return null;
            // Both bow templates have the same ground model. Only an explicit
            // unstrung marker or a variant name can disambiguate them.
            if(iconId==1214 && matches(model,"model.bow.short")) {
                if(model.contains(".unstrung") || model.contains(".unstringed") || model.contains(".unfinished")) return resource(20);
                return forIcon(iconId,byName(name));
            }
            for(int i=0;i<IDS.length;i++) if(IDS[i]==iconId && matches(model,MODELS[i])) return resource(i);
            return null;
        }
        return forIcon(iconId,byName(name));
    }

    static String inventoryResource(InventoryMetaItem item) {
        if(item==null) return null;
        String exact=resource(item.getType(),item.getBaseName(),null);
        if(exact!=null) return exact;
        String name=bare(item.getBaseName());
        float grams=item.getWeight()<100f ? item.getWeight()*1000f : item.getWeight();
        // Exact template weights only; altered weights and ambiguous recipe
        // labels retain their original icon rather than guessing a size.
        if(item.getType()==1206 && name.equals("axe head")) {
            if(weight(grams,500)) return resource(13);
            if(weight(grams,1000)) return resource(14);
        }
        if(item.getType()==1212 && name.equals("maul head")) {
            if(weight(grams,2000)) return resource(15);
            if(weight(grams,3000)) return resource(16);
        }
        if(item.getType()==1213 && name.equals("maul")) {
            if(weight(grams,3000)) return resource(17);
            if(weight(grams,4000)) return resource(18);
        }
        return null;
    }

    static String byName(String value) {
        String name=bare(value);
        if(name.equals("strung short bow") || name.equals("short bow with string")) return resource(19);
        if(name.equals("unstrung short bow") || name.equals("unstringed short bow") || name.equals("short bow unstrung") || name.equals("short bow without string")) return resource(20);
        for(int i=0;i<SLUGS.length;i++) {
            if(i==19 || i==20) continue;
            if(name.equals(SLUGS[i].replace('-',' '))) return resource(i);
        }
        return null;
    }

    static String resource(int index) { return "img.wurmhighres.workshop."+SLUGS[index].replace('-','.'); }
    private static boolean weight(float value,float expected) { return Math.abs(value-expected)<1f; }
    private static boolean matches(String value,String prefix) { return value.equals(prefix) || value.startsWith(prefix+"."); }
    private static String forIcon(short iconId,String resource) {
        for(int i=0;i<IDS.length;i++) if(IDS[i]==iconId && resource(i).equals(resource)) return resource;
        return null;
    }
    private static String bare(String value) {
        String name=normalize(value);
        for(String rarity:new String[]{"rare ","supreme ","fantastic "}) if(name.startsWith(rarity)) {name=name.substring(rarity.length());break;}
        int quality=name.indexOf(" (");if(quality>=0) name=name.substring(0,quality);
        name=name.replace(',',' ').trim().replaceAll("\\s+"," ");
        for(String material:MATERIALS) {
            if(name.startsWith(material+" ")) {name=name.substring(material.length()+1);break;}
            if(name.endsWith(" "+material)) {name=name.substring(0,name.length()-material.length()-1);break;}
        }
        return name;
    }
    private static String normalize(String value) { return value==null?"":value.trim().toLowerCase(Locale.ENGLISH); }
    private static String[] materialNames() {
        Set<String> names=new LinkedHashSet<String>();
        for(int id=1;id<128;id++) {names.add(normalize(MaterialUtilities.getMaterialString((byte)id)));names.add(normalize(MaterialUtilities.getClientMaterialString((byte)id,true)));}
        names.remove("");return names.toArray(new String[names.size()]);
    }
}
