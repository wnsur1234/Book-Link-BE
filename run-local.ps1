param(
    [switch]$SkipRun
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFilePath = Join-Path $projectRoot ".env"

if (-not (Test-Path $envFilePath)) {
    throw ".env 파일을 찾을 수 없습니다: $envFilePath"
}

Write-Host "Loading environment variables from .env ..."

Get-Content $envFilePath | ForEach-Object {
    $line = $_.Trim()

    if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
        return
    }

    $pair = $line -split "=", 2
    if ($pair.Count -ne 2) {
        return
    }

    $name = $pair[0].Trim()
    $value = $pair[1].Trim()

    if (
        ($value.StartsWith('"') -and $value.EndsWith('"')) -or
        ($value.StartsWith("'") -and $value.EndsWith("'"))
    ) {
        $value = $value.Substring(1, $value.Length - 2)
    }

    [Environment]::SetEnvironmentVariable($name, $value, "Process")
}

Write-Host "Environment variables loaded."

if ($SkipRun) {
    Write-Host "SkipRun 옵션이 활성화되어 실행을 건너뜁니다."
    exit 0
}

Write-Host "Starting Spring Boot with Gradle..."
Push-Location $projectRoot
try {
    & ".\gradlew.bat" bootRun
}
finally {
    Pop-Location
}
