<# 需在本地已启动 Auth / User / Order 后运行；超时 5 秒。#>
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$outDir = Join-Path $root "docs/openapi"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$targets = @(
    @{ Name = "store-auth-service";    Url = "http://localhost:19082/v3/api-docs" },
    @{ Name = "store-user-service";    Url = "http://localhost:19080/v3/api-docs" },
    @{ Name = "store-order-service";   Url = "http://localhost:19081/v3/api-docs" }
)

foreach ($t in $targets) {
    $dest = Join-Path $outDir ($t.Name + "-openapi.json")
    Write-Host "GET $($t.Url) -> $dest"
    try {
        Invoke-WebRequest -Uri $t.Url -OutFile $dest -TimeoutSec 5 -UseBasicParsing
    }
    catch {
        Write-Warning "跳过 $($t.Name)：$($_.Exception.Message)"
    }
}

Write-Host "完成。若没有文件或服务未启动属正常，请先启动 JVM 后再执行。"
