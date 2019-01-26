@echo off

cd "Client" && call :clean && cd ..
cd "Server" && call :clean && cd ..
cd "Starter" && call :clean && cd ..

pause
exit

:clean - 清除编译结果和日志
    rmdir /S /Q "target"
    rmdir /S /Q "logs"
    del /F /S /Q "*.log"
goto :eof