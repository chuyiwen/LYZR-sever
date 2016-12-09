package sophia.mmorpg.base.sprite.state;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState;
import sophia.mmorpg.base.sprite.state.adjunction.PoisoningState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;

public class FightSpriteStateMgrTest {
	
	private static FightSpriteStateMgr fightSpriteStateMgr = new FightSpriteStateMgr(null);

	@Before
	public void setUp() throws Exception {
		fightSpriteStateMgr.setDefaultActionState(IdleState.IdleState_Id);
		fightSpriteStateMgr.setDefaultMovementState(StopState.StopState_Id);
		fightSpriteStateMgr.setDefaultPostureState(StandedState.StandedState_Id);
		fightSpriteStateMgr.reset();
		AdjunctionState adjunctionState = FSMStateFactory.getAdjunctionState(ParalysisState.ParalysisState_Id);
		fightSpriteStateMgr.getAdjunctionStates().add(adjunctionState);
		adjunctionState = FSMStateFactory.getAdjunctionState(PoisoningState.PoisoningState_Id);
		fightSpriteStateMgr.getAdjunctionStates().add(adjunctionState);
	}

	@Test
	public void testSwitchStateShort() {
		fightSpriteStateMgr.switchState(DeadState.DeadState_Id);
		assertTrue(fightSpriteStateMgr.getAdjunctionStates().isEmpty());
	}

}
