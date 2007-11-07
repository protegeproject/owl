
package edu.stanford.smi.protegex.owl.swrl.ormap;

import edu.stanford.smi.protegex.owl.swrl.ormap.impl.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;

import java.util.*;

public class ORFactory
{
  public static Database createDatabase(String jdbcDriverName, String serverName, String databaseName, int portNumber) 
  {
    return new DatabaseImpl(jdbcDriverName, serverName, databaseName, portNumber);
  } // createDatabase

  public static Table createTable(Database database, String schemaName, String tableName, Set<Column> columns)
  {
    return new TableImpl(database, schemaName, tableName, columns);
  } // createTable

  public static Table createTable(Database database, String schemaName, String tableName, Set<Column> columns, 
                           PrimaryKey primaryKey, Set<ForeignKey> foreignKeys)
  {
    return new TableImpl(database, schemaName, tableName, columns, primaryKey, foreignKeys);
  } // createTable

  public static Column createColumn(String columnName, int columnType) { return new ColumnImpl(columnName, columnType); }

  public static KeyColumn createKeyColumn(String columnName, int columnType) { return new KeyColumnImpl(columnName, columnType); }

  public static ForeignKeyColumn createForeignKeyColumn(String columnName, int columnType, String referencedColumnName) 
  { 
    return new ForeignKeyColumnImpl(columnName, columnType, referencedColumnName); 
  } // createForeignKeyColumn

  public static PrimaryKey createPrimaryKey(Table table, Set<KeyColumn> keyColumns) { return new PrimaryKeyImpl(table, keyColumns); }

  public static ForeignKey createForeignKey(Table baseTable, Set<ForeignKeyColumn> keyColumns, Table referencedTable) 
  { 
    return new ForeignKeyImpl(baseTable, keyColumns, referencedTable); 
  } // createForeignKey

  public static OWLClassMap createOWLClassMap(OWLClass owlClass, PrimaryKey primaryKey) { return new OWLClassMapImpl(owlClass, primaryKey); }

  public static OWLObjectPropertyMap createOWLObjectPropertyMap(OWLProperty owlProperty, PrimaryKey primaryKey, ForeignKey foreignKey)
  {
    return new OWLObjectPropertyMapImpl(owlProperty, primaryKey, foreignKey);
  } // OWLObjectPropertyMap
      
  public static OWLDatatypePropertyMap createOWLDatatypePropertyMap(OWLProperty owlProperty, PrimaryKey primaryKey, Column valueColumn)
  {
    return new OWLDatatypePropertyMapImpl(owlProperty, primaryKey, valueColumn);
  } // createOWLDatatypePropertyMap

} // ORFactory
