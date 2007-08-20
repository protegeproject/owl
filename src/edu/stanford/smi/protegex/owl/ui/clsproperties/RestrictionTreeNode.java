package edu.stanford.smi.protegex.owl.ui.clsproperties;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class RestrictionTreeNode extends DefaultMutableTreeNode implements Disposable {


    public abstract void checkExpression(String text) throws Throwable;


    public void dispose() {
    }


    public abstract String getFillerText();


    public abstract Icon getIcon();


    public abstract RDFSClass getInheritedFromClass();


    public abstract char getOperator();


    public String getOperatorName() {
        Cls metaCls = getRestrictionMetaCls();
        /* TT - The type of a restriction is null, if the restriction was deleted. 
         * So, theoretically, this should not be the case. This condition was 
         * necessary to fix Mantis bug: 445.
         * http://www.co-ode.org/mantis/view.php?id=445 
         */         
        if (metaCls == null) {
        	return "?";
        }
        
        String name = metaCls.getName();
        if (OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION.equals(name)) {
            return "allValuesFrom";
        }
        else if (OWLNames.Cls.HAS_VALUE_RESTRICTION.equals(name)) {
            return "hasValue";
        }
        else if (OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION.equals(name)) {
            return "someValuesFrom";
        }
        else if (OWLNames.Cls.CARDINALITY_RESTRICTION.equals(name)) {
            return "cardinality";
        }
        else if (OWLNames.Cls.MAX_CARDINALITY_RESTRICTION.equals(name)) {
            return "maxCardinality";
        }
        else if (OWLNames.Cls.MIN_CARDINALITY_RESTRICTION.equals(name)) {
            return "minCardinality";
        }
        else {
            return "?";
        }
    }


    public PropertyTreeNode getParentNode() {
        return (PropertyTreeNode) getParent();
    }


    public abstract Cls getRestrictionMetaCls();


    public abstract boolean isInherited();
}
