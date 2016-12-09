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
package sophia.foundation.property.symbol;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;


/**
 * This class maintains a PropertySymbol table served as a reference for 
 * SimulatorProperty to look for.
 */
// Is it suitable to be a public class here, or rather a private one, if
// there exists a delegate class `SimulatorPropertySymbolContext`?
public class SimulatorPropertySymbolMgr {
	private final Map<Short, PropertySymbol> idToSymbolMap = new HashMap<Short, PropertySymbol>();
	private final Map<String, PropertySymbol> keyToSymbolMap = new HashMap<String, PropertySymbol>();
	
	public PropertySymbol getPropertySymbol(short id) {
		PropertySymbol propertySymbol = idToSymbolMap.get(id);
		checkArgument(propertySymbol != null, "propertySymbol[id = " + id + "] is not defined.");
		return propertySymbol;
	}
	
	public PropertySymbol getPropertySymbol(String key) {
		PropertySymbol propertySymbol = keyToSymbolMap.get(key);
		checkArgument(propertySymbol != null, "propertySymbol[key =" + key + "] is not defined.");
		return propertySymbol;
	}
	
	public void addPropertySymbol(PropertySymbol symbol) {
		short id = symbol.getId();
		String key = symbol.getKey();
		idToSymbolMap.put(id, symbol);
		keyToSymbolMap.put(key, symbol);
	}
}
