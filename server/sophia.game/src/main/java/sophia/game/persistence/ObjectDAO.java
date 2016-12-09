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
package sophia.game.persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.util.DebugUtil;

public abstract class ObjectDAO<T> {
	private static final Logger logger = Logger.getLogger(ObjectDAO.class);

	// DAO是单例
	protected ObjectDAO() {

	}

	protected final Connection getConnection() {
		return ConnectionManager.getConnection();
	}

	protected abstract String getInstertSql();

	protected abstract AbstractSaveableObject getInsertData(T t);

	protected abstract String getUpdateSql();

	protected abstract AbstractSaveableObject getUpdateData(T t);

	protected abstract String getDeleteSql();

	protected abstract AbstractSaveableObject getDeleteData(T t);

	public int insert(T t) {
		return modify(getInstertSql(), getInsertData(t));
	}

	public void batchInsert(Collection<AbstractSaveableObject> collection) throws Exception {
		modifyBatch(getInstertSql(), collection);
	}

	public int update(T t) {
		return modify(getUpdateSql(), getUpdateData(t));
	}

	public void batchUpdate(Collection<AbstractSaveableObject> collection) throws Exception {
		modifyBatch(getUpdateSql(), collection);
	}

	public int delete(T t) {
		return modify(getDeleteSql(), getDeleteData(t));
	}

	public void batchDelete(Collection<AbstractSaveableObject> collection) throws Exception {
		modifyBatch(getDeleteSql(), collection);
	}

	public int modify(String sql, AbstractSaveableObject object) {
		Connection conn = null;
		CallableStatement callableStatement = null;
		int count = 0;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			List<PersistenceParameter> persistenceParameters = object.getPersistenceParameters();
			for (PersistenceParameter parameter : persistenceParameters) {
				callableStatement.setObject(parameter.getName(), parameter.getValue());
			}
			count = callableStatement.executeUpdate();
			
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

		return count;
	}

	/**
	 * 只执行一次的修改语句
	 * 
	 * @param sql
	 * @return
	 */
	public int modifyOnce(String sql) {
		Connection conn = null;
		CallableStatement callableStatement = null;
		int count = 0;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			count = callableStatement.executeUpdate(sql);
			
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

		return count;
	}

	public void modifyBatch(String sql, Collection<AbstractSaveableObject> collection) throws Exception {
		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			for (AbstractSaveableObject object : collection) {
				List<PersistenceParameter> persistenceParameters = object.getPersistenceParameters();
				for (PersistenceParameter parameter : persistenceParameters) {
					callableStatement.setObject(parameter.getName(), parameter.getValue());
				}
				callableStatement.addBatch();
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+callableStatement.toString()+"]");
			}
			callableStatement.executeBatch();
			
		} catch (Exception e) {
			throw e;
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
	}
}
