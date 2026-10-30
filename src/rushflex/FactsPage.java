package rushflex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** 50 maddede ben + düzenleme. */
public class FactsPage extends Page {

    private final Config cfg;
    private final Runnable afterSave;
    private final Consumer<String> toast;
    private final List<Components.FactRow> rows = new ArrayList<>();
    private final JLabel counter;

    public FactsPage(Config cfg, Runnable afterSave, Consumer<String> toast) {
        this.cfg = cfg;
        this.afterSave = afterSave;
        this.toast = toast;

        setBorder(new EmptyBorder(0, 0, 4, 6));
        add(header("50 MADDE", "50 maddede ben",
                "Kendimi en kısa yoldan anlatmanın yolu: numaralı 50 gerçek. Ara, düzenle, kendine göre yaz."));
        add(Components.vgap(12));

        Components.SearchField search = new Components.SearchField("50 madde içinde ara...");
        search.setPreferredSize(new Dimension(100, 38));
        counter = Components.label("", Theme.mono(11), Theme.MUTED);

        Components.RoundButton edit = new Components.RoundButton("Maddeleri Düzenle", Components.Kind.GHOST);
        edit.compact();
        edit.addActionListener(e -> openEditor());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        right.setOpaque(false);
        right.add(counter);
        right.add(edit);

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        toolbar.setPreferredSize(new Dimension(100, 42));
        toolbar.add(search, BorderLayout.CENTER);
        toolbar.add(right, BorderLayout.EAST);

        JPanel list = Components.vbox();
        list.setBorder(new EmptyBorder(4, 4, 4, 4));
        String[] facts = cfg.factList();
        for (int i = 0; i < facts.length; i++) {
            Components.FactRow row = new Components.FactRow(i + 1, facts[i]);
            rows.add(row);
            list.add(row);
        }

        Components.Card listCard = new Components.Card(new BorderLayout());
        listCard.pad(6);
        listCard.setAlignmentX(LEFT_ALIGNMENT);
        listCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        listCard.add(list, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(search.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { filter(search.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { filter(search.getText()); }
        });

        updateCounter(rows.size(), rows.size());
        add(toolbar);
        add(Components.vgap(8));
        add(listCard);
        add(Box.createVerticalGlue());
    }

    private void updateCounter(int shown, int total) {
        counter.setText(shown + " / " + total + " madde");
    }

    private void filter(String query) {
        Locale tr = Locale.forLanguageTag("tr-TR");
        String q = query == null ? "" : query.trim().toLowerCase(tr);
        int shown = 0;
        for (Components.FactRow row : rows) {
            boolean visible = q.isEmpty() || row.text().toLowerCase(tr).contains(q);
            row.setVisible(visible);
            if (visible) shown++;
        }
        updateCounter(shown, rows.size());
        revalidate();
        repaint();
    }

    /* ------------------------------------------------------------------ */
    /* Düzenleme penceresi                                                 */
    /* ------------------------------------------------------------------ */
    private void openEditor() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Maddeleri Düzenle", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Theme.g2(g0);
                int w = getWidth(), h = getHeight();
                Theme.fillRound(g, 1, 1, w - 2, h - 2, 18,
                        new GradientPaint(0, 0, new Color(0x121830), 0, h, new Color(0x0A0E1A)));
                Theme.strokeRound(g, 1, 1, w - 2, h - 2, 18, Theme.alpha(Theme.ACCENT, 130), 1f);
                g.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(20, 22, 18, 22));

        JPanel head = Components.vbox();
        head.add(Components.label("MADDELERİ DÜZENLE", Theme.bold(10), Theme.ACCENT2));
        head.add(Components.vgap(3));
        head.add(Components.label("50 maddede ben", Theme.display(22), Theme.TEXT));
        head.add(Components.vgap(2));
        head.add(Components.label("Her satır bir madde. Boş bırakıp kaydedersen varsayılan liste geri gelir.",
                Theme.body(11), Theme.MUTED));

        JTextArea area = new JTextArea(String.join("\n", cfg.factList()));
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
        sp.setBorder(new LineBorder(Theme.alpha(Color.WHITE, 26), 1, true));
        sp.getViewport().setBackground(new Color(0x0B1020));
        sp.getVerticalScrollBar().setUnitIncrement(22);

        Components.RoundButton defaults = new Components.RoundButton("Varsayılana Dön", Components.Kind.GHOST);
        defaults.compact();
        defaults.addActionListener(e -> area.setText(String.join("\n", Data.FACTS)));

        Components.RoundButton cancel = new Components.RoundButton("İptal", Components.Kind.GHOST);
        cancel.compact();
        cancel.addActionListener(e -> dialog.dispose());

        Components.RoundButton save = new Components.RoundButton("Kaydet", Components.Kind.PRIMARY);
        save.compact();
        save.addActionListener(e -> {
            List<String> lines = new ArrayList<>();
            for (String s : area.getText().split("\\R")) {
                if (!s.isBlank()) lines.add(s.trim());
            }
            String[] arr = lines.toArray(new String[0]);
            cfg.facts = Arrays.equals(arr, Data.FACTS) ? "" : String.join("\n", lines);
            cfg.save();
            dialog.dispose();
            afterSave.run();
            toast.accept(lines.isEmpty() ? "Varsayılan maddeler geri yüklendi"
                    : lines.size() + " madde kaydedildi");
        });

        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
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
        dialog.setSize(700, 540);
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().registerKeyboardAction(
                (ActionEvent e) -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setVisible(true);
    }
}
