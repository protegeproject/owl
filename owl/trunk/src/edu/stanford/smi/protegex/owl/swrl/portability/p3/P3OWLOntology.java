
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.ArgumentFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.DataPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValueFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLPropertyPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.bridge.ObjectPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.PropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidPropertyNameException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.OWLDataValueImpl;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.SWRLClassAtomImpl;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDAnyURI;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDDate;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDDateTime;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDDuration;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDTime;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSameIndividualAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSomeValuesFromReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSubClassAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLBuiltInAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLClassAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLDataPropertyAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLDataRangeAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLDifferentIndividualsAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLIndividualArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLObjectPropertyAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLSameIndividualAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLVariableReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.DataValueImpl;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;

/**
 * Class to convert between OWLAPI-like entity references and Protege-OWL entities.
 */
public class P3OWLOntology implements OWLOntology
{
	private OWLModel owlModel;
	private SWRLFactory swrlFactory;
	private OWLDataFactory owlFactory;
	private OWLDataValueFactory owlDataValueFactory;
	private ArgumentFactory argumentFactory;
	private Map<String, OWLClassReference> classes;
	private Map<String, OWLObjectPropertyReference> objectProperties;
	private Map<String, OWLDataPropertyReference> dataProperties;
	private Map<String, OWLNamedIndividualReference> individuals;

	public P3OWLOntology(OWLModel owlModel)
	{
		this.owlModel = owlModel;
		this.owlFactory = new P3OWLDataFactory();
		swrlFactory = new SWRLFactory(owlModel);

		argumentFactory = ArgumentFactory.getFactory();

		classes = new HashMap<String, OWLClassReference>();
		objectProperties = new HashMap<String, OWLObjectPropertyReference>();
		dataProperties = new HashMap<String, OWLDataPropertyReference>();
		individuals = new HashMap<String, OWLNamedIndividualReference>();
	}

	public boolean containsClassInSignature(String classURI, boolean includesImportsClosure)
	{
		return SWRLOWLUtil.isOWLClass(owlModel, classURI);
	}

	public boolean containsObjectPropertyInSignature(String propertyURI, boolean includesImportsClosure)
	{
		return SWRLOWLUtil.isOWLObjectProperty(owlModel, propertyURI);
	}

	public boolean containsDataPropertyInSignature(String propertyURI, boolean includesImportsClosure)
	{
		return SWRLOWLUtil.isOWLDataProperty(owlModel, propertyURI);
	}

	public boolean containsIndividualInSignature(String individualURI, boolean includesImportsClosure)
	{
		return SWRLOWLUtil.isOWLIndividual(owlModel, individualURI);
	}

	public boolean isOWLNamedIndividualOfClass(String individualURI, String classURI)
	{
		return SWRLOWLUtil.isOWLIndividualOfType(owlModel, individualURI, classURI);
	}

	public boolean isSWRLBuiltIn(String builtInURI)
	{
		return SWRLOWLUtil.isSWRLBuiltIn(owlModel, builtInURI);
	}

	public String createNewResourceURI(String prefix)
	{
		return SWRLOWLUtil.createNewResourceName(owlModel, prefix);
	}

	public Set<SWRLRuleReference> getSWRLRules() throws OWLConversionFactoryException, SQWRLException, BuiltInException
	{
		Collection<SWRLImp> p3SWRLRules = swrlFactory.getImps();
		Set<SWRLRuleReference> swrlRules = new HashSet<SWRLRuleReference>();

		for (SWRLImp p3SWRLRule : p3SWRLRules) {
			if (p3SWRLRule.isEnabled()) {
				SWRLRuleReference rule = getSWRLRule(p3SWRLRule.getName());
				swrlRules.add(rule);
			}
		}

		return swrlRules;
	}

	public SWRLRuleReference createSWRLRule(String ruleName, String ruleText) throws OWLConversionFactoryException, SWRLParseException
	{
		swrlFactory.createImp(ruleName, ruleText);
		return getSWRLRule(ruleName);
	}

	public SWRLRuleReference getSWRLRule(String ruleName) throws OWLConversionFactoryException
	{
		List<SWRLAtomReference> bodyAtoms = new ArrayList<SWRLAtomReference>();
		List<SWRLAtomReference> headAtoms = new ArrayList<SWRLAtomReference>();
		SWRLImp p3SWRLRule = swrlFactory.getImp(ruleName);

		if (p3SWRLRule == null)
			throw new OWLConversionFactoryException("invalid rule name: " + ruleName);

		if (p3SWRLRule.getBody() != null && p3SWRLRule.getBody().getValues() != null && !p3SWRLRule.getBody().getValues().isEmpty()) {
			Iterator<?> p3AtomIterator = p3SWRLRule.getBody().getValues().iterator();
			while (p3AtomIterator.hasNext()) {
				SWRLAtom p3SWRLAtom = (SWRLAtom)p3AtomIterator.next();
				bodyAtoms.add(convertSWRLAtom(p3SWRLAtom));
			}
		}

		if (p3SWRLRule.getHead() != null && p3SWRLRule.getHead().getValues() != null && !p3SWRLRule.getHead().getValues().isEmpty()) {
			Iterator<?> p3AtomIterator = p3SWRLRule.getHead().getValues().iterator();
			while (p3AtomIterator.hasNext()) {
				SWRLAtom p3SWRLAtom = (SWRLAtom)p3AtomIterator.next();
				headAtoms.add(convertSWRLAtom(p3SWRLAtom));
			}
		}

		return new P3SWRLRuleReference(p3SWRLRule.getPrefixedName(), bodyAtoms, headAtoms);
	}

	public void deleteSWRLRule(String ruleURI) throws OWLConversionFactoryException
	{
		swrlFactory.deleteImp(ruleURI);
	}

	public OWLClassReference createOWLClass()
	{
		String anonymousURI = SWRLOWLUtil.getNextAnonymousResourceName(owlModel);

		return new P3OWLClassReference(anonymousURI);
	}

	public OWLClassReference getOWLClass(String classURI) throws OWLConversionFactoryException
	{
		P3OWLClassReference owlClassImpl;

		if (classes.containsKey(classURI))
			return classes.get(classURI);
		else {
			OWLNamedClass p3OWLNamedClass = SWRLOWLUtil.createOWLNamedClass(owlModel, classURI);
			owlClassImpl = new P3OWLClassReference(classURI);
			classes.put(classURI, owlClassImpl);

			if (!classURI.equals(OWLNames.Cls.THING)) {
				for (String superClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(p3OWLNamedClass.getNamedSuperclasses(false)))
					owlClassImpl.addSuperClass(getOWLClass(superClassURI));
				for (String subClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(p3OWLNamedClass.getNamedSubclasses(false)))
					owlClassImpl.addSubClass(getOWLClass(subClassURI));
				for (String equivalentClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassNames(p3OWLNamedClass.getEquivalentClasses()))
					owlClassImpl.addEquivalentClass(getOWLClass(equivalentClassURI));

			}
		}

		return owlClassImpl;
	}

