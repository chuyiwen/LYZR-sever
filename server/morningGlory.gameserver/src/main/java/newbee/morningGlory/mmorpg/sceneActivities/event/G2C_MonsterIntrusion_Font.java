/**
 * 
 */
package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * @author Administrator
 *
 */
public class G2C_MonsterIntrusion_Font extends ActionEventBase {

	private byte fontType;//0:代表没有显示  1：显示第一条  2：显示第二条 3：显示第三条
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(fontType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public byte getFontType() {
		return fontType;
	}

	public void setFontType(byte fontType) {
		this.fontType = fontType;
	}



}
