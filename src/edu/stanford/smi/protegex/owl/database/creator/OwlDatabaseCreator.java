package edu.stanford.smi.protegex.owl.database.creator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;

public class OwlDatabaseCreator extends AbstractOwlDatabaseCreator {
    private boolean wipe;
    
    private String ontologyName;

    public OwlDatabaseCreator(boolean wipe) {
        this(new OWLDatabaseKnowledgeBaseFactory(), wipe);
    }

    public OwlDatabaseCreator(OWLDatabaseKnowledgeBaseFactory factory, boolean wipe) {
        super(factory);
        this.wipe = wipe;
    }
    
    @Override
    public Project create(Collection errors) throws IOException {
        if (pleaseCleanDatabase()) {
            initializeTable(errors);
        }
        Project project = super.create(errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        if (ontologyName == null) {
            ontologyName = FactoryUtils.generateOntologyURIBase();
        }
        try {
            FactoryUtils.addOntologyToTripleStore(owlModel, 
                                                  owlModel.getTripleStoreModel().getActiveTripleStore(), 
                                                  ontologyName);
            writeOntologyAndPrefixInfo(owlModel, errors);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
        return project;
    }
    
    private boolean pleaseCleanDatabase() throws IOException {
        if (wipe) { return true; }
        Connection connection = null;
        try {
            Class.forName(getDriver());
            connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
        }
        catch (ClassNotFoundException cnfe) {
            rethrow(cnfe);
        }
        catch (SQLException sqle) {
            rethrow(sqle);
        }
        try {
            return DatabaseFactoryUtils.getOntologyFromTable(connection, getTable()) == null;
        }
        catch (SQLException sqle) {
            return true;
        }
    }
    
    private void rethrow(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw (IOException) t;
        }
        IOException ioe = new IOException(t.getMessage());
        ioe.initCause(t);
        throw ioe;
    }
    
    /*
     * setters and getters
     */
    
    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }

}
