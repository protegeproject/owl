
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.ResultException;

import java.util.*;

/**
 ** This inferface defines the methods that must be provided by a SQWRL query engine.
 **
 */
public interface SQWRLQueryEngine
{
  /**
   **  Get the results from a SQWRL query. Null is retured if there are no results or if the query subsystem is not activated.
   */
  Result getSQWRLResult(String ruleName) throws ResultException;
} // SQWRLQueryEngine
