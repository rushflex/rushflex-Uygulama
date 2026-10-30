package rushflex;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/** Renkler, fontlar ve küçük çizim yardımcıları. */
public final class Theme {

    public static final Color BG_TOP    = new Color(0x070A13);
    public static final Color BG_BOTTOM = new Color(0x0A0F1E);
    public static final Color TEXT      = new Color(0xEDF2FF);
    public static final Color MUTED     = new Color(0x909AB2);
    public static final Color DIM       = new Color(0x5C657A);
    public static final Color GREEN     = new Color(0x34D399);
    public static final Color PINK      = new Color(0xFE2C55);
    public static final Color GITHUB    = new Color(0xFFFFFF);
    public static final Color TIKTOK    = new Color(0x25F4EE);
    public static final Color INSTAGRAM = new Color(0xE1306C);
    public static final Color DISCORD   = new Color(0x5865F2);
    public static final Color GOLD      = new Color(0xFBBF24);

    public static Color ACCENT  = new Color(0x7C5CFF);
    public static Color ACCENT2 = new Color(0x22D3EE);

    /** [accent, accent2] çiftleri */
    public static final Color[][] PALETTES = {
            { new Color(0x7C5CFF), new Color(0x22D3EE) }, // mor - turkuaz
            { new Color(0xFF4D6D), new Color(0xFFB86C) }, // ateş
            { new Color(0x22C55E), new Color(0xA3E635) }, // orman
            { new Color(0xF472B6), new Color(0x818CF8) }, // pembe
            { new Color(0x38BDF8), new Color(0x6366F1) }, // okyanus
            { new Color(0xF59E0B), new Color(0xEF4444) }, // gün batımı
    };

    public static final String[] PALETTE_NAMES = {
            "Mor - Turkuaz", "Ateş", "Orman", "Pembe", "Okyanus", "Gün Batımı"
    };

    public static void setAccent(int index) {
        Color[] p = PALETTES[Math.max(0, Math.min(PALETTES.length - 1, index))];
        ACCENT = p[0];
        ACCENT2 = p[1];
    }

