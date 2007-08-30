package edu.stanford.smi.protegex.owl.database;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.FrameFactory;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.server.ClientInitializerKnowledgeBaseFactory;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.database.triplestore.DatabaseTripleStoreModel;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.ProgressDisplayDialog;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A DatabaseKnowledgeBaseFactory with an even longer name.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseKnowledgeBaseFactory extends DatabaseKnowledgeBaseFactory
        implements OWLKnowledgeBaseFactory, ClientInitializerKnowledgeBaseFactory {
    private static Logger log = Log.getLogger(OWLDatabaseKnowledgeBaseFactory.class);

    
    public OWLDatabaseKnowledgeBaseFactory() {
     	setOwlMode(true);
    }


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
                        Object o = (Object) sit.next();
                        log.fine("- " + o + " = " + (o instanceof Slot) + " " +
                                ((Instance) o).getDirectType());
                    }
                    Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                }
            }
        }
      }
    }


    public String getProjectFilePath() {
        return "OWL.pprj";
    }


    public String getDescription() {
        return "OWL / RDF Database";
    }


    public void loadKnowledgeBase(KnowledgeBase kb,
                                  String driver,
                                  String table,
                                  String url,
                                  String user,
                                  String password,
                                  Collection errors) {

        OWLDatabaseModel owlModel = (OWLDatabaseModel) kb;

        super.loadKnowledgeBase(kb, driver, table, url, user, password, errors);

        owlModel.initialize();
    }

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

    public void saveKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;
            sources.setInteger(JenaKnowledgeBaseFactory.OWL_BUILD_PROPERTY, OWLText.getBuildNumber());
            
            //move this from here
            if (owlModel instanceof JenaOWLModel) {
            	writePrefixesToDatabase(owlModel);
            }
            
            if (owlModel instanceof JenaOWLModel) {
                kb.removeFrameStore(owlModel.getOWLFrameStore());
            }
            super.saveKnowledgeBase(kb, sources, errors);
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


    private void writePrefixesToDatabase(OWLModel owlModel) {
		// TODO Auto-generated method stub
    	System.out.println(owlModel.getDefaultOWLOntology());
    	System.out.println(owlModel.getNamespaceManager().getPrefixes());
    	
    	//delete the initial default ontology
    	OWLOntology initialDefaultOntology = owlModel.getOWLOntologyByURI(ProtegeNames.DEFAULT_ONTOLOGY);
    	if (initialDefaultOntology != null) {
    		initialDefaultOntology.delete();
    	}
    	
    	OWLOntology defaultOwlOntology = owlModel.getDefaultOWLOntology();
    	
    	//write the default ontology to the database
    	createTopLevelOntologyInstance(owlModel);
    	
    	Slot prefixesSlot = owlModel.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
    	NamespaceManager nm = owlModel.getNamespaceManager();
    	for (String prefix  : nm.getPrefixes()) {
			String namespace = nm.getNamespaceForPrefix(prefix);
			String value = prefix + ":" + namespace;
			defaultOwlOntology.addOwnSlotValue(prefixesSlot, value);
		}
		
	}

    
    protected void createTopLevelOntologyInstance(OWLModel owlModel) {
		Cls topLevelOWLOntologyClass = owlModel.getCls(OWLNames.Cls.TOP_LEVEL_ONTOLOGY);
		Slot topLevelOWLOntologyURISlot = owlModel.getSlot(OWLNames.Slot.TOP_LEVEL_ONTOLOGY_URI);
		
		if (topLevelOWLOntologyClass == null || topLevelOWLOntologyURISlot == null) {
			Log.getLogger().warning("Could not write top level ontology to the database. Missing system frames");
			return;
		}
		
		//should be just one
		Instance inst = topLevelOWLOntologyClass.createDirectInstance(null);
		inst.setOwnSlotValue(topLevelOWLOntologyURISlot, owlModel.getDefaultOWLOntology());
		
	}
    

	protected void updateKnowledgeBase(DefaultKnowledgeBase kb) {
        // Overloaded to suppress super call
    }


    public void initializeClientKnowledgeBase(FrameStore fs, 
                                              NarrowFrameStore systemNfs,
                                              NarrowFrameStore userNfs,
                                              KnowledgeBase kb) { 
      if (kb instanceof OWLDatabaseModel) {
        OWLDatabaseModel owlModel = (OWLDatabaseModel) kb;
        DatabaseTripleStoreModel tsm = new DatabaseTripleStoreModel(owlModel,systemNfs, userNfs);
        owlModel.setTripleStoreModel(tsm);
        owlModel.initializeClient();
        owlModel.adjustClientFrameStores();
      }
    }

}
