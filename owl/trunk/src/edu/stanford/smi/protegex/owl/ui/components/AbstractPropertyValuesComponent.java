package edu.stanford.smi.protegex.owl.ui.components;

import edu.stanford.smi.protege.util.StringUtilities;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditorManager;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractPropertyValuesComponent extends JComponent implements PropertyValuesComponent {

    private final RDFProperty predicate;

    private RDFResource subject;
    
    private String label;


    protected AbstractPropertyValuesComponent(RDFProperty predicate) {
    	this(predicate, null);
    }

    protected AbstractPropertyValuesComponent(RDFProperty predicate, String label) {
        this.predicate = predicate;
        setLayout(new BorderLayout());
        this.label = label;
    }
    

    protected PropertyValueEditor getEditor(Object value) {
        final RDFResource subject = getSubject();
        final RDFProperty predicate = getPredicate();
        return PropertyValueEditorManager.getEditor(subject, predicate, value);
    }


    protected String getLabel() {
    	if (label == null) {
	        RDFProperty property = getPredicate();
	        String text = property.getBrowserText();
	        if (getOWLModel().getProject().getPrettyPrintSlotWidgetLabels()) {
	            text = StringUtilities.symbolToLabel(text);
	        }
	        return text;
	    }
    	
    	return label;
    }


    protected Object getObject() {
        Collection objects = getObjects();
        if (objects.isEmpty()) {
            return null;
        }
        else {
            return objects.iterator().next();
        }
    }


    public Collection getObjects() {
        return getObjects(false);
    }


    public Collection getObjects(boolean includingSubproperties) {
        RDFResource subject = getSubject();
        if (subject != null) {
            return subject.getPropertyValues(getPredicate(), includingSubproperties);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    protected OWLModel getOWLModel() {
        return predicate.getOWLModel();
    }


    public RDFProperty getPredicate() {
        return predicate;
    }


    public RDFResource getSubject() {
        return subject;
    }


    /**
     * Gets the first rdf:type of the current subject.
     *
     * @return the type of the subject
     */
    public RDFSClass getSubjectType() {
        RDFResource subject = getSubject();
        if (subject != null) {
            return subject.getRDFType();
        }
        else {
            return null;
        }
    }


    protected boolean hasHasValueRestriction() {
        if (getSubject() != null && getPredicate() != null) {
            return !getSubject().getHasValuesOnTypes(getPredicate()).isEmpty();
        }
        else {
            return false;
        }
    }


    protected boolean hasOnlyEditableValues() {
        Collection values = getObjects();
        TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object value = it.next();
            if (!tsm.isEditableTriple(getSubject(), getPredicate(), value)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if the property has an owl:oneOf class as its range at the given type.
     *
     * @return true  if true, false otherwise :)
     */
    protected boolean isEnumerationProperty() {
        if (subject != null) {
            for (Iterator it = subject.getRDFTypes().iterator(); it.hasNext();) {
                RDFSClass type = (RDFSClass) it.next();
                if (type instanceof OWLNamedClass) {
                    OWLNamedClass namedClass = (OWLNamedClass) type;
                    RDFResource all = namedClass.getAllValuesFrom(predicate);
                    if (all instanceof OWLNamedClass) {
                        OWLNamedClass typeClass = (OWLNamedClass) all;
                        Iterator sit = typeClass.getSuperclasses(false).iterator();
                        while (sit.hasNext()) {
                            if (sit.next() instanceof OWLEnumeratedClass) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    public void setSubject(RDFResource subject) {
        this.subject = subject;
        repaint();
    }


    protected void showResource(RDFResource resource) {
        getOWLModel().getProject().show(resource);
    }
}
