@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM
@REM   https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Maven Wrapper Script for Windows - version 3.2.0

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0") ELSE (SET "BASE_DIR=%__MVNW_ARG0_NAME__%..")
@SET WRAPPER_JAR="%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
@SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%BASE_DIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    @IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@IF EXIST %WRAPPER_JAR% (
    @REM wrapper jar already present
) ELSE (
    @ECHO Downloading maven-wrapper.jar ...
    @powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile %WRAPPER_JAR%"
)

@IF "%JAVA_HOME%"=="" (
    @ECHO.
    @ECHO ERROR: JAVA_HOME not found in your environment.
    @ECHO Please set the JAVA_HOME variable in your environment to match the
    @ECHO location of your Java installation.
    @ECHO.
    GOTO error
)

@SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
@IF NOT EXIST "%JAVA_EXE%" (
    @ECHO.
    @ECHO ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
    @ECHO.
    GOTO error
)

@SET MAVEN_PROJECTBASEDIR=%BASE_DIR%
@SET MAVEN_CMD_LINE_ARGS=%MAVEN_CONFIG% %*

"%JAVA_EXE%" ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.home=%M2_HOME%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*

@IF "%ERRORLEVEL%"=="0" GOTO end

:error
@SET ERROR_CODE=%ERRORLEVEL%
@ECHO.
@ECHO ERROR: Maven execution failed. Error code: %ERROR_CODE%
@ECHO.
EXIT /B %ERROR_CODE%

:end
EXIT /B 0
