
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;
import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import java.util.*;
import java.sql.*;

public class DatabaseConnectionImpl implements DatabaseConnection
{
  private String jdbcConnectionString, userID, password, schemaName;
  private Database database;
  private Set<Table> tables = new HashSet<Table>();
  private JDBCConnection jdbcConnection = null;

  public DatabaseConnectionImpl(Database database, String schemaName, String userID, String password) 
    throws JDBCException
  {
    this.database = database;
    jdbcConnectionString = database.getJDBCConnectionString();
    this.schemaName = schemaName;
    this.userID = userID;
    this.password = password;
  } // DatabaseConnectionImpl

  public void open() throws JDBCException, SQLException
  {
    if (isOpen()) jdbcConnection.closeConnection();

    jdbcConnection = new JDBCConnection(jdbcConnectionString, userID, password);
    tables = getTables(jdbcConnection, schemaName);
  } // open

  public boolean isOpen() { return jdbcConnection != null; }

  public Database getDatabase() { return database; }

  public ResultSet executeQuery(String query) throws JDBCException, SQLException
  {
    if (!isOpen()) open();

    return jdbcConnection.executeQuery(query);
  } // executeQuery

  public void close() throws JDBCException, SQLException
  {
    if (jdbcConnection != null) jdbcConnection.closeConnection();
  } // close

  public String getUserID() { return userID; }
  public String getPassword() { return password; }
  public String getSchemaName() { return schemaName; }
  public Set<Table> getTables() { return tables; }

  private Set<Table> getTables(JDBCConnection jdbcConnection, String schemaName) throws JDBCException, SQLException
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

