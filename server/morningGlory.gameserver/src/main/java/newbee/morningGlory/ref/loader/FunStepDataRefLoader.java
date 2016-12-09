package newbee.morningGlory.ref.loader;

import newbee.morningGlory.mmorpg.player.funStep.FunStepDataRef;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonObject;

public class FunStepDataRefLoader extends AbstractGameRefObjectLoader<FunStepDataRef> {

	public FunStepDataRefLoader(){
		super(RefKey.funStep);
	}
	
	@Override
	protected FunStepDataRef create() {
		return new FunStepDataRef();
	}
	
	@Override
	protected void fillNonPropertyDictionary(FunStepDataRef ref, JsonObject refData) {	
		//System.out.println("FunStepDataRef = " + ref.getId());
	}
}
