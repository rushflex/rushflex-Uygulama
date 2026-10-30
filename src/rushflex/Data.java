package rushflex;

import java.awt.Color;

/** Uygulamanın içeriği. */
public final class Data {

    public record Project(String name, String desc, String[] tags, String url,
                          String glyph, Color tint, String badge) {}

    public record Skill(String name, int value, Color tint) {}

    public static final Project[] PROJECTS = {
            new Project("Corenya Guard",
                    "Minecraft sunucuları için gelişmiş koruma, moderasyon ve olay yönetimi eklentisi.",
                    new String[]{"Java", "Paper API", "Güvenlik"},
                    "https://github.com/rushflex", "shield", new Color(0x7C5CFF), "GUARD"),
            new Project("Corenya TAB",
                    "Tablist, scoreboard, header/footer ve placeholder yönetimini tek yerden yönetir.",
                    new String[]{"Java", "Gradle", "Config"},
                    "https://github.com/rushflex", "layers", new Color(0x22D3EE), "TAB"),
            new Project("Corenya Bot",
                    "Discord topluluğu için moderasyon, otomasyon ve eğlence komutları barındıran bot.",
                    new String[]{"Node.js", "discord.js", "MongoDB"},
                    "https://github.com/rushflex", "bot", new Color(0x5865F2), "BOT"),
            new Project("Corenya Site",
                    "Projelerin ve topluluğun tanıtıldığı modern, hızlı web vitrini.",
                    new String[]{"HTML", "CSS", "JavaScript"},
                    "https://github.com/rushflex", "globe", new Color(0x34D399), "WEB"),
    };

    public static final Skill[] LANGUAGES = {
            new Skill("Java", 90, new Color(0xF89820)),
            new Skill("JavaScript / Node.js", 82, new Color(0xF7DF1E)),
            new Skill("Kotlin", 70, new Color(0xA97BFF)),
            new Skill("SQL / MySQL", 72, new Color(0x22D3EE)),
            new Skill("HTML & CSS", 78, new Color(0xFF6B6B)),
            new Skill("TypeScript", 55, new Color(0x3178C6)),
    };

    public static final Skill[] TOOLS = {
            new Skill("Gradle", 85, new Color(0x34D399)),
            new Skill("Git & GitHub", 84, new Color(0xFF8A5B)),
            new Skill("Linux", 62, new Color(0xFBBF24)),
            new Skill("Discord.js", 80, new Color(0x5865F2)),
            new Skill("Redis", 58, new Color(0xEF4444)),
            new Skill("Docker", 45, new Color(0x38BDF8)),
    };

    public static final String[] QUOTES = {
            "Önce çalıştır, sonra güzelleştir.",
            "Bugünün hatası, yarının özelliği.",
            "Kod yazmak, düşünmeyi öğrenmenin en hızlı yolu.",
            "Küçük adımlar, büyük projeler.",
            "Dokümantasyon okumak da kod yazmaktır.",
            "Bir kere dene; hata verirse iki kere dene.",
            "Temiz kod, gelecekteki sana bir hediyedir.",
            "En iyi proje, bitirilmiş projedir.",
            "Hız her şey değil; doğruluk her şey.",
            "Öğrenmeyi bıraktığın gün, gelişmeyi bırakırsın.",
            "Klavye sesi, en iyi odak müziği.",
            "3 ms bile kullanıcı için fark eder.",
    };

    public static final String[] TICKER = {
            "JAVA", "KOTLIN", "NODE.JS", "DISCORD.JS", "GRADLE", "MYSQL",
            "REDIS", "LINUX", "GIT", "PAPER API", "16 YAŞINDA", "RUSH FLEX"
    };

    public static final String BIO =
            "16 yaşındayım ve yazılım geliştirmeyi hayatımın merkezine koydum. " +
            "Minecraft sunucuları için Java eklentileri, Discord toplulukları için Node.js botları " +
            "ve web tarafında arayüzler geliştiriyorum. Corenya ekosistemi benim imzam.";

