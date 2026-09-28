@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo  EscapeX: Intelligent Escape Room Adventure - Launcher
echo ========================================================

REM Look for modern JDK (JDK 17+)
set JAVA_CMD=java

if exist "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" (
    set JAVA_CMD="C:\Program Files\Java\jdk-26.0.1\bin\java.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set JAVA_CMD="%JAVA_HOME%\bin\java.exe"
    )
)

if not exist "bin\escapex\Main.class" (
    echo [INFO] Classes not compiled yet. Running compile.bat first...
    call compile.bat
)

echo Starting EscapeX GUI on Java Virtual Machine...
%JAVA_CMD% -cp bin escapex.Main

pause
