package newbee.morningGlory.mmorpg.union.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionMember;
import newbee.morningGlory.mmorpg.union.MGUnionMemberMgr;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class UnionPersistenceObject {
	private static Logger logger = Logger.getLogger(UnionPersistenceObject.class);
	
	private static UnionPersistenceObject instance = new UnionPersistenceObject();

	private UnionPersistenceObject() {

	}

	public static UnionPersistenceObject getInstance() {
		return instance;
	}

	public byte[] unionInfoToBytes10000(MGUnion union) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(10000);
		buffer.writeString(union.getName());
		buffer.writeByte(union.getAutoState());
		buffer.writeLong(union.getCreateTime());
		buffer.writeByte(union.getKingCityType());
		buffer.writeString(union.getMessage());
		buffer.writeString(union.getCreater().getPlayerId());
		buffer.writeString(union.getCreater().getPlayerName());
		buffer.writeBoolean(union.isSignup());
		buffer.writeBoolean(union.isApplyGameInstance());
		buffer.writeLong(union.getBecomeKingCityMills());

		return buffer.getData();
	}

	public byte[] unionInfoToBytes10001(MGUnion union) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		try {
			buffer.writeInt(10001);
			buffer.writeString(union.getName());
			buffer.writeByte(union.getAutoState());
			buffer.writeLong(union.getCreateTime());
			buffer.writeByte(union.getKingCityType());
			buffer.writeString(union.getMessage());
			buffer.writeString(union.getCreater().getPlayerId());
			buffer.writeString(union.getCreater().getPlayerName());
			buffer.writeBoolean(union.isSignup());
			buffer.writeBoolean(union.isApplyGameInstance());
			buffer.writeLong(union.getBecomeKingCityMills());
		} catch (Exception e) {
			throw e;
		}
		
		return buffer.getData();
	}

	// =============================================================================

	// 公会成员
	public byte[] memberInfoToBytes10000(MGUnion union) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		try {
			MGUnionMemberMgr memberMgr = union.getMemberMgr();
			buffer.writeInt(memberMgr.getMemberCount());
			for (MGUnionMember member : memberMgr.getMemberList()) {
				buffer.writeString(member.getPlayerId());
				buffer.writeString(member.getPlayerName());
				buffer.writeByte(member.getProfessionId());
				buffer.writeInt(member.getLevel());
				buffer.writeInt(member.getFightValue());
				buffer.writeByte(member.getUnionOfficialId());
				buffer.writeByte(member.getOnline());
				buffer.writeLong(member.getEnterTime());
			}
		} catch (Exception e) {
			throw e;
		}
		
		return buffer.getData();
	}

	public byte[] memberInfoToBytes10001(MGUnion union) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGUnionMemberMgr memberMgr = union.getMemberMgr();
		buffer.writeInt(memberMgr.getMemberCount());
		for (MGUnionMember member : memberMgr.getMemberList()) {
			buffer.writeString(member.getPlayerId());
			buffer.writeString(member.getPlayerName());
			buffer.writeByte(member.getProfessionId());
			buffer.writeInt(member.getLevel());
			buffer.writeInt(member.getFightValue());
			buffer.writeByte(member.getUnionOfficialId());
			buffer.writeByte(member.getOnline());
			buffer.writeLong(member.getEnterTime());
			buffer.writeByte(member.getVipType());
			buffer.writeLong(member.getLastLogoutTime());
		}

		return buffer.getData();
	}

	// =====================================================================

	// 公会申请列表
	public byte[] applyInfoToBytes10000(MGUnion union) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		union.getUnionApplyMgr().applyListToBytes(buffer);

		return buffer.getData();
	}

	public byte[] applyInfoToBytes10001(MGUnion union) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		union.getUnionApplyMgr().applyListToBytes(buffer);

		return buffer.getData();
	}

	// =============================================================================

	public MGUnion unionInfoFromBytes(String id, byte[] unionData, byte[] memberData, byte[] applyData) {
		ByteArrayReadWriteBuffer unionBuffer = new ByteArrayReadWriteBuffer(unionData);
		ByteArrayReadWriteBuffer memberBuffer = new ByteArrayReadWriteBuffer(memberData);
		ByteArrayReadWriteBuffer applyBuffer = new ByteArrayReadWriteBuffer(applyData);

		MGUnion union = null;
		int version = unionBuffer.readInt();

		if (version == 10000) {
			union = unionInfoFromBytesVer10000(id, unionBuffer, memberBuffer, applyBuffer);
		} else if (version == 10001) {
			union = unionInfoFromBytesVer10001(id, unionBuffer, memberBuffer, applyBuffer);
		} else {
			logger.error("UnionPersistenceObject");
		}

		return union;
	}

	public MGUnion unionInfoFromBytesVer10000(String id, ByteArrayReadWriteBuffer unionBuffer, 
												ByteArrayReadWriteBuffer memberBuffer, 
												ByteArrayReadWriteBuffer applyBuffer) {
		MGUnion union = new MGUnion();
		MGUnionMemberMgr memberMgr = union.getMemberMgr();
		MGUnionMember creater = new MGUnionMember();

		String unionName = unionBuffer.readString();
		byte autoState = unionBuffer.readByte();
		long createTime = unionBuffer.readLong();
		byte kingCityType = unionBuffer.readByte();
		String message = unionBuffer.readString();
		String createrId = unionBuffer.readString();
		String createrName = unionBuffer.readString();
		boolean isSignup = unionBuffer.readBoolean();
		boolean isApplyGameInstance = unionBuffer.readBoolean();
		long becomeKingCityMills = unionBuffer.readLong();

		creater.setPlayerId(createrId);
		creater.setPlayerName(createrName);

		int count = memberBuffer.readInt();
		for (int i = 0; i < count; i++) {
			MGUnionMember member = new MGUnionMember();
			String playerId = memberBuffer.readString();
			member.setPlayerId(playerId);
			member.setPlayerName(memberBuffer.readString());
			member.setProfessionId(memberBuffer.readByte());
			member.setLevel(memberBuffer.readInt());
			member.setFightValue(memberBuffer.readInt());
			member.setUnionOfficialId(memberBuffer.readByte());
			member.setOnline(memberBuffer.readByte());
			member.setEnterTime(memberBuffer.readLong());
			
			PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
			Player player = playerManager.getPlayer(playerId);
			if (player == null) {
				logger.error("union member data restore error! playerId = " + playerId + " is not exist!");
				continue;
			}
			
			long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(player.getProperty());
			byte vipType = player.getVipType();
			member.setVipType(vipType);
			member.setLastLogoutTime(lastLogoutTime);
			memberMgr.addMember(member);
		}

		memberMgr.sortMember();
		int applySize = applyBuffer.readInt();
		List<String> applyList = new ArrayList<String>();
		for (int i = 0; i < applySize; i++) {
			applyList.add(applyBuffer.readString());

		}
		union.setId(id);
		union.setName(unionName);
		union.setAutoState(autoState);
		union.setCreateTime(createTime);
		union.setKingCityType(kingCityType);
		union.setMessage(message);
		union.setCreater(creater);
		union.getUnionApplyMgr().setApplyList(applyList);
		union.setSignup(isSignup);
		union.setApplyGameInstance(isApplyGameInstance);
		union.setBecomeKingCityMills(becomeKingCityMills);
		return union;
	}

	public MGUnion unionInfoFromBytesVer10001(String id, ByteArrayReadWriteBuffer unionBuffer, 
												ByteArrayReadWriteBuffer memberBuffer, 
												ByteArrayReadWriteBuffer applyBuffer) {
		MGUnion union = new MGUnion();
		MGUnionMemberMgr memberMgr = union.getMemberMgr();
		MGUnionMember creater = new MGUnionMember();

		String unionName = unionBuffer.readString();
		byte autoState = unionBuffer.readByte();
		long createTime = unionBuffer.readLong();
		byte kingCityType = unionBuffer.readByte();
		String message = unionBuffer.readString();
		String createrId = unionBuffer.readString();
		String createrName = unionBuffer.readString();
		boolean isSignup = unionBuffer.readBoolean();
		boolean isApplyGameInstance = unionBuffer.readBoolean();
		long becomeKingCityMills = unionBuffer.readLong();

		creater.setPlayerId(createrId);
		creater.setPlayerName(createrName);

		int count = memberBuffer.readInt();
		for (int i = 0; i < count; i++) {
			MGUnionMember member = new MGUnionMember();
			member.setPlayerId(memberBuffer.readString());
			member.setPlayerName(memberBuffer.readString());
			member.setProfessionId(memberBuffer.readByte());
			member.setLevel(memberBuffer.readInt());
			member.setFightValue(memberBuffer.readInt());
			member.setUnionOfficialId(memberBuffer.readByte());
			member.setOnline(memberBuffer.readByte());
			member.setEnterTime(memberBuffer.readLong());
			
			member.setVipType(memberBuffer.readByte());
			member.setLastLogoutTime(memberBuffer.readLong());
			memberMgr.addMember(member);
		}

		memberMgr.sortMember();
		int applySize = applyBuffer.readInt();
		List<String> applyList = new ArrayList<String>();
		for (int i = 0; i < applySize; i++) {
			applyList.add(applyBuffer.readString());

		}
		union.setId(id);
		union.setName(unionName);
		union.setAutoState(autoState);
		union.setCreateTime(createTime);
		union.setKingCityType(kingCityType);
		union.setMessage(message);
		union.setCreater(creater);
		union.getUnionApplyMgr().setApplyList(applyList);
		union.setSignup(isSignup);
		union.setApplyGameInstance(isApplyGameInstance);
		union.setBecomeKingCityMills(becomeKingCityMills);
		return union;
	}
}
