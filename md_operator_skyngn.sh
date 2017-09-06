#export LANG=ru_RU.KOI8-R
#export LANG=ru_RU.CP1251
export JAVA_HOME=/home/manager/jre1.7.0_55
export APP_HOME=$(pwd)
export LIB_PATH=$APP_HOME/lib
export ODBC_PATH=$LIB_PATH/ojdbc14.jar
export COMMONS_PATH=$LIB_PATH/commons-net-3.3.jar
export MAIL_PATH=$LIB_PATH/javax.mail.jar
export APP_PATH=$LIB_PATH/md_operator.jar

cd $APP_HOME

$JAVA_HOME/bin/java -classpath $APP_PATH:$ODBC_PATH:$COMMONS_PATH:$MAIL_PATH telecom.atyrau.Starter skyngn

