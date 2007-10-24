
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

public class ColumnImpl implements Column
{
  private String columnName;
  private int columnType;

  public ColumnImpl(String columnName, int columnType)
  {
    this.columnName = columnName;
    this.columnType = columnType;
  } // ColumnImpl

  public String getColumnName() { return columnName; }
  public int getColumnType() { return columnType; }
} // ColumnImpl
