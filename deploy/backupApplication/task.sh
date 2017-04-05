#!/bin/bash

#导入环境变量
# export LANG="en_US.UTF-8"
# export JAVA_HOME=/usr/javak1.6.0_31
# export PATH=$JAVA_HOME/bin:$PATH
# export CLASSPATH=.:$JAVA_HOMEb/dt.jar:$JAVA_HOMEb/tools.jar

EXEC_HOME=.

echo "`date +%Y-%m-%d_%H:%M:%S` start task......"
java -jar $EXEC_HOME/BackupDBApplication.jar
echo "`date +%Y-%m-%d_%H:%M:%S` end task......"

#11 10 * * * sh /Users/Lee/Desktop/backupApplication/task.sh >> /Users/Lee/Desktop/backupApplication/BackupDBApplication.log