package rushflex;

import javax.swing.*;
import java.awt.*;

/** Sayfaların ortak iskeleti. */
public abstract class Page extends JPanel implements Scrollable {

    protected Page() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 26; }
    @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return Math.max(60, visibleRect.height - 60);
    }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }

    /** Sayfa gösterildiğinde tetiklenir (animasyonları başlatmak için). */
    public void onShow() { }

    protected JPanel header(String kicker, String title, String subtitle) {
        JPanel v = Components.vbox();
        JLabel k = Components.label(kicker.toUpperCase(), Theme.bold(10), Theme.ACCENT2);
        JLabel t = Components.label(title, Theme.display(28), Theme.TEXT);
        JLabel s = Components.label(subtitle, Theme.body(12), Theme.MUTED);
        k.setBorder(new javax.swing.border.EmptyBorder(0, 0, 4, 0));
        t.setBorder(new javax.swing.border.EmptyBorder(0, 0, 4, 0));
        v.add(k);
        v.add(t);
        v.add(s);
        v.setAlignmentX(LEFT_ALIGNMENT);
        return v;
    }

    protected JPanel sectionTitle(String text, JComponent right) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l = Components.label(text, Theme.semi(15), Theme.TEXT);
        row.add(l, BorderLayout.WEST);
        if (right != null) row.add(right, BorderLayout.EAST);
        return row;
    }
}
