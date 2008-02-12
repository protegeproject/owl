package edu.stanford.smi.protegex.owl.database;

import java.net.URI;

import edu.stanford.smi.protege.storage.database.DatabasePlugin;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLDatabasePlugin extends DatabasePlugin {

    void setOntologyFileURI(URI uri);
    
    void setOntologyName(String name);
}
