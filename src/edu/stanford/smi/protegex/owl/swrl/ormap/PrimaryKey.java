
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.Set;

public interface PrimaryKey extends Key
{
  Set<KeyColumn> getKeyColumns();
} // PrimaryKey
