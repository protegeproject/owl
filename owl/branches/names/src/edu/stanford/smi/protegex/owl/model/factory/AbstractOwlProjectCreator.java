package edu.stanford.smi.protegex.owl.model.factory;

import java.util.Collection;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.DefaultErrorHandler;
import edu.stanford.smi.protege.util.ErrorHandler;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

public abstract class AbstractOwlProjectCreator {
    private Class defaultClassView;
    private String profileURI;
    protected OWLKnowledgeBaseFactory factory;
    
    private ErrorHandler<Throwable> errorHandler = new DefaultErrorHandler<Throwable>();
    
    public AbstractOwlProjectCreator(OWLKnowledgeBaseFactory factory) {
        this.factory = factory;
    }
    

    public abstract Project create();
    
    protected void addViewSettings(PropertyList sources) {
        String typeName = null;
        if (defaultClassView == null) {
            typeName = SwitchClassDefinitionResourceDisplayPlugin.getDefaultClassView();
        }
        else {
            typeName = defaultClassView.getName();
        }
        SwitchClassDefinitionResourceDisplayPlugin.setClassesView(sources, typeName);
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeName);
        if (profileURI != null) {
            ProfilesManager.setProfile(sources, profileURI);
        }
    }
    
    /*
     * This is non-optimal but...
     */
    @SuppressWarnings("unchecked")
    protected void handleErrors(Collection errors) {
        for (Object  o : errors)  {
            if (o instanceof Throwable) {
                try {
                    errorHandler.error((Throwable) o);
                }
                catch (Throwable t) {
                    new RuntimeException(t);
                }
            }
        }
    }
    
    /*
     * ------------------------------------------------------
     * setters and getters
     */
    
    public void setDefaultClassView(Class defaultClassView) {
        this.defaultClassView = defaultClassView;
    }

    public void setProfileURI(String profileURI) {
        this.profileURI = profileURI;
    }
    

    public void setErrorHandler(ErrorHandler<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }
    
}
