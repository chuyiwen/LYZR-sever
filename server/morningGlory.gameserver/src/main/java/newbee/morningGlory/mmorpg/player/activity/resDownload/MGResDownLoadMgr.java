package newbee.morningGlory.mmorpg.player.activity.resDownload;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MGResDownLoadMgr {
	
	private static final List<String> rewardIds = new ArrayList<String>(10);
	
	private List<MGResDownLoadData> resDownloads = new ArrayList<>();

	public List<MGResDownLoadData> getResDownloads() {
		return resDownloads;
	}

	public void setResDownloads(List<MGResDownLoadData> resDownloads) {
		this.resDownloads = resDownloads;
	}
	
	public boolean addResDownLoadData(MGResDownLoadData resDownLoadData){	
		return resDownloads.add(resDownLoadData);
	}
	
	public MGResDownLoadData getResDownLoadData(String rewardId){
		
		for(MGResDownLoadData resDownLoadData : resDownloads){
			if(StringUtils.equals(rewardId, resDownLoadData.getRewardId())){
				return resDownLoadData;
			}
		}
		
		return null;
	}
	public static void addRewardIds(String rewardId){
		rewardIds.add(rewardId);
	}
	public static List<String> getRewardids() {
		return rewardIds;
	}
}
