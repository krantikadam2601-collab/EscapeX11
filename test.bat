@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo  EscapeX: Automated Self-Check and Verification Test
echo ========================================================

REM Locate JDK 17+
set JAVA_CMD=java
set JAVAC_CMD=javac

if exist "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" (
    set JAVA_CMD="C:\Program Files\Java\jdk-26.0.1\bin\java.exe"
    set JAVAC_CMD="C:\Program Files\Java\jdk-26.0.1\bin\javac.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set JAVA_CMD="%JAVA_HOME%\bin\java.exe"
        set JAVAC_CMD="%JAVA_HOME%\bin\javac.exe"
    )
)

if not exist "bin\escapex\SmokeTest.class" (
    echo [INFO] Compiling SmokeTest...
    %JAVAC_CMD% -encoding UTF-8 -cp bin -sourcepath src -d bin src/escapex/SmokeTest.java
)

echo Running headless verification test across all 5 sectors, 15 puzzles, inventory, and save/load...
echo.
%JAVA_CMD% -cp bin escapex.SmokeTest

echo.
pause