	public OWLNamedIndividualReference getOWLIndividual(String individualURI) throws OWLConversionFactoryException
	{
		P3OWLNamedIndividualReference owlIndividual;

		if (individuals.containsKey(individualURI))
			return individuals.get(individualURI);
		else {
			OWLIndividual p3OWLIndividual = SWRLOWLUtil.createOWLIndividual(owlModel, individualURI);
			owlIndividual = new P3OWLNamedIndividualReference(individualURI);
			individuals.put(individualURI, owlIndividual);

			buildDefiningClasses(owlIndividual, p3OWLIndividual);
			buildSameAsIndividuals(owlIndividual, p3OWLIndividual);
			buildDifferentFromIndividuals(owlIndividual, p3OWLIndividual);
		}
		return owlIndividual;
	}

	public OWLObjectPropertyReference getOWLObjectProperty(String propertyURI) throws OWLConversionFactoryException
	{
		P3OWLObjectPropertyReference property;

		if (objectProperties.containsKey(propertyURI))
			return objectProperties.get(propertyURI);
		else {
			OWLObjectProperty p3OWLObjectProperty = SWRLOWLUtil.createOWLObjectProperty(owlModel, propertyURI);
			property = new P3OWLObjectPropertyReference(propertyURI);
			objectProperties.put(propertyURI, property);

			initializeProperty(property, p3OWLObjectProperty);
		}

		return property;
	}

	public OWLDataPropertyReference getOWLDataProperty(String propertyURI) throws OWLConversionFactoryException
	{
		P3OWLDataPropertyReference owlDataProperty;

		if (dataProperties.containsKey(propertyURI))
			return dataProperties.get(propertyURI);
		else {
			OWLDatatypeProperty p3OWLDataProperty = SWRLOWLUtil.createOWLDatatypeProperty(owlModel, propertyURI);
			owlDataProperty = new P3OWLDataPropertyReference(propertyURI);
			dataProperties.put(propertyURI, owlDataProperty);

			initializeProperty(owlDataProperty, p3OWLDataProperty);
		}

		return owlDataProperty;
	}

	public void writeOWLClassDeclaration(OWLClassReference cls) throws OWLConversionFactoryException
	{
		String classURI = cls.getURI();
		OWLClass p3OWLClass, p3OWLSuperclass;

		if (SWRLOWLUtil.isOWLNamedClass(owlModel, classURI))
			p3OWLClass = SWRLOWLUtil.getOWLNamedClass(owlModel, classURI);
		else
			p3OWLClass = SWRLOWLUtil.getOWLNamedClass(owlModel, classURI);

		for (OWLClassReference superClass : cls.getSuperClasses()) {
			String superClassURI = superClass.getURI();
			if (SWRLOWLUtil.isOWLNamedClass(owlModel, superClassURI))
				p3OWLSuperclass = SWRLOWLUtil.getOWLNamedClass(owlModel, superClassURI);
			else
				p3OWLSuperclass = SWRLOWLUtil.getOWLNamedClass(owlModel, superClassURI);

			if (!p3OWLClass.isSubclassOf(p3OWLSuperclass))
				p3OWLClass.addSuperclass(p3OWLSuperclass);
		}
	}

	public void writeOWLIndividualDeclaration(OWLNamedIndividualReference individual) throws OWLConversionFactoryException
	{
		String individualURI = individual.getURI();
		OWLIndividual p3OWLIndividual;

		if (SWRLOWLUtil.isIndividual(owlModel, individualURI))
			p3OWLIndividual = SWRLOWLUtil.getIndividual(owlModel, individualURI);
		else
			p3OWLIndividual = SWRLOWLUtil.createIndividual(owlModel, individualURI);

		for (OWLClassReference owlClass : individual.getTypes()) {
			OWLNamedClass p3OWLNamedClass = SWRLOWLUtil.getOWLNamedClass(owlModel, owlClass.getURI());

			if (!p3OWLIndividual.hasRDFType(p3OWLNamedClass)) {
				if (p3OWLIndividual.hasRDFType(SWRLOWLUtil.getOWLThingClass(owlModel)))
					p3OWLIndividual.setRDFType(p3OWLNamedClass);
				else
					p3OWLIndividual.addRDFType(p3OWLNamedClass);
			}
		}
	}

	public void writeOWLAxiom(OWLAxiomReference axiom) throws OWLConversionFactoryException
	{
		if (axiom instanceof OWLClassAssertionAxiomReference)
			write2OWLModel((OWLClassAssertionAxiomReference)axiom);
		else if (axiom instanceof OWLClassPropertyAssertionAxiomReference)
			write2OWLModel((OWLClassPropertyAssertionAxiomReference)axiom);
		else if (axiom instanceof OWLDataPropertyAssertionAxiomReference)
			write2OWLModel((OWLDataPropertyAssertionAxiomReference)axiom);
		else if (axiom instanceof OWLObjectPropertyAssertionAxiomReference)
			write2OWLModel((OWLObjectPropertyAssertionAxiomReference)axiom);
		else if (axiom instanceof OWLPropertyPropertyAssertionAxiomReference)
			write2OWLModel((OWLPropertyPropertyAssertionAxiomReference)axiom);
		else if (axiom instanceof OWLSomeValuesFromReference)
			write2OWLModel((OWLSomeValuesFromReference)axiom);
		else if (axiom instanceof OWLSubClassAxiomReference)
			write2OWLModel((OWLSubClassAxiomReference)axiom);
		else
			throw new OWLConversionFactoryException("unsupported OWL axiom: " + axiom);
	}

	public boolean isValidURI(String uri)
	{
		return SWRLOWLUtil.isValidURI(uri);
	}

	public Set<OWLNamedIndividualReference> getAllOWLIndividualsOfClass(String classURI) throws OWLConversionFactoryException
	{
		RDFSClass p3RDFSNamedClass = SWRLOWLUtil.getRDFSNamedClass(owlModel, classURI);
		Set<OWLNamedIndividualReference> individuals = new HashSet<OWLNamedIndividualReference>();

		if (p3RDFSNamedClass != null) {
			Iterator<?> p3OWLIndividualIterator = p3RDFSNamedClass.getInstances(true).iterator();
			while (p3OWLIndividualIterator.hasNext()) {
				Object o = p3OWLIndividualIterator.next();
				if (o instanceof OWLIndividual) {
					OWLIndividual p3OWLndividual = (OWLIndividual)o;
					individuals.add(getOWLIndividual(p3OWLndividual.getURI()));
				}
			}
		}
		return individuals;
	}

	public boolean isOWLNamedClass(String classURI)
	{
		RDFResource p3RDFResource = SWRLOWLUtil.getRDFResource(owlModel, classURI);

		return (p3RDFResource == null || p3RDFResource instanceof OWLNamedClass);
	}

	public String uri2PrefixedName(String uri)
	{
		return NamespaceUtil.getPrefixedName(owlModel, uri);
	}

