/*
 * Microsoft JDBC Driver for SQL Server
 * 
 * Copyright(c) Microsoft Corporation All rights reserved.
 * 
 * This program is made available under the terms of the MIT License. See the LICENSE file in the project root for more information.
 */

package com.microsoft.sqlserver.testframework;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;

/*
 * Wrapper class for SQLServerConnection
 */
public class DBConnection extends AbstractParentWrapper {

    // TODO: add Isolation Level
    // TODO: add auto commit
    // TODO: add connection Savepoint and rollback
    // TODO: add additional connection properties
    // TODO: add DataSource support
    private SQLServerConnection connection = null;

    /**
     * establishes connection using the input
     * 
     * @param connectionString
     */
    public DBConnection(String connectionString) {
        super(null, null, "connection");
        getConnection(connectionString);
    }

    /**
     * establish connection
     * 
     * @param connectionString
     */
    void getConnection(String connectionString) {
        try {
            connection = PrepUtil.getConnection(connectionString);
            setInternal(connection);
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail(ex.getMessage());
        }
    }

    @Override
    void setInternal(Object internal) {
        this.internal = internal;
    }

    /**
     * 
     * @return Statement wrapper
     */
    public DBStatement createStatement() {
        try {
            DBStatement dbstatement = new DBStatement(this);
            return dbstatement.createStatement();
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        return null;
    }

    /**
     * 
     * @param type
     * @param concurrency
     * @return
     * @throws SQLException
     */
    public DBStatement createStatement(int type,
            int concurrency) throws SQLException {
        DBStatement dbstatement = new DBStatement(this);
        return dbstatement.createStatement(type, concurrency);

    }

    /**
     * 
     * @param rsType
     * @return
     * @throws SQLServerException
     */
    public DBStatement createStatement(DBResultSetTypes rsType) throws SQLServerException {
        DBStatement dbstatement = new DBStatement(this);
        return dbstatement.createStatement(rsType.resultsetCursor, rsType.resultSetConcurrency);
    }

    /**
     * 
     * @param query
     * @return
     * @throws SQLException
     */
    public DBPreparedStatement prepareStatement(String query) throws SQLException {
        DBPreparedStatement dbpstmt = new DBPreparedStatement(this);
        return dbpstmt.prepareStatement(query);
    }

    /**
     * close connection
     */
    public void close() {
        try {
            connection.close();
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * checks if the connection is closed.
     * 
     * @return true if connection is closed.
     * @throws SQLException
     */
    public boolean isClosed() {
        boolean current = false;
        try {
            current = connection.isClosed();
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        return current;
    }

    /**
     * Retrieves metaData
     * 
     * @return
     * @throws SQLException
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData product = connection.getMetaData();
        return product;
    }

    /**
     * 
     * @param con
     * @return
     * @throws SQLException
     */
    public static boolean isSqlAzure(Connection con) throws SQLException {
        boolean isSqlAzure = false;

        ResultSet rs = con.createStatement().executeQuery("SELECT CAST(SERVERPROPERTY('EngineEdition') as INT)");
        rs.next();
        int engineEdition = rs.getInt(1);
        rs.close();
        if (ENGINE_EDITION_FOR_SQL_AZURE == engineEdition) {
            isSqlAzure = true;
        }

        return isSqlAzure;
    }

}
