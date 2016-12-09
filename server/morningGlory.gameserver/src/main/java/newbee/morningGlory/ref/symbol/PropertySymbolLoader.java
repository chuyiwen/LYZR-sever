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
package newbee.morningGlory.ref.symbol;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import sophia.foundation.property.symbol.JavaPropertySymbol;
import sophia.foundation.property.symbol.PropertySymbol;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class PropertySymbolLoader {

	private static Map<Short, PropertySymbol> getPropertySymbols() {

		try {
			Properties prop = new Properties();

			String PROPPERTY_PATH = "data.properties";
			InputStream in = PropertySymbolLoader.class.getClassLoader().getResourceAsStream(PROPPERTY_PATH);
			prop.load(in);
			String path = prop.getProperty("symbolPath");
			InputStream inn = PropertySymbolLoader.class.getClassLoader().getResourceAsStream(path);
			String symbolContent = IOUtils.toString(inn);
			JsonParser parser = new JsonParser();
			JsonReader reader = new JsonReader(new StringReader(symbolContent));
			reader.setLenient(true);
			JsonElement element = parser.parse(reader);
			checkArgument(element != null);

			Map<Short, PropertySymbol> coll = new HashMap<Short, PropertySymbol>();
			for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				JsonObject data = entry.getValue().getAsJsonObject();
				int id = data.get("id").getAsInt();
				String key = data.get("key").getAsString();
				String name = data.get("name").getAsString();
				String symbol = data.get("symbol").getAsString();
				String description = data.get("description").getAsString();
				String type = data.get("type").getAsString();
				JavaPropertySymbol j = new JavaPropertySymbol((short) id, key, symbol, name, description, type);
				coll.put((short) id, j);
			}
			return coll;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final void load() {
		Map<Short, PropertySymbol> symbols = getPropertySymbols();
		for (Map.Entry<Short, PropertySymbol> entry : symbols.entrySet()) {
			PropertySymbol symbol = entry.getValue();
			SimulatorPropertySymbolContext.addPropertySymbol(symbol);
		}
	}
}
