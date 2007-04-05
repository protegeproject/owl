
package edu.stanford.smi.protegex.owl.swrl.bridge.query;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.ResultException;

public interface QueryLibrary
{
  Result getQueryResult(String ruleName) throws ResultException;
} // QueryLibrary
