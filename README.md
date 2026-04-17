# Termin Tracker v2.0.0

Almanya'da yaşayanlar için kapsamlı randevu takip ve yönetim sistemi.

## 🚀 Özellikler

### Randevu Yönetimi
- **Kategorili Randevu Türleri:**
  - 🏛️ Behörde: KVR, Bürgeramt, BMV (Kfz-Zulassung), Ausländerbehörde
  - 🏥 Sağlık: Hausarzt, Facharzt, Zahnarzt, Krankenkasse
  - 💰 Finans: Bank, Steuerberater
  - 📝 Diğer: Özel randevular

### Akıllı Bildirimler
- **Çoklu Kanal Desteği:** Email, Telegram, Desktop bildirimleri
- **Takvim Entegrasyonu:**
  - 📅 Google Calendar export
  - 📅 Outlook Calendar export  
  - 📅 iCal (.ics) dosya export
- **Otomatik Hatırlatmalar:** Randevu öncesi bildirimler

### Modern UI
- **Material Design 3:** Güncel Android/iOS standartlarında arayüz
- **Çoklu Dil Desteği:** 
  - 🇩🇪 **Almanca (Varsayılan)**
  - 🇬🇧 İngilizce
  - 🇹🇷 Türkçe
- **Karanlık/Aydınlık Tema:** Sistem temasına uyumlu

### Dil Ayarları
Sistem varsayılan olarak **Almanca** dilinde çalışır. Dil değiştirmek için:
1. Ayarlar (Settings) menüsüne gidin
2. Dil (Language) seçeneğinden istediğiniz dili seçin
3. Değişiklikler otomatik uygulanır

### Veri Yönetimi
- **Offline Çalışma:** İnternet olmadan tam fonksiyonellik
- **Güvenli Depolama:** Yerel SQLite veritabanı
- **Yedekleme:** JSON export/import
- **Loglama:** Detaylı hata ve olay kayıtları

## 🛠️ Teknolojiler

| Bileşen | Versiyon | Açıklama |
|---------|----------|----------|
| Kotlin | 1.9.22 | Modern JVM dili |
| Compose Desktop | 1.6.2 | UI framework'ü |
| Gradle | 8.7 | Build sistemi |
| SQLite | 3.45.1 | Yerel veritabanı |
| Coroutines | 1.8.0 | Asenkron programlama |
| OkHttp | 4.12.0 | HTTP client |

## 📦 Platform Desteği

- ✅ Linux (.deb, .rpm)
- ✅ Windows (.exe)
- ✅ macOS (.dmg) - Deneysel

## 🚀 Hızlı Başlangıç

### 1. İndirme
Releases sayfasından işletim sisteminize uygun sürümü indirin.

### 2. Kurulum

#### Linux (Ubuntu/Debian)
```bash
sudo dpkg -i termin-tracker_2.0.0-1_amd64.deb
sudo apt-get install -f  # Eksik bağımlılıkları kur
```

#### Windows
`TerminTracker-2.0.0.exe` dosyasını çalıştırın.

#### macOS
`TerminTracker-2.0.0.dmg` dosyasını açın ve Applications klasörüne sürükleyin.

### 3. İlk Çalıştırma
- Uygulamayı başlatın
- Dil tercihinizi seçin
- İlk randevunuzu ekleyin
- Google Calendar entegrasyonunu yapılandırın (opsiyonel)

## 🧪 Test

```bash
./gradlew :common:test          # Unit testleri çalıştır
./gradlew :desktop:test         # Desktop testleri
./gradlew test                  # Tüm testleri çalıştır
```

## 🏗️ Build

### Gereksinimler
- JDK 17+
- Kotlin 1.9+
- Gradle 8.7+

### Build Komutları

```bash
# Linux paketi
./gradlew :desktop:packageDeb

# Windows paketi
./gradlew :desktop:packageExe

# macOS paketi  
./gradlew :desktop:packageDmg

# Tüm platformlar
./gradlew :desktop:package
```

## 📋 Sürüm Geçmişi

### v2.0.0 (17 Nisan 2026)
- 🎉 İlk stable release
- ✨ 10+ yeni randevu türü eklendi
- 📅 Google/Outlook Calendar entegrasyonu
- 🧪 Unit test coverage
- 🐛 Global exception handling
- 📝 Detaylı loglama sistemi

### v1.1.0 (17 Nisan 2026)
- ✨ Yeni randevu türleri (BMV, Ausländerbehörde)
- 📅 Takvim entegrasyonu (iCal, Google, Outlook)
- 🏷️ Kategori bazlı randevu yönetimi

### v1.0.2 (17 Nisan 2026)
- 🐛 Hata yönetimi iyileştirildi
- 📝 Loglama sistemi eklendi
- ℹ️ Uygulama bilgisi entegrasyonu

### v1.0.1 (9 Nisan 2026)
- 🔧 Build düzeltmeleri
- 🪟 Windows uyumluluğu
- 👤 Kişisel bilgi yönetimi

### v1.0.0 (6 Nisan 2026)
- 🎉 İlk beta sürüm
- 🗓️ Temel randevu takibi
- 🔍 Online randevu arama
- 🔔 Bildirim sistemi

## 👨‍💻 Geliştirme

### Katkıda Bulunma
1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

### Proje Yapısı
```
termin-tracker/
├── java/
│   ├── common/          # Paylaşılan kod
│   │   ├── src/main/kotlin/
│   │   │   ├── calendar/      # Takvim entegrasyonu
│   │   │   ├── database/      # SQLite yönetimi
│   │   │   ├── model/         # Data class'ları
│   │   │   ├── network/       # API istekleri
│   │   │   ├── notification/  # Bildirim servisleri
│   │   │   ├── repository/    # Veri erişim
│   │   │   ├── scheduler/     # Zamanlayıcı
│   │   │   └── utils/         # Yardımcı fonksiyonlar
│   │   └── src/test/kotlin/   # Unit testleri
│   └── desktop/         # Desktop GUI
│       └── src/main/kotlin/
├── uninstaller/
└── README.md
```

## 📝 Lisans

Bu proje GNU General Public License v3.0 altında lisanslanmıştır.

## 🙏 Teşekkürler

- [JetBrains](https://jetbrains.com) - Kotlin ve Compose Desktop
- [OpenStreetMap](https://openstreetmap.org) - Adres verileri
- Tüm katkıda bulunanlar ❤️

---

<p align="center">
  <strong>Made with ❤️ in Berlin</strong><br>
  <sub>© 2026 Technic Genius GmbH</sub>
</p>