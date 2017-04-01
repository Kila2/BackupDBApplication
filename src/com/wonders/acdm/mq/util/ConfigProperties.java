
package com.wonders.acdm.mq.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Data Access class to access properties file for Test client
 * 
 * @author paul.crisp
 * @version $Revision: 1.1 $ $Date: 2009/11/19 09:45:43 $
 */
public class ConfigProperties {
    private static Logger logger = Logger.getLogger(ConfigProperties.class);
    	
	private final static Properties properties = new Properties();
	
	static{
		System.out.println("loading config......");
		try {
			// InputStream inputStream =
			// TestClientProperties.class.getClassLoader().getResourceAsStream("src/resource/testclient.properties");
			//InputStream inputStream = ConfigProperties.class.getResourceAsStream(System.getProperty("user.dir") + File.separator + "config.properties");
			InputStream inputStream=new FileInputStream(System.getProperty("user.dir") + File.separator + "config.properties");
			properties.load(inputStream);
			inputStream.close();

		} catch (IOException e) {
			logger.debug("Exception in load configProperties:");
			e.printStackTrace();
			
		}
	}

	
	public static String getProperty(Property p) {
		String key=p.getPropertyKey();
		String value=  properties.getProperty(key);

		logger.debug(key+"="+value);
		return value;
	}
	
	public static String getProperty(Property p,String defaultValue) {
		String key=p.getPropertyKey();
		String value=  properties.getProperty(key,defaultValue);

		logger.debug(key+"="+value);
		return value;
	}

	public static String getProperty(String key) {	
		String value=  properties.getProperty(key.toLowerCase().replaceAll("-", ".").replaceAll("_", "."));

		logger.debug(key+"="+value);
		return value;
	}

    public enum Property {
        HOST_NAME("mq.hostName"), PORT("mq.port"), SERVER_NAME("mq.serverName"),MAX_DEPTH("mq.maxDepth"),BACKUP_SRC("mq.backupSrc"),BACKUP_DST("mq.backupDst"),BACKUP_RECYLE("mq.backupRecyle");
        
        private String key;
        private Property(String key){
        	this.key=key;
        }
        
        public String getPropertyKey(){
        	return key;
        }

    }

    public static void main(String[] args){
    	
//    	String[] supportDataTypes=ConfigProperties.getProperty(Property.DATA_TYPE).split(",");
//    	List<String> supportDataTypeList=Arrays.asList(supportDataTypes);
//    	
//		System.out.println(supportDataTypeList.contains("security"));
//			
//		
//    	
//    	System.out.println(ConfigProperties.getProperty("IB"));
//    	System.out.println(ConfigProperties.getProperty(Property.DATA_TYPE));
//    	System.out.println(ConfigProperties.getProperty(Property.SERVER_NAME));
//    	System.out.println(ConfigProperties.getProperty(Property.HOST_NAME));
//    	System.out.println(ConfigProperties.getProperty(Property.PORT));
//    	System.out.println(ConfigProperties.getProperty(Property.CCSID));
//    	System.out.println(ConfigProperties.getProperty(Property.CHANNEL));
//    	System.out.println(ConfigProperties.getProperty(Property.QUEUE_MANAGER));
    	System.out.println(ConfigProperties.getProperty(Property.MAX_DEPTH,String.valueOf(32000)));
    }
    
}