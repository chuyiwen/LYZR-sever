package newbee.morningGlory.mmorpg.union.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.union.MGUnion;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ObjectDAO;

public class MGUnionDAO extends ObjectDAO<MGUnion> {
	private static final Logger logger = Logger.getLogger(MGUnionDAO.class);
	private static final String table = "game_union";
	private UnionPersistenceObject persistenceObject = UnionPersistenceObject.getInstance();
	private static MGUnionDAO unionDAO = new MGUnionDAO();

	public static MGUnionDAO getInstance() {
		return unionDAO;
	}

	@Override
	protected String getDeleteSql() {
		String deleteSql = "delete from " + table + " where id =?";
		return deleteSql;
	}

	public void deleteData(MGUnion union) throws Exception {
		String id = union.getId();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getDeleteSql());
			ps.setString(1, id);
			ps.executeUpdate();
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+ps.toString()+"]");
			}
		} catch (Exception e) {
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
	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(id, unionData, memberData, applyData) values (?, ?, ?, ?)";
		return insertSql;
	}

	public void insertData(MGUnion union) throws Exception {
		String id = union.getId();
		byte[] unionBytes = persistenceObject.unionInfoToBytes10001(union);
		byte[] memberBytes = persistenceObject.memberInfoToBytes10001(union);
		byte[] applyBytes = persistenceObject.applyInfoToBytes10001(union);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getInstertSql());
			ps.setString(1, id);
			ps.setBytes(2, unionBytes);
			ps.setBytes(3, memberBytes);
			ps.setBytes(4, applyBytes);
			ps.executeUpdate();
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+ps.toString()+"]");
			}
			
		} catch (Exception e) {
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
	protected String getUpdateSql() {
		return null;
	}
	
	protected String getUpdateUnionInfoSql(){
		String updateSql = "update " + table + " set unionData = ? where id = ?";
		return updateSql;
	}
	
	protected String getUpdateMemberInfoSql(){
		String updateSql = "update " + table + " set memberData = ? where id = ?";
		return updateSql;
	}
	
	protected String getUpdateApplyInfoSql(){
		String updateSql = "update " + table + " set applyData = ? where id = ?";
		return updateSql;
	}

	public void updateUnionData(MGUnion union) throws Exception{
		String sql = getUpdateUnionInfoSql();
		byte[] bytes = persistenceObject.unionInfoToBytes10001(union);
		updateData(union, sql, bytes);
	}
	
	public void updateMemberData(MGUnion union) throws Exception{
		String sql = getUpdateMemberInfoSql();
		byte[] bytes = persistenceObject.memberInfoToBytes10001(union);
		updateData(union, sql, bytes);
	}
	
	public void updateApplyData(MGUnion union) throws Exception{
		String sql = getUpdateApplyInfoSql();
		byte[] bytes = persistenceObject.applyInfoToBytes10001(union);
		updateData(union, sql, bytes);
	}
	
	public void updateData(MGUnion union, String sql, byte[] bytes) throws Exception{
		String id = union.getId();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setBytes(1, bytes);
			ps.setString(2, id);
			ps.executeUpdate();
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+ps.toString()+"]");
			}
			
		} catch (Exception e) {
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

	protected String getSelectSql() {
		String selectSql = "select * from " + table;
		return selectSql;
	}

	public ConcurrentHashMap<String, MGUnion> selectData(){
		ConcurrentHashMap<String, MGUnion> nameToUnionMap = new ConcurrentHashMap<String, MGUnion>();
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(getSelectSql());
			rs = ps.executeQuery();
			while (rs.next()) {
				String id = rs.getString("id");
				byte[] unionData = rs.getBytes("unionData");
				byte[] memberData = rs.getBytes("memberData");
				byte[] applyData = rs.getBytes("applyData");
				MGUnion union = persistenceObject.unionInfoFromBytes(id, unionData, memberData, applyData);
				nameToUnionMap.put(union.getName(), union);
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+ps.toString()+"]");
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
		return nameToUnionMap;
	}

	@Override
	protected AbstractSaveableObject getInsertData(MGUnion arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getDeleteData(MGUnion arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getUpdateData(MGUnion arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
