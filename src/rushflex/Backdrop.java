package rushflex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Animasyonlu ama ucuz arka plan: statik katmanlar (degrade + ızgara + vinyet)
 * bir kez çizilip önbelleğe alınır; sadece küçük "ışık" görüntüsü yenilenir.
 */
public class Backdrop extends JPanel {

    private final Timer timer;
    private float t = 0f;
    private boolean animations = true;

    private static Backdrop instance;
    private BufferedImage base, vignette, glow, scene;
    private int cachedW = -1, cachedH = -1;

    private final Random rnd = new Random(7);
    private final float[] px = new float[26];
    private final float[] py = new float[26];
    private final float[] ps = new float[26];
    private final float[] pv = new float[26];
    private final float[] pa = new float[26];

    public Backdrop() {
        instance = this;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_TOP);
        setOpaque(true);
        for (int i = 0; i < px.length; i++) {
            px[i] = rnd.nextFloat();
            py[i] = rnd.nextFloat();
            ps[i] = 0.6f + rnd.nextFloat() * 1.8f;
            pv[i] = 0.0006f + rnd.nextFloat() * 0.0014f;
            pa[i] = 0.15f + rnd.nextFloat() * 0.45f;
        }
        timer = new Timer(2400, e -> {
            if (!animations || !isShowing()) return;
            t += 0.75f;
            for (int i = 0; i < px.length; i++) {
                py[i] -= pv[i];
                if (py[i] < -0.02f) {
                    py[i] = 1.02f;
                    px[i] = rnd.nextFloat();
                }
            }
            updateGlow();
            compose();
            repaint();
        });
        // Arka plan animasyonu kapalı: tam pencere yenilemesi pahalı olduğu için
        // duvar kağıdı tek seferlik çizilir (statik ve çok daha akıcı).
    }

    /** Arka plan görüntüsü (sayfa görünümleri kendi bölgelerini bununla boyar). */
    public static BufferedImage wallpaper() { return instance == null ? null : instance.scene; }

    public void setAnimations(boolean value) {
        animations = value;
        if (!value) timer.stop();
    }

    @Override public void addNotify() { super.addNotify(); }
    @Override public void removeNotify() { timer.stop(); super.removeNotify(); }

    @Override protected void paintComponent(Graphics g0) {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        if (scene == null || w != cachedW || h != cachedH) buildImages(w, h);

        Graphics2D g = Theme.g2(g0);
        g.drawImage(scene, 0, 0, null);
        g.dispose();
    }

    private void buildImages(int w, int h) {
        cachedW = w;
        cachedH = h;

        base = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = Theme.g2(base.createGraphics());
        g.setPaint(new GradientPaint(0, 0, Theme.BG_TOP, w * 0.4f, h, Theme.BG_BOTTOM));
        g.fillRect(0, 0, w, h);
        g.setColor(Theme.alpha(Color.WHITE, 8));
        for (int x = 0; x < w; x += 52) g.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 52) g.drawLine(0, y, w, y);
        g.dispose();

        vignette = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D vg = Theme.g2(vignette.createGraphics());
        vg.setPaint(new RadialGradientPaint(w / 2f, h / 2f, Math.max(w, h) * 0.78f,
                new float[]{0.35f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 120)}));
        vg.fillRect(0, 0, w, h);
        vg.dispose();

        int gw = Math.max(140, w / 8);
        int gh = Math.max(90, h / 8);
        glow = new BufferedImage(gw, gh, BufferedImage.TYPE_INT_ARGB);
        updateGlow();
        compose();
    }

    /** Önceden ölçeklenmiş tek bir arka plan görüntüsü: çizim maliyeti ~1 blit. */
    private void compose() {
        int w = cachedW, h = cachedH;
        if (w <= 0 || h <= 0 || base == null || vignette == null) return;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = Theme.g2(img.createGraphics());
        g.drawImage(base, 0, 0, null);
        if (glow != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(glow, 0, 0, w, h, null);
        }
        g.drawImage(vignette, 0, 0, null);
        g.dispose();
        scene = img;
    }

    /** Işık lekeleri + tozlar; sadece küçük görüntü üzerinde çalışır. */
    private void updateGlow() {
        if (glow == null) return;
        int gw = glow.getWidth(), gh = glow.getHeight();
        Graphics2D g = glow.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, gw, gh);
        g.setComposite(AlphaComposite.SrcOver);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        blob(g, gw * (0.16f + 0.02f * (float) Math.sin(t * 0.9)), gh * (0.16f + 0.03f * (float) Math.cos(t * 0.7)),
                gw * 0.58f, Theme.alpha(Theme.ACCENT, 90));
        blob(g, gw * (0.88f + 0.02f * (float) Math.cos(t * 0.6)), gh * (0.30f + 0.04f * (float) Math.sin(t * 0.8)),
                gw * 0.50f, Theme.alpha(Theme.ACCENT2, 55));
        blob(g, gw * (0.60f + 0.05f * (float) Math.sin(t * 0.45)), gh * (0.94f + 0.02f * (float) Math.cos(t * 0.5)),
                gw * 0.52f, Theme.alpha(Theme.ACCENT, 45));

        for (int i = 0; i < px.length; i++) {
            float alpha = pa[i] * (0.6f + 0.4f * (float) Math.sin(t * 1.6 + i));
            g.setColor(Theme.alpha(i % 3 == 0 ? Theme.ACCENT2 : Color.WHITE, (int) (alpha * 150)));
            float s = ps[i];
            g.fill(new Ellipse2D.Float(px[i] * gw, py[i] * gh, s, s));
        }
        g.dispose();
    }

    private void blob(Graphics2D g, float cx, float cy, float r, Color color) {
        if (r <= 0) return;
        g.setPaint(new RadialGradientPaint(cx, cy, r,
                new float[]{0f, 0.55f, 1f},
                new Color[]{color, Theme.alpha(color, color.getAlpha() / 3),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}));
        g.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
    }

    /* ------------------------------------------------------------------ */
    /** Üstte duran cam katman: konfeti + bildirim. */
    public static class Glass extends JPanel {
        private static class P {
            float x, y, vx, vy, rot, vr, size, life;
            Color color;
        }

        private final java.util.List<P> parts = new java.util.ArrayList<>();
        private final Random rnd = new Random();
        private final Timer timer;
        private String toast;
        private int toastLife = 0;

        public Glass() {
            setOpaque(false);
            timer = new Timer(40, e -> tick());
        }

        @Override public boolean contains(int x, int y) { return false; }
        @Override public boolean contains(Point p) { return false; }

        public void burst(int x, int y, int count) {
            for (int i = 0; i < count; i++) {
                P p = new P();
                p.x = x;
                p.y = y;
                double a = rnd.nextDouble() * Math.PI * 2;
                double sp = 2.5 + rnd.nextDouble() * 7.5;
                p.vx = (float) (Math.cos(a) * sp);
                p.vy = (float) (Math.sin(a) * sp) - 2.5f;
                p.rot = rnd.nextFloat() * 360f;
                p.vr = (rnd.nextFloat() - 0.5f) * 16f;
                p.size = 5 + rnd.nextFloat() * 7;
                p.life = 70 + rnd.nextFloat() * 50;
                Color[] pool = {Theme.ACCENT, Theme.ACCENT2, Theme.GREEN, Theme.GOLD, Theme.PINK, Color.WHITE};
                p.color = pool[rnd.nextInt(pool.length)];
                parts.add(p);
            }
            ensureTimer();
            repaint();
        }

        public void toast(String message) {
            toast = message;
            toastLife = 95;
            ensureTimer();
            repaint();
        }

        private void ensureTimer() {
            if (!timer.isRunning()) timer.start();
        }

        private void tick() {
            boolean alive = false;
            for (int i = parts.size() - 1; i >= 0; i--) {
                P p = parts.get(i);
                p.x += p.vx;
                p.y += p.vy;
                p.vy += 0.22f;
                p.vx *= 0.995f;
                p.rot += p.vr;
                p.life -= 1;
                if (p.life <= 0 || p.y > getHeight() + 40) {
                    parts.remove(i);
                } else {
                    alive = true;
                }
            }
            if (toastLife > 0) {
                toastLife--;
                alive = true;
            }
            if (!alive) timer.stop();
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();

            for (P p : parts) {
                Graphics2D g2 = (Graphics2D) g.create();
                float alpha = Math.min(1f, p.life / 35f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.translate(p.x, p.y);
                g2.rotate(Math.toRadians(p.rot));
                g2.setColor(p.color);
                g2.fill(new RoundRectangle2D.Float(-p.size / 2, -p.size / 4, p.size, p.size / 2, 2, 2));
                g2.dispose();
            }

            if (toast != null && toastLife > 0) {
                int life = 95 - toastLife;
                float a = Math.min(1f, Math.min(life / 8f, toastLife / 14f));
                a = Math.max(0f, Math.min(1f, a));
                Font f = Theme.semi(13);
                g.setFont(f);
                FontMetrics fm = g.getFontMetrics();
                int tw = fm.stringWidth(toast);
                int bw = tw + 58, bh = 46;
                float bx = (w - bw) / 2f, by = h - 84;
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
                Theme.shadow(g, bx, by, bw, bh, bh / 2f, 110);
                g.setColor(new Color(0x11162A));
                g.fill(new RoundRectangle2D.Float(bx, by, bw, bh, bh / 2f, bh / 2f));
                Theme.strokeRound(g, bx + .5f, by + .5f, bw - 1, bh - 1, bh / 2f, Theme.alpha(Theme.ACCENT, 150), 1f);
                Theme.glyph(g, "check", bx + 18, by + 15, 16, Theme.GREEN);
                Theme.drawString(g, toast, f, Theme.TEXT, bx + 42, by + bh / 2f + 5, 0);
            }
            g.dispose();
        }
    }
}
