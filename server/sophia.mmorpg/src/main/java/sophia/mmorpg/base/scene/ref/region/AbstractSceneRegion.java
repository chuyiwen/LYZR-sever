package sophia.mmorpg.base.scene.ref.region;

import sophia.mmorpg.base.scene.grid.SceneTerrainGridContainer;

public class AbstractSceneRegion {
	protected int id;
	protected SceneTerrainGridContainer region;
	
	public AbstractSceneRegion(int id, SceneTerrainGridContainer region) { 
		super();
		this.id = id;
		this.region = region;
	}

	public final int getId() {
		return id;
	}

	public final SceneTerrainGridContainer getRegion() {
		return region;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractSceneRegion other = (AbstractSceneRegion) obj;
		if (id != other.id)
			return false;
		return true;
	}
}