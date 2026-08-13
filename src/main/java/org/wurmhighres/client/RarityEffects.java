package org.wurmhighres.client;

import com.wurmonline.client.game.inventory.InventoryMetaItem;
import com.wurmonline.client.renderer.backend.Queue;
import com.wurmonline.client.renderer.gui.PaperDollItem;
import com.wurmonline.client.renderer.gui.Renderer;
import com.wurmonline.client.renderer.gui.ToolBeltComponent;
import com.wurmonline.client.resources.textures.ImageTextureLoader;
import com.wurmonline.client.resources.textures.Texture;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RarityEffects {
    private static final double TWO_PI = Math.PI * 2.0;
    private static final long GLOW_PERIOD_NANOS = 1800000000L;
    private static final long SHIMMER_PERIOD_NANOS = 7200000000L;
    private static final long STAR_PERIOD_NANOS = 4000000000L;
    private static final int SHIMMER_FRAME_COUNT = 12;
    private static final int STAR_FRAME_COUNT = 30;
    private static final Logger LOGGER = Logger.getLogger(RarityEffects.class.getName());
    private static final ThreadLocal<RenderContext> INVENTORY_CONTEXT = new ThreadLocal<RenderContext>();
    private static final ThreadLocal<RenderContext> TOOLBELT_CONTEXT = new ThreadLocal<RenderContext>();
    private static final Map<String, Field> FIELDS = new ConcurrentHashMap<String, Field>();
    private static final Map<String, Method> METHODS = new ConcurrentHashMap<String, Method>();
    private static final AtomicBoolean FAILURE_REPORTED = new AtomicBoolean();
    private static volatile Texture whiteTexture;
    private static volatile Texture auraTexture;
    private static volatile Texture[] shimmerTextures;
    private static volatile Texture[] starTextures;

    private RarityEffects() {
    }

    public static void beginInventoryRender(Object panel, Queue queue) {
        INVENTORY_CONTEXT.set(new RenderContext(panel, queue));
    }

    public static void endInventoryRender() {
        INVENTORY_CONTEXT.remove();
    }

    public static void renderInventoryIcon(Object treeItem, Texture icon) {
        RenderContext context = INVENTORY_CONTEXT.get();
        if (context == null || treeItem == null || icon == null) {
            return;
        }
        try {
            InventoryMetaItem metaItem = (InventoryMetaItem) field(treeItem.getClass(), "item").get(treeItem);
            RarityStyle style = style(metaItem);
            if (style == null) {
                return;
            }

            Object panel = context.owner;
            Object tree = field(panel.getClass(), "this$0").get(panel);
            @SuppressWarnings("unchecked")
            List<Object> lines = (List<Object>) field(tree.getClass(), "lines").get(tree);
            int lineHeight = field(tree.getClass(), "lineHeight").getInt(tree);
            int lineIndex = -1;
            int depth = 0;
            for (int index = 0; index < lines.size(); index++) {
                Object node = lines.get(index);
                if (field(node.getClass(), "item").get(node) == treeItem) {
                    lineIndex = index;
                    depth = field(node.getClass(), "depth").getInt(node);
                    break;
                }
            }
            if (lineIndex < 0) {
                return;
            }

            int panelX = integerField(panel, "x");
            int panelY = integerField(panel, "y");
            int panelWidth = integerField(panel, "width");
            float iconX = panelX + depth * 16.0f;
            float iconY = panelY + lineIndex * (float) lineHeight;

            if (WurmHighresSettings.rarityBackground) {
                drawBackground(context.queue, style, panelX + 1, iconY + 1,
                        Math.max(1, panelWidth - 2), Math.max(1, lineHeight - 2));
            }
            if (WurmHighresSettings.rarityGlow) {
                drawGlow(context.queue, icon, style, iconX, iconY, 16.0f, 16.0f);
            }
        } catch (Exception exception) {
            reportFailure("inventory", exception);
        }
    }

    public static void beginToolbeltRender(ToolBeltComponent component, Queue queue) {
        TOOLBELT_CONTEXT.set(new RenderContext(component, queue));
    }

    public static void endToolbeltRender() {
        TOOLBELT_CONTEXT.remove();
    }

    public static void renderToolbeltIcon(ToolBeltComponent.ToolBeltItem beltItem, Texture icon) {
        RenderContext context = TOOLBELT_CONTEXT.get();
        if (context == null || beltItem == null || icon == null) {
            return;
        }
        try {
            RarityStyle style = style(beltItem.getItem());
            if (style == null) {
                return;
            }

            ToolBeltComponent component = (ToolBeltComponent) context.owner;
            Object items = field(component.getClass(), "items").get(component);
            int slot = -1;
            for (int index = 0; index < Array.getLength(items); index++) {
                if (Array.get(items, index) == beltItem) {
                    slot = index;
                    break;
                }
            }
            if (slot < 0) {
                return;
            }

            Object properties = field(component.getClass(), "beltProperties").get(component);
            boolean vertical = field(component.getClass(), "vertical").getBoolean(component);
            int componentX = integerField(component, "x");
            int componentY = integerField(component, "y");
            int iconSize = invokeInt(properties, "getIconSize");
            int startWidth = invokeInt(properties, "getStartWidth");
            int middleWidth = invokeInt(properties, "getMiddleWidth");
            int iconMarginLeft = invokeInt(properties, "getIconMarginLeft");
            int iconMarginTop = invokeInt(properties, "getIconMarginTop");

            float iconX;
            float iconY;
            if (vertical) {
                float verticalOffset = field(properties.getClass(), "iconOffsetVertical").getFloat(properties);
                iconX = componentX + verticalOffset + iconMarginTop;
                iconY = componentY + startWidth + iconMarginLeft + slot * middleWidth;
            } else {
                iconX = componentX + startWidth + iconMarginLeft + slot * middleWidth;
                iconY = componentY + iconMarginTop;
            }

            if (WurmHighresSettings.rarityBackground) {
                drawBackground(context.queue, style, iconX - 2, iconY - 2,
                        iconSize + 4, iconSize + 4);
            }
            if (WurmHighresSettings.rarityGlow) {
                drawGlow(context.queue, icon, style, iconX, iconY, iconSize, iconSize);
            }
        } catch (Exception exception) {
            reportFailure("toolbelt", exception);
        }
    }

    public static void renderPaperDoll(PaperDollItem paperDollItem, Queue queue) {
        if (paperDollItem == null || queue == null || paperDollItem.getTexture() == null) {
            return;
        }
        try {
            RarityStyle style = style(paperDollItem.getItem());
            if (style == null) {
                return;
            }
            float x = paperDollItem.getXPosition();
            float y = paperDollItem.getYPosition();
            int width = integerField(paperDollItem, "width");
            int height = integerField(paperDollItem, "height");
            if (WurmHighresSettings.rarityBackground) {
                drawBackground(queue, style, x - 2, y - 2, width + 4, height + 4);
            }
            if (WurmHighresSettings.rarityGlow) {
                drawGlow(queue, paperDollItem.getTexture(), style, x, y, width, height);
            }
        } catch (Exception exception) {
            reportFailure("equipment", exception);
        }
    }

    private static RarityStyle style(InventoryMetaItem item) {
        if (item == null) {
            return null;
        }
        byte rarity = item.getRarity();
        float[] color = WurmHighresSettings.colorFor(rarity);
        if (color == null) {
            return null;
        }
        float rarityBoost = 1.0f + Math.max(0, rarity - 1) * 0.10f;
        return new RarityStyle(color[0], color[1], color[2], rarityBoost);
    }

    private static void drawBackground(Queue queue, RarityStyle style,
                                       float x, float y, float width, float height) {
        Texture texture = whiteTexture();
        if (texture == null) {
            return;
        }
        Renderer.texturedQuadAlphaBlend(queue, texture,
                style.red, style.green, style.blue,
                clamp(WurmHighresSettings.backgroundAlpha * style.intensity),
                x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    private static void drawGlow(Queue queue, Texture icon, RarityStyle style,
                                 float x, float y, float width, float height) {
        int radius = WurmHighresSettings.glowRadius;
        long now = System.nanoTime();
        if ("star".equals(WurmHighresSettings.rarityAnimation)) {
            drawStar(queue, style, x, y, width, height, radius, now);
            return;
        }
        double phase = WurmHighresSettings.pulseGlow
                ? animationPhase(now)
                : 0.0;
        float diameter = glowDiameter(Math.max(width, height), radius, phase);
        float auraX = x + width * 0.5f - diameter * 0.5f;
        float auraY = y + height * 0.5f - diameter * 0.5f;
        float breath = pulseAtPhase(phase);
        float alpha = clamp(WurmHighresSettings.glowAlpha * style.intensity
                * (0.94f - breath * 0.18f));

        Texture aura = auraTexture();
        if (aura != null) {
            Renderer.texturedQuadAlphaBlend(queue, aura,
                    style.red, style.green, style.blue, alpha,
                    auraX, auraY, diameter, diameter,
                    0.0f, 0.0f, 1.0f, 1.0f);
        }

        double shimmerPhase = shimmerPhase(now);
        float shimmerProgress = WurmHighresSettings.pulseGlow
                ? shimmerProgressAtPhase(shimmerPhase)
                : -1.0f;
        float whiteAmount = WurmHighresSettings.pulseGlow
                ? whiteShimmerAtPhase(shimmerPhase)
                : 0.0f;
        if (whiteAmount > 0.01f) {
            Texture[] frames = shimmerTextures();
            if (frames != null) {
                int frame = Math.min(SHIMMER_FRAME_COUNT - 1,
                        (int) Math.floor(shimmerProgress * SHIMMER_FRAME_COUNT));
                Renderer.texturedQuadAlphaBlend(queue, frames[frame],
                    1.0f, 1.0f, 1.0f,
                    clamp(WurmHighresSettings.glowAlpha * style.intensity * whiteAmount * 1.35f),
                    auraX, auraY, diameter, diameter,
                    0.0f, 0.0f, 1.0f, 1.0f);
            }
        }
    }

    private static void drawStar(Queue queue, RarityStyle style,
                                 float x, float y, float width, float height,
                                 int radius, long now) {
        Texture[] frames = starTextures();
        if (frames == null) {
            return;
        }
        double phase = starPhase(now);
        int frame = starFrameAtPhase(phase);
        float diameter = starDiameter(Math.max(width, height), radius);
        float starX = x + width * 0.5f - diameter * 0.5f;
        float starY = y + height * 0.5f - diameter * 0.5f;
        float alpha = clamp(WurmHighresSettings.glowAlpha * style.intensity * 1.08f);
        Renderer.texturedQuadAlphaBlend(queue, frames[frame],
                style.red, style.green, style.blue, alpha,
                starX, starY, diameter, diameter,
                0.0f, 0.0f, 1.0f, 1.0f);

        if (WurmHighresSettings.pulseGlow) {
            float whiteAmount = whiteShimmerAtPhase(shimmerPhase(now));
            if (whiteAmount > 0.01f) {
                Renderer.texturedQuadAlphaBlend(queue, frames[frame],
                        1.0f, 1.0f, 1.0f,
                        clamp(WurmHighresSettings.glowAlpha * style.intensity
                                * whiteAmount * 0.32f),
                        starX, starY, diameter, diameter,
                        0.0f, 0.0f, 1.0f, 1.0f);
            }
        }
    }

    static BufferedImage createAuraImage(int size) {
        validateTextureSize(size);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        double center = (size - 1) / 2.0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - center;
                double dy = y - center;
                double angle = Math.atan2(dy, dx);
                double radius = Math.sqrt(dx * dx + dy * dy) / center;
                double outer = irregularRadius(angle);
                double edge = smoothStep(clamp01((outer - radius) / 0.095));
                double ringOffset = (radius - (outer - 0.105)) / 0.075;
                double ring = Math.exp(-ringOffset * ringOffset);
                double fill = 0.68 + ring * 0.27;
                int alpha = (int) Math.round(255.0 * clamp01(fill * edge));
                image.setRGB(x, y, (alpha << 24) | 0x00FFFFFF);
            }
        }
        return image;
    }

    static BufferedImage createShimmerImage(int size, double angleCenter) {
        validateTextureSize(size);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        double center = (size - 1) / 2.0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - center;
                double dy = y - center;
                double angle = Math.atan2(dy, dx);
                double radius = Math.sqrt(dx * dx + dy * dy) / center;
                double outer = irregularRadius(angle);
                double edge = smoothStep(clamp01((outer - radius) / 0.085));
                double ringOffset = (radius - (outer - 0.10)) / 0.065;
                double ring = Math.exp(-ringOffset * ringOffset);
                double primaryArc = Math.exp(-Math.pow(angleDistance(angle, angleCenter) / 0.55, 2.0));
                double secondaryArc = Math.exp(-Math.pow(
                        angleDistance(angle, angleCenter + Math.PI) / 0.38, 2.0)) * 0.38;
                int alpha = (int) Math.round(255.0
                        * clamp01(ring * edge * Math.min(1.0, primaryArc + secondaryArc)));
                image.setRGB(x, y, (alpha << 24) | 0x00FFFFFF);
            }
        }
        return image;
    }

    static BufferedImage createStarImage(int size, double rotation) {
        validateTextureSize(size);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            double center = (size - 1) * 0.5;
            double outer = size * 0.46;
            double inner = outer * 0.46;
            double[] xs = new double[10];
            double[] ys = new double[10];
            for (int index = 0; index < 10; index++) {
                double angle = rotation - Math.PI * 0.5 + index * Math.PI / 5.0;
                double radius = (index & 1) == 0 ? outer : inner;
                xs[index] = center + Math.cos(angle) * radius;
                ys[index] = center + Math.sin(angle) * radius;
            }

            for (int index = 0; index < 10; index++) {
                Path2D.Double facet = new Path2D.Double();
                facet.moveTo(center, center);
                facet.lineTo(xs[index], ys[index]);
                int next = (index + 1) % 10;
                facet.lineTo(xs[next], ys[next]);
                facet.closePath();
                int brightness = (index & 1) == 0 ? 245 : 155;
                graphics.setColor(new Color(brightness, brightness, brightness, 242));
                graphics.fill(facet);
            }

            Path2D.Double outline = new Path2D.Double();
            outline.moveTo(xs[0], ys[0]);
            for (int index = 1; index < 10; index++) {
                outline.lineTo(xs[index], ys[index]);
            }
            outline.closePath();
            graphics.setColor(new Color(255, 255, 255, 215));
            graphics.setStroke(new BasicStroke(Math.max(1.0f, size / 72.0f),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.draw(outline);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    static double animationPhase(long nanoTime) {
        return cyclePhase(nanoTime, GLOW_PERIOD_NANOS);
    }

    static double shimmerPhase(long nanoTime) {
        return cyclePhase(nanoTime, SHIMMER_PERIOD_NANOS);
    }

    static double starPhase(long nanoTime) {
        return cyclePhase(nanoTime, STAR_PERIOD_NANOS);
    }

    private static double cyclePhase(long nanoTime, long period) {
        long wrapped = nanoTime % period;
        if (wrapped < 0L) {
            wrapped += period;
        }
        return wrapped / (double) period;
    }

    static float pulseAtPhase(double phase) {
        return (float) (0.5 - 0.5 * Math.cos(TWO_PI * phase));
    }

    static float glowDiameter(float iconSize, int radius, double phase) {
        return iconSize + radius * 2.0f * (1.0f + pulseAtPhase(phase));
    }

    static float starDiameter(float iconSize, int radius) {
        return iconSize + radius * 3.0f;
    }

    static int starFrameAtPhase(double phase) {
        double normalized = phase - Math.floor(phase);
        return Math.min(STAR_FRAME_COUNT - 1,
                (int) Math.floor(normalized * STAR_FRAME_COUNT));
    }

    static float whiteShimmerAtPhase(double phase) {
        float progress = shimmerProgressAtPhase(phase);
        if (progress < 0.0f) {
            return 0.0f;
        }
        double wave = Math.sin(Math.PI * progress);
        return (float) (wave * wave);
    }

    static float shimmerProgressAtPhase(double phase) {
        final double start = 0.70;
        final double end = 0.88;
        if (phase < start || phase > end) {
            return -1.0f;
        }
        return (float) ((phase - start) / (end - start));
    }

    private static Texture auraTexture() {
        Texture current = auraTexture;
        if (current != null) {
            return current;
        }
        synchronized (RarityEffects.class) {
            if (auraTexture == null) {
                try {
                    auraTexture = ImageTextureLoader.loadNowrapNearestTexture(createAuraImage(96), false);
                } catch (RuntimeException exception) {
                    reportFailure("aura texture", exception);
                }
            }
            return auraTexture;
        }
    }

    private static Texture[] shimmerTextures() {
        Texture[] current = shimmerTextures;
        if (current != null) {
            return current;
        }
        synchronized (RarityEffects.class) {
            if (shimmerTextures == null) {
                try {
                    Texture[] frames = new Texture[SHIMMER_FRAME_COUNT];
                    for (int index = 0; index < frames.length; index++) {
                        double angle = TWO_PI * index / frames.length;
                        frames[index] = ImageTextureLoader.loadNowrapNearestTexture(
                                createShimmerImage(96, angle), false);
                    }
                    shimmerTextures = frames;
                } catch (RuntimeException exception) {
                    reportFailure("shimmer textures", exception);
                }
            }
            return shimmerTextures;
        }
    }

    private static Texture[] starTextures() {
        Texture[] current = starTextures;
        if (current != null) {
            return current;
        }
        synchronized (RarityEffects.class) {
            if (starTextures == null) {
                try {
                    Texture[] frames = new Texture[STAR_FRAME_COUNT];
                    for (int index = 0; index < frames.length; index++) {
                        double rotation = TWO_PI * index / frames.length;
                        frames[index] = ImageTextureLoader.loadNowrapNearestTexture(
                                createStarImage(96, rotation), false);
                    }
                    starTextures = frames;
                } catch (RuntimeException exception) {
                    reportFailure("star textures", exception);
                }
            }
            return starTextures;
        }
    }

    private static double irregularRadius(double angle) {
        return 0.82
                + 0.060 * Math.sin(angle * 5.0 + 0.35)
                + 0.035 * Math.sin(angle * 9.0 + 1.70)
                + 0.022 * Math.sin(angle * 17.0 + 2.45);
    }

    private static double angleDistance(double first, double second) {
        double distance = Math.abs(first - second) % TWO_PI;
        return Math.min(distance, TWO_PI - distance);
    }

    private static double smoothStep(double value) {
        return value * value * (3.0 - 2.0 * value);
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static void validateTextureSize(int size) {
        if (size < 8) {
            throw new IllegalArgumentException("Glow texture must be at least 8 pixels");
        }
    }

    private static Texture whiteTexture() {
        Texture current = whiteTexture;
        if (current != null) {
            return current;
        }
        synchronized (RarityEffects.class) {
            if (whiteTexture == null) {
                try {
                    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                    image.setRGB(0, 0, 0xFFFFFFFF);
                    whiteTexture = ImageTextureLoader.loadNowrapNearestTexture(image, false);
                } catch (RuntimeException exception) {
                    reportFailure("background texture", exception);
                }
            }
            return whiteTexture;
        }
    }

    private static int integerField(Object object, String name) throws Exception {
        return field(object.getClass(), name).getInt(object);
    }

    private static int invokeInt(Object object, String name) throws Exception {
        return ((Integer) method(object.getClass(), name).invoke(object)).intValue();
    }

    private static Field field(Class<?> type, String name) throws NoSuchFieldException {
        String key = type.getName() + '#' + name;
        Field cached = FIELDS.get(key);
        if (cached != null) {
            return cached;
        }
        Class<?> cursor = type;
        while (cursor != null) {
            try {
                Field found = cursor.getDeclaredField(name);
                found.setAccessible(true);
                FIELDS.put(key, found);
                return found;
            } catch (NoSuchFieldException ignored) {
                cursor = cursor.getSuperclass();
            }
        }
        throw new NoSuchFieldException(key);
    }

    private static Method method(Class<?> type, String name) throws NoSuchMethodException {
        String key = type.getName() + '#' + name;
        Method cached = METHODS.get(key);
        if (cached != null) {
            return cached;
        }
        Class<?> cursor = type;
        while (cursor != null) {
            try {
                Method found = cursor.getDeclaredMethod(name);
                found.setAccessible(true);
                METHODS.put(key, found);
                return found;
            } catch (NoSuchMethodException ignored) {
                cursor = cursor.getSuperclass();
            }
        }
        throw new NoSuchMethodException(key);
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static void reportFailure(String area, Exception exception) {
        if (FAILURE_REPORTED.compareAndSet(false, true)) {
            LOGGER.log(Level.WARNING,
                    "wurm-highres disabled a rarity rendering path after a " + area + " error", exception);
        }
    }

    private static final class RenderContext {
        private final Object owner;
        private final Queue queue;

        private RenderContext(Object owner, Queue queue) {
            this.owner = owner;
            this.queue = queue;
        }
    }

    private static final class RarityStyle {
        private final float red;
        private final float green;
        private final float blue;
        private final float intensity;

        private RarityStyle(float red, float green, float blue, float intensity) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.intensity = intensity;
        }
    }
}
