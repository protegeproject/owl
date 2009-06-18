
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/*
** Class representing a SWRL data valued property atom
*/
public class DatavaluedPropertyAtomImpl extends AtomImpl implements DatavaluedPropertyAtom
{
  private String propertyName, prefixedPropertyName;
  private AtomArgument argument1, argument2;
  
  public DatavaluedPropertyAtomImpl(String propertyName, String prefixedPropertyName, AtomArgument argument1, AtomArgument argument2)
  {
    this.propertyName = propertyName;
    this.prefixedPropertyName = prefixedPropertyName;
    this.argument1 = argument1;
    this.argument2 = argument2;
  } // DatavaluedPropertyAtomImpl

  public DatavaluedPropertyAtomImpl(String propertyName, String prefixedPropertyName)
  {
    this.propertyName = propertyName;
    this.prefixedPropertyName = prefixedPropertyName;
    this.argument1 = null;
    this.argument2 = null;
  } // DatavaluedPropertyAtomImpl

  public void setArgument1(AtomArgument argument1) { this.argument1 = argument1; }
  public void setArgument2(AtomArgument argument2) { this.argument2 = argument2; }

  public String getPropertyName() { return propertyName; }  
  public String getPrefixedPropertyName() { return prefixedPropertyName; }  
  public AtomArgument getArgument1() { return argument1; }
  public AtomArgument getArgument2() { return argument2; }

  public String toString() 
  { 
    String result = "" + getPrefixedPropertyName() + "(" + getArgument1() + ", ";

    if (getArgument2() instanceof OWLDatatypeValue && ((OWLDatatypeValue)getArgument2()).isString())
      result += "\"" + getArgument2() + "\"";
    else result += "" + getArgument2();

    result += ")"; 

    return result;
  } // toString
} // DatavaluedPropertyAtomImpl
