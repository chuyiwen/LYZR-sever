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
package newbee.morningGlory.mmorpg.sprite.buff;

import groovy.lang.Closure;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGFightSpriteBuffRef extends AbstractGameRefObjectBase{
	private static final long serialVersionUID = -4015889039332290687L;
	
	private PropertyDictionary effectProperty = new PropertyDictionary();
	private PropertyDictionary periodEffectProperty = new PropertyDictionary();
	private Closure<RuntimeResult> attachClosure;
	
	private Closure<RuntimeResult> tickAffectClosure;
	
	private Closure<RuntimeResult> detachClosure;
	
	public MGFightSpriteBuffRef() {
		
	}
	
	public String getName() {
		return MGPropertyAccesser.getName(getProperty());
	}
	
	public String getDescription() {
		return MGPropertyAccesser.getDescription(getProperty());
	}
	
	/**
	 * buff的生命周期时间
	 * @return
	 */
	public long getDuration() {
		return  MGPropertyAccesser.getDuration(getProperty());
	}
	
	/**
	 * {@link MGFightSpriteBuffDurationType}
	 * @return
	 */
	public byte getDurationType() {
		return MGPropertyAccesser.getDurationType(getProperty());
	}
	public boolean isDependOnDurationTime(){
		return MGPropertyAccesser.getDuration(getProperty()) != -1;
	}
	/**
	 * 是否，是以周期间隔时间作用的buff
	 * @return
	 */
	public boolean isPeriodAffectBuff() {
		return MGPropertyAccesser.getIsPeriodAffectBuff(getProperty()) == 1;
	}
	
	/**
	 * [option] 周期间隔时间作用的时间。当{@link #isPeriodAffectBuff()} 为true时，有意义
	 * @return
	 */
	public long getPeriodAffectTime() {
		return MGPropertyAccesser.getPeriodAffectTime(getProperty());
	}
	
	public boolean isClearOnDeadBuff() {
		return MGPropertyAccesser.getIsClearOnDeadBuff(getProperty()) == 1;
	}
	
	public boolean isNeedSaveBuff() {
		return MGPropertyAccesser.getIsNeedSaveBuff(getProperty()) == 1;
	}
	
	public boolean isChangeFightValueBuff(){
		return MGPropertyAccesser.getIsChangeFightValueBuff(getProperty()) == 1;
	}
	public boolean isPkModelCheckBuff(){
		return MGPropertyAccesser.getIsPkModelCheck(getProperty()) == 1;
	}
	public boolean isPositiveBuff(){
		return MGPropertyAccesser.getIsPositiveBuff(getProperty()) == 1;
	}
	
	public int getGroupId() {
		return MGPropertyAccesser.getBuffGroupId(getProperty());
	}
	
	public int getWeightOfGroup() {
		return MGPropertyAccesser.getWeightOfGroup(getProperty());
	}
	
	/**
	 * {@link MGFightSpriteBuffAttachGroupRuleType}
	 * @return
	 */
	public byte getAttachGorupRuleType() {
		return MGPropertyAccesser.getAttachGorupRuleType(getProperty());
	}
	
	/**
	 * {@link MGFightSpriteBuffAttachRepeatRuleType}
	 * @return
	 */
	public byte getAttachRepeatRuleType() {
		return MGPropertyAccesser.getAttachRepeatRuleType(getProperty());
	}
	
	public Closure<RuntimeResult> getAttachClosure() {
		return attachClosure;
	}

	public void setAttachClosure(Closure<RuntimeResult> attachClosure) {
		this.attachClosure = attachClosure;
	}

	public Closure<RuntimeResult> getTickAffectClosure() {
		return tickAffectClosure;
	}

	public void setTickAffectClosure(Closure<RuntimeResult> tickAffectClosure) {
		this.tickAffectClosure = tickAffectClosure;
	}

	public Closure<RuntimeResult> getDetachClosure() {
		return detachClosure;
	}

	public void setDetachClosure(Closure<RuntimeResult> detachClosure) {
		this.detachClosure = detachClosure;
	}

	public PropertyDictionary getEffectProperty() {
		return effectProperty;
	}

	public void setEffectProperty(PropertyDictionary effectProperty) {
		this.effectProperty = effectProperty;
	}

	public PropertyDictionary getPeriodEffectProperty() {
		return periodEffectProperty;
	}

	public void setPeriodEffectProperty(PropertyDictionary periodEffectProperty) {
		this.periodEffectProperty = periodEffectProperty;
	}

	@Override
	public String toString() {
		return "MGFightSpriteBuffRef [id=" + id + "]";
	}
}
