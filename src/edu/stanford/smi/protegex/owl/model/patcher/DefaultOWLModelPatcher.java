package edu.stanford.smi.protegex.owl.model.patcher;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLModelPatcher implements OWLModelPatcher {

    private Map objectTypeMap = new HashMap();


    public DefaultOWLModelPatcher(OWLModel owlModel) {
        objectTypeMap.put(owlModel.getRDFSDomainProperty(), owlModel.getRDFSNamedClassClass());
        objectTypeMap.put(owlModel.getRDFSRangeProperty(), owlModel.getRDFSNamedClassClass());
        objectTypeMap.put(owlModel.getRDFSSubPropertyOfProperty(), owlModel.getRDFPropertyClass());
        objectTypeMap.put(owlModel.getRDFSSubClassOfProperty(), owlModel.getRDFSNamedClassClass());
        objectTypeMap.put(owlModel.getRDFTypeProperty(), owlModel.getRDFSNamedClassClass());
        objectTypeMap.put(owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY), owlModel.getRDFPropertyClass());
    }


    private RDFSNamedClass getRDFType(RDFResource resource) {
        KnowledgeBase kb = resource.getOWLModel();
        Iterator refs = kb.getReferences(resource, 1000).iterator();
        while (refs.hasNext()) {
            Reference ref = (Reference) refs.next();
            RDFSNamedClass type = (RDFSNamedClass) objectTypeMap.get(ref.getSlot());
            if (type != null) {
                return type;
            }
        }
        RDFProperty dummy = new DefaultRDFProperty(kb, resource.getFrameID());
        Iterator frames = resource.getOWLModel().getOWLFrameStore().getFramesWithAnyDirectOwnSlotValue(dummy).iterator();
        if (frames.hasNext()) {
            return resource.getOWLModel().getRDFPropertyClass();
        }
        return null;
    }


    public void patch(Iterator resources, String namespace) {
        OWLModel owlModel = null;
        TripleStore ts = null;
        while (resources.hasNext()) {
            RDFResource resource = (RDFResource) resources.next();
            if (namespace.equals(resource.getNamespace())) {
                if (owlModel == null) {
                    owlModel = resource.getOWLModel();
                    String uri = namespace;
                    if (uri.endsWith("#")) {
                        uri = uri.substring(0, uri.length() - 1);
                    }
                    ts = owlModel.getTripleStoreModel().createTripleStore(uri);
                }
                RDFSClass type = getRDFType(resource);
                if (type != null) {
                    ts.add(resource, owlModel.getRDFTypeProperty(), type);
                    resource.removeDirectType(owlModel.getRDFUntypedResourcesClass());
                    owlModel.getTripleStoreModel().setHomeTripleStore(resource, ts);
                }
            }
        }
    }
}
