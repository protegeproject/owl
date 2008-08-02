package edu.stanford.smi.protegex.owl.database.creator;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;

public class OwlDatabaseFromFileCreator extends AbstractOwlDatabaseCreator {
    private static transient Logger log = Log.getLogger(OwlDatabaseFromFileCreator.class);

    private List<Repository> repositories = new ArrayList<Repository>();
    private boolean isMergeImportMode = false;
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
        loadProjectRepositories(getOwlModel());

        boolean initialMergeMode = ProtegeOWLParser.isMergingImportMode();
        try {
        	ProtegeOWLParser.setMergingImportMode(isMergeImportMode);
        	ProtegeOWLParser parser = new ProtegeOWLParser(getOwlModel());
        	parser.run(URIUtilities.createURI(ontologySource));
        } finally {
        	ProtegeOWLParser.setMergingImportMode(initialMergeMode);
        }

        try {
            writeOntologyAndPrefixInfo(getOwlModel(), errors);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }

        errors.addAll(ProtegeOWLParser.getErrors());
    }


    protected void loadProjectRepositories(OWLDatabaseModel owlModel) {
    	URI prjUri = owlModel.getProject().getProjectURI(); //hack for relative repositories
		try {
			String pprjString = FileUtilities.replaceExtension(ontologySource, ".pprj");
			owlModel.getProject().setProjectURI(URIUtilities.createURI(pprjString));
			String repString = FileUtilities.replaceExtension(ontologySource, ".repository");
			RepositoryUtil.loadProjectRepositoriesFromURI(owlModel, URIUtilities.createURI(repString), false);
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at loading project repositories", e);
		} finally {
			owlModel.getProject().setProjectURI(prjUri); //end hack
		}

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

    @Override
	public List<Repository> getRepositories() {
    	return Collections.unmodifiableList(repositories);
    }

    public boolean isMergeImportMode() {
		return isMergeImportMode;
	}

	public void setMergeImportMode(boolean isMergeImportMode) {
		this.isMergeImportMode = isMergeImportMode;
	}

}
