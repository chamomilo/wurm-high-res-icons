package org.wurmhighres.client;

import com.wurmonline.client.renderer.gui.PaperDollItem;
import com.wurmonline.client.resources.textures.ImageTextureLoader;
import com.wurmonline.client.resources.textures.Texture;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Animated prismatic surface treatment for explicitly magical container icons. */
public final class MagicEffects {
    static final int FRAME_COUNT = 36;
    static final long PERIOD_NANOS = 4500000000L;

    private static final double TWO_PI = Math.PI * 2.0;
    private static final Logger LOGGER = Logger.getLogger(MagicEffects.class.getName());
    private static final Map<String, String> IMAGE_PATHS;
    private static final Map<String, Texture[]> TEXTURE_CACHE =
            new ConcurrentHashMap<String, Texture[]>();
    private static final AtomicBoolean FAILURE_REPORTED = new AtomicBoolean();
    private static volatile Field paperDollTextureField;

    static {
        Map<String, String> paths = new HashMap<String, String>();
        paths.put(IconOverrides.MAGIC_WATER_SKIN_RESOURCE,
                "/org/wurmhighres/client/icons/magic-water-skin.png");
        paths.put(IconOverrides.SANTA_SACK_RESOURCE,
                "/org/wurmhighres/client/icons/santa-sack.png");
        IMAGE_PATHS = Collections.unmodifiableMap(paths);
    }

    private MagicEffects() {
    }

    static boolean supports(String resource) {
        return IMAGE_PATHS.containsKey(resource);
    }

    static Texture icon(String resource, Texture fallback) {
        if (!WurmHighresSettings.magicShimmer || !supports(resource)) {
            return fallback;
        }
        try {
            Texture[] frames = TEXTURE_CACHE.get(resource);
            if (frames == null) {
                synchronized (TEXTURE_CACHE) {
                    frames = TEXTURE_CACHE.get(resource);
                    if (frames == null) {
                        frames = loadFrames(resource);
                        TEXTURE_CACHE.put(resource, frames);
                    }
                }
            }
            return frames[frameIndex(System.nanoTime())];
        } catch (RuntimeException exception) {
            reportFailure(exception);
            return fallback;
        } catch (IOException exception) {
            reportFailure(exception);
            return fallback;
        }
    }

    /** Refreshes equipment icons, whose stock texture field is otherwise fixed at construction. */
    public static void animatePaperDoll(PaperDollItem paperDoll) {
        if (!WurmHighresSettings.replaceIcons
                || !WurmHighresSettings.magicShimmer
                || paperDoll == null
                || paperDoll.getItem() == null) {
            return;
        }
        String resource = IconOverrides.magicalContainerResource(
                IconOverrides.itemDescriptor(paperDoll.getItem()));
        if (resource == null) {
            return;
        }
        try {
            Texture texture = icon(resource, paperDoll.getTexture());
            if (texture == null) {
                return;
            }
            Field field = paperDollTextureField;
            if (field == null) {
                synchronized (MagicEffects.class) {
                    field = paperDollTextureField;
                    if (field == null) {
                        field = PaperDollItem.class.getDeclaredField("texture");
                        field.setAccessible(true);
                        paperDollTextureField = field;
                    }
                }
            }
            field.set(paperDoll, texture);
        } catch (ReflectiveOperationException exception) {
            reportFailure(exception);
        } catch (RuntimeException exception) {
            reportFailure(exception);
        }
    }

    static double animationPhase(long nanoTime) {
        long wrapped = nanoTime % PERIOD_NANOS;
        if (wrapped < 0L) {
            wrapped += PERIOD_NANOS;
        }
        return wrapped / (double) PERIOD_NANOS;
    }

    static int frameIndex(long nanoTime) {
        return Math.min(FRAME_COUNT - 1,
                (int) Math.floor(animationPhase(nanoTime) * FRAME_COUNT));
    }

