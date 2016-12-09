package sophia.mmorpg.monsterRefresh;

import sophia.foundation.util.Position;
import sophia.mmorpg.base.scene.grid.SceneTerrainGridContainer;

public class Rectangle {

	private SceneTerrainGridContainer area;
	private Position position;

	private int width;
	private int height;

	public Rectangle() {
	}

	public SceneTerrainGridContainer getArea() {
		return area;
	}


	public void setArea(SceneTerrainGridContainer area) {
		this.area = area;
	}


	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
