package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTableModel;

import java.util.Collection;
import java.util.Collections;

/**
 * A TriplesTableModel that is restricted to only display annotation properties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AnnotationsTableModel extends TriplesTableModel {


    public AnnotationsTableModel() {
    }


    public AnnotationsTableModel(RDFResource subject) {
        super(subject);
    }


    protected Collection getRelevantProperties() {
        OWLModel owlModel = getOWLModel();
        return owlModel.getOWLAnnotationProperties();
    }


    protected boolean hasTypeColumn() {
        return false;
    }

    protected boolean isRelevantProperty(RDFProperty property) {
        return property.isAnnotationProperty();
    }


	public Collection getDefaultProperties() {
		OWLModel owlModel = getOWLModel();
		return Collections.singleton(owlModel.getRDFSCommentProperty());
	}
}
