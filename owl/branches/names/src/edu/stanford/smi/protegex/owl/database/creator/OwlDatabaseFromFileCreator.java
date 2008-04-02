package edu.stanford.smi.protegex.owl.database.creator;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;

public class OwlDatabaseFromFileCreator extends AbstractOwlDatabaseCreator {
    private static transient Logger log = Log.getLogger(OwlDatabaseFromFileCreator.class);
    
    private String ontologySource;
    
    public OwlDatabaseFromFileCreator() {
        this(new OWLDatabaseKnowledgeBaseFactory());
    }
    
    public OwlDatabaseFromFileCreator(OWLDatabaseKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Project create(Collection errors) throws IOException {
        initializeTable(errors);
        Project project = super.create(errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        parser.run(URIUtilities.createURI(ontologySource));

        try {
            writeOntologyAndPrefixInfo(owlModel, errors);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
        DatabaseFactoryUtils.loadImports(owlModel, errors);
        ProtegeOWLParser.doFinalPostProcessing(owlModel);
        
        errors.addAll(ProtegeOWLParser.getErrors());
        
        return project;
    }
    
    
    /*
     * setters and getters
     */
    
    public void setOntologySource(String uri) {
        ontologySource = uri;
    }

}
