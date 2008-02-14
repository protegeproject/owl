package edu.stanford.smi.protegex.owl.jena.creator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ErrorHandler;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class NewOwlProjectCreator extends AbstractOwlProjectCreator {
    private String ontologyName;
    
    public NewOwlProjectCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public NewOwlProjectCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @Override
    public Project create() {
        Collection errors = new ArrayList();
        Project project = Project.createNewProject(factory, errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        if (ontologyName == null) {
            ontologyName = FactoryUtils.generateOntologyURIBase();
        }
        FactoryUtils.addOntologyToTripleStore(owlModel, owlModel.getTripleStoreModel().getActiveTripleStore(), ontologyName);
        
        addViewSettings(project.getSources());
        
        handleErrors(errors);
        
        return project;
    }

    /*
     * ---------------------------------------------------------------------
     * setters and getters
     */
    
    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }
    
    

}
