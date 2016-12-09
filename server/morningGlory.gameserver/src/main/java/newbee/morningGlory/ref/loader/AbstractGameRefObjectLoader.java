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
package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.ref.JSONDataManagerContext;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.property.ValueProperty;
import sophia.foundation.property.symbol.PropertySymbol;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;
import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectLoader;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * This class helps load "property" data into <code>GameRefObject</code>. Every
 * <code>GameRefObjectLoader</code> should inherit from this class and let it
 * help with "property" data loading. For non-property-dictionary data loading,
 * children of this class can override <code>fillNonPropertyDictionary</code>.
 * 
 */
public abstract class AbstractGameRefObjectLoader<T extends GameRefObject> implements GameRefObjectLoader<T> {
	private static Logger logger = Logger.getLogger(AbstractGameRefObjectLoader.class);

	// refsKey is used as an information when something goes wrong.
	private String refsKey;
	private String refsData;

	public AbstractGameRefObjectLoader() {
		this.refsData = null;
	}

	public AbstractGameRefObjectLoader(String jsonKey) {
		this.setRefsKey(jsonKey);
		this.refsData = JSONDataManagerContext.get(jsonKey);
	}

	protected void setRefsData(String refsData) {
		this.refsData = refsData;
	}

	/**
	 * @return please return your <code>GameRefObject</code> here
	 */
	protected abstract T create();

	@Override
	public Collection<T> loadAll() {
		try {
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(refsData);

			Set<Entry<String, JsonElement>> entrySet = jsonElement.getAsJsonObject().entrySet();
			ArrayList<T> refs = new ArrayList<>(entrySet.size());
			for (Entry<String, JsonElement> entry : entrySet) {
				refs.add(fromJson(entry.getValue()));
			}

			return refs;
		} catch (JsonParseException e) {
			logger.error("json data: key = " + this.refsKey + "\n" + refsData);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("json data: key = " + this.refsKey + "\n" + refsData);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<T> load(String... ids) {
		Set<String> idSet = new HashSet<>();
		for (String id : ids) {
			idSet.add(id);
		}

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(refsData).getAsJsonObject();

			Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
			ArrayList<T> refs = new ArrayList<>(entrySet.size());

			for (Entry<String, JsonElement> entry : entrySet) {
				String refId = entry.getKey();
				if (idSet.contains(refId)) {
					refs.add(fromJson(entry.getValue()));
				}
			}

			return refs;
		} catch (JsonParseException e) {
			logger.error("json data: key = " + this.refsKey + "\n" + refsData + " ids: " + idSet);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("json data: key = " + this.refsKey + "\n" + refsData);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * fill <code>propertyJson</code> into <code>pd</code>
	 * 
	 * @param propertyJson
	 * @param pd
	 */
	protected final void fillPropertyDictionary(PropertyDictionary pd, JsonObject propertyJson) {
		Preconditions.checkArgument(propertyJson != null);

		for (Map.Entry<String, JsonElement> entry : propertyJson.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			PropertySymbol symbol = SimulatorPropertySymbolContext.getPropertySymbol(key);
			Short id = symbol.getId();
			Class<?> clazz = symbol.getClassType();
			SimulatorProperty<?> property = null;
			if (clazz.equals(byte.class)) {
				property = new ValueProperty<Byte>(id, value.getAsByte());
			} else if (clazz.equals(short.class)) {
				property = new ValueProperty<Short>(id, value.getAsShort());
			} else if (clazz.equals(int.class)) {
				property = new ValueProperty<Integer>(id, value.getAsInt());
			} else if (clazz.equals(long.class)) {
				property = new ValueProperty<Long>(id, value.getAsLong());
			} else if (clazz.equals(float.class)) {
				property = new ValueProperty<Float>(id, value.getAsFloat());
			} else if (clazz.equals(double.class)) {
				property = new ValueProperty<Double>(id, value.getAsDouble());
			} else if (clazz.equals(char.class)) {
				property = new ValueProperty<Character>(id, value.getAsCharacter());
			} else if (clazz.equals(String.class)) {
				property = new ValueProperty<String>(id, value.getAsString());
			}

			if (property != null) {
				pd.addProperty(property);
			} else {
				logger.error("Can't load refsData property! key = " + key + " type = " + clazz);
			}
		}
		return;

	}

	/**
	 * fill <CODE>ref</CODE> with data that's not PropertyDictionary. Children
	 * of this class should override this method if there is any data that's not
	 * PropertyDictionary
	 * 
	 * @param object
	 * @param refData
	 */
	protected void fillNonPropertyDictionary(T ref, JsonObject refData) {

	}

	private final T fromJson(JsonElement refData) {
		T ret = create();

		// TODO: make "refId", "property" configurable
		// if getAsString() fails, we can do nothing here, make sure that every
		// refData has at least a field called "refId"
		// refId
		String refId = refData.getAsJsonObject().get("refId").getAsString();
		ret.setId(refId);

		if (logger.isDebugEnabled()) {
			logger.debug("fromJson refId: " + refId);
		}
		// property
		JsonElement propertyJson = refData.getAsJsonObject().get("property");
		if (propertyJson != null) {
			fillPropertyDictionary(ret.getProperty(), propertyJson.getAsJsonObject());
		}

		fillNonPropertyDictionary(ret, refData.getAsJsonObject());

		return ret;
	}

	public String getRefsKey() {
		return refsKey;
	}

	private void setRefsKey(String refsKey) {
		this.refsKey = refsKey;
	}

}
