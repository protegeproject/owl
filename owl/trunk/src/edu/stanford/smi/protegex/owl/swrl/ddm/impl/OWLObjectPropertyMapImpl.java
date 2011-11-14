
package edu.stanford.smi.protegex.owl.swrl.ddm.impl;

import edu.stanford.smi.protegex.owl.swrl.ddm.*;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;


public class OWLObjectPropertyMapImpl extends OWLPropertyMapImpl implements OWLObjectPropertyMap
{
  private ForeignKey foreignKey;

  public OWLObjectPropertyMapImpl(OWLPropertyReference owlProperty, PrimaryKey primaryKey, ForeignKey foreignKey)
  {
    super(owlProperty, primaryKey);
    this.foreignKey = foreignKey;
  } // OWLObjectPropertyMapImpl

  public ForeignKey getForeignKey() { return foreignKey; }
} // OWLObjectPropertyMap
