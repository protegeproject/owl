
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;

public class OWLDatatypePropertyMapImpl extends OWLPropertyMapImpl implements OWLDatatypePropertyMap
{
  private Column valueColumn;

  public OWLDatatypePropertyMapImpl(OWLProperty owlProperty, PrimaryKey primaryKey, Column valueColumn)
  {
    super(owlProperty, primaryKey);
    this.valueColumn = valueColumn;
  } // OWLDatatypePropertyMapImpl

  public Column getValueColumn() { return valueColumn; }
} // OWLDatatypePropertyMap
