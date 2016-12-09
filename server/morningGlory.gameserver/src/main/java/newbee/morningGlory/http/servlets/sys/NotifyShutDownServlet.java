package newbee.morningGlory.http.servlets.sys;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.actionEvent.G2C_GameServerShutDown;
import newbee.morningGlory.http.actionEvent.GameServerGmEventDefines;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

public class NotifyShutDownServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -583045234753236086L;
	private static final Logger logger = Logger.getLogger(NotifyShutDownServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("通知玩家即将关闭服务器");
		}
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		G2C_GameServerShutDown shutDown = MessageFactory.getConcreteMessage(GameServerGmEventDefines.G2C_GameServerShutDown);
		for (Player player : playerManager.getOnlinePlayerList()) {
			GameRoot.sendMessage(player.getIdentity(), shutDown);
		}
	}
}
