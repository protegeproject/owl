package edu.stanford.smi.protegex.owl.model.event;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceAdapter implements ResourceListener {

    public void typeAdded(RDFResource resource, RDFSClass type, InstanceEvent event) {
    	typeAdded(resource, type);
    }
	
    public void typeAdded(RDFResource resource, RDFSClass type) {
        // Do nothing
    }


    public void typeRemoved(RDFResource resource, RDFSClass type, InstanceEvent event) {
    	typeRemoved(resource, type);
    }

    
    public void typeRemoved(RDFResource resource, RDFSClass type) {
        // Do nothing
    }

    
    /************* Deprecated methods **************/
	
    /**
     * @deprecated
     */
    public final void directTypeAdded(InstanceEvent event) {
        if (event.getInstance() instanceof RDFResource && event.getCls() instanceof RDFSClass) {
            typeAdded((RDFResource) event.getInstance(), (RDFSClass) event.getCls(), event);
        }
    }


    /**
     * @deprecated
     */
    public final void directTypeRemoved(InstanceEvent event) {
        if (event.getInstance() instanceof RDFResource && event.getCls() instanceof RDFSClass) {
            typeRemoved((RDFResource) event.getInstance(), (RDFSClass) event.getCls(), event);
        }
    }

}
