package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/** Projeler sayfası. */
public class ProjectsPage extends Page {

    public ProjectsPage(Consumer<String> toast) {
        setBorder(new EmptyBorder(0, 0, 4, 6));
        add(header("PROJELER", "Ürettiklerim",
                "Corenya ekosistemi: koruma, tablist, bot ve web. Hepsi tek bir vizyonun parçası."));
        add(Components.vgap(14));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        for (Data.Project p : Data.PROJECTS) grid.add(project(p));
        grid.setPreferredSize(new Dimension(100, 390));
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 390));
        add(grid);
        add(Components.vgap(12));

        Components.Card foot = new Components.Card(new BorderLayout(12, 0));
        foot.pad(12).tint(Theme.ACCENT);
        foot.setAlignmentX(LEFT_ALIGNMENT);
        foot.setPreferredSize(new Dimension(100, 56));
        foot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        foot.add(Components.label("Daha fazlası ve kaynak kodlar GitHub profilimde.", Theme.body(12), Theme.MUTED),
                BorderLayout.WEST);
        Components.RoundButton open = new Components.RoundButton("GitHub'ı Aç", Components.Kind.PRIMARY);
        open.compact();
        open.addActionListener(e -> RushFlexApp.open(RushFlexApp.cfg.github));
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        holder.setOpaque(false);
        holder.add(open);
        foot.add(holder, BorderLayout.EAST);
        add(foot);
        add(Box.createVerticalGlue());
    }

    private Components.Card project(Data.Project p) {
        Components.Card c = new Components.Card(new BorderLayout(0, 8));
        c.pad(18).tint(p.tint()).clickable(() -> RushFlexApp.open(p.url()));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        JPanel id = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        id.setOpaque(false);
        id.add(new HomePage.GlyphTile(p.glyph(), p.tint(), 40));
        JPanel names = Components.vbox();
        names.add(Components.label(p.name(), Theme.semi(16), Theme.TEXT));
        names.add(Components.label(p.badge() + "  ·  RUSHFLEX", Theme.mono(9), Theme.DIM));
        id.add(names);
        top.add(id, BorderLayout.WEST);
        top.add(new Components.Badge(p.badge(), p.tint()), BorderLayout.EAST);

        JTextArea desc = Components.wrapped(p.desc(), Theme.body(11), Theme.MUTED);

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setOpaque(false);
        JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagRow.setOpaque(false);
        for (String t : p.tags()) tagRow.add(new Components.Badge(t, Theme.alpha(Theme.TEXT, 190)));
        bottom.add(tagRow, BorderLayout.WEST);

        Components.RoundButton open = new Components.RoundButton("Aç", Components.Kind.GHOST);
        open.compact();
        open.setForeground(p.tint());
        open.addActionListener(e -> RushFlexApp.open(p.url()));
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        holder.setOpaque(false);
        holder.add(open);
        bottom.add(holder, BorderLayout.EAST);

        c.add(top, BorderLayout.NORTH);
        c.add(desc, BorderLayout.CENTER);
        c.add(bottom, BorderLayout.SOUTH);
        return c;
    }
}
