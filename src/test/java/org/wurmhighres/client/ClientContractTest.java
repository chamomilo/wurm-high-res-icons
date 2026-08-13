package org.wurmhighres.client;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import org.junit.Test;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClientContractTest {
    @Test
    public void modDeclaresLifecycleMarkerInterfaces() {
        assertTrue(PreInitable.class.isAssignableFrom(WurmHighresMod.class));
        assertTrue(Initable.class.isAssignableFrom(WurmHighresMod.class));
    }

    @Test
    public void targetClientMethodsAndFieldsExist() throws Exception {
        ClassPool pool = clientPool();

        CtClass iconLoader = pool.get("com.wurmonline.client.resources.textures.IconLoader");
        assertNotNull(iconLoader.getDeclaredMethod("initIcons"));

        CtClass resourceTextureLoader = pool.get(
                "com.wurmonline.client.resources.textures.ResourceTextureLoader");
        assertNotNull(resourceTextureLoader.getDeclaredMethod(
                "getNearestTextureNonScaling"));

        CtClass treePanel = pool.get("com.wurmonline.client.renderer.gui.WurmTreeList$TreeListPanel");
        assertNotNull(treePanel.getMethod("renderComponent",
                "(Lcom/wurmonline/client/renderer/backend/Queue;F)V"));

        CtClass inventoryItem = pool.get(
                "com.wurmonline.client.renderer.gui.InventoryListComponent$InventoryTreeListItem");
        assertNotNull(inventoryItem.getDeclaredMethod("getIcon"));

        CtClass toolbelt = pool.get("com.wurmonline.client.renderer.gui.ToolBeltComponent");
        assertNotNull(toolbelt.getMethod("renderComponent",
                "(Lcom/wurmonline/client/renderer/backend/Queue;F)V"));

        CtClass paperDoll = pool.get("com.wurmonline.client.renderer.gui.PaperDollItem");
        assertNotNull(paperDoll.getMethod("render",
                "(Lcom/wurmonline/client/renderer/backend/Queue;FFFF)V"));

        CtClass creationFrame = pool.get("com.wurmonline.client.renderer.gui.CreationFrame");
        assertNotNull(creationFrame.getMethod("setTexture", "(S)V"));
        assertNotNull(creationFrame.getMethod("setTexture",
                "(Lcom/wurmonline/client/game/inventory/InventoryMetaItem;)V"));
        assertNotNull(pool.get("com.wurmonline.client.renderer.gui.CreationListItem")
                .getDeclaredMethod("getIcon"));
        assertNotNull(pool.get("com.wurmonline.client.renderer.gui.CreationItemTreeLisItem")
                .getDeclaredMethod("getIcon"));

        assertNotNull(pool.get("com.wurmonline.client.renderer.cell.CellRenderable")
                .getDeclaredMethod("getWorld"));
        assertNotNull(pool.get("com.wurmonline.client.game.World")
                .getDeclaredMethod("getClient"));
        assertNotNull(pool.get("com.wurmonline.client.WurmClientBase")
                .getDeclaredMethod("getConnectionListener"));
        assertNotNull(pool.get("com.wurmonline.client.comm.ServerConnectionListenerClass")
                .getDeclaredMethod("findGroundItem"));
        assertNotNull(pool.get("com.wurmonline.client.renderer.cell.GroundItemCellRenderable")
                .getDeclaredField("item"));
        CtClass groundItemData = pool.get("com.wurmonline.client.renderer.GroundItemData");
        assertNotNull(groundItemData.getDeclaredMethod("getR"));
        assertNotNull(groundItemData.getDeclaredMethod("getG"));
        assertNotNull(groundItemData.getDeclaredMethod("getB"));
    }

    @Test
    public void allJavassistHookBodiesCompileAgainstPinnedClient() throws Exception {
        ClassPool pool = clientPool();
        invokeInstaller("installResourcePackHook", pool);
        invokeInstaller("installCustomIconHooks", pool);
        invokeInstaller("installItemIconHooks", pool);
        invokeInstaller("installInventoryHooks", pool);
        invokeInstaller("installToolbeltHooks", pool);
        invokeInstaller("installPaperDollHook", pool);
    }

    private static ClassPool clientPool() throws Exception {
        String libraryDirectory = System.getProperty("wurmClientLibDir");
        ClassPool pool = new ClassPool(false);
        pool.appendSystemPath();
        pool.insertClassPath(new LoaderClassPath(ClientContractTest.class.getClassLoader()));
        pool.appendClassPath(new File(libraryDirectory, "client-patched.jar").getAbsolutePath());
        pool.appendClassPath(new File(libraryDirectory, "common.jar").getAbsolutePath());
        return pool;
    }

    private static void invokeInstaller(String name, ClassPool pool) throws Exception {
        Method method = WurmHighresMod.class.getDeclaredMethod(name, ClassPool.class);
        method.setAccessible(true);
        method.invoke(null, pool);
    }
}
