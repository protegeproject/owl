
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.*;

public interface Database
{
  String getServerName();
  String getDatabaseName();
  String getUserID();
  String getPassword();
  String getJDBCConnectionString();
  String getODBCName();
  List<Table> getTables(); 
} // Database
