
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.*;

public interface Table
{
  public Database getDatabase();
  public String getTableName();
  public List<Column> getColumns(); 
  public List<ForeignKey> getForeignKeys();
} // Table
