package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;

/** Ayarlar: isim, linkler, tema. */
public class SettingsPage extends Page {

    private final Config cfg;
    private final Runnable afterSave;
    private final Consumer<String> toast;
    private final JTextField nameField, tagField, ghField, ttField, igField, dcField;
    private int accent;

    public SettingsPage(Config cfg, Runnable afterSave, Consumer<String> toast) {
        this.cfg = cfg;
        this.afterSave = afterSave;
        this.toast = toast;
        this.accent = cfg.accent;
        setBorder(new EmptyBorder(0, 0, 4, 6));

        add(header("AYARLAR", "Kendine göre düzenle",
                "İsmini, sloganını, linklerini ve 50 maddeni değiştir. Değişiklikler anında kaydedilir."));
        add(Components.vgap(10));

        nameField = new Components.TextInput(cfg.name, "Görünen ad");
        tagField = new Components.TextInput(cfg.tagline, "Kısa slogan");
        ghField = new Components.TextInput(cfg.github, "https://github.com/kullanici");
        ttField = new Components.TextInput(cfg.tiktok, "https://tiktok.com/@kullanici");
        igField = new Components.TextInput(cfg.instagram, "https://instagram.com/kullanici");
        dcField = new Components.TextInput(cfg.discord, "https://discord.gg/davet");

        /* profil + görünüm */
        JPanel top = new JPanel(new GridLayout(1, 2, 16, 0));
        top.setOpaque(false);
        top.setAlignmentX(LEFT_ALIGNMENT);

        Components.Card profile = new Components.Card(new BorderLayout(0, 10));
        profile.pad(16).tint(Theme.ACCENT);
        JPanel pv = Components.vbox();
        pv.add(Components.label("PROFİL", Theme.bold(10), Theme.ACCENT));
        pv.add(Components.vgap(6));
        pv.add(field("GÖRÜNEN AD", nameField));
        pv.add(Components.vgap(4));
        pv.add(field("SLOGAN", tagField));
        profile.add(pv, BorderLayout.CENTER);

        Components.Card look = new Components.Card(new BorderLayout(0, 8));
        look.pad(16).tint(Theme.ACCENT2);
        JPanel lv = Components.vbox();
        lv.add(Components.label("GÖRÜNÜM", Theme.bold(10), Theme.ACCENT2));
        lv.add(Components.vgap(4));
        lv.add(Components.label("Vurgu rengi", Theme.semi(13), Theme.TEXT));
        lv.add(Components.vgap(8));

        JPanel swatches = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        swatches.setOpaque(false);
        for (int i = 0; i < Theme.PALETTES.length; i++) {
            swatches.add(new Swatch(i));
        }
        lv.add(swatches);
        lv.add(Components.vgap(4));
        lv.add(Components.label("Seçtiğin renk tüm uygulamaya anında uygulanır.", Theme.body(10), Theme.DIM));
        look.add(lv, BorderLayout.CENTER);

        top.add(profile);
        top.add(look);
        top.setPreferredSize(new Dimension(100, 180));
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        /* linkler */
        Components.Card links = new Components.Card(new BorderLayout(0, 10));
        links.pad(16).tint(Theme.GREEN);
        links.setAlignmentX(LEFT_ALIGNMENT);
        links.setPreferredSize(new Dimension(100, 212));
        links.setMaximumSize(new Dimension(Integer.MAX_VALUE, 212));

        links.add(Components.label("SOSYAL LİNKLER", Theme.bold(10), Theme.GREEN), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 16, 10));
        form.setOpaque(false);
        form.add(field("GITHUB", ghField));
        form.add(field("TIKTOK", ttField));
        form.add(field("INSTAGRAM", igField));
        form.add(field("DISCORD", dcField));
        links.add(form, BorderLayout.CENTER);

