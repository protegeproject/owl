package edu.stanford.smi.protegex.owl.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLExistentialRestriction;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;

/**
 * A utility for creating and detecting closure axioms.
 * <p/>
 * Closure axioms can be applied to existential restrictions that are direct
 * pure superclass or part of an equivalent intersection of an OWLNamedClass.
 * <p/>
 * For example:
 * hasParent some Mother
 * hasParent some Father
 * Can be closed by adding:
 * hasParent all (Mother or Father)
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClosureAxiomFactory {

    public static OWLAllValuesFrom addClosureAxiom(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {

        RDFProperty property = (OWLObjectProperty) restriction.getOnProperty();
        OWLModel owlModel = restriction.getOWLModel();
        OWLAnonymousClass root = restriction.getExpressionRoot();
        OWLNamedClass owner = (OWLNamedClass) root.getOwner();
        List existentials = getFillerStrings(owner, property);
        RDFSClass filler = getFiller(existentials, owlModel);
        OWLAllValuesFrom allValuesFrom = owlModel.createOWLAllValuesFrom(property, filler);

        if (root instanceof OWLIntersectionClass) {
            if (owner.equals(namedClass)) {
                OWLIntersectionClass cl = (OWLIntersectionClass) root.createClone();
                cl.addOperand(allValuesFrom);
                root.delete();
                namedClass.addEquivalentClass(cl);
            }
            else {
                namedClass.addSuperclass(allValuesFrom);
            }
            return allValuesFrom;
        }
        else {
            if (owner.hasEquivalentClass(restriction) && owner.equals(namedClass)) {
                OWLIntersectionClass intersectionCls = owlModel.createOWLIntersectionClass();
                intersectionCls.addOperand(restriction.createClone());
                intersectionCls.addOperand(allValuesFrom);
                restriction.delete();
                namedClass.addEquivalentClass(intersectionCls);
            }
            else {
                namedClass.addSuperclass(allValuesFrom);
            }
            return allValuesFrom;
        }
    }


    /**
     * Checks whether a closure axiom exists for a given class/property pair.
     * This is always true after <CODE>addClosureAxiom()</CODE> has been called.
     *
     * @param namedClass  the named class
     * @param restriction the restriction being (possibly) closed
     * @return an existing OWLAllValuesFrom or null if no matching restriction exists
     */
    public static OWLAllValuesFrom getClosureAxiom(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {
        Iterator candidates = getClosureAxiomCandidates(namedClass, restriction);
        if (candidates.hasNext()) {
            return (OWLAllValuesFrom) candidates.next();  // TODO: Wrong!
        }
        else {
            return null;
        }
    }


    private static Iterator getClosureAxiomCandidates(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {
        List candidates = new ArrayList();
        OWLAnonymousClass root = restriction.getExpressionRoot();
        if (root instanceof OWLIntersectionClass) {
            Iterator it = ((OWLIntersectionClass) root).listOperands();
            while (it.hasNext()) {
                RDFSClass operand = (RDFSClass) it.next();
                if (operand instanceof OWLAllValuesFrom) {
                    candidates.add(operand);
                }
            }
        }
        else {
            Iterator it = namedClass.getSuperclasses(true).iterator();
            while (it.hasNext()) {
                RDFSClass superclass = (RDFSClass) it.next();
                if (superclass instanceof OWLAllValuesFrom) {
                    OWLAllValuesFrom allValuesFrom = (OWLAllValuesFrom) superclass;
                    if (!allValuesFrom.getEquivalentClasses().contains(namedClass)) {
                        candidates.add(allValuesFrom);
                    }
                }
            }
        }
        return candidates.iterator();
    }


    private static RDFSClass getFiller(List existentials, OWLModel owlModel) {
        OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
        RDFSClass filler = null;
        if (existentials.size() == 1) {
            try {
                String expression = (String) existentials.iterator().next();
                filler = parser.parseClass(owlModel, expression);
            }
            catch (Exception ex) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        }
        else {
            OWLUnionClass unionCls = owlModel.createOWLUnionClass();
            for (Iterator it = existentials.iterator(); it.hasNext();) {
                String expression = (String) it.next();
                try {
                    RDFSClass fillerCls = parser.parseClass(owlModel, expression);
                    unionCls.addOperand(fillerCls);
                }
                catch (Exception ex) {
                  Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                }
            }
            filler = unionCls;
        }
        return filler;
    }


    private static List getFillerStrings(OWLNamedClass cls, RDFProperty property) {
        Set set = new HashSet();
        for (Iterator it = cls.getRestrictions(false).iterator(); it.hasNext();) {
            OWLRestriction restriction = (OWLRestriction) it.next();
            if (restriction instanceof OWLExistentialRestriction) {
                if (property.equals(restriction.getOnProperty())) {
                    String fillerText = restriction.getFillerText();
                    if (restriction instanceof OWLSomeValuesFrom) {
                        set.add(fillerText);
                    }
                    else if (restriction instanceof OWLHasValue) {
                        set.add("{" + fillerText + "}");
                    }
                }
            }
        }
        java.util.List results = new ArrayList(set);
        Collections.sort(results);
        return results;
    }
}
