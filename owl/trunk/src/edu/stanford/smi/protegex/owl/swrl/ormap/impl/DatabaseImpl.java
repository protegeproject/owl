
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;
import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import java.util.*;
import java.sql.*;

public class DatabaseImpl implements Database
{
  private String jdbcDriverName, serverName, databaseName, jdbcConnectionString;
  private int portNumber;

  public DatabaseImpl(String jdbcDriverName, String serverName, String databaseName, int portNumber) throws JDBCException, SQLException
  {
    this.jdbcDriverName = jdbcDriverName;
    this.serverName = serverName;
    this.databaseName = databaseName;
    this.portNumber = portNumber;
    jdbcConnectionString = JDBCConnection.getConnectionString(jdbcDriverName, serverName, databaseName, portNumber);
  } // DatabaseImpl

  public String getJDBCDriverName() { return jdbcDriverName; }
  public String getServerName() { return serverName; }
  public String getDatabaseName() { return databaseName; }
  public int getPortNumber() { return portNumber; }
  public String getJDBCConnectionString() { return jdbcConnectionString; }

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    DatabaseImpl impl = (DatabaseImpl)obj;
    return ((jdbcDriverName != null && impl.jdbcDriverName != null && jdbcDriverName.equals(impl.jdbcDriverName)) &&
            (serverName != null && impl.serverName != null && serverName.equals(impl.serverName)) &&
            (databaseName != null && impl.databaseName != null && databaseName.equals(impl.databaseName)) &&
            (jdbcConnectionString != null && impl.jdbcConnectionString != null && jdbcConnectionString.equals(impl.jdbcConnectionString)) &&
            (portNumber == impl.portNumber));
  } // equals

  public int hashCode()
  {
    int hash = 345;
    hash = hash + (null == jdbcDriverName ? 0 : jdbcDriverName.hashCode());
    hash = hash + (null == serverName ? 0 : serverName.hashCode());
    hash = hash + (null == databaseName ? 0 : databaseName.hashCode());
    hash = hash + (null == jdbcConnectionString ? 0 : jdbcConnectionString.hashCode());
    hash = hash + portNumber;
    return hash;
  } // hashCode

  public String toString() 
  {
    return jdbcConnectionString;
  } // toString

} // Database
