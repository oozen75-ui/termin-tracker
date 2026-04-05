#!/bin/bash

echo "=========================================="
echo "   Termin Tracker Uninstaller"
echo "   Version 1.0.1"
echo "=========================================="
echo ""
echo "Bu program Termin Tracker'i sisteminizden kaldıracak."
echo ""
read -p "Devam etmek için Enter'a basın..."

echo ""
echo "Kontrol ediliyor..."

# Debian/Ubuntu
if dpkg -l | grep -q termin-tracker; then
    echo "✓ Paket bulundu, kaldırılıyor..."
    sudo apt-get remove --purge -y termin-tracker
    echo "✓ Paket kaldırıldı"
fi

# Kullanıcı verileri
CONFIG_DIR="$HOME/.config/termin-tracker"
if [ -d "$CONFIG_DIR" ]; then
    echo "✓ Yapılandırma dizini bulundu: $CONFIG_DIR"
    rm -rf "$CONFIG_DIR"
    echo "✓ Yapılandırma silindi"
fi

# Masaüstü kısayolu
DESKTOP_FILE="$HOME/.local/share/applications/termin-tracker.desktop"
if [ -f "$DESKTOP_FILE" ]; then
    rm "$DESKTOP_FILE"
    echo "✓ Masaüstü kısayolu silindi"
fi

echo ""
echo "=========================================="
echo "   Kaldırma işlemi tamamlandı!"
echo "=========================================="
echo ""
echo "Termin Tracker sistemden kaldırıldı."
echo ""
read -p "Tamamlandı. Çıkmak için Enter'a basın..."