	public String prefixedName2URI(String prefixedName)
	{
		return NamespaceUtil.getFullName(owlModel, prefixedName);
	}

	public static SWRLLiteralArgumentReference convertRDFSLiteral2DataValueArgument(OWLModel owlModel, RDFSLiteral literal) throws OWLConversionFactoryException
	{
		RDFSDatatype p3RDFSDatatype = literal.getDatatype();
		SWRLLiteralArgumentReference dataValueArgument = null;

		try {
			if ((p3RDFSDatatype == owlModel.getXSDint()) || (p3RDFSDatatype == owlModel.getXSDinteger()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getInt()));
			else if (p3RDFSDatatype == owlModel.getXSDshort())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getShort()));
			else if (p3RDFSDatatype == owlModel.getXSDlong())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getLong()));
			else if (p3RDFSDatatype == owlModel.getXSDboolean())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getBoolean()));
			else if (p3RDFSDatatype == owlModel.getXSDfloat())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getFloat()));
			else if (p3RDFSDatatype == owlModel.getXSDdouble())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getDouble()));
			else if ((p3RDFSDatatype == owlModel.getXSDstring()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getString()));
			else if ((p3RDFSDatatype == owlModel.getXSDtime()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDTime(literal.getString())));
			else if ((p3RDFSDatatype == owlModel.getXSDanyURI()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDAnyURI(literal.getString())));
			else if ((p3RDFSDatatype == owlModel.getXSDbyte()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(Byte.valueOf(literal.getString())));
			else if ((p3RDFSDatatype == owlModel.getXSDduration()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDDuration(literal.getString())));
			else if ((p3RDFSDatatype == owlModel.getXSDdateTime()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDDateTime(literal.getString())));
			else if ((p3RDFSDatatype == owlModel.getXSDdate()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDDate(literal.getString())));
			else
				throw new OWLConversionFactoryException("cannot create an OWLDataValue object for RDFS literal " + literal.getString() + " of type " + p3RDFSDatatype);
		} catch (DataValueConversionException e) {
			throw new OWLConversionFactoryException("error creating an OWLDataValue object for RDFS literal value " + literal.getString() + " with type "
					+ p3RDFSDatatype.getURI() + ": " + e.getMessage());
		}

		return dataValueArgument;
	}

	public static OWLLiteralReference convertRDFSLiteral2OWLLiteral(OWLModel owlModel, RDFSLiteral p3RDFSLiteral) throws OWLConversionFactoryException
	{
		RDFSDatatype datatype = p3RDFSLiteral.getDatatype();
		OWLDataValue dataValue = null;

		try {
			if ((datatype == owlModel.getXSDint()) || (datatype == owlModel.getXSDinteger()))
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getInt());
			else if (datatype == owlModel.getXSDshort())
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getShort());
			else if (datatype == owlModel.getXSDlong())
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getLong());
			else if (datatype == owlModel.getXSDboolean())
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getBoolean());
			else if (datatype == owlModel.getXSDfloat())
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getFloat());
			else if (datatype == owlModel.getXSDdouble())
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getDouble());
			else if ((datatype == owlModel.getXSDstring()))
				dataValue = new OWLDataValueImpl(p3RDFSLiteral.getString());
			else if ((datatype == owlModel.getXSDtime()))
				dataValue = new OWLDataValueImpl(new XSDTime(p3RDFSLiteral.getString()));
			else if ((datatype == owlModel.getXSDanyURI()))
				dataValue = new OWLDataValueImpl(new XSDAnyURI(p3RDFSLiteral.getString()));
			else if ((datatype == owlModel.getXSDbyte()))
				dataValue = new OWLDataValueImpl(Byte.valueOf(p3RDFSLiteral.getString()));
			else if ((datatype == owlModel.getXSDduration()))
				dataValue = new OWLDataValueImpl(new XSDDuration(p3RDFSLiteral.getString()));
			else if ((datatype == owlModel.getXSDdateTime()))
				dataValue = new OWLDataValueImpl(new XSDDateTime(p3RDFSLiteral.getString()));
			else if ((datatype == owlModel.getXSDdate()))
				dataValue = new OWLDataValueImpl(new XSDDate(p3RDFSLiteral.getString()));
			else
				throw new OWLConversionFactoryException("cannot create an OWLDataValue object for RDFS literal " + p3RDFSLiteral.getString() + " of type '" + datatype);
		} catch (DataValueConversionException e) {
			throw new OWLConversionFactoryException("error creating an OWLDataValue object for RDFS literal value " + p3RDFSLiteral.getString() + " with type "
					+ datatype.getURI() + ": " + e.getMessage());
		}

		return dataValue;
	}

	private SWRLClassAtomReference convertSWRLClassAtom(SWRLClassAtom p3SWRLClassAtom) throws OWLConversionFactoryException
	{
		String classURI = p3SWRLClassAtom.getClassPredicate() != null ? p3SWRLClassAtom.getClassPredicate().getURI() : null;
		SWRLClassAtomImpl swrlClassAtom = new SWRLClassAtomImpl(classURI);

		if (classURI == null)
			throw new OWLConversionFactoryException("empty class name in SWRLClassAtom: " + p3SWRLClassAtom.getBrowserText());

		swrlClassAtom.addReferencedClassURI(classURI);

		if (p3SWRLClassAtom.getArgument1() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLClassAtom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlClassAtom.setArgument1(argument1);
			swrlClassAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLClassAtom.getArgument1() instanceof OWLIndividual) {
			String individualArgumentURI = ((OWLIndividual)p3SWRLClassAtom.getArgument1()).getURI();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individualArgumentURI);
			swrlClassAtom.setArgument1(argument1);
			swrlClassAtom.addReferencedIndividualURI(argument1.getURI());
		} else if (p3SWRLClassAtom.getArgument1() instanceof OWLNamedClass) {
			String classArgumentURI = ((OWLNamedClass)p3SWRLClassAtom.getArgument1()).getURI();
			ClassArgument argument1 = argumentFactory.createClassArgument(classArgumentURI);
			swrlClassAtom.setArgument1(argument1);
			swrlClassAtom.addReferencedClassURI(classURI);
		} else if (p3SWRLClassAtom.getArgument1() instanceof OWLObjectProperty) {
			String propertyArgumentURI = ((OWLObjectProperty)p3SWRLClassAtom.getArgument1()).getURI();
			ObjectPropertyArgument argument1 = argumentFactory.createObjectPropertyArgument(propertyArgumentURI);
			swrlClassAtom.setArgument1(argument1);
			swrlClassAtom.addReferencedPropertyURI(propertyArgumentURI);
		} else if (p3SWRLClassAtom.getArgument1() instanceof OWLDatatypeProperty) {
			String propertyArgumentURI = ((OWLDatatypeProperty)p3SWRLClassAtom.getArgument1()).getURI();
			DataPropertyArgument argument1 = argumentFactory.createDataPropertyArgument(propertyArgumentURI);
			swrlClassAtom.setArgument1(argument1);
			swrlClassAtom.addReferencedPropertyURI(propertyArgumentURI);
		} else
			throw new OWLConversionFactoryException("unexpected argument to class atom " + p3SWRLClassAtom.getBrowserText() + "; expecting "
					+ "variable or individual, got instance of " + p3SWRLClassAtom.getArgument1().getClass());

		return swrlClassAtom;
	}

	private SWRLObjectPropertyAtomReference convertSWRLObjectPropertyAtom(SWRLIndividualPropertyAtom p3SWRLObjectPropertyAtom)
		throws OWLConversionFactoryException
	{
		String propertyURI = p3SWRLObjectPropertyAtom.getPropertyPredicate() != null ? p3SWRLObjectPropertyAtom.getPropertyPredicate().getURI() : null;
		OWLObjectPropertyReference objectProperty = new P3OWLObjectPropertyReference(propertyURI);
		P3SWRLObjectPropertyAtomReference swrlObjectPropertyAtom = new P3SWRLObjectPropertyAtomReference(objectProperty);

		if (propertyURI == null)
			throw new OWLConversionFactoryException("empty property name in SWRLIndividualPropertyAtom: " + p3SWRLObjectPropertyAtom.getBrowserText());

		swrlObjectPropertyAtom.addReferencedPropertyURI(propertyURI);

		if (p3SWRLObjectPropertyAtom.getArgument1() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLObjectPropertyAtom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlObjectPropertyAtom.setArgument1(argument1);
			swrlObjectPropertyAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLObjectPropertyAtom.getArgument1() instanceof OWLIndividual) {
			OWLIndividual p3OWLIndividual = (OWLIndividual)p3SWRLObjectPropertyAtom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(p3OWLIndividual.getURI());
			swrlObjectPropertyAtom.setArgument1(argument1);
			swrlObjectPropertyAtom.addReferencedIndividualURI(argument1.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected first argument to individual property atom " + p3SWRLObjectPropertyAtom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + p3SWRLObjectPropertyAtom.getArgument1().getClass());

		if (p3SWRLObjectPropertyAtom.getArgument2() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLObjectPropertyAtom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLObjectPropertyAtom.getArgument2() instanceof OWLIndividual) {
			OWLIndividual p3OWLIndividual = (OWLIndividual)p3SWRLObjectPropertyAtom.getArgument2();
			SWRLIndividualArgumentReference argument2 = argumentFactory.createIndividualArgument(p3OWLIndividual.getURI());
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedIndividualURI(argument2.getURI());
		} else if (p3SWRLObjectPropertyAtom.getArgument2() instanceof OWLNamedClass) {
			OWLNamedClass p3OWLNamedClass = (OWLNamedClass)p3SWRLObjectPropertyAtom.getArgument2();
			ClassArgument argument2 = argumentFactory.createClassArgument(p3OWLNamedClass.getURI());
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedClassURI(argument2.getURI());
		} else if (p3SWRLObjectPropertyAtom.getArgument2() instanceof OWLProperty) {
			OWLProperty p3OWLProperty = (OWLProperty)p3SWRLObjectPropertyAtom.getArgument2();
			PropertyArgument argument2;
			String propertyArgumentURI = ((OWLObjectProperty)p3OWLProperty).getURI();
			if (p3OWLProperty.isObjectProperty())
				argument2 = argumentFactory.createObjectPropertyArgument(propertyArgumentURI);
			else
				argument2 = argumentFactory.createDataPropertyArgument(propertyArgumentURI);
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedPropertyURI(propertyArgumentURI);
		} else
			throw new OWLConversionFactoryException("unexpected second argument to individual property atom " + p3SWRLObjectPropertyAtom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + p3SWRLObjectPropertyAtom.getArgument2().getClass());

		return swrlObjectPropertyAtom;
	}

	private SWRLDataPropertyAtomReference convertSWRLDataPropertyAtom(SWRLDatavaluedPropertyAtom p3SWRLDataPropertyAtom) throws OWLConversionFactoryException
	{
		String propertyURI = p3SWRLDataPropertyAtom.getPropertyPredicate() != null ? p3SWRLDataPropertyAtom.getPropertyPredicate().getURI() : null;
		OWLDataPropertyReference dataProperty = new P3OWLDataPropertyReference(propertyURI);
		P3SWRLDataPropertyAtomReference swrlDataPropertyAtom = new P3SWRLDataPropertyAtomReference(dataProperty);

		if (propertyURI == null)
			throw new OWLConversionFactoryException("empty property name in SWRLDatavaluedPropertyAtom: " + p3SWRLDataPropertyAtom.getBrowserText());

		swrlDataPropertyAtom.addReferencedPropertyURI(propertyURI);

		if (p3SWRLDataPropertyAtom.getArgument1() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLDataPropertyAtom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlDataPropertyAtom.setArgument1(argument1);
			swrlDataPropertyAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLDataPropertyAtom.getArgument1() instanceof OWLIndividual) {
			OWLIndividual p3OWLIndividual = (OWLIndividual)p3SWRLDataPropertyAtom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(p3OWLIndividual.getURI());
			swrlDataPropertyAtom.setArgument1(argument1);
			swrlDataPropertyAtom.addReferencedIndividualURI(argument1.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected argument first to datavalued property atom '" + p3SWRLDataPropertyAtom.getBrowserText()
					+ "' - expecting variable or individual, got instance of " + p3SWRLDataPropertyAtom.getArgument1().getClass());

		if (p3SWRLDataPropertyAtom.getArgument2() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLDataPropertyAtom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlDataPropertyAtom.setArgument2(argument2);
			swrlDataPropertyAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLDataPropertyAtom.getArgument2() instanceof RDFSLiteral) {
			SWRLLiteralArgumentReference argument2 = convertRDFSLiteral2DataValueArgument(owlModel, (RDFSLiteral)p3SWRLDataPropertyAtom.getArgument2());
			swrlDataPropertyAtom.setArgument2(argument2);
		} else
			throw new OWLConversionFactoryException("unexpected second to datavalued property atom " + p3SWRLDataPropertyAtom.getBrowserText()
					+ " - expecting variable or literal, got instance of " + p3SWRLDataPropertyAtom.getArgument2().getClass());

		return swrlDataPropertyAtom;
	}

	private SWRLSameIndividualAtomReference convertSWRLSameIndividualAtom(SWRLSameIndividualAtom p3SWRLSameIndividualAtom)
		throws OWLConversionFactoryException
	{
		P3SWRLSameIndividualAtomReference swrlSameIndividualAtom = new P3SWRLSameIndividualAtomReference();

		if (p3SWRLSameIndividualAtom.getArgument1() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLSameIndividualAtom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlSameIndividualAtom.setArgument1(argument1);
			swrlSameIndividualAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLSameIndividualAtom.getArgument1() instanceof OWLIndividual) {
			OWLIndividual individual = (OWLIndividual)p3SWRLSameIndividualAtom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individual.getURI());
			swrlSameIndividualAtom.setArgument1(argument1);
			swrlSameIndividualAtom.addReferencedIndividualURI(individual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected first argument to atom '" + p3SWRLSameIndividualAtom.getBrowserText()
					+ "' - expecting variable or individual, got instance of " + p3SWRLSameIndividualAtom.getArgument1().getClass() + ".");

		if (p3SWRLSameIndividualAtom.getArgument2() instanceof SWRLVariable) {
			SWRLVariable p2SWRLVariable = (SWRLVariable)p3SWRLSameIndividualAtom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(p2SWRLVariable.getLocalName());
			swrlSameIndividualAtom.setArgument2(argument2);
			swrlSameIndividualAtom.addReferencedVariableName(p2SWRLVariable.getLocalName());
		} else if (p3SWRLSameIndividualAtom.getArgument2() instanceof OWLIndividual) {
			OWLIndividual p3OWLIndividual = (OWLIndividual)p3SWRLSameIndividualAtom.getArgument2();
			SWRLIndividualArgumentReference argument2 = argumentFactory.createIndividualArgument(p3OWLIndividual.getURI());
			swrlSameIndividualAtom.setArgument2(argument2);
			swrlSameIndividualAtom.addReferencedIndividualURI(p3OWLIndividual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected second argument to atom " + p3SWRLSameIndividualAtom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + p3SWRLSameIndividualAtom.getArgument2().getClass());

		return swrlSameIndividualAtom;
	}

	private SWRLDifferentIndividualsAtomReference convertDifferentIndividualsAtom(SWRLDifferentIndividualsAtom p3SWRLDifferentIndividualsAtom)
		throws OWLConversionFactoryException
	{
		P3SWRLDifferentIndividualsAtomReference swrlDifferentIndividualsAtom = new P3SWRLDifferentIndividualsAtomReference();

		if (p3SWRLDifferentIndividualsAtom.getArgument1() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLDifferentIndividualsAtom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlDifferentIndividualsAtom.setArgument1(argument1);
			swrlDifferentIndividualsAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLDifferentIndividualsAtom.getArgument1() instanceof OWLIndividual) {
			OWLIndividual p3OWLIndividual = (OWLIndividual)p3SWRLDifferentIndividualsAtom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(p3OWLIndividual.getURI());
			swrlDifferentIndividualsAtom.setArgument1(argument1);
			swrlDifferentIndividualsAtom.addReferencedIndividualURI(p3OWLIndividual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected first argument to atom " + p3SWRLDifferentIndividualsAtom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + p3SWRLDifferentIndividualsAtom.getArgument1().getClass());

		if (p3SWRLDifferentIndividualsAtom.getArgument2() instanceof SWRLVariable) {
			SWRLVariable p3SWRLVariable = (SWRLVariable)p3SWRLDifferentIndividualsAtom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName());
			swrlDifferentIndividualsAtom.setArgument2(argument2);
			swrlDifferentIndividualsAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
		} else if (p3SWRLDifferentIndividualsAtom.getArgument2() instanceof OWLIndividual) {
			OWLIndividual p3OWLIndividual = (OWLIndividual)p3SWRLDifferentIndividualsAtom.getArgument2();
			SWRLIndividualArgumentReference argument2 = argumentFactory.createIndividualArgument(p3OWLIndividual.getURI());
			swrlDifferentIndividualsAtom.setArgument2(argument2);
			swrlDifferentIndividualsAtom.addReferencedIndividualURI(p3OWLIndividual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected second argument to atom " + p3SWRLDifferentIndividualsAtom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + p3SWRLDifferentIndividualsAtom.getArgument2().getClass());

		return swrlDifferentIndividualsAtom;
	}

	private SWRLBuiltInAtomReference convertSWRLBuiltInAtom(SWRLBuiltinAtom p3SWRLBuiltInAtom)
		throws OWLConversionFactoryException
	{
		String builtInName = (p3SWRLBuiltInAtom.getBuiltin() != null) ? p3SWRLBuiltInAtom.getBuiltin().getURI() : null;
		String builtInPrefixedName = (p3SWRLBuiltInAtom.getBuiltin() != null) ? p3SWRLBuiltInAtom.getBuiltin().getPrefixedName() : null;
		P3SWRLBuiltInAtomReference swrlBuiltInAtom = new P3SWRLBuiltInAtomReference(builtInName, builtInPrefixedName);
		List<BuiltInArgument> arguments = new ArrayList<BuiltInArgument>();
		RDFList rdfList = p3SWRLBuiltInAtom.getArguments();

		if (builtInName == null)
			throw new OWLConversionFactoryException("empty built-in name in SWRLBuiltinAtom: " + p3SWRLBuiltInAtom.getBrowserText());

		Iterator<?> iterator = rdfList.getValues().iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof SWRLVariable) {
				SWRLVariable p3SWRLVariable = (SWRLVariable)o;
				arguments.add(argumentFactory.createVariableArgument(p3SWRLVariable.getLocalName()));
				swrlBuiltInAtom.addReferencedVariableName(p3SWRLVariable.getLocalName());
			} else if (o instanceof OWLIndividual) {
				OWLIndividual p3OWLIndividual = (OWLIndividual)o;
				arguments.add(argumentFactory.createIndividualArgument(p3OWLIndividual.getURI()));
				swrlBuiltInAtom.addReferencedIndividualURI(p3OWLIndividual.getURI());
			} else if (o instanceof OWLNamedClass) {
				OWLNamedClass p3OWLNamedClass = (OWLNamedClass)o;
				arguments.add(argumentFactory.createClassArgument(p3OWLNamedClass.getURI()));
				swrlBuiltInAtom.addReferencedClassURI(p3OWLNamedClass.getURI());
			} else if (o instanceof OWLProperty) {
				OWLProperty p3OWLProperty = (OWLProperty)o;
				String propertyArgumentURI = ((OWLObjectProperty)p3OWLProperty).getURI();
				if (p3OWLProperty.isObjectProperty())
					arguments.add(argumentFactory.createObjectPropertyArgument(propertyArgumentURI));
				else
					arguments.add(argumentFactory.createDataPropertyArgument(propertyArgumentURI));
				swrlBuiltInAtom.addReferencedPropertyURI(propertyArgumentURI);
			} else if (o instanceof RDFSLiteral)
				arguments.add(convertRDFSLiteral2DataValueArgument(owlModel, (RDFSLiteral)o));
			else {
				try {
					arguments.add(argumentFactory.createDataValueArgument(o));
				} catch (DataValueConversionException e) {
					throw new OWLConversionFactoryException("error converting argument to built-in " + builtInPrefixedName + " with value " + o + " of unknown type "
							+ o.getClass() + ": " + e.getMessage());
				}
			}
		}

		swrlBuiltInAtom.setBuiltInArguments(arguments);

		return swrlBuiltInAtom;
	}

	private void write2OWLModel(OWLClassAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		String classURI = axiom.getDescription().getURI();
		String individualURI = axiom.getIndividual().getURI();
		SWRLOWLUtil.addType(owlModel, individualURI, classURI);
	}

	private void write2OWLModel(OWLClassPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		String objectClassURI = axiom.getObject().getURI();
		RDFProperty p3OWLProperty = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
		OWLIndividual p3SubjectIndividual;
		OWLNamedClass p3ObjectClass = null;

		if (p3OWLProperty == null)
			throw new OWLConversionFactoryException("invalid property name: " + propertyURI);

		p3SubjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (p3SubjectIndividual == null)
			throw new OWLConversionFactoryException("invalid individual URI " + subjectIndividualURI);

		p3ObjectClass = SWRLOWLUtil.getOWLNamedClass(owlModel, objectClassURI);

		if (!p3SubjectIndividual.hasPropertyValue(p3OWLProperty, p3ObjectClass, false))
			p3SubjectIndividual.addPropertyValue(p3OWLProperty, p3ObjectClass);
	}

	private void write2OWLModel(OWLDataPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		OWLIndividual p3SubjectIndividual;
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		RDFProperty p3OWLProperty = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);

		if (p3OWLProperty == null)
			throw new OWLConversionFactoryException("invalid property URI " + propertyURI);

		RDFSDatatype rangeDatatype = p3OWLProperty.getRangeDatatype();
		Object objectValue;

		p3SubjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (p3SubjectIndividual == null)
			throw new OWLConversionFactoryException("invalid individual URI " + subjectIndividualURI);

		if (rangeDatatype == null) {
			OWLDataValue dataValue = owlDataValueFactory.getOWLDataValue(axiom.getObject());
			if (dataValue.isString())
				objectValue = dataValue.toString();
			else
				objectValue = axiom.getObject().toString();
		} else
			objectValue = owlModel.createRDFSLiteral(axiom.getObject().toString(), rangeDatatype);

		if (!p3SubjectIndividual.hasPropertyValue(p3OWLProperty, objectValue, false))
			p3SubjectIndividual.addPropertyValue(p3OWLProperty, objectValue);
	}

	private void write2OWLModel(OWLObjectPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		OWLIndividual p3SubjectIndividual, p3ObjectIndividual;
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		String objectIndividualURI = axiom.getObject().getURI();
		RDFProperty p3OWLProperty = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);

		if (p3OWLProperty == null)
			throw new OWLConversionFactoryException("invalid property URI" + propertyURI);

		p3SubjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (p3SubjectIndividual == null)
			throw new OWLConversionFactoryException("invalid subject individual URI " + subjectIndividualURI);

		p3ObjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, objectIndividualURI);
		if (p3ObjectIndividual == null)
			throw new OWLConversionFactoryException("invalid object individual URI " + objectIndividualURI);

		if (!p3SubjectIndividual.hasPropertyValue(p3OWLProperty, p3ObjectIndividual, false))
			p3SubjectIndividual.addPropertyValue(p3OWLProperty, p3ObjectIndividual);
	}

	private void write2OWLModel(OWLPropertyPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		String objectPropertyURI = axiom.getObject().getURI();
		RDFProperty p3OWLProperty = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
		OWLIndividual p3SubjectIndividual;
		OWLProperty p3ObjectProperty;

		if (p3OWLProperty == null)
			throw new OWLConversionFactoryException("invalid property URI " + propertyURI);

		p3SubjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (p3SubjectIndividual == null)
			throw new OWLConversionFactoryException("invalid subject individual URI" + subjectIndividualURI);

		p3ObjectProperty = SWRLOWLUtil.getOWLProperty(owlModel, objectPropertyURI);
		if (p3ObjectProperty == null)
			throw new OWLConversionFactoryException("invalid object individual URI" + objectPropertyURI);

		if (!p3SubjectIndividual.hasPropertyValue(p3OWLProperty, p3ObjectProperty, false))
			p3SubjectIndividual.addPropertyValue(p3OWLProperty, p3ObjectProperty);
	}

	private void write2OWLModel(OWLSomeValuesFromReference axiom) throws OWLConversionFactoryException
	{
		OWLSomeValuesFrom p3SomeValuesFrom = SWRLOWLUtil.getOWLSomeValuesFrom(owlModel, axiom.asOWLClass().getURI());
		OWLProperty p2OWLProperty = SWRLOWLUtil.getOWLProperty(owlModel, axiom.getProperty().getURI());
		RDFResource p3Filler = SWRLOWLUtil.getClass(owlModel, axiom.getSomeValuesFrom().getURI());

		p3SomeValuesFrom.setOnProperty(p2OWLProperty);
		p3SomeValuesFrom.setFiller(p3Filler);
	}

	private void write2OWLModel(OWLSubClassAxiomReference axiom) throws OWLConversionFactoryException
	{
		String subClassURI = axiom.getSubClass().getURI();
		String superClassURI = axiom.getSuperClass().getURI();
		SWRLOWLUtil.addOWLSuperClass(owlModel, subClassURI, superClassURI);
	}

	private SWRLDataRangeAtomReference convertDataRangeAtom(SWRLDataRangeAtom p3SWRLDataRangeAtom) throws OWLConversionFactoryException
	{
		throw new OWLConversionFactoryException("SWRL data range atoms not implemented.");
	}

	private SWRLAtomReference convertSWRLAtom(SWRLAtom p3SWRLAtom) throws OWLConversionFactoryException
	{
		if (p3SWRLAtom instanceof SWRLClassAtom) {
			return convertSWRLClassAtom((SWRLClassAtom)p3SWRLAtom);
		} else if (p3SWRLAtom instanceof SWRLDatavaluedPropertyAtom) {
			return convertSWRLDataPropertyAtom((SWRLDatavaluedPropertyAtom)p3SWRLAtom);
		} else if (p3SWRLAtom instanceof SWRLIndividualPropertyAtom) {
			return convertSWRLObjectPropertyAtom((SWRLIndividualPropertyAtom)p3SWRLAtom);
		} else if (p3SWRLAtom instanceof SWRLSameIndividualAtom) {
			return convertSWRLSameIndividualAtom((SWRLSameIndividualAtom)p3SWRLAtom);
		} else if (p3SWRLAtom instanceof SWRLDifferentIndividualsAtom) {
			return convertDifferentIndividualsAtom((SWRLDifferentIndividualsAtom)p3SWRLAtom);
		} else if (p3SWRLAtom instanceof SWRLBuiltinAtom) {
			return convertSWRLBuiltInAtom((SWRLBuiltinAtom)p3SWRLAtom);
		} else if (p3SWRLAtom instanceof SWRLDataRangeAtom)
			return convertDataRangeAtom((SWRLDataRangeAtom)p3SWRLAtom);
		else
			throw new OWLConversionFactoryException("invalid SWRL atom: " + p3SWRLAtom.getBrowserText());
	}

	// Utility method to create a collection of OWL property assertion axioms for every subject/predicate combination for a particular OWL
	// property. TODO: This is incredibly inefficient.

	public Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String propertyURI)
		throws OWLConversionFactoryException, DataValueConversionException
	{
		return getOWLPropertyAssertionAxioms(null, propertyURI);
	}

	private void calculateTransitiveSubAndEquivalentPropertyClosure(OWLProperty p3OWLProperty, Set<OWLProperty> p3OWLPropertyClosure)
	{
		Set<OWLProperty> properties = new HashSet<OWLProperty>();

		properties.add(p3OWLProperty);
		calculateTransitiveSubAndEquivalentPropertyClosure(properties, p3OWLPropertyClosure);
	}

	private void calculateTransitiveSubAndEquivalentPropertyClosure(Set<OWLProperty> p3OWLProperties, Set<OWLProperty> p3OWLPropertyClosure)
	{
		for (OWLProperty property : p3OWLProperties) {
			if (!p3OWLPropertyClosure.contains(property)) {
				p3OWLPropertyClosure.add(property);
				calculateTransitiveSubAndEquivalentPropertyClosure(SWRLOWLUtil.getSubPropertiesOf(owlModel, property), p3OWLPropertyClosure);
				calculateTransitiveSubAndEquivalentPropertyClosure(SWRLOWLUtil.getEquivalentPropertiesOf(owlModel, property), p3OWLPropertyClosure);
			}
		}
	}

	public Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String subjectURI, String propertyURI)
		throws OWLConversionFactoryException, DataValueConversionException
	{
		Set<OWLPropertyAssertionAxiomReference> propertyAssertionAxioms = new HashSet<OWLPropertyAssertionAxiomReference>();
		OWLProperty p3OWLProperty = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
		OWLIndividual p3SubjectIndividual = (subjectURI == null) ? null : SWRLOWLUtil.getOWLIndividual(owlModel, subjectURI);
		TripleStoreModel tsm = owlModel.getTripleStoreModel();
		List<RDFResource> p3Subjects = new ArrayList<RDFResource>();
		Set<OWLProperty> p3ExpandedOWLProperties = new HashSet<OWLProperty>();

		if (p3OWLProperty == null)
			throw new InvalidPropertyNameException(propertyURI);

		calculateTransitiveSubAndEquivalentPropertyClosure(p3OWLProperty, p3ExpandedOWLProperties);

		for (TripleStore ts : tsm.getTripleStores()) {
			for (RDFProperty localProperty : p3ExpandedOWLProperties) {
				Iterator<RDFResource> si = ts.listSubjects(localProperty);
				while (si.hasNext())
					p3Subjects.add(si.next());
			}
		}

		for (RDFResource p2LocalSubject : p3Subjects) {

			if (!(p2LocalSubject instanceof OWLIndividual))
				continue; // Deal only with OWL individuals (could return metaclass, for example)
			if (p3SubjectIndividual != null && !p2LocalSubject.getURI().equals(p3SubjectIndividual.getURI()))
				continue; // If subject supplied, ensure it is same
			OWLIndividual subjectIndividual = (OWLIndividual)p2LocalSubject;

			for (Object object : p2LocalSubject.getPropertyValues(p3OWLProperty, true)) {
				OWLPropertyAssertionAxiomReference axiom;

				if (p3OWLProperty.hasObjectRange()) { // Object property
					OWLObjectPropertyReference objectProperty = owlFactory.getOWLObjectProperty(propertyURI);

					if (object instanceof OWLIndividual) {
						OWLIndividual p3ObjectIndividual = (OWLIndividual)object;
						OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
						OWLNamedIndividualReference objectOWLIndividual = owlFactory.getOWLIndividual(p3ObjectIndividual.getURI());
						axiom = owlFactory.getOWLObjectPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectOWLIndividual);
						propertyAssertionAxioms.add(axiom);
					} else if (object instanceof OWLNamedClass) { // This will be OWL Full
						OWLNamedClass p3ObjectOWLNamedClass = (OWLNamedClass)object;
						OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
						OWLClassReference objectPropertyClassValue = owlFactory.getOWLClass(p3ObjectOWLNamedClass.getURI());
						axiom = owlFactory.getOWLClassPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectPropertyClassValue);
						propertyAssertionAxioms.add(axiom);
					} else if (object instanceof OWLProperty) { // This will be OWL Full
						OWLProperty p3ObjectOWLPropertyValue = (OWLProperty)object;
						OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
						OWLPropertyReference objectPropertyPropertyValue;
						if (p3ObjectOWLPropertyValue.isObjectProperty())
							objectPropertyPropertyValue = owlFactory.getOWLObjectProperty(p3ObjectOWLPropertyValue.getURI());
						else
							objectPropertyPropertyValue = owlFactory.getOWLDataProperty(p3ObjectOWLPropertyValue.getURI());
						axiom = owlFactory.getOWLPropertyPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectPropertyPropertyValue);
						propertyAssertionAxioms.add(axiom);
					}
				} else { // DataProperty
					OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
					RDFSLiteral p3RDFSLiteral = owlModel.asRDFSLiteral(object);
					OWLLiteralReference literal = convertRDFSLiteral2OWLLiteral(owlModel, p3RDFSLiteral);
					OWLDataPropertyReference dataProperty = owlFactory.getOWLDataProperty(propertyURI);
					axiom = owlFactory.getOWLDataPropertyAssertionAxiom(subjectOWLIndividual, dataProperty, literal);
					propertyAssertionAxioms.add(axiom);
				}
			}
		}

		return propertyAssertionAxioms;
	}

	public Set<OWLSameIndividualAxiomReference> getSameIndividualAxioms() throws OWLConversionFactoryException
	{
		RDFProperty p3SameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(owlModel);
		Set<OWLSameIndividualAxiomReference> sameIndividualAxioms = new HashSet<OWLSameIndividualAxiomReference>();

		Iterator<?> p3IndividualsIterator1 = owlModel.listSubjects(p3SameAsProperty);
		while (p3IndividualsIterator1.hasNext()) {
			Object object1 = p3IndividualsIterator1.next();
			if (!(object1 instanceof OWLIndividual))
				continue; // Deal only with OWL individuals (could return metaclass, for example)
			OWLIndividual p3OWLIndividual1 = (OWLIndividual)object1;
			Collection<?> p3Individuals = (Collection<?>)p3OWLIndividual1.getPropertyValues(p3SameAsProperty);
			Iterator<?> p3IndividualsIterator2 = p3Individuals.iterator();
			while (p3IndividualsIterator2.hasNext()) {
				Object object2 = p3IndividualsIterator2.next();
				if (!(object2 instanceof OWLIndividual))
					continue;
				OWLIndividual p3OWLIndividual2 = (OWLIndividual)object2;
				sameIndividualAxioms.add(owlFactory.getOWLSameIndividualAxiom(owlFactory.getOWLIndividual(p3OWLIndividual1.getURI()),
						owlFactory.getOWLIndividual(p3OWLIndividual2.getURI())));
			}
		}
		return sameIndividualAxioms;
	}

	public Set<OWLDifferentIndividualsAxiomReference> getOWLDifferentIndividualsAxioms() throws OWLConversionFactoryException
	{
		RDFProperty differentFromProperty = SWRLOWLUtil.getOWLDifferentFromProperty(owlModel);
		Set<OWLDifferentIndividualsAxiomReference> differentIndividualsAxioms = new HashSet<OWLDifferentIndividualsAxiomReference>();
		Collection<?> p3AllDifferents = SWRLOWLUtil.getOWLAllDifferents(owlModel);

		Iterator<?> p3IndividualsIterator1 = owlModel.listSubjects(differentFromProperty);
		while (p3IndividualsIterator1.hasNext()) {
			Object object1 = p3IndividualsIterator1.next();
			if (!(object1 instanceof OWLIndividual))
				continue; // Deal only with OWL individuals (could return metaclass, for example)
			OWLIndividual p3OWLIndividual1 = (OWLIndividual)object1;
			Collection<?> p3Individuals = (Collection<?>)p3OWLIndividual1.getPropertyValues(differentFromProperty);
			Iterator<?> p3IndividualsIterator2 = p3Individuals.iterator();
			while (p3IndividualsIterator2.hasNext()) {
				Object object2 = p3IndividualsIterator2.next();
				if (!(object2 instanceof OWLIndividual))
					continue;
				OWLIndividual p3OWLIndividual2 = (OWLIndividual)object2;
				differentIndividualsAxioms.add(owlFactory.getOWLDifferentIndividualsAxiom(owlFactory.getOWLIndividual(p3OWLIndividual1.getURI()),
						owlFactory.getOWLIndividual(p3OWLIndividual2.getURI())));
			}
		}

		if (!p3AllDifferents.isEmpty()) {
			Iterator<?> p3AllDifferentsIterator = p3AllDifferents.iterator();
			while (p3AllDifferentsIterator.hasNext()) {
				OWLAllDifferent p3OWLAllDifferent = (OWLAllDifferent)p3AllDifferentsIterator.next();

				if (p3OWLAllDifferent.getDistinctMembers().size() != 0) {
					OWLDifferentIndividualsAxiomReference axiom;
					Set<OWLNamedIndividualReference> individuals = new HashSet<OWLNamedIndividualReference>();

					Iterator<?> individualsIterator = p3OWLAllDifferent.getDistinctMembers().iterator();
					while (individualsIterator.hasNext()) {
						RDFIndividual p3Individual = (RDFIndividual)individualsIterator.next();
						if (p3Individual instanceof OWLIndividual) { // Ignore OWL individuals
							String individualURI = ((OWLIndividual)p3Individual).getURI();
							OWLNamedIndividualReference individual = owlFactory.getOWLIndividual(individualURI);
							individuals.add(individual);
						}
					}
					axiom = owlFactory.getOWLDifferentIndividualsAxiom(individuals);
					differentIndividualsAxioms.add(axiom);
				}
			}
		}

		return differentIndividualsAxioms;
	}

	private void initializeProperty(P3OWLPropertyReference owlPropertyImpl, OWLProperty p3OWLProperty) throws OWLConversionFactoryException
	{
		for (String domainClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(p3OWLProperty.getUnionDomain()))
			owlPropertyImpl.addDomainClass(getOWLClass(domainClassURI));
		for (String rangeClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(p3OWLProperty.getUnionRangeClasses()))
			owlPropertyImpl.addRangeClass(getOWLClass(rangeClassURI));
		for (String superPropertyURI : SWRLOWLUtil.rdfResources2OWLPropertyURIs(p3OWLProperty.getSuperproperties(false)))
			if (p3OWLProperty.isObjectProperty())
				owlPropertyImpl.addSuperProperty(getOWLObjectProperty(superPropertyURI));
			else
				owlPropertyImpl.addSuperProperty(getOWLDataProperty(superPropertyURI));
		for (String subPropertyURI : SWRLOWLUtil.rdfResources2OWLPropertyURIs(p3OWLProperty.getSubproperties(false)))
			if (p3OWLProperty.isObjectProperty())
				owlPropertyImpl.addSubProperty(getOWLObjectProperty(subPropertyURI));
			else
				owlPropertyImpl.addSubProperty(getOWLDataProperty(subPropertyURI));
		for (String equivalentPropertyURI : SWRLOWLUtil.rdfResources2OWLPropertyURIs(p3OWLProperty.getEquivalentProperties()))
			if (p3OWLProperty.isObjectProperty())
				owlPropertyImpl.addEquivalentProperty(getOWLObjectProperty(equivalentPropertyURI));
			else
				owlPropertyImpl.addEquivalentProperty(getOWLDataProperty(equivalentPropertyURI));
	}

	private void buildDefiningClasses(P3OWLNamedIndividualReference individual, OWLIndividual p3OWLIndividual) throws OWLConversionFactoryException
	{
		for (Object o : p3OWLIndividual.getRDFTypes()) {
			RDFSClass p3Class = (RDFSClass)o;
			if (!p3Class.isAnonymous() && p3Class instanceof OWLNamedClass) {
				String classURI = ((OWLNamedClass)p3Class).getURI();
				individual.addType(getOWLClass(classURI));
			}
		}
	}

	private void buildSameAsIndividuals(P3OWLNamedIndividualReference individual, OWLIndividual p3OWLIndividual) throws OWLConversionFactoryException
	{
		RDFProperty p3SameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(owlModel);

		if (p3OWLIndividual.hasPropertyValue(p3SameAsProperty)) {
			Collection<?> p3Individuals = (Collection<?>)p3OWLIndividual.getPropertyValues(p3SameAsProperty);
			Iterator<?> p3IndividualsIterator = p3Individuals.iterator();
			while (p3IndividualsIterator.hasNext()) {
				Object object = p3IndividualsIterator.next();
				if (!(object instanceof OWLIndividual))
					continue;
				OWLIndividual p2SameAsIndividual = (OWLIndividual)object;
				individual.addSameAsIndividual(owlFactory.getOWLIndividual(p2SameAsIndividual.getURI()));
			}
		}
	}

	private void buildDifferentFromIndividuals(P3OWLNamedIndividualReference individual, OWLIndividual p3OWLIndividual) throws OWLConversionFactoryException
	{
		RDFProperty p3DifferentFromProperty = SWRLOWLUtil.getOWLDifferentFromProperty(owlModel);

		if (p3OWLIndividual.hasPropertyValue(p3DifferentFromProperty)) {
			Collection<?> p3Individuals = (Collection<?>)p3OWLIndividual.getPropertyValues(p3DifferentFromProperty);
			Iterator<?> p3IndividualsIterator = p3Individuals.iterator();
			while (p3IndividualsIterator.hasNext()) {
				Object object = p3IndividualsIterator.next();
				if (!(object instanceof OWLIndividual))
					continue;
				OWLIndividual p3DifferentFromIndividual = (OWLIndividual)object;
				individual.addDifferentFromIndividual(owlFactory.getOWLIndividual(p3DifferentFromIndividual.getURI()));
			}
		}
	}

	public OWLModel getOWLModel()
	{
		return owlModel;
	} // TODO: Protege-OWL dependency

}
