package newbee.morningGlory.mmorpg.ladder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.mmorpg.ladder.persistence.MGLadderDAO;
import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

public class LadderSystemComponent extends AbstractComponent{

	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	
	public LadderSystemComponent(){
		
	}
	
	public void configLadderData() {
		Map<Integer, MGLadderMember> members = MGLadderDAO.getInstance().selectData();
		
		List<MGLadderMember> list = new ArrayList<>();
		list.addAll(members.values());
		Collections.sort(list, new Comparator<MGLadderMember>() {
			@Override
			public int compare(MGLadderMember o1, MGLadderMember o2) {
				return o1.getRank() - o2.getRank();
			}
		});
		
		Map<Integer, MGLadderMember> newMembers = new HashMap<>();
		Map<String, Integer> nameToRankMapping = new HashMap<String, Integer>();
		int limitCount = 1000;
		int rank = 0;
		for(int i= 0; i< list.size(); i++){
			MGLadderMember mgLadderMember = list.get(i);
			String playerName = mgLadderMember.getPlayerName();
			// 消除重复的玩家天梯排名
			if (nameToRankMapping.containsKey(playerName)) {
				continue;
			}
			
			// 消除天梯成员存在，但找不到玩家的排名
			Player player = playerManager.getPlayerByName(playerName);
			if( player == null ){
				continue;
			}
			
			
			rank += 1;
			nameToRankMapping.put(playerName, rank);
			
			if (mgLadderMember.getRank() != rank) {
				mgLadderMember.setLastRank(rank);
			}
			
			mgLadderMember.setRank(rank);
			newMembers.put(rank, mgLadderMember);
			if (--limitCount <= 0) {
				break;
			}
		}
		
		MGLadderMemberMgr.setMembers(newMembers);
		MGLadderMemberMgr.setNameToRankMapping(nameToRankMapping);
	}
}
