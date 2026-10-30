package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Ana sayfa. */
public class HomePage extends Page {

    private final JLabel typeLabel;
    private final Timer typeTimer;
    private final String[] lines = {
            "16 yaşında geliştirici.",
            "Minecraft plugin geliştiricisi.",
            "Discord bot mimarı.",
            "Corenya'nın arkasındaki isim.",
            "Öğrenmeyi hiç bırakmayan biri."
    };
    private int lineIndex = 0, charIndex;
    private int pause = 30;
    private boolean deleting = true;

    public HomePage(Config cfg, IntConsumer navigate, Consumer<String> toast) {
        setBorder(new EmptyBorder(0, 0, 4, 6));
        charIndex = lines[0].length();

        /* ---------------- hero ---------------- */
        JPanel hero = new JPanel(new BorderLayout(20, 0));
        hero.setOpaque(false);
        hero.setAlignmentX(LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        hero.setPreferredSize(new Dimension(100, 104));

        JPanel left = Components.vbox();
        JLabel kicker = Components.label("MERHABA, BEN", Theme.bold(10), Theme.ACCENT2);
        JLabel name = Components.label(cfg.name, Theme.display(42), Theme.TEXT);
        typeLabel = Components.label(lines[0] + "▌", Theme.mono(14), Theme.ACCENT2);
        typeLabel.setPreferredSize(new Dimension(640, 22));
        typeLabel.setMaximumSize(new Dimension(760, 22));
        left.add(kicker);
        left.add(Components.vgap(1));
        left.add(name);
        left.add(Components.vgap(1));
        left.add(typeLabel);
        hero.add(left, BorderLayout.WEST);

        JPanel right = Components.vbox();
        right.setBorder(new EmptyBorder(2, 0, 0, 0));
        Components.Badge online = new Components.Badge("ÇEVRİMİÇİ  ·  KOD YAZIYOR", Theme.GREEN);
        online.setAlignmentX(RIGHT_ALIGNMENT);
        Components.RoundButton share = new Components.RoundButton("Kartviziti kopyala", Components.Kind.GHOST);
        share.compact();
        share.setAlignmentX(RIGHT_ALIGNMENT);
        share.addActionListener(e -> {
            RushFlexApp.copyCard();
            toast.accept("Kartvizit panoya kopyalandı");
        });
        JLabel version = Components.label("RushFlex v1.0", Theme.mono(10), Theme.DIM);
        version.setAlignmentX(RIGHT_ALIGNMENT);
        version.setBorder(new EmptyBorder(4, 0, 0, 2));
        right.add(online);
        right.add(Components.vgap(7));
        right.add(share);
        right.add(version);
        hero.add(right, BorderLayout.EAST);

        /* ---------------- istatistikler ---------------- */
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(LEFT_ALIGNMENT);
        stats.add(stat("16", "YAŞINDA", Theme.ACCENT));
        stats.add(stat("4", "PROJE", Theme.ACCENT2));
        stats.add(stat("12", "YETENEK", Theme.GREEN));
        stats.add(stat("50", "MADDE", Theme.GOLD));
        stats.setPreferredSize(new Dimension(100, 80));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        /* ---------------- biyografi ---------------- */
        Components.Card bio = new Components.Card(new BorderLayout(0, 7));
        bio.pad(16).tint(Theme.ACCENT);
        bio.setAlignmentX(LEFT_ALIGNMENT);
        bio.setPreferredSize(new Dimension(100, 104));
        bio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));

        JPanel bioTop = new JPanel(new BorderLayout());
        bioTop.setOpaque(false);
        bioTop.add(Components.label("Kısaca ben", Theme.semi(14), Theme.TEXT), BorderLayout.WEST);
        JPanel tags = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        tags.setOpaque(false);
        tags.add(new Components.Badge("JAVA", Theme.GOLD));
        tags.add(new Components.Badge("NODE.JS", Theme.GREEN));
        tags.add(new Components.Badge("MINECRAFT", Theme.ACCENT2));
        tags.add(new Components.Badge("AÇIK KAYNAK", Theme.ACCENT));
        bioTop.add(tags, BorderLayout.EAST);

        JTextArea bioText = Components.wrapped(cfg.bioText(), Theme.body(11), Theme.MUTED);
        bio.add(bioTop, BorderLayout.NORTH);
        bio.add(bioText, BorderLayout.CENTER);

        /* ---------------- öne çıkanlar ---------------- */
        Components.RoundButton all = new Components.RoundButton("Tümü", Components.Kind.GHOST);
        all.compact();
        all.addActionListener(e -> navigate.accept(2));

