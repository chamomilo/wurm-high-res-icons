package org.wurmhighres.client;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WurmHighresMod implements WurmClientMod, Configurable, PreInitable, Initable {
    private static final Logger LOGGER = Logger.getLogger(WurmHighresMod.class.getName());

    @Override
    public void configure(Properties properties) {
        WurmHighresSettings.configure(properties);
    }

    @Override
    public void preInit() {
        try {
            ClassPool pool = HookManager.getInstance().getClassPool();
            if (WurmHighresSettings.replaceIcons) {
                installResourcePackHook(pool);
                installCustomIconHooks(pool);
            }
            if (WurmHighresSettings.replaceIcons
                    || WurmHighresSettings.rarityGlow
                    || WurmHighresSettings.rarityBackground) {
                installItemIconHooks(pool);
            }
            if (WurmHighresSettings.rarityGlow || WurmHighresSettings.rarityBackground) {
                installInventoryHooks(pool);
                installToolbeltHooks(pool);
            }
            if (WurmHighresSettings.rarityGlow
                    || WurmHighresSettings.rarityBackground
                    || (WurmHighresSettings.replaceIcons && WurmHighresSettings.magicShimmer)) {
                installPaperDollHook(pool);
            }
            LOGGER.info("wurm-highres bytecode hooks installed");
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Unable to install wurm-highres hooks", exception);
            throw new RuntimeException("Unable to install wurm-highres hooks", exception);
        }
    }

    @Override
    public void init() {
        LOGGER.info("wurm-highres 0.1.26 initialized; iconStyle="
                + WurmHighresSettings.iconStyle);
    }

    private static void installResourcePackHook(ClassPool pool) throws Exception {
        CtClass iconLoader = pool.get("com.wurmonline.client.resources.textures.IconLoader");
        iconLoader.getDeclaredMethod("initIcons").insertBefore(
                "org.wurmhighres.client.WurmHighresResources.ensurePackLoaded();");
    }

    private static void installCustomIconHooks(ClassPool pool) throws Exception {
        CtClass containerItem = pool.get(
                "com.wurmonline.client.renderer.gui.InventoryContainerWindow$InventoryContainerItem");
        containerItem.getDeclaredMethod("getIcon").insertAfter(
                "{ $_ = org.wurmhighres.client.IconOverrides.override(this.getItem(), $_); }");

        CtClass paperDoll = pool.get("com.wurmonline.client.renderer.gui.PaperDollItem");
        CtField texture = paperDoll.getDeclaredField("texture");
        texture.setModifiers(texture.getModifiers() & ~Modifier.FINAL);
        for (CtConstructor constructor : paperDoll.getDeclaredConstructors()) {
            constructor.insertAfter(
                    "{ this.texture = org.wurmhighres.client.IconOverrides.override(this.item, this.texture); }");
        }

        CtClass creationFrame = pool.get("com.wurmonline.client.renderer.gui.CreationFrame");
        method(creationFrame, "setTexture", "(S)V")
                .insertAfter("{ if (this.groundCreationItem != null) { this.itemTexture = "
                        + "org.wurmhighres.client.IconOverrides.overrideGround("
                        + "$1, this.groundCreationItem.getId(), "
                        + "this.groundCreationItem.getName(), this.itemTexture); } }");
        method(creationFrame, "setTexture",
                "(Lcom/wurmonline/client/game/inventory/InventoryMetaItem;)V")
                .insertAfter("{ this.itemTexture = "
                        + "org.wurmhighres.client.IconOverrides.override($1, this.itemTexture); }");

        CtClass creationListItem = pool.get(
                "com.wurmonline.client.renderer.gui.CreationListItem");
        creationListItem.getDeclaredMethod("getIcon").insertAfter(
                "{ $_ = org.wurmhighres.client.IconOverrides.overrideByName("
                        + "this.getName(), $_); }");

        CtClass creationTreeItem = pool.get(
                "com.wurmonline.client.renderer.gui.CreationItemTreeLisItem");
        creationTreeItem.getDeclaredMethod("getIcon").insertAfter(
                "{ $_ = org.wurmhighres.client.IconOverrides.overrideByName("
                        + "this.getName(), $_); }");
    }

    private static void installItemIconHooks(ClassPool pool) throws Exception {
        CtClass inventoryItem = pool.get(
                "com.wurmonline.client.renderer.gui.InventoryListComponent$InventoryTreeListItem");
        inventoryItem.getDeclaredMethod("getIcon").insertAfter(
                "{ $_ = org.wurmhighres.client.IconOverrides.override(this.item, $_); "
                        + "org.wurmhighres.client.RarityEffects.renderInventoryIcon(this, $_); }");

        CtClass toolbeltItem = pool.get(
                "com.wurmonline.client.renderer.gui.ToolBeltComponent$ToolBeltItem");
        toolbeltItem.getDeclaredMethod("getIcon").insertAfter(
                "{ $_ = org.wurmhighres.client.IconOverrides.override(this.getItem(), $_); "
                        + "org.wurmhighres.client.RarityEffects.renderToolbeltIcon(this, $_); }");
    }

    private static void installInventoryHooks(ClassPool pool) throws Exception {
        CtClass panel = pool.get("com.wurmonline.client.renderer.gui.WurmTreeList$TreeListPanel");
        CtMethod render = method(panel, "renderComponent",
                "(Lcom/wurmonline/client/renderer/backend/Queue;F)V");
        render.insertBefore("org.wurmhighres.client.RarityEffects.beginInventoryRender(this, $1);");
        render.insertAfter("org.wurmhighres.client.RarityEffects.endInventoryRender();", true);

    }

    private static void installToolbeltHooks(ClassPool pool) throws Exception {
        CtClass toolbelt = pool.get("com.wurmonline.client.renderer.gui.ToolBeltComponent");
        CtMethod render = method(toolbelt, "renderComponent",
                "(Lcom/wurmonline/client/renderer/backend/Queue;F)V");
        render.insertBefore("org.wurmhighres.client.RarityEffects.beginToolbeltRender(this, $1);");
        render.insertAfter("org.wurmhighres.client.RarityEffects.endToolbeltRender();", true);

    }

    private static void installPaperDollHook(ClassPool pool) throws Exception {
        CtClass paperDoll = pool.get("com.wurmonline.client.renderer.gui.PaperDollItem");
        method(paperDoll, "render", "(Lcom/wurmonline/client/renderer/backend/Queue;FFFF)V")
                .insertBefore("{ org.wurmhighres.client.MagicEffects.animatePaperDoll(this); "
                        + "org.wurmhighres.client.RarityEffects.renderPaperDoll(this, $1); }");
    }

    private static CtMethod method(CtClass type, String name, String descriptor) throws NotFoundException {
        return type.getMethod(name, descriptor);
    }
}
