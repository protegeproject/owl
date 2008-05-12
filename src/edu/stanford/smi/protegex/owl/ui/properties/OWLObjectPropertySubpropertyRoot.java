package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

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
public class OWLObjectPropertySubpropertyRoot extends OWLPropertySubpropertyRoot {

	public OWLObjectPropertySubpropertyRoot(OWLModel owlModel) {
		super(owlModel, getTopLevelObjectProperties(owlModel), OWLUI.getPropertiesTreeSortedOption());
	}

	public static Collection getTopLevelObjectProperties(OWLModel owlModel) {
		Collection properties = owlModel.getUserDefinedOWLObjectProperties();
		for(Iterator it = properties.iterator(); it.hasNext(); ) {
			
			Object o = it.next() ;
			
			//TT - happens with OWL Full ontologies
			if (o instanceof OWLObjectProperty) {
				OWLObjectProperty curProp = (OWLObjectProperty) o;
				if(curProp.getSuperpropertyCount() > 0 || curProp.isSystem() || curProp.isAnnotationProperty()) {
					it.remove();
				}				
			} else {
				Log.getLogger().warning("Expected to find owl:ObjectProperty. Found: " + o);
				it.remove();
			}
		}
		return properties;
	}


	public boolean isSuitable(RDFProperty rdfProperty) {
		boolean suitable = rdfProperty instanceof OWLObjectProperty &&
		       (rdfProperty.isAnnotationProperty() == false);
		return suitable;
	}

}

