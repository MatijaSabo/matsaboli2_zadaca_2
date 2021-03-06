@REM ----------------------------------------------------------------------------
@REM Copyright 2001-2004 The Apache Software Foundation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM ----------------------------------------------------------------------------
@REM

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup
if exist %BASEDIR%\bin\setenv.bat call %BASEDIR%\bin\setenv.bat

if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=..\lib

set CLASSPATH="%BASEDIR%"\conf;"%REPO%"\james-server-cli-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\commons-cli-1.2.jar;"%REPO%"\james-server-core-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\apache-mailet-api-2.5.1-20140218.040810-386.jar;"%REPO%"\apache-mailet-base-2.5.1-20140218.040823-369.jar;"%REPO%"\commons-io-2.4.jar;"%REPO%"\geronimo-javamail_1.4_mail-1.8.3.jar;"%REPO%"\james-server-data-api-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-dnsservice-library-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-filesystem-api-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\apache-jsieve-manager-api-0.6-20140218.041539-796.jar;"%REPO%"\javax.inject-1.jar;"%REPO%"\james-server-lifecycle-api-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\commons-configuration-1.9.jar;"%REPO%"\james-server-mailetcontainer-api-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-queue-api-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-spring-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\apache-james-mailbox-api-0.6-20140218.041948-308.jar;"%REPO%"\apache-james-mailbox-tool-0.6-20140218.042753-274.jar;"%REPO%"\geronimo-annotation_1.0_spec-1.1.1.jar;"%REPO%"\protocols-api-1.6.3.jar;"%REPO%"\spring-core-3.1.2.RELEASE.jar;"%REPO%"\spring-asm-3.1.2.RELEASE.jar;"%REPO%"\spring-beans-3.1.2.RELEASE.jar;"%REPO%"\spring-context-3.1.2.RELEASE.jar;"%REPO%"\spring-expression-3.1.2.RELEASE.jar;"%REPO%"\spring-web-3.1.2.RELEASE.jar;"%REPO%"\commons-collections-3.2.1.jar;"%REPO%"\log4j-1.2.17.jar;"%REPO%"\geronimo-jpa_2.0_spec-1.1.jar;"%REPO%"\slf4j-api-1.7.2.jar;"%REPO%"\slf4j-log4j12-1.7.2.jar;"%REPO%"\jcl-over-slf4j-1.7.2.jar;"%REPO%"\commons-daemon-1.0.10.jar;"%REPO%"\camel-core-2.10.3.jar;"%REPO%"\camel-spring-2.10.3.jar;"%REPO%"\spring-aop-3.1.2.RELEASE.jar;"%REPO%"\xbean-spring-3.12.jar;"%REPO%"\commons-logging-1.0.3.jar;"%REPO%"\spring-jms-3.1.2.RELEASE.jar;"%REPO%"\aopalliance-1.0.jar;"%REPO%"\spring-orm-3.1.2.RELEASE.jar;"%REPO%"\spring-tx-3.1.2.RELEASE.jar;"%REPO%"\spring-jdbc-3.1.2.RELEASE.jar;"%REPO%"\james-server-mailetcontainer-camel-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\geronimo-annotation_1.1_spec-1.0.1.jar;"%REPO%"\james-server-mailbox-adapter-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\apache-james-mailbox-store-0.6-20140218.042009-306.jar;"%REPO%"\apache-james-mailbox-maildir-0.6-20140218.042721-278.jar;"%REPO%"\james-server-dnsservice-api-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-dnsservice-dnsjava-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\dnsjava-2.1.1.jar;"%REPO%"\james-server-protocols-library-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\protocols-netty-1.6.3.jar;"%REPO%"\netty-3.3.1.Final.jar;"%REPO%"\james-server-util-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-data-library-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-data-ldap-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-protocols-smtp-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\protocols-smtp-1.6.3.jar;"%REPO%"\apache-jspf-resolver-1.0.0.jar;"%REPO%"\james-server-protocols-imap4-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-protocols-lmtp-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\protocols-lmtp-1.6.3.jar;"%REPO%"\james-server-protocols-pop3-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\protocols-pop3-1.6.3.jar;"%REPO%"\james-server-fetchmail-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-queue-file-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-queue-jms-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\geronimo-jms_1.1_spec-1.1.1.jar;"%REPO%"\james-server-queue-activemq-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\activemq-core-5.7.0.jar;"%REPO%"\kahadb-5.7.0.jar;"%REPO%"\activemq-protobuf-1.1.jar;"%REPO%"\mqtt-client-1.3.jar;"%REPO%"\hawtdispatch-transport-1.11.jar;"%REPO%"\hawtdispatch-1.11.jar;"%REPO%"\hawtbuf-1.9.jar;"%REPO%"\geronimo-j2ee-management_1.1_spec-1.0.1.jar;"%REPO%"\james-server-mailets-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\apache-jsieve-mailet-0.6-20140218.041509-850.jar;"%REPO%"\apache-jsieve-core-0.6-20140218.041457-887.jar;"%REPO%"\geronimo-activation_1.1_spec-1.1.jar;"%REPO%"\apache-mailet-standard-2.5.1-20140218.040910-356.jar;"%REPO%"\httpclient-osgi-4.2.2.jar;"%REPO%"\httpclient-4.2.2.jar;"%REPO%"\httpcore-4.2.2.jar;"%REPO%"\httpmime-4.2.2.jar;"%REPO%"\httpclient-cache-4.2.2.jar;"%REPO%"\fluent-hc-4.2.2.jar;"%REPO%"\derby-10.9.1.0.jar;"%REPO%"\apache-james-mailbox-jpa-0.6-20140218.042641-281.jar;"%REPO%"\openjpa-2.2.1.jar;"%REPO%"\serp-1.13.1.jar;"%REPO%"\geronimo-jta_1.1_spec-1.1.1.jar;"%REPO%"\commons-pool-1.6.jar;"%REPO%"\asm-3.2.jar;"%REPO%"\stax-api-1.0-2.jar;"%REPO%"\jasypt-1.9.0.jar;"%REPO%"\apache-james-mailbox-spring-0.6-20140218.042815-272.jar;"%REPO%"\commons-dbcp-1.4.jar;"%REPO%"\jcr-2.0.jar;"%REPO%"\apache-james-mailbox-lucene-0.6-20140218.042658-283.jar;"%REPO%"\apache-mime4j-core-0.7.2.jar;"%REPO%"\apache-mime4j-dom-0.7.2.jar;"%REPO%"\lucene-core-3.6.0.jar;"%REPO%"\lucene-analyzers-3.6.0.jar;"%REPO%"\lucene-smartcn-3.6.0.jar;"%REPO%"\apache-james-mailbox-jcr-0.6-20140218.042612-281.jar;"%REPO%"\jackrabbit-jcr-commons-2.5.2.jar;"%REPO%"\apache-james-mailbox-memory-0.6-20140218.042736-279.jar;"%REPO%"\james-server-data-file-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-data-jpa-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\commons-codec-1.7.jar;"%REPO%"\james-server-data-jdbc-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-data-hbase-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\james-server-data-jcr-3.0.0-beta5-SNAPSHOT.jar;"%REPO%"\protocols-imap-1.6.3.jar;"%REPO%"\commons-lang-2.6.jar;"%REPO%"\jutf7-1.0.0.jar;"%REPO%"\jetm-1.2.3.jar;"%REPO%"\jetm-optional-1.2.3.jar;"%REPO%"\guava-13.0.jar;"%REPO%"\james-server-app-3.0.0-beta5-SNAPSHOT.jar
set EXTRA_JVM_ARGUMENTS=-XX:+HeapDumpOnOutOfMemoryError -Xms128m -Xmx512m -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.authenticate=false -Dmail.mime.multipart.ignoremissingendboundary=true -Dmail.mime.multipart.ignoremissingboundaryparameter=true -Dmail.mime.ignoreunknownencoding=true -Dmail.mime.uudecode.ignoreerrors=true -Dmail.mime.uudecode.ignoremissingbeginend=true -Dmail.mime.multipart.allowempty=true -Dmail.mime.base64.ignoreerrors=true -Dmail.mime.encodeparameters=true -Dmail.mime.decodeparameters=true -Dmail.mime.address.strict=false -Djames.message.usememorycopy=false
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS% %EXTRA_JVM_ARGUMENTS% -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="run" -Dapp.repo="%REPO%" -Dbasedir="%BASEDIR%" org.apache.james.app.spring.JamesAppSpringMain %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal

:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%