
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;

public class OWLObjectPropertyMapImpl extends OWLPropertyMapImpl implements OWLObjectPropertyMap
{
  private ForeignKey foreignKey;

  public OWLObjectPropertyMapImpl(OWLProperty owlProperty, PrimaryKey primaryKey, ForeignKey foreignKey)
  {
    super(owlProperty, primaryKey);
    this.foreignKey = foreignKey;
  } // OWLObjectPropertyMapImpl

  public ForeignKey getForeignKey() { return foreignKey; }
} // OWLObjectPropertyMap