        /* alt butonlar */
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setAlignmentX(LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        footer.setPreferredSize(new Dimension(100, 44));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftButtons.setOpaque(false);
        Components.RoundButton folder = new Components.RoundButton("Config Klasörü", Components.Kind.GHOST);
        folder.compact();
        folder.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Config.folder());
            } catch (Exception ex) {
                toast.accept("Klasör açılamadı: " + ex.getMessage());
            }
        });
        Components.RoundButton share = new Components.RoundButton("Kartvizit", Components.Kind.GHOST);
        share.compact();
        share.addActionListener(e -> {
            RushFlexApp.copyCard();
            toast.accept("Kartvizit panoya kopyalandı");
        });
        Components.RoundButton bio = new Components.RoundButton("Biyografi", Components.Kind.GHOST);
        bio.compact();
        bio.addActionListener(e -> openBioEditor());
        Components.RoundButton reset = new Components.RoundButton("Sıfırla", Components.Kind.GHOST);
        reset.compact();
        reset.addActionListener(e -> {
            nameField.setText("Rushflex");
            tagField.setText("16 yaşında geliştirici");
            ghField.setText("https://github.com/rushflex");
            ttField.setText("https://www.tiktok.com/@_wqrzsx");
            igField.setText("");
            dcField.setText("https://discord.gg/corenya");
            toast.accept("Varsayılanlar yüklendi - kaydetmeyi unutma");
        });
        leftButtons.add(folder);
        leftButtons.add(share);
        leftButtons.add(bio);
        leftButtons.add(reset);
        footer.add(leftButtons, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightButtons.setOpaque(false);
        Components.RoundButton confetti = new Components.RoundButton("Konfeti", Components.Kind.SOFT);
        confetti.compact();
        confetti.addActionListener(e -> RushFlexApp.celebrate());
        Components.RoundButton save = new Components.RoundButton("Kaydet", Components.Kind.PRIMARY);
        save.compact();
        save.addActionListener(e -> {
            String n = nameField.getText().trim();
            cfg.name = n.isEmpty() ? "Rushflex" : n;
            cfg.tagline = tagField.getText().trim().isEmpty() ? "16 yaşında geliştirici" : tagField.getText().trim();
            cfg.github = Config.normalize("github", ghField.getText());
            cfg.tiktok = Config.normalize("tiktok", ttField.getText());
            cfg.instagram = Config.normalize("instagram", igField.getText());
            cfg.discord = Config.normalize("discord", dcField.getText());
            cfg.accent = accent;
            Theme.setAccent(accent);
            cfg.save();
            afterSave.run();
            RushFlexApp.toast("Ayarlar kaydedildi");
        });
        rightButtons.add(confetti);
        rightButtons.add(save);
        footer.add(rightButtons, BorderLayout.EAST);

        add(top);
        add(Components.vgap(10));
        add(links);
        add(Components.vgap(10));
        add(footer);
        add(Box.createVerticalGlue());
    }

    /** Ana sayfadaki "Kısaca ben" metnini düzenleme penceresi. */
    private void openBioEditor() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Hakkımda Metni", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(new Color(0x0B0F1C));
        root.setBorder(new javax.swing.border.LineBorder(Theme.alpha(Theme.ACCENT, 130), 1, true));

        JPanel head = Components.vbox();
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(18, 22, 0, 22));
        head.add(Components.label("KISACA BEN", Theme.bold(10), Theme.ACCENT2));
        head.add(Components.vgap(3));
        head.add(Components.label("Biyografini düzenle", Theme.display(22), Theme.TEXT));
        head.add(Components.vgap(2));
        head.add(Components.label("Ana sayfada görünen kısa tanıtım. Boş bırakırsan varsayılan metin gelir.",
                Theme.body(11), Theme.MUTED));

        JTextArea area = new JTextArea(cfg.bioText());
        area.setFont(Theme.body(12));
        area.setForeground(Theme.TEXT);
        area.setBackground(new Color(0x0B1020));
        area.setCaretColor(Theme.ACCENT2);
        area.setSelectionColor(Theme.alpha(Theme.ACCENT, 120));
        area.setSelectedTextColor(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(new EmptyBorder(0, 22, 0, 22));
        sp.getViewport().setBackground(new Color(0x0B1020));
        sp.setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(22);

        Components.RoundButton defaults = new Components.RoundButton("Varsayılana Dön", Components.Kind.GHOST);
        defaults.compact();
        defaults.addActionListener(e -> area.setText(Data.BIO));
        Components.RoundButton cancel = new Components.RoundButton("İptal", Components.Kind.GHOST);
        cancel.compact();
        cancel.addActionListener(e -> dialog.dispose());
        Components.RoundButton save = new Components.RoundButton("Kaydet", Components.Kind.PRIMARY);
        save.compact();
        save.addActionListener(e -> {
            String text = area.getText().trim();
            cfg.bio = text.equals(Data.BIO.trim()) ? "" : text;
            cfg.save();
            dialog.dispose();
            toast.accept(text.isEmpty() ? "Varsayılan biyografi kullanılacak" : "Biyografi kaydedildi");
            afterSave.run();
        });

        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        foot.setBorder(new EmptyBorder(0, 22, 16, 22));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(defaults);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(cancel);
        right.add(save);
        foot.add(left, BorderLayout.WEST);
        foot.add(right, BorderLayout.EAST);

        root.add(head, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);
        root.add(foot, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setSize(640, 420);
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setVisible(true);
    }

    private JPanel field(String label, JTextField input) {
        JPanel p = Components.vbox();
        JLabel l = Components.label(label, Theme.bold(9), Theme.DIM);
        l.setBorder(new EmptyBorder(0, 2, 4, 0));
        input.setAlignmentX(LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(l);
        p.add(input);
        return p;
    }

    /** Renk seçici. */
    private class Swatch extends JComponent {
        private final int index;
        private boolean hover;

        Swatch(int index) {
            this.index = index;
            setPreferredSize(new Dimension(32, 32));
            setMaximumSize(new Dimension(32, 32));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    accent = Swatch.this.index;
                    Theme.setAccent(accent);
                    Container root = SwingUtilities.getRoot(Swatch.this) instanceof Container c ? c : null;
                    if (root != null) root.repaint();
                    repaint();
                }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int s = Math.min(getWidth(), getHeight());
            Color[] p = Theme.PALETTES[index];
            g.setPaint(new GradientPaint(0, 0, p[0], s, s, p[1]));
            g.fill(new Ellipse2D.Float(3, 3, s - 6, s - 6));
            if (index == accent) {
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(2f));
                g.draw(new Ellipse2D.Float(1, 1, s - 2, s - 2));
                Theme.glyph(g, "check", s * 0.24f, s * 0.24f, s * 0.52f, new Color(0x0A0E18));
            } else if (hover) {
                g.setColor(Theme.alpha(Color.WHITE, 120));
                g.setStroke(new BasicStroke(1.5f));
                g.draw(new Ellipse2D.Float(1, 1, s - 2, s - 2));
            }
            g.dispose();
        }
    }
}
