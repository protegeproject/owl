package edu.stanford.smi.protegex.owl.model.event;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceAdapter implements ResourceListener {

    /**
     * @deprecated
     */
    public final void directTypeAdded(InstanceEvent event) {
        if (event.getInstance() instanceof RDFResource && event.getCls() instanceof RDFSClass) {
            typeAdded((RDFResource) event.getInstance(), (RDFSClass) event.getCls());
        }
    }


    /**
     * @deprecated
     */
    public final void directTypeRemoved(InstanceEvent event) {
        if (event.getInstance() instanceof RDFResource && event.getCls() instanceof RDFSClass) {
            typeRemoved((RDFResource) event.getInstance(), (RDFSClass) event.getCls());
        }
    }


    public void typeAdded(RDFResource resource, RDFSClass type) {
        // Do nothing
    }


    public void typeRemoved(RDFResource resource, RDFSClass type) {
        // Do nothing
    }
}
