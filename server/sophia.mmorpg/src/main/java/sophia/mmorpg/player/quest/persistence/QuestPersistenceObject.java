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
package sophia.mmorpg.player.quest.persistence;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.quest.PlayerQuestManager;
import sophia.mmorpg.player.quest.Quest;

public final class QuestPersistenceObject extends AbstractPersistenceObject {
	private static final Logger logger = Logger
			.getLogger(QuestPersistenceObject.class.getName());
	
	private PlayerQuestManager owner;
	
	public static final byte Json_Data = 1;
	
	public static final byte Bytes_Data = 2;
	
	private static final String SaveFormatParameterName = "saveFormat";
	
	private static final Byte saveFormatParameterValue = Bytes_Data;
	
	private static final String SaveDataParameterName = "questData";
	
	private PersistenceParameter questDataPersistenceParameter = new PersistenceParameter();
	
	private final QuestPersistenceReadWrite readWrite;
	
	public QuestPersistenceObject(PlayerQuestManager owner, Player player) {
		this.readWrite = new QuestPersistenceReadWrite(player);
		this.owner = owner;
		this.persistenceParameters = new ArrayList<>(2);
		questDataPersistenceParameter.setName(SaveDataParameterName);
		persistenceParameters.add(questDataPersistenceParameter);
	}
	
	@Override	
	public void snapshot() {
		Quest crtQuest = owner.getCrtQuest();
		byte[] bytes = readWrite.toBytes(crtQuest);
		questDataPersistenceParameter.setValue(bytes);
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for(PersistenceParameter persistenceParameter : persistenceParameters)
		{
			String name = persistenceParameter.getName();
			if (name.equals(SaveDataParameterName)){
				byte[] bytes = (byte[])persistenceParameter.getValue();
				Quest crtQuest = readWrite.fromBytes(bytes);
				owner.setCrtQuest(crtQuest);
			}
		}
	}

}
