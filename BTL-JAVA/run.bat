@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo Dang khoi dong... (lan dau se tai thu vien, can Internet)
echo Mo trinh duyet tai http://localhost:8080 khi thay dong "Started BtlJavaApplication"
call mvnw.cmd spring-boot:run
pause
