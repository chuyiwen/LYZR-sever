/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.game.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

import sophia.foundation.util.PropertiesWrapper;
import sophia.game.GameContext;

public final class DaoConfig {

	private static final Logger logger = Logger.getLogger(DaoConfig.class
			.getName());
	
	private static final String DATABASE_XML = "sophia.game.database.xml";

	public static boolean init() {
		boolean isSuccess = true;
		try {
			// 注册数据库连接池
			isSuccess = registerProxool();
			if (!isSuccess) {
				return false;
			}

			// 测试数据库连接池
			// testConnection();

			// 打印数据库连接池配置
			loggerProxoolConfig();
		} catch (Exception e) {
			logger.error("DaoConfig init error!", e);
			return false;
		}

		return true;
	}

	private static boolean registerProxool() {
		InputStream in = null;
		// 游戏数据库proxool连接池路径
		PropertiesWrapper properties = GameContext.getProperties();
		
		String url = properties.getProperty(DATABASE_XML);
		
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");

			in = DaoConfig.class.getClassLoader().getResourceAsStream(url);
			Reader reader = new InputStreamReader(in);

			JAXPConfigurator.configure(reader, false);
		} catch (ProxoolException e) {
			logger.error("registerProxool error!", e);
			return false;
		} catch (ClassNotFoundException e) {
			logger.error("register ProxoolDriver error!", e);
			return false;
		} catch (Exception e) {
			logger.error("registerProxool error!", e);
			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("registerProxool error!", e);
			}
		}

		return true;
	}

	public static boolean testConnection() {
		Connection connection = ConnectionManager.getConnection();
		if (connection != null) {
			logger.info("testConnection success!");

			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(
						"testConnection success!but close connection error!", e);
			}

			return true;
		} else {
			logger.error("testConnection faild!");
			return false;
		}
	}

	public static String getInfo() {
		String info1 = "";
		try {
			ConnectionPoolDefinitionIF connectionPoolDefinitionGameDb = ProxoolFacade
					.getConnectionPoolDefinition("morningGlory_data");

			info1 = String.format("数据库=%s;用户名=%s;",
					connectionPoolDefinitionGameDb.getUrl(),
					connectionPoolDefinitionGameDb.getUser());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return info1;
	}

	private static void loggerProxoolConfig() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug(getInfo());
			}
		} catch (Exception e) {
			logger.error("打印数据库连接池信息出错!", e);
		}
	}
}
