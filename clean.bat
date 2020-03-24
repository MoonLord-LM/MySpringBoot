@echo off

cd "Client" && call :clean && cd ..
cd "Server" && call :clean && cd ..
cd "Starter" && call :clean && cd ..

pause
exit

:clean
    rmdir /S /Q "target"
    rmdir /S /Q "logs"
goto :eof
