package newbee.morningGlory.mmorpg.player.achievement.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import newbee.morningGlory.mmorpg.player.achievement.CastleWarRecord;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievement;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementMgr;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementRef;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.equipment.PlayerEquipBodyConponent;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public class AchievementReadWrite extends AbstractPersistenceObjectReadWrite<MGPlayerAchievementMgr> implements PersistenceObjectReadWrite<MGPlayerAchievementMgr> {
	private static Logger logger = Logger.getLogger(AchievementReadWrite.class);
	private MGPlayerAchievementMgr achievementMgr;
	private static final int Current_Version = 10003;

	public AchievementReadWrite(MGPlayerAchievementMgr achievementMgr) {
		this.achievementMgr = achievementMgr;
	}

	@Override
	public byte[] toBytes(MGPlayerAchievementMgr persistenceObject) {
		return toBytesVer10003(persistenceObject);
	}
	
	private byte[] toBytesVer10003(MGPlayerAchievementMgr persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) persistenceObject.getPlayer().getTagged(MGPlayerAchievementComponent.Tag);
		List<MGPlayerAchievement> achievements = persistenceObject.getCrtAchievementList();
		Set<String> allAchievementIdSet = persistenceObject.getAllAchievementIdSet();
		boolean exchange = achievementComponent.isExchange();
		
		buffer.writeInt(Current_Version);
		buffer.writeBoolean(exchange);
		buffer.writeInt(achievements.size());
		if (logger.isDebugEnabled()) {
			logger.debug("成就列表长度:" + achievements.size());
		}
		for (MGPlayerAchievement achievement : achievements) {
			String achieveRefId = achievement.getAchievementRef().getId();
			byte success = achievement.getSuccess();
			buffer.writeString(achieveRefId);
			buffer.writeByte(success);
		}
		int achievePoint = achievementComponent.getAchievePointMgr().getAchievePoint();
		buffer.writeInt(achievePoint);
		// 保存杀怪、BOSS的个数,强化和洗练装备的次数
		buffer.writeInt(achievementComponent.getNumberMgr().getKillMonsterNumber());
		buffer.writeInt(achievementComponent.getNumberMgr().getKillBossNumber());
		buffer.writeInt(achievementComponent.getNumberMgr().getStrengthCount());
		buffer.writeInt(achievementComponent.getNumberMgr().getWashCount());

		buffer.writeInt(allAchievementIdSet.size());
		Iterator<String> it = allAchievementIdSet.iterator();
		while (it.hasNext()) {
			buffer.writeString(it.next());
		}
		
		CastleWarRecord record = achievementComponent.getRecord();
		long lastInPalaceStamp = record.getLastInPalaceStamp();
		boolean lastCastleWarResult = record.isLastCastleWarResult();
		int joinCastleWarCount = record.getJoinCastleWarCount();
		int winCastleWarCount = record.getWinCastleWarCount();
		int consecutiveCastelWarCount = record.getConsecutiveCastelWarCount();
		int killEnemyInCastleWarCount = record.getKillEnemyInCastleWarCount();
		int killCastleWarBossCount = record.getKillCastleWarBossCount();
		int addUnionCount = record.getAddUnionCount();
		int createUnionCount = record.getCreateUnionCount();
		int fullUnionCount = record.getFullUnionCount();
		
		buffer.writeLong(lastInPalaceStamp);
		buffer.writeBoolean(lastCastleWarResult);
		buffer.writeInt(joinCastleWarCount);
		buffer.writeInt(winCastleWarCount);
		buffer.writeInt(consecutiveCastelWarCount);
		buffer.writeInt(killEnemyInCastleWarCount);
		buffer.writeInt(killCastleWarBossCount);
		buffer.writeInt(addUnionCount);
		buffer.writeInt(createUnionCount);
		buffer.writeInt(fullUnionCount);
		
		return buffer.getData();
	}
	

	@Override
	public MGPlayerAchievementMgr fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		MGPlayerAchievementMgr mgr = null;
		int version = buffer.readInt();

		if (version == 10000) {
			mgr = fromBytesVer10000(buffer);
		} else if (version == 10001) {
			mgr = fromBytesVer10001(buffer);
		} else if (version == 10002) {
			mgr = fromBytesVer10002(buffer);
		} else if (version == 10003) {
			mgr = fromBytesVer10003(buffer);
		}
		return mgr;
	}

	private MGPlayerAchievementMgr fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		int length = buffer.readInt();
		List<MGPlayerAchievement> achievements = new ArrayList<MGPlayerAchievement>();
		for (int i = 0; i < length; i++) {
			String achieveRefId = buffer.readString();
			byte success = buffer.readByte();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(achieveRefId);
			MGPlayerAchievement achievement = new MGPlayerAchievement(playerAchievementRef);
			achievement.setSuccess(success);
			achievements.add(achievement);
		}
		int achievePoint = buffer.readInt();
		achievementMgr.setCrtAchievementList(achievements);
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) achievementMgr.getPlayer().getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.getAchievePointMgr().setAchievePoint(achievePoint);
		achievementComponent.getNumberMgr().setKillMonsterNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setKillBossNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setStrengthCount(buffer.readInt());
		achievementComponent.getNumberMgr().setWashCount(buffer.readInt());
		return achievementMgr;
	}

	private MGPlayerAchievementMgr fromBytesVer10001(ByteArrayReadWriteBuffer buffer) {
		int length = buffer.readInt();
		List<MGPlayerAchievement> achievements = new ArrayList<MGPlayerAchievement>();
		Set<String> allAchievementIdSet = new HashSet<String>();

		for (int i = 0; i < length; i++) {
			String achieveRefId = buffer.readString();
			byte success = buffer.readByte();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(achieveRefId);
			MGPlayerAchievement achievement = new MGPlayerAchievement(playerAchievementRef);
			achievement.setSuccess(success);
			achievements.add(achievement);
		}
		int achievePoint = buffer.readInt();
		achievementMgr.setCrtAchievementList(achievements);
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) achievementMgr.getPlayer().getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.getAchievePointMgr().setAchievePoint(achievePoint);
		achievementComponent.getNumberMgr().setKillMonsterNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setKillBossNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setStrengthCount(buffer.readInt());
		achievementComponent.getNumberMgr().setWashCount(buffer.readInt());

		PlayerEquipBodyConponent playerEquipBodyConponent = achievementMgr.getPlayer().getPlayerEquipBodyConponent();
		PlayerEquipBodyArea bodyArea = playerEquipBodyConponent.getPlayerBody().getBodyArea(PlayerEquipBodyArea.medalBodyId);
		Item medalOnBody = bodyArea.getEquipment();
		if (medalOnBody != null) {
			achievementComponent.setExchange(true);
		}
		
		int setSize = buffer.readInt();
		for (int i = 0; i < setSize; i++) {
			allAchievementIdSet.add(buffer.readString());
		}
		achievementMgr.setAllAchievementIdSet(allAchievementIdSet);
		return achievementMgr;
	}
	
	private MGPlayerAchievementMgr fromBytesVer10002(ByteArrayReadWriteBuffer buffer) {
		boolean isExchange = buffer.readBoolean();
		int length = buffer.readInt();
		List<MGPlayerAchievement> achievements = new ArrayList<MGPlayerAchievement>();
		Set<String> allAchievementIdSet = new HashSet<String>();

		for (int i = 0; i < length; i++) {
			String achieveRefId = buffer.readString();
			byte success = buffer.readByte();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(achieveRefId);
			MGPlayerAchievement achievement = new MGPlayerAchievement(playerAchievementRef);
			achievement.setSuccess(success);
			achievements.add(achievement);
		}
		int achievePoint = buffer.readInt();
		achievementMgr.setCrtAchievementList(achievements);
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) achievementMgr.getPlayer().getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.setExchange(isExchange);
		achievementComponent.getAchievePointMgr().setAchievePoint(achievePoint);
		achievementComponent.getNumberMgr().setKillMonsterNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setKillBossNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setStrengthCount(buffer.readInt());
		achievementComponent.getNumberMgr().setWashCount(buffer.readInt());

		int setSize = buffer.readInt();
		for (int i = 0; i < setSize; i++) {
			allAchievementIdSet.add(buffer.readString());
		}
		achievementMgr.setAllAchievementIdSet(allAchievementIdSet);
		return achievementMgr;
	}
	
	private MGPlayerAchievementMgr fromBytesVer10003(ByteArrayReadWriteBuffer buffer) {
		boolean isExchange = buffer.readBoolean();
		int length = buffer.readInt();
		List<MGPlayerAchievement> achievements = new ArrayList<MGPlayerAchievement>();
		Set<String> allAchievementIdSet = new HashSet<String>();

		for (int i = 0; i < length; i++) {
			String achieveRefId = buffer.readString();
			byte success = buffer.readByte();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(achieveRefId);
			MGPlayerAchievement achievement = new MGPlayerAchievement(playerAchievementRef);
			achievement.setSuccess(success);
			achievements.add(achievement);
		}
		int achievePoint = buffer.readInt();
		achievementMgr.setCrtAchievementList(achievements);
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) achievementMgr.getPlayer().getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.setExchange(isExchange);
		achievementComponent.getAchievePointMgr().setAchievePoint(achievePoint);
		achievementComponent.getNumberMgr().setKillMonsterNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setKillBossNumber(buffer.readInt());
		achievementComponent.getNumberMgr().setStrengthCount(buffer.readInt());
		achievementComponent.getNumberMgr().setWashCount(buffer.readInt());

		int setSize = buffer.readInt();
		for (int i = 0; i < setSize; i++) {
			allAchievementIdSet.add(buffer.readString());
		}
		achievementMgr.setAllAchievementIdSet(allAchievementIdSet);
		
		CastleWarRecord record = achievementComponent.getRecord();
		record.setLastInPalaceStamp(buffer.readLong());
		record.setLastCastleWarResult(buffer.readBoolean());
		record.setJoinCastleWarCount(buffer.readInt());
		record.setWinCastleWarCount(buffer.readInt());
		record.setConsecutiveCastelWarCount(buffer.readInt());
		record.setKillEnemyInCastleWarCount(buffer.readInt());
		record.setKillCastleWarBossCount(buffer.readInt());
		record.setAddUnionCount(buffer.readInt());
		record.setCreateUnionCount(buffer.readInt());
		record.setFullUnionCount(buffer.readInt());
		
		return achievementMgr;
	}

	@Override
	public String toJsonString(MGPlayerAchievementMgr persistenceObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGPlayerAchievementMgr fromJsonString(String persistenceJsonString) {
		// TODO Auto-generated method stub
		return null;
	}
}
