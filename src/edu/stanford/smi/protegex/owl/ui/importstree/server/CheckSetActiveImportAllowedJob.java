package edu.stanford.smi.protegex.owl.ui.importstree.server;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;

public class CheckSetActiveImportAllowedJob extends ProtegeJob {
    private static final long serialVersionUID = 7234800355325524952L;
    
    private Boolean cachedResult = null;
    
    public static String ALLOW_MULTIUSER_SET_ACTIVE_ONTOLOGY = "allow.multiuser.client.set.active.ontology";

    public static boolean doCheck(OWLModel owlModel) {
        Project p = owlModel.getProject();
        if (!p.isMultiUserClient()) {
            return true;
        }
        if (!new CheckSetActiveImportAllowedJob(owlModel).execute()) {
            return false; 
        }
        return RemoteClientFrameStore.isOperationAllowed(owlModel, OwlMetaProjectConstants.SET_ACTIVE_IMPORT);
    }
    
    public CheckSetActiveImportAllowedJob(OWLModel owlModel) {
        super(owlModel);
    }
    
    @Override
    public Boolean run() throws ProtegeException {
        return ApplicationProperties.getBooleanProperty(ALLOW_MULTIUSER_SET_ACTIVE_ONTOLOGY, false);
    }
    
    @Override
    public Boolean execute() throws ProtegeException {
        return (Boolean) super.execute();
    }

}
