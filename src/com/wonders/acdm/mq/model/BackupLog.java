package com.wonders.acdm.mq.model;

import org.apache.log4j.Logger;
import org.codehaus.plexus.archiver.tar.TarArchiver;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * Created by Lee on 2017/3/31.
 */
@Entity
@Table(name = "LOG_BK_TABLE")
public class BackupLog implements java.io.Serializable {
    private static Logger logger = Logger.getLogger(BackupLog.class);

    private Long id;
    private String startTime;
    private String endTime;
    private String execSQL;
    private String fileName;
    private String fileDir;
    private String fileType;
    private Long recNum;
    private String status;
    private File file;
    private File tarFile;
    private String desFileDir;
    private String desFileName;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @Column(name = "START_TIME")
    public String getStartTime() {
        return startTime;
    }

    @Column(name = "END_TIME")
    public String getEndTime() {
        return endTime;
    }

    @Column(name = "EXEC_SQL")
    public String getExecSQL() {
        return execSQL;
    }

    @Column(name = "FILE_NAME")
    public String getFileName() {
        return fileName;
    }

    @Column(name = "FILE_DIR")
    public String getFileDir() {
        return fileDir;
    }

    @Column(name = "FILE_TYPE")
    public String getFileType() {
        return fileType;
    }

    @Column(name = "REC_NUM")
    public Long getRecNum() {
        return recNum;
    }

    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    @Column(name = "DES_FILE_DIR")
    public String getDesFileDir() {
        return desFileDir;
    }

    @Column(name = "DES_FILE_NAME")
    public String getDesFileName() {
        return desFileName;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setExecSQL(String execSQL) {
        this.execSQL = execSQL;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setRecNum(Long recNum) {
        this.recNum = recNum;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDesFileDir(String desFileDir) {
        this.desFileDir = desFileDir;
    }

    public void setDesFileName(String desFileName) {
        this.desFileName = desFileName;
    }

    @Transient
    public File getFile() {
        if (file == null) {
            file = new File(fileDir + File.separator + fileName);
        }
        return file;
    }

    @Transient
    public File getTarFile() {
        if (tarFile == null) {
            if (Files.exists(getFile().toPath())) {
                TarArchiver archiver = new TarArchiver();
                archiver.addFile(getFile(), getFileName());
                archiver.setCompression(TarArchiver.TarCompressionMethod.gzip);
                File dstFile = new File(fileDir + File.separator + fileName + ".tar.gz");
                archiver.setDestFile(dstFile);
                try {
                    archiver.createArchive();
                    tarFile = dstFile;
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return tarFile;
    }
}
