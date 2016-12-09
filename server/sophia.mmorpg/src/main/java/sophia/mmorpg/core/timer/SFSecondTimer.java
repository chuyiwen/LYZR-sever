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

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;

final class SFSecondTimer extends SFInnerTimer {
	private static final Logger logger = Logger.getLogger(SFSecondTimer.class.getName());
	
	public SFSecondTimer(SFTimeChimeService timerChimeService, SFTimeChimeListener timeChimeListener) {
		super(timerChimeService, timeChimeListener);
	}
	
	@Override
	void timeTick(long crtTime, Calendar crtCalendar) {
		try {
			this.timeChimeListener.handleTimeChime();
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(toString() + "timeTick...");
		}
	}

	@Override
	public String type() {
		return SFSecondTimer.class.getSimpleName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SFSecondTimer [getId()=");
		builder.append(getId());
		builder.append("]");
		return builder.toString();
	}
}
