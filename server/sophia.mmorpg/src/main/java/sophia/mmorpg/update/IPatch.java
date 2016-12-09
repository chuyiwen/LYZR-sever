package sophia.mmorpg.update;

import sophia.mmorpg.update.util.UpData;

/**
 * Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public interface IPatch {
	/**
	 * 升级对应的数据
	 * @param colData
	 * @return 成功返回true，否则 false
	 * @throws Exception
	 */
	public abstract boolean upgrade(UpData colData,String dbType)throws Exception;
}
