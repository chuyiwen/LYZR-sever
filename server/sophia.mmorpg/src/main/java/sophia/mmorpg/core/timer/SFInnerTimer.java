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
package sophia.mmorpg.core.timer;

import java.util.Calendar;
import java.util.UUID;

abstract class SFInnerTimer implements SFTimer {
	private final String id = UUID.randomUUID().toString();
	
	protected final SFTimeChimeService timerChimeService;
	
	protected final SFTimeChimeListener timeChimeListener;
	
	protected SFInnerTimer(SFTimeChimeService timerChimeService, SFTimeChimeListener timeChimeListener) {
		this.timerChimeService = timerChimeService;
		this.timeChimeListener = timeChimeListener;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void cancel() {
		timerChimeService.cancel(this);
	}

	@Override
	public SFTimeChimeListener getTimeChimeListener() {
		return timeChimeListener;
	}

	abstract void timeTick(long crtTime, Calendar crtCalendar);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SFInnerTimer other = (SFInnerTimer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
