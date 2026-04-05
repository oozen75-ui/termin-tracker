@echo off
chcp 65001 > nul
echo ==========================================
echo    Termin Tracker Uninstallerecho    Version 1.0.1
echo ==========================================
echo.
echo Bu program Termin Tracker'i sisteminizden kaldıracak.echo.
pause

set "INSTALL_DIR=%LOCALAPPDATA%\TerminTracker"
set "START_MENU=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Termin Tracker"

echo.echo Kontrol ediliyor...

if exist "%INSTALL_DIR%" (
    echo Kurulum dizini bulundu: %INSTALL_DIR%
    echo Dosyalar siliniyor...
    rmdir /s /q "%INSTALL_DIR%"
    echo ✓ Kurulum dizini silindi
) else (
    echo ! Kurulum dizini bulunamadı
)

if exist "%START_MENU%" (
    echo Başlangıç menüsü kısayolları siliniyor...
    rmdir /s /q "%START_MENU%"
    echo ✓ Kısayollar silindi
)

echo.echo Registry temizleniyor...
reg delete "HKCU\Software\TerminTracker" /f > nul 2>&1
echo ✓ Registry temizlendi

echo.echo ==========================================
echo    Kaldırma işlemi tamamlandı!echo ==========================================
echo.echo Termin Tracker sistemden kaldırıldı.echo.
pause
