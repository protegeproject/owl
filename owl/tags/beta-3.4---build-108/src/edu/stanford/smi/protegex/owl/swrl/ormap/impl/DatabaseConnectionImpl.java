
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.util.*;
import java.sql.*;

public class DatabaseConnectionImpl implements DatabaseConnection
{
  private String userID, password, schemaName;
  private Database database;
  private Set<Table> tables;
  private JDBCConnection jdbcConnection;

  public DatabaseConnectionImpl(Database database, String schemaName, String userID, String password) throws SQLException
  {
    jdbcConnection = new JDBCConnection(database.getJDBCConnectionString(), userID, password);
    tables = getTables();
  } // DatabaseConnectionImpl

  public ResultSet executeQuery(String query) throws SQLException
  {
    return jdbcConnection.executeQuery(query);
  } // executeQuery

  public void close() throws SQLException
  {
    jdbcConnection.closeConnection();
  } // close

  public String getUserID() { return userID; }
  public String getPassword() { return password; }
  public String getSchemaName() { return schemaName; }
  public Database getDatabase() { return database; }
  public Set<Table> getTables() { return tables; }

  private Set<Table> getTables(JDBCConnection jdbcConnection, String schemaName) throws SQLException
  {
    Set<Table> result = new HashSet<Table>();
    PrimaryKey primaryKey = null;
    Set<ForeignKey> foreignKeys = new HashSet<ForeignKey>();

    for (String tableName : jdbcConnection.getTableNames(schemaName)) {
      Set<Column> columns = new HashSet<Column>();
      for (String columnName : jdbcConnection.getColumnNames(schemaName, tableName)) {
        int columnType = jdbcConnection.getColumnType(schemaName, tableName, columnName);
        columns.add(new ColumnImpl(columnName, columnType));
      } // for
      result.add(new TableImpl(database, schemaName, tableName, columns, primaryKey, foreignKeys));
    } // for
    
    return result;
  } // getTables

} // DatabaseConnection

