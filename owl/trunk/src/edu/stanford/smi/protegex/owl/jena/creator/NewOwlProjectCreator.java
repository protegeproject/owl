package edu.stanford.smi.protegex.owl.jena.creator;

import java.io.IOException;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;

public class NewOwlProjectCreator extends AbstractOwlProjectCreator {
    private String ontologyName;
    
    public NewOwlProjectCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public NewOwlProjectCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @Override
    public Project create(Collection errors) throws IOException {
        Project project = Project.createNewProject(factory, errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        if (ontologyName == null) {
            ontologyName = FactoryUtils.generateOntologyURIBase();
        }
        try {
            FactoryUtils.addOntologyToTripleStore(owlModel, owlModel.getTripleStoreModel().getActiveTripleStore(), ontologyName);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
        addViewSettings(project.getSources());
        
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
