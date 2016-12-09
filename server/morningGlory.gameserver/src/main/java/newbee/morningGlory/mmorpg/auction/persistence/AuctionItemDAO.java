package newbee.morningGlory.mmorpg.auction.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.auction.AuctionItem;
import newbee.morningGlory.mmorpg.auction.AuctionMgr;
import newbee.morningGlory.mmorpg.auction.AuctionSystemComponent;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.item.Item;

import com.google.common.base.Strings;

public class AuctionItemDAO {

	private static Logger logger = Logger.getLogger(AuctionItemDAO.class);

	private static AuctionItemDAO instance = new AuctionItemDAO();

	private static final String table = "game_auction";

	private AuctionItemPersistenceObject persistenceObject = AuctionItemPersistenceObject.getInstance();

	public static AuctionItemDAO getInstance() {
		return instance;
	}

	protected String getSelectSql() {
		String selectSql = "select * from " + table;
		return selectSql;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + " (id,playerId,price,startTime,endTime,item) values(?,?,?,?,?,?)";
		return insertSql;
	}

	protected String getDeleteSql() {
		String deleteSql = "delete from " + table + " where id = ?";
		return deleteSql;
	}

	public void doSave(AuctionItem auctionItem) throws Exception {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, auctionItem.getId());
			ps.setString(2, auctionItem.getPlayerId());
			ps.setInt(3, auctionItem.getPrice());
			ps.setLong(4, auctionItem.getStartTime());
			ps.setLong(5, auctionItem.getEndTime());
			ps.setBytes(6, persistenceObject.toBytes(auctionItem.getItem()));
			ps.execute();

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

	public void doDelete(String id) throws SQLException {

		String sql = getDeleteSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.execute();
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

	public void loadData() {
		String sql = getSelectSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}
		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String id = rs.getString("id");
				String playerId = rs.getString("playerId");
				int price = rs.getInt("price");
				long startTime = rs.getLong("startTime");
				long endTime = rs.getLong("endTime");
				byte[] itemBytes = rs.getBytes("item");
				Item item = persistenceObject.fromBytes(itemBytes);
				AuctionItem createAuctionItem = auctionMgr.createAuctionItem(playerId, item, price, startTime, endTime);
				createAuctionItem.setId(id);
				auctionMgr.initAuctionItem(createAuctionItem);
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

	}


	

}
