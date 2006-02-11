package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

import java.util.Collection;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLDatatypePropertySubpropertyRoot extends OWLPropertySubpropertyRoot {

	public OWLDatatypePropertySubpropertyRoot(OWLModel owlModel) {
		super(owlModel, getTopLevelProperties(owlModel));
	}


	protected boolean isSuitable(RDFProperty rdfProperty) {
		boolean suitable = rdfProperty instanceof OWLDatatypeProperty && !rdfProperty.isAnnotationProperty();
		return suitable;
	}


	public static Collection getTopLevelProperties(OWLModel owlModel) {
		Collection properties = owlModel.getUserDefinedOWLDatatypeProperties();
		for(Iterator it = properties.iterator(); it.hasNext(); ) {
			OWLDatatypeProperty curProp = (OWLDatatypeProperty) it.next();
			if(curProp.getSuperpropertyCount() > 0 || curProp.isSystem() || curProp.isAnnotationProperty()) {
				it.remove();
			}
		}
		return properties;
	}


}

