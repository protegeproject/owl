
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.*;

public interface Database
{
  String getJDBCDriverName();
  String getServerName();
  String getDatabaseName();
  int getPortNumber();
  String getJDBCConnectionString();
} // Database
