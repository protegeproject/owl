package edu.stanford.smi.protegex.owl.ui.components.multiresource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;

import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.individuals.OWLGetOwnSlotValuesBrowserTextJob;

/**
 * A ListModel representing values of a subject-predicate pair.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class MultiResourceListModel extends AbstractListModel {

	private static final long serialVersionUID = -7197293909519481988L;

	private RDFProperty predicate;
    private RDFResource subject;
    private List<FrameWithBrowserText> values = new ArrayList<FrameWithBrowserText>();

    public MultiResourceListModel(RDFProperty predicate) {
        this.predicate = predicate;
    }

    public Object getElementAt(int index) {
    	return  values.get(index);
    }

    public RDFProperty getPredicate() {
        return predicate;
    }

    public RDFResource getResourceAt(int row) {
    	FrameWithBrowserText fbt = (FrameWithBrowserText) getElementAt(row);
        return (RDFResource) fbt.getFrame();
    }

    public int getRowOf(Object value) {
    	if (value instanceof RDFResource) {
    		return values.indexOf(new FrameWithBrowserText((RDFResource)value));
    	} else if (value instanceof FrameWithBrowserText) {
    		return values.indexOf(value);
    	}
        return -1;
    }

    public int getSize() {
        return values.size();
    }

    public RDFResource getSubject() {
        return subject;
    }


    public boolean isEditable(int row) {
        TripleStoreModel tsm = subject.getOWLModel().getTripleStoreModel();
        Object object = getResourceAt(row);
        return tsm.isEditableTriple(subject, predicate, object);
    }

    public boolean isRDFResource(int row) {
        return ((FrameWithBrowserText) getElementAt(row)).getFrame() instanceof RDFResource;
    }

    public void setSubject(RDFResource subject) {
        this.subject = subject;
        updateValues();
    }


    public void updateValues() {
        fireIntervalRemoved(this, 0, values.size());
        values = getValues();
        fireIntervalAdded(this, 0, values.size());
    }

    private List<FrameWithBrowserText> getValues() {
    	OWLGetOwnSlotValuesBrowserTextJob job = new OWLGetOwnSlotValuesBrowserTextJob(subject.getOWLModel(), subject, predicate, true);
    	Collection<FrameWithBrowserText> vals = job.execute();
    	return new ArrayList<FrameWithBrowserText>(vals);
    }

}
