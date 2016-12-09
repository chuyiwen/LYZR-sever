package sophia.mmorpg.player.persistence.update;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-3 下午4:57:54
 * @version 1.0
 */
public interface IPatch {
	public static final int SKIP = 0;
	public static final int SUCCESS = 1;
	public static final int FAIL = 2;

	public abstract String getPatchName();

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getPublishDate();

	public abstract void upgrade();

	public abstract String getErrMsg();

	public abstract int getState();
}
