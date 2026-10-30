# RushFlex — Kişisel Portfolyo Uygulaması

Java (Swing / Java2D) ile yazılmış, tamamen özel çizilmiş bir masaüstü portfolyo uygulaması.
Harici UI kütüphanesi yok; tüm bileşenler, tema, ikonlar ve arka plan efektleri `src/rushflex`
altında elle çiziliyor.

## Sayfalar

| Sayfa | İçerik |
| --- | --- |
| Home | Tanıtım, öne çıkan projeler |
| Projects | Corenya Guard / TAB / Bot / Site kartları |
| Skills | Dil ve araç yetkinlik çubukları |
| Facts | Kısa bilgiler / istatistikler |
| Social | Discord, GitHub, Instagram, TikTok bağlantıları |
| Settings | Tema ve görünüm ayarları |

## Gereksinimler

- JDK 17 veya üzeri

## Derleme ve çalıştırma

```powershell
# derle
javac -encoding UTF-8 -d build/classes (Get-ChildItem -Recurse src\rushflex -Filter *.java).FullName

# çalıştır (ikonlar src/icons altından classpath ile okunur)
java -cp "build/classes;src" rushflex.RushFlexApp
```

`tools/` altındaki yardımcı sınıflar geliştirme içindir:

- `MakeIcon.java` — `assets/rushflex.ico` uygulama ikonunu üretir
- `IconTest.java` — ikon setini önizler
- `LayoutProbe.java` — yerleşim ölçümleri

## Kaynak yapısı

```
src/rushflex/
├── RushFlexApp.java   # ana pencere, sayfa yönlendirme
├── Theme.java         # renkler, fontlar, ölçüler
├── Backdrop.java      # arka plan efektleri (gradyan, cam)
├── Components.java    # kart, badge, buton, avatar bileşenleri
├── Icons.java         # SVG ikon yükleyici
├── Config.java        # ayar kaydı
├── Data.java          # proje / yetenek / alıntı içeriği
└── *Page.java         # sayfalar
```

## Notlar

`build/` ve `dist/` (gömülü JRE ile paketlenmiş çıktı) depoya dahil değildir.
