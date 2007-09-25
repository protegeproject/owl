package edu.stanford.smi.protegex.owl.swrl.model;

import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;

import java.util.Set;

/**
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface SWRLImp extends SWRLIndividual {


    SWRLImp createClone();


    /**
     * Deletes this and all depending objects of the rule.
     */
    void deleteImp();


    Set getReferencedInstances();


    SWRLAtomList getBody();


    void setBody(SWRLAtomList swrlAtomList);


    SWRLAtomList getHead();


    void setHead(SWRLAtomList swrlAtomList);


    /**
     * Tries to parse the given text to create head and body
     * of this Imp.  This will replace the old content.
     * This method can be used to implement editing of existing
     * rules without deleting them.
     *
     * @param parsableText a SWRL expression
     */
    void setExpression(String parsableText) throws SWRLParseException;

} // SWRLImp

