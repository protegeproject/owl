
package edu.stanford.smi.protegex.owl.swrl.sqwrl.impl;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.io.*;

public class Literal extends OWLDatatypeValueImpl implements ResultValue, DatatypeValue
{
  public Literal() { super(); } 
  public Literal(String s) { super(s); } 
  public Literal(Number n) { super(n); }
  public Literal(boolean b) { super(b); }
  public Literal(int i) { super(i); }
  public Literal(long l) { super(l); }
  public Literal(float f) { super(f); }
  public Literal(double d) { super(d); }
  public Literal(PrimitiveXSDType value) { super(value); }
  public Literal(OWLModel owlModel, RDFSLiteral literal) throws DatatypeConversionException { super(owlModel, literal); }
} // Literal
