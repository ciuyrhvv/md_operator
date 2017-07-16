#set JAVA_HOME=c:\java
set JAVA_HOME="c:\Program Files\Java\jre1.8.0_131"

set APP_HOME=%cd%

#set GROOVY_HOME=c:\groovy-2.4.0
set GROOVY_HOME="c:\Program Files (x86)\groovy-2.4.7"


set LIB=%APP_HOME%\lib
set LIB1=%LIB%\commons-net-3.3.jar
set LIB2=%LIB%\my_ftp.jar

cd %APP_HOME%

%GROOVY_HOME%/bin/groovy -cp %LIB1%;%LIB2%; sip_corrector.groovy
