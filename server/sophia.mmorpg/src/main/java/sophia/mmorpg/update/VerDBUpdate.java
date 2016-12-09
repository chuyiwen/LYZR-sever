package sophia.mmorpg.update;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import sophia.mmorpg.update.util.IOUtil;

/**
 * 版本更新 Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public abstract class VerDBUpdate {
	private final static Logger logger = Logger.getLogger(VerDBUpdate.class);

	protected List<TableUp> tableUps = new ArrayList<TableUp>();// 列升级数据列表

	/**
	 * 版本升级处理
	 * 
	 * @return
	 */
	public boolean upgrade() throws Exception {
		init(".sql");// 升级之前进行初始化

		// TODO 做其他的升级处理
		logger.info("Start upgrade " + getMajorVersion() + "." + getMinorVersion() + "." + getFractionalVersion() + " !");
		for (TableUp tableUp : tableUps) {
			if (!tableUp.upgrade(DBTypeDefine.MorningGlory_Data)) {
				logger.info("Upgrade MorningGlory_Data failure！");
				return false;
			}
		}
		
		tableUps.clear();
		
		init(".sql_log");
		for (TableUp tableUp : tableUps) {
			if (!tableUp.upgrade(DBTypeDefine.MorningGlory_Log)) {
				logger.info("Upgrade MorningGlory_Log failure！");
				return false;
			}
		}

		return true;
	}

	/**
	 * 初始化工作
	 */
	protected void init(final String type) throws Exception {
		Properties prop = new Properties();
		String PROPPERTY_PATH = "sql.properties";
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(PROPPERTY_PATH);
		prop.load(in);
		String defaultDir = prop.getProperty("sqlDir");
		URL url = this.getClass().getClassLoader().getResource(defaultDir);
		
		String fileNamepath = url.getFile() +"/"+ getVersionSqlFileName()+"/";
		
		//URL url = this.getClass().getResource("");
		File file = new File(fileNamepath);
		File[] fs = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(type);
			}
		});

		for (int i = 0; i < fs.length; i++) {
			InputStream fis = null;
			try {
				fis = new FileInputStream(fs[i]);
				TableUp tableUp = new TableUp();
				tableUp.init(fis);

				tableUps.add(tableUp);
			} catch (Exception e) {
				throw e;
			} finally {
				IOUtil.closeIs(fis);
			}
		}
	}

	public abstract byte getMajorVersion();

	public abstract byte getMinorVersion();

	public abstract byte getFractionalVersion();
	
	public abstract String getVersionSqlFileName();
}
