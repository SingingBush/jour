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

set JVM_ARGS=
set JVM_ARGS=%JVM_ARGS% -Dlog4j.configuration=file:%PROJECT_BUILD_HOME%\src\test\resources\log4j.xml

java %JVM_ARGS% -jar ..\target\jour-instrument-%VERSION%.jar  --config ..\src\test\resources\exceptionCatcher.jour.xml --src ..\target\test-classes --dst ..\target\test-iclasses-scripts

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in build
:endmark
ENDLOCAL
pause
