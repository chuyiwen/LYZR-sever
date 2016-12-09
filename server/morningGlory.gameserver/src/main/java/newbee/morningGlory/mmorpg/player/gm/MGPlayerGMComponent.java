package newbee.morningGlory.mmorpg.player.gm;

import org.apache.log4j.Logger;

import sophia.foundation.data.PersistenceObject;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;

public class MGPlayerGMComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGPlayerGMComponent.class);
	public static final String Tag = "MGPlayerGMComponent";
	private PersistenceObject persistenceObject;
	private final MGPlayerGMMgr playerGMMgr = new MGPlayerGMMgr();

	public MGPlayerGMComponent() {
		
	}

	public PersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(PersistenceObject persistenceObject) {
		playerGMMgr.setPlayer(getConcreteParent());
		this.persistenceObject = persistenceObject;	
	}

	public MGPlayerGMMgr getPlayerGMMgr() {
		return playerGMMgr;
	}

}
