import rushflex.Theme;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/** Uygulama simgesini (PNG tabanlı .ico) üretir. */
public class MakeIcon {
    public static void main(String[] args) throws Exception {
        int[] sizes = {16, 24, 32, 48, 64, 128, 256};
        byte[][] png = new byte[sizes.length][];
        for (int i = 0; i < sizes.length; i++) {
            BufferedImage img = (BufferedImage) Theme.logoMark(sizes[i]);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bos);
            png[i] = bos.toByteArray();
        }

        File out = new File("assets/rushflex.ico");
        out.getParentFile().mkdirs();
        try (OutputStream os = new FileOutputStream(out)) {
            writeShort(os, 0);
            writeShort(os, 1);
            writeShort(os, sizes.length);
            int offset = 6 + 16 * sizes.length;
            for (int i = 0; i < sizes.length; i++) {
                int s = sizes[i];
                os.write(s >= 256 ? 0 : s);
                os.write(s >= 256 ? 0 : s);
                os.write(0);
                os.write(0);
                writeShort(os, 1);
                writeShort(os, 32);
                writeInt(os, png[i].length);
                writeInt(os, offset);
                offset += png[i].length;
            }
            for (byte[] data : png) os.write(data);
        }
        System.out.println("ico ok: " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
    }

    static void writeShort(OutputStream os, int v) throws Exception {
        os.write(v & 0xFF);
        os.write((v >> 8) & 0xFF);
    }

    static void writeInt(OutputStream os, int v) throws Exception {
        os.write(v & 0xFF);
        os.write((v >> 8) & 0xFF);
        os.write((v >> 16) & 0xFF);
        os.write((v >> 24) & 0xFF);
    }
}