    public static final String[] FACTS = {
            "16 yaşındayım ve kod yazmak hayatımın en büyük hobisi.",
            "Türkiye'de yaşıyorum; klavyem Türkçe Q.",
            "Kod yazmaya küçük yaşta merakla başladım, hâlâ her gün yeni bir şey öğreniyorum.",
            "GitHub'da @rushflex kullanıcı adıyla bulunabilirsin.",
            "En sevdiğim renk mor; bu uygulamanın teması da mor-turkuaz.",
            "Gece geç saatlerde kod yazmak bana iyi geliyor.",
            "Kulaklık + sözsüz beat = en verimli çalışma modum.",
            "Masamda her zaman bir bardak çay var.",
            "Bir şeyi bozup yeniden yapmayı öğrenmekten korkmuyorum.",
            "Hata mesajları benim en iyi öğretmenlerim.",
            "Takım çalışmasını severim; tek başıma üretmek de ayrı bir keyif.",
            "Yeni bir teknoloji görünce 'bunu nasıl yaparım?' diye düşünmeden edemiyorum.",
            "Hedefim sadece kod yazan değil, ürün çıkaran bir geliştirici olmak.",
            "Boş zamanlarımda oyun oynamayı ve müzik dinlemeyi severim.",
            "Kısaca: 16 yaşında, meraklı ve durmadan üreten biriyim.",
            "Minecraft plugin geliştiriyorum; sunucuların arkasındaki görünmez kahramanım.",
            "Java benim ana dilim, Kotlin'e de göz kırpıyorum.",
            "Discord botları için Node.js ve discord.js kullanıyorum.",
            "Projelerimi Gradle ile derliyorum.",
            "MySQL ve Redis ile veri tarafını yönetiyorum.",
            "Linux terminalinde kendimi evimde hissediyorum.",
            "Git olmadan kod yazmak, haritasız yolculuğa benziyor.",
            "Temiz kod ve düzenli klasör yapısı benim için önemli.",
            "Performans optimizasyonu en sevdiğim kısım: 3 ms bile fark eder.",
            "Paper/Spigot API'leriyle event tabanlı sistemler yazıyorum.",
            "Anti-cheat ve guard sistemleri ilgimi çekiyor.",
            "Yaptığım işlerde önce güvenlik, sonra hız geliyor.",
            "Web tarafında HTML/CSS ile başlayıp JavaScript'e geçtim.",
            "Yeni bir kütüphane öğrenmek için dokümantasyon okumayı severim.",
            "En büyük projem: Corenya ekosistemi.",
            "Corenya Guard: sunucuları koruyan bir güvenlik eklentisi.",
            "Corenya TAB: tablist ve scoreboard'u profesyonelce yönetir.",
            "Corenya Bot: Discord topluluğunu bir arada tutan bot.",
            "Corenya Site: projelerin vitrini.",
            "Hedefim Corenya'yı binlerce oyuncuya ulaştırmak.",
            "Açık kaynak paylaşmayı seviyorum; kod paylaştıkça güzelleşir.",
            "Bir gün kendi yazılım şirketimi kurmak istiyorum.",
            "Yaptığım işlerin kullanıcıya dokunması beni motive ediyor.",
            "2027 hedefim: 10 bin satırlık temiz bir açık kaynak projesi.",
            "Her projede bir öncekinden daha iyi olmaya çalışıyorum.",
            "Favori oyunum Minecraft: hem oynuyorum hem geliştiriyorum.",
            "Build yaparken detaylara takıntılıyım.",
            "Kahve değil çay; tartışmaya kapalı.",
            "Kod yazarken müzik şart, sözsüz olması tercihim.",
            "En sevdiğim klavye kısayolu Ctrl+Z.",
            "Bir bug'ı çözdüğüm anki his paha biçilemez.",
            "Bazen sadece 'çalışıyor' demek yeterli.",
            "Toplulukla vakit geçirmek, kod kadar keyifli.",
            "Bana ulaşmak için GitHub, TikTok ve Instagram linklerim uygulamada.",
            "Bu uygulama tamamen benim hakkımda ve hâlâ gelişiyor.",
    };
}
