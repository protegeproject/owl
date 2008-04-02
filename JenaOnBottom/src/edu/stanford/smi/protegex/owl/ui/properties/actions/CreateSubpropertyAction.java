package edu.stanford.smi.protegex.owl.ui.properties.actions;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.properties.OWLSubpropertyPane;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateSubpropertyAction extends ResourceAction {

    public CreateSubpropertyAction() {
        super("Create subproperty", OWLIcons.getCreatePropertyIcon("SubProperty"));
    }


    public void actionPerformed(ActionEvent e) {
        OWLModel owlModel = getResource().getOWLModel();
        OWLSubpropertyPane pane = (OWLSubpropertyPane) getComponent();
        RDFProperty superProperty = (RDFProperty) getResource();
        performAction(owlModel, superProperty, pane);
    }


    public static void createInverseSlot(Slot slot, Collection superslots) {
        Collection superInverses = new ArrayList();
        Cls metaCls = null;
        Iterator i = superslots.iterator();
        while (i.hasNext()) {
            Slot superslot = (Slot) i.next();
            Slot inverse = superslot.getInverseSlot();
            if (inverse != null) {
                superInverses.add(inverse);
                if (metaCls == null) {
                    metaCls = inverse.getDirectType();
                }
            }
        }
        if (!superInverses.isEmpty()) {
            Slot inverse = slot.getKnowledgeBase().createSlot("inverse_of_" + slot.getName(), metaCls, superInverses, true);
            slot.setInverseSlot(inverse);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof OWLSubpropertyPane && resource instanceof RDFProperty;
    }


    public static void performAction(OWLModel owlModel, RDFProperty superProperty, OWLSubpropertyPane pane) {
        RDFProperty subproperty = null;
        try {
            owlModel.beginTransaction("Create subproperty of " + superProperty.getName());
            String name = owlModel.createNewResourceName(superProperty.getLocalName());
            subproperty = owlModel.createSubproperty(name, superProperty);
            final Set superproperties = Collections.singleton(superProperty);
            createInverseSlot(subproperty, superproperties);
            if (superProperty instanceof OWLProperty &&
                    subproperty instanceof OWLProperty &&
                    ((OWLProperty) superProperty).isInverseFunctional()) {
                ((OWLProperty) subproperty).setInverseFunctional(true);
            }
        }
        catch (Exception ex) {
            OWLUI.handleError(owlModel, ex);
        }
        finally {
            owlModel.endTransaction();
        }
        pane.extendSelection(subproperty);
    }
}
