package edu.stanford.smi.protegex.owl.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.hp.hpl.jena.ontology.OntResource;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A base implementation of OWLAnonymousClass.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLAnonymousClass extends AbstractRDFSClass
        implements OWLAnonymousClass {


    public AbstractOWLAnonymousClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
        // setVisible(false);
    }
    
    public AbstractOWLAnonymousClass(KnowledgeBase kb, OntResource r) {
      super(kb, r);
    }


    public AbstractOWLAnonymousClass() {
    }


    public String getBrowserText() {
        return getOWLModel().getOWLClassDisplay().getDisplayText(this);
    }


    public Collection getDependingClasses() {
        return Collections.EMPTY_SET;
    }


    public OWLAnonymousClass getExpressionRoot() {
        final RDFProperty rdfFirstProperty = getOWLModel().getRDFFirstProperty();
        Collection refs = getReferences();
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof OWLAnonymousClass) {
                OWLAnonymousClass reverend = (OWLAnonymousClass) reference.getFrame();
                return reverend.getExpressionRoot();
            }
            else if (reference.getFrame() instanceof RDFList && rdfFirstProperty.equals(reference.getSlot())) {
                RDFList list = (RDFList) reference.getFrame();
                RDFList start = list.getStart();
                Set set = new HashSet();
                OWLUtil.getReferringLogicalClasses(start, set);
                if (set.size() > 0) {
                    OWLAnonymousClass reverend = (OWLAnonymousClass) set.iterator().next();
                    return reverend.getExpressionRoot();
                }
            }
        }
        return this;
    }


    public Icon getIcon() {
        // Suffix after "Default" in the class name is the icon name
        String className = getClass().getName();
        int index = className.lastIndexOf('.');
        String iconName = className.substring(index + 8);
        if (isEditable()) {
            return OWLIcons.getImageIcon(iconName);
        }
        else {
            return OWLIcons.getReadOnlyClsIcon(OWLIcons.getImageIcon(iconName));
        }
    }


    public ImageIcon getImageIcon() {
        // Suffix after "Default" in the class name is the icon name
        String className = getClass().getName();
        int index = className.lastIndexOf('.');
        String iconName = className.substring(index + 8);
        return OWLIcons.getImageIcon(iconName);
    }


    public OWLNamedClass getOwner() {
        Collection subclasses = getSubclasses(false);
        if (subclasses.isEmpty()) {
            OWLAnonymousClass root = getExpressionRoot();
            if (this.equals(root)) {
                return null;
            }
            else {
                return root.getOwner();
            }
        }
        else {
            return (OWLNamedClass) subclasses.iterator().next();
        }
    }


    public boolean isAnonymous() {
        return true;
    }


    public boolean isVisible() {
        return false;
    }
}
