package sophia.mmorpg.player.chat;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class SystemPromptConfigRef extends AbstractGameRefObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2481192510408340718L;

	public String getBricks() {
		return MGPropertyAccesser.getBricks(getProperty());
	}
}
