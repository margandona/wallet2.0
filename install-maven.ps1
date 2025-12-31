# Download Maven 3.9.6
$mvnVersion = "3.9.6"
$mvnDownloadUrl = "https://archive.apache.org/dist/maven/maven-3/$mvnVersion/binaries/apache-maven-$mvnVersion-bin.zip"
$mvnPath = "$env:ProgramFiles\Apache\maven"
$mvnZip = "$env:TEMP\maven-$mvnVersion.zip"

if (-not (Test-Path $mvnPath)) {
    Write-Host "Descargando Maven $mvnVersion..." -ForegroundColor Cyan
    Write-Host "From: $mvnDownloadUrl" -ForegroundColor Yellow
    
    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $mvnDownloadUrl -OutFile $mvnZip -UseBasicParsing
        
        Write-Host "✓ Maven descargado correctamente" -ForegroundColor Green
        Write-Host "Extrayendo..." -ForegroundColor Yellow
        
        $extractPath = "$env:ProgramFiles\Apache"
        if (-not (Test-Path $extractPath)) {
            New-Item -ItemType Directory -Path $extractPath | Out-Null
        }
        
        Add-Type -AssemblyName System.IO.Compression.FileSystem
        [System.IO.Compression.ZipFile]::ExtractToDirectory($mvnZip, $extractPath)
        
        Rename-Item -Path "$extractPath\apache-maven-$mvnVersion" -NewName "maven" -Force
        
        Write-Host "✓ Maven instalado en: $mvnPath" -ForegroundColor Green
        
        # Set environment variables
        [Environment]::SetEnvironmentVariable("M2_HOME", $mvnPath, "User")
        [Environment]::SetEnvironmentVariable("MAVEN_HOME", $mvnPath, "User")
        
        $pathValue = [Environment]::GetEnvironmentVariable("Path", "User")
        if ($pathValue -notlike "*$mvnPath\bin*") {
            [Environment]::SetEnvironmentVariable("Path", "$pathValue;$mvnPath\bin", "User")
        }
        
        Remove-Item -Path $mvnZip -Force
        
        Write-Host ""
        Write-Host "Configuración completada. Por favor reinicia PowerShell para que se apliquen los cambios." -ForegroundColor Yellow
        
    } catch {
        Write-Host "Error al descargar Maven: $_" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "Maven ya está instalado en: $mvnPath" -ForegroundColor Green
}
