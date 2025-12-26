# Bootstrap Gradle for Windows PowerShell
# Downloads a specified Gradle distribution and runs the given Gradle command.
# Usage: .\bootstrap-gradle.ps1 build

param(
    [string]$GradleVersion = "8.6",
    [string[]]$GradleArgs = $("build")
)

$gradleZip = "gradle-$GradleVersion-bin.zip"
$downloadUrl = "https://services.gradle.org/distributions/$gradleZip"
$cacheDir = Join-Path $PSScriptRoot "\.gradle-bootstrap"
$extractDir = Join-Path $cacheDir "gradle-$GradleVersion"

if (-not (Test-Path $extractDir)) {
    Write-Host "Gradle $GradleVersion not found locally. Downloading..."
    New-Item -ItemType Directory -Force -Path $cacheDir | Out-Null
    $tmpZip = Join-Path $cacheDir $gradleZip
    Invoke-WebRequest -Uri $downloadUrl -OutFile $tmpZip
    Write-Host "Extracting Gradle..."
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory($tmpZip, $cacheDir)
    Remove-Item $tmpZip
}

$gradleCmd = Join-Path $extractDir "bin\gradle.bat"
if (-not (Test-Path $gradleCmd)) {
    Write-Error "Gradle executable not found after extraction: $gradleCmd"
    exit 1
}

# Run gradle with provided args
& $gradleCmd @GradleArgs
exit $LASTEXITCODE
