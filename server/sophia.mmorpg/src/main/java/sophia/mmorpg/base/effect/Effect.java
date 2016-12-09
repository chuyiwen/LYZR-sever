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
package sophia.mmorpg.base.effect;

import sophia.game.component.GameObject;

/**
 * <b>影响效果能力抽象基类</b><br>
 *
 * 着重说明的是效果的T，即影响的目标对象和实际作用的目标对象，通常语义都是同一对象。但实质上，它们可能不是同一对象。<br>
 * 
 * 比如：<br>
 * 	1. 宠物套装的效果，它可能：有一部分效果是直接影响目标（宠物）；另一部分是影响宠物的所有玩家；其他部分是影响套装成员；<br>
 *  2. 战斗技能的效果，它的影响目标对象是个抽象的战斗对象（FightObject)。但实质它可能影响的对象是：战斗对象所出的场景、攻击者、被攻击者、某区域的战斗对象etc。<br>
 */
public interface Effect<T extends GameObject> {
	/**
	 * 获取该效果是否对指定的目标对象，可以施加效果影响。如果可以，返回true；否则，返回false;
	 * 
	 * @param affectTarget 是否可以施加效果影响。如果可以，返回true；否则，返回false;
	 * @return
	 */
	boolean affectable(T affectTarget);
	/**
	 * 对指定的目标对象，附加效果影响
	 * @param affectTarget 指定的目标对象
	 * @return
	 */
	boolean append(T affectTarget);
	/**
	 * 取消对指定的目标附加效果影响
	 * @param affectTarget 指定的目标对象
	 * @return
	 */
	boolean cancel(T affectTarget);
	/**
	 * 对指定的目标对象，施加效果影响。如果指定的目标对象，当前状态可以施加该效果影响，将执行该影响数据，并返回true；否则，不执行该影响数据，返回false;
	 * @param affectTarget 指定的目标对象
	 * @return 返回true时，执行该影响数据;返回false,不执行该影响数据;
	 */
	boolean affect(T affectTarget);
	/**
	 * 获取该效果能力是否可去除。如果可以，返回true；否则，返回false;
	 * @return
	 */
	boolean disaffectable();
	/**
	 * 去除目标的该效果能力。如果可以，返回true；否则，返回false;
	 * @param affectTarget
	 * @return
	 */
	boolean disaffect(T affectTarget);
//	/**
//	 * 是否是战斗属性效果。如果是，返回true；否则，返回false;
//	 * @return
//	 */
//	boolean isFightPropertyEffect();
//	/**
//	 * Effect效果类型
//	 * @return EffectType {@link EffectType}
//	 */
//	EffectType getEffectType();
}
