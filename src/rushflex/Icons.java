package rushflex;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Marka ikonlarını (SVG) yükleyip istenen renk/boyutta raster'lar. */
public final class Icons {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();
    private static final Map<String, Path2D> PATHS = new HashMap<>();

    public static BufferedImage get(String name, int size, Color color) {
        String key = name + "|" + size + "|" + color.getRGB();
        BufferedImage img = CACHE.get(key);
        if (img != null) return img;

        Path2D path = PATHS.computeIfAbsent(name, Icons::load);
        img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        if (path != null) {
            Graphics2D g = Theme.g2(img.createGraphics());
            double s = size / 24.0;
            g.scale(s, s);
            g.setColor(color);
            g.fill(path);
            g.dispose();
        }
        CACHE.put(key, img);
        return img;
    }

    /** Marka rengiyle küçük resim (yoksa null). */
    public static ImageIcon icon(String name, int size, Color color) {
        return new ImageIcon(get(name, size, color));
    }

    private static Path2D load(String name) {
        try (InputStream in = Icons.class.getResourceAsStream("/icons/" + name + ".svg")) {
            if (in == null) return null;
            String svg = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("\\sd=\"([^\"]+)\"").matcher(svg);
            if (!m.find()) return null;
            return SvgPath.parse(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }

    /** Simple Icons path verisini (M/L/H/V/C/S/Q/T/Z) Path2D'ye çevirir. */
    static final class SvgPath {
        private static final Pattern TOKEN = Pattern.compile(
                "[MmLlHhVvCcSsQqTtAaZz]|[-+]?(?:\\d*\\.\\d+|\\d+\\.?)(?:[eE][-+]?\\d+)?");

        static Path2D.Double parse(String d) {
            List<String> t = new ArrayList<>();
            Matcher m = TOKEN.matcher(d);
            while (m.find()) t.add(m.group());

            Path2D.Double p = new Path2D.Double();
            int i = 0;
            char cmd = 0;
            double cx = 0, cy = 0, sx = 0, sy = 0;
            double pcx = 0, pcy = 0;
            char prev = 0;

            while (i < t.size()) {
                String tok = t.get(i);
                if (tok.length() == 1 && Character.isLetter(tok.charAt(0))) {
                    cmd = tok.charAt(0);
                    i++;
                }
                if (i >= t.size()) break;

                boolean rel = Character.isLowerCase(cmd);
                char u = Character.toUpperCase(cmd);
                try {
                    switch (u) {
                        case 'M': {
                            double x = n(t, i++), y = n(t, i++);
                            if (rel) { x += cx; y += cy; }
                            p.moveTo(x, y);
                            cx = x; cy = y; sx = x; sy = y;
                            cmd = rel ? 'l' : 'L';
                            break;
                        }
                        case 'L': {
                            double x = n(t, i++), y = n(t, i++);
                            if (rel) { x += cx; y += cy; }
                            p.lineTo(x, y);
                            cx = x; cy = y;
                            break;
                        }
                        case 'H': {
                            double x = n(t, i++);
                            if (rel) x += cx;
                            p.lineTo(x, cy);
                            cx = x;
                            break;
                        }
                        case 'V': {
                            double y = n(t, i++);
                            if (rel) y += cy;
                            p.lineTo(cx, y);
                            cy = y;
                            break;
                        }
                        case 'C': {
                            double x1 = n(t, i++), y1 = n(t, i++), x2 = n(t, i++), y2 = n(t, i++), x = n(t, i++), y = n(t, i++);
                            if (rel) { x1 += cx; y1 += cy; x2 += cx; y2 += cy; x += cx; y += cy; }
                            p.curveTo(x1, y1, x2, y2, x, y);
                            pcx = x2; pcy = y2; cx = x; cy = y;
                            prev = 'C';
                            break;
                        }
                        case 'S': {
                            double x2 = n(t, i++), y2 = n(t, i++), x = n(t, i++), y = n(t, i++);
                            if (rel) { x2 += cx; y2 += cy; x += cx; y += cy; }
                            double x1 = cx, y1 = cy;
                            if (prev == 'C' || prev == 'S') { x1 = 2 * cx - pcx; y1 = 2 * cy - pcy; }
                            p.curveTo(x1, y1, x2, y2, x, y);
                            pcx = x2; pcy = y2; cx = x; cy = y;
                            prev = 'S';
                            break;
                        }
                        case 'Q': {
                            double x1 = n(t, i++), y1 = n(t, i++), x = n(t, i++), y = n(t, i++);
                            if (rel) { x1 += cx; y1 += cy; x += cx; y += cy; }
                            p.quadTo(x1, y1, x, y);
                            pcx = x1; pcy = y1; cx = x; cy = y;
                            prev = 'Q';
                            break;
                        }
                        case 'T': {
                            double x = n(t, i++), y = n(t, i++);
                            if (rel) { x += cx; y += cy; }
                            double x1 = cx, y1 = cy;
                            if (prev == 'Q' || prev == 'T') { x1 = 2 * cx - pcx; y1 = 2 * cy - pcy; }
                            p.quadTo(x1, y1, x, y);
                            pcx = x1; pcy = y1; cx = x; cy = y;
                            prev = 'T';
                            break;
                        }
                        case 'A': {
                            double rx = n(t, i++), ry = n(t, i++), rot = n(t, i++);
                            boolean large = n(t, i++) != 0;
                            boolean sweep = n(t, i++) != 0;
                            double x = n(t, i++), y = n(t, i++);
                            if (rel) { x += cx; y += cy; }
                            arcTo(p, cx, cy, rx, ry, rot, large, sweep, x, y);
                            cx = x; cy = y;
                            prev = 'A';
                            break;
                        }
                        case 'Z':
                            p.closePath();
                            cx = sx; cy = sy;
                            prev = 'Z';
                            if (i < t.size()) {
                                String nx = t.get(i);
                                if (!(nx.length() == 1 && Character.isLetter(nx.charAt(0)))) {
                                    // Z'den sonra sayı gelirse yeni alt path başlat
                                    p.moveTo(cx, cy);
                                }
                            }
                            break;
                        default:
                            i++;
                            break;
                    }
                } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                    return p;
                }
            }
            return p;
        }

        private static double n(List<String> t, int idx) {
            if (idx >= t.size()) throw new IndexOutOfBoundsException();
            return Double.parseDouble(t.get(idx));
        }

        /** SVG yay (A/a) komutunu kübik Bezier'lere çevirir. */
        private static void arcTo(Path2D.Double p, double x1, double y1, double rx, double ry,
                                  double rotDeg, boolean largeArc, boolean sweep,
                                  double x2, double y2) {
            if (rx == 0 || ry == 0) {
                p.lineTo(x2, y2);
                return;
            }
            double phi = Math.toRadians(rotDeg % 360);
            double cosPhi = Math.cos(phi), sinPhi = Math.sin(phi);
            double dx = (x1 - x2) / 2.0, dy = (y1 - y2) / 2.0;
            double x1p = cosPhi * dx + sinPhi * dy;
            double y1p = -sinPhi * dx + cosPhi * dy;

            rx = Math.abs(rx);
            ry = Math.abs(ry);
            double lambda = (x1p * x1p) / (rx * rx) + (y1p * y1p) / (ry * ry);
            if (lambda > 1) {
                double s = Math.sqrt(lambda);
                rx *= s;
                ry *= s;
            }

            double rx2 = rx * rx, ry2 = ry * ry;
            double num = rx2 * ry2 - rx2 * y1p * y1p - ry2 * x1p * x1p;
            double den = rx2 * y1p * y1p + ry2 * x1p * x1p;
            double coef = (largeArc != sweep ? 1 : -1) * Math.sqrt(Math.max(0, num / den));
            double cxp = coef * (rx * y1p / ry);
            double cyp = coef * (-ry * x1p / rx);
            double cx = cosPhi * cxp - sinPhi * cyp + (x1 + x2) / 2;
            double cy = sinPhi * cxp + cosPhi * cyp + (y1 + y2) / 2;

            double ux = (x1p - cxp) / rx, uy = (y1p - cyp) / ry;
            double vx = (-x1p - cxp) / rx, vy = (-y1p - cyp) / ry;
            double a1 = Math.atan2(uy, ux);
            double delta = Math.atan2(ux * vy - uy * vx, ux * vx + uy * vy);
            if (!sweep && delta > 0) delta -= Math.PI * 2;
            if (sweep && delta < 0) delta += Math.PI * 2;

            int segs = Math.max(1, (int) Math.ceil(Math.abs(delta) / (Math.PI / 2)));
            double step = delta / segs;
            double t = 4.0 / 3.0 * Math.tan(step / 4);
            double curX = x1, curY = y1;

            for (int i = 0; i < segs; i++) {
                double a2 = a1 + step;
                double cosA1 = Math.cos(a1), sinA1 = Math.sin(a1);
                double cosA2 = Math.cos(a2), sinA2 = Math.sin(a2);

                double ex2 = cx + cosPhi * rx * cosA2 - sinPhi * ry * sinA2;
                double ey2 = cy + sinPhi * rx * cosA2 + cosPhi * ry * sinA2;

                double d1x = -rx * sinA1 * t, d1y = ry * cosA1 * t;
                double d2x = -rx * sinA2 * t, d2y = ry * cosA2 * t;

                double c1x = curX + cosPhi * d1x - sinPhi * d1y;
                double c1y = curY + sinPhi * d1x + cosPhi * d1y;
                double c2x = ex2 - (cosPhi * d2x - sinPhi * d2y);
                double c2y = ey2 - (sinPhi * d2x + cosPhi * d2y);

                p.curveTo(c1x, c1y, c2x, c2y, ex2, ey2);
                curX = ex2;
                curY = ey2;
                a1 = a2;
            }
            if (Math.abs(curX - x2) > 1e-4 || Math.abs(curY - y2) > 1e-4) p.lineTo(x2, y2);
        }
    }
}
