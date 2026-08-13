package org.wurmhighres.client;

import java.util.Properties;
import java.util.Locale;
import java.util.logging.Logger;

final class WurmHighresSettings {
    private static final Logger LOGGER = Logger.getLogger(WurmHighresSettings.class.getName());
    private static final String DEFAULT_ICON_STYLE = "realistic";
    private static final String DEFAULT_RARITY_ANIMATION = "blob";
    private static final String DEFAULT_READABLE_PACK =
            "mods/wurm-highres/wurm-highres-resources-readable-0.1.24.jar";
    private static final String DEFAULT_REALISTIC_PACK =
            "mods/wurm-highres/wurm-highres-resources-realistic-0.1.24.jar";

    static volatile boolean replaceIcons = true;
    static volatile boolean rarityGlow = true;
    static volatile boolean rarityBackground = false;
    static volatile boolean pulseGlow = true;
    static volatile String rarityAnimation = DEFAULT_RARITY_ANIMATION;
    static volatile int glowRadius = 3;
    static volatile float glowAlpha = 0.48f;
    static volatile float backgroundAlpha = 0.16f;
    static volatile String iconStyle = DEFAULT_ICON_STYLE;
    static volatile String readableResourcePack = DEFAULT_READABLE_PACK;
    static volatile String realisticResourcePack = DEFAULT_REALISTIC_PACK;
    static volatile String resourcePack = DEFAULT_READABLE_PACK;

    private static volatile float[] rareColor = rgb(66, 153, 224);
    private static volatile float[] supremeColor = rgb(0, 255, 255);
    private static volatile float[] fantasticColor = rgb(255, 0, 255);

    private WurmHighresSettings() {
    }

    static void configure(Properties properties) {
        replaceIcons = bool(properties, "replaceIcons", replaceIcons);
        rarityGlow = bool(properties, "rarityGlow", rarityGlow);
        rarityBackground = bool(properties, "rarityBackground", rarityBackground);
        pulseGlow = bool(properties, "pulseGlow", pulseGlow);
        rarityAnimation = rarityAnimation(properties.getProperty(
                "rarityAnimation", DEFAULT_RARITY_ANIMATION));
        glowRadius = integer(properties, "glowRadius", glowRadius, 1, 4);
        glowAlpha = decimal(properties, "glowAlpha", glowAlpha, 0.0f, 1.0f);
        backgroundAlpha = decimal(properties, "backgroundAlpha", backgroundAlpha, 0.0f, 1.0f);
        iconStyle = iconStyle(properties.getProperty("iconStyle", DEFAULT_ICON_STYLE));
        readableResourcePack = string(properties, "readableResourcePack", DEFAULT_READABLE_PACK);
        realisticResourcePack = string(properties, "realisticResourcePack", DEFAULT_REALISTIC_PACK);
        String override = properties.getProperty("resourcePack");
        resourcePack = override == null || override.trim().isEmpty()
                ? selectedResourcePack()
                : override.trim();
        rareColor = color(properties, "rareColor", rareColor);
        supremeColor = color(properties, "supremeColor", supremeColor);
        fantasticColor = color(properties, "fantasticColor", fantasticColor);
    }

    static float[] colorFor(byte rarity) {
        float[] source;
        switch (rarity) {
            case 1:
                source = rareColor;
                break;
            case 2:
                source = supremeColor;
                break;
            case 3:
                source = fantasticColor;
                break;
            default:
                return null;
        }
        return new float[] {source[0], source[1], source[2]};
    }

    static String selectedResourcePack() {
        return "realistic".equals(iconStyle) ? realisticResourcePack : readableResourcePack;
    }

    private static String iconStyle(String value) {
        String normalized = value == null ? DEFAULT_ICON_STYLE : value.trim().toLowerCase(Locale.ROOT);
        if ("readable".equals(normalized) || "realistic".equals(normalized)) {
            return normalized;
        }
        LOGGER.warning("Invalid iconStyle '" + value + "'; using " + DEFAULT_ICON_STYLE);
        return DEFAULT_ICON_STYLE;
    }

    private static String rarityAnimation(String value) {
        String normalized = value == null
                ? DEFAULT_RARITY_ANIMATION
                : value.trim().toLowerCase(Locale.ROOT);
        if ("blob".equals(normalized) || "star".equals(normalized)) {
            return normalized;
        }
        LOGGER.warning("Invalid rarityAnimation '" + value + "'; using "
                + DEFAULT_RARITY_ANIMATION);
        return DEFAULT_RARITY_ANIMATION;
    }

    private static boolean bool(Properties properties, String name, boolean fallback) {
        String value = properties.getProperty(name);
        return value == null ? fallback : Boolean.parseBoolean(value.trim());
    }

    private static int integer(Properties properties, String name, int fallback, int min, int max) {
        try {
            return clamp(Integer.parseInt(properties.getProperty(name, Integer.toString(fallback)).trim()), min, max);
        } catch (RuntimeException exception) {
            LOGGER.warning("Invalid " + name + "; using " + fallback);
            return fallback;
        }
    }

    private static float decimal(Properties properties, String name, float fallback, float min, float max) {
        try {
            return clamp(Float.parseFloat(properties.getProperty(name, Float.toString(fallback)).trim()), min, max);
        } catch (RuntimeException exception) {
            LOGGER.warning("Invalid " + name + "; using " + fallback);
            return fallback;
        }
    }

    private static String string(Properties properties, String name, String fallback) {
        String value = properties.getProperty(name);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static float[] color(Properties properties, String name, float[] fallback) {
        String value = properties.getProperty(name);
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            String normalized = value.trim();
            if (normalized.startsWith("#")) {
                int packed = Integer.parseInt(normalized.substring(1), 16);
                if (normalized.length() != 7) {
                    throw new IllegalArgumentException("expected #RRGGBB");
                }
                return rgb((packed >>> 16) & 255, (packed >>> 8) & 255, packed & 255);
            }
            String[] channels = normalized.split(",");
            if (channels.length != 3) {
                throw new IllegalArgumentException("expected R,G,B");
            }
            return rgb(
                    clamp(Integer.parseInt(channels[0].trim()), 0, 255),
                    clamp(Integer.parseInt(channels[1].trim()), 0, 255),
                    clamp(Integer.parseInt(channels[2].trim()), 0, 255));
        } catch (RuntimeException exception) {
            LOGGER.warning("Invalid " + name + "; keeping previous color");
            return fallback;
        }
    }

    private static float[] rgb(int red, int green, int blue) {
        return new float[] {red / 255.0f, green / 255.0f, blue / 255.0f};
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
