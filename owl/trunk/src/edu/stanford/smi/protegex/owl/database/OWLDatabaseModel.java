package edu.stanford.smi.protegex.owl.database;

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
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.AbstractFrameStoreInvocationHandler;
import edu.stanford.smi.protege.model.framestore.EventGeneratorFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protegex.owl.database.triplestore.DatabaseTripleStoreModel;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.LocalClassificationFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameFactoryInvocationHandler;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;

import java.util.List;

/**
 * An AbstractOWLModel with extra support for databases.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseModel
        extends AbstractOWLModel
        implements OntModelProvider {

    private TripleStoreModel tripleStoreModel;


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
        getOWLSystemFramesArray();
        initialize();
    }

    public void initialize() {
        final OWLNamespaceManager namespaceManager = new OWLNamespaceManager();
        super.initialize(namespaceManager);
        initCustomFrameStores();
        setFrameFactory(new OWLJavaFactory(this));
        adjustThing();
        adjustSystemClasses();
        getNamespaceManager().update();
    }


    public OWLOntology getDefaultOWLOntology() {
        OWLOntology ontology = super.getDefaultOWLOntology();
        if (ontology == null) {
            createDefaultOWLOntologyReally();
            return super.getDefaultOWLOntology();
        }
        else {
            return ontology;
        }
    }


    public OntModel getOntModel() {

        long startTime = System.currentTimeMillis();
        JenaCreator creator = new JenaCreator(this, false, null,
                                              new ModalProgressBarManager("Converting Ontology"));
        OntModel ontModel = creator.createOntModel();
        long endTime = System.currentTimeMillis();
        System.out.println("[OWLDatabaseModel.getOntModel] Duration " + (endTime - startTime));
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
        System.out.println("[OWLDatabaseModel.getOWLDLOntModel] Duration " + (endTime - startTime));
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
            tripleStoreModel = new DatabaseTripleStoreModel(this);
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


    public void initOWLFrameFactoryInvocationHandler() {
        Class clazz = OWLFrameFactoryInvocationHandler.class;
        FrameStore frameFactoryInvocationFrameStore = AbstractFrameStoreInvocationHandler.newInstance(clazz, this);
        List frameStores = getFrameStores();
        int index = 0;
        while (!(frameStores.get(index) instanceof EventGeneratorFrameStore)) {
            index++;
        }
        //index = frameStores.size(); // Test!
        insertFrameStore(frameFactoryInvocationFrameStore, index - 1);
    }


    public void resetTripleStoreModel() {
        tripleStoreModel = null;
    }


    public void setProject(Project project) {
        super.setProject(project);

        if (!project.isMultiUserServer()) {
            int position = getFrameStores().size() - 1;
            if (!(getFrameStores().get(position) instanceof LocalClassificationFrameStore)) {
                insertFrameStore(new LocalClassificationFrameStore(this), position);
            }
        }
    }
}
