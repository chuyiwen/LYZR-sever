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
package sophia.mmorpg.event;

import java.util.LinkedList;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.GameRoot;

public final class ResultEvent extends ActionEventBase {
	
	private static final LinkedList<ResultEvent> POOL = new LinkedList<ResultEvent>();
	public static int MAX_POOL_SIZE = 100;
	
	public static int getCurrentPoolSize() {
		return POOL.size();
	}
	
	public static void setMaxPoolSize(int maxSize) {
		assert(maxSize >= 0);
		MAX_POOL_SIZE = maxSize;
	}
	
	public static ResultEvent getInstance() {
		synchronized (POOL) {
			ResultEvent event;
			if (POOL.size() > 0) {
				event = POOL.pollFirst();
				return event;
			} else {
				return new ResultEvent();
			}
		}	
	}
	
	public static ResultEvent getInstance(short actionEventId, int code) {
		synchronized (POOL) {
			ResultEvent event;
			if (POOL.size() > 0) {
				event = POOL.pollFirst();
				event.setMsgId(actionEventId);
				event.setCode(code);
				return event;
			} else {
				return new ResultEvent(actionEventId, code);
			}
		}	
	}
	
	public static void pool(ResultEvent event) {
		assert(event != null);
		synchronized (POOL) {
			if (POOL.size() < MAX_POOL_SIZE) {
				event.identity = null;
				event.msgId = 0;
				event.code = 0;
				POOL.addLast(event);
			}
		}
	}
	
	public static void sendResult(Identity identity, short actionEventId, int code) {
		ResultEvent res = ResultEvent.getInstance(actionEventId, code);
		GameRoot.sendMessage(identity, res);
		ResultEvent.pool(res);
	}
	
	private int code;
	private short msgId;
	
	public ResultEvent() {
		actionEventId = MMORPGEventDefines.G2C_Resultevent;
	}
	
	public ResultEvent(final short actionEventId, final int code) {
		this();
		this.setMsgId(actionEventId);
		this.setCode(code);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		msgId = buffer.getShort();
		code = buffer.getInt();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putShort(msgId);
		buffer.putInt(code);
		return buffer;
	}

	public short getMsgId() {
		return msgId;
	}

	public void setMsgId(short msgId) {
		this.msgId = msgId;
	}
}
