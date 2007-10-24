
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.util.*;

public abstract class KeyImpl implements Key
{
  private Table table;
  private Set<Column> keyColumns;

  public KeyImpl(Table table, Set<Column> keyColumns)
  {
    this.table = table;
    this.keyColumns = keyColumns;
  } // KeyImpl

  public Table getTable() { return table; }
  public Set<Column> getKeyColumns() { return keyColumns; }
  public boolean isComposite() { return keyColumns.size() > 1; }
} // KeyImpl
