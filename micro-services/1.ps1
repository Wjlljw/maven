$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
# 强制底层 CMD 代码页切换到 UTF-8
chcp 65001 | Out-Null

Write-Host "`n=== 微服务自动化启动工具 ===" -ForegroundColor Green
Write-Host "当前运行环境：Windows PowerShell" -ForegroundColor Gray

# 2. 检查 Maven 环境变量
if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "【错误】: 未找到 'mvn' 命令！请检查 Maven 是否安装并已配置到系统环境变量 Path 中。" -ForegroundColor Red
    pause
    exit
}

# 3. 定义服务配置 (顺序：折扣 -> 库存 -> 订单)
$services = @(
    @{ Name = "discount-service"; Port = 8082 },
    @{ Name = "stock-service"; Port = 8083 },
    @{ Name = "order-service"; Port = 8081 }
)

# 获取脚本所在的根目录
$baseDir = $PSScriptRoot
if ([string]::IsNullOrEmpty($baseDir)) { $baseDir = Get-Location }

Write-Host "准备启动 $($services.Count) 个服务，请稍候...`n" -ForegroundColor Cyan

# 4. 循环启动流程
foreach ($service in $services) {
    $sName = $service.Name
    $sPort = $service.Port
    
    Write-Host "------------------------------------------------" -ForegroundColor White
    Write-Host "正在启动：$sName" -ForegroundColor Green
    Write-Host "目标端口：$sPort" -ForegroundColor Gray
    
    $targetDir = Join-Path $baseDir $sName
    if (!(Test-Path $targetDir)) {
        Write-Host "【警告】: 找不到文件夹 $targetDir，已跳过该服务。" -ForegroundColor Red
        continue
    }

    # 在新窗口中启动，并设置窗口标题
    Start-Process powershell.exe -WorkingDirectory $targetDir -ArgumentList "-NoExit", "-Command", "chcp 65001; title $sName ; mvn spring-boot:run"
    
    Write-Host "等待 15 秒确保服务初始化..." -ForegroundColor Yellow
    Start-Sleep -Seconds 15
}

Write-Host "`n=== 正在检查各服务健康状态 ===" -ForegroundColor Cyan

# 5. 健康检查
foreach ($service in $services) {
    $p = $service.Port
    $n = $service.Name
    try {
        $url = "http://localhost:$p/actuator/health"
        # 忽略 IE 首次运行配置，强制抓取数据
        $resp = Invoke-WebRequest -Uri $url -TimeoutSec 5 -UseBasicParsing
        if ($resp.Content -match "UP") {
            Write-Host " [正常] $n (端口: $p)" -ForegroundColor Green
        } else {
            Write-Host " [异常] $n (端口: $p) - 状态非 UP" -ForegroundColor Yellow
        }
    } catch {
        Write-Host " [失败] $n (端口: $p) - 无法访问接口" -ForegroundColor Red
    }
}

Write-Host "`n所有启动指令已发出。如果某个服务显示[失败]，请去对应的黑窗口查看日志。" -ForegroundColor Cyan
Write-Host "测试命令: curl 'http://localhost:8081/order/create?itemName=MacBook&quantity=2'" -ForegroundColor Gray
pause