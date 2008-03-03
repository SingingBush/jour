@echo off
rem @version $Revision$ ($Author$)  $Date$
SETLOCAL

call %~dp0version.cmd %*
if errorlevel 1 (
    echo Error calling version.cmd
    endlocal
    pause
    exit /b 1
)

set ARGS=--systempath
set ARGS=%ARGS% --src %PROJECT_BUILD_HOME%\target\test-classes
set ARGS=%ARGS% --packages uut.signature

java -cp %PROJECT_BUILD_HOME%\target\jour-instrument-%VERSION%.jar  net.sf.jour.SignatureGenerator %ARGS%

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in test
:endmark
ENDLOCAL
pause
