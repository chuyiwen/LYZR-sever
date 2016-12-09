package newbee.morningGlory.mmorpg.ladder;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.ladder.persistence.MGLadderDAO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;

import com.google.common.base.Preconditions;

public class MGLadderMemberSaver {

	private static Logger logger = Logger.getLogger(MGLadderMemberSaver.class);
	private static MGLadderMemberSaver instance = new MGLadderMemberSaver();

	private static Set<MGLadderMember> saveImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<MGLadderMember, Boolean>());

	private static Set<MGLadderMember> saveImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<MGLadderMember, Boolean>());

	public static Set<MGLadderMember> getSaveImmediateSetPrimary() {
		return saveImmediateSetPrimary;
	}

	public static Set<MGLadderMember> getSaveImmediateSetSecondary() {
		return saveImmediateSetSecondary;
	}

	private MGLadderMemberSaver() {

	}

	public static MGLadderMemberSaver getInstance() {
		return instance;
	}

	public void save() {
		if (saveImmediateSetPrimary.isEmpty()) {
			return;
		}
		saveImmediateSetSecondary.addAll(saveImmediateSetPrimary);
		saveImmediateSetPrimary.removeAll(saveImmediateSetSecondary);
		for (MGLadderMember member : saveImmediateSetSecondary) {

			try {
				MGLadderDAO.getInstance().updateMemberData(member);
			} catch (Exception e) {
				saveImmediateSetPrimary.add(member);
				logger.error("save error, ladderMember=" + member);
				logger.error(DebugUtil.printStack(e));
			}
		}

		saveImmediateSetSecondary.clear();
	}

	public void shutDownSave() throws Exception {
		save();
		if (!saveImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("MGLadderMember save data error!!!");
		}
	}

	public void saveImmediateData(MGLadderMember member) {
		// 只对Max_Member_Count以内的ladderMember保存数据
		if (member.getRank() > MGLadderMacro.Max_Member_Count) {
			return;
		}

		saveImmediateSetPrimary.add(member);
	}

	public boolean insertMGLadderMember(MGLadderMember member) {
		Preconditions.checkArgument(member != null);
		try {
			MGLadderDAO.getInstance().insertData(member);
		} catch (Exception e) {
			logger.error("insertMGLadderMember error, member=" + member);
			logger.error("insertMGLadderMember error, " + DebugUtil.printStack(e));
			return false;
		}

		return true;
	}

	public boolean deleteMGLadderMember(MGLadderMember member) {
		Preconditions.checkArgument(member != null);
		try {
			MGLadderDAO.getInstance().deleteMemberData(member);
		} catch (Exception e) {
			logger.error("deleteMGLadderMember error, member=" + member);
			logger.error("deleteMGLadderMember error, " + DebugUtil.printStack(e));
			return false;
		}

		return true;
	}

	public static MGLadderMember getMGLadderMember(String playerId) {
		for (MGLadderMember member : saveImmediateSetPrimary) {
			if (StringUtils.equals(playerId, member.getPlayerId())) {
				return member;
			}
		}
		return null;
	}

}
