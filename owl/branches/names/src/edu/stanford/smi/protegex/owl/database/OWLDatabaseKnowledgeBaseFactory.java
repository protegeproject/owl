package edu.stanford.smi.protegex.owl.database;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameFactory;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.menu.OWLBackwardsCompatibilityProjectFixups;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A DatabaseKnowledgeBaseFactory with an even longer name.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseKnowledgeBaseFactory extends DatabaseKnowledgeBaseFactory
        implements OWLKnowledgeBaseFactory {
    private static Logger log = Log.getLogger(OWLDatabaseKnowledgeBaseFactory.class);
    
    public final static String NAMESPACE_PREFIX_SEPARATOR = ":";
    public final static String CREATED_ONTOLOGY_NAME = "created_ontology_name";

    
    public OWLDatabaseKnowledgeBaseFactory() {
     	setOwlMode(true);
    }


    @Override
    public KnowledgeBase createKnowledgeBase(Collection errors) {   	
    	         
        ResourceSelectionAction.setActivated(false);
              
        OWLDatabaseModel owlModel = new OWLDatabaseModel(this);
       	   
        return owlModel;    	 	
    }
    


    private void dump(Cls cls, String tabs) {
      if (log.isLoggable(Level.FINE)) {
        log.fine(tabs + cls);
        for (Iterator it = cls.getDirectSubclasses().iterator(); it.hasNext();) {
            Cls subCls = (Cls) it.next();
            if (!subCls.isEditable()) {
                try {
                    dump(subCls, tabs + "  ");
                }
                catch (Exception ex) {
                    log.fine("ERROR at " + cls + " / " + subCls);
                    for (Iterator sit = subCls.getDirectSubclasses().iterator(); sit.hasNext();) {
                        Object o = sit.next();
                        log.fine("- " + o + " = " + (o instanceof Slot) + " " +
                                ((Instance) o).getDirectType());
                    }
                    Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                }
            }
        }
      }
    }


    @Override
    public String getProjectFilePath() {
        return "OWL.pprj";
    }


    @Override
    public String getDescription() {
        return "OWL / RDF Database";
    }

    @Override
    public void loadKnowledgeBase(KnowledgeBase kb, 
                                  PropertyList sources, 
                                  Collection errors) {
        OWLModel owlModel = (OWLModel) kb;
        boolean existsOntologyInstance = false;
        
        super.loadKnowledgeBase(kb, sources, errors);
        
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        tripleStoreModel.setTopTripleStore(activeTripleStore);
        if (DatabaseFactoryUtils.readOWLOntologyFromDatabase(owlModel, activeTripleStore)) {
            FactoryUtils.loadEncodedNamespaceFromModel(owlModel, activeTripleStore, errors);
            existsOntologyInstance = true;
        }
        else {
            String ontologyName = sources.getString(CREATED_ONTOLOGY_NAME);
            if (ontologyName != null) {
                FactoryUtils.addOntologyToTripleStore(owlModel, activeTripleStore, ontologyName);
                DatabaseFactoryUtils.writeOWLOntologyToDatabase(owlModel, activeTripleStore);
                FactoryUtils.encodeNamespaceIntoModel(owlModel, activeTripleStore);
                existsOntologyInstance = true;
            }
        }
        if (existsOntologyInstance) {
            FactoryUtils.addPrefixesToModelListener(owlModel, activeTripleStore);
            owlModel.resetOntologyCache();
            RepositoryFileManager.loadProjectRepositories(owlModel);
            DatabaseFactoryUtils.loadImports(owlModel, errors);
        }
    }




    
    @Override
    protected void initializeKB(KnowledgeBase kb, 
    		String driver, 
    		String url, 
    		String user, 
    		String password,
    		String table,
    		boolean isInclude) {

    	AbstractOWLModel dkb = (AbstractOWLModel) kb;
    	
    	FrameFactory factory = dkb.getFrameFactory();
    	
    	if (!(factory instanceof OWLJavaFactory)) {
    		Log.getLogger().warning("Adapting the java factory to OWLJavaFactory");
    		factory = new OWLJavaFactory(dkb);
    	}
    	
    	//TT remove IDA?
    	DatabaseFrameDb db = getDatabaseFrameDb(dkb);
    	db.initialize(factory, driver, url, user, password, table, isInclude);
    	kb.flushCache();
    }    

    @Override
    public void saveKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;
            TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
            OWLBackwardsCompatibilityProjectFixups.insertVersionData(sources);
            
            //move this from here
            if (owlModel instanceof JenaOWLModel) {
                TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
                DatabaseFactoryUtils.writeOWLOntologyToDatabase(owlModel, activeTripleStore);
            	FactoryUtils.encodeNamespaceIntoModel(owlModel, activeTripleStore);
            }
            
            if (owlModel instanceof JenaOWLModel) {
                kb.removeFrameStore(owlModel.getOWLFrameStore());
            }
            super.saveKnowledgeBase(kb, sources, errors);
            RepositoryFileManager.saveProjectRepositories(owlModel);
            if (owlModel instanceof JenaOWLModel) {
                kb.insertFrameStore(owlModel.getOWLFrameStore(), 0);
            }
        }
        else {
        	String message = "You can only save OWL projects to OWL Database format.";
            errors.add(new MessageError(message));
            Log.getLogger().severe(message);
        }
    }

}
