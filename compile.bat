@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo  EscapeX: Intelligent Escape Room Adventure - Compiler
echo ========================================================

REM Look for modern JDK (JDK 17+)
set JAVAC_CMD=javac

if exist "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" (
    set JAVAC_CMD="C:\Program Files\Java\jdk-26.0.1\bin\javac.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set JAVAC_CMD="%JAVA_HOME%\bin\javac.exe"
    )
)

echo Using Java Compiler: %JAVAC_CMD%

if not exist "bin" (
    mkdir "bin"
)

echo Compiling Java source files from src/...
%JAVAC_CMD% -encoding UTF-8 -d bin -sourcepath src src/escapex/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================================
    echo  [SUCCESS] Compilation Complete!
    echo  Run 'run.bat' to launch EscapeX!
    echo ========================================================
) else (
    echo.
    echo ========================================================
    echo  [ERROR] Compilation Failed. Please check the errors above.
    echo ========================================================
)

pause
