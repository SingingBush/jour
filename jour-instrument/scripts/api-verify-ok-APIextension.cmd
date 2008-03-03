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

java -cp %PROJECT_BUILD_HOME%\target\jour-instrument-%VERSION%.jar  net.sf.jour.SignatureVerify

set ARGS=--systempath
set ARGS=%ARGS% --src %PROJECT_BUILD_HOME%\target\test-classes
set ARGS=%ARGS% --signature %PROJECT_BUILD_HOME%\src\test\resources\net\sf\jour\signature\api-signature-change-interface.xml

set ARGS=%ARGS% --allowAPIextension true

java -cp %PROJECT_BUILD_HOME%\target\jour-instrument-%VERSION%.jar  net.sf.jour.SignatureVerify %ARGS%

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in test
:endmark
ENDLOCAL
pause
