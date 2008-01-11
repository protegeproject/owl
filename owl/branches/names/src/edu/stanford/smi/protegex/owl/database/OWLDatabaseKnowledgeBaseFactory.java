package edu.stanford.smi.protegex.owl.database;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleStoreModelImpl;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A DatabaseKnowledgeBaseFactory with an even longer name.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseKnowledgeBaseFactory extends DatabaseKnowledgeBaseFactory
        implements OWLKnowledgeBaseFactory, ClientInitializerKnowledgeBaseFactory {
    private static Logger log = Log.getLogger(OWLDatabaseKnowledgeBaseFactory.class);
    
    public final static String NAMESPACE_PREFIX_SEPARATOR = ":";

    
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
        
        owlModel.resetDefaultOWLOntologyCache();
        loadImports(owlModel, errors);
        loadPrefixes(owlModel, errors);

    }

    
    private void loadImports(OWLModel owlModel, Collection errors) {
        for (String imprt : owlModel.getDefaultOWLOntology().getImports()) {
            try {
                 owlModel.addImport(URIUtilities.createURI(imprt));
            }
            catch (IOException e) {
                errors.add(e);
            }
        }
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
                createTopLevelOntologyInstance(owlModel);
            	writePrefixesToModel(owlModel);
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
    
    protected void createTopLevelOntologyInstance(OWLModel owlModel) {
		RDFProperty topLevelOWLOntologyURISlot = owlModel.getSystemFrames().getTopOWLOntologyURISlot();
		OWLDatabaseModel.getTopLevelOWLOntologyClassInstance(owlModel).setPropertyValue(topLevelOWLOntologyURISlot, owlModel.getDefaultOWLOntology());
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
        TripleStoreModel tsm = new TripleStoreModelImpl(owlModel, userNfs);
        owlModel.setTripleStoreModel(tsm);
        owlModel.initializeClient();
        owlModel.adjustClientFrameStores();
      }
    }
    
    
    /*
     * In case you are dyslexic like me a typical prefix namespace pair is
     *    prefix = dc
     *    namespace = http://purl.org/dc/elements/1.1/
     */
    
    private void loadPrefixes(OWLModel owlModel, Collection errors) {
        OWLOntology defaultOwlOntology = owlModel.getDefaultOWLOntology();
        
        RDFProperty prefixesProperty = owlModel.getSystemFrames().getOwlOntologyPrefixesProperty();
        NamespaceManager nm = owlModel.getNamespaceManager();
        for (Object o : defaultOwlOntology.getPropertyValues(prefixesProperty)) {
            if (o instanceof String) {
                String encodedNamespaceEntry = (String) o;
                int index = encodedNamespaceEntry.indexOf(NAMESPACE_PREFIX_SEPARATOR);
                if (index < 0) continue;
                String prefix = encodedNamespaceEntry.substring(0, index);
                String namespace = encodedNamespaceEntry.substring(index + 1);
                nm.setPrefix(namespace, prefix);
            }
        }
    }
    
    private void writePrefixesToModel(OWLModel owlModel) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Saving Prefixes to database, owl ontology = " + owlModel.getDefaultOWLOntology());
            log.fine("prefixes = " + owlModel.getNamespaceManager().getPrefixes());
        }
        
        
        OWLOntology defaultOwlOntology = owlModel.getDefaultOWLOntology();
        //delete the initial default ontology
        OWLOntology initialDefaultOntology = owlModel.getOWLOntologyByURI(ProtegeNames.DEFAULT_ONTOLOGY);
        if (initialDefaultOntology != null) {
            initialDefaultOntology.delete();
        }     
        RDFProperty prefixesProperty = owlModel.getSystemFrames().getOwlOntologyPrefixesProperty();
        NamespaceManager nm = owlModel.getNamespaceManager();
        for (String prefix  : nm.getPrefixes()) {
            String namespace = nm.getNamespaceForPrefix(prefix);
            String value = prefix + NAMESPACE_PREFIX_SEPARATOR + namespace;
            defaultOwlOntology.addPropertyValue(prefixesProperty, value);
        }
        
    }

}
