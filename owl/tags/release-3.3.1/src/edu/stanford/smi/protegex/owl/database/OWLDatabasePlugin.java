package edu.stanford.smi.protegex.owl.database;

import edu.stanford.smi.protege.storage.database.DatabasePlugin;

import java.net.URI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLDatabasePlugin extends DatabasePlugin {

    void setOntologyFileURI(URI uri);
}
