package newbee.morningGlory.ref.loader.activity;

import java.util.Iterator;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossRef;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossTransferIn;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossTransferOut;
import newbee.morningGlory.ref.RefKey;
import sophia.mmorpg.player.team.PlayerTeamManagerComponent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TeamBossRefLoader extends AbstractSceneActivityRefLoader {
	
	public TeamBossRefLoader() {
		super(RefKey.teamBoss);
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		JsonElement activityData = refData.get("activityData").getAsJsonArray();
		Iterator<JsonElement> orderFieldData = activityData.getAsJsonArray().iterator();
		while (orderFieldData.hasNext()) {
			TeamBossRef teamBossRef = new TeamBossRef();
			JsonObject elment = orderFieldData.next().getAsJsonObject();
			int type = elment.get("type").getAsInt();
			
			JsonElement contentScene = elment.get("contentScene");
			if (contentScene.isJsonArray()) {
				Iterator<JsonElement> contentSceneData = contentScene.getAsJsonArray().iterator();
				while (contentSceneData.hasNext()) {
					JsonObject elem = contentSceneData.next().getAsJsonObject();
					String targetScene = elem.get("targetScene").getAsString();
					teamBossRef.addToContentScene(targetScene);
				}
			}
			
			JsonObject transfer = elment.get("transfer").getAsJsonObject();
			setTransferData(teamBossRef, transfer);
			teamBossRef.setType((byte) type);
			ref.addComponentRef(teamBossRef);
			String sceneRefId = refData.get("sceneRefId").getAsString();
			PlayerTeamManagerComponent.teamBossScene.add(sceneRefId);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}
	
	private void setTransferData(TeamBossRef teamBossRef, JsonObject transfer) {
		TeamBossTransferIn transferIn = new TeamBossTransferIn();
		TeamBossTransferOut transferOut = new TeamBossTransferOut();
		
		JsonObject transferInData = transfer.get("transferIn").getAsJsonObject();
		JsonObject transferOutData = transfer.get("transferOut").getAsJsonObject();
		
		setTransferInData(teamBossRef, transferIn, transferInData);
		setTransferOutData(teamBossRef, transferOut, transferOutData);
	}
	
	private void setTransferInData(TeamBossRef teamBossRef, TeamBossTransferIn transferIn, JsonObject transferInData) {
		String targetScene1 = transferInData.get("targetScene").getAsString();
		int tranferInId = transferInData.get("tranferInId").getAsInt();
		transferIn.setTargetScene(targetScene1);
		transferIn.setTranferInId(tranferInId);
		teamBossRef.setTransferIn(transferIn);
	}
	
	private void setTransferOutData(TeamBossRef teamBossRef, TeamBossTransferOut transferOut, JsonObject transferOutData) {
		String targetScene2 = transferOutData.get("targetScene").getAsString();
		int transferOutId = transferOutData.get("tranferOutId").getAsInt();
		transferOut.setTargetScene(targetScene2);
		transferOut.setTranferInId(transferOutId);
		teamBossRef.setTransferOut(transferOut);
	}
}
