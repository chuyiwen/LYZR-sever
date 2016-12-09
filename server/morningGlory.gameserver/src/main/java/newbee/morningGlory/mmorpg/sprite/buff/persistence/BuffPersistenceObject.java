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
package newbee.morningGlory.mmorpg.sprite.buff.persistence;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffMgr;
import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;


public final class BuffPersistenceObject extends AbstractPersistenceObject {
	public static final String TalismanFieldName = "buffData";
	private MGFightSpriteBuffMgr fightSpriteBuffMgr;	
	public static final byte Json_Data = 1;
	
	public static final byte Bytes_Data = 2;
	
//	private static final String SaveFormatParameterName = "saveFormat";
//	
//	private static final Byte saveFormatParameterValue = Bytes_Data;
//	
	private static final String SaveDataParameterName = "buffData";
	
	private PersistenceParameter talismanDataPersistenceParameter = new PersistenceParameter();
	
	private final BuffReadWrite readWrite ;
	
	public BuffPersistenceObject(MGFightSpriteBuffMgr fightSpriteBuffMgr,Player player) {
		this.fightSpriteBuffMgr = fightSpriteBuffMgr;
		readWrite= new BuffReadWrite(fightSpriteBuffMgr,player);
		persistenceParameters = new ArrayList<>(1);
		talismanDataPersistenceParameter.setName(SaveDataParameterName);
		this.persistenceParameters.add(talismanDataPersistenceParameter);
	}
	
	@Override
	public void snapshot() {
		
		byte[] bytes = readWrite.toBytes(fightSpriteBuffMgr);
		
		talismanDataPersistenceParameter.setValue(bytes);
		
	}

	@Override
	public Collection<PersistenceParameter> getPersistenceParameters() {
		return persistenceParameters;
	}

	@Override
	public void setDataFrom(
			Collection<PersistenceParameter> persistenceParameters) {
		for(PersistenceParameter persistenceParameter : persistenceParameters)
		{
			String name = persistenceParameter.getName();
			if(SaveDataParameterName.equals(name))
			{
				readWrite.fromBytes((byte[])persistenceParameter.getValue());
			}
		}
	}

}
