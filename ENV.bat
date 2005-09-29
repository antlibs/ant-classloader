rem  @echo off
set jdk=%1%
set antversion=%2%
if NOT DEFINED jdk set jdk=1.5
if NOT DEFINED antversion set antversion=-
set PATH=
set PROJECT=jtools
set JAVA_PATH=
set ANT_PATH=
call HOME.bat %jdk% %antversion%

set DIR_BUILD=
set DIR_RELEASE=
if NOT DEFINED JAVA_PATH set JAVA_PATH=%JAVA_HOME%\bin
if NOT DEFINED ANT_PATH set ANT_PATH=%ANT_HOME%\bin


set CLASSPATH=%CHECKSTYLE_HOME%\checkstyle-all-3.5.jar;%SIMIAN_JAR%
set PATH=%JAVA_PATH%;%ANT_PATH%;%PATH%

set ANT_OPTS=-Dconfig.dir=%ANT_CHECKSTYLE_CFG_DIR% -Djava.dir=ant/src/main
@echo on