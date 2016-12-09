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
package newbee.morningGlory.ref;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

import sophia.game.ref.JSONDataManager;

public final class JSONDataManagerContext {
	private static Logger logger = Logger.getLogger(JSONDataManagerContext.class);
	 
	private JSONDataManagerContext() {
	}
	
	private static void init() {
		try {
			Properties prop = new Properties();
			String PROPPERTY_PATH = "data.properties";
			InputStream in = JSONDataManagerContext.class.getClassLoader()
					.getResourceAsStream(PROPPERTY_PATH);
			prop.load(in);
			String defaultDir = prop.getProperty("jsonDir");
			URL url = JSONDataManagerContext.class.getClassLoader()
					.getResource(defaultDir);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Default json path: " + url);
			}
			File dir = new File(url.getPath());
			JSONDataManager.init(dir);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		init();
		JSONDataManager.load();
	}
	
	public static String get(String key) {
		return JSONDataManager.get(key);
	}
	
//	public static JSONObject get(String key, String id) {
//		return JSONDataManager.get(key, id);
//	}
	
	public static Collection<String> allKeys() {
		return JSONDataManager.allKeys();
	}
}
