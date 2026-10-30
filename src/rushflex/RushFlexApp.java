package rushflex;

import rushflex.Components.Avatar;
import rushflex.Components.NavButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/** Uygulama giriş noktası. */
public class RushFlexApp {

    public static Config cfg;
    static JFrame frame;
    static Backdrop.Glass glass;
    static Backdrop backdrop;
    static JPanel content;
    static CardLayout cards;
    static final Page[] pages = new Page[6];
    static final NavButton[] navs = new NavButton[6];
    static JLabel sideName, sideTag;
    static Avatar avatar;
    static Fade fade;
    static Timer fadeTimer;
    static int current = 0;
    static int avatarClicks = 0;

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        cfg = Config.load();
        Theme.setAccent(cfg.accent);
        SwingUtilities.invokeLater(RushFlexApp::showSplash);
    }

    /* ------------------------------------------------------------------ */
    /* Açılış ekranı                                                       */
    /* ------------------------------------------------------------------ */
    static class SplashPanel extends JPanel {
        private float progress = 0f;
        private final Timer timer;

        SplashPanel() {
            setPreferredSize(new Dimension(460, 264));
            timer = new Timer(16, e -> {
                progress = Math.min(1f, progress + 0.012f);
                repaint();
            });
            timer.start();
        }

        @Override public void removeNotify() { timer.stop(); super.removeNotify(); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(0x0A0E1A));
            g.fillRect(0, 0, w, h);
            g.setPaint(new RadialGradientPaint(w / 2f, h * 0.28f, w * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{Theme.alpha(Theme.ACCENT, 80), new Color(0x0A0E1A)}));
            g.fillRect(0, 0, w, h);
            Theme.strokeRound(g, .5f, .5f, w - 1, h - 1, 18, Theme.alpha(Theme.ACCENT, 70), 1f);

            int logo = 66;
            g.drawImage(Theme.logoMark(logo), (w - logo) / 2, 46, null);
            Theme.drawSpaced(g, "RUSHFLEX", Theme.bold(20), Theme.TEXT, (w - Theme.spacedWidth(g, "RUSHFLEX", Theme.bold(20), 5f)) / 2f, 148, 5f);
            Theme.drawString(g, "16 yaşında geliştirici", Theme.body(12), Theme.MUTED, w / 2f, 174, 1);
            Theme.drawString(g, "kişisel tanıtım uygulaması", Theme.mono(10), Theme.DIM, w / 2f, 194, 1);

            int bw = w - 120, bx = 60, by = h - 44;
            Theme.fillRound(g, bx, by, bw, 4, 4, Theme.alpha(Color.WHITE, 18));
            Theme.fillRound(g, bx, by, bw * progress, 4, 4, Theme.accentGradient(bx, by, bx + bw, by));
            g.dispose();
        }
    }

    static void showSplash() {
        JWindow splash = new JWindow();
        SplashPanel p = new SplashPanel();
        splash.setContentPane(p);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        Timer t = new Timer(1350, e -> {
            splash.dispose();
            buildFrame();
        });
        t.setRepeats(false);
        t.start();
    }

    /* ------------------------------------------------------------------ */
    /* Ana pencere                                                         */
    /* ------------------------------------------------------------------ */
    static void buildFrame() {
        frame = new JFrame("RushFlex");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Theme.logoMark(256));

        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double scale = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
        if (scale <= 0) scale = 1;
        int availW = (int) (bounds.width / scale);
        int availH = (int) (bounds.height / scale);
        int w = Math.min(1210, availW - 24);
        int h = Math.min(800, availH - 24);
        frame.setSize(w, h);
        frame.setLocation((int) ((availW - w) / 2.0 * scale), (int) ((availH - h) / 2.0 * scale));

        cards = new CardLayout();
        content = new JPanel(cards);
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 18, 10, 18));

        fade = new Fade();

        JPanel wrap = new JPanel(null) {
            @Override public void doLayout() {
                for (Component c : getComponents()) c.setBounds(0, 0, getWidth(), getHeight());
            }
        };
        wrap.setOpaque(false);
        wrap.add(content);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(sidebar(), BorderLayout.WEST);
        body.add(wrap, BorderLayout.CENTER);

        backdrop = new Backdrop();
        backdrop.add(titleBar(), BorderLayout.NORTH);
        backdrop.add(body, BorderLayout.CENTER);
        frame.setContentPane(backdrop);
        frame.addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) { backdrop.setAnimations(cfg.animations); }
            @Override public void windowLostFocus(WindowEvent e) { backdrop.setAnimations(false); }
        });

        glass = new Backdrop.Glass();
        frame.setGlassPane(glass);
        glass.setVisible(true);

        rebuildPages();

        frame.setVisible(true);
        installKeys();

        showPage(0, true);
    }

    static void rebuildPages() {
        content.removeAll();
        pages[0] = new HomePage(cfg, RushFlexApp::showPage, RushFlexApp::toast);
        pages[1] = new SkillsPage();
        pages[2] = new ProjectsPage(RushFlexApp::toast);
        pages[3] = new SocialPage(cfg, RushFlexApp::showPage, RushFlexApp::toast);
        pages[4] = new FactsPage(cfg, RushFlexApp::rebuildPages, RushFlexApp::toast);
        pages[5] = new SettingsPage(cfg, RushFlexApp::rebuildAndStay, RushFlexApp::toast);

        for (int i = 0; i < pages.length; i++) {
            content.add(Components.scroll(pages[i]), "p" + i);
        }
        content.revalidate();
        content.repaint();
        updateSidebar();
        for (NavButton n : navs) if (n != null) n.setActive(n.index() == current);
        if (cards != null) {
            cards.show(content, "p" + current);
            pages[current].onShow();
        }
    }

    static void rebuildAndStay() {
        rebuildPages();
        toast("Profil güncellendi");
    }

    /* ------------------------------------------------------------------ */
    /* Başlık çubuğu                                                       */
    /* ------------------------------------------------------------------ */
    static JComponent titleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 52));
        bar.setBorder(new EmptyBorder(0, 18, 0, 10));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 13));
        left.setOpaque(false);
        JLabel logo = new JLabel(new ImageIcon(Theme.logoMark(26)));
        left.add(logo);
        left.add(new SpacedLabel("RUSHFLEX", Theme.bold(13), Theme.TEXT, 2.6f));
        left.add(new Components.Badge("v1.0", Theme.ACCENT2));
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 11));
        right.setOpaque(false);
        Components.IconButton min = new Components.IconButton("minimize", false);
        min.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        Components.IconButton close = new Components.IconButton("close", true);
        close.addActionListener(e -> System.exit(0));
        right.add(min);
        right.add(close);
        bar.add(right, BorderLayout.EAST);

        drag(bar);
        drag(left);
        drag(logo);
        drag(right);
        return bar;
    }

    static void drag(Component c) {
        MouseAdapter a = new MouseAdapter() {
            Point start;
            @Override public void mousePressed(MouseEvent e) { start = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (start == null) return;
                Point p = e.getLocationOnScreen();
                frame.setLocation(p.x - start.x, p.y - start.y);
            }
        };
        c.addMouseListener(a);
        c.addMouseMotionListener(a);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    static class SpacedLabel extends JComponent {
        private final String text;
        private final Font font;
        private final Color color;
        private final float spacing;

        SpacedLabel(String text, Font font, Color color, float spacing) {
            this.text = text;
            this.font = font;
            this.color = color;
            this.spacing = spacing;
            setOpaque(false);
        }

        @Override public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(font);
            int w = 0;
            for (char ch : text.toCharArray()) w += fm.stringWidth(String.valueOf(ch)) + (int) spacing;
            return new Dimension(w - (int) spacing, fm.getHeight());
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            Theme.drawSpaced(g, text, font, color, 0, getHeight() / 2f + 5, spacing);
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Kenar çubuğu                                                        */
    /* ------------------------------------------------------------------ */
    static JComponent sidebar() {
        JPanel side = new JPanel();
        side.setOpaque(false);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(252, 100));
        side.setBorder(new EmptyBorder(2, 16, 18, 12));

        avatar = new Avatar(72);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatar.onClick(() -> {
            avatarClicks++;
            if (avatarClicks >= 5) {
                avatarClicks = 0;
                celebrate();
                toast("Sürpriz! Konfeti zamanı");
            } else if (avatarClicks == 1) {
                toast("Selam! 5 kez tıklamayı dene");
            }
        });

        sideName = Components.label(cfg.name, Theme.semi(18), Theme.TEXT);
        sideName.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideTag = Components.label(cfg.tagline, Theme.body(11), Theme.MUTED);
        sideTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        side.add(Components.vgap(8));
        side.add(avatar);
        side.add(Components.vgap(10));
        side.add(sideName);
        side.add(Components.vgap(3));
        side.add(sideTag);
        side.add(Components.vgap(20));

        String[] labels = {"Hakkımda", "Yetenekler", "Projeler", "Sosyal", "50 Madde", "Ayarlar"};
        String[] glyphs = {"home", "chart", "grid", "link", "list", "gear"};
        for (int i = 0; i < labels.length; i++) {
            NavButton n = new NavButton(labels[i], glyphs[i], i);
            n.setAlignmentX(Component.LEFT_ALIGNMENT);
            navs[i] = n;
            final int idx = i;
            n.addActionListener(e -> showPage(idx));
            side.add(n);
            side.add(Components.vgap(4));
        }

        side.add(Box.createVerticalGlue());

        JLabel follow = Components.label("BENİ TAKİP ET", Theme.bold(9), Theme.DIM);
        follow.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(follow);
        side.add(Components.vgap(8));

        JPanel mins = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        mins.setOpaque(false);
        mins.setAlignmentX(Component.CENTER_ALIGNMENT);
        mins.add(mini("github", () -> open(cfg.github), Theme.GITHUB));
        mins.add(mini("tiktok", () -> open(cfg.tiktok), Theme.TIKTOK));
        mins.add(mini("instagram", () -> open(cfg.instagram), Theme.INSTAGRAM));
        mins.add(mini("discord", () -> open(cfg.discord), Theme.DISCORD));
        side.add(mins);
        side.add(Components.vgap(10));

        JLabel ver = Components.label("v1.0  ·  2026", Theme.mono(10), Theme.DIM);
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(ver);
        side.add(Components.vgap(4));

        JLabel made = Components.label("Türkiye'den sevgilerle", Theme.body(10), Theme.DIM);
        made.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(made);

        return side;
    }

    static JComponent mini(String icon, Runnable action, Color tint) {
        JComponent c = new JComponent() {
            boolean hover;

            {
                setPreferredSize(new Dimension(36, 36));
                setMaximumSize(new Dimension(36, 36));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { action.run(); }
                });
            }

            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Theme.g2(g0);
                int s = Math.min(getWidth(), getHeight());
                g.setColor(Theme.alpha(tint, hover ? 40 : 18));
                g.fill(new java.awt.geom.Ellipse2D.Float(0, 0, s, s));
                if (hover) {
                    g.setColor(Theme.alpha(tint, 120));
                    g.setStroke(new BasicStroke(1f));
                    g.draw(new java.awt.geom.Ellipse2D.Float(.5f, .5f, s - 1, s - 1));
                }
                int is = Math.round(s * 0.5f);
                g.drawImage(Icons.get(icon, is, Theme.alpha(tint, hover ? 255 : 200)), (s - is) / 2, (s - is) / 2, null);
                g.dispose();
            }
        };
        return c;
    }

    static void updateSidebar() {
        if (sideName != null) sideName.setText(cfg.name);
        if (sideTag != null) sideTag.setText(cfg.tagline);
        if (avatar != null) avatar.repaint();
        if (frame != null) frame.setTitle(cfg.name + " · " + cfg.tagline);
    }

    /* ------------------------------------------------------------------ */
    /* Sayfa geçişi                                                        */
    /* ------------------------------------------------------------------ */
    static void showPage(int index) { showPage(index, true); }

    static void showPage(int index, boolean animate) {
        if (index < 0 || index >= pages.length || pages[index] == null) return;
        current = index;
        cards.show(content, "p" + index);
        for (NavButton n : navs) if (n != null) n.setActive(n.index() == index);
        pages[index].onShow();
    }

    static class Fade extends JPanel {
        float alpha = 0f;

        Fade() { setOpaque(false); }

        @Override public boolean contains(int x, int y) { return false; }
        @Override public boolean contains(Point p) { return false; }

        @Override protected void paintComponent(Graphics g0) {
            if (alpha <= 0.002f) return;
            Graphics2D g = Theme.g2(g0);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, alpha)));
            g.setColor(Theme.BG_TOP);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Yardımcılar                                                         */
    /* ------------------------------------------------------------------ */
    public static void open(String url) {
        if (url == null || url.isBlank()) {
            toast("Link tanımlı değil");
            return;
        }
        new Thread(() -> {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    copy(url);
                    toast("Link açılamadı, panoya kopyalandı");
                });
            }
        }, "link-opener").start();
    }

    public static void copy(String text) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        } catch (Exception ignored) {
        }
    }

    /** Profil kartvizitini panoya kopyalar (paylaşmak için). */
    public static void copyCard() {
        StringBuilder sb = new StringBuilder();
        sb.append(cfg.name).append(" · ").append(cfg.tagline).append('\n');
        if (!cfg.github.isBlank()) sb.append("GitHub:    ").append(cfg.github).append('\n');
        if (!cfg.tiktok.isBlank()) sb.append("TikTok:    ").append(cfg.tiktok).append('\n');
        if (!cfg.instagram.isBlank()) sb.append("Instagram: ").append(cfg.instagram).append('\n');
        if (!cfg.discord.isBlank()) sb.append("Discord:   ").append(cfg.discord).append('\n');
        copy(sb.toString().trim());
    }

    public static void toast(String msg) {
        if (glass != null) glass.toast(msg);
    }

    public static void celebrate() {
        if (glass == null || frame == null) return;
        glass.burst(frame.getWidth() / 2, (int) (frame.getHeight() * 0.32), 170);
    }

    static void installKeys() {
        JRootPane root = frame.getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, InputEvent.CTRL_DOWN_MASK), "page" + i);
            am.put("page" + i, new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { showPage(idx); }
            });
        }
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "quit");
        am.put("quit", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { System.exit(0); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "none");
        am.put("none", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { }
        });
    }

    static List<Component> all(Container c) {
        List<Component> list = new ArrayList<>();
        for (Component comp : c.getComponents()) {
            list.add(comp);
            if (comp instanceof Container cc) list.addAll(all(cc));
        }
        return list;
    }
}
