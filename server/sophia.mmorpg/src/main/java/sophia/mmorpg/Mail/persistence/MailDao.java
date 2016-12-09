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
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.item.Item;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-10 下午3:30:54
 * @version 1.0
 */
public class MailDao extends ObjectDAO<Mail> {
	private static final Logger logger = Logger.getLogger(MailDao.class);

	private static final String table = "mail";

	private MailPersistenceObject persistence = MailPersistenceObject.getInstance();

	private MailDao() {
	}

	private static MailDao mailDao = new MailDao();

	public static MailDao getInstance() {
		return mailDao;
	}

	@Override
	protected String getInstertSql() {
		return null;
	}

	@Override
	protected AbstractSaveableObject getInsertData(Mail t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getUpdateSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getUpdateData(Mail t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDeleteSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getDeleteData(Mail t) {
		// TODO Auto-generated method stub
		return null;
	}

	// ==============================================================
	private List<Mail> mapResultSet(ResultSet rs) throws SQLException {
		List<Mail> mails = new ArrayList<Mail>();
		while (rs.next()) {
			Mail mail = new Mail();
			mail.setCoin(rs.getInt("coin"));
			mail.setContent(rs.getString("Content"));
			mail.setGold(rs.getInt("gold"));
			mail.setItem(rs.getString("item"));
			mail.setMailId(rs.getString("mailId"));
			mail.setPlayerId(rs.getString("playerId"));
			mail.setRead(rs.getBoolean("isRead"));
			mail.setRelateMailId(rs.getString("relateMailId"));
			mail.setTime(rs.getLong("TIME"));
			mail.setMailType(rs.getByte("mailType"));
			mail.setBindGold(rs.getInt("bindGold"));
			mail.setTitle(rs.getString("title"));
			byte[] itemData = rs.getBytes("itemIns");
			if (itemData != null) {
				Item itemObj = persistence.fromBytes(itemData);
				mail.setItemInstance(itemObj);
			}

			mails.add(mail);
		}
		return mails;
	}

	public boolean insertMail(Mail mail) {
		boolean ret = false;

		String sql = "insert into " + table
				+ "(playerId,mailId,Content,gold,coin,item,isRead,`TIME`,mailType,bindGold,relateMailId,title,itemIns) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			callableStatement.setObject(1, mail.getPlayerId());
			callableStatement.setObject(2, mail.getMailId());
			callableStatement.setObject(3, mail.getContent());
			callableStatement.setObject(4, mail.getGold());
			callableStatement.setObject(5, mail.getCoin());
			callableStatement.setObject(6, mail.getItem());
			callableStatement.setObject(7, mail.isRead());
			callableStatement.setObject(8, mail.getTime());
			callableStatement.setObject(9, mail.getMailType());
			callableStatement.setObject(10, mail.getBindGold());
			callableStatement.setObject(11, mail.getRelateMailId());
			callableStatement.setObject(12, mail.getTitle());
			Item item = mail.getItemInstance();
			if (item != null) {
				byte[] itemData = persistence.toBytes(item);
				callableStatement.setObject(13, itemData);
			} else {
				callableStatement.setObject(13, null);
			}

			if (callableStatement.executeUpdate() > 0) {
				ret = true;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + callableStatement.toString() + "]");
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

	public Mail getMail(String mailId) {
		Mail ret = null;

		String sql = "select * from " + table + " where mailId = '" + mailId + "'";

		Connection conn = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			rs = callableStatement.executeQuery();
			List<Mail> mails = mapResultSet(rs);
			if (mails.size() > 0) {
				ret = mails.get(0);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + callableStatement.toString() + "]");
			}

		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
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

	public List<Mail> getMailsByPlayerId(String playerId) {
		List<Mail> mails = null;

		String sql = "select * from " + table + " where playerId = '" + playerId + "'  and mailType !=" + Mail.CustomToGMType + " order by `TIME` desc limit 0,200";

		Connection conn = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			rs = callableStatement.executeQuery();
			mails = mapResultSet(rs);

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + callableStatement.toString() + "]");
			}

		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
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

		return mails;
	}

	// public boolean deleteMail(String mailId) {
	// String sql = "UPDATE " + table + " SET isDelete = TRUE where mailId = " +
	// mailId;
	// DbResource resource = createDbResource(sql);
	// CallableStatement callableStatement = resource.getCallableStatement();
	// try {
	// if (callableStatement.executeUpdate() > 0) {
	// return true;
	// }
	// } catch (Exception e) {
	// logger.error("", e);
	// } finally {
	// closeResources(resource);
	// }
	// return false;
	// }

	public boolean readMail(String mailId) {
		boolean ret = false;

		String sql = "UPDATE " + table + " SET isRead = TRUE where mailId = '" + mailId + "'";

		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			if (callableStatement.executeUpdate() > 0) {
				ret = true;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + callableStatement.toString() + "]");
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

	public List<Mail> selectCancelOrNormalAuctionMail() {
		List<Mail> mails = null;

		String sql = "select * from " + table + " where mailType =" + Mail.auctionCancel + " or mailType =" + Mail.auctionNormal + " order by `TIME` desc limit 0,10000";

		Connection conn = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			rs = callableStatement.executeQuery();
			mails = mapResultSet(rs);

			if (logger.isDebugEnabled()) {
				logger.debug("execute sql=[" + callableStatement.toString() + "]");
			}

		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
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

		return mails;
	}

}
