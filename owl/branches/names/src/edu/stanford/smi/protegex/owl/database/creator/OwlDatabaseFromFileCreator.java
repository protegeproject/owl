package edu.stanford.smi.protegex.owl.database.creator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;

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

        writeOntologyAndPrefixInfo(owlModel, errors);
        DatabaseFactoryUtils.loadImports(owlModel, errors);
        
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
