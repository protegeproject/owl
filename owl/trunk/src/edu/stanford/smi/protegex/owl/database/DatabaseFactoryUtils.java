package edu.stanford.smi.protegex.owl.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.AmalgamatedLoadException;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.DefaultSlot;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameFactory;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDbFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

/**
 * This class is for internal use only.  It is used during the loading, saving and importing
 * of database models.
 * 
 * @author tredmond
 *
 */
public class DatabaseFactoryUtils {
    public static transient final Logger log = Log.getLogger(DatabaseFactoryUtils.class);
    
    

    
    public static boolean readOWLOntologyFromDatabase(OWLModel owlModel, TripleStore tripleStore) {
        NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        RDFProperty owlOntologyPointerProperty = systemFrames.getOwlOntologyPointerProperty();
        RDFIndividual owlOntologyPointerInstance = getOwlOntologyPointerInstance(owlModel, nfs);
        if (owlOntologyPointerInstance != null) {
            OWLOntology  ontology = (OWLOntology) owlOntologyPointerInstance.getPropertyValue(owlOntologyPointerProperty);
            tripleStore.setName(ontology.getName());
            return true;
        }
        else {
            return false;
        }
    }
    
    public static void writeOWLOntologyToDatabase(OWLModel owlModel, TripleStore tripleStore) {
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        try {
            tripleStoreModel.setActiveTripleStore(tripleStore);
            NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
            OWLSystemFrames systemFrames = owlModel.getSystemFrames();
            RDFProperty owlOntologyPointerProperty = systemFrames.getOwlOntologyPointerProperty();
            RDFIndividual owlOntologyPointerInstance = getOwlOntologyPointerInstance(owlModel, nfs);
            if (owlOntologyPointerInstance == null) {
                owlOntologyPointerInstance = (RDFIndividual) systemFrames.getOwlOntologyPointerClass().createInstance(null);
            }
            owlOntologyPointerInstance.setPropertyValue(owlOntologyPointerProperty, tripleStore.getOWLOntology());
        }
        finally {
            tripleStoreModel.setActiveTripleStore(activeTripleStore);
        }
    }
    
    private static  RDFIndividual getOwlOntologyPointerInstance(OWLModel owlModel, NarrowFrameStore nfs) {
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        RDFSNamedClass owlOntologyPointerClass = systemFrames.getOwlOntologyPointerClass();
        for (Object o : nfs.getValues(owlOntologyPointerClass, systemFrames.getDirectInstancesSlot(), null, false)) {
            return (RDFIndividual) o;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void loadImports(OWLModel owlModel, Collection errors) {
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        OWLOntology ontology = activeTripleStore.getOWLOntology();
        for (String imprt : ontology.getImports()) {
            try {
                 ((AbstractOWLModel) owlModel).loadImportedAssertions(URIUtilities.createURI(imprt));
            }
            catch (AmalgamatedLoadException e) {
                errors.addAll(e.getErrorList());
            }
            catch (OntologyLoadException e) {
                errors.add(e);
            }
        }
    }

	public static String getOntologyFromTable(Class<? extends DatabaseFrameDb> databaseFrameDbClass, 
	                                          String driver, String url, 
	                                          String username, String password, String table) throws SQLException {
	    DatabaseFrameDb frameDb = null;
		try {
			FrameFactory factory = new OWLJavaFactory( null );
			frameDb = DatabaseFrameDbFactory.createDatabaseFrameDb(databaseFrameDbClass);
			frameDb.initialize( factory, driver, url, username, password, table, false );
			Frame owlOntologyPointerCls = new DefaultRDFSNamedClass( null, new FrameID(
					OWLNames.Cls.OWL_ONTOLOGY_POINTER_CLASS ) );
			Slot directInstances = new DefaultSlot( null, Model.SlotID.DIRECT_INSTANCES );
			Slot owlOntologyPointerSlot = new DefaultRDFProperty( null, new FrameID(
					OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY ) );
			List<?> intermediate = frameDb.getValues( owlOntologyPointerCls, directInstances, null,
					false );
			for( Object o : intermediate ) {
				if( o instanceof Frame ) {
					Frame f = (Frame) o;
					List<?> values = frameDb.getValues( f, owlOntologyPointerSlot, null, false );
					for( Object v : values ) {
						if( v instanceof OWLOntology )
							return ((OWLOntology) v).getName();
					}
				}
			}
		} catch( RuntimeException e ) {
			Throwable cause = e.getCause();
			if( (cause != null) && (cause instanceof SQLException) )
				throw (SQLException)cause;
			else
				throw e;
		}
		finally {
		    if (frameDb != null) {
		        frameDb.close();
		    }
		}
		return null;
	}
}
