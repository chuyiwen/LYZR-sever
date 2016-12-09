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
package sophia.mmorpg.player.persistence;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.data.AbstractSaveableObjectSaveSlaver;
import sophia.foundation.data.SaveJob;
import sophia.foundation.data.SaveableObject.SaveableObjectState;

public class PlayerSaveSlaver extends AbstractSaveableObjectSaveSlaver<PlayerSaveableObject> {
	
	private static final Logger logger = Logger.getLogger(PlayerSaveSlaver.class);

	@Override
	protected void doSave(SaveJob<PlayerSaveableObject> saveJob) throws Exception{
		Collection<PlayerSaveableObject> collection = saveJob.getSaveObjects();
		
		Collection<PlayerSaveableObject> insertCollection = new ArrayList<>();
		Collection<PlayerSaveableObject> updateCollection = new ArrayList<>();
		Collection<PlayerSaveableObject> deleteCollection = new ArrayList<>();
		
		for (PlayerSaveableObject saveComponent : collection) {
			// 没有被保存
			SaveableObjectState state = saveComponent.getSaveableObjectState();
			if (logger.isDebugEnabled()) {
				logger.debug("SaveableObjectState " + state);
			}
			if (SaveableObjectState.isSaved == state) {
				continue;
			}
			
			if (SaveableObjectState.isNew == state) {
				insertCollection.add(saveComponent);
			} else if (SaveableObjectState.isDirty == state) {
				updateCollection.add(saveComponent);
			} else if (SaveableObjectState.isDelete == state) {
				deleteCollection.add(saveComponent);
			}
		}
		
		// FIXME: 黄晓源
		if (insertCollection.size() > 0) {
			Collection<?> t = insertCollection;
			PlayerDAO.getInstance().batchInsert((Collection<AbstractSaveableObject>) t);
		}
		if (updateCollection.size() > 0) {
			Collection<?> t = updateCollection;
			PlayerDAO.getInstance().batchUpdate((Collection<AbstractSaveableObject>) t);
		}
		if (deleteCollection.size() > 0) {
			Collection<?> t = deleteCollection;
			PlayerDAO.getInstance().batchDelete((Collection<AbstractSaveableObject>) t);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("insertCollection size " + insertCollection.size());
			logger.debug("updateCollection size " + updateCollection.size());
			logger.debug("deleteCollection size " + deleteCollection.size());
		}
	}

	@Override
	protected Collection<PlayerSaveableObject> doLoad() {
		return null;
	}

}
