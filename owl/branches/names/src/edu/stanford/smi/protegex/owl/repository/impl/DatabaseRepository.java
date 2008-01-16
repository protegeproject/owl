package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.ValueCachingNarrowFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;

public class DatabaseRepository implements Repository {
    private final static Logger log = Log.getLogger(DatabaseRepository.class);
    
	public enum DB_FIELDS {
		DRIVER, URL, USER, PASSWORD;
	};
	Connection connection;
	private Map<DB_FIELDS, String> fieldMap = new EnumMap<DB_FIELDS, String>(DB_FIELDS.class);
	private Map<URI, String> ontologyToTable = new HashMap<URI, String>();
	private Map<String, PreparedStatement> statementCache = new HashMap<String, PreparedStatement>();
	
	public final static String REPOSITORY_DESCRIPTOR_PREFIX = "database:";
	public final static char SEPARATOR_CHAR = ',';
	
	static List<String> parse(String repositoryDescriptor) {
		List<String> fields = new ArrayList<String>();
		int start = REPOSITORY_DESCRIPTOR_PREFIX.length();
		while (true) {
			int end = repositoryDescriptor.indexOf(SEPARATOR_CHAR, start);
			if (end < 0) {
				fields.add(repositoryDescriptor.substring(start));
				return fields;
			}
			fields.add(repositoryDescriptor.substring(start, end));
			start = end + 1;
		}
	}
	
	public DatabaseRepository(String repositoryDescriptor) throws ClassNotFoundException, SQLException {
		List<String> fields = parse(repositoryDescriptor);
		for (DB_FIELDS field : DB_FIELDS.values()) {
			fieldMap.put(field, fields.get(field.ordinal()));
		}
		Class.forName(getDriver());
		connect();
		try {
		    for (int index = DB_FIELDS.values().length; index < fields.size(); index++) {
		        addTable(fields.get(index));
		    }
		} finally {
		    disconnect();
		}
	}
	
	public void connect() throws SQLException {
		connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
	}
	
	public void disconnect() throws SQLException {
	    for (PreparedStatement statement : statementCache.values()) {
	        statement.close();
	    }
		connection.close();
		connection = null;
	}
	
	public PreparedStatement getStatement(String query) throws SQLException {
	    PreparedStatement statement = statementCache.get(query);
	    if (statement == null) {
	        statement = connection.prepareStatement(query);
	        statementCache.put(query, statement);
	    }
	    return statement;
	}
	
	public static final String GET_ONTOLOGY_QUERY;
	static {
	    StringBuffer sb = new StringBuffer();
	    sb.append("select getOntology.short_value ");
	    sb.append("from ? as ontInstance join ? as getOntology ");
	    sb.append("on ontInstance.frame='");
	    sb.append(OWLNames.Cls.TOP_LEVEL_ONTOLOGY);
	    sb.append("' and ontInstance.slot='");
	    sb.append(Model.Slot.DIRECT_INSTANCES);
	    sb.append("' and getOntology.frame=ontInstance.short_value and getOntology.slot = '");
	    sb.append(OWLNames.Slot.TOP_LEVEL_ONTOLOGY_URI);
	    sb.append("';");
	    GET_ONTOLOGY_QUERY=sb.toString();
	}

	public boolean addTable(String table) {
	    String ontology = null;
	    try {
	        PreparedStatement query = getStatement(GET_ONTOLOGY_QUERY);
	        query.setString(0, table);
	        query.setString(1, table);
	        ResultSet results = query.executeQuery();
	        try {
	            while (results.next()) {
	                ontology = results.getString(0);
	                ontologyToTable.put(new URI(ontology), table);
	            }
	        }
	        finally {
	            results.close();
	        }
	        return true;
	    }
	    catch (SQLException e) {
	        if (log.isLoggable(Level.FINE)) {
	            log.log(Level.FINE, "Exception caught looking for ontology in db table " + table, e);
	        }
	        return false;
	    }
	    catch (URISyntaxException e) {
	        if (log.isLoggable(Level.FINE)) {
	            log.log(Level.FINE, "Ontology " + ontology + " found in " + table + " not in uri format.", e);
	        }
	        return false;  
	    }
	}

	public void addImport(OWLModel owlModel, URI ontologyName)
			throws IOException {
	    String table = ontologyToTable.get(ontologyName);
	    DatabaseFrameDb dbFrameStore = new DatabaseFrameDb();
	    dbFrameStore.initialize(owlModel.getOWLJavaFactory(), getDriver(), getUrl(), getUser(), getPassword(), table, true);
	    ValueCachingNarrowFrameStore valueCache = new ValueCachingNarrowFrameStore(dbFrameStore);
        TripleStore importedTripleStore = owlModel.getTripleStoreModel().createTripleStore(valueCache);
        importedTripleStore.setName(ontologyName.toString());
	}

	public boolean contains(URI ontologyName) {
	    return ontologyToTable.keySet().contains(ontologyName);
	}

	public Collection<URI> getOntologies() {
	    return Collections.unmodifiableCollection(ontologyToTable.keySet());
	}

	public String getOntologyLocationDescription(URI ontologyName) {
		return "Table " + ontologyToTable.get(ontologyName) + " of the database " + getUrl();
	}

	public OutputStream getOutputStream(URI ontologyName) throws IOException {
		return null;
	}

	public String getRepositoryDescription() {
		return "Repository for the database " + getUrl();
	}

	public String getRepositoryDescriptor() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(REPOSITORY_DESCRIPTOR_PREFIX);
	    for (DB_FIELDS field : DB_FIELDS.values()) {
	        sb.append(fieldMap.get(field));
	        sb.append(SEPARATOR_CHAR);
	    }
	    
		return null;
	}

	public boolean isSystem() {
		return false;
	}

	public boolean isWritable(URI ontologyName) {
		return true;
	}

	public void refresh() {
	    // not sure if this should look at all the tables in the database or just 
	    // request the ones selected...  When am i called?
	}

	public String getDriver() {
		return fieldMap.get(DB_FIELDS.DRIVER);
	}
	
	public String getUrl() {
		return fieldMap.get(DB_FIELDS.URL);
	}
	
	public String getUser() {
		return fieldMap.get(DB_FIELDS.USER);
	}

	public String getPassword() {
		return fieldMap.get(DB_FIELDS.PASSWORD);
	}
}
