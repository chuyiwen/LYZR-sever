/**
 * 
 */
package sophia.foundation.communication.practice;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.util.DebugUtil;

public final class IdentityBindedSessionManager {

	private static final Logger logger = Logger.getLogger(IdentityBindedSessionManager.class);

	private final ConcurrentHashMap<Identity, Connection> map = new ConcurrentHashMap<>();

	public IdentityBindedSessionManager() {

	}

	public void addSession(Identity identity, Connection session) {
		if (identity == null || session == null) {
			throw new NullPointerException();
		}

		Connection oldSession = map.put(identity, session);
		if (oldSession != null) {
			try {
				oldSession.close(true);
			} catch (IOException e) {
			}
		}
	}

	public void removeSession(Identity identity, Connection session) {
		if (identity == null || session == null) {
			throw new NullPointerException();
		}

		map.remove(identity, session);
	}

	public Connection getSession(Identity identity) {
		return map.get(identity);
	}

	public int getIdentityNumber() {
		return map.size();
	}

	public Collection<Connection> getSessionCollection() {
		return map.values();
	}

	public Collection<Identity> getIdentityCollection() {
		return map.keySet();
	}

	public void sendMessageToAllSession(ActionEventBase message) {
		Collection<Connection> values = map.values();
		for (Connection session : values) {
			try {
				session.sendMessage(message);
			} catch (IOException e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	public boolean checkSpeedUpMessage(Identity identity) {

		if (identity == null) {
			throw new NullPointerException();
		}

		Connection session = map.get(identity);
		if (session != null) {
			AtomicInteger isSpeedUp = (AtomicInteger) session.getAttribute("isSpeedUp");
			if (isSpeedUp != null && isSpeedUp.get() == 1) {
				return true;
			}
		}

		return false;
	}

	public void closeSession(Identity identity) {
		if (identity == null) {
			throw new NullPointerException();
		}

		Connection session = map.get(identity);
		if (session != null) {
			try {
				session.close(true);
			} catch (IOException e) {
			}
		}
	}

	public void sendMessageByIdentity(ActionEventBase message, Identity identity) throws IOException {
		if (identity == null) {
			throw new NullPointerException();
		}

		Connection session = map.get(identity);
		if (session != null) {
			try {
				session.sendMessage(message);
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("sendMessageByIdentity error, identityName=" + identity.getName() + DebugUtil.printStack(e));
				}
				throw e;
			}
		}
	}
}
