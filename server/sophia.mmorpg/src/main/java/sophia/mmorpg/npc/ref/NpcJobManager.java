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
package sophia.mmorpg.npc.ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

public final class NpcJobManager {
	private List<NpcJob> npcJobList = new ArrayList<>();

	public NpcJobManager() {
	}

	public final NpcJob getNpcJob(Short jobType) {
		for (NpcJob npcJob : npcJobList) {
			if (npcJob.getJobType().equals(jobType)) {
				return npcJob;
			}
		}
		return null;
	}
	
	public final List<NpcJob> getNpcJobList() {
		return npcJobList;
	}

	public final NpcJob getTopPriorityNpcJob() {
		if (npcJobList.size() == 0) {
			return null;
		}

		return npcJobList.get(0);
	}

	public final void addJob(NpcJob npcJob) {
		Preconditions.checkNotNull(npcJob, "npcJob can not be null.");

		if (npcJobList.contains(npcJob)) {
			return;
		}

		npcJobList.add(npcJob);
		Collections.sort(this.npcJobList);
	}

	public final void setNpcJobList(List<NpcJob> npcJobList) {
		this.npcJobList = new ArrayList<>(npcJobList);
		Collections.sort(this.npcJobList);
	}
}
