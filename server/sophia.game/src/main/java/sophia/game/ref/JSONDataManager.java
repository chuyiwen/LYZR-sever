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
package sophia.game.ref;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;

/**
 * This class loads all json files into a map, with file name without extension
 * as key, file content as JSONObject as value.
 */
public final class JSONDataManager {
	private static Logger logger = Logger.getLogger(JSONDataManager.class);
	private static File dataDir;
	private static Map<String, String> data = new HashMap<String, String>();

	private JSONDataManager() {
		super();
	}

	public static void init(File dir) {
		dataDir = dir;
	}

	// only json files are interested
	/**
	 * @return all json files
	 */
	private static Collection<File> jsonFiles() {
		Collection<File> files = FileUtils.listFiles(dataDir, new RegexFileFilter("^.+\\.json"), DirectoryFileFilter.DIRECTORY);

		return files;
	}

	// check file name ubiquity
	/**
	 * @return whether file names ubiquitous
	 */
	private static boolean isUbiquitous() {
		Map<String, File> temp = new HashMap<String, File>();

		Collection<File> files = jsonFiles();
		for (File f : files) {
			String filename = f.getName();
			String key = FilenameUtils.removeExtension(filename);
			if (!temp.containsKey(key)) {
				temp.put(key, f);
			} else {
				logger.error("duplicate file names: " + temp.get(key).getPath() + ", " + f.getPath());
				return false;
			}
		}
		return true;
	}

	/**
	 * load all the json files
	 */
	public static void load() {
		if (!isUbiquitous()) {
			throw new RuntimeException("duplicate file names");
		}

		Collection<File> files = jsonFiles();
		for (File f : files) {
			String filename = f.getName();
			if (logger.isDebugEnabled()) {
				logger.debug("loading file: " + filename);
			}
			String key = FilenameUtils.removeExtension(filename);
			try {
				String content = FileUtils.readFileToString(f, Charsets.UTF_8);
				if (content.isEmpty()) {
					logger.error(filename + " is empty!");
					continue;
				}
				data.put(key, content);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("can't read file: " + filename + " reason: " + e.getMessage());
			}
		}
	}

	/**
	 * @param key
	 *            filename without extension
	 * @return a JSONObject of a collection of JSONObjects which can be
	 *         deserialized into a GameRefObject
	 */
	public static String get(String key) {
		return data.get(key);
	}

	public static Collection<String> allKeys() {
		return data.keySet();
	}

}
