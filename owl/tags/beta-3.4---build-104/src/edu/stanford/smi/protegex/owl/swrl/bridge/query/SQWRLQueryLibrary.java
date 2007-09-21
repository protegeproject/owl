
package edu.stanford.smi.protegex.owl.swrl.bridge.query;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.ResultException;

public interface SQWRLQueryLibrary
{
  Result getSQWRLResult(String ruleName) throws ResultException;
} // SQWRLQueryLibrary
