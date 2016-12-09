package newbee.morningGlory.ref.loader;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatarMgr;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.HandUpSkillRef;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonObject;

public class HandUpSkillRefLoader extends AbstractGameRefObjectLoader<HandUpSkillRef> {

	public HandUpSkillRefLoader(){
		super(RefKey.handUpSkill);
	}
	@Override
	protected HandUpSkillRef create() {
		return new HandUpSkillRef();
	}

	
	@Override
	protected void fillNonPropertyDictionary(HandUpSkillRef ref, JsonObject refData) {
		String refId = refData.get("refId").getAsString();
		ref.setSkillRefId(refId);
		PlayerAvatarMgr.putHandUpSkillRef(ref);
		super.fillNonPropertyDictionary(ref, refData);
	}
	
	
}