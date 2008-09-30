package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A ResourceAction for named classes, to create a sibling of the currently selected class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateSiblingClassAction extends ResourceAction {

    public final static String TEXT = "Create sibling class";


    public CreateSiblingClassAction() {
        super(TEXT, OWLIcons.getCreateIcon(OWLIcons.SIBLING_CLASS));
    }


    public void actionPerformed(ActionEvent e) {
        ClassTreePanel classTreePanel = (ClassTreePanel) getComponent();
        RDFSNamedClass sibling = (RDFSNamedClass) getResource();
        performAction(sibling, classTreePanel);
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof OWLSubclassPane &&
                resource instanceof RDFSNamedClass &&
                !(resource.equals(resource.getOWLModel().getOWLThingClass()));
    }


    public static void performAction(RDFSNamedClass sibling, ClassTreePanel classTreePanel) {
    	RDFSNamedClass cls = null;
        Collection parents = sibling.getNamedSuperclasses();
        if (!parents.isEmpty()) {
            OWLModel owlModel = sibling.getOWLModel();
            String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_CLASS_NAME);
            
            owlModel.beginTransaction("Create sibling of class " + sibling.getBrowserText(), name);
            
            try {                
                RDFSClass siblingType = sibling.getRDFType();
                if(siblingType == null) {
                    siblingType = sibling.getProtegeType();
                }
                /*
                 * The set is necessary to treat duplicates
                 * for the case that a class is defined in 
                 * multiple imports.
                 */
                HashSet parentsSet = new HashSet(parents);
                cls = owlModel.createRDFSNamedClass(name, parentsSet, siblingType);
                if (cls instanceof OWLNamedClass) {
                    for (Iterator it = parentsSet.iterator(); it.hasNext();) {
                        RDFSNamedClass s = (RDFSNamedClass) it.next();
                        ((OWLNamedClass) cls).addInferredSuperclass(s);
                    }
                }
                owlModel.commitTransaction();                
			} catch (Exception e) {
				owlModel.rollbackTransaction();
				OWLUI.handleError(owlModel, e);
			}
			
			if (cls != null) 
				classTreePanel.setSelectedClass(cls);			
			
        }
    }
}
