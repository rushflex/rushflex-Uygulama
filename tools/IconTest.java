import rushflex.Icons;
import rushflex.Theme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class IconTest {
    public static void main(String[] args) throws Exception {
        int s = 96;
        BufferedImage img = new BufferedImage(s * 4 + 50, s + 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x0B0F1C));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String[] names = {"github", "tiktok", "instagram", "discord"};
        for (int i = 0; i < names.length; i++) {
            g.drawImage(Icons.get(names[i], s, Color.WHITE), 10 + i * (s + 10), 20, null);
        }
        g.dispose();
        ImageIO.write(img, "png", new File("icons_test.png"));
        System.out.println("ok");
    }
}
