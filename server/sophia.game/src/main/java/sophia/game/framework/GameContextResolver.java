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
package sophia.game.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import sophia.foundation.util.PropertiesWrapper;

public final class GameContextResolver {
	
	private static final Logger logger = Logger.getLogger(GameContextResolver.class);
	
	private static final String Game_Properties_Path = "game.properties";
	
	private static PropertiesWrapper properties;
	
	public GameContextResolver() {
		Properties p = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				Game_Properties_Path);

		try {
			p.load(is);
		} catch (IOException e) {
			logger.error("game properties load failed.", e);
		}

		properties = new PropertiesWrapper(p);
	}
	
	public PropertiesWrapper getProperty() {
		return properties;
	}
}