    private static String pick(String... names) {
        String[] avail = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String n : names) {
            for (String a : avail) {
                if (a.equalsIgnoreCase(n)) return a;
            }
        }
        return "Dialog";
    }

    static final String FAMILY  = pick("Segoe UI", "Inter", "Arial");
    static final String SEMI    = pick("Segoe UI Semibold", FAMILY);
    static final String DISPLAY = pick("Segoe UI Black", "Segoe UI Semibold", FAMILY);
    static final String MONO    = pick("Consolas", "Cascadia Mono", "Courier New");

    private static final java.util.Map<Long, Font> FONT_CACHE = new java.util.HashMap<>();

    private static Font cached(String family, int style, int size) {
        long key = ((long) family.hashCode() << 24) ^ ((long) style << 16) ^ size;
        return FONT_CACHE.computeIfAbsent(key, k -> new Font(family, style, size));
    }

    public static Font body(int size)    { return cached(FAMILY, Font.PLAIN, size); }
    public static Font bold(int size)    { return cached(FAMILY, Font.BOLD, size); }
    public static Font semi(int size)    { return cached(SEMI, Font.PLAIN, size); }
    public static Font display(int size) { return cached(DISPLAY, Font.BOLD, size); }
    public static Font mono(int size)    { return cached(MONO, Font.PLAIN, size); }

    public static Graphics2D g2(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), clamp(a));
    }

    public static Color mix(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    private static int clamp(int v) { return v < 0 ? 0 : Math.min(v, 255); }

    public static GradientPaint accentGradient(float x1, float y1, float x2, float y2) {
        return new GradientPaint(x1, y1, ACCENT, x2, y2, ACCENT2);
    }

    public static Color accentBlend(int alpha) {
        return alpha(ACCENT, alpha);
    }

    public static void fillRound(Graphics2D g, float x, float y, float w, float h, float r, Paint p) {
        g.setPaint(p);
        g.fill(new RoundRectangle2D.Float(x, y, w, h, r, r));
    }

    public static void strokeRound(Graphics2D g, float x, float y, float w, float h, float r, Color c, float weight) {
        g.setColor(c);
        g.setStroke(new BasicStroke(weight));
        g.draw(new RoundRectangle2D.Float(x, y, w, h, r, r));
    }

    /** Kartların altına yumuşak gölge. */
    public static void shadow(Graphics2D g, float x, float y, float w, float h, float r, int strength) {
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(0, 0, 0, Math.max(1, strength / (i * 2))));
            g.fill(new RoundRectangle2D.Float(x - i, y + i * 1.2f, w + i * 2, h + i * 2, r + i, r + i));
        }
    }

    public static void drawString(Graphics2D g, String s, Font f, Color c, float x, float baseline, int align) {
        g.setFont(f);
        g.setColor(c);
        float w = g.getFontMetrics(f).stringWidth(s);
        if (align == 1) x -= w / 2f;
        else if (align == 2) x -= w;
        g.drawString(s, x, baseline);
    }

    /** Harf aralıklı başlık (örn. RUSHFLEX). */
    public static void drawSpaced(Graphics2D g, String s, Font f, Color c, float x, float baseline, float spacing) {
        g.setFont(f);
        g.setColor(c);
        float cx = x;
        for (char ch : s.toCharArray()) {
            String str = String.valueOf(ch);
            g.drawString(str, cx, baseline);
            cx += g.getFontMetrics(f).stringWidth(str) + spacing;
        }
    }

    public static float spacedWidth(Graphics2D g, String s, Font f, float spacing) {
        g.setFont(f);
        float w = 0;
        for (char ch : s.toCharArray()) w += g.getFontMetrics(f).stringWidth(String.valueOf(ch)) + spacing;
        return w - spacing;
    }

    public static String ellipsize(Graphics2D g, String s, Font f, float maxWidth) {
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(s) <= maxWidth) return s;
        String dots = "...";
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            if (fm.stringWidth(sb.toString() + ch + dots) > maxWidth) break;
            sb.append(ch);
        }
        return sb + dots;
    }

    /** Küçük vektör ikonlar. */
    public static void glyph(Graphics2D g, String name, float x, float y, float s, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(Math.max(1.4f, s * 0.085f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float cx = x + s / 2f, cy = y + s / 2f;
        switch (name) {
            case "home":
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .14f, y + s * .46f); lineTo(cx, y + s * .16f);
                    lineTo(x + s * .86f, y + s * .46f); lineTo(x + s * .86f, y + s * .84f);
                    lineTo(x + s * .60f, y + s * .84f); lineTo(x + s * .60f, y + s * .60f);
                    lineTo(x + s * .40f, y + s * .60f); lineTo(x + s * .40f, y + s * .84f);
                    lineTo(x + s * .14f, y + s * .84f); closePath(); }});
                break;
            case "chart":
                g2.fill(new RoundRectangle2D.Float(x + s * .13f, y + s * .55f, s * .16f, s * .30f, s * .06f, s * .06f));
                g2.fill(new RoundRectangle2D.Float(x + s * .42f, y + s * .36f, s * .16f, s * .49f, s * .06f, s * .06f));
                g2.fill(new RoundRectangle2D.Float(x + s * .71f, y + s * .16f, s * .16f, s * .69f, s * .06f, s * .06f));
                break;
            case "grid":
                for (int i = 0; i < 4; i++) {
                    float gx = x + s * (.14f + (i % 2) * .40f);
                    float gy = y + s * (.14f + (i / 2) * .40f);
                    g2.fill(new RoundRectangle2D.Float(gx, gy, s * .32f, s * .32f, s * .10f, s * .10f));
                }
                break;
            case "link":
                g2.fill(new Ellipse2D.Float(cx - s * .30f, y + s * .22f, s * .20f, s * .20f));
                g2.fill(new Ellipse2D.Float(cx + s * .10f, y + s * .22f, s * .20f, s * .20f));
                g2.fill(new Ellipse2D.Float(cx - s * .10f, y + s * .58f, s * .20f, s * .20f));
                g2.draw(new Line2D.Float(cx - s * .02f, cy - s * .06f, cx + s * .16f, y + s * .30f));
                g2.draw(new Line2D.Float(cx - s * .02f, cy + s * .04f, cx + s * .02f, y + s * .60f));
                break;
            case "list":
                for (int i = 0; i < 3; i++) {
                    float ly = y + s * (.28f + i * .22f);
                    g2.fill(new Ellipse2D.Float(x + s * .14f, ly - s * .05f, s * .10f, s * .10f));
                    g2.draw(new Line2D.Float(x + s * .34f, ly, x + s * .84f, ly));
                }
                break;
            case "gear":
                g2.draw(new Ellipse2D.Float(cx - s * .22f, cy - s * .22f, s * .44f, s * .44f));
                g2.fill(new Ellipse2D.Float(cx - s * .07f, cy - s * .07f, s * .14f, s * .14f));
                for (int i = 0; i < 8; i++) {
                    double a = Math.PI * 2 * i / 8;
                    g2.draw(new Line2D.Float(
                            (float) (cx + Math.cos(a) * s * .28f), (float) (cy + Math.sin(a) * s * .28f),
                            (float) (cx + Math.cos(a) * s * .42f), (float) (cy + Math.sin(a) * s * .42f)));
                }
                break;
            case "external":
                g2.draw(new Line2D.Float(x + s * .30f, y + s * .70f, x + s * .78f, y + s * .22f));
                g2.draw(new Line2D.Float(x + s * .50f, y + s * .20f, x + s * .80f, y + s * .20f));
                g2.draw(new Line2D.Float(x + s * .80f, y + s * .20f, x + s * .80f, y + s * .50f));
                break;
            case "copy":
                g2.draw(new RoundRectangle2D.Float(x + s * .34f, y + s * .14f, s * .48f, s * .48f, s * .12f, s * .12f));
                g2.draw(new RoundRectangle2D.Float(x + s * .16f, y + s * .34f, s * .40f, s * .48f, s * .12f, s * .12f));
                break;
            case "check":
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .20f, cy); lineTo(x + s * .42f, y + s * .72f);
                    lineTo(x + s * .82f, y + s * .28f); }});
                break;
            case "close":
                g2.draw(new Line2D.Float(x + s * .26f, y + s * .26f, x + s * .74f, y + s * .74f));
                g2.draw(new Line2D.Float(x + s * .74f, y + s * .26f, x + s * .26f, y + s * .74f));
                break;
            case "minimize":
                g2.draw(new Line2D.Float(x + s * .24f, cy, x + s * .76f, cy));
                break;
            case "search":
                g2.draw(new Ellipse2D.Float(x + s * .14f, y + s * .14f, s * .50f, s * .50f));
                g2.draw(new Line2D.Float(x + s * .62f, y + s * .62f, x + s * .84f, y + s * .84f));
                break;
            case "refresh":
                g2.draw(new Arc2D.Float(cx - s * .30f, cy - s * .30f, s * .60f, s * .60f, 40, 280, Arc2D.OPEN));
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .64f, y + s * .16f); lineTo(x + s * .84f, y + s * .22f);
                    lineTo(x + s * .78f, y + s * .42f); }});
                break;
            case "code":
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .36f, y + s * .28f); lineTo(x + s * .16f, cy);
                    lineTo(x + s * .36f, y + s * .72f); }});
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .64f, y + s * .28f); lineTo(x + s * .84f, cy);
                    lineTo(x + s * .64f, y + s * .72f); }});
                break;
            case "shield":
                g2.draw(new Path2D.Float() {{ moveTo(cx, y + s * .13f); lineTo(x + s * .82f, y + s * .28f);
                    lineTo(x + s * .82f, y + s * .52f);
                    curveTo(x + s * .82f, y + s * .72f, x + s * .62f, y + s * .84f, cx, y + s * .90f);
                    curveTo(x + s * .38f, y + s * .84f, x + s * .18f, y + s * .72f, x + s * .18f, y + s * .52f);
                    lineTo(x + s * .18f, y + s * .28f); closePath(); }});
                break;
            case "layers":
                g2.draw(new Path2D.Float() {{ moveTo(cx, y + s * .16f); lineTo(x + s * .84f, y + s * .38f);
                    lineTo(cx, y + s * .60f); lineTo(x + s * .16f, y + s * .38f); closePath(); }});
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .16f, y + s * .54f); lineTo(cx, y + s * .76f);
                    lineTo(x + s * .84f, y + s * .54f); }});
                break;
            case "bot":
                g2.draw(new RoundRectangle2D.Float(x + s * .16f, y + s * .30f, s * .68f, s * .48f, s * .18f, s * .18f));
                g2.draw(new Line2D.Float(cx, y + s * .30f, cx, y + s * .16f));
                g2.fill(new Ellipse2D.Float(cx - s * .05f, y + s * .10f, s * .10f, s * .10f));
                g2.fill(new Ellipse2D.Float(x + s * .32f, y + s * .48f, s * .10f, s * .10f));
                g2.fill(new Ellipse2D.Float(x + s * .58f, y + s * .48f, s * .10f, s * .10f));
                break;
            case "globe":
                g2.draw(new Ellipse2D.Float(x + s * .14f, y + s * .14f, s * .72f, s * .72f));
                g2.draw(new Ellipse2D.Float(cx - s * .18f, y + s * .14f, s * .36f, s * .72f));
                g2.draw(new Line2D.Float(x + s * .16f, cy, x + s * .84f, cy));
                break;
            case "arrow":
                g2.draw(new Line2D.Float(x + s * .20f, cy, x + s * .78f, cy));
                g2.draw(new Path2D.Float() {{ moveTo(x + s * .58f, cy - s * .20f); lineTo(x + s * .80f, cy);
                    lineTo(x + s * .58f, cy + s * .20f); }});
                break;
            default:
                g2.fill(new Ellipse2D.Float(cx - s * .18f, cy - s * .18f, s * .36f, s * .36f));
        }
        g2.dispose();
    }

    public static Image logoMark(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = g2(img.createGraphics());
        float r = size * 0.28f;
        g.setPaint(new GradientPaint(0, 0, ACCENT, size, size, ACCENT2));
        g.fill(new RoundRectangle2D.Float(0.5f, 0.5f, size - 1, size - 1, r, r));
        g.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 235), size, size, new Color(255, 255, 255, 170)));
        g.setFont(display((int) (size * 0.52f)));
        FontMetrics fm = g.getFontMetrics();
        String s = "R";
        int tw = fm.stringWidth(s);
        g.drawString(s, (size - tw) / 2f, (size - fm.getHeight()) / 2f + fm.getAscent() + size * 0.02f);
        g.dispose();
        return img;
    }
}
