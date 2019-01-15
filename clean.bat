@echo off

cd "Client"
rmdir /S /Q "target"
rmdir /S /Q "logs"
del /F /S /Q "*.log"

cd "../Server"
rmdir /S /Q "target"
rmdir /S /Q "logs"
del /F /S /Q "*.log"

cd "../Starter"
rmdir /S /Q "target"
rmdir /S /Q "logs"
del /F /S /Q "*.log"

pause
exit