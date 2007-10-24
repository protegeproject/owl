
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.*;

public interface Key
{
  Table getTable();
  Set<Column> getKeyColumns();
  boolean isComposite();
} // Key
