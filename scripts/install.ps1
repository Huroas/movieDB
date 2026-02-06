[CmdletBinding()]
param(
    [string]$InstallDir = "$env:LOCALAPPDATA\movieDB",
    [string]$JarPath,
    [switch]$Run
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Resolve-JarPath {
    param([string]$BaseDir)

    $candidates = @(
        (Join-Path $BaseDir "movieDB.jar")
    )

    $preferred = Get-ChildItem -Path $BaseDir -Filter "movieDB*.jar" -File -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($preferred) {
        $candidates += $preferred.FullName
    }

    $fallback = Get-ChildItem -Path $BaseDir -Filter "*.jar" -File -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($fallback) {
        $candidates += $fallback.FullName
    }

    return $candidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $scriptDir

if (-not $JarPath) {
    $JarPath = Resolve-JarPath -BaseDir $scriptDir
    if (-not $JarPath) {
        $JarPath = Resolve-JarPath -BaseDir (Get-Location)
    }
    if (-not $JarPath -and $repoRoot) {
        $JarPath = Resolve-JarPath -BaseDir $repoRoot
    }
    if (-not $JarPath -and $repoRoot) {
        $JarPath = Resolve-JarPath -BaseDir (Join-Path $repoRoot "target")
    }
}

if (-not $JarPath -or -not (Test-Path $JarPath)) {
    Write-Error "Jar not found. Build with .\\mvnw.cmd -DskipTests package or pass -JarPath."
    exit 1
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java not found. Install Java 17+ and try again."
    exit 1
}

New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $InstallDir "data") -Force | Out-Null

$jarDest = Join-Path $InstallDir "movieDB.jar"
Copy-Item -Path $JarPath -Destination $jarDest -Force

$runPs1 = Join-Path $InstallDir "run.ps1"
@'
Set-Location -Path $PSScriptRoot
& java -jar "movieDB.jar" --spring.datasource.url=jdbc:sqlite:data/database.db
'@ | Set-Content -Path $runPs1 -Encoding ASCII

$runBat = Join-Path $InstallDir "run.bat"
@'
@echo off
cd /d "%~dp0"
java -jar "movieDB.jar" --spring.datasource.url=jdbc:sqlite:data/database.db
'@ | Set-Content -Path $runBat -Encoding ASCII

Write-Host "Installed to $InstallDir"
Write-Host "Run: $runPs1"

if ($Run) {
    Write-Host "Starting..."
    & $runPs1
}
