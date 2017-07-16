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


#export LANG=ru_RU.KOI8-R
#export LANG=ru_RU.CP1251
export JAVA_HOME=/usr/
export APP_HOME=$(pwd)
export LIB_PATH=$APP_HOME/lib
export ODBC_PATH=$LIB_PATH/ojdbc14.jar
export COMMONS_PATH=$LIB_PATH/commons-net-3.3.jar:$LIB_PATH/commons-net-examples-3.3.jar:$LIB_PATH/commons-net-3.3-sources.jar
#export LOG4J_PATH=$LIB_PATH/log4j-1.2.17.jar:$LIB_PATH/apache-log4j-extras-1.1.jar
export APP_PATH=$LIB_PATH/md_operator.jar

cd $APP_PATH

$JAVA_HOME/bin/java -classpath $ODBC_PATH:$COMMONS_PATH:$APP_PATH pkgmain.Starter sip

