
package edu.stanford.smi.protegex.owl.swrl.ormap;

import java.util.*;

public interface ForeignKey extends Key
{
  Set<Table> getKeyedTables();
} // ForeignKey
