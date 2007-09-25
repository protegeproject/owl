package edu.stanford.smi.protegex.owl.ui.components.multiresource;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A ListModel representing values of a subject-predicate pair.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class MultiResourceListModel extends AbstractListModel {

    private RDFProperty predicate;

    private RDFResource subject;

    private List values = new ArrayList();


    public MultiResourceListModel(RDFProperty predicate) {
        this.predicate = predicate;
    }


    public Object getElementAt(int index) {
        return values.get(index);
    }


    public RDFProperty getPredicate() {
        return predicate;
    }


    public RDFResource getResourceAt(int row) {
        return (RDFResource) getElementAt(row);
    }


    public int getRowOf(Object value) {
        return values.indexOf(value);
    }


    public int getSize() {
        return values.size();
    }


    public RDFResource getSubject() {
        return subject;
    }


    public boolean isEditable(int row) {
        TripleStoreModel tsm = subject.getOWLModel().getTripleStoreModel();
        Object object = getElementAt(row);
        return tsm.isEditableTriple(subject, predicate, object);
    }


    public boolean isRDFResource(int row) {
        return getElementAt(row) instanceof RDFResource;
    }


    public void setSubject(RDFResource subject) {
        this.subject = subject;
        updateValues();
    }


    public void updateValues() {
        fireIntervalRemoved(this, 0, values.size());
        values = new ArrayList(subject.getPropertyValues(predicate, true));
        Collection hasValues = subject.getHasValuesOnTypes(predicate);
        for (Iterator it = hasValues.iterator(); it.hasNext();) {
            Object value = it.next();
            if (value instanceof RDFResource) {
                if (!values.contains(value)) {
                    values.add(value);
                }
            }
        }
        fireIntervalAdded(this, 0, values.size());
    }
}
