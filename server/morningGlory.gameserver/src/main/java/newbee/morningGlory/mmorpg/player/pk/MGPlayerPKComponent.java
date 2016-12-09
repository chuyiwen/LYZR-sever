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
package newbee.morningGlory.mmorpg.player.pk;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.pk.event.C2G_Pk_Model;
import newbee.morningGlory.mmorpg.player.pk.event.G2C_Name_Color;
import newbee.morningGlory.mmorpg.player.pk.event.G2C_Pk_Model;
import newbee.morningGlory.mmorpg.player.pk.event.G2C_Rookie_Protection;
import newbee.morningGlory.mmorpg.player.pk.event.PkEventDefines;
import newbee.morningGlory.mmorpg.player.pk.ref.MGRefIdContain;
import newbee.morningGlory.mmorpg.player.pk.ref.MGScenePKDropRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.task.PeriodicTaskHandle;
import sophia.foundation.task.Task;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.GameObject;
import sophia.game.component.communication.GameEvent;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.BeforeAttack_GE;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.RookieProtection_GE;
import sophia.mmorpg.base.sprite.state.global.PKState;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.property.PlayerNameColor;
import sophia.mmorpg.ref.GameConstantRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * pk组件
 */
public class MGPlayerPKComponent<T extends FightSprite> extends ConcreteComponent<T> {

	private static final Logger logger = Logger.getLogger(MGPlayerPKComponent.class);

	public static final String Tag = "MGPlayerPKComponent";
	private static final String BeforeAttack_GE_Id = BeforeAttack_GE.class.getSimpleName();
	private static final String AfterAttack_GE_Id = AfterAttack_GE.class.getSimpleName();
	private static final String PlayerDead_GE_Id = PlayerDead_GE.class.getSimpleName();
	private static final String LeaveWorld_GE_Id = LeaveWorld_GE.class.getSimpleName();
	private static final String EnterWorld_SceneReady_GE = EnterWorld_SceneReady_GE.class.getSimpleName();

	private PeriodicTaskHandle handle;
	private PeriodicTaskHandle pkValuehandle;

	private long exitPkStateTime = 5 * 60 * 1000l;
	private static final int PlayerDead_Bag_Drop_1 = 30; // 30% 概率
	private static final int PlayerDead_Bag_Drop_2 = 50; // 50%概率
	private static final int PlayerDead_Bag_Equip = 20; // 20%概率
	private static final int PlayerDead_Bag_Drop_Number = 3; // 20掉落数量

	private Map<String, Integer> lootItemRefMap = new HashMap<>();

	public Map<String, Integer> getLootItemRefMap() {
		return lootItemRefMap;
	}

	private MGPlayerPKMgr playerPKMgr = new MGPlayerPKMgr(MGPlayerPKModel.PeaceModel);

	public MGPlayerPKMgr getPlayerPKMgr() {
		return playerPKMgr;
	}

	public void setPlayerPKMgr(MGPlayerPKMgr playerPKMgr) {
		this.playerPKMgr = playerPKMgr;
	}

	@Override
	public void setParent(GameObject owner) {
		super.setParent(owner);
	}

	@Override
	public void ready() {
		addActionEventListener(PkEventDefines.C2G_Pk_Model);
		addActionEventListener(PkEventDefines.C2G_Name_Color);
		addInterGameEventListener(BeforeAttack_GE_Id);
		addInterGameEventListener(AfterAttack_GE_Id);
		addInterGameEventListener(PlayerDead_GE_Id);
		addInterGameEventListener(LeaveWorld_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE);
		addInterGameEventListener(RookieProtection_GE.RookieProtectionId);

		if (getParent() instanceof Player) {
			Player player = (Player) getParent();
			calcutePKValue();
			short nameColor = getCurrentPlayerNameColor();
			MGPropertyAccesser.setOrPutNameColor(player.getSenceProperty(), nameColor);
		}
	}

	@Override
	public void suspend() {
		removeActionEventListener(PkEventDefines.C2G_Pk_Model);
		removeActionEventListener(PkEventDefines.C2G_Name_Color);
		removeInterGameEventListener(BeforeAttack_GE_Id);
		removeInterGameEventListener(AfterAttack_GE_Id);
		removeInterGameEventListener(PlayerDead_GE_Id);
		removeInterGameEventListener(LeaveWorld_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE);
		removeInterGameEventListener(RookieProtection_GE.RookieProtectionId);

		if (handle != null)
			handle.cancel();
		if (pkValuehandle != null)
			pkValuehandle.cancel();
	}

	public MGPlayerPKComponent() {
	}

