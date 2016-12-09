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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import sophia.foundation.property.PropertyDictionary;

public class JavaPropertySymbol implements PropertySymbol, Serializable {
	private static final long serialVersionUID = 7966267057781644480L;
	
	private Short id;
	private String key;
	private String symbol;
	private String name;
	private String description;
	private String type;
	private Class<?> clazz;
	
	public JavaPropertySymbol() {

	}

	public JavaPropertySymbol(final Short id, final String key, final String symbol, final String name, final String description, final String type) {
		this.id = id;
		this.key = key;
		this.symbol = symbol;
		this.name = name;
		this.description = description;
		this.type = type;
		if ("int8".equalsIgnoreCase(type))
			this.clazz = byte.class;
		else if ("int16".equalsIgnoreCase(type))
			this.clazz = short.class;
		else if ("int32".equalsIgnoreCase(type))
			this.clazz = int.class;
		else if ("int64".equalsIgnoreCase(type))
			this.clazz = long.class;
		else if ("float32".equalsIgnoreCase(type))
			this.clazz = float.class;
		else if ("float64".equalsIgnoreCase(type))
			this.clazz = double.class;
		else if ("char".equalsIgnoreCase(type) || "character".equalsIgnoreCase(type))
			this.clazz = char.class;
		else if ("string".equalsIgnoreCase(type))
			this.clazz = String.class;
		else if ("binary".equalsIgnoreCase(type))
			this.clazz = byte[].class;
		else if ("collection".equalsIgnoreCase(type))
			this.clazz = Collection.class;
		else if ("map".equalsIgnoreCase(type))
			this.clazz = Map.class;
		else if ("dictionary".equalsIgnoreCase(type))
			this.clazz = PropertyDictionary.class;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getId()
	 */
	@Override
	public Short getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getSymbol()
	 */
	@Override
	public String getSymbol() {
		return symbol;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see sophia.game.component.property.symbol.PropertySymbol#getClassType()
	 */
	@Override
	public Class<?> getClassType() {
		return clazz;
	}

}
