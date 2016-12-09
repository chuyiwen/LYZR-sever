package sophia.mmorpg.Mail;

import java.util.List;
import sophia.mmorpg.Mail.persistence.GmMailDao;

public class GmMailMgr {

	private List<GmMail> gmMaillist;

	private static GmMailMgr instance = new GmMailMgr();


	private GmMailMgr() {
	}

	public static GmMailMgr getInstance() {
		return instance;
	}

	public void loadAll() {
		setGmMaillist(GmMailDao.getInstance().selectAll());
	}

	public List<GmMail> getGmMaillist() {
		return gmMaillist;
	}

	public void setGmMaillist(List<GmMail> gmMaillist) {
		this.gmMaillist = gmMaillist;
	}
}
