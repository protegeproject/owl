
package edu.stanford.smi.protegex.owl.swrl.bridge;

/*
** Interface representing a SWRL data valued property atom
*/
public interface DatavaluedPropertyAtom extends Atom
{
  String getPropertyName();
  AtomArgument getArgument1();
  AtomArgument getArgument2();
} // DatavaluedPropertyAtom
