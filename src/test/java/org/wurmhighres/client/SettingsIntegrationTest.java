package org.wurmhighres.client;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.chamomilo.wurm.update.ModSetting;
import org.chamomilo.wurm.update.SharedModSettings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class SettingsIntegrationTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void sharedEditorPersistsLiveChangesAndDefersResourcePackUntilRestart() throws Exception {
        String originalUserDirectory = System.getProperty("user.home");
        SharedModSettings.Provider previous = SharedModSettings.provider("wurm-highres");
        Properties defaults = new Properties();
        for (ModSetting spec : ModSettingsSchema.specs()) defaults.setProperty(spec.key, spec.defaultValue);
        try {
            System.setProperty("user.home", temporary.getRoot().getAbsolutePath());
            WurmHighresMod mod = new WurmHighresMod();
            mod.configure(defaults);
            SharedModSettings.Provider editor = SharedModSettings.provider("wurm-highres");
            assertNotNull(editor);
            assertEquals(13, editor.settings().size());
            editor.change("rarityAnimation", "star");
            assertEquals("star", WurmHighresSettings.rarityAnimation);
            editor.change("rareColor", "#336699");
            assertArrayEquals(new float[]{0x33/255f, 0x66/255f, 0x99/255f},
                    WurmHighresSettings.colorFor((byte) 1), 0.0001f);
            String originalPack = WurmHighresSettings.resourcePack;
            editor.change("resourcePack", "mods/wurm-highres/custom.jar");
            editor.change("glowRadius", "4");
            assertEquals(4, WurmHighresSettings.glowRadius);
            assertEquals(originalPack, WurmHighresSettings.resourcePack);
            try {
                editor.change("glowRadius", "99");
                fail("The editor must reject invalid values before changing runtime settings.");
            } catch (IllegalArgumentException expected) { }
            assertEquals(4, WurmHighresSettings.glowRadius);
            Path saved = temporary.getRoot().toPath().resolve(".chamomilo/mod-settings/wurm-highres.properties");
            Properties values = new Properties();
            try (InputStream input = Files.newInputStream(saved)) { values.load(input); }
            assertEquals(4, values.size());
            assertEquals("star", values.getProperty("rarityAnimation"));
            assertFalse(values.containsKey("replaceIcons"));
            mod.configure(defaults);
            assertEquals("mods/wurm-highres/custom.jar", WurmHighresSettings.resourcePack);
            assertEquals("star", WurmHighresSettings.rarityAnimation);
            assertEquals(4, WurmHighresSettings.glowRadius);
        } finally {
            System.setProperty("user.home", originalUserDirectory);
            WurmHighresSettings.configure(defaults);
            if (previous != null) SharedModSettings.register("wurm-highres", previous);
        }
    }
}
