
package edu.stanford.smi.protegex.owl.swrl.ormap;

import edu.stanford.smi.protegex.owl.swrl.ormap.impl.*;
import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

public class MapperFactory
{
  public static Mapper create(SQWRLQueryEngine queryEngine) throws MapperException
  {
    return new RelationalMapper(queryEngine);
  } // getMapper
} // MapperFactory
