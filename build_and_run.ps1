# EscapeX: Build and Run PowerShell Script
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  EscapeX: Intelligent Escape Room Adventure" -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan

$javac = "javac"
$java = "java"

if (Test-Path "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe") {
    $javac = "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe"
    $java = "C:\Program Files\Java\jdk-26.0.1\bin\java.exe"
}

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "Compiling EscapeX..." -ForegroundColor Green
$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -d bin $files

if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Compilation Succeeded. Launching EscapeX GUI..." -ForegroundColor Green
    & $java -cp bin escapex.Main
} else {
    Write-Host "[ERROR] Compilation Failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
