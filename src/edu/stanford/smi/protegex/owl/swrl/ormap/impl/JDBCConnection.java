
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.sql.*;
import java.util.*;

public class JDBCConnection
{
  private Connection connection;
  private Statement queryStmt;
  private DatabaseMetaData dbmd;

  public JDBCConnection(String jdbcConnectionString, String id, String password) throws SQLException
  {
    connection = DriverManager.getConnection(jdbcConnectionString, id, password);

    queryStmt = connection.createStatement();

    dbmd = connection.getMetaData();
  } // JDBCConnection

  public static String getConnectionString(String jdbcDriverName, String serverName, String databaseName, int portNumber) throws SQLException
  {
    String url = "";

    if (jdbcDriverName.equals("SQLServerJDBCDriver2000")) {
      url = new String("jdbc:microsoft:sqlserver://" + serverName + ":" + portNumber + ";databaseName=" + databaseName + ";");
    } else if (jdbcDriverName.equals("SQLServerJDBCDriver2005")) {
      url = new String("jdbc:sqlserver://" + serverName + ":" + portNumber + ";instanceName=" + databaseName + ";");
    } else if (jdbcDriverName.equals("SunJDBCDriver")) { 
      url = new String("jdbc:odbc:" + databaseName);
    } else if (jdbcDriverName.equals("OracleThin")) { 
      url = new String("jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + databaseName);
    } else if (jdbcDriverName.equals("MySQLJDBCDriver")) {
      url = new String("jdbc:mysql:" + serverName + ":" + portNumber + "/" + databaseName);
    } else {
      if (jdbcDriverName.equals("")) throw new SQLException("no JDBC driver specified");
      else throw new SQLException("unknown JDBC driver '" + jdbcDriverName + "'");
    } // if

    return url;
  } // getConnectionString

  public void loadDrivers(String jdbcDriverName) throws SQLException
  {
    if (jdbcDriverName.equals("SQLServerJDBCDriver2000")) {
      try { Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
      } catch (Exception e) {
	throw new SQLException("failed to load Microsoft SQL Server 2000 JDBC driver");
      } // try
    } else if (jdbcDriverName.equals("SQLServerJDBCDriver2005")) {
      try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      } catch (Exception e) {
	throw new SQLException("failed to load Microsoft SQL Server 2005 JDBC driver");
      } // try
    } else if (jdbcDriverName.equals("SunJDBCDriver")) { 
      try { Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      } catch (Exception e) {
        throw new SQLException("failed to load Sun JDBC driver");
      } // try
    } else if (jdbcDriverName.equals("OracleThin")) { 
      try { Class.forName("oracle.jdbc.driver.OracleDriver");
      } catch (Exception e) {
      throw new SQLException("failed to load Oracle JDBC driver");
      } // try
    } else if (jdbcDriverName.equals("MySQLJDBCDriver")) {
      try {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
      } catch (Exception e) {
	throw new SQLException("failed to load MySQL JDBC driver");
      } // try
    } else {
      if (jdbcDriverName.equals("")) throw new SQLException("no JDBC driver specified");
      else throw new SQLException("unknown JDBC driver '" + jdbcDriverName + "'");
    } // if
  } // getConnectionString

  public synchronized ResultSet executeQuery(String query) throws SQLException
  {
    ResultSet rs = null;

    if (queryStmt == null) queryStmt = connection.createStatement();
    
    rs = queryStmt.executeQuery(query);

    return rs;
  } // executeQuery

  public synchronized int executeUpdate(String command) throws SQLException
  {
    int result;

    if (queryStmt == null) queryStmt = connection.createStatement();
    
    result = queryStmt.executeUpdate(command);

    return result;
  } // executeUpdate

  public synchronized int getMaxColumnNameLength() throws SQLException
  {
    return dbmd.getMaxColumnNameLength();
  } // getMaxColumnNameLenght

  public synchronized int getMaxCatalogNameLength() throws SQLException
  {
    return dbmd.getMaxCatalogNameLength();
  } // getMaxCatalogNameLength

  public synchronized int getMaxCharLiteralLength() throws SQLException
  {
    return dbmd.getMaxCharLiteralLength();
  } // getMaxCharLiteralLength

  public synchronized String getDatabaseProductName() throws SQLException
  {
    return dbmd.getDatabaseProductName();
  } // getMaxCharLiteralLength

  public synchronized int getMaxRowSize() throws SQLException
  {
    return dbmd.getMaxRowSize();
  } // getMaxRowSize

  public synchronized int getMaxTableNameLength() throws SQLException
  {
    return dbmd.getMaxTableNameLength();
  } // getMaxRowSize

  public synchronized int getMaxSchemaNameLength() throws SQLException
  {
    return dbmd.getMaxSchemaNameLength();
  } // getMaxSchemaNameLength

  public synchronized String getUserName() throws SQLException
  {
    return dbmd.getUserName();
  } // getUserName

  public synchronized Set<String> getCatalogs() throws SQLException
  {
    Set<String> catalogs = new HashSet<String>();
    ResultSet rs = dbmd.getCatalogs();

    while (rs.next()) {
      String s = rs.getString(1);
      catalogs.add(s);
    } // while

    rs.close();
      
    return catalogs;
  } // getCatalogs

  public synchronized Set<String> getSchemas() throws SQLException
  {
    Set<String> schemas = new HashSet<String>();
    ResultSet rs = dbmd.getSchemas();

    while (rs.next()) {
      String s = rs.getString(1);
      schemas.add(s);
    } // while

    rs.close();

    return schemas;
  } // getSchemas

