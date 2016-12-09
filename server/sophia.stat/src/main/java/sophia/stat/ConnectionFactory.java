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
package sophia.stat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

public class ConnectionFactory {
	private static final Logger logger = Logger.getLogger(ConnectionFactory.class);

	private static final String DB_CONFIG_FILE = "morningGlory_log.xml";
	private static final String DB_ALIAS = "morningGlory_log";
	private static final String DB_URL = "proxool.morningGlory_log";

	public static ConnectionFactory INSTANCE = new ConnectionFactory();

	private ConnectionFactory() {
		super();
		init();
	}

	public static ConnectionFactory getInstance() {
		return INSTANCE;
	}

	public void init() {
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			InputStream in = getClass().getClassLoader().getResourceAsStream(DB_CONFIG_FILE);

			JAXPConfigurator.configure(new InputStreamReader(in), false);

			if (logger.isDebugEnabled()) {
				logger.debug(getInfo());
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProxoolException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static String getInfo() {
		String info1 = "";
		try {
			ConnectionPoolDefinitionIF connectionPoolDefinitionGameDb = ProxoolFacade
					.getConnectionPoolDefinition(DB_ALIAS);

			info1 = String.format("数据库=%s;用户名=%s;",
					connectionPoolDefinitionGameDb.getUrl(),
					connectionPoolDefinitionGameDb.getUser());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return info1;
	}
}
