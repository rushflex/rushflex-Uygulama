import rushflex.*;

import javax.swing.*;

public class LayoutProbe {
    public static void main(String[] args) {
        Config cfg = Config.load();
        JComponent[] pages = {
                new HomePage(cfg, i -> {}, s -> {}),
                new SkillsPage(),
                new ProjectsPage(s -> {}),
                new SocialPage(cfg, i -> {}, s -> {}),
                new FactsPage(cfg, () -> {}, s -> {}),
                new SettingsPage(cfg, () -> {}, s -> {})
        };
        int i = 0;
        for (JComponent p : pages) {
            p.setSize(930, 800);
            p.doLayout();
            System.out.println(i++ + " " + p.getClass().getSimpleName()
                    + " pref=" + p.getPreferredSize().width + "x" + p.getPreferredSize().height);
        }
        System.out.println("frame=" + java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
    }
}