  public synchronized Set<String> getTableNames(String schemaName) throws SQLException
  {
    Set<String> tableNames = new HashSet<String>();
    ResultSet rs = dbmd.getTables(null, schemaName, null, null);

    while (rs.next()) {
      String s = rs.getString("TABLE_NAME");
      tableNames.add(s);
    } // while

    rs.close();

    return tableNames;
  } // getTableNames

  public synchronized void determinePrimaryKey(Table table) throws SQLException
  {
    Set<String> keyColumnNames = new HashSet<String>();
    String tableName = table.getTableName();
    String schemaName = table.getSchemaName();
    ResultSet rs = dbmd.getPrimaryKeys(null, schemaName, tableName);
    Set<Column> columns = new HashSet<Column>();
    PrimaryKey primaryKey;

    while (rs.next()) {
      String s = rs.getString("COLUMN_NAME");
      keyColumnNames.add(s);
    } // while

    rs.close();

    if (!keyColumnNames.isEmpty()) {

      for (String columnName : keyColumnNames) {
        int columnType = getColumnType(schemaName, tableName, columnName);
        Column column = new ColumnImpl(columnName, columnType);
        columns.add(column);
      } // for
      
      primaryKey = new PrimaryKeyImpl(table, columns);
      
      table.setPrimaryKey(primaryKey);
    } // if
  } // getPrimaryKeyColumnNames

  public synchronized void determineForeignKeys(Table table) throws SQLException
  {
    Set<String> keyColumnNames = new HashSet<String>();
    String tableName = table.getTableName();
    String schemaName = table.getSchemaName();
    ResultSet rs = dbmd.getImportedKeys(null, schemaName, tableName);
    int oldKeySequenceNumber = -1;
    Set<Column> keyColumns = new HashSet<Column>();
    Set<Table> keyedTables = new HashSet<Table>();
    Set<ForeignKey> foreignKeys = new HashSet<ForeignKey>();

    while (rs.next()) {
      String foreignKeyTableName = rs.getString("FKTABLE_NAME");
      String foreignKeyColumnName = rs.getString("FKCOLUMN_NAME");
      String primaryKeyTableName = rs.getString("PKTABLE_NAME");
      String primaryKeyColumnName = rs.getString("PKCOLUMN_NAME");
      int keySequenceNumber = rs.getInt("KEY_SEQ");

      if (keySequenceNumber != oldKeySequenceNumber) {
        ForeignKey foreignKey = new ForeignKeyImpl(table, keyColumns, keyedTables);
        foreignKeys.add(foreignKey);
        keyColumns = new HashSet<Column>();
        keyedTables = new HashSet<Table>();
        oldKeySequenceNumber = keySequenceNumber;
      } // if

      keyColumns.add(new ColumnImpl(primaryKeyColumnName, getColumnType(schemaName, tableName, primaryKeyColumnName)));
      keyedTables.add(new TableImpl(table.getDatabase(), schemaName, foreignKeyTableName, getColumns(schemaName, foreignKeyTableName)));
    } // while

    rs.close();

    if (!foreignKeys.isEmpty()) table.setForeignKeys(foreignKeys);
  } // determineForeignKeys

  public synchronized void closeConnection() throws SQLException
  {
    connection.close();
    connection = null;
    dbmd = null;
  } // closeConnection

  public synchronized void closeStatements() throws SQLException
  {
    if (queryStmt != null) queryStmt.close();

    queryStmt = null;
  } // closeStatements

  public synchronized String getColumnTypeName(String schemaName, String tableName, String columnName) throws SQLException
  {
    ResultSet rs;
    String columnType = "";

    rs = dbmd.getColumns(null, schemaName, tableName, columnName);
    rs.next();

    if (rs.getString("COLUMN_NAME").equalsIgnoreCase(columnName)) columnType = rs.getString("TYPE_NAME");

    return columnType;
  } // getColumnTypeName

  public synchronized int getColumnType(String schemaName, String tableName, String columnName) throws SQLException
  {
    ResultSet rs = dbmd.getColumns(null, schemaName, tableName, columnName);
    int columnType = -1;

    rs.next();
    if (rs.getString("COLUMN_NAME").equalsIgnoreCase(columnName)) columnType = rs.getInt("DATA_TYPE");

    return columnType;
  } // getColumnType

  public synchronized Set<String> getColumnTypeNames(String schemaName, String tableName) throws SQLException
  {
    ResultSetMetaData rsmd;
    Set<String> columnTypeNames = new HashSet<String>();
    ResultSet rs = dbmd.getColumns(null, schemaName, tableName, null);
    int numberOfColumns, i = 0;

    rsmd = rs.getMetaData();
    numberOfColumns = rsmd.getColumnCount();

    while (rs.next()) columnTypeNames.add(rsmd.getColumnTypeName(i++));

    rs.close();

    return columnTypeNames;
  } // getColumnTypesNames

  public synchronized Set<Column> getColumns(String schemaName, String tableName) throws SQLException
  {
    Set<Column> result = new HashSet<Column>();
    
    for (String columnName : getColumnNames(schemaName, tableName)) {
      Column column = new ColumnImpl(columnName, getColumnType(schemaName, tableName, columnName));
      result.add(column);
    } // for

    return result;
  } // getColumns

  public synchronized Set<String> getColumnNames(String schemaName, String tableName) throws SQLException
  {
    ResultSet rs;    
    ResultSetMetaData rsmd;
    Set<String> columnNames = new HashSet<String>();
    String column_name;
    int numberOfColumns;

    rs = dbmd.getColumns(null, schemaName, tableName, null);
    rsmd = rs.getMetaData();
    numberOfColumns = rsmd.getColumnCount();

    while (rs.next()) columnNames.add(rs.getString("COLUMN_NAME"));

    rs.close();
      
    return columnNames;
  } // getColumnNames

} // JDBCConnection
