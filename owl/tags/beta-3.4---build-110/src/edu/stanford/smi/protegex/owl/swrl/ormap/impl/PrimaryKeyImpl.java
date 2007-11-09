
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.util.*;

public class PrimaryKeyImpl extends KeyImpl implements PrimaryKey
{
  private Set<KeyColumn> keyColumns;

  public PrimaryKeyImpl(Table baseTable, Set<KeyColumn> keyColumns)
  {
    super(baseTable);
    this.keyColumns = keyColumns;
  } // PrimaryKeyImpl

  public boolean isComposite() { return keyColumns.size() > 1; }
  public Set<KeyColumn> getKeyColumns() { return keyColumns; }
} // PrimaryKeyImpl
