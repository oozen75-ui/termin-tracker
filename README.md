# Termin Tracker v1.0.1

Almanya'da yaşayanlar için randevu takip ve yönetim sistemi.

## 🚀 Özellikler

- **Şehir/İlçe Seçimi:** Dropdown menüler ile Almanya şehirleri ve ilçeleri
- **Adres Yönetimi:** İnternet üzerinden otomatik veri çekme (OpenStreetMap API)
- **Offline Mod:** İnternet olmadan da çalışır
- **Modern UI:** Material Design 3 ile geliştirilmiş arayüz
- **Çapraz Platform:** Linux (.deb) ve Windows (.exe) desteği
- **Uninstaller:** Her iki platform için kaldırıcı dahil

## 🛠️ Teknolojiler (En Son LTS/Stabil)

| Bileşen | Versiyon |
|-----------|----------|
| Kotlin | 1.9.22 LTS |
| Compose Desktop | 1.6.2 |
| Gradle | 8.7 |
| **Node.js** | **24.x LTS** |
| Coroutines | 1.8.0 |

## 🏗️ Build

```bash
# Linux
./gradlew :desktop:packageDeb

# Windows
./gradlew.bat :desktop:packageExe
```

## 📦 Kurulum

### Linux (Ubuntu/Debian)
```bash
sudo dpkg -i termin-tracker_1.0.1-1_amd64.deb
```

### Windows
`TerminTracker-1.0.1.exe` dosyasını çalıştırın.

## 🗑️ Kaldırma

### Linux
```bash
sudo apt-get remove termin-tracker
# veya
./uninstall.sh
```

### Windows
```
Başlat Menüsü > Termin Tracker > Uninstall
# veya
uninstall.bat
```

## 🔧 Geliştirici

**Technic Genius GmbH** - 2026
