package edu.stanford.smi.protegex.owl.database.creator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.repository.Repository;

public class OwlDatabaseFromFileCreator extends AbstractOwlDatabaseCreator {
    private static transient Logger log = Log.getLogger(OwlDatabaseFromFileCreator.class);
    
    private List<Repository> repositories = new ArrayList<Repository>();

    
    private String ontologySource;
    
    public OwlDatabaseFromFileCreator() {
        this(new OWLDatabaseKnowledgeBaseFactory());
    }
    
    public OwlDatabaseFromFileCreator(OWLDatabaseKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(Collection errors) throws OntologyLoadException {
        try {
			initializeTable(errors);
		} catch (IOException e1) {
			throw new OntologyLoadException(e1, "Could not initialize DB tables");
		}

        super.create(errors);
        insertRepositoriesIntoOwlModel(getOwlModel());
        ProtegeOWLParser parser = new ProtegeOWLParser(getOwlModel());
        parser.run(URIUtilities.createURI(ontologySource));

        try {
            writeOntologyAndPrefixInfo(getOwlModel(), errors);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
        DatabaseFactoryUtils.loadImports(getOwlModel(), errors);
        ProtegeOWLParser.doFinalPostProcessing(getOwlModel());
        
        errors.addAll(ProtegeOWLParser.getErrors());
    }
    
    
    /*
     * setters and getters
     */
    
    public void setOntologySource(String uri) {
        ontologySource = uri;
    }
    
    public void addRepository(Repository repository) {
    	repositories.add(repository);
    }
    
    public void clearRepositories() {
    	repositories.clear();
    }
    
    public List<Repository> getRepositories() {
    	return Collections.unmodifiableList(repositories);
    }

}
