package rushflex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Kullanıcı bilgileri — %APPDATA%/RushFlex/config.properties içinde saklanır. */
public class Config {

    public String name      = "Rushflex";
    public String tagline   = "16 yaşında geliştirici";
    public String github    = "https://github.com/rushflex";
    public String tiktok    = "https://www.tiktok.com/@_wqrzsx";
    public String instagram = "";
    public String discord   = "https://discord.gg/corenya";
    public int accent       = 0;
    public boolean animations = true;
    /** Kullanıcının düzenlediği 50 madde (satır satır). Boşsa varsayılan liste kullanılır. */
    public String facts      = "";
    /** Kullanıcının düzenlediği kısa biyografi. Boşsa varsayılan metin kullanılır. */
    public String bio        = "";

    public static File folder() {
        String appData = System.getenv("APPDATA");
        if (appData == null || appData.isEmpty()) appData = System.getProperty("user.home");
        File f = new File(appData, "RushFlex");
        if (!f.exists()) f.mkdirs();
        return f;
    }

    public static File file() {
        return new File(folder(), "config.properties");
    }

    public static Config load() {
        Config c = new Config();
        File f = file();
        if (!f.exists()) {
            c.save();
            return c;
        }
        Properties p = new Properties();
        try (InputStreamReader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            p.load(r);
        } catch (Exception ignored) {
        }
        c.name = p.getProperty("name", c.name);
        c.tagline = p.getProperty("tagline", c.tagline);
        c.github = p.getProperty("github", c.github);
        c.tiktok = p.getProperty("tiktok", c.tiktok);
        c.instagram = p.getProperty("instagram", c.instagram);
        c.discord = p.getProperty("discord", c.discord);
        c.facts = p.getProperty("facts", "");
        c.bio = p.getProperty("bio", "");
        try { c.accent = Integer.parseInt(p.getProperty("accent", "0")); } catch (Exception ignored) {}
        c.animations = !"false".equalsIgnoreCase(p.getProperty("animations", "true"));
        return c;
    }

    public void save() {
        Properties p = new Properties();
        p.setProperty("name", name);
        p.setProperty("tagline", tagline);
        p.setProperty("github", github);
        p.setProperty("tiktok", tiktok);
        p.setProperty("instagram", instagram);
        p.setProperty("discord", discord);
        p.setProperty("facts", facts);
        p.setProperty("bio", bio);
        p.setProperty("accent", String.valueOf(accent));
        p.setProperty("animations", String.valueOf(animations));
        File f = file();
        try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            p.store(w, "RushFlex - kisisel tanitim uygulamasi");
        } catch (Exception e) {
            System.err.println("Ayar kaydedilemedi: " + e.getMessage());
        }
    }

    /** Kısa biyografi (kullanıcı düzenlediyse onu, yoksa varsayılanı döndürür). */
    public String bioText() {
        return bio == null || bio.isBlank() ? Data.BIO : bio;
    }

    /** 50 madde listesi (kullanıcı düzenlediyse onu, yoksa varsayılanı döndürür). */
    public String[] factList() {
        if (facts == null || facts.isBlank()) return Data.FACTS;
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String s : facts.split("\\R")) {
            if (!s.isBlank()) out.add(s.trim());
        }
        return out.isEmpty() ? Data.FACTS : out.toArray(new String[0]);
    }

    /** Kullanıcının girdiği @kullanici / kullanici / tam URL'yi düzgün URL'ye çevirir. */
    public static String normalize(String platform, String value) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) return "";
        if (v.contains("://")) return v;
        if (v.startsWith("@")) v = v.substring(1);
        switch (platform) {
            case "github":    return "https://github.com/" + v;
            case "tiktok":    return "https://www.tiktok.com/@" + v;
            case "instagram": return "https://www.instagram.com/" + v;
            case "discord":
                if (v.startsWith("discord.gg/") || v.startsWith("discord.com/")) return "https://" + v;
                return "https://discord.gg/" + v;
            default: return v;
        }
    }

    /** URL'den kısa görünüm: github.com/rushflex */
    public static String pretty(String url) {
        if (url == null) return "";
        String v = url.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
        if (v.endsWith("/")) v = v.substring(0, v.length() - 1);
        return v;
    }

    /** @kullanici biçimi (TikTok/Instagram için). */
    public static String handle(String url) {
        String p = pretty(url);
        int slash = p.lastIndexOf('/');
        String last = slash >= 0 ? p.substring(slash + 1) : p;
        if (last.startsWith("@")) return last;
        return last.isEmpty() ? p : (p.contains("tiktok") || p.contains("instagram") ? "@" + last : p);
    }
}