        JPanel section = sectionTitle("Öne çıkan projeler", all);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        section.setPreferredSize(new Dimension(100, 32));

        JPanel featured = new JPanel(new GridLayout(1, 2, 16, 0));
        featured.setOpaque(false);
        featured.setAlignmentX(LEFT_ALIGNMENT);
        featured.add(featured(Data.PROJECTS[0]));
        featured.add(featured(Data.PROJECTS[2]));
        featured.setPreferredSize(new Dimension(100, 144));
        featured.setMaximumSize(new Dimension(Integer.MAX_VALUE, 144));

        /* ---------------- kayan yazı ---------------- */
        Components.Card tickerCard = new Components.Card(new BorderLayout());
        tickerCard.pad(6);
        tickerCard.setAlignmentX(LEFT_ALIGNMENT);
        tickerCard.setPreferredSize(new Dimension(100, 42));
        tickerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tickerCard.add(new Components.Ticker(Data.TICKER), BorderLayout.CENTER);

        add(hero);
        add(Components.vgap(10));
        add(stats);
        add(Components.vgap(10));
        add(bio);
        add(Components.vgap(14));
        add(section);
        add(Components.vgap(6));
        add(featured);
        add(Components.vgap(10));
        add(tickerCard);
        add(Box.createVerticalGlue());

        typeTimer = new Timer(140, e -> step());
    }

    private Components.Card stat(String value, String caption, Color tint) {
        Components.Card c = new Components.Card(new BorderLayout());
        c.pad(14).tint(tint);
        JLabel v = Components.label(value, Theme.display(26), tint);
        JLabel l = Components.label(caption, Theme.bold(9), Theme.MUTED);
        c.add(v, BorderLayout.NORTH);
        c.add(l, BorderLayout.SOUTH);
        return c;
    }

    private Components.Card featured(Data.Project p) {
        Components.Card c = new Components.Card(new BorderLayout(0, 6));
        c.pad(16).tint(p.tint()).clickable(() -> RushFlexApp.open(p.url()));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        JPanel id = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        id.setOpaque(false);
        id.add(new GlyphTile(p.glyph(), p.tint(), 34));
        id.add(Components.label(p.name(), Theme.semi(15), Theme.TEXT));
        top.add(id, BorderLayout.WEST);
        top.add(new Components.Badge(p.badge(), p.tint()), BorderLayout.EAST);

        JTextArea desc = Components.wrapped(p.desc(), Theme.body(11), Theme.MUTED);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagRow.setOpaque(false);
        for (String t : p.tags()) tagRow.add(new Components.Badge(t, Theme.alpha(Theme.TEXT, 190)));
        bottom.add(tagRow, BorderLayout.WEST);

        JLabel open = Components.label("GitHub'da aç  →", Theme.bold(10), p.tint());
        bottom.add(open, BorderLayout.EAST);

        c.add(top, BorderLayout.NORTH);
        c.add(desc, BorderLayout.CENTER);
        c.add(bottom, BorderLayout.SOUTH);
        return c;
    }

    private void step() {
        if (!isShowing()) return;
        String line = lines[lineIndex];
        if (!deleting) {
            charIndex = Math.min(line.length(), charIndex + 3);
            if (charIndex >= line.length()) {
                deleting = true;
                pause = 16;
            }
        } else {
            if (pause > 0) {
                pause--;
            } else {
                charIndex = Math.max(0, charIndex - 4);
                if (charIndex <= 0) {
                    deleting = false;
                    lineIndex = (lineIndex + 1) % lines.length;
                    typeTimer.setDelay(110);
                }
            }
        }
        String text = line.substring(0, Math.max(0, charIndex)) + "▌";
        if (!text.equals(typeLabel.getText())) typeLabel.setText(text);
    }

    @Override public void onShow() {
        if (!typeTimer.isRunning()) typeTimer.start();
    }

    @Override public void removeNotify() {
        typeTimer.stop();
        super.removeNotify();
    }

    /** Proje simgesi için yuvarlatılmış kutu. */
    static class GlyphTile extends JComponent {
        private final String glyph;
        private final Color tint;
        private final int size;

        GlyphTile(String glyph, Color tint, int size) {
            this.glyph = glyph;
            this.tint = tint;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int s = Math.min(size, Math.min(getWidth(), getHeight()));
            Theme.fillRound(g, 0, 0, s, s, 10, Theme.alpha(tint, 40));
            Theme.strokeRound(g, .5f, .5f, s - 1, s - 1, 10, Theme.alpha(tint, 110), 1f);
            Theme.glyph(g, glyph, (s - s * 0.5f) / 2f, (s - s * 0.5f) / 2f, s * 0.5f, tint);
            g.dispose();
        }
    }
}
