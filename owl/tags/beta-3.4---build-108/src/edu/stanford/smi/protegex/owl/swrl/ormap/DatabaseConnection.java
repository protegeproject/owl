
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.*;
import java.sql.*;

public interface DatabaseConnection
{
  String getUserID();
  String getPassword();
  String getSchemaName();
  Database getDatabase();
  Set<Table> getTables();
  ResultSet executeQuery(String query) throws SQLException;
  void close() throws SQLException;
} // DatabaseConnection

