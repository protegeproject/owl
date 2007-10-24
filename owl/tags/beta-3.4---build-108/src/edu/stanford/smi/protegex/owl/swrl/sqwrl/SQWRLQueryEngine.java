
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;

/**
 ** This inferface defines the methods that must be provided by a SQWRL query engine.
 **
 */
public interface SQWRLQueryEngine
{
  /**
   ** Run all the SQWRL queries in an ontology.
   */
  void runSQWRLQueries() throws SQWRLException;

  /**
   ** Get the results from a SQWRL query. Null is retured if there is no result.
   */
  SQWRLResult getSQWRLResult(String queryName) throws SQWRLException;
} // SQWRLQueryEngine
