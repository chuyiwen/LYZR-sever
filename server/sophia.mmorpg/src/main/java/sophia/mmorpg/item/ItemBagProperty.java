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
package sophia.mmorpg.item;

public interface ItemBagProperty {
	String getName();
	
	String getDescription();
	
	String getIconId();
	
	byte getQuality();
	/**ItemRef 属性*/
	byte getBindType();
	/**Item 属性*/
	byte getBindStatus();
	void setBindStatus(byte bindStatus);
	boolean binded();
	
	int getItemOrderId();
	
	byte getItemType();
	
	boolean usable();
	byte usableType();
	boolean canStack();
	/**Item 属性*/
	int getNumber();
	void setNumber(int number);
	/**ItemRef 属性*/
	int getMaxStackNumber();
	/**ItemRef 属性*/
	boolean isNonPropertyItemRef();
	/**Item 属性*/
	boolean isNonPropertyItem();
	
	boolean canSale();
	
	int getSalePrice();
	
	byte getSaleCurrency();
	
	boolean discardable();
	
	boolean isNonThrow();
}
