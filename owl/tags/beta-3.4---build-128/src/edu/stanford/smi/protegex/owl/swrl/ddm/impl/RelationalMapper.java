
package edu.stanford.smi.protegex.owl.swrl.ddm.impl;

import edu.stanford.smi.protegex.owl.swrl.ddm.*;
import edu.stanford.smi.protegex.owl.swrl.ddm.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;
import java.sql.*;

public class RelationalMapper implements Mapper, MapperGenerator
{
  private Map<String, OWLClassMap> classMaps;
  private Map<String, OWLObjectPropertyMap> objectPropertyMaps;
  private Map<String, OWLDatatypePropertyMap> datatypePropertyMaps;
  private Set<Database> databases;
  private Map<Database, DatabaseConnection> databaseConnections;

  public RelationalMapper(SQWRLQueryEngine queryEngine) throws MapperException
  {
    classMaps = new HashMap<String, OWLClassMap>();
    objectPropertyMaps = new HashMap<String, OWLObjectPropertyMap>();
    datatypePropertyMaps = new HashMap<String, OWLDatatypePropertyMap>();
    databases = new HashSet<Database>();
    databaseConnections = new HashMap<Database, DatabaseConnection>();

    readMaps(queryEngine);
  } // RelationalMapper

  public void open() throws MapperException
  {
    try {
      for (DatabaseConnection connection : databaseConnections.values()) 
        if (!connection.isOpen()) connection.open();
    } catch (JDBCException e) {
      throw new MapperException("JDBC error opening database connections: " + e.getMessage());
    } catch (SQLException e) {
      throw new MapperException("SQL error opening database connections: " + e.getMessage());
    } // try
  } // open

  public void close() throws MapperException
  {
    try {
      for (DatabaseConnection connection : databaseConnections.values()) 
        if (connection.isOpen()) connection.close();
    } catch (JDBCException e) {
      throw new MapperException("JDBC error closing database connections: " + e.getMessage());
    } catch (SQLException e) {
      throw new MapperException("SQL error closing database connections: " + e.getMessage());
    } // try
  } // close

  public boolean isMapped(OWLClass owlClass) { return classMaps.containsKey(owlClass.getClassName()); }
  public boolean isMapped(OWLProperty owlProperty) 
  { 
    return objectPropertyMaps.containsKey(owlProperty.getPropertyName()) || datatypePropertyMaps.containsKey(owlProperty.getPropertyName());
  } //isMapped

  public void addMap(OWLClassMap classMap)
  {
    String className = classMap.getOWLClass().getClassName();

    if (classMaps.containsKey(className)) classMaps.remove(className); // Remove old map, if any

    classMaps.put(className, classMap);
  } // addMap

  public void addMap(OWLObjectPropertyMap objectPropertyMap)
  {
    String propertyName = objectPropertyMap.getProperty().getPropertyName();

    if (objectPropertyMaps.containsKey(propertyName)) objectPropertyMaps.remove(propertyName); // Remove old map, if any

    objectPropertyMaps.put(propertyName, objectPropertyMap);
  } // addMap

  public void addMap(OWLDatatypePropertyMap datatypePropertyMap)
  {
    String propertyName = datatypePropertyMap.getProperty().getPropertyName();

    if (datatypePropertyMaps.containsKey(propertyName)) datatypePropertyMaps.remove(propertyName); // Remove old map, if any

    datatypePropertyMaps.put(propertyName, datatypePropertyMap);
  } // addMap

  public Set<OWLIndividual> mapOWLClass(OWLClass owlClass) throws MapperException
  {
    String className = owlClass.getClassName();
    OWLClassMap classMap = getOWLClassMap(className);
    PrimaryKey primaryKey = classMap.getPrimaryKey();
    String primaryKeyColumnName = primaryKey.getPrimaryKeyColumns().iterator().next().getColumnName(); // Will have checked for non composite key
    Database database = primaryKey.getBaseTable().getDatabase();
    String tableName = primaryKey.getBaseTable().getTableName();
    String columnName = primaryKey.getPrimaryKeyColumns().iterator().next().getColumnName();
    Set<OWLIndividual> result = new HashSet<OWLIndividual>();
    DatabaseConnection databaseConnection;
    ResultSet rs;

    databaseConnection = getDatabaseConnection(database);

    try { 
      rs = databaseConnection.executeQuery("SELECT " + primaryKeyColumnName + " FROM " + tableName);
      
      while (rs.next()) {
        String individualName = rs.getString(primaryKeyColumnName);
        result.add(OWLFactory.generateOWLIndividual(individualName));
      } // while
    } catch (JDBCException e) {
      throw new MapperException("JDBC error mapping class '" + className + "': " + e.getMessage());
    } catch (SQLException e) {
      throw new MapperException("SQL error mapping class '" + className + "': " + e.getMessage());
    } // try

    return result;
  } // mapOWLClass

