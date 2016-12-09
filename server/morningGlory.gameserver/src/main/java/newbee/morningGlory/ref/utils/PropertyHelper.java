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
package newbee.morningGlory.ref.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertyHelper {
	
	public String getProperty(String key) {
		Properties prop = new Properties();
		String PROPPERTY_PATH = "data.properties";
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(PROPPERTY_PATH);
		try {
			prop.load(in);
			String value = prop.getProperty(key);
			return value;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
