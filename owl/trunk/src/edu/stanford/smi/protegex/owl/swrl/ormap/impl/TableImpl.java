
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.util.*;

public class TableImpl implements Table
{
  private Database database;
  private String schemaName, tableName;
  private Set<Column> columns;
  private PrimaryKey primaryKey;
  private Set<ForeignKey> foreignKeys;

  public TableImpl(Database database, String schemaName, String tableName, Set<Column> columns)
  {
    this.database = database;
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.columns = columns;
    primaryKey = null;
    foreignKeys = new HashSet<ForeignKey>();
  } // TableImpl

  public TableImpl(Database database, String schemaName, String tableName, Set<Column> columns, 
                   PrimaryKey primaryKey, Set<ForeignKey> foreignKeys)
  {
    this.database = database;
    this.tableName = tableName;
    this.schemaName = schemaName;
    this.columns = columns;
    this.primaryKey = primaryKey;
    this.foreignKeys = foreignKeys;
  } // TableImpl

  public boolean hasPrimaryKey() { return primaryKey != null; }
  public boolean hasForeignKeys() { return !foreignKeys.isEmpty(); }
  
  public PrimaryKey getPrimaryKey() { return primaryKey; }
  public Set<ForeignKey> getForeignKeys() { return foreignKeys; }
  public Database getDatabase() { return database; }
  public String getTableName() { return tableName; }
  public String getSchemaName() { return schemaName; }
  public Set<Column> getColumns() { return columns; }

  public void setPrimaryKey(PrimaryKey primaryKey) { this.primaryKey = primaryKey; }
  public void setForeignKeys(Set<ForeignKey> foreignKeys) { this.foreignKeys = foreignKeys; }
} // TableImpl
