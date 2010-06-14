package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.util.ClosureAxiomFactory;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A ResourceAction that adds a closure axiom to the class.
 * This can be applied to existential that are direct pure superclass or part of an
 * equivalent intersection of an OWLNamedClass.
 * <p/>
 * For example:
 * hasParent some Mother
 * hasParent some Father
 * Adds:
 * hasParent all (Mother or Father)
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddClosureAxiomAction extends ResourceAction {

    public AddClosureAxiomAction() {
        super("Add closure axiom", OWLIcons.getAllRestrictionIcon());
    }


    public void actionPerformed(ActionEvent e) {
        OWLExistentialRestriction restriction = (OWLSomeValuesFrom) getResource();
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(getComponent());
        OWLNamedClass editedCls = ((ConditionsTable) getComponent()).getEditedCls();

        performAction(editedCls, restriction);

        if (tab != null) {
            tab.ensureClsSelected(editedCls, -1);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        if (component instanceof ConditionsTable) {
            OWLNamedClass editedCls = ((ConditionsTable) component).getEditedCls();
            return isSuitable(resource, editedCls);
        }
        else {
            return false;
        }
    }


    public static boolean isSuitable(edu.stanford.smi.protege.model.Frame frame, OWLNamedClass editedCls) {
        if (frame instanceof OWLSomeValuesFrom) {
            OWLSomeValuesFrom someRestriction = (OWLSomeValuesFrom) frame;
            if (editedCls.isEditable() &&
                    someRestriction.getOnProperty() instanceof OWLObjectProperty) {
                OWLAnonymousClass root = someRestriction.getExpressionRoot();
                if (root instanceof OWLIntersectionClass) {
                    RDFSClass subclass = root.getOwner();
                    if (subclass instanceof OWLNamedClass && root.isSubclassOf(subclass)) {
                        if (((OWLIntersectionClass) root).getOperands().contains(someRestriction)) {
                            return true;  // Part of an equivalent intersection
                        }
                    }
                }
                else if (someRestriction.getSubclassCount() == 1) {
                    RDFSClass subCls = someRestriction.getOwner();
                    if (subCls instanceof OWLNamedClass) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * @deprecated please use the more generic version
     */
    public static OWLAllValuesFrom performAction(OWLNamedClass namedClass, OWLSomeValuesFrom someValuesFrom) {
        return ClosureAxiomFactory.addClosureAxiom(namedClass, someValuesFrom);
    }


    public static OWLAllValuesFrom performAction(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {
        return ClosureAxiomFactory.addClosureAxiom(namedClass, restriction);
    }
}
