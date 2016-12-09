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
package sophia.foundation.property;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.property.symbol.PropertySymbol;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.foundation.util.DebugUtil;

import com.google.common.base.Preconditions;

//TODO: consider not to use clone!
//see http://www.artima.com/intv/bloch13.html and Item 11 of Effective Java
@SuppressWarnings("all")
public class PropertyDictionary implements Cloneable {
	public static final Byte ByteNull = 0;
	public static final Short ShortNull = 0;
	public static final Integer IntNull = 0;
	public static final Long LongNull = (long) 0;
	public static final Float FloatNull = (float) 0;
	public static final Double DoubleNull = (double) 0;
	public static final Character CharNull = Character.MIN_VALUE;
	public static final String StringNull = null;
	public static final Collection<?> CollectionNull = null;
	public static final Map<?, ?> MapNull = null;
	public static final PropertyDictionary DictionaryNull = null;
	public static final Closure<?> ClosureNull = null;

	private static final Logger logger = Logger.getLogger(PropertyDictionary.class);
	private Map<Short, SimulatorProperty<?>> dictionary = new HashMap<Short, SimulatorProperty<?>>();

	public PropertyDictionary() {

	}

	public PropertyDictionary(int capacity) {
		dictionary = new HashMap<>(capacity);
	}

	public final Map<Short, SimulatorProperty<?>> getDictionary() {
		return dictionary;
	}

	@Override
	public PropertyDictionary clone() {
		PropertyDictionary pd = new PropertyDictionary();
		Map<Short, SimulatorProperty<?>> dict = pd.getDictionary();

		Set<Entry<Short, SimulatorProperty<?>>> entrySet = dictionary.entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			dict.put(entry.getKey(), entry.getValue().clone());
		}

