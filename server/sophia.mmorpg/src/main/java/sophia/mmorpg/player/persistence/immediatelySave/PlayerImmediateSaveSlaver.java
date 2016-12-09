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
package sophia.mmorpg.player.persistence.immediatelySave;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.data.AbstractSaveableObjectSaveSlaver;
import sophia.foundation.data.SaveJob;
import sophia.foundation.data.SaveableObject.SaveableObjectState;

public class PlayerImmediateSaveSlaver extends AbstractSaveableObjectSaveSlaver<PlayerImmediateSaveableObject> {
	
	private static final Logger logger = Logger.getLogger(PlayerImmediateSaveSlaver.class);

	
	@Override
	protected void doSave(SaveJob<PlayerImmediateSaveableObject> saveJob) throws Exception {
		Collection<PlayerImmediateSaveableObject> collection = saveJob.getSaveObjects();
		
		Collection<PlayerImmediateSaveableObject> insertCollection = new ArrayList<>();
		Collection<PlayerImmediateSaveableObject> updateCollection = new ArrayList<>();
		Collection<PlayerImmediateSaveableObject> deleteCollection = new ArrayList<>();
		for (PlayerImmediateSaveableObject saveComponent : collection) {
			// 没有被保存
			SaveableObjectState state = saveComponent.getSaveableObjectState();
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
			PlayerImmediateDAO.getInstance().batchInsert((Collection<AbstractSaveableObject>) t);
		}
		if (updateCollection.size() > 0) {
			Collection<?> t = updateCollection;
			PlayerImmediateDAO.getInstance().batchUpdate((Collection<AbstractSaveableObject>) t);
		}
		if (deleteCollection.size() > 0) {
			Collection<?> t = deleteCollection;
			PlayerImmediateDAO.getInstance().batchDelete((Collection<AbstractSaveableObject>) t);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("insertCollection size " + insertCollection.size());
			logger.debug("updateCollection size " + updateCollection.size());
			logger.debug("deleteCollection size " + deleteCollection.size());
		}
	}

	@Override
	protected Collection<PlayerImmediateSaveableObject> doLoad() {
		return null;
	}

}
