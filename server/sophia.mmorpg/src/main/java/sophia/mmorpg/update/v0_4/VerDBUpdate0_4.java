package sophia.mmorpg.update.v0_4;

import sophia.mmorpg.update.VerDBUpdate;

/** 
 * Copyright (c) 2014 by 游爱.
 *
 * @version 1.0
 */
public class VerDBUpdate0_4 extends VerDBUpdate{

	
	@Override
	public byte getMajorVersion() {
		return 0;
	}

	@Override
	public byte getMinorVersion() {
		return 0;
	}

	@Override
	public byte getFractionalVersion() {
		return 4;
	}
	
	@Override
	public String getVersionSqlFileName(){
		
		return "v0_4";
	}
}