    static BufferedImage createFrame(BufferedImage base, double phase) {
        int width = base.getWidth();
        int height = base.getHeight();
        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        double wrappedPhase = phase - Math.floor(phase);
        float[] hsb = new float[3];

        for (int y = 0; y < height; y++) {
            double ny = height <= 1 ? 0.0 : y / (double) (height - 1);
            for (int x = 0; x < width; x++) {
                int packed = base.getRGB(x, y);
                int alpha = (packed >>> 24) & 255;
                if (alpha == 0) {
                    continue;
                }

                int red = (packed >>> 16) & 255;
                int green = (packed >>> 8) & 255;
                int blue = packed & 255;
                double nx = width <= 1 ? 0.0 : x / (double) (width - 1);
                Color.RGBtoHSB(red, green, blue, hsb);

                double diagonal = 0.68 * nx + 0.32 * (1.0 - ny);
                double hue = wrap(wrappedPhase + diagonal * 0.72
                        + 0.035 * Math.sin(TWO_PI * (nx - ny + wrappedPhase)));
                int spectrum = Color.HSBtoRGB((float) hue, 0.86f,
                        Math.min(1.0f, 0.72f + hsb[2] * 0.38f));

                double rainbowWave = 0.5 + 0.5 * Math.sin(
                        TWO_PI * (diagonal * 1.35 - wrappedPhase));
                double colourMix = 0.20 + 0.30 * rainbowWave;
                int outRed = mix(red, (spectrum >>> 16) & 255, colourMix);
                int outGreen = mix(green, (spectrum >>> 8) & 255, colourMix);
                int outBlue = mix(blue, spectrum & 255, colourMix);

                double sweepDistance = circularDistance(wrap(diagonal), wrappedPhase);
                double sunlight = Math.exp(-(sweepDistance * sweepDistance)
                        / (2.0 * 0.052 * 0.052));
                double luminance = (red * 0.2126 + green * 0.7152 + blue * 0.0722) / 255.0;
                double facetPulse = Math.pow(Math.max(0.0, Math.cos(TWO_PI
                        * (wrappedPhase * 1.7 + nx * 0.83 - ny * 0.47))), 12.0)
                        * (0.25 + 0.75 * luminance);
                double whiteMix = Math.min(0.62, sunlight * 0.46 + facetPulse * 0.18);
                outRed = mix(outRed, 255, whiteMix);
                outGreen = mix(outGreen, 255, whiteMix);
                outBlue = mix(outBlue, 255, whiteMix);

                frame.setRGB(x, y, (alpha << 24) | (outRed << 16) | (outGreen << 8) | outBlue);
            }
        }
        return frame;
    }

    private static Texture[] loadFrames(String resource) throws IOException {
        String path = IMAGE_PATHS.get(resource);
        InputStream input = MagicEffects.class.getResourceAsStream(path);
        if (input == null) {
            throw new IOException("Missing magical icon " + path);
        }
        try {
            BufferedImage base = ImageIO.read(input);
            if (base == null) {
                throw new IOException("Unsupported magical icon " + path);
            }
            Texture[] frames = new Texture[FRAME_COUNT];
            for (int index = 0; index < FRAME_COUNT; index++) {
                frames[index] = ImageTextureLoader.loadNowrapNearestTexture(
                        createFrame(base, index / (double) FRAME_COUNT), false);
                if (frames[index] == null) {
                    throw new IOException("Unable to upload magical icon frame " + index);
                }
            }
            return frames;
        } finally {
            input.close();
        }
    }

    private static double wrap(double value) {
        return value - Math.floor(value);
    }

    private static double circularDistance(double first, double second) {
        double distance = Math.abs(first - second);
        return Math.min(distance, 1.0 - distance);
    }

    private static int mix(int first, int second, double amount) {
        return clamp((int) Math.round(first + (second - first) * amount));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static void reportFailure(Exception exception) {
        if (FAILURE_REPORTED.compareAndSet(false, true)) {
            LOGGER.log(Level.WARNING, "Unable to animate magical container icons", exception);
        }
    }
}
