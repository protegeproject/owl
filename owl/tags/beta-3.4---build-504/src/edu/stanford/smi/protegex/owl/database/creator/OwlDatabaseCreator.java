package edu.stanford.smi.protegex.owl.database.creator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
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
	public void create(Collection errors) throws OntologyLoadException {
		if (pleaseCleanDatabase()) {
			try {
				initializeTable(errors);
			} catch (IOException e) {
				throw new OntologyLoadException(e, "Could not initialize DB tables");
			}
		}

		super.create(errors);
		if (ontologyName == null) {
			ontologyName = FactoryUtils.generateOntologyURIBase();
		}
		try {
			FactoryUtils.addOntologyToTripleStore(getOwlModel(), 
					getOwlModel().getTripleStoreModel().getActiveTripleStore(), 
					ontologyName);
			writeOntologyAndPrefixInfo(getOwlModel(), errors);
		}
		catch (AlreadyImportedException e) {
			throw new RuntimeException("This shouldn't happen", e);
		}
	}

	private boolean pleaseCleanDatabase() throws OntologyLoadException {
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

	private void rethrow(Throwable t) throws OntologyLoadException {
		if (t instanceof OntologyLoadException) {
			throw (OntologyLoadException) t;
		}
		throw new OntologyLoadException(t, t.getMessage());
	}

	/*
	 * setters and getters
	 */

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

}
