# PowerShell build and run script for Algorithm Visualizer
$ErrorActionPreference = "Stop"

$workspaceRoot = $PSScriptRoot
$srcDir = Join-Path $workspaceRoot "src"
$outDir = Join-Path $workspaceRoot "out"

# Find JDK tools (compatible with PowerShell 5.1 and newer)
$cmdJavac = Get-Command javac -ErrorAction SilentlyContinue
$javac = if ($cmdJavac) { $cmdJavac.Source } else { $null }

$cmdJava = Get-Command java -ErrorAction SilentlyContinue
$java = if ($cmdJava) { $cmdJava.Source } else { $null }

if (-not $javac) {
    $fallbackJdk = "C:\Program Files\Java\jdk-26.0.2\bin"
    if (Test-Path "$fallbackJdk\javac.exe") {
        $javac = "$fallbackJdk\javac.exe"
        $java = "$fallbackJdk\java.exe"
    } elseif (Test-Path "C:\Program Files\Java\latest\jdk-26\bin\javac.exe") {
        $javac = "C:\Program Files\Java\latest\jdk-26\bin\javac.exe"
        $java = "C:\Program Files\Java\latest\jdk-26\bin\java.exe"
    }
}

if (-not $javac) {
    Write-Error "Could not locate javac.exe. Please install JDK or set JAVA_HOME."
    exit 1
}

Write-Host "Using compiler: $javac" -ForegroundColor Cyan

# Create output directory
if (-not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

# Collect all .java source files
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName

Write-Host "Compiling $($javaFiles.Count) Java files into out/..." -ForegroundColor Yellow
& $javac -d $outDir $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed."
    exit 1
}

Write-Host "Compilation succeeded! Launching Algorithm Visualizer..." -ForegroundColor Green
& $java -cp $outDir com.visualizer.Main
