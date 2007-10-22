
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.util.*;

public class ForeignKeyImpl extends KeyImpl implements ForeignKey
{
  private Set<Table> keyedTables;

  public ForeignKeyImpl(Table table, Set<Column> columns, Set<Table> keyedTables)
  {
    super(table, columns);
    this.keyedTables = keyedTables;
  } // ForeignKeyImpl

  public Set<Table> getKeyedTables() { return keyedTables; }
} // ForeignKeyImpl
