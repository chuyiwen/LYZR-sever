package newbee.morningGlory.mmorpg.ladder.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.ladder.MGLadderMember;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ObjectDAO;

public class MGLadderDAO extends ObjectDAO<MGLadderMember> {
	private static final Logger logger = Logger.getLogger(MGLadderDAO.class);

	private static final String table = "game_ladder";
	private static MGLadderDAO instance = new MGLadderDAO();

	private MGLadderDAO() {

	}

	public static MGLadderDAO getInstance() {
		return instance;
	}

	private LadderPersistenceObject persistenceObject = LadderPersistenceObject.getInstance();

	public String getInstertSql() {
		return "insert into " + table + "(id,memberData, isDelete) values" + "(?, ?, ?)";
	}

	public String getSelectSql() {
		return "select * from " + table;
	}

	public String getDeleteSql() {
		return "update " + table + " set isDelete = 1 where id = ? ";
	}

	public String getUpdateSql() {
		return "update " + table + " set memberData = ? where id = ?";
	}

	public Map<Integer, MGLadderMember> selectData() {
		Map<Integer, MGLadderMember> ladderMembers = new HashMap<Integer, MGLadderMember>();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(getSelectSql());
			rs = ps.executeQuery();
			while (rs.next()) {
				String id = rs.getString("id");
				byte[] memberData = rs.getBytes("memberData");
				int isDelete = rs.getInt("isDelete");
				if(isDelete == 1){
					continue;
				}
				
				MGLadderMember member = persistenceObject.ladderMemberInfoFromBytes(id, memberData);
				ladderMembers.put(member.getRank(), member);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + ps.toString() + "]");
			}
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return ladderMembers;
	}

	public void insertData(MGLadderMember member) throws SQLException {
		String id = member.getId();
		byte[] memberData = persistenceObject.ladderMemberInfoToBytes(member);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getInstertSql());
			ps.setString(1, id);
			ps.setBytes(2, memberData);
			ps.setInt(3, 0);
			ps.executeUpdate();
			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + ps.toString() + "]");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	public void updateMemberData(MGLadderMember member) throws SQLException {
		String id = member.getId();
		byte[] memberData = persistenceObject.ladderMemberInfoToBytes(member);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getUpdateSql());
			ps.setBytes(1, memberData);
			ps.setString(2, id);
			ps.executeUpdate();

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + ps.toString() + "]");
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	public void deleteMemberData(MGLadderMember member) throws SQLException {
		String id = member.getId();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getDeleteSql());
			ps.setString(1, id);
			ps.executeUpdate();

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + ps.toString() + "]");
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	protected AbstractSaveableObject getInsertData(MGLadderMember t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getUpdateData(MGLadderMember t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getDeleteData(MGLadderMember t) {
		// TODO Auto-generated method stub
		return null;
	}
}
