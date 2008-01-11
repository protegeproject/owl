package edu.stanford.smi.protegex.owl.database;

import java.util.Collections;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.dig.DIGReasoner;
import com.hp.hpl.jena.reasoner.dig.DIGReasonerFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.LocalClassificationFrameStore;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLDatabaseNamespaceManager;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleStoreModelImpl;
import edu.stanford.smi.protegex.owl.model.util.ImportUtil;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;

import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;

/**
 * An AbstractOWLModel with extra support for databases.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseModel
        extends AbstractOWLModel
        implements OntModelProvider {
    private static transient Logger log = Log.getLogger(OWLDatabaseModel.class);

    private TripleStoreModel tripleStoreModel;
    
    private OWLOntology defaultDBOWLOntology;


    public OWLDatabaseModel(KnowledgeBaseFactory factory) {    	
        super(factory);
    }

	/**
     * Initializes the OWLDatabaseModel in the case that it is a client of a remote server.
     * <p/>
     * This is a little delicate because there is no database and no NarrowFrameStores.
     * Many of the assumptions of the OWLDatabaseModel class are invalid.
     */
    public void initializeClient() {
        initialize();
    }

    public void initialize() {
    	setFrameFactory(new OWLJavaFactory(this));
        
        super.initialize();

        initCustomFrameStores();
    }


    @Override
	public OWLOntology getDefaultOWLOntology() {
    	if (defaultDBOWLOntology != null) {
    		return defaultDBOWLOntology;
    	}
    	return defaultDBOWLOntology = getTopLevelOntololgyFromDatabase();
    }
    
    public void setDefaultOWLOntology(OWLOntology defaultOWLOntology) {
        RDFProperty topLevelOWLOntologyURISlot = getSystemFrames().getTopOWLOntologyURISlot();
        getTopLevelOWLOntologyClassInstance(this).setPropertyValue(topLevelOWLOntologyURISlot, defaultOWLOntology);
        defaultDBOWLOntology = defaultOWLOntology;
    }
    
    public void resetDefaultOWLOntologyCache() {
        defaultDBOWLOntology = null;
    }
    
    protected OWLOntology getTopLevelOntololgyFromDatabase() {

		RDFProperty topLevelOWLOntologyURISlot = getSystemFrames().getTopOWLOntologyURISlot();
		return (OWLOntology) getTopLevelOWLOntologyClassInstance(this).getPropertyValue(topLevelOWLOntologyURISlot);
	}
    
    public static RDFIndividual getTopLevelOWLOntologyClassInstance(OWLModel owlModel) {
        RDFSNamedClass topLevelOWLOntologyClass = owlModel.getSystemFrames().getTopOWLOntologyClass();
        if (topLevelOWLOntologyClass.getInstances(false).isEmpty()) {
            return topLevelOWLOntologyClass.createRDFIndividual(null);
        }
        else {
            // there should only be one!
            return ((RDFIndividual) topLevelOWLOntologyClass.getInstances(false).iterator().next());
        }
    }
    
    
	@Override
    protected OWLOntology createDefaultOWLOntologyReally() {
    	return (OWLOntology) createInstance(ProtegeNames.DEFAULT_ONTOLOGY, getOWLOntologyClass());
    }

	public OntModel getOntModel() {

        long startTime = System.currentTimeMillis();
        JenaCreator creator = new JenaCreator(this, false, null,
                                              new ModalProgressBarManager("Converting Ontology"));
        OntModel ontModel = creator.createOntModel();
        long endTime = System.currentTimeMillis();
        Log.getLogger().info("[OWLDatabaseModel.getOntModel] Duration " + (endTime - startTime));
        return ontModel;
    }


    public OntModel getOWLDLOntModel() {
        // return new JenaDLConverter(getOntModel()).convertOntModel();
        long startTime = System.currentTimeMillis();
        JenaCreator creator = new JenaCreator(this, true, null,
                                              new ModalProgressBarManager("Preparing Ontology"));
        OntModel ontModel = creator.createOntModel();
        // ontModel.write(System.out, ModelLoader.langXMLAbbrev);
        long endTime = System.currentTimeMillis();
        log.info("[OWLDatabaseModel.getOWLDLOntModel] Duration " + (endTime - startTime));
        return ontModel;
    }


    public int getOWLSpecies() {
        OntModel ontModel = getOntModel();
        return Jena.getOWLSpecies(ontModel);
    }


    public OntModel getReasonerOntModel(String classifierURL) {

        com.hp.hpl.jena.rdf.model.Model newModel = ModelFactory.createDefaultModel();
        Resource resource = newModel.createResource("http://foo.de#foo");
        newModel.add(resource, ReasonerVocabulary.EXT_REASONER_URL, classifierURL);

        DIGReasoner reasoner = (DIGReasoner) ReasonerRegistry.theRegistry().
                create(DIGReasonerFactory.URI, resource);

        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
        spec.setReasoner(reasoner);
        OntModel m = Jena.cloneOntModel(getOntModel(), spec);
        //m.getBaseModel().write(System.out, ModelLoader.langXMLAbbrev, getNamespaceManager().getDefaultNamespace());
        return m;
    }


    @Override
	public String getNextAnonymousResourceName() {
        for (; ;) {
            int rand = (int) (Math.random() * 1000000);
            String name = ANONYMOUS_BASE + rand;
            if (getFrame(name) == null) {
                return name;
            }
        }
    }


    public TripleStoreModel getTripleStoreModel() {
        if (tripleStoreModel == null) {
            tripleStoreModel = new TripleStoreModelImpl(this);
        }
        return tripleStoreModel;
    }

    public void setTripleStoreModel(TripleStoreModel tripleStoreModel) {
        this.tripleStoreModel = tripleStoreModel;
    }


    public void initCustomFrameStores() {
        initOWLFrameStore();
    }


    public void initDefaultNamespaces() {
        NamespaceManager namespaceManager = getNamespaceManager();
        namespaceManager.setPrefix(OWL.getURI(), OWLNames.OWL_PREFIX);
        namespaceManager.setPrefix(RDF.getURI(), RDFNames.RDF_PREFIX);
        namespaceManager.setPrefix(RDFS.getURI(), RDFSNames.RDFS_PREFIX);
        namespaceManager.setPrefix(XSDDatatype.XSD + "#", RDFNames.XSD_PREFIX);
        namespaceManager.setModifiable(OWLNames.OWL_PREFIX, false);
        namespaceManager.setModifiable(RDFNames.RDF_PREFIX, false);
        namespaceManager.setModifiable(RDFSNames.RDFS_PREFIX, false);
        namespaceManager.setModifiable(RDFNames.XSD_PREFIX, false);
    }





    public void resetTripleStoreModel() {
        tripleStoreModel = null;
    }


    @Override
	public void setProject(Project project) {
        super.setProject(project);

        if (!project.isMultiUserServer()) {
            int position = getFrameStores().size() - 1;
            if (!(getFrameStores().get(position) instanceof LocalClassificationFrameStore)) {
                insertFrameStore(new LocalClassificationFrameStore(this), position);
            }
        }
    }

    
	@Override
	public void initOWLFrameFactoryInvocationHandler() {
		Log.getLogger().warning("This method should not be invoked!");		
	}
}
