package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Yetenekler sayfası. */
public class SkillsPage extends Page {

    private final List<Components.SkillBar> bars = new ArrayList<>();

    public SkillsPage() {
        setBorder(new EmptyBorder(0, 0, 4, 6));
        add(header("YETENEKLER", "Ne yapabiliyorum?",
                "Diller, araçlar ve günlük hayatımda gerçekten kullandığım teknolojiler."));
        add(Components.vgap(14));

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.add(group("DİLLER", "Kod yazdığım diller", Data.LANGUAGES, Theme.ACCENT));
        grid.add(group("ARAÇLAR", "Günlük kullandığım araçlar", Data.TOOLS, Theme.ACCENT2));
        grid.setPreferredSize(new Dimension(100, 338));
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 338));

        Components.Card now = new Components.Card(new BorderLayout(16, 0));
        now.pad(18).tint(Theme.GREEN);
        now.setAlignmentX(LEFT_ALIGNMENT);
        now.setPreferredSize(new Dimension(100, 104));
        now.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));

        JPanel leftBox = Components.vbox();
        leftBox.add(Components.label("ŞU AN ÖĞRENİYORUM", Theme.bold(10), Theme.GREEN));
        leftBox.add(Components.vgap(5));
        leftBox.add(Components.label("TypeScript, Docker ve sistem tasarımı", Theme.semi(16), Theme.TEXT));
        now.add(leftBox, BorderLayout.WEST);

        JPanel tags = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        tags.setOpaque(false);
        tags.add(new Components.Badge("TYPESCRIPT", Theme.ACCENT2));
        tags.add(new Components.Badge("DOCKER", Theme.GREEN));
        tags.add(new Components.Badge("SYSTEM DESIGN", Theme.GOLD));
        now.add(tags, BorderLayout.EAST);

        add(grid);
        add(Components.vgap(12));
        add(now);
        add(Box.createVerticalGlue());
    }

    private Components.Card group(String kicker, String title, Data.Skill[] skills, Color tint) {
        Components.Card c = new Components.Card(new BorderLayout(0, 12));
        c.pad(18).tint(tint);

        JPanel head = Components.vbox();
        head.add(Components.label(kicker, Theme.bold(10), tint));
        head.add(Components.vgap(3));
        head.add(Components.label(title, Theme.semi(16), Theme.TEXT));
        c.add(head, BorderLayout.NORTH);

        JPanel list = Components.vbox();
        for (Data.Skill s : skills) {
            Components.SkillBar bar = new Components.SkillBar(s.name(), s.value(), s.tint());
            bars.add(bar);
            list.add(bar);
        }
        c.add(list, BorderLayout.CENTER);
        return c;
    }

    @Override public void onShow() {
        for (Components.SkillBar b : bars) b.start();
    }
}