  public Set<OWLIndividual> mapOWLClass(OWLClass owlClass, OWLIndividual owlIndividual) throws MapperException
  {
    throw new MapperException("not implemented");
  } // mapOWLClass

  public Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty) throws MapperException
  {
    String propertyName = owlProperty.getPropertyName();
    OWLObjectPropertyMap propertyMap = getOWLObjectPropertyMap(propertyName);
    ForeignKey foreignKey = propertyMap.getForeignKey();
    String subjectTableName = foreignKey.getBaseTable().getTableName();
    String subjectPrimaryKeyColumnName = foreignKey.getBaseTable().getPrimaryKey().getPrimaryKeyColumns().iterator().next().getColumnName();
    String subjectForeignKeyColumnName = foreignKey.getForeignKeyColumns().iterator().next().getColumnName();
    String objectTableName = foreignKey.getReferencedTable().getTableName();
    String objectPrimaryKeyColumnName = foreignKey.getReferencedTable().getPrimaryKey().getPrimaryKeyColumns().iterator().next().getColumnName();
    Database database = foreignKey.getBaseTable().getDatabase();
    Set<OWLObjectPropertyAssertionAxiom> result = new HashSet<OWLObjectPropertyAssertionAxiom>();
    DatabaseConnection databaseConnection;
    String query;
    ResultSet rs;

    databaseConnection = getDatabaseConnection(database);

    query = "SELECT S." + subjectPrimaryKeyColumnName + ", S." + subjectForeignKeyColumnName + ", O." + objectPrimaryKeyColumnName + " " +
            "FROM " + subjectTableName + " AS S, " + objectTableName + " AS O " +
            "WHERE " + subjectForeignKeyColumnName + " = " + objectPrimaryKeyColumnName;

    try {
      rs = databaseConnection.executeQuery(query);
      
      while (rs.next()) {
        OWLIndividual subject = OWLFactory.generateOWLIndividual(rs.getString(subjectPrimaryKeyColumnName));
        OWLIndividual object = OWLFactory.generateOWLIndividual(rs.getString(objectPrimaryKeyColumnName));
        OWLObjectPropertyAssertionAxiom axiom = OWLFactory.createOWLObjectPropertyAssertionAxiom(subject, owlProperty, object);
        result.add(axiom);
      } // while
    } catch (JDBCException e) {
      throw new MapperException("JDBC error mapping object property '" + propertyName + "': " + e.getMessage());
    } catch (SQLException e) {
      throw new MapperException("SQL error mapping object property '" + propertyName + "': " + e.getMessage());
    } // try

    return result;
  } // mapOWLObjectProperty

  public Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty, OWLIndividual subject) throws MapperException
  {
    throw new MapperException("not implemented");
  } // mapOWLObjectProperty

  public Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty, OWLIndividual subject, OWLIndividual object) 
    throws MapperException
  {
    throw new MapperException("not implemented");
  } // mapOWLbjectProperty

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subjectOWLIndividual,
                                                                       OWLDatatypeValue objectOWLDatatypeValue) 
    throws MapperException
  {
    String propertyName = owlProperty.getPropertyName();
    OWLDatatypePropertyMap propertyMap = getOWLDatatypePropertyMap(propertyName);
    PrimaryKey primaryKey = propertyMap.getPrimaryKey();
    String valueColumnName = propertyMap.getValueColumn().getColumnName();
    String subjectPrimaryKeyColumnName = primaryKey.getPrimaryKeyColumns().iterator().next().getColumnName();
    String subjectTableName = primaryKey.getBaseTable().getTableName();
    Database database = primaryKey.getBaseTable().getDatabase();
    Set<OWLDatatypePropertyAssertionAxiom> result = new HashSet<OWLDatatypePropertyAssertionAxiom>();
    boolean hasSubject = (subjectOWLIndividual != null);
    boolean hasObject = (objectOWLDatatypeValue != null);
    DatabaseConnection databaseConnection;
    String query;
    ResultSet rs;

    databaseConnection = getDatabaseConnection(database);

    query = "SELECT " + subjectPrimaryKeyColumnName + ", " + valueColumnName + " FROM " + subjectTableName;
    if (hasSubject || hasObject) {
      query += " WHERE ";
      if (hasSubject) query +=  subjectPrimaryKeyColumnName + " = " + subjectOWLIndividual.getIndividualName();
      if (hasObject) {
        if (hasSubject) query += " AND ";
        query += valueColumnName + " = ";
        if (objectOWLDatatypeValue.isString()) query += "\"" + objectOWLDatatypeValue.toString() + "\"";
        else query += objectOWLDatatypeValue.toString();
      } // if
    } // if

    try {
      rs = databaseConnection.executeQuery(query);
      
      while (rs.next()) {
        OWLIndividual subject = OWLFactory.generateOWLIndividual(rs.getString(subjectPrimaryKeyColumnName));
        OWLDatatypeValue value = OWLFactory.createOWLDatatypeValue(rs.getFloat(valueColumnName)); // TODO: float only
        OWLDatatypePropertyAssertionAxiom axiom = OWLFactory.createOWLDatatypePropertyAssertionAxiom(subject, owlProperty, value);
        result.add(axiom);
      } // while
      rs.close();
    } catch (JDBCException e) {
      throw new MapperException("JDBC error mapping datatype property '" + propertyName + "': " + e.getMessage());
    } catch (SQLException e) {
      throw new MapperException("SQL error mapping datatype property '" + propertyName + "': " + e.getMessage());
    } // try

    return result;
  } // mapOWLDatatypeProperty

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty,
                                                                       OWLIndividual subjectOWLIndividual)

    throws MapperException
  {
    return mapOWLDatatypeProperty(owlProperty, subjectOWLIndividual, null);
  } // mapOWLDatatypeProperty

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty,
                                                                       OWLDatatypeValue objectOWLDatatypeValue)

    throws MapperException
  {
    return mapOWLDatatypeProperty(owlProperty, null, objectOWLDatatypeValue);
  } // mapOWLDatatypeProperty

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty) throws MapperException
  {
    return mapOWLDatatypeProperty(owlProperty, null, null);
  } // mapOWLDatatypeProperty

  private OWLClassMap getOWLClassMap(String className) throws MapperException
  {
    if (!classMaps.containsKey(className)) throw new MapperException("attempt to map unmapped class '" + className + "'");

    return classMaps.get(className);
  } // getOWLClassMap

  private OWLObjectPropertyMap getOWLObjectPropertyMap(String propertyName) throws MapperException
  {
    if (!objectPropertyMaps.containsKey(propertyName)) throw new MapperException("attempt to map unmapped object property '" + propertyName + "'");

    return objectPropertyMaps.get(propertyName);
  } // getOWLObjectPropertyMap

  private OWLDatatypePropertyMap getOWLDatatypePropertyMap(String propertyName) throws MapperException
  {
    if (!datatypePropertyMaps.containsKey(propertyName)) throw new MapperException("attempt to map unmapped datatype property '" + propertyName + "'");

    return datatypePropertyMaps.get(propertyName);
  } // getOWLDatatypePropertyMap

  private void readMaps(SQWRLQueryEngine queryEngine) throws MapperException
  {
    try {
      queryEngine.runSQWRLQueries();
      
      readOWLClassMaps(queryEngine);
      readOWLObjectPropertyMaps(queryEngine);
      readOWLDatatypePropertyMaps(queryEngine);
    } catch (JDBCException e) {
      throw new MapperException("JDBC error reading mapping information: " + e.getMessage());
    } catch (SQLException e) {
      throw new MapperException("SQL error reading mapping information: " + e.getMessage());
    } catch (InvalidQueryNameException e) {
      // We have not imported http://swrl.stanford.edu/ontologies/built-ins/3.4/ddm.owl so there is nothing to map
    } catch (SQWRLException e) {
      throw new MapperException("SQWRL error reading mapping information: " + e.getMessage());
    } // try
  } // readMaps

  private void readOWLClassMaps(SQWRLQueryEngine queryEngine) throws MapperException, SQWRLException
  {    
  } // readOWLClassMaps

  private void readOWLObjectPropertyMaps(SQWRLQueryEngine queryEngine) throws MapperException, SQWRLException
  {
  } // readOWLObjectPropertyMaps

  private void readOWLDatatypePropertyMaps(SQWRLQueryEngine queryEngine) throws MapperException, SQWRLException, SQLException, JDBCException
  {
    Table subjectTable;
    Column valueColumn;
    PrimaryKeyColumn keyColumn;
    Set<PrimaryKeyColumn> idColumns;
    Set<Column> allColumns;
    Database database;
    PrimaryKey primaryKey;
    OWLDatatypePropertyMap datatypePropertyMap;
    OWLDatatypeProperty owlDatatypeProperty;

    SQWRLResult result = queryEngine.getSQWRLResult("ddm:OWLDatatypePropertyMap-Query");
    if (result != null) {
      while (result.hasNext()) {
        String propertyName = result.getPropertyValue("?ddm:owlDatatypeProperty").getPropertyName();
        String schemaName  = result.getDatatypeValue("?ddm:schemaName").getString();
        String tableName  = result.getDatatypeValue("?ddm:tableName").getString();
        String keyColumnName  = result.getDatatypeValue("?ddm:keyColumnName").getString();
        String valueColumnName  = result.getDatatypeValue("?ddm:valueColumnName").getString();
        String jdbcDriverName  = result.getDatatypeValue("?ddm:jdbcDriverName").getString();
        String databaseName  = result.getDatatypeValue("?ddm:databaseName").getString();
        String serverName  = result.getDatatypeValue("?ddm:serverName").getString();
        int portNumber  = result.getDatatypeValue("?ddm:portNumber").getInt();

        owlDatatypeProperty = OWLFactory.getOWLDatatypeProperty(propertyName);

        database = DDMFactory.createDatabase(jdbcDriverName, serverName, databaseName, portNumber);
        if (!databases.contains(database)) databases.add(database);

        allColumns = new HashSet<Column>();
        idColumns = new HashSet<PrimaryKeyColumn>();

        keyColumn = DDMFactory.createPrimaryKeyColumn(keyColumnName, 999);
        idColumns.add(keyColumn);
        allColumns.add(keyColumn);

        valueColumn = DDMFactory.createColumn(valueColumnName, 999);
        allColumns.add(keyColumn);

        subjectTable = DDMFactory.createTable(database, schemaName, tableName, allColumns);

        primaryKey = DDMFactory.createPrimaryKey(subjectTable, idColumns);

        datatypePropertyMap = DDMFactory.createOWLDatatypePropertyMap(owlDatatypeProperty, primaryKey, valueColumn);
        datatypePropertyMaps.put(propertyName, datatypePropertyMap);

        result.next();
      } // while
    } // if

  } // readOWLDatatypePropertyMaps

  private DatabaseConnection createDatabaseConnection(Database database) throws MapperException
  {
    DatabaseConnection connection = null;

    try {
      connection = new DatabaseConnectionImpl(database, "root", "w0rches");
    } catch (SQLException e) {
      throw new MapperException("error creating connection to database '" + database + "': " + e.getMessage());
    } // try
    
    return connection;
  } // createDatabaseConnection

  private DatabaseConnection getDatabaseConnection(Database database) throws MapperException
  {
    DatabaseConnection databaseConnection = null;

    if (!databaseConnections.containsKey(database)) {
      databaseConnection = createDatabaseConnection(database);
      databaseConnections.put(database, databaseConnection);
    } else databaseConnection = databaseConnections.get(database);

    try {
      if (!databaseConnection.isOpen()) databaseConnection.open();
    } catch (SQLException e) {
      throw new MapperException("error connecting to database '" + database + "': " + e.getMessage());
    } // try

    return databaseConnection;
  } // getDatabaseConnection

} // RelationalMapper


