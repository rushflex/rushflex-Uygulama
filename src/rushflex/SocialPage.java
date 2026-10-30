package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Sosyal medya sayfası. */
public class SocialPage extends Page {

    private final IntConsumer navigate;

    public SocialPage(Config cfg, IntConsumer navigate, Consumer<String> toast) {
        this.navigate = navigate;
        setBorder(new EmptyBorder(0, 0, 4, 6));
        add(header("SOSYAL", "Beni bul",
                "Tek tıkla aç, tek tıkla kopyala. Bütün hesaplarım burada."));
        add(Components.vgap(14));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.add(social("github", "GitHub", cfg.github,
                "Kodlarım, repolarım ve açık kaynak projelerim.", Theme.GITHUB, toast));
        grid.add(social("tiktok", "TikTok", cfg.tiktok,
                "Kısa videolar, kod anları ve sahne arkası.", Theme.TIKTOK, toast));
        grid.add(social("instagram", "Instagram", cfg.instagram,
                "Günlük hayat, projeler ve hikayeler.", Theme.INSTAGRAM, toast));
        grid.add(social("discord", "Discord", cfg.discord,
                "Corenya topluluğu, destek ve sohbet.", Theme.DISCORD, toast));
        grid.setPreferredSize(new Dimension(100, 376));
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 376));
        add(grid);
        add(Components.vgap(12));

        Components.Card note = new Components.Card(new BorderLayout(12, 0));
        note.pad(12).tint(Theme.ACCENT2);
        note.setAlignmentX(LEFT_ALIGNMENT);
        note.setPreferredSize(new Dimension(100, 56));
        note.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        note.add(Components.label("Linkler sana ait: Ayarlar sayfasından saniyeler içinde güncelle.",
                Theme.body(12), Theme.MUTED), BorderLayout.WEST);
        Components.RoundButton edit = new Components.RoundButton("Ayarları Aç", Components.Kind.GHOST);
        edit.compact();
        edit.addActionListener(e -> navigate.accept(5));
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        holder.setOpaque(false);
        holder.add(edit);
        note.add(holder, BorderLayout.EAST);
        add(note);
        add(Box.createVerticalGlue());
    }

    private Components.Card social(String icon, String name, String url, String desc, Color tint, Consumer<String> toast) {
        boolean has = url != null && !url.isBlank();
        Components.Card c = new Components.Card(new BorderLayout(0, 8));
        c.pad(18).tint(tint);
        if (has) c.clickable(() -> RushFlexApp.open(url));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        JPanel id = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        id.setOpaque(false);
        id.add(new BrandIcon(icon, tint, 44));
        JPanel names = Components.vbox();
        names.add(Components.label(name, Theme.semi(16), Theme.TEXT));
        names.add(Components.vgap(2));
        names.add(Components.label(has ? Config.pretty(url) : "link tanımlı değil",
                Theme.mono(10), has ? Theme.alpha(tint, 220) : Theme.DIM));
        id.add(names);
        top.add(id, BorderLayout.WEST);
        top.add(new Components.Badge(name.toUpperCase(), tint), BorderLayout.EAST);

        JTextArea d = Components.wrapped(desc, Theme.body(11), Theme.MUTED);

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setOpaque(false);
        Components.RoundButton open = new Components.RoundButton(has ? "Profili Aç" : "Link Ekle", Components.Kind.BRAND);
        open.brand(has ? tint : Theme.MUTED);
        open.compact();
        open.addActionListener(e -> {
            if (has) RushFlexApp.open(url);
            else navigate.accept(5);
        });
        bottom.add(open, BorderLayout.WEST);

        Components.RoundButton copy = new Components.RoundButton("Kopyala", Components.Kind.GHOST);
        copy.compact();
        copy.addActionListener(e -> {
            if (!has) {
                toast.accept("Bu platform için link tanımlı değil");
                navigate.accept(5);
                return;
            }
            RushFlexApp.copy(url);
            toast.accept(name + " linki kopyalandı");
        });
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        holder.setOpaque(false);
        holder.add(copy);
        bottom.add(holder, BorderLayout.EAST);

        c.add(top, BorderLayout.NORTH);
        c.add(d, BorderLayout.CENTER);
        c.add(bottom, BorderLayout.SOUTH);
        return c;
    }

    static class BrandIcon extends JComponent {
        private final String icon;
        private final Color tint;
        private final int size;

        BrandIcon(String icon, Color tint, int size) {
            this.icon = icon;
            this.tint = tint;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Theme.g2(g0);
            int s = Math.min(size, Math.min(getWidth(), getHeight()));
            g.setColor(Theme.alpha(tint, 36));
            g.fill(new Ellipse2D.Float(0, 0, s, s));
            g.setColor(Theme.alpha(tint, 120));
            g.setStroke(new BasicStroke(1f));
            g.draw(new Ellipse2D.Float(.5f, .5f, s - 1, s - 1));
            int is = Math.round(s * 0.48f);
            g.drawImage(Icons.get(icon, is, tint), (s - is) / 2, (s - is) / 2, null);
            g.dispose();
        }
    }
}
