package org.wurmhighres.client;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WurmHighresSettingsTest {
    @Test
    public void parsesHexAndRgbRarityColors() {
        Properties properties = new Properties();
        properties.setProperty("rareColor", "#336699");
        properties.setProperty("supremeColor", "1,2,3");
        WurmHighresSettings.configure(properties);

        assertArrayEquals(new float[] {0x33 / 255f, 0x66 / 255f, 0x99 / 255f},
                WurmHighresSettings.colorFor((byte) 1), 0.0001f);
        assertArrayEquals(new float[] {1 / 255f, 2 / 255f, 3 / 255f},
                WurmHighresSettings.colorFor((byte) 2), 0.0001f);
    }

    @Test
    public void clampsEffectSettings() {
        Properties properties = new Properties();
        properties.setProperty("glowRadius", "99");
        properties.setProperty("glowAlpha", "2.0");
        properties.setProperty("backgroundAlpha", "-1.0");
        WurmHighresSettings.configure(properties);

        assertEquals(4, WurmHighresSettings.glowRadius);
        assertEquals(1.0f, WurmHighresSettings.glowAlpha, 0.0001f);
        assertEquals(0.0f, WurmHighresSettings.backgroundAlpha, 0.0001f);
    }

    @Test
    public void selectsResourcePackFromIconStyle() {
        Properties realistic = new Properties();
        realistic.setProperty("iconStyle", "REALISTIC");
        realistic.setProperty("readableResourcePack", "readable.jar");
        realistic.setProperty("realisticResourcePack", "realistic.jar");
        WurmHighresSettings.configure(realistic);

        assertEquals("realistic", WurmHighresSettings.iconStyle);
        assertEquals("realistic.jar", WurmHighresSettings.resourcePack);

        Properties invalid = new Properties();
        invalid.setProperty("iconStyle", "unknown");
        invalid.setProperty("readableResourcePack", "readable.jar");
        invalid.setProperty("realisticResourcePack", "realistic.jar");
        WurmHighresSettings.configure(invalid);

        assertEquals("realistic", WurmHighresSettings.iconStyle);
        assertEquals("realistic.jar", WurmHighresSettings.resourcePack);
    }

    @Test
    public void selectsRarityAnimationAndRejectsUnknownValues() {
        Properties star = new Properties();
        star.setProperty("rarityAnimation", "STAR");
        WurmHighresSettings.configure(star);
        assertEquals("star", WurmHighresSettings.rarityAnimation);

        Properties invalid = new Properties();
        invalid.setProperty("rarityAnimation", "hexagon");
        WurmHighresSettings.configure(invalid);
        assertEquals("blob", WurmHighresSettings.rarityAnimation);
    }
}
