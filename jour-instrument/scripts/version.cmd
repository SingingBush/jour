@echo off
rem @version $Revision$ ($Author$)  $Date$

set VERSION=2.0.2-SNAPSHOT

set MAVEN2_REPO=%HOMEDRIVE%\%HOMEPATH%\.m2\repository

set PROJECT_BUILD_HOME=%~dp0
for /f %%i in ("%PROJECT_BUILD_HOME%..") do @set PROJECT_BUILD_HOME=%%~fi