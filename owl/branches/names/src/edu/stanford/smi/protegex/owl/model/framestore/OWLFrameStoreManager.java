package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFrameStoreManager extends FrameStoreManager {
    
    private OWLModel  owlModel;
    
    private OWLFrameStore owlFrameStore;
    private FacetUpdateFrameStore facetUpdateFrameStore;
    private DuplicateValuesFrameStore duplicateValuesFrameStore;
    
    private List<FrameStore> frameStores = new ArrayList<FrameStore>();

    public OWLFrameStoreManager(OWLModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
        initializeOwlFrameStores();
    }
    
    private void initializeOwlFrameStores() {
        addFrameStore(owlFrameStore = new OWLFrameStore((AbstractOWLModel) owlModel));
        addFrameStore(duplicateValuesFrameStore = new DuplicateValuesFrameStore());
        addFrameStore(new OWLDomainUpdateFrameStore(owlModel));
        addFrameStore(facetUpdateFrameStore = new FacetUpdateFrameStore(owlModel));
        addFrameStore(new RangeUpdateFrameStore(owlModel));
        addFrameStore(new OwlSubclassFrameStore(owlModel));
        addFrameStore(new TypeUpdateFrameStore(owlModel));  // this goes at the end so that the others see the swizzle.
        
        for (FrameStore fs : frameStores) {
            insertFrameStore(fs);
        }
    }
    
    /*
     * add the frame stores in reverse order
     */
    private void addFrameStore(FrameStore fs) {
        frameStores.add(0, fs);
    }
    

    @Override
    protected FrameStore create(Class clas) {
        if (clas == DeleteSimplificationFrameStore.class) {
            return new OWLDeleteSimplificationFrameStore();
        }
        else {
            return super.create(clas);
        }
    }
    
    public void setOwlFrameStoresEnabled(boolean enabled) {
        for (FrameStore fs : frameStores) {
            setEnabled(fs, enabled);
        }
    }
    
    /*
     * getters
     */
    
    public OWLFrameStore getOWLFrameStore() {
        return owlFrameStore;
    }
    
    public FacetUpdateFrameStore getFacetUpdateFrameStore() {
        return facetUpdateFrameStore;
    }

    /**
     * @return the duplicateValuesFrameStore
     */
    public DuplicateValuesFrameStore getDuplicateValuesFrameStore() {
        return duplicateValuesFrameStore;
    }
}