		return pd;
	}

	/**
	 * 根据给定数据组装字典数据<br>
	 * 二进制数组的格式为：COUNT|id|value|id|value|id...<br>
	 * id:属性的编号；<br>
	 * type：属性的类型(byte、short、int、long、float、double、char、string等)；<br>
	 * value： 属性的具体值
	 * 
	 * @param data
	 *            二进制数组
	 */
	public final void loadDictionary(byte[] data) {
		if (data == null || data.length == 0)
			return;
		ByteArrayReadWriteBuffer buf = new ByteArrayReadWriteBuffer(data);
		buf.readShort();
		while (buf.hasRemaining()) {
			short id = buf.readShort();
			PropertySymbol propertySymbol = SimulatorPropertySymbolContext.getPropertySymbol(id);
			Class<?> clazz = propertySymbol.getClassType();
			try {
				SimulatorProperty<?> property = readSimulatorProperty(buf, propertySymbol);
				if (property != null)
					addProperty(property);
			} catch (Exception ex) {
				logger.error("解析PropertyData错误!SymbolID=" + id + ",Type=" + clazz, ex);
			}
		}
	}

	private SimulatorProperty<?> readSimulatorProperty(ByteArrayReadWriteBuffer buf, PropertySymbol propertySymbol) {
		short id = propertySymbol.getId();
		Class<?> clazz = propertySymbol.getClassType();
		SimulatorProperty<?> property = null;
		if (clazz.equals(byte.class))
			property = new ValueProperty<Byte>(id, buf.readByte());
		else if (clazz.equals(short.class))
			property = new ValueProperty<Short>(id, buf.readShort());
		else if (clazz.equals(int.class))
			property = new ValueProperty<Integer>(id, buf.readInt());
		else if (clazz.equals(long.class))
			property = new ValueProperty<Long>(id, buf.readLong());
		else if (clazz.equals(float.class))
			property = new ValueProperty<Float>(id, buf.readFloat());
		else if (clazz.equals(double.class))
			property = new ValueProperty<Double>(id, buf.readDouble());
		else if (clazz.equals(char.class))
			property = new ValueProperty<Character>(id, buf.readChar());
		else if (clazz.equals(String.class))
			property = new ValueProperty<String>(id, buf.readString());
		else if (clazz.equals(byte[].class)) {
			int size = buf.readInt();
			property = new ValueProperty<byte[]>(id, buf.readBytes(size));
		} else if (Collection.class.isAssignableFrom(clazz)) {
			int len = buf.readInt();
			int size = buf.readShort();
			Collection<SimulatorProperty> collection = new ArrayList<SimulatorProperty>(size);
			for (int i = 0; i < size; i++) {
				short symbolId = buf.readShort();
				SimulatorProperty simulatorProperty = readSimulatorProperty(buf, SimulatorPropertySymbolContext.getPropertySymbol(symbolId));
				if (simulatorProperty != null)
					collection.add(simulatorProperty);
			}
			property = new ValueProperty<Collection<SimulatorProperty>>(propertySymbol.getId(), collection);
		} else if (Map.class.isAssignableFrom(clazz)) {
			int len = buf.readInt();
			int size = buf.readShort();
			Map<String, SimulatorProperty> map = new LinkedHashMap<String, SimulatorProperty>();
			for (int i = 0; i < size; i++) {
				String key = buf.readString();
				short symbolId = buf.readShort();
				SimulatorProperty simulatorProperty = readSimulatorProperty(buf, SimulatorPropertySymbolContext.getPropertySymbol(symbolId));
				if (simulatorProperty != null)
					map.put(key, simulatorProperty);
			}
			property = new ValueProperty<Map<String, SimulatorProperty>>(propertySymbol.getId(), map);
		} else if (PropertyDictionary.class.equals(clazz)) {
			PropertyDictionary pd = new PropertyDictionary();
			int length = buf.readInt();
			pd.loadDictionary(buf.readBytes(length));
			property = new ValueProperty<PropertyDictionary>(propertySymbol.getId(), pd);
		}
		return property;
	}

	public byte[] toByteArray() {
		ByteArrayReadWriteBuffer buf = new ByteArrayReadWriteBuffer();
		buf.writeShort((short) dictionary.size());
		for (Entry<Short, SimulatorProperty<?>> e : dictionary.entrySet()) {
			try {
				writeSimulatorProperty(buf, e.getValue());
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
		return buf.getData();
	}

	public byte[] toByteArrayWithFilter(Collection<Short> filter) {
		ByteArrayReadWriteBuffer buf = new ByteArrayReadWriteBuffer();
		buf.writeShort((short) dictionary.size());
		for (Entry<Short, SimulatorProperty<?>> e : dictionary.entrySet()) {
			try {
				if (!filter.contains(e.getKey())) {
					writeSimulatorProperty(buf, e.getValue());
				}
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
		return buf.getData();
	}

	private void writeSimulatorProperty(ByteArrayReadWriteBuffer buf, SimulatorProperty<?> prop) {
		short id = prop.getId();
		Object v = prop.getValue();
		Class<?> clazz = SimulatorPropertySymbolContext.getPropertySymbol(id).getClassType();
		buf.writeShort(id);
		if (clazz.equals(byte.class))
			buf.writeByte(Byte.valueOf(v.toString()));
		else if (clazz.equals(short.class))
			buf.writeShort((Short) v);
		else if (clazz.equals(int.class))
			buf.writeInt((Integer) v);
		else if (clazz.equals(long.class))
			buf.writeLong((Long) v);
		else if (clazz.equals(float.class))
			buf.writeFloat((Float) v);
		else if (clazz.equals(double.class))
			buf.writeDouble((Double) v);
		else if (clazz.equals(char.class))
			buf.writeChar((Character) v);
		else if (clazz.equals(String.class))
			buf.writeString((String) v);
		else if (clazz.equals(byte[].class)) {
			byte[] data = (byte[]) v;
			buf.writeInt(data.length);
			buf.writeBytes(data);
		} else if (Collection.class.isAssignableFrom(clazz)) {
			Collection<SimulatorProperty> collection = (Collection<SimulatorProperty>) v;
			int oldPos = buf.position();
			buf.writeInt(0);
			buf.writeShort((short) collection.size());
			for (SimulatorProperty property : collection) {
				writeSimulatorProperty(buf, property);
			}
			int newPos = buf.position();
			int len = newPos - oldPos;
			buf.writeInt(oldPos, len);

		} else if (Map.class.isAssignableFrom(clazz)) {
			int oldPos = buf.position();
			buf.writeInt(0);
			Map<String, SimulatorProperty> map = (Map<String, SimulatorProperty>) v;
			buf.writeShort((short) map.size());
			for (Entry<String, SimulatorProperty> e : map.entrySet()) {
				buf.writeString(e.getKey());
				writeSimulatorProperty(buf, e.getValue());
			}
			int newPos = buf.position();
			int len = newPos - oldPos;
			buf.writeInt(oldPos, len);
		} else if (PropertyDictionary.class.equals(clazz)) {
			PropertyDictionary pd = (PropertyDictionary) v;
			byte[] byteArray = pd.toByteArray();
			buf.writeInt(byteArray.length);
			buf.writeBytes(byteArray);
		}
	}

	public final void setDictionary(Map<Short, SimulatorProperty<?>> dictionary) {
		this.dictionary = new HashMap<Short, SimulatorProperty<?>>(dictionary);
	}

	public final <T> SimulatorProperty<T> getProperty(final Short id) {
		@SuppressWarnings("unchecked")
		SimulatorProperty<T> property = (SimulatorProperty<T>) dictionary.get(id);
		return property;
	}

	public final <T extends Number> ValueProperty<T> getNumberValueProperty(final Short id, Class<T> classType) {
		@SuppressWarnings("unchecked")
		ValueProperty<T> property = (ValueProperty<T>) dictionary.get(id);
		return property;
	}

	public final boolean hasProperty(SimulatorProperty<?> property) {
		return hasProperty(property.getId());
	}

	public final boolean hasProperty(final Short id) {
		return dictionary.containsKey(id);
	}

	// how do the client know that the id is mapping to a ValueProperty???
	public final <T> ValueProperty<T> getValueProperty(final Short id) {
		@SuppressWarnings("unchecked")
		ValueProperty<T> property = (ValueProperty<T>) dictionary.get(id);
		return property;
	}

	// how do the client know that the id is mapping to a IndexProperty???
	public final <T> IndexProperty<T> getIndexProperty(final Short id) {
		@SuppressWarnings("unchecked")
		IndexProperty<T> property = (IndexProperty<T>) dictionary.get(id);
		return property;
	}

	public final <T> void addProperty(SimulatorProperty<T> property) {
		dictionary.put(property.getId(), property);
	}

	public final <T> void addProperty(ValueProperty<T> property) {
		dictionary.put(property.getId(), property);
	}

	public final <T> void addProperty(IndexProperty<T> property) {
		dictionary.put(property.getId(), property);
	}

	public final void removeProperty(final Short id) {
		dictionary.remove(id);
	}

	public boolean contains(final Short id) {
		return dictionary.containsKey(id);
	}

	public final <T> T getValue(final Short id) {
		SimulatorProperty<T> property = getProperty(id);
		if (property == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("get a non-existent field in this PropertyDictionary. symbol id " + id + "\n" + DebugUtil.printStack());
			}

			return defaultValue(id);
		}
		return property.getValue();
	}

	private <T> T defaultValue(final Short id) {
		Class<?> clazz = SimulatorPropertySymbolContext.getPropertySymbol(id).getClassType();
		if (clazz.equals(byte.class))
			return (T) ByteNull;
		else if (clazz.equals(short.class))
			return (T) ShortNull;
		else if (clazz.equals(int.class))
			return (T) IntNull;
		else if (clazz.equals(long.class))
			return (T) LongNull;
		else if (clazz.equals(float.class))
			return (T) FloatNull;
		else if (clazz.equals(double.class))
			return (T) DoubleNull;
		else if (clazz.equals(char.class))
			return (T) CharNull;
		else if (clazz.equals(String.class))
			return (T) StringNull;
		else if (clazz.equals(Map.class))
			return (T) MapNull;
		else if (clazz.equals(Collection.class))
			return (T) CollectionNull;
		else if (clazz.equals(PropertyDictionary.class))
			return (T) DictionaryNull;
		return null;
	}

	public final <T> void setValue(final Short id, T value) {
		@SuppressWarnings("unchecked")
		ValueProperty<T> property = (ValueProperty<T>) getProperty(id);
		if (property == null) {
			throw new NullPointerException();
		}
		property.setValue(value);
	}

	public final <T> void setOrPutValue(final Short id, T value) {
		@SuppressWarnings("unchecked")
		ValueProperty<T> property = (ValueProperty<T>) getProperty(id);
		if (property == null)
			addProperty(new ValueProperty<T>(id, value));
		else
			property.setValue(value);
	}

	public final <T> void addOrPutValue(final Short id, T value) {
		add(new ValueProperty<T>(id, value));
	}

	public final Collection<SimulatorProperty<?>> getProperties() {
		Collection<SimulatorProperty<?>> values = this.dictionary.values();
		return values;
	}

	/**
	 * 数据字典加上一个ValueProperty(如果原来数据字典中存在相同的类型的valueProperty(id相等),则值相加)。
	 * 没有就什么也不操作
	 * 
	 * @param valueProperty
	 */
	public final void addJustOnly(ValueProperty<?> otherValueProperty) {
		short id = otherValueProperty.getId();
		ValueProperty<?> vp = getValueProperty(id);
		if (vp != null) {
			PropertySymbol symbol = SimulatorPropertySymbolContext.getPropertySymbol(id);
			Class<?> clazz = symbol.getClassType();
			if (clazz == byte.class) {
				ValueProperty<Byte> _vp = (ValueProperty<Byte>) otherValueProperty;
				byte oldValue = (Byte) vp.getValue();
				byte otherValue = _vp.getValue();
				((ValueProperty<Byte>) vp).setValue((byte) (oldValue + otherValue));
			} else if (clazz == short.class) {
				ValueProperty<Short> _vp = (ValueProperty<Short>) otherValueProperty;
				short oldValue = (Short) vp.getValue();
				short otherValue = _vp.getValue();
				((ValueProperty<Short>) vp).setValue((short) (oldValue + otherValue));
			} else if (clazz == int.class) {
				ValueProperty<Integer> _vp = (ValueProperty<Integer>) otherValueProperty;
				int oldValue = (Integer) vp.getValue();
				int otherValue = _vp.getValue();
				((ValueProperty<Integer>) vp).setValue(oldValue + otherValue);
			} else if (clazz == Long.class) {
				ValueProperty<Long> _vp = (ValueProperty<Long>) otherValueProperty;
				long oldValue = (Long) vp.getValue();
				long otherValue = _vp.getValue();
				((ValueProperty<Long>) vp).setValue(oldValue + otherValue);
			}
		}
	}

	/**
	 * 数据字典加上一个ValueProperty(如果原来数据字典中存在相同的类型的valueProperty(id相等),则值相加)
	 * 
	 * @param valueProperty
	 */
	@SuppressWarnings("unchecked")
	public final void add(ValueProperty<?> otherValueProperty) {
		short id = otherValueProperty.getId();
		ValueProperty<?> vp = getValueProperty(id);
		if (vp != null) {
			PropertySymbol symbol = SimulatorPropertySymbolContext.getPropertySymbol(id);
			Class<?> clazz = symbol.getClassType();
			if (clazz == byte.class) {
				ValueProperty<Byte> _vp = (ValueProperty<Byte>) otherValueProperty;
				byte oldValue = (Byte) vp.getValue();
				byte otherValue = _vp.getValue();
				((ValueProperty<Byte>) vp).setValue((byte) (oldValue + otherValue));
			} else if (clazz == short.class) {
				ValueProperty<Short> _vp = (ValueProperty<Short>) otherValueProperty;
				short oldValue = (Short) vp.getValue();
				short otherValue = _vp.getValue();
				((ValueProperty<Short>) vp).setValue((short) (oldValue + otherValue));
			} else if (clazz == int.class) {
				ValueProperty<Integer> _vp = (ValueProperty<Integer>) otherValueProperty;
				int oldValue = (Integer) vp.getValue();
				int otherValue = _vp.getValue();
				((ValueProperty<Integer>) vp).setValue(oldValue + otherValue);
			} else if (clazz == Long.class) {
				ValueProperty<Long> _vp = (ValueProperty<Long>) otherValueProperty;
				long oldValue = (Long) vp.getValue();
				long otherValue = _vp.getValue();
				((ValueProperty<Long>) vp).setValue(oldValue + otherValue);
			}
		} else {
			ValueProperty<?> otherValuePropertyClone = (ValueProperty<?>) otherValueProperty.clone();
			addProperty(otherValuePropertyClone);
		}
	}

	/**
	 * 2个数据字典相加(重叠部分值相加，非重叠部分取并集)，返回一个新的数据字典，原来的2个数据字典没有任何变化
	 * 
	 * @param otherDP
	 * @return
	 */
	public final PropertyDictionary add(PropertyDictionary otherDP) {
		Preconditions.checkNotNull(otherDP);
		PropertyDictionary pd = this.clone();

		Set<Entry<Short, SimulatorProperty<?>>> otherEntrySet = otherDP.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : otherEntrySet) {
			if (pd.dictionary.containsKey(entry.getKey())) {
				SimulatorProperty<?> clone = entry.getValue().clone();
				ValueProperty<?> valueProperty = (ValueProperty<?>) clone;
				pd.add(valueProperty);
			} else {
				pd.addProperty(entry.getValue().clone());
			}
		}
		return pd;
	}

	public void copyFrom(PropertyDictionary other) {
		for (Entry<Short, SimulatorProperty<?>> entry : other.dictionary.entrySet()) {
			setOrPutValue(entry.getKey(), entry.getValue().getValue());
		}
	}

	/**
	 * 2个数据字典相减(重叠部分值相减，非重叠部分没有变化)，被减的数据字典发生变化
	 * 
	 * @param otherDP
	 * @return
	 */
	public final PropertyDictionary mutableSubtract(PropertyDictionary otherDP) {
		Set<Short> otherDPKeySet = otherDP.dictionary.keySet();

		for (Short id : otherDPKeySet) {
			SimulatorProperty<?> sp = dictionary.get(id);
			if (sp != null) {
				PropertySymbol symbol = SimulatorPropertySymbolContext.getPropertySymbol(id);
				Class<?> clazz = symbol.getClassType();
				if (clazz == byte.class) {
					byte oldValue = (Byte) dictionary.get(id).getValue();
					byte otherValue = (Byte) otherDP.dictionary.get(id).getValue();
					sp = processByteProperty((byte) (oldValue - otherValue), id);
				} else if (clazz == short.class) {
					short oldValue = (Short) dictionary.get(id).getValue();
					short otherValue = (Short) otherDP.dictionary.get(id).getValue();
					sp = processShortProperty((short) (oldValue - otherValue), id);
				} else if (clazz == int.class) {
					int oldValue = (Integer) dictionary.get(id).getValue();
					int otherValue = (Integer) otherDP.dictionary.get(id).getValue();
					sp = processIntProperty(oldValue - otherValue, id);
				} else if (clazz == Long.class) {
					long oldValue = (Long) dictionary.get(id).getValue();
					long otherValue = (Long) otherDP.dictionary.get(id).getValue();
					ValueProperty<Long> _sp = new ValueProperty<Long>();
					_sp.setValue(oldValue - otherValue);
				} else {
					logger.error("不支持的数据类型：" + clazz);
					throw new RuntimeException();
				}
				dictionary.remove(id);
				dictionary.put(id, sp);
			}
		}
		return this;
	}

	/**
	 * 数据字典中每一个值乘以一个系数，返回一个新的数据字典 万分比
	 * 
	 * @param rate
	 * @return
	 */
	public PropertyDictionary multiply(int rate) {
		PropertyDictionary pd = new PropertyDictionary();
		float rateF = rate / 10000f;

		Set<Short> keySet = dictionary.keySet();
		for (Short id : keySet) {
			SimulatorProperty<?> sp = processMultiplyProperty(rateF, id);
			pd.addProperty(sp);
		}

		return pd;
	}

	private SimulatorProperty<?> processMultiplyProperty(float rateF, Short id) {
		PropertySymbol symbol = SimulatorPropertySymbolContext.getPropertySymbol(id);
		Class<?> clazz = symbol.getClassType();
		SimulatorProperty<?> sp = null;
		if (clazz == byte.class) {
			byte oldValue = (Byte) dictionary.get(id).getValue();
			byte newValue = (byte) (oldValue * rateF);
			sp = processByteProperty(newValue, id);
		} else if (clazz == short.class) {
			short oldValue = (Short) dictionary.get(id).getValue();
			short newValue = (short) (oldValue * rateF);
			sp = processShortProperty(newValue, id);
		} else if (clazz == int.class) {
			int oldValue = (Integer) dictionary.get(id).getValue();
			int newValue = (int) (oldValue * rateF);
			sp = processIntProperty(newValue, id);
		} else {
			throw new RuntimeException("不支持的类型");
		}
		sp.setId(id);
		return sp;
	}

	private ValueProperty<Integer> processIntProperty(int newValue, Short id) {
		ValueProperty<Integer> _sp = new ValueProperty<Integer>();
		_sp.setValue(newValue);
		return _sp;
	}

	private ValueProperty<Short> processShortProperty(short newValue, Short id) {
		ValueProperty<Short> _sp = new ValueProperty<Short>();
		_sp.setValue(newValue);
		return _sp;
	}

	private ValueProperty<Byte> processByteProperty(byte newValue, Short id) {
		ValueProperty<Byte> _sp = new ValueProperty<Byte>();
		_sp.setValue(newValue);
		return _sp;
	}

	public void clear() {
		this.dictionary.clear();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Short, SimulatorProperty<?>> entry : this.dictionary.entrySet()) {
			Short id = entry.getKey();
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			sb.append("[id=" + id + ",name=" + SimulatorPropertySymbolContext.getPropertySymbolName(id) + ", value=" + valueProperty.getValue() + "]");
		}
		return sb.toString();
	}

}
