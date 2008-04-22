
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.ddm.impl.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

public class MapperFactory
{
  public static Mapper createMapper(SQWRLQueryEngine queryEngine) throws MapperException
  {
    return new RelationalMapper(queryEngine);
  } // getMapper
} // MapperFactory
