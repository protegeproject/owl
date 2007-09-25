package edu.stanford.smi.protegex.owl.jena;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.ResourceUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

import java.util.*;

/**
 * A class that is able to normalize a given OntModel.
 * Normalizing makes sure that a given OWL file (edited outside of Protege) fulfills some
 * syntactic constraints.  This process does the following:
 * <UL>
 * <LI>Converts named class expressions (enumerations, boolean classes and restrictions)
 * into normal named classes with the expression as equivalent class.</LI>
 * <LI>Turns all absolute class, property and individual names into local names, so that
 * they get the default namespace.</LI>
 * <LI>Converts multiple domain statements into a single intersectionOf</LI>
 * </UL>
 * <p/>
 * This code could clearly be optimized (generalized) to better exploit the RDF structure.
 * However, we try to make use the of the Ontology API wherever possible, so that we could
 * easier copy this functionality into other OWL APIs.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaNormalizer {

    /**
     * Indicates whether logging is turned on or not
     */
    private static boolean logging = false;

    /**
     * Set this to "true" in protege.properties to enable logging
     */
    private static String LOGGING = JenaNormalizer.class.getName() + ".logging";

    private OntModel ontModel;

    private Model owlFullModel;


    public JenaNormalizer(OntModel ontModel,
                          Model owlFullModel,
                          NamespaceManager namespaceManager) {

        this.owlFullModel = owlFullModel;
        this.ontModel = ontModel;

        Jena.ensureOWLFullModelIsLastModel(ontModel, owlFullModel);

        updateLoggingStatus();

        final String defaultNamespace = namespaceManager.getDefaultNamespace();
        standardizePrefixes(ontModel);
        alignPrefixes(ontModel, namespaceManager);
        createPrefixesForImports(ontModel, namespaceManager);
        mergeOntologies();
        ensureAnnotationPropertiesHaveRange();
        if (owlFullModel != null) {
            ensureDeprecatedClassesAreOntClasses(owlFullModel);
            ensureDeprecatedPropertiesAreOntProperties(owlFullModel);
            ensureFunctionalPropertiesAreOntProperties(owlFullModel);
            ensureSymmetricPropertiesAreOntProperties(owlFullModel);
            ensureTransitivePropertiesAreOntProperties(owlFullModel);
        }

        convertCyclicPropertyInheritance();

        assignNamesToAnonymousTopLevelClasses(ontModel, defaultNamespace);
        // assignNamesToAnonymousIndividuals(ontModel, defaultNamespace);
        convertNamedClassExpressions();
        if (owlFullModel != null) {
            assignRDFTypesToMetaclassInstances(ontModel, owlFullModel);
            ensureSuperclassesAreRDFSClasses(owlFullModel);
            ensureSuperPropertiesAreRDFProperties(owlFullModel);
        }

        removeRedundantDomains(ontModel.listDatatypeProperties());
        removeRedundantDomains(ontModel.listObjectProperties());
        convertMultipleDomainsIntoIntersection(ontModel);
        convertMultipleRangesIntoIntersection(ontModel);
    }


    /**
     * Makes sure that there is a prefix for all non-anonymous owl:Ontologies.
     * Otherwise the system runs into problems with import statements
     *
     * @param ontModel
     */
    private void createPrefixesForImports(OntModel ontModel, NamespaceManager nsm) {
        String def = ontModel.getNsPrefixURI("");
        for (Iterator it = ontModel.listOntologies(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            for (Iterator iit = ontology.listImports(); iit.hasNext();) {
                Resource o = (Resource) iit.next();
                String uri = o.getURI();
                if (!uri.endsWith("/") && !uri.endsWith("#")) {
                    uri += Jena.DEFAULT_NAMESPACE_SEPARATOR;
                }
                if (!def.equals(uri)) {
                    if (ontModel.getOntology(o.getURI()) != null) {
                        String pre = ontModel.getNsURIPrefix(uri);
                        if (pre == null || pre.length() == 0) {
                            String prefix = createImportURI(ontModel);
                            ontology.getModel().setNsPrefix(prefix, uri);
                            nsm.setPrefix(uri, prefix);
                        }
                    }
                }
            }
        }
    }


    private String createImportURI(OntModel ontModel) {
        String prefix = null;
        String prefixBase = "import";
        int i = 1;
        do {
            prefix = prefixBase + i++;
        }
        while (ontModel.getNsPrefixURI(prefix) != null);
        return prefix;
    }


    private void alignPrefixes(OntModel ontModel, NamespaceManager nsm) {
        List graphs = ontModel.getSubGraphs();
        for (Iterator it = graphs.iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            Map map = graph.getPrefixMapping().getNsPrefixMap();
            for (Iterator mit = map.keySet().iterator(); mit.hasNext();) {
                String localPrefix = (String) mit.next();
                if (localPrefix.length() > 0) {
                    String uri = (String) map.get(localPrefix);
                    String globalPrefix = ontModel.getNsURIPrefix(uri);
                    if (!localPrefix.equals(globalPrefix)) {
                        log("Prefix mismatch " + localPrefix + " aligned to " + globalPrefix + " for namespace " + uri);
                        graph.getPrefixMapping().removeNsPrefix(localPrefix);
                        graph.getPrefixMapping().setNsPrefix(globalPrefix, uri);
                        nsm.setPrefix(uri, globalPrefix);
                        nsm.removePrefix(localPrefix);
                    }
                }
            }
        }
    }


    private void ensureDeprecatedClassesAreOntClasses(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listSubjectsWithProperty(RDF.type, OWL.DeprecatedClass)).iterator(); it.hasNext();) {
            Resource c = (Resource) it.next();
            if (!c.canAs(OntClass.class)) {
                ontModel.add(c, RDF.type, RDFS.Class);
                log("+ Made deprecated class " + c + " an RDFS class");
            }
        }
    }


    private void ensureDeprecatedPropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listSubjectsWithProperty(RDF.type, OWL.DeprecatedProperty)).iterator(); it.hasNext();) {
            Resource property = (Resource) it.next();
            if (!property.canAs(OntProperty.class)) {
                ontModel.add(property, RDF.type, RDF.Property);
                log("+ Made deprecated property " + property + " an RDF property");
            }
        }
    }


    private void ensureFunctionalPropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listFunctionalProperties()).iterator(); it.hasNext();) {
            FunctionalProperty property = (FunctionalProperty) it.next();
            if (!hasValidPropertyType(property)) {
                ontModel.add(property, RDF.type, RDF.Property);
                log("+ Made functional property " + property + " an RDF property");
            }
        }
    }


    private boolean hasValidPropertyType(Property property) {
        for (StmtIterator it = property.listProperties(RDF.type); it.hasNext();) {
            Statement s = it.nextStatement();
            Resource object = (Resource) s.getObject();
            if (object.equals(OWL.ObjectProperty) ||
                    object.equals(OWL.DatatypeProperty) ||
                    object.equals(RDF.Property) ||
                    !object.getURI().startsWith(OWL.NS)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Needed for pure RDF files that define subclasses of classes that are
     * not imported.  This method will make these superclasses rdfs:Classes.
     *
     * @param owlFullModel the Model to write the additional rdf:type triples to
     */
    private void ensureSuperclassesAreRDFSClasses(Model owlFullModel) {
        for (Iterator it = ontModel.listObjectsOfProperty(RDFS.subClassOf); it.hasNext();) {
            RDFNode node = (RDFNode) it.next();
            if (!Jena.canAsOntClass(node)) {
                if (node.canAs(Resource.class)) {
                    owlFullModel.add((Resource) node.as(Resource.class), RDF.type, RDFS.Class);
                    log("+ Made non-imported superclass " + node + " an " + RDFS.Class);
                }
            }
        }
    }


    /**
     * Needed for pure RDF files that define sub-properties of properties that are
     * not imported.  This method will make these superclasses rdf:Property.
     *
     * @param owlFullModel the Model to write the additional rdf:type triples to
     */
    private void ensureSuperPropertiesAreRDFProperties(Model owlFullModel) {
        for (Iterator it = ontModel.listObjectsOfProperty(RDFS.subPropertyOf); it.hasNext();) {
            RDFNode node = (RDFNode) it.next();
            if (!node.canAs(OntProperty.class)) {
                if (node.canAs(Resource.class)) {
                    owlFullModel.add((Resource) node.as(Resource.class), RDF.type, RDF.Property);
                    log("+ Made non-imported superclass " + node + " an " + RDF.Property);
                }
            }
        }
    }


    private void ensureSymmetricPropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listSymmetricProperties()).iterator(); it.hasNext();) {
            SymmetricProperty property = (SymmetricProperty) it.next();
            if (!hasValidPropertyType(property)) {
                ontModel.add(property, RDF.type, OWL.ObjectProperty);
                log("+ Made symmetric property " + property + " an object property");
            }
        }
    }


    private void ensureTransitivePropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listTransitiveProperties()).iterator(); it.hasNext();) {
            TransitiveProperty property = (TransitiveProperty) it.next();
            if (!hasValidPropertyType(property)) {
                ontModel.add(property, RDF.type, OWL.ObjectProperty);
                log("+ Made transitive property " + property + " an object property");
            }
        }
    }


    private void assignNamesToAnonymousIndividuals(OntModel ontModel, String defaultNamespace) {
        String baseName = defaultNamespace + AbstractOWLModel.ANONYMOUS_BASE;
        int index = 1;
        for (Iterator it = Jena.cloneIt(ontModel.listIndividuals()); it.hasNext();) {
            Individual individual = (Individual) it.next();
            if (individual.getURI() == null) {
                Resource type = individual.getRDFType();
                String ns = type.getNameSpace();
                if (!ns.equals(OWL.NS) &&
                        !type.equals(RDF.List) &&
                        !ns.equals(RDFS.getURI().toString()) &&
                        !ns.equals(RDF.getURI().toString())) {
                    if (!type.canAs(OntClass.class) ||
                            !((OntClass) type.as(OntClass.class)).hasSuperClass(RDF.List)) {
                        String name;
                        do {
                            name = baseName + index++;
                        }
                        while (ontModel.getIndividual(name) != null);
                        Jena.renameResource(ontModel, individual, name);
                        log("* Assigned name " + name + " to anonymous individual " + individual);
                    }
                }
            }
        }
    }


    private void assignNamesToAnonymousTopLevelClasses(OntModel ontModel, String defaultNamespace) {
        String baseName = defaultNamespace + AbstractOWLModel.ANONYMOUS_BASE;
        int index = 1;
        for (Iterator it = Jena.set(listAnonTopLevelClasses(ontModel)).iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            String name = baseName + index++;
            ResourceUtils.renameResource(ontClass, name);
            log("* Assigned name " + name + " to anonymous top-level class " + ontClass);
        }
    }


    public static void assignRDFTypesToMetaclassInstances(OntModel ontModel, Model owlFullModel) {
        int i = 0;
        boolean repeat = false;
        do {
            final Set individuals = Jena.set(ontModel.listIndividuals());
            repeat = false;
            for (Iterator it = individuals.iterator(); it.hasNext();) {
                Individual individual = (Individual) it.next();
                if (++i % 100 == 0) {
                    log("- Assigning RDF types to individual " + i + " of " + individuals.size());
                }
                if (Jena.canAsOntClass(individual)) {
                    repeat = ensureRDFType(individual, OWL.Class, owlFullModel) || repeat;
                }
                else if (Jena.canAsObjectProperty(individual)) {
                    repeat = ensureRDFType(individual, OWL.ObjectProperty, owlFullModel) || repeat;
                }
                else if (Jena.canAsDatatypeProperty(individual)) {
                    repeat = ensureRDFType(individual, OWL.DatatypeProperty, owlFullModel) || repeat;
                }
            }
        }
        while (repeat);
    }


    private void convertCyclicPropertyInheritance() {
        List errors = new ArrayList();
        convertCyclicPropertyInheritance(ontModel.listDatatypeProperties(), errors);
        convertCyclicPropertyInheritance(ontModel.listObjectProperties(), errors);
        if (errors.size() > 0) {
            for (Iterator eit = errors.iterator(); eit.hasNext();) {
                Object o = eit.next();
                System.err.println("[JenaNormalizer] " + o);
            }
        }
    }


    private void convertCyclicPropertyInheritance(ExtendedIterator it, List errors) {
        while (it.hasNext()) {
            OntProperty ontProperty = (OntProperty) it.next();
            try {
                convertCyclicPropertyInheritance(ontProperty, ontProperty, new HashSet(), errors);
            }
            catch (Throwable t) {
                System.out.println("Can't convertCyclicPropertyInheritance for "
                        + ontProperty.getLocalName());
            }
        }
    }


    private void convertCyclicPropertyInheritance(OntProperty start,
                                                  OntProperty ontProperty,
                                                  Set visited, Collection errors) {
        visited.add(ontProperty);
        for (Iterator it = ontProperty.listSuperProperties(true); it.hasNext();) {
            OntProperty superProperty = (OntProperty) it.next();
            if (visited.contains(superProperty)) {
                errors.add("Error: Property " + ontProperty +
                        " has a cyclic superproperty relation with " + superProperty +
                        " - please use owl:equivalentProperty instead!");
            }
            else {
                Set newSet = new HashSet(visited);
                convertCyclicPropertyInheritance(start, superProperty, newSet, errors);
            }
        }
    }


    private void convertMultipleDomainsIntoIntersection(OntModel ontModel) {
        convertMultipleDomainsIntoIntersection(ontModel.listDatatypeProperties());
        convertMultipleDomainsIntoIntersection(ontModel.listObjectProperties());
    }


    private void convertMultipleDomainsIntoIntersection(Iterator ontProperties) {
        while (ontProperties.hasNext()) {
            OntProperty ontProperty = (OntProperty) ontProperties.next();
            Set domainSet = Jena.set(ontProperty.listDomain());
            if (domainSet.size() > 1) {
                log("* Converted multiple domains for " + ontProperty.getLocalName() +
                        " into an intersection class");
                RDFList list = ontModel.createList(domainSet.iterator());
                IntersectionClass intersectionClass = ontModel.createIntersectionClass(null, list);
                ontProperty.setDomain(intersectionClass);
            }
        }
    }


    private void convertMultipleRangesIntoIntersection(OntModel ontModel) {
        // convertMultipleRangesIntoIntersection(ontModel.listDatatypeProperties());
        convertMultipleRangesIntoIntersection(ontModel.listObjectProperties());
    }


    private void convertMultipleRangesIntoIntersection(Iterator ontProperties) {
        while (ontProperties.hasNext()) {
            OntProperty ontProperty = (OntProperty) ontProperties.next();
            Set rangeSet = Jena.set(ontProperty.listRange());
            if (rangeSet.size() > 1) {
                RDFList list = ontModel.createList(rangeSet.iterator());
                IntersectionClass intersectionClass = ontModel.createIntersectionClass(null, list);
                ontProperty.setRange(intersectionClass);
                log("* Converted multiple ranges for " + ontProperty.getLocalName() +
                        " into an intersection class");
            }
        }
    }


    private void convertNamedClassExpressions() {
        Collection set = Jena.set(ontModel.listNamedClasses());
        for (Iterator it = set.iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            if (Jena.isSystemResource(ontClass)) {
                continue;
            }
            OntClass equivalentClass = null;
            if (ontClass.isComplementClass()) {
                equivalentClass = createComplementClass(ontClass.asComplementClass());
            }
            else if (ontClass.isEnumeratedClass()) {
                equivalentClass = createEnumeratedClass(ontClass); //.asEnumeratedClass());
            }
            else if (ontClass.isIntersectionClass()) {
                equivalentClass = createIntersectionClass(ontClass.asIntersectionClass());
            }
            else if (ontClass.isUnionClass()) {
                equivalentClass = createUnionClass(ontClass.asUnionClass());
            }
            else if (ontClass.isRestriction()) {
                equivalentClass = createRestriction(ontClass.asRestriction());
            }

            if (equivalentClass != null) {
                if (Jena.isImportedResource(ontModel, owlFullModel, ontClass)) {
                    Graph homeGraph = Jena.getHomeGraph(ontModel, ontClass);
                    homeGraph.add(new Triple(ontClass.asNode(), OWL.equivalentClass.getNode(), equivalentClass.getNode()));
                    for (Iterator ss = Jena.set(equivalentClass.listProperties()).iterator(); ss.hasNext();) {
                        Statement statement = (Statement) ss.next();
                        ontModel.remove(statement);
                        Node subject = statement.getSubject().getNode();
                        Node predicate = statement.getPredicate().getNode();
                        Node object = statement.getObject() == null ? null : statement.getObject().asNode();
                        Triple triple = new Triple(subject, predicate, object);
                        homeGraph.add(triple);
                        log("  * Moved triple into imported graph: " + triple);
                    }
                }
                else {
                    ontClass.addEquivalentClass(equivalentClass);
                }
            }
        }
    }


    private AllValuesFromRestriction createAllValuesFromRestriction(AllValuesFromRestriction base) {
        log("+ Adding anonymous AllValuesFromRestriction for " + base);
        AllValuesFromRestriction result = ontModel.createAllValuesFromRestriction(null, base.getOnProperty(), base.getAllValuesFrom());
        base.removeAll(ontModel.getProfile().ALL_VALUES_FROM());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private CardinalityRestriction createCardinalityRestriction(CardinalityRestriction base) {
        log("+ Adding anonymous OWLCardinalityBase for " + base);
        CardinalityRestriction result = ontModel.createCardinalityRestriction(null, base.getOnProperty(), base.getCardinality());
        base.removeAll(ontModel.getProfile().CARDINALITY());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private ComplementClass createComplementClass(ComplementClass base) {
        log("+ Adding anonymous ComplementClass for " + base);
        OntResource operand = base.getOperand();
        base.removeAll(ontModel.getProfile().COMPLEMENT_OF());
        return ontModel.createComplementClass(null, operand);
    }


    private EnumeratedClass createEnumeratedClass(OntClass enumeratedClass) {
        log("+ Adding anonymous EnumeratedClass for " + enumeratedClass);
        Property oneOfProperty = ontModel.getProperty(OWL.oneOf.getURI());
        Statement s = enumeratedClass.getProperty(oneOfProperty);
        RDFList members = (RDFList) s.getObject().as(RDFList.class);
        ontModel.remove(s);
        return ontModel.createEnumeratedClass(null, members);
    }


    private HasValueRestriction createHasValueRestriction(HasValueRestriction base) {
        log("+ Adding anonymous HasValueRestriction for " + base);
        HasValueRestriction result =
                ontModel.createHasValueRestriction(null, base.getOnProperty(), base.getHasValue());
        base.removeAll(ontModel.getProfile().HAS_VALUE());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private IntersectionClass createIntersectionClass(IntersectionClass intersectionClass) {
        log("+ Adding anonymous IntersectionClass for " + intersectionClass);
        RDFList operands = intersectionClass.getOperands();
        IntersectionClass result = ontModel.createIntersectionClass(null, operands);
        intersectionClass.removeAll(ontModel.getProfile().INTERSECTION_OF());
        return result;
    }


    private MaxCardinalityRestriction createMaxCardinalityRestriction(MaxCardinalityRestriction base) {
        log("+ Adding anonymous MaxCardinalityRestriction for " + base);
        MaxCardinalityRestriction result = ontModel.createMaxCardinalityRestriction(null, base.getOnProperty(), base.getMaxCardinality());
        base.removeAll(ontModel.getProfile().CARDINALITY());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private MinCardinalityRestriction createMinCardinalityRestriction(MinCardinalityRestriction base) {
        log("+ Adding anonymous MinCardinalityRestriction for " + base);
        MinCardinalityRestriction result = ontModel.createMinCardinalityRestriction(null, base.getOnProperty(), base.getMinCardinality());
        base.removeAll(ontModel.getProfile().CARDINALITY());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private Restriction createRestriction(Restriction restriction) {
        if (restriction.isAllValuesFromRestriction()) {
            return createAllValuesFromRestriction(restriction.asAllValuesFromRestriction());
        }
        else if (restriction.isSomeValuesFromRestriction()) {
            return createSomeValuesFromRestriction(restriction.asSomeValuesFromRestriction());
        }
        else if (restriction.isHasValueRestriction()) {
            return createHasValueRestriction(restriction.asHasValueRestriction());
        }
        else if (restriction.isMaxCardinalityRestriction()) {
            return createMaxCardinalityRestriction(restriction.asMaxCardinalityRestriction());
        }
        else if (restriction.isMinCardinalityRestriction()) {
            return createMinCardinalityRestriction(restriction.asMinCardinalityRestriction());
        }
        else if (restriction.isCardinalityRestriction()) {
            return createCardinalityRestriction(restriction.asCardinalityRestriction());
        }
        return null;
    }


    private SomeValuesFromRestriction createSomeValuesFromRestriction(SomeValuesFromRestriction base) {
        log("+ Adding anonymous SomeValuesFromRestriction for " + base);
        SomeValuesFromRestriction result =
                ontModel.createSomeValuesFromRestriction(null, base.getOnProperty(), base.getSomeValuesFrom());
        base.removeAll(ontModel.getProfile().SOME_VALUES_FROM());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private UnionClass createUnionClass(UnionClass unionClass) {
        log("+ Adding anonymous UnionClass for " + unionClass);
        RDFList operands = unionClass.getOperands();
        unionClass.removeAll(ontModel.getProfile().UNION_OF());
        return ontModel.createUnionClass(null, operands);
    }


    private void ensureAnnotationPropertiesHaveRange() {
        for (Iterator it = Jena.set(ontModel.listAnnotationProperties()).iterator(); it.hasNext();) {
            AnnotationProperty annotationProperty = (AnnotationProperty) it.next();
            if (!annotationProperty.canAs(DatatypeProperty.class) &&
                    !annotationProperty.canAs(ObjectProperty.class)) {
                String ns = annotationProperty.getNameSpace();
                if (!ns.equals(OWL.NS) && !ns.equals(RDFS.getURI())) {
                    Graph graph = ontModel.getGraph();
                    if (Jena.isImportedResource(ontModel, owlFullModel, annotationProperty)) {
                        graph = getHomeGraph(annotationProperty);
                    }
                    graph.add(new Triple(annotationProperty.getNode(), RDF.type.getNode(), OWL.DatatypeProperty.getNode()));
                    XSDDatatype datatype = XSDDatatype.XSDstring;
                    if (annotationProperty.getURI().equals(ProtegeNames.NS + ProtegeNames.READ_ONLY)) {
                        datatype = XSDDatatype.XSDboolean;
                    }
                    graph.add(new Triple(annotationProperty.getNode(), RDFS.range.getNode(), ontModel.getResource(datatype.getURI()).getNode()));
                    log("Made annotationProperty " + annotationProperty.getURI() + " a datatype property with range " + datatype);
                }
            }
        }
    }


    private static boolean ensureRDFType(OntResource individual, Resource type, Model owlFullModel) {
        if (!individual.hasRDFType(type)) {
            owlFullModel.add(individual, RDF.type, type);
            log("+ Added " + type + " to rdf:types of " + individual + " in OWL Full model");
            return true;
        }
        else {
            return false;
        }
    }


    private Graph getHomeGraph(OntResource resource) {
        Property predicate = RDF.type;
        Resource object = resource.getRDFType();
        for (Iterator it = ontModel.getSubGraphs().iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            if (graph.contains(resource.getNode(), predicate.getNode(), object.getNode())) {
                return graph;
            }
        }
        return null;
    }


    public static Iterator listAnonTopLevelClasses(final OntModel m) {
        final Set objects = Jena.set(m.listObjects());
        return m.listClasses().filterDrop(new Filter() {
            public boolean accept(Object x) {
                OntClass c = (OntClass) x;
                return !c.isAnon() || objects.contains(c);
            }
        });
    }


    private static void log(String message) {
        if (logging) {
            System.out.println("[JenaNormalizer]  " + message);
        }
    }


    private void mergeOntologies() {
        for (Iterator it = ontModel.listOntologies(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            if (ontology.getURI() == null) {
                System.err.println("Warning: Ontology without URI detected: The correct syntax is <owl:Ontology rdf:about=\"\"> and there should be a default namespace");
            }
        }

        // TODO: Move own slot values of namesake ontologies into a single one
    }


    private void removeRedundantDomains(Iterator it) {
        while (it.hasNext()) {
            OntProperty ontProperty = (OntProperty) it.next();
            Collection domain = Jena.set(ontProperty.listDomain());
            if (domain.contains(OWL.Thing)) {
                ontProperty.removeAll(RDFS.domain);
                log("- Removed redundant domain from property " + ontProperty);
            }
        }
    }


    private static Resource setResourceNamespace(Resource resource, String newNamespace) {
        String cmp = newNamespace + "#";
        if (!Jena.isSystemResource(resource) && !cmp.equals(resource.getNameSpace())) {
            String localName = resource.getLocalName();
            if (!AbstractOWLModel.isValidOWLFrameName(null, localName)) {
                localName = "_" + localName;
            }
            final String newURI = (Jena.isNamespaceWithSeparator(newNamespace) ? newNamespace : cmp) + localName;
            log("* Namespacing " + resource.getURI() + " to " + newURI);
            return ResourceUtils.renameResource(resource, newURI);
        }
        else {
            return null;
        }
    }


    public static void standardizePrefixes(OntModel ontModel) {
        ontModel.setNsPrefix("owl", OWL.NS);
    }


    public static void unifyNamespace(OntModel ontModel, String oldNamespace, String newNamespace) {
        if (!oldNamespace.equals(newNamespace)) {
            unifyNamespaceOfOntologies(ontModel, oldNamespace, newNamespace);
            unifyNamespaceOfProperties(ontModel, oldNamespace, newNamespace);
            unifyNamespaceOfNamedClasses(ontModel, oldNamespace, newNamespace);
            unifyNamespaceOfIndividuals(ontModel, oldNamespace, newNamespace);
        }
    }


    private static void unifyNamespaceOfIndividuals(OntModel ontModel,
                                                    String oldNamespace, String newNamespace) {
        Collection individuals = Jena.set(ontModel.listIndividuals());
        unifyNamespaceOfResources(individuals.iterator(), oldNamespace, newNamespace);
        unifyNamespaceOfResources(ontModel.listObjects(), oldNamespace, newNamespace);
    }


    private static void unifyNamespaceOfResources(Iterator it, String oldNamespace, String newNamespace) {
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof Resource && ((Resource) next).canAs(Individual.class)) {
                Individual resource = (Individual) ((Resource) next).as(Individual.class);
                final String namespace = resource.getNameSpace();
                if (namespace != null && (oldNamespace == null || namespace.equals(oldNamespace))) {
                    setResourceNamespace(resource, newNamespace);
                }
            }
        }
    }


    private static void unifyNamespaceOfNamedClasses(OntModel ontModel,
                                                     String oldNamespace, String newNamespace) {
        Collection namedClasses = Jena.set(ontModel.listNamedClasses());
        for (Iterator it = namedClasses.iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            if (oldNamespace == null || ontClass.getNameSpace().equals(oldNamespace)) {
                setResourceNamespace(ontClass, newNamespace);
            }
        }
    }


    private static void unifyNamespaceOfOntologies(OntModel ontModel, String oldNamespace, String newNamespace) {
        Collection ontologies = Jena.set(ontModel.listOntologies());
        for (Iterator it = ontologies.iterator(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            String uri = ontology.getURI();
            if (uri != null) {
                String ontologyNS = Jena.isNamespaceWithSeparator(uri) ?
                        uri : uri + Jena.DEFAULT_NAMESPACE_SEPARATOR;
                if (ontologyNS.equals(oldNamespace)) {
                    String newURI = newNamespace;
                    if (newURI.endsWith(Jena.DEFAULT_NAMESPACE_SEPARATOR)) {
                        newURI = newNamespace.substring(0, newNamespace.length() - 1);
                    }
                    log("* Renaming ontology " + ontology.getURI() + " into namespace " + newURI);
                    ResourceUtils.renameResource(ontology, newURI);
                }
            }
        }
    }


    private static void unifyNamespaceOfProperties(OntModel ontModel,
                                                   String oldNamespace, String newNamespace) {
        Set properties = Jena.set(ontModel.listDatatypeProperties());
        Jena.set(properties, ontModel.listObjectProperties());
        Jena.set(properties, ontModel.listAnnotationProperties());
        for (Iterator it = properties.iterator(); it.hasNext();) {
            Property ontProperty = (Property) it.next();
            if (oldNamespace == null || ontProperty.getNameSpace().equals(oldNamespace)) {
                Resource renamed = setResourceNamespace(ontProperty, newNamespace);
                if (renamed != null) {
                    Property newProperty = (Property) renamed.as(Property.class);
                    Collection statements = Jena.set(ontModel.listStatements(null, ontProperty, (RDFNode) null));
                    for (Iterator sit = statements.iterator(); sit.hasNext();) {
                        Statement s = (Statement) sit.next();
                        s.remove();
                        ontModel.add(s.getSubject(), newProperty, s.getObject());
                        log("* Changed statement (" + s.getSubject() + ", " + newProperty + ", " + s.getObject());
                    }
                }
            }
        }
    }


    private static void updateLoggingStatus() {
        String str = ApplicationProperties.getString(LOGGING, "false");
        logging = str != null && str.equalsIgnoreCase("true");
    }
}
