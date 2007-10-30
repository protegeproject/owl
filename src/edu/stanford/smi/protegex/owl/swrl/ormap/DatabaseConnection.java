
package edu.stanford.smi.protegex.owl.swrl.ormap;

import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import java.util.*;
import java.sql.*;

public interface DatabaseConnection
{
  String getUserID();
  String getPassword();
  String getSchemaName();
  Database getDatabase();
  Set<Table> getTables();

  void open() throws JDBCException, SQLException;
  ResultSet executeQuery(String query) throws JDBCException, SQLException;
  boolean isOpen();
  void close() throws JDBCException, SQLException;
} // DatabaseConnection

