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
package sophia.mmorpg.player.fightSkill.persistence;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public class PlayerFightSkillReadWrite extends AbstractPersistenceObjectReadWrite<PlayerFightSkillTree> implements PersistenceObjectReadWrite<PlayerFightSkillTree> {
	private static final Logger logger = Logger.getLogger(PlayerFightSkillReadWrite.class);
	private PlayerFightSkillTree skillTree;
	private Player player;
	private static int currentVersion = Default_Write_Version + 1;

	public PlayerFightSkillReadWrite(PlayerFightSkillTree skillTree, Player player) {
		super();
		this.skillTree = skillTree;
		this.player = player;
	}

	@Override
	public byte[] toBytes(PlayerFightSkillTree persistenceObject) {
		return toBytesV10001(persistenceObject);
	}

	public byte[] toBytesDefault(PlayerFightSkillTree persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeInt(Default_Write_Version);
		buffer.writeInt(persistenceObject.getLearnedSkills().size());
		for (FightSkill skill : persistenceObject.getLearnedSkills()) {
			skillToBytes(buffer, skill);
		}
		buffer.writeInt(persistenceObject.getShortcutSkills().size());
		for (Map.Entry<Integer, String> entry : persistenceObject.getShortcutSkills().entrySet()) {
			buffer.writeInt(entry.getKey());
			buffer.writeString(entry.getValue());
		}

		return buffer.getData();

	}

	public byte[] toBytesV10001(PlayerFightSkillTree persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeInt(currentVersion);
		Collection<FightSkill> learnedSkills = persistenceObject.getLearnedSkills();
		buffer.writeInt(learnedSkills.size());
		for (FightSkill skill : learnedSkills) {
			skillToBytes(buffer, skill);
		}
		Collection<FightSkill> learnedExtendedSkills = persistenceObject.getLearnedExtendedSkills();
		buffer.writeInt(learnedExtendedSkills.size());
		for (FightSkill skill : learnedExtendedSkills) {
			skillToBytes(buffer, skill);
		}
		buffer.writeInt(persistenceObject.getShortcutSkills().size());
		for (Map.Entry<Integer, String> entry : persistenceObject.getShortcutSkills().entrySet()) {
			buffer.writeInt(entry.getKey());
			buffer.writeString(entry.getValue());
		}

		return buffer.getData();
	}

	@Override
	public PlayerFightSkillTree fromBytes(byte[] persistenceBytes) {
		PlayerFightSkillTree skillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		if (version == Default_Write_Version) {
			fromBytesDefault(persistenceBytes);
		} else if (version == currentVersion) {
			fromBytesV10001(persistenceBytes);
		} else {
			logger.error("no compatible version of deserialization");
			return null;
		}

		return skillTree;
	}

	public PlayerFightSkillTree fromBytesDefault(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		PlayerFightSkillTree skillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
		int version = buffer.readInt();
		int learnedSkillsCount = buffer.readInt();
		for (int i = 0; i < learnedSkillsCount; ++i) {
			FightSkill learnedSkill = bytesToSkill(buffer);
			skillTree.learn(learnedSkill);
		}
		int shortcutSkillCount = buffer.readInt();
		for (int i = 0; i < shortcutSkillCount; ++i) {
			int slotIndex = buffer.readInt();
			String shortcutSkillRefId = buffer.readString();
			skillTree.setShortcutSkill(slotIndex, shortcutSkillRefId);
		}
		return skillTree;
	}

	public PlayerFightSkillTree fromBytesV10001(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		PlayerFightSkillTree skillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
		int version = buffer.readInt();
		int learnedSkillsCount = buffer.readInt();
		for (int i = 0; i < learnedSkillsCount; ++i) {
			FightSkill learnedSkill = bytesToSkill(buffer);
			skillTree.learn(learnedSkill);
		}
		int learnedExtendedSkillsCount = buffer.readInt();
		for (int i = 0; i < learnedExtendedSkillsCount; ++i) {
			FightSkill learnedExtendedSkill = bytesToSkill(buffer);
			skillTree.learnExtendedSkill(learnedExtendedSkill);
		}
		int shortcutSkillCount = buffer.readInt();
		for (int i = 0; i < shortcutSkillCount; ++i) {
			int slotIndex = buffer.readInt();
			String shortcutSkillRefId = buffer.readString();
			skillTree.setShortcutSkill(slotIndex, shortcutSkillRefId);
		}
		return skillTree;
	}

	private ByteArrayReadWriteBuffer skillToBytes(ByteArrayReadWriteBuffer buffer, FightSkill skill) {
		buffer.writeString(skill.getId());
		buffer.writeInt(skill.getLevel());
		buffer.writeInt(skill.getExp());
		buffer.writeString(skill.getRefId());
		return buffer;
	}

	private FightSkill bytesToSkill(ByteArrayReadWriteBuffer buffer) {
		String id = buffer.readString();
		int level = buffer.readInt();
		int exp = buffer.readInt();
		String refId = buffer.readString();
		SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		int maxLevel = ref.getSkillLevelData().size();
		level = level > maxLevel ? maxLevel : level;
		FightSkill skill = new FightSkill(id, level, exp, ref);
		return skill;
	}

	@Override
	public String toJsonString(PlayerFightSkillTree persistenceObject) {
		return null;
	}

	@Override
	public PlayerFightSkillTree fromJsonString(String persistenceJsonString) {
		return null;
	}

}
