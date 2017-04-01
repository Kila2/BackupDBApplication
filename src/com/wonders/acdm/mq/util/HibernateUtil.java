package com.wonders.acdm.mq.util;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.ParameterMode;
import java.io.File;
import java.io.Serializable;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Logger logger = Logger.getLogger(HibernateUtil.class);

    static {
        Configuration configuration = new Configuration()
                .configure(new File(System.getProperty("user.dir") + File.separator + "hibernate.cfg.xml"));
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, Serializable id) throws Exception {
        T ret = null;
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            ret = (T) session.get(clazz, id);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        }
        return ret;
    }

    public static void save(Object obj) throws Exception {
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            session.save(obj);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        }
    }

    public static void update(Object obj) throws Exception {
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            session.update(obj);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        }
    }

    public static void saveOrUpdate(Object obj) throws Exception {
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            session.saveOrUpdate(obj);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        }
    }

    public static void delete(Object obj) throws Exception {
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            session.delete(obj);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        }
    }



    public static long excuteProcedureSender(String serverName,String dataType,String status, long minMsgId) {
        long ret = -1L;
        String procedureName="ACDMDS_SENDER_"+serverName;
        if(dataType!=null && !dataType.isEmpty() && !dataType.equalsIgnoreCase("ALL")){
        	String proNameInConfig=ConfigProperties.getProperty(dataType);
        	if(proNameInConfig==null){
        		procedureName=procedureName.concat("_").concat(dataType);	
        	}else{
        		procedureName=proNameInConfig;
        	}
        	
        }
        
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            ProcedureCall call = session.createStoredProcedureCall(procedureName);
            call.registerParameter("STATUS", String.class, ParameterMode.IN).bindValue(status);
            call.registerParameter("MIN_MSG_ID", Long.class, ParameterMode.IN).bindValue(minMsgId);

            call.registerParameter0("res_stus", String.class, ParameterMode.OUT)
                    .registerParameter0("operation_type", String.class, ParameterMode.OUT)
                    .registerParameter0("MSG_ID", Long.class, ParameterMode.OUT);
            ProcedureOutputs outputs = call.getOutputs();
            ret = (Long) outputs.getOutputParameterValue("MSG_ID");
            logger.info("procedure "+procedureName+": STATUS:" + status + " min_msg_id:" + minMsgId + " res_stus:"
                    + outputs.getOutputParameterValue("res_stus") + " operation_type:"
                    + outputs.getOutputParameterValue("operation_type") + " MSG_ID:"
                    + outputs.getOutputParameterValue("MSG_ID"));
            trans.commit();
        } catch (Exception e) {
            logger.error(e);
            trans.rollback();
        }
        return ret;
    }

    public static boolean excuteProcedureReceiver(String serverName,long msgId) {
    	String procedureName="ACDMUS_RECEIVER_"+serverName;
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();
        try {
            ProcedureCall call = session.createStoredProcedureCall(procedureName);
            call.registerParameter("SID", Long.class, ParameterMode.IN).bindValue(msgId);
            call.registerParameter0("ID", String.class, ParameterMode.OUT).registerParameter0("OPERATION_TYPE",
                    String.class, ParameterMode.OUT);
            ProcedureOutputs outputs = call.getOutputs();
            String id = outputs.getOutputParameterValue("ID").toString();
            logger.info("procedure "+procedureName+": msgId:" + msgId + " id:" + id + " operation_type:"
                    + outputs.getOutputParameterValue("OPERATION_TYPE"));
            trans.commit();
            return !id.equals("0");
        } catch (Exception e) {
            logger.error(e);
            trans.rollback();
            return false;
        }
    }
}