package sophia.mmorpg.monsterRefresh;

public class RefreshConditionType {
	/** 当副本场景创建时，怪物出现 */
	public static final byte OnSceneCreated_Type = 1;
	/** 当指定怪物（组）出现，以“指定参数时间”后的时间，怪物出现 */
	public static final byte OnPreMonster_Arise_Type = 2;
	/** 当指定怪物（组）死亡，以“指定参数时间”后的时间，怪物出现 */
	public static final byte OnPreMonster_Dead_Type = 3;
	/** 当处于场景怪物刷新时间段时，怪物出现 */
	public static final byte InSceneTimeRange_Type = 4;
}
