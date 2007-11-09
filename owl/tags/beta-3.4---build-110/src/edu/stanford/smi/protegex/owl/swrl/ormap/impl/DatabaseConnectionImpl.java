
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;
import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import java.util.*;
import java.sql.*;

public class DatabaseConnectionImpl implements DatabaseConnection
{
  private String jdbcConnectionString, userID, password, schemaName;
  private Database database;
  private JDBCConnection jdbcConnection = null;

  public DatabaseConnectionImpl(Database database, String schemaName, String userID, String password) throws SQLException
  {
    this.database = database;
    this.schemaName = schemaName;
    this.userID = userID;
    this.password = password;
    jdbcConnectionString = JDBCConnection.getConnectionString(database.getJDBCDriverName(), database.getServerName(), 
                                                              database.getDatabaseName(), database.getPortNumber());
  } // DatabaseConnectionImpl

  public void open() throws JDBCException, SQLException
  {
    if (isOpen()) jdbcConnection.closeConnection();

    jdbcConnection = new JDBCConnection(jdbcConnectionString, userID, password);
  } // open

  public boolean isOpen() { return jdbcConnection != null; }

  public Database getDatabase() { return database; }

  public ResultSet executeQuery(String query) throws SQLException
  {
    if (!isOpen()) open();

    return jdbcConnection.executeQuery(query);
  } // executeQuery

  public void close() throws SQLException
  {
    if (jdbcConnection != null) jdbcConnection.closeConnection();
  } // close

  public String getUserID() { return userID; }
  public String getPassword() { return password; }
  public String getSchemaName() { return schemaName; }

  public Set<String> getSchemaNames() throws SQLException
  {
    Set<String> result = new HashSet<String>();

    if (isOpen()) result = jdbcConnection.getSchemaNames();

    return result;
  } // getSchemaNames

  public Set<Table> getTables(String schemaName) throws SQLException
  {
    Set<Table> result = new HashSet<Table>();
    Set<ForeignKey> foreignKeys = new HashSet<ForeignKey>();
    Table table;

    if (isOpen()) {
      for (String tableName : jdbcConnection.getTableNames(schemaName)) {
        Set<Column> columns = new HashSet<Column>();
        Set<KeyColumn> primaryKeyColumns = new HashSet<KeyColumn>();
        Set<ForeignKeyColumn> foreignKeyColumns = new HashSet<ForeignKeyColumn>();

        table = ORFactory.createTable(database, schemaName, tableName, columns);

        for (String columnName : jdbcConnection.getColumnNames(schemaName, tableName)) {
          int columnType = jdbcConnection.getColumnType(schemaName, tableName, columnName);
          columns.add(ORFactory.createColumn(columnName, columnType));
        } // for
        
        for (String columnName : jdbcConnection.getPrimaryKeyColumnNames(schemaName, tableName)) {
          int columnType = jdbcConnection.getColumnType(schemaName, tableName, columnName);
          primaryKeyColumns.add(ORFactory.createKeyColumn(columnName, columnType));
        } // for

        if (!primaryKeyColumns.isEmpty()) {
          PrimaryKey primaryKey = ORFactory.createPrimaryKey(table, primaryKeyColumns);
          table.setPrimaryKey(primaryKey);
        } // if
        
        /* TODO: call JDBCConnection.getForeignKeys
        keyColumns.add(new ColumnImpl(primaryKeyColumnName, getColumnType(schemaName, tableName, primaryKeyColumnName)));
        keyedTables.add(new TableImpl(table.getDatabase(), schemaName, foreignKeyTableName, getColumns(schemaName, foreignKeyTableName)));
        ForeignKey foreignKey = new ForeignKeyImpl(table, keyColumns, keyedTables);
        foreignKeys.add(foreignKey);
        keyColumns = new HashSet<Column>();
        keyedTables = new HashSet<Table>();
        oldKeySequenceNumber = keySequenceNumber;
        if (!foreignKeys.isEmpty()) table.setForeignKeys(foreignKeys);
        */
        
        result.add(table);
      } // for
    } // if
    
    return result;
  } // getTables

  public Set<PrimaryKey> getPrimaryKeys(String schemaName) throws SQLException
  {
    Set<PrimaryKey> result = new HashSet<PrimaryKey>();

    if (isOpen()) {
      for (Table table : getTables(schemaName)) if (table.hasPrimaryKey()) result.add(table.getPrimaryKey());
    } // if
    return result;
  } // getPrimaryKeys

  public Set<ForeignKey> getForeignKeys(String schemaName) throws SQLException
  {
    Set<ForeignKey> result = new HashSet<ForeignKey>();

    if (isOpen()) {
      for (Table table : getTables(schemaName)) if (table.hasForeignKeys()) result.addAll(table.getForeignKeys());
    } // if
    return result;
  } // getForeignKeys

  public Set<Table> getTables() throws SQLException { return getTables(null); }
  public Set<PrimaryKey> getPrimaryKeys() throws SQLException { return getPrimaryKeys(null); }
  public Set<ForeignKey> getForeignKeys() throws SQLException { return getForeignKeys(null); }

} // DatabaseConnection

