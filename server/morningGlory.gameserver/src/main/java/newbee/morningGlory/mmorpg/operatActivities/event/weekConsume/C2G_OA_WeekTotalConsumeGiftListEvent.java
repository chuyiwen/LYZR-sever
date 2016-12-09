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
package newbee.morningGlory.mmorpg.operatActivities.event.weekConsume;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_OA_WeekTotalConsumeGiftListEvent extends ActionEventBase {

	/* (non-Javadoc)
	 * @see sophia.foundation.communication.core.ActionEventBase#packBody(org.apache.mina.core.buffer.IoBuffer)
	 */
	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see sophia.foundation.communication.core.ActionEventBase#unpackBody(org.apache.mina.core.buffer.IoBuffer)
	 */
	@Override
	public void unpackBody(IoBuffer arg0) {

	}

}
