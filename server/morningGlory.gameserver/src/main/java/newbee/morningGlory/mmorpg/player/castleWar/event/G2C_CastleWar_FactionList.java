package newbee.morningGlory.mmorpg.player.castleWar.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_CastleWar_FactionList extends ActionEventBase {
	private List<String> castleWarList = new ArrayList<>();
	private String kingCityUnion;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (StringUtils.isEmpty(kingCityUnion) || !castleWarList.contains(kingCityUnion)) {
			buffer.putShort((short) (castleWarList.size()));
		} else {
			buffer.putShort((short) (castleWarList.size()-1));
		}
		
		for (String unionName : castleWarList) {
			if (unionName.equals(kingCityUnion)) {
				continue;
			}
			putString(buffer, unionName);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public List<String> getCastleWarList() {
		return castleWarList;
	}

	public void setCastleWarList(List<String> castleWarList) {
		this.castleWarList = castleWarList;
	}

	public String getKingCityUnion() {
		return kingCityUnion;
	}

	public void setKingCityUnion(String kingCityUnion) {
		this.kingCityUnion = kingCityUnion;
	}

}