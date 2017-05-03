package com.wonders.acdm.mq.dao;

import com.wonders.acdm.mq.model.BackupLog;
import com.wonders.acdm.mq.util.ConfigProperties;
import com.wonders.acdm.mq.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee on 2017/3/31.
 */
public class BackupLogDAO {
    private static Logger logger = Logger.getLogger(BackupLog.class);

    public static List<BackupLog> getList() {
        List<BackupLog> list = new ArrayList<>();
        Session session = HibernateUtil.getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            Criteria criteria = session.createCriteria(BackupLog.class)
                    .add(Restrictions.eq("status", "0"));
            criteria.addOrder(Order.asc("id"));
            criteria.setMaxResults(10000);
            list = criteria.list();
            trans.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            trans.rollback();
        }
        return list;
    }

    public static void updateStatusToMoved(BackupLog backupLog, Path dstPath) {
        try {
            backupLog.setStatus("1");
            String desFileDir = ConfigProperties.getProperty(ConfigProperties.Property.DSTDBABS_PATH);
            backupLog.setDesFileDir(desFileDir+File.separator+dstPath.getParent().getFileName().toString());
            backupLog.setDesFileName(dstPath.getFileName().toString());
            HibernateUtil.update(backupLog);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
