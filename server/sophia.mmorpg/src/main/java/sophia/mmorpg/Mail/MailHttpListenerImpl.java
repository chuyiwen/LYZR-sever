package sophia.mmorpg.Mail;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import sophia.mmorpg.Mail.persistence.MailDao;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.HttpConnection;

public final class MailHttpListenerImpl implements HttpConnection.CallbackListener {

	private Player owner;
	private Mail mail;
	private short actionEventId;

	public void callBack(final int responseCode, final String result) {
		if (owner == null)
			return;

		if (responseCode == HttpStatus.SC_OK && !StringUtils.isEmpty(result) && result.equals("success")) {
			MailDao.getInstance().insertMail(mail);
			ResultEvent.sendResult(owner.getIdentity(), getActionEventId(), MMORPGSuccessCode.CODE_SUCCESS);
		} else {
			ResultEvent.sendResult(owner.getIdentity(), getActionEventId(), MMORPGErrorCode.CODE_SEND_CUSTOMER_MAIL_FAIL);
		}

	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	public short getActionEventId() {
		return actionEventId;
	}

	public void setActionEventId(short actionEventId) {
		this.actionEventId = actionEventId;
	}

}