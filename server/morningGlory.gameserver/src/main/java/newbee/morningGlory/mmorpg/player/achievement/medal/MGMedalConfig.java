package newbee.morningGlory.mmorpg.player.achievement.medal;

import java.util.HashMap;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

public class MGMedalConfig extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -4349838891666326600L;
	private Map<String, MGMedalDataRef> medalDataRefMap = new HashMap<String, MGMedalDataRef>();
	public static final String MEDALID = "medalId";
	private String id;

	public MGMedalConfig() {

	}

	public Map<String, MGMedalDataRef> getMedalDataRefMap() {
		return medalDataRefMap;
	}

	public void setMedalDataRefMap(Map<String, MGMedalDataRef> medalDataRefMap) {
		this.medalDataRefMap = medalDataRefMap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
