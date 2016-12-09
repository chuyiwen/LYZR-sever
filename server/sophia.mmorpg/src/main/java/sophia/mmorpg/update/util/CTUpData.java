package sophia.mmorpg.update.util;

import sophia.mmorpg.update.IPatch;

/**
 * 
 * 创建表的数据 Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public class CTUpData extends UpData {

	@Override
	public IPatch getPatch() {
		return createTablePath;
	}
}