	public static boolean isNeededToProtectRookie(Player attacker, Player target) {
		GameConstantRef constantRef = (GameConstantRef) GameRoot.getGameRefObjectManager().getManagedObject(GameConstantRef.gameConstantRefKey);
		int rookieLevel = MGPropertyAccesser.getRookieProtectionLevel(constantRef.getProperty());
		if (attacker.getLevel() < rookieLevel || target.getLevel() < rookieLevel) {
			return true;
		}
		return false;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		FightSprite player = getConcreteParent();
		if (event.isId(RookieProtection_GE.RookieProtectionId)) {
			FightSprite attacker = ((RookieProtection_GE) event.getData()).getAttacker();
			FightSprite target = ((RookieProtection_GE) event.getData()).getTarget();
			if (attacker instanceof Player && target instanceof Player && isNeededToProtectRookie((Player) attacker, (Player) target)) {
				G2C_Rookie_Protection res = new G2C_Rookie_Protection((Player) attacker, (Player) target);
				Player attackerP = (Player) attacker;
				Player targetP = (Player) target;
				GameRoot.sendMessage(attackerP.getIdentity(), res);
				GameRoot.sendMessage(targetP.getIdentity(), res);
				if (logger.isDebugEnabled()) {
					logger.debug("protect rookie@I'm " + getConcreteParent() + res);
				}
			}

		} else if (event.isId(AfterAttack_GE_Id)) {
			FightSprite attacker = ((AfterAttack_GE) event.getData()).getAttacker();
			FightSprite target = ((AfterAttack_GE) event.getData()).getTarget();
			if (attacker instanceof Monster) {
				Monster monster = (Monster) attacker;
				if (!monster.getMonsterRef().isRegularMonster() && monster.getOwner() != null) {
					attacker = monster.getOwner();
				}
			}
			String crtSceneId = player.getCrtScene().getRef().getId();
			if (attacker instanceof Player && target instanceof Player) {

				if (!MGRefIdContain.getPKDropMap().containsKey(crtSceneId)) { // 如果是默认掉落
					attackPlayer(attacker, target, 10, true);
				} else {
					String pkDropRefId = MGRefIdContain.getPKDropMap().get(crtSceneId);
					MGScenePKDropRef ref = (MGScenePKDropRef) GameRoot.getGameRefObjectManager().getManagedObject(pkDropRefId);
					int attackAddPKValue = ref.getAttackAddPkValue();
					boolean centerPkState = ref.isEnterPKState();
					attackPlayer(attacker, target, attackAddPKValue, centerPkState);

				}
			}
		} else if (event.isId(PlayerDead_GE_Id)) {
			FightSprite attacker = ((PlayerDead_GE) event.getData()).getAttacker();
			Player target = ((PlayerDead_GE) event.getData()).getPlayer();
			// 自杀不掉物品
			if (StringUtils.equals(attacker.getId(), getConcreteParent().getId())) {
				return;
			}

			if (!StringUtils.equals(target.getId(), getConcreteParent().getId())) {
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("PlayerDead_GE, target=" + target);
			}

			if (attacker instanceof Monster) {
				Monster monster = (Monster) attacker;
				if (!monster.getMonsterRef().isRegularMonster() && monster.getOwner() != null) {
					attacker = monster.getOwner();
				}
			}

			if (attacker instanceof Player && target instanceof Player) {
				attacker = (Player) attacker;
				target = (Player) target;
				String crtSceneId = player.getCrtScene().getRef().getId();
				if (!MGRefIdContain.getPKDropMap().containsKey(crtSceneId)) { // 如果是默认掉落
					killedPlayer(attacker, target, 100, true);
				} else {
					String pkDropRefId = MGRefIdContain.getPKDropMap().get(crtSceneId);
					MGScenePKDropRef ref = (MGScenePKDropRef) GameRoot.getGameRefObjectManager().getManagedObject(pkDropRefId);
					int killAddPKValue = ref.getKillAddPkValue();
					killedPlayer(attacker, target, killAddPKValue, ref.isUseDefaultDrop());
					String dropMethod = ref.getDropMethod();
					if (!ref.isUseDefaultDrop() && StringUtils.isNotEmpty(dropMethod)) {
						String className = "newbee.morningGlory.mmorpg.player.pk.PkDropMgr";
						try {
							Class<?> clazz = Class.forName(className);
							Method method = clazz.getMethod(dropMethod, new Class[] { Player.class, Player.class, GameRefObject.class });
							method.invoke(method, target, (Player) attacker, ref);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
		} else if (event.isId(LeaveWorld_GE_Id)) {
			if (getConcreteParent().getFightSpriteStateMgr().isState(PKState.PKState_Id)) {
				getConcreteParent().cancelState(PKState.PKState_Id);
				changePKModel(MGPlayerPKModel.PeaceModel);
			}
		} else if (event.isId(EnterWorld_SceneReady_GE)) {
			sendPKNameColorEvent();
			changePKModel(MGPlayerPKModel.PeaceModel);
		}

	}

	@SuppressWarnings("rawtypes")
	public void attackPlayer(FightSprite attacker, FightSprite target, int pkValue, boolean centerPkState) {

		if (StringUtils.equals(getConcreteParent().getId(), target.getId())) {
			if (!target.getFightSpriteStateMgr().isState(PKState.PKState_Id)) {
				int targetPkValue = MGPropertyAccesser.getPkValue(target.getProperty());
				MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) attacker.getTagged(MGPlayerPKComponent.Tag);
				if (targetPkValue <= 200 && !attacker.getFightSpriteStateMgr().isState(PKState.PKState_Id)) {
					if (centerPkState) {
						attacker.changeState(PKState.PKState_Id);
						pkComponent.updateAttackerTime();
					}
					pkComponent.addPkValue(attacker, pkValue);
				} else {
					pkComponent.updateAttackerTime();
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void killedPlayer(FightSprite attackerSprite, FightSprite targetSpirte, int pkValue, boolean isUseDefaultDrop) {
		if (!(attackerSprite instanceof Player) || !(targetSpirte instanceof Player)) {
			return;
		}

		Player target = (Player) targetSpirte;
		Player attacker = (Player) attackerSprite;
		List<Loot> lootList = new ArrayList<>();
		if (StringUtils.equals(getConcreteParent().getId(), target.getId())) {
			int targetPkValue = MGPropertyAccesser.getPkValue(target.getProperty());
			if (targetPkValue <= 200 && !target.getFightSpriteStateMgr().isState(PKState.PKState_Id)) {
				MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) attacker.getTagged(MGPlayerPKComponent.Tag);
				pkComponent.addPkValue(attacker, pkValue);
				if (isUseDefaultDrop) {
					List<Loot> tmpLootList = ItemFacade.dropItem(target, (Player) attacker, PlayerDead_Bag_Drop_1, PlayerDead_Bag_Drop_Number, ItemOptSource.Pk);
					if (tmpLootList != null) {
						lootList.addAll(tmpLootList);
					}
				}
			} else if (targetPkValue > 200 && isUseDefaultDrop) {
				List<Loot> tmpLootList = target.getPlayerEquipBodyConponent().dropItem((Player) attacker, PlayerDead_Bag_Equip, ItemOptSource.Pk);
				if (tmpLootList != null) {
					lootList.addAll(tmpLootList);
				}

				tmpLootList = ItemFacade.dropItem(target, (Player) attacker, PlayerDead_Bag_Drop_2, PlayerDead_Bag_Drop_Number, ItemOptSource.Pk);
				if (tmpLootList != null) {
					lootList.addAll(tmpLootList);
				}
			}

			if (target.getFightSpriteStateMgr().isState(PKState.PKState_Id)) {
				target.cancelState(PKState.PKState_Id);
			}

			changeNameColorBroadcast();
			sendPKNameColorEvent();
		}

		addKilledLootInfo(lootList);
	}

	public void addKilledLootInfo(List<Loot> lootList) {
		if (logger.isDebugEnabled()) {
			logger.debug("addKilledLootInfo, player=" + getConcreteParent());
		}

		lootItemRefMap.clear();

		if (lootList == null || lootList.size() == 0) {
			return;
		}

		FightSprite owner = getConcreteParent();
		if (owner instanceof Player) {
			for (Loot loot : lootList) {
				String itemRefId;
				int count = 1;

				if (loot.getItem() != null) {
					itemRefId = loot.getItem().getItemRefId();
					count = loot.getItem().getNumber();
				} else {
					itemRefId = loot.getItemPair().getItemRefId();
					count = loot.getItemPair().getNumber();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("addKilledLootInfo, pk loot itemRefId=" + itemRefId + ", count=" + count);
				}

				Integer integer = lootItemRefMap.get(itemRefId);
				if (integer != null) {
					count += integer.intValue();
				}

				lootItemRefMap.put(itemRefId, count);

				if (logger.isDebugEnabled()) {
					logger.debug("addKilledLootInfo, lootItemRefMap size=" + lootItemRefMap.size());
				}
			}
		}
	}

	public void addPkValue(FightSprite player, int pkValue) {
		int attackerPkValue = MGPropertyAccesser.getPkValue(player.getProperty());
		attackerPkValue += pkValue;
		MGPropertyAccesser.setOrPutPkValue(player.getProperty(), attackerPkValue);
		PropertyDictionary property = new PropertyDictionary(1);
		MGPropertyAccesser.setOrPutPkValue(property, attackerPkValue);
		((Player) player).notifyPorperty(property);
		changeNameColorBroadcast();
		sendPKNameColorEvent();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();

		switch (actionEventId) {
		case PkEventDefines.C2G_Pk_Model:
			handle_Pk_State((C2G_Pk_Model) event);
			break;
		case PkEventDefines.C2G_Name_Color:
			break;
		default:
			break;
		}
	}

	private void handle_Pk_State(C2G_Pk_Model event) {
		short state = event.getState();
		if (state < 3 || state > 7) {
			logger.error("pk 模式切换失败，state = " + state);
			return;
		}
		changeAttackState(state);

	}

	private void sendPKNameColorEvent() {
		if (!(getParent() instanceof Player)) {
			return;
		}
		Player player = (Player) getParent();
		short color = getCurrentPlayerNameColor();
		G2C_Name_Color res = (G2C_Name_Color) MessageFactory.getConcreteMessage(PkEventDefines.G2C_Name_Color);
		res.setColor(color);
		res.setCharId(getConcreteParent().getId());
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	private void changeAttackState(short state) {
		if (!(getParent() instanceof Player)) {
			logger.error("pk 模式切换失败，getParent not Player,state = " + state);
			return;
		}
		Player player = (Player) getParent();
		playerPKMgr.setCrtAttackModel(state);
		player.setCrtAttackModel(state);
		G2C_Pk_Model res = (G2C_Pk_Model) MessageFactory.getConcreteMessage(PkEventDefines.G2C_Pk_Model);
		res.setRet((byte) state);
		GameRoot.sendMessage(player.getIdentity(), res);
		logger.debug("成功切换pk模式,当前pk模式 = " + playerPKMgr.getCrtAttackModel());
		changeNameColorBroadcast();
	}

	public void changePKModel(short model) {
		playerPKMgr.setCrtAttackModel(model);
		if (!(getParent() instanceof Player)) {
			return;
		}
		Player player = (Player) getParent();
		player.setCrtAttackModel(model);
	}

	private void calcutePKValue() {
		pkValuehandle = GameContext.getTaskManager().schedulePeriodicTask(new Task() {
			@Override
			public void run() throws Exception {
				Player player = (Player) getParent();
				int pkValue = MGPropertyAccesser.getPkValue(player.getProperty());
				if (pkValue > 0) {
					pkValue -= 1;
					MGPropertyAccesser.setOrPutPkValue(player.getProperty(), pkValue);
					PropertyDictionary property = new PropertyDictionary(1);
					MGPropertyAccesser.setOrPutPkValue(property, pkValue);
					player.notifyPorperty(property);
					if (pkValue == 50 || pkValue == 200) {
						changeNameColorBroadcast();
						sendPKNameColorEvent();
					}
				}
			}
		}, 60 * 1000, 60 * 1000);
	}

	private void calcuteExitPkState() {

		handle = GameContext.getTaskManager().schedulePeriodicTask(new Task() {
			@Override
			public void run() throws Exception {
				Player player = (Player) getParent();
				player.cancelState(PKState.PKState_Id);
				changeNameColorBroadcast();
				sendPKNameColorEvent();
			}
		}, exitPkStateTime, exitPkStateTime);

	}

	public void updateAttackerTime() {
		if (handle != null)
			handle.cancel();
		calcuteExitPkState();
	}

	public short getCurrentPlayerNameColor() {
		boolean pkState = getConcreteParent().getFightSpriteStateMgr().isState(PKState.PKState_Id);
		int pkValue = MGPropertyAccesser.getPkValue(getConcreteParent().getProperty());
		if (pkValue > 200) {
			return PlayerNameColor.Red;
		}
		if (!pkState) {
			if (pkValue >= 0 && pkValue <= 50) {
				return PlayerNameColor.White;
			} else if (pkValue >= 51 && pkValue <= 200) {
				return PlayerNameColor.Yellow;
			} else {

			}
		} else {
			if (pkValue <= 200) {
				return PlayerNameColor.Gray;
			}
		}

		return PlayerNameColor.White;
	}

	public void changeNameColorBroadcast() {
		if (!(getParent() instanceof Player)) {
			return;
		}
		Player player = (Player) getParent();
		short nameColor = getCurrentPlayerNameColor();
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutNameColor(pd, nameColor);
		MGPropertyAccesser.setOrPutPkModel(pd, (byte) playerPKMgr.getCrtAttackModel());
		MGPropertyAccesser.setOrPutNameColor(player.getSenceProperty(), nameColor);
		MGPropertyAccesser.setOrPutPkModel(player.getSenceProperty(), (byte) playerPKMgr.getCrtAttackModel());
		player.getAoiComponent().broadcastProperty(pd);

	}

}
