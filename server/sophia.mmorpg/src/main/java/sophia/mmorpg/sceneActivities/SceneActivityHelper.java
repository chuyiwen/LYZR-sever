/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package sophia.mmorpg.sceneActivities;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class SceneActivityHelper {
	
	private static List<String> specialActivityScenes = Arrays.asList("S035", "S036", "S053", "S054", "S045", "S046", "S063", "S064", "S031", "S032", "S049", "S050", "S041", "S042", "S059", "S060", "S039", "S040", "S057", "S058", "S033", "S034", "S051", "S052", "S037", "S038", "S055", "S056", "S043", "S044", "S061", "S062");
	private static boolean isSpecialActivityScene(AbstractGameSceneRef sceneRef) {
		return specialActivityScenes.contains(sceneRef.getId());
	}
	
	public static boolean checkEnter(Player player, AbstractGameSceneRef targetRef) {
		
		AbstractGameSceneRef crtRef = player.getCrtScene().getRef();
		
		int sceneOpenLevel = MGPropertyAccesser.getOpenLevel(targetRef.getProperty());
		if (player.getExpComponent().getLevel() < sceneOpenLevel) {
			return true;
		}
		
		if (targetRef.getType() == SceneRef.Activity) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(targetRef.getId());
			byte crtActivityState = sceneActivity.getCrtActivityState();
			byte preActivityState = sceneActivity.getPreActivityState();
			if (crtActivityState == SceneActivityInterface.ACTIVITY_START || preActivityState == SceneActivityInterface.ACTIVITY_PRE_START) {
				if(isSpecialActivityScene(targetRef)){
					if (sceneActivity.checkEnter(player)) {
						
						if(player.isUseFeixue()){
							return true;
						}	
						
						if(sceneActivity.onEnter(player)){
							return false;
						}else{
							return true;
						}
					}
				}				
				else if (!sceneActivity.checkEnter(player)) {
					return false;
				}
			}
		} else if (crtRef.getType() == SceneRef.Activity) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(crtRef.getId());
			byte crtActivityState = sceneActivity.getCrtActivityState();
			byte preActivityState = sceneActivity.getPreActivityState();
			if (crtActivityState == SceneActivityInterface.ACTIVITY_START || preActivityState == SceneActivityInterface.ACTIVITY_PRE_START) {
				if (!sceneActivity.checkLeave(player)) {
					return false;
				}
			}
		}
		
		return true;
	}

	public static boolean checkTransfer(AbstractGameSceneRef crtRef) {
		boolean ret = true;
//		if (crtRef.getType() == SceneRef.Activity) {
//			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
//			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(crtRef.getId());
//			byte crtActivityState = sceneActivity.getCrtActivityState();
//			if (crtActivityState == 2) {
//				ret = true;
//			}
//		}
		if (StringUtils.equals(crtRef.getId(), "S009") || StringUtils.equals(crtRef.getId(), "S012")) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId("S012");
			byte crtActivityState = sceneActivity.getCrtActivityState();
			if (crtActivityState == SceneActivityInterface.ACTIVITY_START) {
				ret = false;
			}
		}
		
		//付费地宫不能使用传送
		if(StringUtils.equals(crtRef.getId(), "S070") || StringUtils.equals(crtRef.getId(), "S071")){
			ret = false;
		}
		
		return ret;
	}

}
