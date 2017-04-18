package com.wonders.acdm.mq;

import com.wonders.acdm.mq.dao.BackupLogDAO;
import com.wonders.acdm.mq.model.BackupLog;
import com.wonders.acdm.mq.util.ConfigProperties;
import org.apache.log4j.Logger;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by Lee on 2017/3/31.
 */
public class BackupTask {

    private static Logger logger = Logger.getLogger(BackupTask.class);

    BackupTask() {
        try {

            Set<PosixFilePermission> perms =
                    PosixFilePermissions.fromString("rwxrw-rw-");
            FileAttribute<Set<PosixFilePermission>> attr =
                    PosixFilePermissions.asFileAttribute(perms);


            Path srcPath = Paths.get(ConfigProperties.getProperty(ConfigProperties.Property.BACKUP_RECYLE));
            if (!Files.exists(srcPath)) {
                Files.createDirectory(srcPath,attr);
            }
            Path dstPath = Paths.get(ConfigProperties.getProperty(ConfigProperties.Property.BACKUP_DST));
            if (!Files.exists(dstPath)) {
                Files.createDirectory(dstPath,attr);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void exec() {
        List<BackupLog> backupLogs = readyToProcess();
        tarFiles(backupLogs);
        moveTarToBackupDir(backupLogs);
        clearRecyle();
    }

    private void moveTarToBackupDir(List<BackupLog> backupLogs) {
        try {
            for (BackupLog backupLog :
                    backupLogs) {
                if (backupLog.getTarFile() != null) {
                    Path dstPath = Paths.get(ConfigProperties.getProperty(ConfigProperties.Property.BACKUP_DST),
                            File.separator,backupLog.getFileType(),
                            File.separator,backupLog.getTarFile().getName());
                    existsPath(dstPath.getParent());
                    Files.move(backupLog.getTarFile().toPath(), dstPath, REPLACE_EXISTING);
                    TarGZipUnArchiver unArchiver = new TarGZipUnArchiver(dstPath.toFile());
                    unArchiver.setDestDirectory(dstPath.getParent().toFile());
                    unArchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG,
                            "Logger"));
                    unArchiver.extract();
                    updateMovedStatus(backupLog,dstPath);
                    renameSrcFile(backupLog.getFile());
                }
            }
        } catch (IOException ioe) {
            logger.error(String.format("Could not move file: %s%n", ioe.getMessage()));
        }
    }

    private void existsPath(Path dstPath) {
        try {
            if (!Files.exists(dstPath)) {
                Set<PosixFilePermission> perms =
                        PosixFilePermissions.fromString("rwxrw-rw-");
                FileAttribute<Set<PosixFilePermission>> attr =
                        PosixFilePermissions.asFileAttribute(perms);
                Files.createDirectory(dstPath, attr);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void updateMovedStatus(BackupLog backupLog, Path dstPath) {
        BackupLogDAO.updateStatusToMoved(backupLog,dstPath);
    }

    private void clearRecyle() {
        File file = new File(ConfigProperties.getProperty(ConfigProperties.Property.BACKUP_RECYLE));
        if (file.exists()&&file.isDirectory()) {
            File[] files =  file.listFiles();
            for (File deletefile:
                 files) {
                try {
                    Files.deleteIfExists(deletefile.toPath());
                } catch (IOException ioe) {
                    System.err.printf("Could not delete file: %s%n", ioe.getMessage());
                }
            }
        }
    }

    private void moveSrcToRecyle(File file) {
        Path dstPath = Paths.get(ConfigProperties.getProperty(ConfigProperties.Property.BACKUP_RECYLE), File.separator,file.getName());
        try {
            Files.move(file.toPath(), dstPath, REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void renameSrcFile(File file) {
        try {
            String fileOriginNamePath = file.toString();
            fileOriginNamePath = file.toString().substring(0,fileOriginNamePath.lastIndexOf('/'));
            Path donePath = Files.move(file.toPath(), Paths.get(fileOriginNamePath,".done"), REPLACE_EXISTING);
            moveSrcToRecyle(donePath.toFile());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    private List<BackupLog> tarFiles(List<BackupLog> backupLogs) {

        for (BackupLog backupLog :
                backupLogs) {
            backupLog.getTarFile();
        }
        return backupLogs;
    }

    private List<BackupLog> readyToProcess() {
        List<BackupLog> backupLogList = BackupLogDAO.getList();
        return backupLogList;
    }
}
