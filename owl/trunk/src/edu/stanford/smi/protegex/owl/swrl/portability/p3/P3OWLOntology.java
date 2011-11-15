
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
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
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
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyAssertionAxiomReference;
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
		Collection<edu.stanford.smi.protegex.owl.swrl.model.SWRLImp> imps = swrlFactory.getImps();
		Set<SWRLRuleReference> result = new HashSet<SWRLRuleReference>();

		for (edu.stanford.smi.protegex.owl.swrl.model.SWRLImp imp : imps) {
			if (imp.isEnabled()) {
				SWRLRuleReference rule = getSWRLRule(imp.getName());
				result.add(rule);
			} 
		} 

		return result;
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
		edu.stanford.smi.protegex.owl.swrl.model.SWRLImp imp = swrlFactory.getImp(ruleName);

		if (imp == null)
			throw new OWLConversionFactoryException("invalid rule name: " + ruleName);

		if (imp.getBody() != null && imp.getBody().getValues() != null && !imp.getBody().getValues().isEmpty()) {
			Iterator<?> iterator = imp.getBody().getValues().iterator();
			while (iterator.hasNext()) {
				edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom swrlAtom = (edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom)iterator.next();
				bodyAtoms.add(convertSWRLAtom(swrlAtom));
			} 
		} 

		if (imp.getHead() != null && imp.getHead().getValues() != null && !imp.getHead().getValues().isEmpty()) {
			Iterator<?> iterator = imp.getHead().getValues().iterator();
			while (iterator.hasNext()) {
				edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom swrlAtom = (edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom)iterator.next();
				headAtoms.add(convertSWRLAtom(swrlAtom));
			}
		}

		return new P3SWRLRuleReference(imp.getPrefixedName(), bodyAtoms, headAtoms);
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
			edu.stanford.smi.protegex.owl.model.OWLNamedClass owlNamedClass = SWRLOWLUtil.createOWLNamedClass(owlModel, classURI);
			owlClassImpl = new P3OWLClassReference(classURI);
			classes.put(classURI, owlClassImpl);

			if (!classURI.equals(OWLNames.Cls.THING)) {
				for (String superClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(owlNamedClass.getNamedSuperclasses(false)))
					owlClassImpl.addSuperClass(getOWLClass(superClassURI));
				for (String subClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(owlNamedClass.getNamedSubclasses(false)))
					owlClassImpl.addSubClass(getOWLClass(subClassURI));
				for (String equivalentClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassNames(owlNamedClass.getEquivalentClasses()))
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
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = SWRLOWLUtil.createOWLIndividual(owlModel, individualURI);
			owlIndividual = new P3OWLNamedIndividualReference(individualURI);
			individuals.put(individualURI, owlIndividual);

			buildDefiningClasses(owlIndividual, individual);
			buildSameAsIndividuals(owlIndividual, individual);
			buildDifferentFromIndividuals(owlIndividual, individual);
		} 
		return owlIndividual;
	}

	public OWLObjectPropertyReference getOWLObjectProperty(String propertyURI) throws OWLConversionFactoryException
	{
		P3OWLObjectPropertyReference owlObjectProperty;

		if (objectProperties.containsKey(propertyURI))
			return objectProperties.get(propertyURI);
		else {
			edu.stanford.smi.protegex.owl.model.OWLObjectProperty property = SWRLOWLUtil.createOWLObjectProperty(owlModel, propertyURI);
			owlObjectProperty = new P3OWLObjectPropertyReference(propertyURI);
			objectProperties.put(propertyURI, owlObjectProperty);

			initializeProperty(owlObjectProperty, property);
		} 

		return owlObjectProperty;
	}

	public OWLDataPropertyReference getOWLDataProperty(String propertyURI) throws OWLConversionFactoryException
	{
		P3OWLDataPropertyReference owlDataProperty;

		if (dataProperties.containsKey(propertyURI))
			return dataProperties.get(propertyURI);
		else {
			edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty property = SWRLOWLUtil.createOWLDatatypeProperty(owlModel, propertyURI);
			owlDataProperty = new P3OWLDataPropertyReference(propertyURI);
			dataProperties.put(propertyURI, owlDataProperty);

			initializeProperty(owlDataProperty, property);
		} 

		return owlDataProperty;
	}

	public void writeOWLClassDeclaration(OWLClassReference owlClass) throws OWLConversionFactoryException
	{
		String classURI = owlClass.getURI();
		edu.stanford.smi.protegex.owl.model.OWLClass cls, superclass;

		if (SWRLOWLUtil.isOWLNamedClass(owlModel, classURI))
			cls = SWRLOWLUtil.getOWLNamedClass(owlModel, classURI);
		else
			cls = SWRLOWLUtil.getOWLNamedClass(owlModel, classURI);

		for (OWLClassReference superClass : owlClass.getSuperClasses()) {
			String superClassURI = superClass.getURI();
			if (SWRLOWLUtil.isOWLNamedClass(owlModel, superClassURI))
				superclass = SWRLOWLUtil.getOWLNamedClass(owlModel, superClassURI);
			else
				superclass = SWRLOWLUtil.getOWLNamedClass(owlModel, superClassURI);

			if (!cls.isSubclassOf(superclass))
				cls.addSuperclass(superclass);
		} 
	}

	public void writeOWLIndividualDeclaration(OWLNamedIndividualReference owlIndividual) throws OWLConversionFactoryException
	{
		String individualURI = owlIndividual.getURI();
		edu.stanford.smi.protegex.owl.model.OWLIndividual individual;

		if (SWRLOWLUtil.isIndividual(owlModel, individualURI))
			individual = SWRLOWLUtil.getIndividual(owlModel, individualURI);
		else
			individual = SWRLOWLUtil.createIndividual(owlModel, individualURI);

		for (OWLClassReference owlClass : owlIndividual.getTypes()) {
			edu.stanford.smi.protegex.owl.model.RDFSClass cls = SWRLOWLUtil.getOWLNamedClass(owlModel, owlClass.getURI());

			if (!individual.hasRDFType(cls)) {
				if (individual.hasRDFType(SWRLOWLUtil.getOWLThingClass(owlModel)))
					individual.setRDFType(cls);
				else
					individual.addRDFType(cls);
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
		RDFSClass rdfsClass = SWRLOWLUtil.getRDFSNamedClass(owlModel, classURI);
		Set<OWLNamedIndividualReference> result = new HashSet<OWLNamedIndividualReference>();

		if (rdfsClass != null) {
			Iterator<?> iterator = rdfsClass.getInstances(true).iterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
					edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
					result.add(getOWLIndividual(individual.getURI()));
				}
			}
		}
		return result;
	}

	public boolean couldBeOWLNamedClass(String classURI)
	{
		RDFResource resource = SWRLOWLUtil.getRDFResource(owlModel, classURI);

		return (resource == null || resource instanceof OWLNamedClass);
	}

	public String uri2PrefixedName(String uri)
	{
		String result = NamespaceUtil.getPrefixedName(owlModel, uri);

		return result;
	}

	public String prefixedName2URI(String prefixedName)
	{
		String result = NamespaceUtil.getFullName(owlModel, prefixedName);

		return result;
	}

	public static SWRLLiteralArgumentReference convertRDFSLiteral2DataValueArgument(OWLModel owlModel, edu.stanford.smi.protegex.owl.model.RDFSLiteral literal)
		throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype = literal.getDatatype();
		SWRLLiteralArgumentReference dataValueArgument = null;

		try {
			if ((datatype == owlModel.getXSDint()) || (datatype == owlModel.getXSDinteger()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getInt()));
			else if (datatype == owlModel.getXSDshort())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getShort()));
			else if (datatype == owlModel.getXSDlong())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getLong()));
			else if (datatype == owlModel.getXSDboolean())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getBoolean()));
			else if (datatype == owlModel.getXSDfloat())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getFloat()));
			else if (datatype == owlModel.getXSDdouble())
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getDouble()));
			else if ((datatype == owlModel.getXSDstring()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(literal.getString()));
			else if ((datatype == owlModel.getXSDtime()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDTime(literal.getString())));
			else if ((datatype == owlModel.getXSDanyURI()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDAnyURI(literal.getString())));
			else if ((datatype == owlModel.getXSDbyte()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(Byte.valueOf(literal.getString())));
			else if ((datatype == owlModel.getXSDduration()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDDuration(literal.getString())));
			else if ((datatype == owlModel.getXSDdateTime()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDDateTime(literal.getString())));
			else if ((datatype == owlModel.getXSDdate()))
				dataValueArgument = new P3SWRLLiteralArgumentReference(new DataValueImpl(new XSDDate(literal.getString())));
			else
				throw new OWLConversionFactoryException("cannot create an OWLDataValue object for RDFS literal " + literal.getString() + " of type " + datatype);
		} catch (DataValueConversionException e) {
			throw new OWLConversionFactoryException("error creating an OWLDataValue object for RDFS literal value " + literal.getString() + " with type "
					+ datatype.getURI() + ": " + e.getMessage());
		} // try

		return dataValueArgument;
	}

	public static OWLLiteralReference convertRDFSLiteral2OWLLiteral(OWLModel owlModel, edu.stanford.smi.protegex.owl.model.RDFSLiteral literal)
		throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype = literal.getDatatype();
		OWLDataValue dataValue = null;

		try {
			if ((datatype == owlModel.getXSDint()) || (datatype == owlModel.getXSDinteger()))
				dataValue = new OWLDataValueImpl(literal.getInt());
			else if (datatype == owlModel.getXSDshort())
				dataValue = new OWLDataValueImpl(literal.getShort());
			else if (datatype == owlModel.getXSDlong())
				dataValue = new OWLDataValueImpl(literal.getLong());
			else if (datatype == owlModel.getXSDboolean())
				dataValue = new OWLDataValueImpl(literal.getBoolean());
			else if (datatype == owlModel.getXSDfloat())
				dataValue = new OWLDataValueImpl(literal.getFloat());
			else if (datatype == owlModel.getXSDdouble())
				dataValue = new OWLDataValueImpl(literal.getDouble());
			else if ((datatype == owlModel.getXSDstring()))
				dataValue = new OWLDataValueImpl(literal.getString());
			else if ((datatype == owlModel.getXSDtime()))
				dataValue = new OWLDataValueImpl(new XSDTime(literal.getString()));
			else if ((datatype == owlModel.getXSDanyURI()))
				dataValue = new OWLDataValueImpl(new XSDAnyURI(literal.getString()));
			else if ((datatype == owlModel.getXSDbyte()))
				dataValue = new OWLDataValueImpl(Byte.valueOf(literal.getString()));
			else if ((datatype == owlModel.getXSDduration()))
				dataValue = new OWLDataValueImpl(new XSDDuration(literal.getString()));
			else if ((datatype == owlModel.getXSDdateTime()))
				dataValue = new OWLDataValueImpl(new XSDDateTime(literal.getString()));
			else if ((datatype == owlModel.getXSDdate()))
				dataValue = new OWLDataValueImpl(new XSDDate(literal.getString()));
			else
				throw new OWLConversionFactoryException("cannot create an OWLDataValue object for RDFS literal " + literal.getString() + " of type '" + datatype);
		} catch (DataValueConversionException e) {
			throw new OWLConversionFactoryException("error creating an OWLDataValue object for RDFS literal value " + literal.getString() + " with type "
					+ datatype.getURI() + ": " + e.getMessage());
		} // try

		return dataValue;
	} // convertOWLDataValue

	private SWRLClassAtomReference convertClassAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom atom) throws OWLConversionFactoryException
	{
		String classURI = (atom.getClassPredicate() != null) ? atom.getClassPredicate().getURI() : null;
		SWRLClassAtomImpl classAtom = new SWRLClassAtomImpl(classURI);

		if (classURI == null)
			throw new OWLConversionFactoryException("empty class name in SWRLClassAtom: " + atom.getBrowserText());

		classAtom.addReferencedClassURI(classURI);

		if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(variable.getLocalName());
			classAtom.setArgument1(argument1);
			classAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			String individualArgumentURI = ((edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1()).getURI();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individualArgumentURI);
			classAtom.setArgument1(argument1);
			classAtom.addReferencedIndividualURI(argument1.getURI());
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
			String classArgumentURI = ((edu.stanford.smi.protegex.owl.model.OWLNamedClass)atom.getArgument1()).getURI();
			ClassArgument argument1 = argumentFactory.createClassArgument(classArgumentURI);
			classAtom.setArgument1(argument1);
			classAtom.addReferencedClassURI(classURI);
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLObjectProperty) {
			String propertyArgumentURI = ((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)atom.getArgument1()).getURI();
			ObjectPropertyArgument argument1 = argumentFactory.createObjectPropertyArgument(propertyArgumentURI);
			classAtom.setArgument1(argument1);
			classAtom.addReferencedPropertyURI(propertyArgumentURI);
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty) {
			String propertyArgumentURI = ((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)atom.getArgument1()).getURI();
			DataPropertyArgument argument1 = argumentFactory.createDataPropertyArgument(propertyArgumentURI);
			classAtom.setArgument1(argument1);
			classAtom.addReferencedPropertyURI(propertyArgumentURI);
		} else
			throw new OWLConversionFactoryException("unexpected argument to class atom " + atom.getBrowserText() + "; expecting "
					+ "variable or individual, got instance of " + atom.getArgument1().getClass());

		return classAtom;
	}

	private SWRLObjectPropertyAtomReference convertSWRLObjectPropertyAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom atom)
		throws OWLConversionFactoryException
	{
		String propertyURI = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getURI() : null;
		P3SWRLObjectPropertyAtomReference swrlObjectPropertyAtom = new P3SWRLObjectPropertyAtomReference(propertyURI);

		if (propertyURI == null)
			throw new OWLConversionFactoryException("empty property name in SWRLIndividualPropertyAtom: " + atom.getBrowserText());

		swrlObjectPropertyAtom.addReferencedPropertyURI(propertyURI);

		if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(variable.getLocalName());
			swrlObjectPropertyAtom.setArgument1(argument1);
			swrlObjectPropertyAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individual.getURI());
			swrlObjectPropertyAtom.setArgument1(argument1);
			swrlObjectPropertyAtom.addReferencedIndividualURI(argument1.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected first argument to individual property atom " + atom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

		if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(variable.getLocalName());
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
			SWRLIndividualArgumentReference argument2 = argumentFactory.createIndividualArgument(individual.getURI());
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedIndividualURI(argument2.getURI());
		} else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
			edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)atom.getArgument2();
			ClassArgument argument2 = argumentFactory.createClassArgument(cls.getURI());
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedClassURI(argument2.getURI());
		} else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
			edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)atom.getArgument2();
			PropertyArgument argument2;
			String propertyArgumentURI = ((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)property).getURI();
			if (property.isObjectProperty())
				argument2 = argumentFactory.createObjectPropertyArgument(propertyArgumentURI);
			else
				argument2 = argumentFactory.createDataPropertyArgument(propertyArgumentURI);
			swrlObjectPropertyAtom.setArgument2(argument2);
			swrlObjectPropertyAtom.addReferencedPropertyURI(propertyArgumentURI);
		} else
			throw new OWLConversionFactoryException("unexpected second argument to individual property atom " + atom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + atom.getArgument2().getClass());

		return swrlObjectPropertyAtom;
	}

	private SWRLDataPropertyAtomReference convertDatavaluedPropertyAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom atom)
		throws OWLConversionFactoryException
	{
		String propertyURI = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getURI() : null;
		P3SWRLDataPropertyAtomReference datavaluedPropertyAtom = new P3SWRLDataPropertyAtomReference(propertyURI);

		if (propertyURI == null)
			throw new OWLConversionFactoryException("empty property name in SWRLDatavaluedPropertyAtom: " + atom.getBrowserText());

		datavaluedPropertyAtom.addReferencedPropertyURI(propertyURI);

		if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(variable.getLocalName());
			datavaluedPropertyAtom.setArgument1(argument1);
			datavaluedPropertyAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individual.getURI());
			datavaluedPropertyAtom.setArgument1(argument1);
			datavaluedPropertyAtom.addReferencedIndividualURI(argument1.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected argument first to datavalued property atom '" + atom.getBrowserText()
					+ "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

		if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(variable.getLocalName());
			datavaluedPropertyAtom.setArgument2(argument2);
			datavaluedPropertyAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument2() instanceof RDFSLiteral) {
			SWRLLiteralArgumentReference argument2 = convertRDFSLiteral2DataValueArgument(owlModel, (RDFSLiteral)atom.getArgument2());
			datavaluedPropertyAtom.setArgument2(argument2);
		} else
			throw new OWLConversionFactoryException("unexpected second to datavalued property atom " + atom.getBrowserText()
					+ " - expecting variable or literal, got instance of " + atom.getArgument2().getClass());

		return datavaluedPropertyAtom;
	}

	private SWRLSameIndividualAtomReference convertSameIndividualAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom atom)
		throws OWLConversionFactoryException
	{
		P3SWRLSameIndividualAtomReference sameIndividualAtom = new P3SWRLSameIndividualAtomReference();

		if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(variable.getLocalName());
			sameIndividualAtom.setArgument1(argument1);
			sameIndividualAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individual.getURI());
			sameIndividualAtom.setArgument1(argument1);
			sameIndividualAtom.addReferencedIndividualURI(individual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected first argument to atom '" + atom.getBrowserText()
					+ "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass() + ".");

		if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(variable.getLocalName());
			sameIndividualAtom.setArgument2(argument2);
			sameIndividualAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
			SWRLIndividualArgumentReference argument2 = argumentFactory.createIndividualArgument(individual.getURI());
			sameIndividualAtom.setArgument2(argument2);
			sameIndividualAtom.addReferencedIndividualURI(individual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected second argument to atom " + atom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + atom.getArgument2().getClass());

		return sameIndividualAtom;
	}

	private SWRLDifferentIndividualsAtomReference convertDifferentIndividualsAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom atom)
		throws OWLConversionFactoryException
	{
		P3SWRLDifferentIndividualsAtom differentIndividualsAtom = new P3SWRLDifferentIndividualsAtom();

		if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
			SWRLVariableReference argument1 = argumentFactory.createVariableArgument(variable.getLocalName());
			differentIndividualsAtom.setArgument1(argument1);
			differentIndividualsAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
			SWRLIndividualArgumentReference argument1 = argumentFactory.createIndividualArgument(individual.getURI());
			differentIndividualsAtom.setArgument1(argument1);
			differentIndividualsAtom.addReferencedIndividualURI(individual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected first argument to atom " + atom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

		if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
			edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
			SWRLVariableReference argument2 = argumentFactory.createVariableArgument(variable.getLocalName());
			differentIndividualsAtom.setArgument2(argument2);
			differentIndividualsAtom.addReferencedVariableName(variable.getLocalName());
		} else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
			SWRLIndividualArgumentReference argument2 = argumentFactory.createIndividualArgument(individual.getURI());
			differentIndividualsAtom.setArgument2(argument2);
			differentIndividualsAtom.addReferencedIndividualURI(individual.getURI());
		} else
			throw new OWLConversionFactoryException("unexpected second argument to atom " + atom.getBrowserText()
					+ " - expecting variable or individual, got instance of " + atom.getArgument2().getClass());

		return differentIndividualsAtom;
	}

	private SWRLBuiltInAtomReference convertBuiltInAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom atom) throws OWLConversionFactoryException
	{
		String builtInName = (atom.getBuiltin() != null) ? atom.getBuiltin().getURI() : null;
		String builtInPrefixedName = (atom.getBuiltin() != null) ? atom.getBuiltin().getPrefixedName() : null;
		P3SWRLBuiltInAtomReference builtInAtom = new P3SWRLBuiltInAtomReference(builtInName, builtInPrefixedName);
		List<BuiltInArgument> arguments = new ArrayList<BuiltInArgument>();
		RDFList rdfList = atom.getArguments();

		if (builtInName == null)
			throw new OWLConversionFactoryException("empty built-in name in SWRLBuiltinAtom: " + atom.getBrowserText());

		Iterator<?> iterator = rdfList.getValues().iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
				edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)o;
				arguments.add(argumentFactory.createVariableArgument(variable.getLocalName()));
				builtInAtom.addReferencedVariableName(variable.getLocalName());
			} else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
				edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
				arguments.add(argumentFactory.createIndividualArgument(individual.getURI()));
				builtInAtom.addReferencedIndividualURI(individual.getURI());
			} else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
				edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)o;
				arguments.add(argumentFactory.createClassArgument(cls.getURI()));
				builtInAtom.addReferencedClassURI(cls.getURI());
			} else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
				edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)o;
				String propertyArgumentURI = ((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)property).getURI();
				if (property.isObjectProperty())
					arguments.add(argumentFactory.createObjectPropertyArgument(propertyArgumentURI));
				else
					arguments.add(argumentFactory.createDataPropertyArgument(propertyArgumentURI));
				builtInAtom.addReferencedPropertyURI(propertyArgumentURI);
			} else if (o instanceof RDFSLiteral)
				arguments.add(convertRDFSLiteral2DataValueArgument(owlModel, (RDFSLiteral)o));
			else {
				try {
					arguments.add(argumentFactory.createDataValueArgument(o));
				} catch (DataValueConversionException e) {
					throw new OWLConversionFactoryException("error converting argument to built-in " + builtInPrefixedName + " with value " + o + " of unknown type "
							+ o.getClass() + ": " + e.getMessage());
				} // try
			} 
		} 

		builtInAtom.setBuiltInArguments(arguments);

		return builtInAtom;
	}

	private void write2OWLModel(OWLClassAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		String classURI = axiom.getDescription().getURI();
		String individualURI = axiom.getIndividual().getURI();
		SWRLOWLUtil.addType(owlModel, individualURI, classURI);
	} // write2OWLModel

	private void write2OWLModel(OWLClassPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		String objectClassURI = axiom.getObject().getURI();
		edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
		edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
		edu.stanford.smi.protegex.owl.model.OWLNamedClass objectClass = null;

		if (property == null)
			throw new OWLConversionFactoryException("invalid property name: " + propertyURI);

		subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (subjectIndividual == null)
			throw new OWLConversionFactoryException("invalid individual URI " + subjectIndividualURI);

		objectClass = SWRLOWLUtil.getOWLNamedClass(owlModel, objectClassURI);

		if (!subjectIndividual.hasPropertyValue(property, objectClass, false))
			subjectIndividual.addPropertyValue(property, objectClass);
	}

	private void write2OWLModel(OWLDataPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);

		if (property == null)
			throw new OWLConversionFactoryException("invalid property URI " + propertyURI);

		edu.stanford.smi.protegex.owl.model.RDFSDatatype rangeDatatype = property.getRangeDatatype();
		Object objectValue;

		subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (subjectIndividual == null)
			throw new OWLConversionFactoryException("invalid individual URI " + subjectIndividualURI);

		if (rangeDatatype == null) {
			OWLDataValue dataValue = owlDataValueFactory.getOWLDataValue(axiom.getObject());
			if (dataValue.isString())
				objectValue = dataValue.toString();
			else
				objectValue = axiom.getObject().toString();
		} else
			objectValue = owlModel.createRDFSLiteral(axiom.getObject().toString(), rangeDatatype);

		if (!subjectIndividual.hasPropertyValue(property, objectValue, false))
			subjectIndividual.addPropertyValue(property, objectValue);
	}

	private void write2OWLModel(OWLObjectPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual, objectIndividual;
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		String objectIndividualURI = axiom.getObject().getURI();
		edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);

		if (property == null)
			throw new OWLConversionFactoryException("invalid property URI" + propertyURI);

		subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (subjectIndividual == null)
			throw new OWLConversionFactoryException("invalid subject individual URI " + subjectIndividualURI);

		objectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, objectIndividualURI);
		if (objectIndividual == null)
			throw new OWLConversionFactoryException("invalid object individual URI " + objectIndividualURI);

		if (!subjectIndividual.hasPropertyValue(property, objectIndividual, false))
			subjectIndividual.addPropertyValue(property, objectIndividual);
	}

	private void write2OWLModel(OWLPropertyPropertyAssertionAxiomReference axiom) throws OWLConversionFactoryException
	{
		String propertyURI = axiom.getProperty().getURI();
		String subjectIndividualURI = axiom.getSubject().getURI();
		String objectPropertyURI = axiom.getObject().getURI();
		edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
		edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
		edu.stanford.smi.protegex.owl.model.OWLProperty objectProperty;

		if (property == null)
			throw new OWLConversionFactoryException("invalid property URI " + propertyURI);

		subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualURI);
		if (subjectIndividual == null)
			throw new OWLConversionFactoryException("invalid subject individual URI" + subjectIndividualURI);

		objectProperty = SWRLOWLUtil.getOWLProperty(owlModel, objectPropertyURI);
		if (objectProperty == null)
			throw new OWLConversionFactoryException("invalid object individual URI" + objectPropertyURI);

		if (!subjectIndividual.hasPropertyValue(property, objectProperty, false))
			subjectIndividual.addPropertyValue(property, objectProperty);
	}

	private void write2OWLModel(OWLSomeValuesFromReference axiom) throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom someValuesFrom = SWRLOWLUtil.getOWLSomeValuesFrom(owlModel, axiom.asOWLClass().getURI());
		edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, axiom.getProperty().getURI());
		edu.stanford.smi.protegex.owl.model.RDFResource filler = SWRLOWLUtil.getClass(owlModel, axiom.getSomeValuesFrom().getURI());

		someValuesFrom.setOnProperty(property);
		someValuesFrom.setFiller(filler);
	}

	private void write2OWLModel(OWLSubClassAxiomReference axiom) throws OWLConversionFactoryException
	{
		String subClassURI = axiom.getSubClass().getURI();
		String superClassURI = axiom.getSuperClass().getURI();
		SWRLOWLUtil.addOWLSuperClass(owlModel, subClassURI, superClassURI);
	}

	private SWRLDataRangeAtomReference convertDataRangeAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom atom) throws OWLConversionFactoryException
	{
		throw new OWLConversionFactoryException("SWRL data range atoms not implemented.");
	}

	private SWRLAtomReference convertSWRLAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom swrlAtom) throws OWLConversionFactoryException
	{
		SWRLAtomReference atom;

		if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom) {
			atom = convertClassAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom)swrlAtom);
		} else if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom) {
			atom = convertDatavaluedPropertyAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom)swrlAtom);
		} else if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom) {
			atom = convertSWRLObjectPropertyAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom)swrlAtom);
		} else if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom) {
			atom = convertSameIndividualAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom)swrlAtom);
		} else if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom) {
			atom = convertDifferentIndividualsAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom)swrlAtom);
		} else if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom) {
			atom = convertBuiltInAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom)swrlAtom);
		} else if (swrlAtom instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom)
			atom = convertDataRangeAtom((edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom)swrlAtom);
		else
			throw new OWLConversionFactoryException("invalid SWRL atom: " + swrlAtom.getBrowserText());

		return atom;
	}

	// Utility method to create a collection of OWL property assertion axioms for every subject/predicate combination for a particular OWL
	// property. TODO: This is incredibly inefficient.

	public Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String propertyURI)
		throws OWLConversionFactoryException, DataValueConversionException
	{
		return getOWLPropertyAssertionAxioms(null, propertyURI);
	}

	private void calculateTransitiveSubAndEquivalentPropertyClosure(edu.stanford.smi.protegex.owl.model.OWLProperty property,
																																	Set<edu.stanford.smi.protegex.owl.model.OWLProperty> closure)
	{
		Set<edu.stanford.smi.protegex.owl.model.OWLProperty> properties = new HashSet<edu.stanford.smi.protegex.owl.model.OWLProperty>();

		properties.add(property);
		calculateTransitiveSubAndEquivalentPropertyClosure(properties, closure);
	}

	private void calculateTransitiveSubAndEquivalentPropertyClosure(Set<edu.stanford.smi.protegex.owl.model.OWLProperty> properties,
																																	Set<edu.stanford.smi.protegex.owl.model.OWLProperty> closure)
	{
		for (edu.stanford.smi.protegex.owl.model.OWLProperty property : properties) {
			if (!closure.contains(property)) {
				closure.add(property);
				calculateTransitiveSubAndEquivalentPropertyClosure(SWRLOWLUtil.getSubPropertiesOf(owlModel, property), closure);
				calculateTransitiveSubAndEquivalentPropertyClosure(SWRLOWLUtil.getEquivalentPropertiesOf(owlModel, property), closure);
			} 
		} 
	}

	public Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String subjectURI, String propertyURI)
		throws OWLConversionFactoryException, DataValueConversionException
	{
		Set<OWLPropertyAssertionAxiomReference> propertyAssertions = new HashSet<OWLPropertyAssertionAxiomReference>();
		edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
		edu.stanford.smi.protegex.owl.model.OWLIndividual subject = (subjectURI == null) ? null : SWRLOWLUtil.getOWLIndividual(owlModel, subjectURI);
		TripleStoreModel tsm = owlModel.getTripleStoreModel();
		List<edu.stanford.smi.protegex.owl.model.RDFResource> subjects = new ArrayList<edu.stanford.smi.protegex.owl.model.RDFResource>();
		Set<edu.stanford.smi.protegex.owl.model.OWLProperty> expandedProperties = new HashSet<edu.stanford.smi.protegex.owl.model.OWLProperty>();

		if (property == null)
			throw new InvalidPropertyNameException(propertyURI);

		calculateTransitiveSubAndEquivalentPropertyClosure(property, expandedProperties);

		for (TripleStore ts : tsm.getTripleStores()) {
			for (RDFProperty localProperty : expandedProperties) {
				Iterator<RDFResource> si = ts.listSubjects(localProperty);
				while (si.hasNext())
					subjects.add(si.next());
			} 
		} 

		for (RDFResource localSubject : subjects) {

			if (!(localSubject instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual))
				continue; // Deal only with OWL individuals (could return metaclass, for example)
			if (subject != null && !localSubject.getURI().equals(subject.getURI()))
				continue; // If subject supplied, ensure it is same
			edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)localSubject;

			for (Object object : localSubject.getPropertyValues(property, true)) {
				OWLPropertyAssertionAxiomReference axiom;

				if (property.hasObjectRange()) { // Object property
					OWLObjectPropertyReference objectProperty = owlFactory.getOWLObjectProperty(propertyURI);

					if (object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
						edu.stanford.smi.protegex.owl.model.OWLIndividual objectIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
						OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
						OWLNamedIndividualReference objectOWLIndividual = owlFactory.getOWLIndividual(objectIndividual.getURI());
						axiom = owlFactory.getOWLObjectPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectOWLIndividual);
						propertyAssertions.add(axiom);
					} else if (object instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) { // This will be OWL Full
						edu.stanford.smi.protegex.owl.model.OWLNamedClass objectClass = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)object;
						OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
						OWLClassReference objectPropertyClassValue = owlFactory.getOWLClass(objectClass.getURI());
						axiom = owlFactory.getOWLClassPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectPropertyClassValue);
						propertyAssertions.add(axiom);
					} else if (object instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) { // This will be OWL Full
						edu.stanford.smi.protegex.owl.model.OWLProperty objectPropertyValue = (edu.stanford.smi.protegex.owl.model.OWLProperty)object;
						OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
						OWLPropertyReference objectPropertyPropertyValue;
						if (objectPropertyValue.isObjectProperty())
							objectPropertyPropertyValue = owlFactory.getOWLObjectProperty(objectPropertyValue.getURI());
						else
							objectPropertyPropertyValue = owlFactory.getOWLDataProperty(objectPropertyValue.getURI());
						axiom = owlFactory.getOWLPropertyPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectPropertyPropertyValue);
						propertyAssertions.add(axiom);
					} 
				} else { // DataProperty
					OWLNamedIndividualReference subjectOWLIndividual = owlFactory.getOWLIndividual(subjectIndividual.getURI());
					RDFSLiteral rdfsLiteral = owlModel.asRDFSLiteral(object);
					OWLLiteralReference literal = convertRDFSLiteral2OWLLiteral(owlModel, rdfsLiteral);
					OWLDataPropertyReference dataProperty = owlFactory.getOWLDataProperty(propertyURI);
					axiom = owlFactory.getOWLDataPropertyAssertionAxiom(subjectOWLIndividual, dataProperty, literal);
					propertyAssertions.add(axiom);
				} 
			} 
		} 

		return propertyAssertions;
	}

	public Set<OWLSameIndividualAxiomReference> getSameIndividualAxioms() throws OWLConversionFactoryException
	{
		RDFProperty sameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(owlModel);
		Set<OWLSameIndividualAxiomReference> result = new HashSet<OWLSameIndividualAxiomReference>();

		Iterator<?> individualsIterator1 = owlModel.listSubjects(sameAsProperty);
		while (individualsIterator1.hasNext()) {
			Object object1 = individualsIterator1.next();
			if (!(object1 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual))
				continue; // Deal only with OWL individuals (could return metaclass, for example)
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual1 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object1;
			Collection<?> individuals = (Collection<?>)individual1.getPropertyValues(sameAsProperty);
			Iterator<?> individualsIterator2 = individuals.iterator();
			while (individualsIterator2.hasNext()) {
				Object object2 = individualsIterator2.next();
				if (!(object2 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual))
					continue;
				edu.stanford.smi.protegex.owl.model.OWLIndividual individual2 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object2;
				result.add(owlFactory.getOWLSameIndividualAxiom(owlFactory.getOWLIndividual(individual1.getURI()), owlFactory.getOWLIndividual(individual2.getURI())));
			} 
		} 
		return result;
	}

	public Set<OWLDifferentIndividualsAxiomReference> getOWLDifferentIndividualsAxioms() throws OWLConversionFactoryException
	{
		RDFProperty differentFromProperty = SWRLOWLUtil.getOWLDifferentFromProperty(owlModel);
		Set<OWLDifferentIndividualsAxiomReference> result = new HashSet<OWLDifferentIndividualsAxiomReference>();
		Collection<?> allDifferents = SWRLOWLUtil.getOWLAllDifferents(owlModel);

		Iterator<?> individualsIterator1 = owlModel.listSubjects(differentFromProperty);
		while (individualsIterator1.hasNext()) {
			Object object1 = individualsIterator1.next();
			if (!(object1 instanceof OWLNamedIndividualReference))
				continue; // Deal only with OWL individuals (could return metaclass, for example)
			edu.stanford.smi.protegex.owl.model.OWLIndividual individual1 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object1;
			Collection<?> individuals = (Collection<?>)individual1.getPropertyValues(differentFromProperty);
			Iterator<?> individualsIterator2 = individuals.iterator();
			while (individualsIterator2.hasNext()) {
				Object object2 = individualsIterator2.next();
				if (!(object2 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual))
					continue;
				edu.stanford.smi.protegex.owl.model.OWLIndividual individual2 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object2;
				result.add(owlFactory.getOWLDifferentIndividualsAxiom(owlFactory.getOWLIndividual(individual1.getURI()),
						owlFactory.getOWLIndividual(individual2.getURI())));
			} 
		} 

		if (!allDifferents.isEmpty()) {
			Iterator<?> allDifferentsIterator = allDifferents.iterator();
			while (allDifferentsIterator.hasNext()) {
				OWLAllDifferent owlAllDifferent = (OWLAllDifferent)allDifferentsIterator.next();

				if (owlAllDifferent.getDistinctMembers().size() != 0) {
					OWLDifferentIndividualsAxiomReference axiom;
					Set<OWLNamedIndividualReference> individuals = new HashSet<OWLNamedIndividualReference>();

					Iterator<?> individualsIterator = owlAllDifferent.getDistinctMembers().iterator();
					while (individualsIterator.hasNext()) {
						RDFIndividual individual = (RDFIndividual)individualsIterator.next();
						if (individual instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) { // Ignore OWL individuals
							String individualURI = ((edu.stanford.smi.protegex.owl.model.OWLIndividual)individual).getURI();
							OWLNamedIndividualReference owlIndividual = owlFactory.getOWLIndividual(individualURI);
							individuals.add(owlIndividual);
						} 
					} 
					axiom = owlFactory.getOWLDifferentIndividualsAxiom(individuals);
					result.add(axiom);
				} 
			} 
		} 

		return result;
	}

	private void initializeProperty(P3OWLPropertyReference owlPropertyImpl, edu.stanford.smi.protegex.owl.model.OWLProperty property)
		throws OWLConversionFactoryException
	{
		for (String domainClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(property.getUnionDomain()))
			owlPropertyImpl.addDomainClass(getOWLClass(domainClassURI));
		for (String rangeClassURI : SWRLOWLUtil.rdfResources2OWLNamedClassURIs(property.getUnionRangeClasses()))
			owlPropertyImpl.addRangeClass(getOWLClass(rangeClassURI));
		for (String superPropertyURI : SWRLOWLUtil.rdfResources2OWLPropertyURIs(property.getSuperproperties(false)))
			if (property.isObjectProperty())
				owlPropertyImpl.addSuperProperty(getOWLObjectProperty(superPropertyURI));
			else
				owlPropertyImpl.addSuperProperty(getOWLDataProperty(superPropertyURI));
		for (String subPropertyURI : SWRLOWLUtil.rdfResources2OWLPropertyURIs(property.getSubproperties(false)))
			if (property.isObjectProperty())
				owlPropertyImpl.addSubProperty(getOWLObjectProperty(subPropertyURI));
			else
				owlPropertyImpl.addSubProperty(getOWLDataProperty(subPropertyURI));
		for (String equivalentPropertyURI : SWRLOWLUtil.rdfResources2OWLPropertyURIs(property.getEquivalentProperties()))
			if (property.isObjectProperty())
				owlPropertyImpl.addEquivalentProperty(getOWLObjectProperty(equivalentPropertyURI));
			else
				owlPropertyImpl.addEquivalentProperty(getOWLDataProperty(equivalentPropertyURI));
	}

	private void buildDefiningClasses(P3OWLNamedIndividualReference owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual)
		throws OWLConversionFactoryException
	{
		for (Object o : individual.getRDFTypes()) {
			RDFSClass cls = (RDFSClass)o;
			if (!cls.isAnonymous() && cls instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
				String classURI = ((edu.stanford.smi.protegex.owl.model.OWLNamedClass)cls).getURI();
				owlIndividualImpl.addType(getOWLClass(classURI));
			} 
		} 
	}

	private void buildSameAsIndividuals(P3OWLNamedIndividualReference owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual)
		throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.RDFProperty sameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(owlModel);

		if (individual.hasPropertyValue(sameAsProperty)) {
			Collection<?> individuals = (Collection<?>)individual.getPropertyValues(sameAsProperty);
			Iterator<?> individualsIterator = individuals.iterator();
			while (individualsIterator.hasNext()) {
				Object object = individualsIterator.next();
				if (!(object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual))
					continue;
				edu.stanford.smi.protegex.owl.model.OWLIndividual sameAsIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
				owlIndividualImpl.addSameAsIndividual(owlFactory.getOWLIndividual(sameAsIndividual.getURI()));
			} 
		} 
	}

	private void buildDifferentFromIndividuals(P3OWLNamedIndividualReference owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual)
		throws OWLConversionFactoryException
	{
		edu.stanford.smi.protegex.owl.model.RDFProperty differentFromProperty = SWRLOWLUtil.getOWLDifferentFromProperty(owlModel);

		if (individual.hasPropertyValue(differentFromProperty)) {
			Collection<?> individuals = (Collection<?>)individual.getPropertyValues(differentFromProperty);
			Iterator<?> individualsIterator = individuals.iterator();
			while (individualsIterator.hasNext()) {
				Object object = individualsIterator.next();
				if (!(object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual))
					continue;
				edu.stanford.smi.protegex.owl.model.OWLIndividual differentFromIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
				owlIndividualImpl.addDifferentFromIndividual(owlFactory.getOWLIndividual(differentFromIndividual.getURI()));
			} 
		} 
	}

	public OWLModel getOWLModel()
	{
		return owlModel;
	} // TODO: Protege-OWL dependency

}
