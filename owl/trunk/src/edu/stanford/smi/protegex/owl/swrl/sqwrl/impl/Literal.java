
package edu.stanford.smi.protegex.owl.swrl.sqwrl.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.OWLDatatypeValueImpl;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.ResultValue;

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
  public Literal(XSDType value) { super(value); }
} // Literal
