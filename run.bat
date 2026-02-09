@echo off
echo 🚀 Starting SauceDemo Automation Tests...
echo =========================================

REM Clean previous reports
echo 🧹 Cleaning previous reports...
rmdir /s /q target\allure-results 2>nul
rmdir /s /q target\allure-report 2>nul

REM Run tests
echo 🎬 Running tests...
call mvn clean test

REM Check if tests ran successfully
if %errorlevel% equ 0 (
    echo ✅ Tests completed successfully!

    REM Generate Allure report
    echo 📊 Generating Allure report...
    call mvn allure:report

    REM Open report in browser
    echo 🔗 Opening report in browser...
    start chrome target\allure-report\index.html
) else (
    echo ❌ Tests failed!
    exit /b 1
)

echo =========================================
echo 🎉 Automation run completed!
pause