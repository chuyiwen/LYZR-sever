package newbee.morningGlory.mmorpg.gameInstance.util;

import sophia.foundation.util.Position;
import sophia.mmorpg.utils.SFRandomUtils;

public class RefreshAreaUtil {

	public static Position getRandomPosition(int x_left_up, int y_left_up, int x_right_down, int y_right_down) {
		Position position = new Position();
		int randomX = SFRandomUtils.random(x_left_up, x_right_down);
		int randomY = SFRandomUtils.random(y_left_up, y_right_down);
		position.setX(randomX);
		position.setY(randomY);
		return position;
	}

}
