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
package sophia.mmorpg.player.scene.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.ObjectPool;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.base.sprite.aoi.PathInfo;
import sophia.mmorpg.base.sprite.aoi.PositionInfo;
import sophia.mmorpg.base.sprite.aoi.SpriteInfo;
import sophia.mmorpg.base.sprite.aoi.SpriteSceneProperty;
import sophia.mmorpg.core.state.FSMStateBase;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.pluck.Pluck;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public class G2C_Scene_AOI extends ActionEventBase {
	private static final Logger logger = Logger.getLogger(G2C_Scene_AOI.class);

	public static final ObjectPool<G2C_Scene_AOI> pool = new ObjectPool<G2C_Scene_AOI>() {
		@Override
		protected G2C_Scene_AOI instance() {
			return new G2C_Scene_AOI();
		}

		@Override
		protected void onRecycle(G2C_Scene_AOI obj) {
			obj.clear();
		}
	};

	private Player player;
	private List<PositionInfo> positionInfoTempList = new ArrayList<>();
	private List<SpriteInfo> spriteInfoTempList = new ArrayList<>();
	private List<PathInfo> pathInfoTempList = new ArrayList<>();
	private List<Player> playerList = new ArrayList<>();
	private List<Monster> monsterList = new ArrayList<>();
	private List<Pluck> pluckList = new ArrayList<>();
	private List<SpriteInfo> removeList = new ArrayList<>();
	private List<PathInfo> moveToList = new ArrayList<>();
	private List<PositionInfo> stopMoveList = new ArrayList<>();
	private List<PositionInfo> jumpToList = new ArrayList<>();
	private List<SpriteSceneProperty> scenePropertyList = new ArrayList<>();
	private List<Loot> lootList = new ArrayList<>();
	private List<Sprite> otherSpriteList = new ArrayList<>();

	public G2C_Scene_AOI() {
		actionEventId = SceneEventDefines.G2C_Scene_AOI;
		setBufferSize((short) 2048);
		ziped = (byte) 1;
	}

	public void clear() {
		positionInfoTempList.clear();
		pathInfoTempList.clear();
		spriteInfoTempList.clear();
		playerList.clear();
		monsterList.clear();
		removeList.clear();
		moveToList.clear();
		stopMoveList.clear();
		jumpToList.clear();
		scenePropertyList.clear();
		lootList.clear();
		pluckList.clear();
		otherSpriteList.clear();
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		IoBuffer newBuffer = buffer;
		if (ziped == (byte) 1) {
			// prelen
			buffer.getShort();
			short len = buffer.getShort();
			byte[] zipData = new byte[len];
			buffer.get(zipData);
			newBuffer = unZipBuffer(zipData);
		}

		byte size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			String id = getString(newBuffer);
			byte spriteType = newBuffer.get();
			SpriteInfo spriteInfo = new SpriteInfo(id, "", spriteType);
			removeList.add(spriteInfo);
		}

		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			// Player player = new Player();
			getString(newBuffer);
			getString(newBuffer);
			newBuffer.get();
			// level
			newBuffer.getShort();
			newBuffer.get();
			newBuffer.getInt();
			newBuffer.getInt();
			// x
			newBuffer.getShort();
			// y
			newBuffer.getShort();
			// speed
			newBuffer.getShort();
			newBuffer.getShort();
			newBuffer.getShort();
			newBuffer.getShort();
			newBuffer.getShort();

			newBuffer.get();
			getString(newBuffer);

			newBuffer.get();

			byte count = newBuffer.get();
			for (short j = 0; j < count; j++) {
				newBuffer.getShort();
			}
			short len = newBuffer.getShort();
			byte[] scenePd = new byte[len];
			newBuffer.get(scenePd);
			// playerSet.add(player);
		}

		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			Monster monster = new Monster();
			monster.setId(getString(newBuffer));
			monster.setName(getString(newBuffer));
			Position crtPosition = new Position();
			crtPosition.setX((int) newBuffer.getShort());
			crtPosition.setY((int) newBuffer.getShort());
			monster.setCrtPosition(crtPosition);

			newBuffer.getInt();
			newBuffer.getInt();
			getString(newBuffer);
			// speed
			newBuffer.getShort();
			byte count = newBuffer.get();
			for (short j = 0; j < count; j++) {
				newBuffer.getShort();
			}
			getString(newBuffer);
			monsterList.add(monster);
		}

		// Loot
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			getString(newBuffer);
			getString(newBuffer);
			newBuffer.getShort();
			newBuffer.getShort();
			newBuffer.get();
			newBuffer.getShort();
		}

		// pluck
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			Pluck pluck = new Pluck();
			String id = getString(newBuffer);
			// refId
			getString(newBuffer);
			int x = newBuffer.getShort();
			int y = newBuffer.getShort();
			pluck.setId(id);
			pluck.getCrtPosition().setX(x);
			pluck.getCrtPosition().setY(y);

			pluckList.add(pluck);

		}

		// moveToSet
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			getString(newBuffer);
			// getString(buffer);
			newBuffer.get();
			// buffer.getLong();
			newBuffer.getShort();
			newBuffer.getShort();
			newBuffer.getShort();
			newBuffer.getShort();
		}

		// stopMoveSet
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			getString(newBuffer);
			// getString(buffer);
			newBuffer.get();
			newBuffer.getShort();
			newBuffer.getShort();
		}

		// jumpToSet
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			getString(newBuffer);
			// getString(buffer);
			newBuffer.get();
			newBuffer.getShort();
			newBuffer.getShort();
		}

		// scenePropertySet
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			getString(newBuffer);
			// getString(buffer);
			newBuffer.get();
			short len = newBuffer.getShort();
			byte[] pdArray = new byte[len];
			newBuffer.get(pdArray);
		}

		// OtherSpriteList
		size = newBuffer.get();
		for (short i = 0; i < size; i++) {
			getString(newBuffer);
			// getString(buffer);
			newBuffer.get();
			newBuffer.getShort();
			newBuffer.getShort();
			short len = newBuffer.getShort();
			byte[] pdArray = new byte[len];
			newBuffer.get(pdArray);
			byte count = newBuffer.get();
			for (short j = 0; j < count; j++) {
				newBuffer.getShort();
			}
		}

	}

	private void writeRemoveList(IoBuffer buffer) {
		byte size = (byte) removeList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("removeList " + size);
		}

		for (int i = 0; i < size; i++) {
			SpriteInfo spriteInfo = removeList.get(i);
			putString(buffer, spriteInfo.getSpriteId());
			buffer.put(spriteInfo.getSpriteType());
			if (logger.isDebugEnabled()) {
				logger.debug(spriteInfo);
			}
		}
	}

	private void writePlayerList(IoBuffer buffer) {
		byte size = (byte) playerList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("playerList " + size);
		}

		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			putString(buffer, player.getId());
			putString(buffer, player.getName());
			buffer.put(player.getProfession());

			// buffer.putInt(player.getLevel());
			buffer.putShort((short) player.getLevel());

			PropertyDictionary pd = player.getProperty();
			buffer.put(MGPropertyAccesser.getGender(pd));
			// hp, hpmax
			buffer.putInt(player.getHP());
			buffer.putInt(player.getHPMax());

			// buffer.putInt(player.getCrtPosition().getX());
			// buffer.putInt(player.getCrtPosition().getY());
			// buffer.putInt(player.getPathComponent().getMoveSpeed());
			buffer.putShort((short) player.getCrtPosition().getX());
			buffer.putShort((short) player.getCrtPosition().getY());
			buffer.putShort((short) player.getPathComponent().getMoveSpeed());

			buffer.putShort((short) MGPropertyAccesser.getWeaponModleId(pd));
			buffer.putShort((short) MGPropertyAccesser.getArmorModleId(pd));
			buffer.putShort((short) MGPropertyAccesser.getWingModleId(pd));
			buffer.putShort((short) MGPropertyAccesser.getMountModleId(pd));
			byte knightLevel = 0;
			if (pd.contains(MGPropertySymbolDefines.Knight_Id)) {
				knightLevel = MGPropertyAccesser.getKnight(pd);
			}
			String unionName = "";
			if (pd.contains(MGPropertySymbolDefines.UnionName_Id)) {
				unionName = MGPropertyAccesser.getUnionName(pd);
			}
			byte isKingCity = 0;
			if (pd.contains(MGPropertySymbolDefines.IsKingCity_Id)) {
				isKingCity = MGPropertyAccesser.getIsKingCity(pd);
			}
			buffer.put(knightLevel);
			putString(buffer, unionName);
			buffer.put(isKingCity);
			List<FSMStateBase<FightSprite>> stateList = player.getStateList();

			// buffer.putShort((short) stateList.size());
			buffer.put((byte) stateList.size());

			for (FSMStateBase<FightSprite> state : stateList) {
				buffer.putShort(state.getId());
			}
			byte[] scenePd = player.getSenceProperty().toByteArray();

			// buffer.putInt(scenePd.length);
			buffer.putShort((short) scenePd.length);
			// System.out.println("player pd len="+scenePd.length);
			buffer.put(scenePd);

			if (logger.isDebugEnabled()) {
				logger.debug(player + ", sceneProperty=" + player.getSenceProperty());
			}
		}
	}

	private void writeMonsterList(IoBuffer buffer) {
		byte size = (byte) monsterList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("monsterList " + size);
		}

		for (int i = 0; i < size; i++) {
			Monster monster = monsterList.get(i);
			putString(buffer, monster.getId());
			putString(buffer, monster.getMonsterRef().getId());
			// buffer.putInt(monster.getCrtPosition().getX());
			// buffer.putInt(monster.getCrtPosition().getY());
			buffer.putShort((short) monster.getCrtPosition().getX());
			buffer.putShort((short) monster.getCrtPosition().getY());

			PropertyDictionary pd = monster.getProperty();
			buffer.putInt(monster.getHP());
			buffer.putInt(monster.getHPMax());
			if (pd.contains(MGPropertySymbolDefines.UnionName_Id)) {
				putString(buffer, MGPropertyAccesser.getUnionName(pd));
			} else {
				putString(buffer, "");
			}

			int moveSpeed = 0;
			if (monster.getPathComponent() != null) {
				moveSpeed = monster.getPathComponent().getMoveSpeed();
			}

			buffer.putShort((short) moveSpeed);

			List<FSMStateBase<FightSprite>> stateList = monster.getStateList();
			buffer.put((byte) stateList.size());
			for (FSMStateBase<FightSprite> state : stateList) {
				buffer.putShort(state.getId());
			}

			if (!monster.getMonsterRef().isRegularMonster() && monster.getOwner() != null) {
				putString(buffer, monster.getOwner().getId());
			} else {
				putString(buffer, "");
			}

			if (logger.isDebugEnabled()) {
				logger.debug(monster);
			}
		}
	}

	private void writeLootList(IoBuffer buffer) {
		byte size = (byte) lootList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("lootList " + size);
		}

		for (int i = 0; i < size; i++) {
			Loot loot = lootList.get(i);
			putString(buffer, loot.getId());
			ItemPair itemPair = loot.getItemPair();
			if (itemPair != null) {
				putString(buffer, itemPair.getItemRefId());
			} else {
				putString(buffer, loot.getItem().getItemRef().getId());
			}
			buffer.putShort((short) loot.getCrtPosition().getX());
			buffer.putShort((short) loot.getCrtPosition().getY());
			if (loot.isWho(player)) {
				buffer.put((byte) 1);
				buffer.putShort((short) 0);
			} else {
				buffer.put((byte) 0);
				buffer.putShort((short) loot.leftGuardSeconds());
			}

			if (logger.isDebugEnabled()) {
				logger.debug(loot);
			}
		}
	}

	private void writePluckList(IoBuffer buffer) {
		byte size = (byte) pluckList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("pluckList " + size);
		}

		for (int i = 0; i < size; i++) {
			Pluck pluck = pluckList.get(i);
			putString(buffer, pluck.getId());
			putString(buffer, pluck.getPluckRef().getId());
			buffer.putShort((short) pluck.getCrtPosition().getX());
			buffer.putShort((short) pluck.getCrtPosition().getY());

			if (logger.isDebugEnabled()) {
				logger.debug(pluck);
			}
		}
	}

	private void writeMoveToList(IoBuffer buffer) {
		byte size = (byte) moveToList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("moveToList " + size);
		}

		for (int i = 0; i < size; i++) {
			PathInfo pathInfo = moveToList.get(i);
			putString(buffer, pathInfo.getSpriteInfo().getSpriteId());
			buffer.put(pathInfo.getSpriteInfo().getSpriteType());
			// buffer.putLong(pathInfo.getServerStamp());
			buffer.putShort((short) pathInfo.getStartPosition().getX());
			buffer.putShort((short) pathInfo.getStartPosition().getY());
			buffer.putShort((short) pathInfo.getEndPosition().getX());
			buffer.putShort((short) pathInfo.getEndPosition().getY());

			if (logger.isDebugEnabled()) {
				logger.debug(pathInfo);
			}
		}
	}

	private void writeStopMoveList(IoBuffer buffer) {
		byte size = (byte) stopMoveList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("stopMoveList " + size);
		}

		for (int i = 0; i < size; i++) {
			PositionInfo posInfo = stopMoveList.get(i);
			putString(buffer, posInfo.getSpriteInfo().getSpriteId());
			buffer.put(posInfo.getSpriteInfo().getSpriteType());
			buffer.putShort((short) posInfo.getPosition().getX());
			buffer.putShort((short) posInfo.getPosition().getY());

			if (logger.isDebugEnabled()) {
				logger.debug(posInfo);
			}
		}
	}

	private void writeJumpToList(IoBuffer buffer) {
		byte size = (byte) jumpToList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("jumpToList " + size);
		}

		for (int i = 0; i < size; i++) {
			PositionInfo posInfo = jumpToList.get(i);
			putString(buffer, posInfo.getSpriteInfo().getSpriteId());
			buffer.put(posInfo.getSpriteInfo().getSpriteType());
			buffer.putShort((short) posInfo.getPosition().getX());
			buffer.putShort((short) posInfo.getPosition().getY());

			if (logger.isDebugEnabled()) {
				logger.debug(posInfo);
			}
		}
	}

	private void writeScenePropertyList(IoBuffer buffer) {
		byte size = (byte) scenePropertyList.size();

		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("scenePropertyList " + size);
		}

		for (int i = 0; i < size; i++) {
			SpriteSceneProperty sceneProperty = scenePropertyList.get(i);
			putString(buffer, sceneProperty.getSpriteInfo().getSpriteId());
			buffer.put(sceneProperty.getSpriteInfo().getSpriteType());
			byte[] pdArray = sceneProperty.getProperty().toByteArray();
			buffer.putShort((short) pdArray.length);
			buffer.put(pdArray);

			if (logger.isDebugEnabled()) {
				logger.debug(sceneProperty);
			}
		}
	}

	private void writeOtherSpriteList(IoBuffer buffer) {
		byte size = (byte) otherSpriteList.size();
		buffer.put(size);
		if (logger.isDebugEnabled()) {
			logger.debug("OtherSpriteList " + size);
		}

		for (Sprite sprite : otherSpriteList) {
			putString(buffer, sprite.getId());
			// putString(buffer, sprite.getGameSpriteType());
			buffer.put(sprite.getSpriteType());
			buffer.putShort((short) sprite.getCrtPosition().getX());
			buffer.putShort((short) sprite.getCrtPosition().getY());
			sprite.packSceneData(buffer);

			if (logger.isDebugEnabled()) {
				logger.debug("OtherSprite " + sprite);
			}
		}
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (logger.isDebugEnabled()) {
			logger.debug("G2C_AOI_Message..............................to player = " + player.getName());
		}

		/**
		 * byte[] zipedData; WriteByteHelper.write(zipedData, dataObject);
		 * zip(zipedData); buffer.writeByte(zipCode); buffer.write(zipedData);
		 */

		writeRemoveList(buffer);

		writePlayerList(buffer);

		writeMonsterList(buffer);

		writeLootList(buffer);

		writePluckList(buffer);

		writeMoveToList(buffer);

		writeStopMoveList(buffer);

		writeJumpToList(buffer);

		writeScenePropertyList(buffer);

		writeOtherSpriteList(buffer);

		return buffer;
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public List<Monster> getMonsterList() {
		return monsterList;
	}

	public List<Pluck> getPluckList() {
		return pluckList;
	}

	public List<SpriteInfo> getRemoveList() {
		return removeList;
	}

	public List<PathInfo> getMoveToList() {
		return moveToList;
	}

	public List<PositionInfo> getStopMoveList() {
		return stopMoveList;
	}

	public List<PositionInfo> getJumpToList() {
		return jumpToList;
	}

	public List<SpriteSceneProperty> getScenePropertyList() {
		return scenePropertyList;
	}

	public List<Loot> getLootList() {
		return lootList;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return "G2C_Scene_AOI [actionEventId=" + actionEventId + ", ziped=" + ziped + ", bufferSize=" + bufferSize + ", identity=" + identity + "]";
	}

	public List<SpriteInfo> getSpriteTempList() {
		return spriteInfoTempList;
	}

	public List<PathInfo> getPathInfoTempList() {
		return pathInfoTempList;
	}

	public List<Sprite> getOtherSpriteList() {
		return otherSpriteList;
	}

	public List<PositionInfo> getPositionInfoTempList() {
		return positionInfoTempList;
	}
}
