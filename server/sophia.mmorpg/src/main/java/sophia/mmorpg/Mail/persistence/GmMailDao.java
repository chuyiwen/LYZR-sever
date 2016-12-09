/**
 * 
 */
package sophia.mmorpg.Mail.persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ObjectDAO;
import sophia.mmorpg.Mail.GmMail;

/**
 * @author Administrator
 *
 */
public class GmMailDao extends ObjectDAO<GmMail> {
	private static final Logger logger = Logger.getLogger(GmMailDao.class);

	private static final String table = "Gmmail";

	private GmMailDao() {
	}

	private static GmMailDao mailDao = new GmMailDao();

	public static GmMailDao getInstance() {
		return mailDao;
	}

	@Override
	protected String getInstertSql() {
		return null;
	}

	@Override
	protected AbstractSaveableObject getInsertData(GmMail t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getUpdateSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getUpdateData(GmMail t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDeleteSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getDeleteData(GmMail t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected List<GmMail> mapResultSet(ResultSet rs) throws SQLException{
		List<GmMail> list = new ArrayList<GmMail>();
		while (rs.next()) {
			GmMail mail = new GmMail();
			mail.setMailId(rs.getString("mailId"));
			mail.setContent(rs.getString("content"));
			mail.setGold(rs.getInt("gold"));
			mail.setCoin(rs.getInt("coin"));
			mail.setItem(rs.getString("item"));	
			mail.setTime(rs.getLong("time"));			
			mail.setEffectBeginTime(rs.getLong("begintime"));
			mail.setEffectEndTime(rs.getLong("endtime"));
			mail.setPlayerMinLevel(rs.getInt("minlevel"));
			mail.setPlayerMaxLevel(rs.getInt("maxlevel"));
			mail.setBindGold(rs.getInt("bindGold"));
			mail.setTitle(rs.getString("title"));
			
			list.add(mail);
		}

		return list;
	}
	
	public boolean insertMail(GmMail t) {
		boolean ret = false;
		
		String sql ="insert into " +table+"(mailId,title,content,gold,coin,item,time,minlevel,maxlevel,begintime,endtime,bindGold) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			callableStatement.setObject(1, t.getMailId());
			callableStatement.setObject(2, t.getTitle());
			callableStatement.setObject(3, t.getContent());
			callableStatement.setObject(4, t.getGold());
			callableStatement.setObject(5, t.getCoin());
			callableStatement.setObject(6, t.getItem());
			callableStatement.setObject(7, t.getTime());
			
			callableStatement.setObject(8, t.getPlayerMinLevel());
			callableStatement.setObject(9, t.getPlayerMaxLevel());
			callableStatement.setObject(10, t.getEffectBeginTime());
			callableStatement.setObject(11, t.getEffectEndTime());
			callableStatement.setObject(12, t.getBindGold());
			
			if (callableStatement.executeUpdate() > 0) {
				ret = true;
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+callableStatement.toString()+"]");
			}
			
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (callableStatement != null) {
				try {
					callableStatement.close();
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
		return ret;
	}
	
	public List<GmMail> selectAll() {
		List<GmMail> list = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.prepareCall("select * from " + table);
			rs = statement.executeQuery();
			list = mapResultSet(rs);
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+statement.toString()+"]");
			}
			
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

}
