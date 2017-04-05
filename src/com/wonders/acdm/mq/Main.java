
package com.wonders.acdm.mq;

import java.util.Date;

class Main {

    public static void main(String[] args) {
        String date = new Date().toString();
        System.out.println("Time: "+date+" StartBackupTask");
        BackupTask task = new BackupTask();
        task.exec();
        date = new Date().toString();
        System.out.println("Time: "+date+" FinishBackupTask");
    }
}
