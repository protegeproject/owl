
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import java.util.*;

public class ForeignKeyImpl extends KeyImpl implements ForeignKey
{
  private Table referencedTable;
  private Set<ForeignKeyColumn> foreignKeyColumns;

  public ForeignKeyImpl(Table baseTable, Set<ForeignKeyColumn> foreignKeyColumns, Table referencedTable)
  {
    super(baseTable);
    this.referencedTable = referencedTable;
    this.foreignKeyColumns = foreignKeyColumns;
  } // ForeignKeyImpl

  public Table getReferencedTable() { return referencedTable; }

  public boolean isComposite() { return foreignKeyColumns.size() > 1; }

  public Set<ForeignKeyColumn> getForeignKeyColumns() { return foreignKeyColumns; }
} // ForeignKeyImpl
