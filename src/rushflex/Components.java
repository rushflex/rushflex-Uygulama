package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/** Ortak arayüz bileşenleri. */
public final class Components {

    private Components() {}

    /* ------------------------------------------------------------------ */
    /* Kart                                                                */
    /* ------------------------------------------------------------------ */
    public static class Card extends JPanel {
        public int radius = 20;
        public Color tint;
        public boolean hover;
        public boolean clickable;
        public Runnable onClick;

        public Card() { this(new BorderLayout()); }

        public Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(new EmptyBorder(18, 20, 18, 20));
        }

        public Card pad(int p) { setBorder(new EmptyBorder(p, p, p, p)); return this; }

        public Card tint(Color c) { this.tint = c; return this; }

        public Card clickable(Runnable r) {
            this.clickable = true;
            this.onClick = r;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
            });
            return this;
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            if (w <= 4 || h <= 4) { g.dispose(); return; }
            Theme.shadow(g, 3, 4, w - 6, h - 8, radius, hover && clickable ? 95 : 55);
            Color top = Theme.alpha(Color.WHITE, hover && clickable ? 24 : 14);
            Color bot = Theme.alpha(Color.WHITE, 4);
            Theme.fillRound(g, 1, 1, w - 2, h - 2, radius, new GradientPaint(0, 0, top, 0, h, bot));
            Color border = tint != null
                    ? Theme.alpha(tint, hover && clickable ? 150 : 70)
                    : Theme.alpha(Color.WHITE, hover && clickable ? 55 : 26);
            Theme.strokeRound(g, 1, 1, w - 2, h - 2, radius, border, 1f);
            if (tint != null) {
                g.setColor(Theme.alpha(tint, 12));
                g.fill(new RoundRectangle2D.Float(1, 1, w - 2, 3, 2, 2));
            }
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Buton                                                               */
    /* ------------------------------------------------------------------ */
    public enum Kind { PRIMARY, GHOST, SOFT, BRAND }

    public static class RoundButton extends JButton {
        private final Kind kind;
        private Color brand;
        private boolean hover;

        public RoundButton(String text, Kind kind) {
            super(text);
            this.kind = kind;
            this.brand = Theme.ACCENT;
            setFont(Theme.bold(13));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setRolloverEnabled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
            setIconTextGap(7);
            setForeground(kind == Kind.PRIMARY ? new Color(0x080B14) : Theme.TEXT);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        public RoundButton brand(Color c) {
            this.brand = c;
            setForeground(c);
            return this;
        }

        public RoundButton compact() { setBorder(new EmptyBorder(7, 12, 7, 12)); setFont(Theme.bold(12)); return this; }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            int r = Math.min(14, h / 2);
            switch (kind) {
                case PRIMARY -> {
                    g.setPaint(new GradientPaint(0, 0, hover ? Theme.mix(Theme.ACCENT, Color.WHITE, .12f) : Theme.ACCENT,
                            w, h, hover ? Theme.mix(Theme.ACCENT2, Color.WHITE, .12f) : Theme.ACCENT2));
                    g.fill(new RoundRectangle2D.Float(0, 0, w, h, r, r));
                }
                case SOFT -> {
                    Theme.fillRound(g, 0, 0, w, h, r, Theme.alpha(Theme.ACCENT, hover ? 60 : 38));
                    Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, r, Theme.alpha(Theme.ACCENT, hover ? 150 : 90), 1f);
                }
                case GHOST -> {
                    Theme.fillRound(g, 0, 0, w, h, r, Theme.alpha(Color.WHITE, hover ? 26 : 14));
                    Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, r, Theme.alpha(Color.WHITE, hover ? 60 : 30), 1f);
                }
                case BRAND -> {
                    Theme.fillRound(g, 0, 0, w, h, r, Theme.alpha(brand, hover ? 62 : 34));
                    Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, r, Theme.alpha(brand, hover ? 190 : 120), 1f);
                }
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /* ------------------------------------------------------------------ */
    /* Kenar çubuğu butonu */
    /* ------------------------------------------------------------------ */
    public static class NavButton extends JButton {
        private final String glyph;
        private boolean active;
        private boolean hover;
        private final int index;

        public NavButton(String text, String glyph, int index) {
            super(text);
            this.glyph = glyph;
            this.index = index;
            setFont(Theme.semi(14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setRolloverEnabled(false);
            setOpaque(false);
            setForeground(Theme.MUTED);
            setBorder(new EmptyBorder(0, 52, 0, 14));
            setPreferredSize(new Dimension(212, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        public int index() { return index; }

        public void setActive(boolean a) {
            active = a;
            setForeground(a ? Theme.TEXT : Theme.MUTED);
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            if (active || hover) {
                Theme.fillRound(g, 0, 0, w, h, 13, active
                        ? new GradientPaint(0, 0, Theme.alpha(Theme.ACCENT, 62), w, 0, Theme.alpha(Theme.ACCENT2, 18))
                        : Theme.alpha(Color.WHITE, 12));
                if (active) {
                    Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, 13, Theme.alpha(Theme.ACCENT, 120), 1f);
                }
            }
            if (active) {
                g.setPaint(Theme.accentGradient(0, 0, 0, h));
                g.fill(new RoundRectangle2D.Float(0, 12, 3, h - 24, 3, 3));
            }
            Theme.glyph(g, glyph, 18, (h - 19) / 2f, 19, active ? Theme.ACCENT2 : (hover ? Theme.TEXT : Theme.DIM));
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /* ------------------------------------------------------------------ */
    /* Pencere butonu                                                      */
    /* ------------------------------------------------------------------ */
    public static class IconButton extends JButton {
        private final String glyph;
        private final boolean danger;
        private boolean hover;

        public IconButton(String glyph, boolean danger) {
            this.glyph = glyph;
            this.danger = danger;
            setPreferredSize(new Dimension(40, 30));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setRolloverEnabled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            if (hover) {
                Theme.fillRound(g, 2, 2, w - 4, h - 4, 9,
                        danger ? Theme.alpha(Theme.PINK, 170) : Theme.alpha(Color.WHITE, 22));
            }
            Theme.glyph(g, glyph, (w - 16) / 2f, (h - 16) / 2f, 16,
                    danger && hover ? Color.WHITE : Theme.MUTED);
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Etiket                                                              */
    /* ------------------------------------------------------------------ */
    public static class Badge extends JComponent {
        private final String text;
        private final Color tint;

        public Badge(String text, Color tint) {
            this.text = text;
            this.tint = tint;
            setFont(Theme.bold(11));
            setOpaque(false);
        }

        @Override public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(text) + 20, 24);
        }

        @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        @Override public Dimension getMinimumSize() { return getPreferredSize(); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            Theme.fillRound(g, 0, 0, w, h, h, Theme.alpha(tint, 34));
            Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, h, Theme.alpha(tint, 90), 1f);
            Theme.drawString(g, text, getFont(), tint, w / 2f, h / 2f + 4, 1);
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Yetenek çubuğu */
    /* ------------------------------------------------------------------ */
    public static class SkillBar extends JComponent {
        private final String name;
        private final int target;
        private final Color tint;
        private float value = 0f;
        private Timer timer;

        public SkillBar(String name, int target, Color tint) {
            this.name = name;
            this.target = target;
            this.tint = tint;
            setOpaque(false);
            setPreferredSize(new Dimension(240, 48));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        }

        public void start() {
            if (timer != null) return;
            value = 0f;
            final float goal = target / 100f;
            timer = new Timer(40, e -> {
                value += (goal - value) * 0.12f + 0.006f;
                if (value >= goal - 0.002f) {
                    value = goal;
                    timer.stop();
                }
                repaint();
            });
            timer.start();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth();
            Theme.drawString(g, name, Theme.semi(13), Theme.TEXT, 0, 17, 0);
            Theme.drawString(g, "%" + target, Theme.mono(12), tint, w, 17, 2);

            int barY = 27, barH = 7;
            Theme.fillRound(g, 0, barY, w, barH, barH, Theme.alpha(Color.WHITE, 16));
            float fillW = Math.max(barH, w * value);
            GradientPaint gp = new GradientPaint(0, 0, tint, fillW, 0, Theme.mix(tint, Theme.ACCENT2, .45f));
            Theme.fillRound(g, 0, barY, fillW, barH, barH, gp);
            if (value > 0.01f) {
                g.setColor(Theme.alpha(Color.WHITE, 120));
                g.fill(new Ellipse2D.Float(fillW - 5, barY + 1.5f, 4, 4));
            }
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Kayan yazÄ±                                                          */
    /* ------------------------------------------------------------------ */
    public static class Ticker extends JComponent {
        private final String[] items;
        private float offset = 0f;
        private float total = 0f;
        private final Timer timer;
        private boolean started;

        public Ticker(String[] items) {
            this.items = items;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            timer = new Timer(200, e -> {
                if (!isShowing()) return;
                offset += 2.0f;
                if (total > 0 && offset >= total) offset -= total;
                repaint();
            });
            timer.start();
        }

        @Override public void addNotify() { super.addNotify(); timer.start(); }
        @Override public void removeNotify() { timer.stop(); super.removeNotify(); }

        private float itemWidth(Graphics2D g, String s) {
            return Theme.spacedWidth(g, s, Theme.bold(11), 1.6f) + 34;
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            if (total == 0) {
                float t = 0;
                for (String s : items) t += itemWidth(g, s);
                total = t;
            }
            float x = -offset;
            g.setFont(Theme.bold(11));
            int guard = 0;
            while (x < w && guard++ < 200) {
                for (String s : items) {
                    if (x > -20 && x < w + 20) {
                        Theme.drawSpaced(g, s, Theme.bold(11), Theme.alpha(Theme.TEXT, 165), x, h / 2f + 4, 1.6f);
                    }
                    x += itemWidth(g, s);
                    if (x > -20 && x < w + 20) {
                        g.setColor(Theme.alpha(Theme.ACCENT2, 200));
                        g.fill(new Ellipse2D.Float(x - 24, h / 2f - 1.5f, 3, 3));
                    }
                    if (x > w + 40) break;
                }
            }
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Avatar                                                              */
    /* ------------------------------------------------------------------ */
    public static class Avatar extends JComponent {
        private final int size;
        private float glow = 0.55f;
        private Runnable onClick;

        public Avatar(int size) {
            this.size = size;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        public void onClick(Runnable r) {
            onClick = r;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int s = Math.min(size, Math.min(getWidth(), getHeight()));
            float pulse = (float) (Math.sin(glow * Math.PI) * 0.5 + 0.5);
            g.setPaint(new RadialGradientPaint(getWidth() / 2f, getHeight() / 2f, s * 0.72f,
                    new float[]{0.5f, 1f},
                    new Color[]{Theme.alpha(Theme.ACCENT, (int) (70 + 60 * pulse)), Theme.alpha(Theme.ACCENT2, 0)}));
            g.fill(new Ellipse2D.Float((getWidth() - s * 1.35f) / 2f, (getHeight() - s * 1.35f) / 2f, s * 1.35f, s * 1.35f));

            float x = (getWidth() - s) / 2f, y = (getHeight() - s) / 2f;
            g.setPaint(new GradientPaint(x, y, Theme.ACCENT, x + s, y + s, Theme.ACCENT2));
            g.fill(new Ellipse2D.Float(x, y, s, s));
            g.setPaint(new GradientPaint(x, y, new Color(0x171C2E), x + s, y + s, new Color(0x0D1120)));
            g.fill(new Ellipse2D.Float(x + 2.5f, y + 2.5f, s - 5, s - 5));

            g.setFont(Theme.display((int) (s * 0.42f)));
            String letter = String.valueOf(RushFlexApp.cfg.name.isEmpty() ? 'R' : Character.toUpperCase(RushFlexApp.cfg.name.charAt(0)));
            FontMetrics fm = g.getFontMetrics();
            g.setPaint(new GradientPaint(x, y, Theme.ACCENT, x + s, y + s, Theme.ACCENT2));
            g.drawString(letter, x + (s - fm.stringWidth(letter)) / 2f, y + (s - fm.getHeight()) / 2f + fm.getAscent());

            // Çevrimiçi noktası
            float dot = s * 0.20f;
            g.setColor(new Color(0x0B0F1C));
            g.fill(new Ellipse2D.Float(x + s - dot - 1, y + s - dot - 1, dot + 2, dot + 2));
            g.setColor(Theme.GREEN);
            g.fill(new Ellipse2D.Float(x + s - dot + 1, y + s - dot + 1, dot - 2, dot - 2));
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* 50 madde satÄ±rÄ±                                                     */
    /* ------------------------------------------------------------------ */
    public static class FactRow extends JComponent {
        private final int number;
        private final String text;
        private boolean hover;

        public FactRow(int number, String text) {
            this.number = number;
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(300, 54));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        public String text() { return text; }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            if (hover) {
                Theme.fillRound(g, 0, 2, w, h - 4, 12, Theme.alpha(Color.WHITE, 12));
            }
            String num = String.format("%02d", number);
            Theme.fillRound(g, 10, h / 2f - 13, 38, 26, 9,
                    Theme.alpha(number % 10 == 0 ? Theme.ACCENT2 : Theme.ACCENT, 34));
            Theme.drawString(g, num, Theme.mono(11), number % 10 == 0 ? Theme.ACCENT2 : Theme.ACCENT, 29, h / 2f + 4, 1);

            Font f = Theme.body(13);
            String t = Theme.ellipsize(g, text, f, w - 80);
            Theme.drawString(g, t, f, hover ? Theme.TEXT : Theme.alpha(Theme.TEXT, 215), 60, h / 2f + 5, 0);

            g.setColor(Theme.alpha(Color.WHITE, 12));
            g.drawLine(60, h - 1, w - 14, h - 1);
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Arama alanÄ±                                                         */
    /* ------------------------------------------------------------------ */
    public static class SearchField extends JTextField {
        private final String placeholder;

        public SearchField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(9, 38, 9, 14));
            setForeground(Theme.TEXT);
            setCaretColor(Theme.ACCENT2);
            setFont(Theme.body(13));
            setSelectionColor(Theme.alpha(Theme.ACCENT, 120));
            setSelectedTextColor(Color.WHITE);
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            boolean focus = isFocusOwner();
            Theme.fillRound(g, 0, 0, w, h, 12, Theme.alpha(Color.WHITE, focus ? 20 : 12));
            Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, 12,
                    focus ? Theme.alpha(Theme.ACCENT, 170) : Theme.alpha(Color.WHITE, 28), 1f);
            Theme.glyph(g, "search", 13, h / 2f - 8, 16, focus ? Theme.ACCENT2 : Theme.DIM);
            g.dispose();
            super.paintComponent(g0);
            if (getText().isEmpty() && !focus) {
                Graphics2D g3 = Theme.g2(g0);
                Theme.drawString(g3, placeholder, Theme.body(13), Theme.DIM, 38, h / 2f + 5, 0);
                g3.dispose();
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /* Metin girişi */
    /* ------------------------------------------------------------------ */
    public static class TextInput extends JTextField {
        private final String placeholder;

        public TextInput(String text) { this(text, null); }

        public TextInput(String text, String placeholder) {
            super(text);
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(9, 12, 9, 12));
            setForeground(Theme.TEXT);
            setCaretColor(Theme.ACCENT2);
            setFont(Theme.body(12));
            setSelectionColor(Theme.alpha(Theme.ACCENT, 120));
            setSelectedTextColor(Color.WHITE);
            setPreferredSize(new Dimension(120, 36));
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            boolean focus = isFocusOwner();
            Theme.fillRound(g, 0, 0, w, h, 10, Theme.alpha(Color.WHITE, focus ? 20 : 12));
            Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, 10,
                    focus ? Theme.alpha(Theme.ACCENT, 180) : Theme.alpha(Color.WHITE, 28), 1f);
            g.dispose();
            super.paintComponent(g0);
            if (placeholder != null && getText().isEmpty() && !focus) {
                Graphics2D g2 = Theme.g2(g0);
                Theme.drawString(g2, placeholder, Theme.body(11), Theme.DIM, 12, h / 2f + 4, 0);
                g2.dispose();
            }
        }
    }

    /** KaydÄ±rÄ±labilir, satÄ±r kaydÄ±ran metin alanÄ±. */
    public static JTextArea wrapped(String text, Font font, Color color) {
        JTextArea ta = new JTextArea(text);
        ta.setFont(font);
        ta.setForeground(color);
        ta.setOpaque(false);
        ta.setEditable(false);
        ta.setFocusable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(null);
        ta.setMargin(new Insets(0, 0, 0, 0));
        ta.setRows(2);
        ta.setCursor(Cursor.getDefaultCursor());
        return ta;
    }

    /* ------------------------------------------------------------------ */
    /* Scroll                                                              */
    /* ------------------------------------------------------------------ */
    public static class ThinBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = Theme.alpha(Color.WHITE, 55);
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override protected JButton createDecreaseButton(int orientation) { return ghostButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return ghostButton(); }

        private JButton ghostButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) { }

        @Override protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
            if (r.isEmpty() || !c.isEnabled()) return;
            Graphics2D g = Theme.g2(g0);
            g.setColor(isDragging ? Theme.alpha(Theme.ACCENT2, 190) : Theme.alpha(Color.WHITE, 60));
            g.fill(new RoundRectangle2D.Float(r.x + 3, r.y + 2, Math.max(3, r.width - 6), Math.max(12, r.height - 4), 6, 6));
            g.dispose();
        }
    }

    public static JScrollPane scroll(JComponent view) {
        JScrollPane sp = new JScrollPane();
        sp.setViewport(new WallpaperViewport());
        sp.getViewport().setView(view);
        sp.setOpaque(false);
        sp.setBorder(null);
        sp.setViewportBorder(null);
        JScrollBar bar = sp.getVerticalScrollBar();
        bar.setUnitIncrement(26);
        bar.setBlockIncrement(180);
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(10, 0));
        bar.setUI(new ThinBarUI());
        return sp;
    }

    /**
     * Sayfa görünümü: arka planı kendi bölgesinde boyar ve opaktır; böylece
     * küçük bir bileşenin yenilenmesi tüm pencereyi yeniden çizmez.
     */
    static class WallpaperViewport extends JViewport {
        @Override public boolean isOpaque() { return true; }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            java.awt.image.BufferedImage wp = Backdrop.wallpaper();
            if (wp != null) {
                Point abs = new Point(0, 0);
                Component c = this;
                while (c != null) {
                    abs.translate(c.getX(), c.getY());
                    c = c.getParent();
                }
                g.drawImage(wp, -abs.x, -abs.y, null);
            } else {
                g.setColor(Theme.BG_TOP);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* YardÄ±mcÄ±lar                                                         */
    /* ------------------------------------------------------------------ */
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setOpaque(false);
        return l;
    }

    public static JPanel vbox() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    public static JPanel hbox(int gap) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (gap > 0) p.add(Box.createHorizontalStrut(gap));
        return p;
    }

    public static Component hgap(int px) { return Box.createHorizontalStrut(px); }
    public static Component vgap(int px) { return Box.createVerticalStrut(px); }

    public static void full(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension d = c.getPreferredSize();
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, d.height));
    }

    public static Color randomTint() {
        Random r = new Random();
        Color[] pool = {Theme.ACCENT, Theme.ACCENT2, Theme.GREEN, Theme.GOLD, Theme.PINK};
        return pool[r.nextInt(pool.length)];
    }
}
