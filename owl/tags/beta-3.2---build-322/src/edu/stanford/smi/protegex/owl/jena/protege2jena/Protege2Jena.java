package edu.stanford.smi.protegex.owl.jena.protege2jena;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

/**
 * An object that can convert an OWLModel into an OntModel.
 * This implementation uses the OWLModel's TripleStore and
 * creates corresponding Jena triples for each.
 * <p/>
 * When completed, this will replace the JenaCreator class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class Protege2Jena {

    /**
     * A temporary Model which is used to create properties without having to define
     * them with the correct type each time
     */
    private Model dummyModel = ModelFactory.createDefaultModel();

    private Collection fillTripleStores;

    /**
     * The source OWLModel
     */
    private OWLModel owlModel;

    /**
     * The target OntModel
     */
    private OntModel ontModel;

    /**
     * A Map from TripleStores to Jena Models
     */
    private Map tripleStore2Model;

    private TripleStoreModel tripleStoreModel;

    private static final String TEMP_SUFFIX = ".temp";


    private Protege2Jena(OWLModel owlModel,
                         OntModel ontModel,
                         Collection fillTripleStores,
                         Map tripleStore2Model) {

        this.fillTripleStores = fillTripleStores;
        this.owlModel = owlModel;
        this.ontModel = ontModel;
        this.tripleStoreModel = owlModel.getTripleStoreModel();
        this.tripleStore2Model = tripleStore2Model;

        // Create SubModels and put them into Map
        createSubModels();

        // Fill each SubModel
        fillModels();
    }


    private void copyTriples(TripleStore tripleStore, Model model) {
        Iterator it = tripleStore.listTriples();
        while (it.hasNext()) {
            Triple triple = (Triple) it.next();
            Statement stmt = createStatement(triple, model);
            model.add(stmt);
        }
    }


    public static OntModel createOntModel(OWLModel owlModel) {
        return createOntModel(owlModel, OntModelSpec.OWL_MEM, owlModel.getTripleStoreModel().getTripleStores());
    }


    public static OntModel createOntModel(OWLModel owlModel, Collection fillTripleStores) {
        return createOntModel(owlModel, OntModelSpec.OWL_MEM, fillTripleStores);
    }


    public static OntModel createOntModel(OWLModel owlModel, Collection fillTripleStores, Map tripleStores2Model) {
        return createOntModel(owlModel, OntModelSpec.OWL_MEM, fillTripleStores, tripleStores2Model);
    }


    public static OntModel createOntModel(OWLModel owlModel, OntModelSpec spec, Collection fillTripleStores) {
        Map map = new HashMap();
        return createOntModel(owlModel, spec, fillTripleStores, map);
    }


    public static OntModel createOntModel(OWLModel owlModel, OntModelSpec spec, Collection fillTripleStores, Map tripleStore2Model) {
        OntModel ontModel = (OntModel) ModelFactory.createOntologyModel(spec);
        new Protege2Jena(owlModel, ontModel, fillTripleStores, tripleStore2Model);
        return ontModel;
    }


    private void createSubModels() {
        Iterator it = tripleStoreModel.listUserTripleStores();
        TripleStore baseTripleStore = (TripleStore) it.next();
        Model baseModel = ontModel.getBaseModel();
        tripleStore2Model.put(baseTripleStore, baseModel);
        while (it.hasNext()) {
            TripleStore tripleStore = (TripleStore) it.next();
            Model subModel = createSubModel(tripleStore);
            ontModel.addSubModel(subModel);
            tripleStore2Model.put(tripleStore, subModel);
        }
    }


    private Model createSubModel(TripleStore tripleStore) {
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }


    private void createNamespacePrefixes(TripleStore tripleStore, Model model) {

        // Delete any existing namespace declarations
        for (Iterator it = model.getNsPrefixMap().keySet().iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            model.removeNsPrefix(prefix);
        }

        // Copy prefixes from TripleStore into Model
        for (Iterator it = tripleStore.getPrefixes().iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            String namespace = tripleStore.getNamespaceForPrefix(prefix);
            model.setNsPrefix(prefix, namespace);
        }
    }


    private Statement createStatement(Triple triple, Model model) {
        Resource subject = getResource(triple.getSubject(), model);
        Property predicate = getProperty(triple.getPredicate(), model);
        RDFNode object = getRDFNode(triple.getObject(), model);
        return model.createStatement(subject, predicate, object);
    }


    private void fillModels() {
        for (Iterator it = tripleStore2Model.keySet().iterator(); it.hasNext();) {
            TripleStore tripleStore = (TripleStore) it.next();
            if (fillTripleStores.contains(tripleStore)) {
                Model model = getModel(tripleStore);
                createNamespacePrefixes(tripleStore, model);
                copyTriples(tripleStore, model);
                removeRedundantRDFSDomains(model);
                removeRedundantRDFSSubClassOfOWLThings(model);
                removeRedundantRDFSSubClassOfEquivalentClasses(model);
                removeRedundantRDFLists(model);
            }
        }
    }


    private Model getModel(TripleStore tripleStore) {
        return (Model) tripleStore2Model.get(tripleStore);
    }


    protected static Set getParseTypeCollectionProperties() {
        Set set = new HashSet();
        set.add(OWL.unionOf);
        set.add(OWL.intersectionOf);
        set.add(OWL.distinctMembers);
        set.add(OWL.oneOf);
        return set;
    }


    private Property getProperty(RDFProperty rdfProperty, Model model) {
        String uri = rdfProperty.getURI();
        Property property = dummyModel.getProperty(uri);
        if (property != null) {
            return property;
        }
        return dummyModel.createProperty(uri);
    }


    private RDFNode getRDFNode(Object object, Model model) {
        if (object instanceof RDFResource) {
            return getResource((RDFResource) object, model);
        }
        else if (object instanceof RDFSLiteral) {
            RDFSLiteral literal = (RDFSLiteral) object;
            String language = literal.getLanguage();
            if (language != null) {
                String value = literal.getString();
                return model.createLiteral(value, language);
            }
            else {
                RDFDatatype datatype = XMLSchemaDatatypes.getXSDDatatype(literal.getDatatype());
                if (datatype == null && owlModel.getRDFXMLLiteralType().equals(literal.getDatatype())) {
                    datatype = XMLLiteralType.theXMLLiteralType;
                }
                return model.createTypedLiteral(literal.getString(), datatype);
            }
        }
        else {
            return model.createTypedLiteral(object);
        }
    }


    private Resource getResource(RDFResource rdfResource, Model model) {
        if (rdfResource.isAnonymous()) {
            AnonId anonId = new AnonId("_:" + rdfResource.getName());
            return model.createResource(anonId);
        }
        else {
            return model.getResource(rdfResource.getURI());
        }
    }


    /**
     * In several contexts, such as owl:unionOf operands, it is unusual to have
     * typed rdf:Lists.  Typeless rdf:Lists are written out as parseType="Collection".
     * This method detects these rdf:Lists and removes its rdf:type.
     *
     * @param model
     */
    public static void removeRedundantRDFLists(Model model) {
        Set properties = getParseTypeCollectionProperties();
        for (Iterator it = properties.iterator(); it.hasNext();) {
            Property property = (Property) it.next();
            Iterator objects = model.listObjectsOfProperty(property);
            while (objects.hasNext()) {
                Object object = objects.next();
                if (object instanceof Resource && ((Resource) object).hasProperty(RDF.type, RDF.List)) {
                    removeRedundantRDFListsRecursively((Resource) object);
                }
            }
        }
    }


    private static void removeRedundantRDFListsRecursively(Resource list) {
        if (!list.equals(RDF.nil)) {
            if (list.hasProperty(RDF.type, RDF.List)) {
                list.removeAll(RDF.type);
            }
            Statement statement = list.getProperty(RDF.rest);
            if (statement != null) {
                removeRedundantRDFListsRecursively((Resource) statement.getObject());
            }
        }
    }


    private void removeRedundantRDFSDomains(Model model) {
        Set subjects = Jena.set(model.listSubjectsWithProperty(RDFS.domain, OWL.Thing));
        for (Iterator it = subjects.iterator(); it.hasNext();) {
            Resource resource = (Resource) it.next();
            Iterator vit = resource.listProperties(RDFS.domain);
            vit.next();
            if (!vit.hasNext()) {  // owl:Thing is the only domain
                resource.removeAll(RDFS.domain);
            }
        }
    }


    private void removeRedundantRDFSSubClassOfOWLThings(Model model) {
        Set subjects = Jena.set(model.listSubjectsWithProperty(RDFS.subClassOf, OWL.Thing));
        for (Iterator it = subjects.iterator(); it.hasNext();) {
            Resource resource = (Resource) it.next();
            Iterator vit = resource.listProperties(RDFS.subClassOf);
            vit.next();
            if (!vit.hasNext()) {  // owl:Thing is the only superclass
                resource.removeAll(RDFS.subClassOf);
            }
        }
    }


    private void removeRedundantRDFSSubClassOfEquivalentClasses(Model model) {
        Iterator classes = model.listSubjectsWithProperty(OWL.equivalentClass);
        while (classes.hasNext()) {
            Resource ontClass = (Resource) classes.next();
            Iterator equis = Jena.cloneIt(ontClass.listProperties(OWL.equivalentClass));
            while (equis.hasNext()) {
                Resource equi = (Resource) ((Statement) equis.next()).getObject();
                if (model.contains(equi, OWL.intersectionOf)) {
                    RDFNode operandsListNode = model.listObjectsOfProperty(equi, OWL.intersectionOf).nextNode();
                    if (operandsListNode.canAs(RDFList.class)) {
                        RDFList operandsList = (RDFList) operandsListNode.as(RDFList.class);
                        Iterator operands = operandsList.iterator();
                        while (operands.hasNext()) {
                            Resource operand = (Resource) operands.next();
                            if (!operand.isAnon()) {
                                model.removeAll(ontClass, RDFS.subClassOf, operand);
                            }
                        }
                    }
                }
                else if (!equi.isAnon()) {
                    model.removeAll(ontClass, RDFS.subClassOf, equi);
                    model.removeAll(equi, RDFS.subClassOf, ontClass);
                }
            }
        }
    }


    public static void saveAll(OWLModel owlModel, URI uri) throws Exception {
        final String language = FileUtils.langXMLAbbrev;
        saveAll(owlModel, uri, language);
    }


    public static void saveAll(OWLModel owlModel, URI uri, String language) throws Exception {
        List fillTripleStores = new ArrayList();
        Iterator ts = owlModel.getTripleStoreModel().listUserTripleStores();
        fillTripleStores.add(ts.next());
        while (ts.hasNext()) {
            TripleStore tripleStore = (TripleStore) ts.next();
            String name = tripleStore.getName();
            URI ontologyName = new URI(name);
            Repository rep = owlModel.getRepositoryManager().getRepository(ontologyName);
            if (rep != null) {
                if (rep.isWritable(ontologyName)) {
                    fillTripleStores.add(tripleStore);
                }
            }
        }

        Map tripleStore2Model = new HashMap();
        OntModel ontModel = createOntModel(owlModel, fillTripleStores, tripleStore2Model);


        File file = new File(uri);
        String namespace = owlModel.getNamespaceManager().getDefaultNamespace();
        JenaOWLModel.save(file, ontModel, language, namespace);
        Iterator tripleStores = owlModel.getTripleStoreModel().listUserTripleStores();
        tripleStores.next();
        while (tripleStores.hasNext()) {
            TripleStore tripleStore = (TripleStore) tripleStores.next();
            if (fillTripleStores.contains(tripleStore)) {
                Model model = (Model) tripleStore2Model.get(tripleStore);
                String name = tripleStore.getName();
                URI ontologyName = new URI(name);
                Repository rep = owlModel.getRepositoryManager().getRepository(ontologyName);
                System.out.println("Saving import " + ontologyName + " to " +
                        rep.getOntologyLocationDescription(ontologyName));
                OutputStream os = rep.getOutputStream(ontologyName);
                JenaOWLModel.saveModel(os, model, language, ontologyName + "#");
            }
        }

        // Save the repositories as well
        RepositoryFileManager fm = new RepositoryFileManager(owlModel);
        fm.saveGlobalRepositories();
        fm.saveProjectRepositories(uri);

        System.out.println("... saving successful.");
    }
}
