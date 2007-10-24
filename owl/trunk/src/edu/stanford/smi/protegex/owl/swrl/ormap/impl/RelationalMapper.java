
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;
import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;
import java.sql.*;

public class RelationalMapper implements Mapper
{
  private Map<String, OWLClassMap> classMaps;
  private Map<String, OWLObjectPropertyMap> objectPropertyMaps;
  private Map<String, OWLDatatypePropertyMap> datatypePropertyMaps;
  private DatabaseConnection databaseConnection;

  public RelationalMapper(SQWRLQueryEngine queryEngine) throws MapperException
  {
    classMaps = new HashMap<String, OWLClassMap>();
    objectPropertyMaps = new HashMap<String, OWLObjectPropertyMap>();
    datatypePropertyMaps = new HashMap<String, OWLDatatypePropertyMap>();

    readMaps(queryEngine);
    createDatabaseConnection();
  } // RelationalMapper

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
    String primaryKeyColumnName = primaryKey.getKeyColumns().iterator().next().getColumnName(); // Will have checked for non composite key
    Database database = primaryKey.getTable().getDatabase();
    String tableName = primaryKey.getTable().getTableName();
    String columnName = primaryKey.getKeyColumns().iterator().next().getColumnName();
    Set<OWLIndividual> result = new HashSet<OWLIndividual>();
    ResultSet rs;

    if (!databaseConnection.getDatabase().equals(database)) throw new MapperException("invalid database: " + database);

    try { 
      rs = databaseConnection.executeQuery("SELECT " + primaryKeyColumnName + " FROM " + tableName);
      
      while (rs.next()) {
        String individualName = rs.getString(primaryKeyColumnName);
        result.add(BridgeFactory.createOWLIndividual(individualName));
      } // while
    } catch (SQLException e) {
      throw new MapperException("database error mapping class '" + className + "': " + e.getMessage());
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
    String subjectTableName = foreignKey.getTable().getTableName();
    String subjectPrimaryKeyColumnName = foreignKey.getTable().getPrimaryKey().getKeyColumns().iterator().next().getColumnName();
    String subjectForeignKeyColumnName = foreignKey.getKeyColumns().iterator().next().getColumnName();
    String objectTableName = foreignKey.getKeyedTables().iterator().next().getTableName();
    String objectPrimaryKeyColumnName = foreignKey.getKeyedTables().iterator().next().getPrimaryKey().getKeyColumns().iterator().next().getColumnName();
    Database database = foreignKey.getTable().getDatabase();
    Set<OWLObjectPropertyAssertionAxiom> result = new HashSet<OWLObjectPropertyAssertionAxiom>();
    String query;
    ResultSet rs;

    if (!databaseConnection.getDatabase().equals(database)) throw new MapperException("invalid database: " + database);

    query = "SELECT S." + subjectPrimaryKeyColumnName + ", S." + subjectForeignKeyColumnName + ", O." + objectPrimaryKeyColumnName + " " +
            "FROM " + subjectTableName + " AS S, " + objectTableName + " AS O " +
            "WHERE " + subjectForeignKeyColumnName + " = " + objectPrimaryKeyColumnName;

    try {
      rs = databaseConnection.executeQuery(query);
      
      while (rs.next()) {
        OWLIndividual subject = BridgeFactory.createOWLIndividual(rs.getString(subjectPrimaryKeyColumnName));
        OWLIndividual object = BridgeFactory.createOWLIndividual(rs.getString(objectPrimaryKeyColumnName));
        OWLObjectPropertyAssertionAxiom axiom = BridgeFactory.createOWLObjectPropertyAssertionAxiom(subject, owlProperty, object);
        result.add(axiom);
      } // while
    } catch (SQLException e) {
      throw new MapperException("database error mapping object property '" + propertyName + "': " + e.getMessage());
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

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty) throws MapperException
  {
    String propertyName = owlProperty.getPropertyName();
    OWLDatatypePropertyMap propertyMap = getOWLDatatypePropertyMap(propertyName);
    PrimaryKey primaryKey = propertyMap.getPrimaryKey();
    String valueColumnName = propertyMap.getValueColumn().getColumnName();
    String subjectPrimaryKeyColumnName = primaryKey.getKeyColumns().iterator().next().getColumnName();
    String subjectTableName = primaryKey.getTable().getTableName();
    Database database = primaryKey.getTable().getDatabase();
    Set<OWLDatatypePropertyAssertionAxiom> result = new HashSet<OWLDatatypePropertyAssertionAxiom>();
    String query;
    ResultSet rs;

    if (!databaseConnection.getDatabase().equals(database)) throw new MapperException("invalid database: " + database);

    query = "SELECT S." + subjectPrimaryKeyColumnName + ", " + valueColumnName + " FROM " + subjectTableName;

    try {
      rs = databaseConnection.executeQuery(query);
      
      while (rs.next()) {
        OWLIndividual subject = BridgeFactory.createOWLIndividual(rs.getString(subjectPrimaryKeyColumnName));
        OWLDatatypeValue value = BridgeFactory.createOWLDatatypeValue(rs.getString(valueColumnName)); // TODO: string only
        OWLDatatypePropertyAssertionAxiom axiom = BridgeFactory.createOWLDatatypePropertyAssertionAxiom(subject, owlProperty, value);
        result.add(axiom);
      } // while
    } catch (SQLException e) {
      throw new MapperException("database error mapping datatype property '" + propertyName + "': " + e.getMessage());
    } // try

    return result;
  } // mapOWLDatatypeProperty

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subject) throws MapperException
  {
    throw new MapperException("not implemented");
  } // mapOWLDatatypeProperty

  public Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subject, OWLDatatypeValue value) 
    throws MapperException
  {
    throw new MapperException("not implemented");
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
    } catch (InvalidQueryNameException e) {
    } catch (SQWRLException e) {
      throw new MapperException("error reading mapping information: " + e.getMessage());
    } // try
  } // readMaps

  private void readOWLClassMaps(SQWRLQueryEngine queryEngine) throws MapperException, SQWRLException
  {
    SQWRLResult result = queryEngine.getSQWRLResult("swrlor:OWLDatatypePropertyMap-Query");
    if (result != null) {
      while (result.hasNext()) {
        String propertyName = result.getPropertyValue("?swrlor:owlDatatypeProperty").getPropertyName();
        System.err.println("propertyName: " + propertyName);
        String schemaName  = result.getDatatypeValue("?swrlor:schemaName").getString();
        System.err.println("schemaName: " + schemaName);
        result.next();
      } // while
    } // if
    
  } // readOWLClassMaps

  private void readOWLObjectPropertyMaps(SQWRLQueryEngine queryEngine) throws MapperException, SQWRLException
  {
  } // readOWLObjectPropertyMaps

  private void readOWLDatatypePropertyMaps(SQWRLQueryEngine queryEngine) throws MapperException, SQWRLException
  {
  } // readOWLDatatypePropertyMaps

  private void createDatabaseConnection() throws MapperException
  {
  } // createDatabaseConnection

} // RelationalMapper


