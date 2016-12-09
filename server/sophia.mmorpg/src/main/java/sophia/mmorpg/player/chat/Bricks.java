package sophia.mmorpg.player.chat;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;

import com.google.common.base.Preconditions;

public class Bricks {

	private static final Logger logger = Logger.getLogger(Bricks.class);
	// 角色
	// {p=小明<0000302>}
	private final static String characterBrick = "{p=%1$s<%2$s>}";

	public final static String obtainCharacterBrick(Player player) {
		return String.format(characterBrick, player.getName(), player.getId());
	}

	// 坐标(场景)
	// {s=太原城,<S001,50,50>}
	private final static String coordinateBrick = "{s=%1$s<%2$s,%3$s,%4$s>}";

	/**
	 * @param name
	 *            场景名称
	 * @param sceneId
	 *            场景Id
	 * @param x
	 *            X坐标
	 * @param y
	 *            Y坐标
	 * @return
	 */
	public final static String obtainCoordinateBrick(String name,
			String sceneId, int x, int y) {
		return String.format(coordinateBrick, name, sceneId, x, y);
	}

	// 物品
	// {g=惊世战鞋,<1>}
	private final static String coordinateItemBrick = "{g=%1$s<%2$s>}";

	public static Object obtainItemBrick(Item item) {
		return String.format(coordinateItemBrick, item.getName(), item.getId());
	}

	public static String getContents(String systemPromptConfigRefId,
			Object... values) {
		String bricks = null;
		Preconditions.checkNotNull(systemPromptConfigRefId);
		Preconditions.checkNotNull(values);
		if (StringUtils.isNotEmpty(systemPromptConfigRefId)) {
			SystemPromptConfigRef ref = GameRoot.getGameRefObjectManager()
					.getManagedObject(systemPromptConfigRefId,
							SystemPromptConfigRef.class);
			if (ref == null) {
				logger.error("systemPromptConfigRefId:"
						+ systemPromptConfigRefId + "不存在");
				return null;
			}
			bricks = ref.getBricks();
		}
		if (StringUtils.isNotEmpty(bricks)) {	
			bricks = String.format(bricks, values);			
		}
		return bricks;
	}
}
