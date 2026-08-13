package org.wurmhighres.client;

import org.gotti.wurmunlimited.modsupport.packs.ModPacks;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WurmHighresResources {
    private static final Logger LOGGER = Logger.getLogger(WurmHighresResources.class.getName());
    private static boolean attempted;
    private static boolean loaded;

    private WurmHighresResources() {
    }

    public static synchronized void ensurePackLoaded() {
        if (attempted || !WurmHighresSettings.replaceIcons) {
            return;
        }
        attempted = true;

        File pack = new File(WurmHighresSettings.resourcePack);
        if (!pack.isAbsolute()) {
            pack = new File(System.getProperty("user.dir"), WurmHighresSettings.resourcePack);
        }
        pack = pack.getAbsoluteFile();

        if (!pack.isFile()) {
            LOGGER.severe("wurm-highres resource pack is missing: " + pack);
            return;
        }

        try {
            loaded = ModPacks.addPack(
                    pack,
                    ModPacks.Options.PREPEND,
                    ModPacks.Options.NORELOAD,
                    ModPacks.Options.NOMAPS,
                    ModPacks.Options.NOARMOR);
            if (loaded) {
                LOGGER.info("Loaded wurm-highres " + WurmHighresSettings.iconStyle
                        + " resource pack: " + pack);
            } else {
                LOGGER.warning("ModLoader did not add the wurm-highres resource pack: " + pack);
            }
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load wurm-highres resource pack " + pack, exception);
        }
    }

    static synchronized boolean isLoaded() {
        return loaded;
    }
}
