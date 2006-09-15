package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SingleLiteralWidgetMetadata implements OWLWidgetMetadata {

    public int getSuitability(RDFSNamedClass cls, RDFProperty property) {
        if (isPreferredWidget(cls, property)) {
            return DEFAULT;
        }
        else if (isSuitableWidget(cls, property)) {
            return SUITABLE;
        }
        else {
            return NOT_SUITABLE;
        }
    }


    public boolean isPreferredWidget(RDFSNamedClass cls, RDFProperty property) {
        return OWLWidgetUtil.isRangelessDatatypeProperty(cls, property) &&
                OWLWidgetUtil.isFunctionalProperty(cls, property);
    }


    public boolean isSuitableWidget(RDFSNamedClass cls, RDFProperty property) {
        return OWLWidgetUtil.isDatatypeProperty(cls, property);
    }
}
