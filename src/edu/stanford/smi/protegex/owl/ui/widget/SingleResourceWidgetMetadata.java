package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protegex.owl.model.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SingleResourceWidgetMetadata implements OWLWidgetMetadata {

    public int getSuitability(RDFSNamedClass cls, RDFProperty property) {
        if (cls instanceof OWLNamedClass) {
            OWLNamedClass namedClass = (OWLNamedClass) cls;
            if (property instanceof OWLObjectProperty ||
                    property.getRange() instanceof RDFSClass ||
                    namedClass.getAllValuesFrom(property) instanceof RDFSClass ||
                    property.isPureAnnotationProperty()) {
                if (cls.isFunctionalProperty(property)) {
                    return DEFAULT;
                }
                else {
                    return SUITABLE;
                }
            }
        }
        return NOT_SUITABLE;
    }
}
