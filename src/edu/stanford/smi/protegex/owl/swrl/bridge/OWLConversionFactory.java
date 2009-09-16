
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.model.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 ** Factory to create bridge instances of OWL entities from Protege-OWL 3.4 API entities.
 */
public class OWLConversionFactory
{
  public static OWLClass createOWLClass(edu.stanford.smi.protegex.owl.model.OWLModel owlModel) 
  { 
    String anonymousName = SWRLOWLUtil.getNextAnonymousResourceName(owlModel);

    return new OWLClassImpl(anonymousName, anonymousName);
  } // createOWLClass

  public static OWLClass createOWLClass(edu.stanford.smi.protegex.owl.model.OWLModel owlModel, String classURI) throws OWLFactoryException 
  { 
    edu.stanford.smi.protegex.owl.model.OWLNamedClass owlNamedClass = null;
    String prefixedClassName;
    OWLClassImpl owlClassImpl = null;

    try {
      owlNamedClass = SWRLOWLUtil.getOWLNamedClass(owlModel, classURI);
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("invalid class name '" + classURI + "'");
    } // try

    prefixedClassName = owlNamedClass.getPrefixedName();

    owlClassImpl = new OWLClassImpl(classURI, prefixedClassName);

    if (!classURI.equals(OWLNames.Cls.THING)) {
      owlClassImpl.setSuperclassNames(SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses(true)));
      owlClassImpl.setDirectSuperClassNames(SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses()));
      owlClassImpl.setDirectSubClassNames(SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSubclasses()));
      owlClassImpl.setEquivalentClassNames(SWRLOWLUtil.rdfResources2OWLNamedClassNames(owlNamedClass.getEquivalentClasses()));
      Set<String> equivalentClassSuperclassNames = new HashSet<String>();

      for (String equivalentClassName : owlClassImpl.getEquivalentClassNames()) {
        edu.stanford.smi.protegex.owl.model.OWLNamedClass equivalentClass = null;

        try {
          equivalentClass = SWRLOWLUtil.getOWLNamedClass(owlModel, equivalentClassName);
        } catch (SWRLOWLUtilException e) {
          throw new InvalidClassNameException(classURI);
        } // try
        
        Iterator equivalentClassSuperClassesIterator = equivalentClass.getNamedSuperclasses(true).iterator();
        while (equivalentClassSuperClassesIterator.hasNext()) {
          Object o = equivalentClassSuperClassesIterator.next();
          if (o instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) { // Ignore anonymous classes
            edu.stanford.smi.protegex.owl.model.OWLNamedClass equivalentClassSuperclass = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)o;
            equivalentClassSuperclassNames.add(equivalentClassSuperclass.getName());
          } // if
        } /// while
      } // for

      owlClassImpl.setEquivalentClassSuperclassNames(equivalentClassSuperclassNames);
    } // if

    return owlClassImpl;
  } // createOWLClass

  public static OWLClass createOWLClass(edu.stanford.smi.protegex.owl.model.OWLNamedClass cls) throws OWLFactoryException 
  { 
    return createOWLClass(cls.getOWLModel(), cls.getName()); 
  } // createOWLClass

  public static OWLIndividual createOWLIndividual(edu.stanford.smi.protegex.owl.model.OWLModel owlModel, String individualURI) throws OWLFactoryException 
  { 
    edu.stanford.smi.protegex.owl.model.OWLIndividual individual = owlModel.getOWLIndividual(individualURI);
    OWLIndividualImpl owlIndividualImpl = null;
    if (individual == null) throw new OWLFactoryException("invalid individual name '" + individualURI + "'");

    owlIndividualImpl = new OWLIndividualImpl(individualURI, individual.getPrefixedName());

    buildDefiningClasses(owlIndividualImpl, individual);
    buildDefiningSuperclasses(owlIndividualImpl, individual);
    buildDefiningEquivalentClasses(owlIndividualImpl, individual);
    buildSameAsIndividuals(owlIndividualImpl, individual);


    return owlIndividualImpl; 
  } // createOWLIndividual

  public static OWLIndividual createOWLIndividual(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException 
  { 
    return createOWLIndividual(individual.getOWLModel(), individual.getName()); 
  } // createOWLIndividual

  public static OWLObjectProperty createOWLObjectProperty(edu.stanford.smi.protegex.owl.model.OWLModel owlModel, String propertyURI) throws OWLFactoryException 
  {
    edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    OWLObjectPropertyImpl owlObjectPropertyImpl = null;
    String prefixedPropertyName;

    if (property == null) throw new OWLFactoryException("invalid property name '" + propertyURI + "'");

    prefixedPropertyName = property.getPrefixedName();

    owlObjectPropertyImpl = new OWLObjectPropertyImpl(propertyURI, prefixedPropertyName);

    initializeProperty(owlObjectPropertyImpl, owlModel, property);

    return owlObjectPropertyImpl;
  } // createOWLObjectProperty

  public static OWLObjectProperty createOWLObjectProperty(edu.stanford.smi.protegex.owl.model.OWLObjectProperty property) throws OWLFactoryException 
  { 
    return createOWLObjectProperty(property.getOWLModel(), property.getName()); 
  } // createOWLObjectProperty

  public static OWLDatatypeProperty createOWLDatatypeProperty(edu.stanford.smi.protegex.owl.model.OWLModel owlModel, String propertyURI) throws OWLFactoryException 
  { 
    edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    OWLDatatypePropertyImpl owlDatatypePropertyImpl = null;
    String prefixedPropertyName;

    if (property == null) throw new OWLFactoryException("invalid property name '" + propertyURI + "'");

    prefixedPropertyName = property.getPrefixedName();

    owlDatatypePropertyImpl = new OWLDatatypePropertyImpl(propertyURI, prefixedPropertyName);

    initializeProperty(owlDatatypePropertyImpl, owlModel, property);

    return owlDatatypePropertyImpl;
  } // createOWLDatatypeProperty

  private static void initializeProperty(OWLPropertyImpl owlPropertyImpl, edu.stanford.smi.protegex.owl.model.OWLModel owlModel, 
                                         edu.stanford.smi.protegex.owl.model.OWLProperty property)
  {
    owlPropertyImpl.setDomainClassNames(SWRLOWLUtil.rdfResources2Names(property.getUnionDomain(true)));
    owlPropertyImpl.setRangeClassNames(SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses()));
    owlPropertyImpl.setSuperPropertyNames(SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true)));
    owlPropertyImpl.setSubPropertyNames(SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true)));
    owlPropertyImpl.setEquivalentPropertyNames(SWRLOWLUtil.rdfResources2Names(property.getEquivalentProperties()));
    Set<String> equivalentPropertySuperPropertyNames = new HashSet<String>();

    for (String equivalentPropertyName : owlPropertyImpl.getEquivalentPropertyNames()) {
      edu.stanford.smi.protegex.owl.model.RDFProperty equivalentProperty = SWRLOWLUtil.getOWLProperty(owlModel, equivalentPropertyName);
      Iterator equivalentPropertySuperPropertiesIterator = equivalentProperty.getSuperproperties(true).iterator();
      while (equivalentPropertySuperPropertiesIterator.hasNext()) {
        edu.stanford.smi.protegex.owl.model.RDFProperty equivalentPropertySuperProperty = (edu.stanford.smi.protegex.owl.model.RDFProperty)equivalentPropertySuperPropertiesIterator.next();
        equivalentPropertySuperPropertyNames.add(equivalentPropertySuperProperty.getName());
      } /// while
    } // for

    owlPropertyImpl.setEquivalentPropertySuperPropertyNames(equivalentPropertySuperPropertyNames);
  } // initializeProperty

  public static OWLDatatypeProperty createOWLDatatypeProperty(edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty property) 
    throws OWLFactoryException 
  { 
    return createOWLDatatypeProperty(property.getOWLModel(), property.getName()); 
  } // createOWLDatatypeProperty

  public static OWLDatatypeValue createOWLDatatypeValue(Object o) throws OWLFactoryException 
  {
    OWLDatatypeValue result = null;

    try {
      result = new OWLDatatypeValueImpl(o);
    } catch (DatatypeConversionException e) {
      throw new OWLFactoryException("error creating an OWLDatatypeValue object for object '" + o + "': " + e.getMessage());
    } // try
    return result;
  } // createOWLDatatypeValue

  public static OWLDatatypeValue createOWLDatatypeValue(edu.stanford.smi.protegex.owl.model.OWLModel owlModel, 
                                                        edu.stanford.smi.protegex.owl.model.RDFSLiteral literal) throws OWLFactoryException 
  { 
    edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype = literal.getDatatype();
    OWLDatatypeValueImpl owlDatatypeValueImpl = null;

    try {
      if ((datatype == owlModel.getXSDint()) || (datatype == owlModel.getXSDinteger()))
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getInt());
      else if (datatype == owlModel.getXSDshort()) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getShort());
      else if (datatype == owlModel.getXSDlong()) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getLong());
      else if (datatype == owlModel.getXSDboolean()) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getBoolean());
      else if (datatype == owlModel.getXSDfloat()) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getFloat());
      else if (datatype == owlModel.getXSDdouble()) 
      owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getDouble());
      else if ((datatype == owlModel.getXSDstring())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(literal.getString());
      else if ((datatype == owlModel.getXSDtime())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(new Time(literal.getString()));
      else if ((datatype == owlModel.getXSDanyURI())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(new AnyURI(literal.getString()));
      else if ((datatype == owlModel.getXSDbase64Binary())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(new Base64Binary(literal.getString()));
      else if ((datatype == owlModel.getXSDbyte())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(Byte.valueOf(literal.getString()));
      else if ((datatype == owlModel.getXSDduration())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(new Duration(literal.getString()));
      else if ((datatype == owlModel.getXSDdateTime())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(new DateTime(literal.getString()));
      else if ((datatype == owlModel.getXSDdate())) 
        owlDatatypeValueImpl = new OWLDatatypeValueImpl(new Date(literal.getString()));
      else throw new OWLFactoryException("cannot create an OWLDatatypeValue object for RDFS literal '" + literal.getString()
                                       + "' of type '" + datatype + "'");
    } catch (DatatypeConversionException e) {
      throw new OWLFactoryException("error creating an OWLDatatypeValue object for RDFS literal value '" + literal.getString() + 
                                    "' with type '" + datatype.getName() + "': " + e.getMessage());
    } // try

    return owlDatatypeValueImpl;
  } // createOWLDatatypeValue

  /*
  public static edu.stanford.smi.protegex.owl.model.RDFSLiteral createRDFSLiteral(OWLDatatypeValue owlDatatypeValue, 
                                                                                  edu.stanford.smi.protegex.owl.model.OWLModel owlModel) 
    throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.RDFSLiteral literal = null;

    try {
      if (owlDatatypeValue.isString()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getString());
      else if (owlDatatypeValue.isInteger()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getInt());
      else if (owlDatatypeValue.isLong()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getLong());
      else if (owlDatatypeValue.isBoolean()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getBoolean());
      else if (owlDatatypeValue.isFloat()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getFloat());
      else if (owlDatatypeValue.isDouble()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getDouble());
      else if (owlDatatypeValue.isShort()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getShort());
      else if (owlDatatypeValue.isBigDecimal()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getBigDecimal());
      else if (owlDatatypeValue.isBigInteger()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getBigInteger());
      else if (owlDatatypeValue.isByte()) literal = owlModel.asRDFSLiteral(owlDatatypeValue.getByte());
      else throw new OWLFactoryException("cannot convert OWLDatatypeValue with value '" + owlDatatypeValue + "' to RDFSLiteral");
    } catch (DatatypeConversionException e) {
      throw new OWLFactoryException("error creating an RDFSLiteral object for RDFS literal '" + literal.getString() + "': " + e.getMessage());
    } // try

    return literal;
  } // createRDFSLiteral
  */

  public static ClassAtom createClassAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom atom) throws OWLFactoryException 
  { 
    String classURI = (atom.getClassPredicate() != null) ? atom.getClassPredicate().getName() : null;
    String prefixedClassName = (atom.getClassPredicate() != null) ? atom.getClassPredicate().getPrefixedName() : null;
    ClassAtomImpl classAtomImpl = new ClassAtomImpl(classURI, prefixedClassName);

    if (classURI == null) throw new OWLFactoryException("empty class name in SWRLClassAtom: " + atom);

    classAtomImpl.addReferencedClassName(classURI);
    
    if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
      VariableAtomArgument argument1 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      classAtomImpl.setArgument1(argument1);
      classAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      OWLIndividual argument1 = createOWLIndividual((edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1());
      classAtomImpl.setArgument1(argument1);
      classAtomImpl.addReferencedIndividualName(argument1.getIndividualName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
      OWLClass argument1 = createOWLClass((edu.stanford.smi.protegex.owl.model.OWLNamedClass)atom.getArgument1());
      classAtomImpl.setArgument1(argument1);
      classAtomImpl.addReferencedClassName(argument1.getClassName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLObjectProperty) {
      OWLObjectProperty argument1 = createOWLObjectProperty((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)atom.getArgument1());
      classAtomImpl.setArgument1(argument1);
      classAtomImpl.addReferencedPropertyName(argument1.getPropertyName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty) {
      OWLDatatypeProperty argument1 = createOWLDatatypeProperty((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)atom.getArgument1());
      classAtomImpl.setArgument1(argument1);
      classAtomImpl.addReferencedPropertyName(argument1.getPropertyName());
    } else throw new OWLFactoryException("unexpected argument to class atom '" + atom.getBrowserText() + "'; expecting " +
                                         "variable or individual, got instance of '" + atom.getArgument1().getClass() + "'");

    return classAtomImpl;
  } // createClassAtom

  public static IndividualPropertyAtom createIndividualPropertyAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom atom)
    throws OWLFactoryException 
  { 
    String propertyURI = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getName() : null;
    String prefixedPropertyName = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getPrefixedName() : null;
    IndividualPropertyAtomImpl individualPropertyAtomImpl = new IndividualPropertyAtomImpl(propertyURI, prefixedPropertyName);

    if (propertyURI == null) throw new OWLFactoryException("empty property name in SWRLIndividualPropertyAtom '" + atom + "'");
    
    individualPropertyAtomImpl.addReferencedPropertyName(propertyURI);

    if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
      VariableAtomArgument argument1 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      individualPropertyAtomImpl.setArgument1(argument1);
      individualPropertyAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
      OWLIndividual argument1 = createOWLIndividual(individual);
      individualPropertyAtomImpl.setArgument1(argument1);
      individualPropertyAtomImpl.addReferencedIndividualName(argument1.getIndividualName());
    } else throw new OWLFactoryException("unexpected first argument to individual property atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

    if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
      VariableAtomArgument argument2 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      individualPropertyAtomImpl.setArgument2(argument2);
      individualPropertyAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
      OWLIndividual argument2 = createOWLIndividual(individual);
      individualPropertyAtomImpl.setArgument2(argument2);
      individualPropertyAtomImpl.addReferencedIndividualName(argument2.getIndividualName());
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
      edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)atom.getArgument2();
      OWLClass argument2 = createOWLClass(cls);
      individualPropertyAtomImpl.setArgument2(argument2);
      individualPropertyAtomImpl.addReferencedClassName(argument2.getClassName());
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
      edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)atom.getArgument2();
      OWLProperty argument2;
      if (property.isObjectProperty()) 
        argument2 = createOWLObjectProperty((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)property);
      else 
        argument2 = createOWLDatatypeProperty((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)property);
      individualPropertyAtomImpl.setArgument2(argument2);
      individualPropertyAtomImpl.addReferencedPropertyName(argument2.getPropertyName());
    } else throw new OWLFactoryException("unexpected second argument to individual property atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument2().getClass());

    return individualPropertyAtomImpl; 
  } // createIndividualPropertyAtom

  public static DatavaluedPropertyAtom createDatavaluedPropertyAtom(edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom atom) 
    throws OWLFactoryException 
  { 
    String propertyURI = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getName() : null;
    String prefixedPropertyName = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getPrefixedName() : null;
    DatavaluedPropertyAtomImpl datavaluedPropertyAtomImpl = new DatavaluedPropertyAtomImpl(propertyURI, prefixedPropertyName);

    if (propertyURI == null) throw new OWLFactoryException("empty property name in SWRLDatavaluedPropertyAtom '" + atom.getBrowserText() + "'");

    datavaluedPropertyAtomImpl.addReferencedPropertyName(propertyURI);

    if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
      VariableAtomArgument argument1 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      datavaluedPropertyAtomImpl.setArgument1(argument1);
      datavaluedPropertyAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
      OWLIndividual argument1 = createOWLIndividual(individual);
      datavaluedPropertyAtomImpl.setArgument1(argument1);
      datavaluedPropertyAtomImpl.addReferencedIndividualName(argument1.getIndividualName());
    } else throw new OWLFactoryException("unexpected argument first to datavalued property atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

    if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
      VariableAtomArgument argument2 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      datavaluedPropertyAtomImpl.setArgument2(argument2);
      datavaluedPropertyAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument2() instanceof RDFSLiteral) {
      OWLDatatypeValue argument2 = createOWLDatatypeValue(atom.getOWLModel(), (RDFSLiteral)atom.getArgument2());
      datavaluedPropertyAtomImpl.setArgument2(argument2);
    } else throw new OWLFactoryException("unexpected second to datavalued property atom '" + atom.getBrowserText()  + 
                                         "' - expecting variable or literal, got instance of " + atom.getArgument2().getClass());
    
    return datavaluedPropertyAtomImpl; 
  } // createDatavaluedPropertyAtom

  public static SameIndividualAtom createSameIndividualAtom(SWRLSameIndividualAtom atom) throws OWLFactoryException 
  { 
    SameIndividualAtomImpl sameIndividualAtomImpl = new SameIndividualAtomImpl();

    if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
      VariableAtomArgument argument1 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      sameIndividualAtomImpl.setArgument1(argument1);
      sameIndividualAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
      OWLIndividual argument1 = createOWLIndividual(individual);
      sameIndividualAtomImpl.setArgument1(argument1);
      sameIndividualAtomImpl.addReferencedIndividualName(individual.getName());
    } else throw new OWLFactoryException("unexpected first argument to atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass() + ".");

    if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
      VariableAtomArgument argument2 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      sameIndividualAtomImpl.setArgument2(argument2);
      sameIndividualAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
      OWLIndividual argument2 = createOWLIndividual(individual);
      sameIndividualAtomImpl.setArgument2(argument2);
      sameIndividualAtomImpl.addReferencedIndividualName(individual.getName());
    } else throw new OWLFactoryException("unexpected second argument to atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument2().getClass() + ".");


    return sameIndividualAtomImpl; 
  } // createSameIndividualAtom

  public static DifferentIndividualsAtom createDifferentIndividualsAtom(SWRLDifferentIndividualsAtom atom) throws OWLFactoryException 
  { 
    DifferentIndividualsAtomImpl differentIndividualsAtomImpl = new DifferentIndividualsAtomImpl();

    if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument1();
      VariableAtomArgument argument1 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      differentIndividualsAtomImpl.setArgument1(argument1);
      differentIndividualsAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
      OWLIndividual argument1 = createOWLIndividual(individual);
      differentIndividualsAtomImpl.setArgument1(argument1);
      differentIndividualsAtomImpl.addReferencedIndividualName(individual.getName());
    } else throw new OWLFactoryException("unexpected first argument to atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass() + ".");

    if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
      edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)atom.getArgument2();
      VariableAtomArgument argument2 = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      differentIndividualsAtomImpl.setArgument2(argument2);
      differentIndividualsAtomImpl.addReferencedVariableName(variable.getName());
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
      OWLIndividual argument2 = createOWLIndividual(individual);
      differentIndividualsAtomImpl.setArgument2(argument2);
      differentIndividualsAtomImpl.addReferencedIndividualName(individual.getName());
    } else throw new OWLFactoryException("unexpected second argument to atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument2().getClass() + ".");


    return differentIndividualsAtomImpl; 
  } // createDifferentIndividualsAtom

  public static BuiltInAtom createBuiltInAtom(SWRLBuiltinAtom atom) throws OWLFactoryException  
  { 
    String builtInName = (atom.getBuiltin() != null) ? atom.getBuiltin().getName() : null;
    String builtInPrefixedName = (atom.getBuiltin() != null) ? atom.getBuiltin().getPrefixedName() : null;
    BuiltInAtomImpl builtInAtomImpl = new BuiltInAtomImpl(builtInName, builtInPrefixedName); 
    List<BuiltInArgument> arguments = new ArrayList<BuiltInArgument>();
    RDFList rdfList = atom.getArguments();

    if (builtInName == null) throw new OWLFactoryException("empty built-in name in SWRLBuiltinAtom: " + atom);

    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable) {
        edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable variable = (edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable)o;
        arguments.add(OWLFactory.createVariableBuiltInArgument(variable.getName(), variable.getPrefixedName()));
        builtInAtomImpl.addReferencedVariableName(variable.getName());
      } else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
        edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
        arguments.add(createOWLIndividual(individual));
        builtInAtomImpl.addReferencedIndividualName(individual.getName());
      } else  if (o instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
        edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)o;
        arguments.add(createOWLClass(atom.getOWLModel(), cls.getName()));
        builtInAtomImpl.addReferencedClassName(cls.getName());
      } else  if (o instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
        edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)o;
        if (property.isObjectProperty()) 
          arguments.add(createOWLObjectProperty((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)property));
        else 
          arguments.add(createOWLDatatypeProperty((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)property));
        builtInAtomImpl.addReferencedPropertyName(property.getName());
      } else  if (o instanceof RDFSLiteral) arguments.add(createOWLDatatypeValue(atom.getOWLModel(), (RDFSLiteral)o));
      else  if (o instanceof Number) arguments.add(OWLFactory.createOWLDatatypeValue((Number)o));
      else  if (o instanceof String) arguments.add(OWLFactory.createOWLDatatypeValue((String)o));
      else throw new OWLFactoryException("unknown type for argument '" + o + "' to built-in '" + builtInPrefixedName + "'");
    } // while

    builtInAtomImpl.setBuiltInArguments(arguments);

    return builtInAtomImpl; 
  } // createBuiltInAtom
  
  public static DataRangeAtom createDataRangeAtom(SWRLDataRangeAtom atom) throws OWLFactoryException 
  { 
    throw new OWLFactoryException("SWRL data range atoms not implemented.");
  } // createDataRangeAtom

  public static Atom createAtom(SWRLAtom swrlAtom) throws OWLFactoryException
  {
    Atom atom;
    
    if (swrlAtom instanceof SWRLClassAtom) {
      atom = createClassAtom((SWRLClassAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDatavaluedPropertyAtom) {
      atom = createDatavaluedPropertyAtom((SWRLDatavaluedPropertyAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLIndividualPropertyAtom) {
      atom = createIndividualPropertyAtom((SWRLIndividualPropertyAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLSameIndividualAtom) {
      atom = createSameIndividualAtom((SWRLSameIndividualAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDifferentIndividualsAtom) {
      atom = createDifferentIndividualsAtom((SWRLDifferentIndividualsAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLBuiltinAtom) {
      atom = createBuiltInAtom((SWRLBuiltinAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDataRangeAtom) 
      atom = createDataRangeAtom((SWRLDataRangeAtom)swrlAtom);
    else throw new OWLFactoryException("invalid SWRL atom '" + swrlAtom.getBrowserText() + "'");

    return atom;
  } // createAtom

  // Utility method to create a collection of OWL property asserion axioms for every subject/predicate combination for a particular OWL
  // property.  TODO: This is incredibly inefficient.

  public static Set<OWLPropertyAssertionAxiom> createOWLPropertyAssertionAxioms(edu.stanford.smi.protegex.owl.model.OWLModel owlModel, String propertyURI) throws OWLFactoryException, DatatypeConversionException
  {
    Set<OWLPropertyAssertionAxiom> propertyAssertions = new HashSet<OWLPropertyAssertionAxiom>();
    edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    OWLPropertyAssertionAxiom axiom;
    RDFResource subject;

    if (property == null) throw new InvalidPropertyNameException(propertyURI);

    TripleStoreModel tsm = owlModel.getTripleStoreModel();
    Iterator<RDFResource> it = tsm.listSubjects(property);
    while (it.hasNext()) {
      subject = it.next();
      if (!(subject instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
      edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)subject;
      
      for (Object object : subject.getPropertyValues(property)) {
        
        if (property.hasObjectRange()) { // Object property
          OWLObjectProperty objectProperty = OWLConversionFactory.createOWLObjectProperty(owlModel, propertyURI);

          if (object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
            edu.stanford.smi.protegex.owl.model.OWLIndividual objectIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
            OWLIndividual subjectOWLIndividual = OWLConversionFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
            OWLIndividual objectOWLIndividual = OWLConversionFactory.createOWLIndividual(owlModel, objectIndividual.getName());
            axiom = OWLFactory.createOWLObjectPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectOWLIndividual);
            propertyAssertions.add(axiom);
          } else if (object instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) { // This will be OWL Full
            edu.stanford.smi.protegex.owl.model.OWLNamedClass objectClass = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)object;
            OWLIndividual subjectOWLIndividual = OWLConversionFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
            OWLClass objectPropertyClassValue = OWLConversionFactory.createOWLClass(owlModel, objectClass.getName());
            axiom = OWLFactory.createOWLClassPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectPropertyClassValue);
            propertyAssertions.add(axiom);
          } else if (object instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) { // This will be OWL Full
            edu.stanford.smi.protegex.owl.model.OWLProperty objectPropertyValue = (edu.stanford.smi.protegex.owl.model.OWLProperty)object;
            OWLIndividual subjectOWLIndividual = OWLConversionFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
            OWLProperty objectPropertyPropertyValue;
            if (objectPropertyValue.isObjectProperty()) objectPropertyPropertyValue = OWLConversionFactory.createOWLObjectProperty(owlModel, objectPropertyValue.getName());
            else objectPropertyPropertyValue = OWLConversionFactory.createOWLDatatypeProperty(owlModel, objectPropertyValue.getName());
            axiom = OWLFactory.createOWLPropertyPropertyAssertionAxiom(subjectOWLIndividual, objectProperty, objectPropertyPropertyValue);
            propertyAssertions.add(axiom);                
          } // if
        } else { // DatatypeProperty
          OWLIndividual subjectOWLIndividual = OWLConversionFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
          RDFSLiteral literal = owlModel.asRDFSLiteral(object);
          OWLDatatypeValue datatypeValue = OWLConversionFactory.createOWLDatatypeValue(owlModel, literal);
          OWLDatatypeProperty datatypeProperty = OWLConversionFactory.createOWLDatatypeProperty(owlModel, propertyURI);
          axiom = OWLFactory.createOWLDatatypePropertyAssertionAxiom(subjectOWLIndividual, datatypeProperty, datatypeValue);
          propertyAssertions.add(axiom);
        } // if
      } // for
    } // while
      
    return propertyAssertions;
  } // createOWLPropertyAssertionAxioms

  public static void write2OWLModel(OWLIndividual owlIndividual, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    String individualURI = owlIndividual.getIndividualName();

    try {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual;

      if (SWRLOWLUtil.isIndividual(owlModel, individualURI)) individual = SWRLOWLUtil.getIndividual(owlModel, individualURI);
      else individual = SWRLOWLUtil.createIndividual(owlModel, individualURI);
      
      for (OWLClass owlClass : owlIndividual.getDefiningClasses()) {
        edu.stanford.smi.protegex.owl.model.RDFSClass cls = SWRLOWLUtil.getOWLNamedClass(owlModel, owlClass.getClassName());

        if (!individual.hasRDFType(cls)) { 
          if (individual.hasRDFType(SWRLOWLUtil.getOWLThingClass(owlModel))) individual.setRDFType(cls);
          else individual.addRDFType(cls);
        } // if
      } // for
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("error writing OWL individual '" + individualURI + "': " + e.getMessage());
    } // try
  } // write2OWLModel

  public static void write2OWLModel(OWLClass owlClass, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    String classURI = owlClass.getClassName();

    try {
      edu.stanford.smi.protegex.owl.model.OWLClass cls, superclass;

      if (SWRLOWLUtil.isClass(owlModel, classURI)) cls = SWRLOWLUtil.getOWLNamedClass(owlModel, classURI);
      else cls = SWRLOWLUtil.createOWLNamedClass(owlModel, classURI);
        
      for (String superclassName : owlClass.getSuperclassNames()) {
        if (SWRLOWLUtil.isClass(owlModel, superclassName)) superclass = SWRLOWLUtil.getOWLNamedClass(owlModel, superclassName);
        else superclass = SWRLOWLUtil.createOWLNamedClass(owlModel, superclassName);

        if (!cls.isSubclassOf(superclass)) cls.addSuperclass(superclass);
      } // for
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("error writing OWL class '" + classURI + "': " + e.getMessage());
    } // try
  } // write2OWLModel

  public static void write2OWLModel(OWLAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    if (axiom instanceof OWLClassAssertionAxiom) write2OWLModel((OWLClassAssertionAxiom)axiom, owlModel);
    else if (axiom instanceof OWLClassPropertyAssertionAxiom) write2OWLModel((OWLClassPropertyAssertionAxiom)axiom, owlModel);
    else if (axiom instanceof OWLDatatypePropertyAssertionAxiom) write2OWLModel((OWLDatatypePropertyAssertionAxiom)axiom, owlModel);
    else if (axiom instanceof OWLObjectPropertyAssertionAxiom) write2OWLModel((OWLObjectPropertyAssertionAxiom)axiom, owlModel);
    else if (axiom instanceof OWLPropertyPropertyAssertionAxiom) write2OWLModel((OWLPropertyPropertyAssertionAxiom)axiom, owlModel);
    else if (axiom instanceof OWLSomeValuesFrom) write2OWLModel((OWLSomeValuesFrom)axiom, owlModel);
    else if (axiom instanceof OWLSubClassAxiom) write2OWLModel((OWLSubClassAxiom)axiom, owlModel);
  } // write2OWLModel

  private static void write2OWLModel(OWLClassAssertionAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    String classURI = axiom.getDescription().getClassName();
    String individualURI = axiom.getIndividual().getIndividualName();

    try {
      SWRLOWLUtil.addClass(owlModel, individualURI, classURI);
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("exception creating OWLClassAssertionAxiom '" + axiom + "': " + e.getMessage());
    } // try
  } // write2OWLModel

  private static void write2OWLModel(OWLClassPropertyAssertionAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    String propertyURI = axiom.getProperty().getPropertyName();
    String subjectIndividualName = axiom.getSubject().getIndividualName();
    String objectClassName = axiom.getObject().getClassName();
    edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
    edu.stanford.smi.protegex.owl.model.OWLNamedClass objectClass = null;
    
    if (property == null) throw new OWLFactoryException("invalid property name '" + propertyURI + "'");
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new OWLFactoryException("invalid individual name '" + subjectIndividualName + "'");

    try {
      objectClass = SWRLOWLUtil.getOWLNamedClass(owlModel, objectClassName); 
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("invalid class name '" + objectClassName + "'");
    } // try

    if (!subjectIndividual.hasPropertyValue(property, objectClass, false)) subjectIndividual.addPropertyValue(property, objectClass);
  } // write2OWLModel

  private static void write2OWLModel(OWLDatatypePropertyAssertionAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) 
   throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
    String propertyURI = axiom.getProperty().getPropertyName();
    String subjectIndividualName = axiom.getSubject().getIndividualName();
    edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    edu.stanford.smi.protegex.owl.model.RDFSDatatype rangeDatatype = property.getRangeDatatype();
    Object objectValue;

    if (property == null) throw new OWLFactoryException("invalid property name '" + propertyURI + "'");
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new OWLFactoryException("invalid individual name '" + subjectIndividualName + "'");

    if (rangeDatatype == null) {
      if (axiom.getObject().isString()) objectValue = axiom.getObject().toString();
      else objectValue = axiom.getObject().toString();
    } else objectValue = owlModel.createRDFSLiteral(axiom.getObject().toString(), rangeDatatype);   

    if (!subjectIndividual.hasPropertyValue(property, objectValue, false)) subjectIndividual.addPropertyValue(property, objectValue);    
  } // write2OWLModel

  private static void write2OWLModel(OWLDifferentIndividualsAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) 
    throws OWLFactoryException
  {
    // TODO:
  } // write2OWLModel

  private static void write2OWLModel(OWLObjectPropertyAssertionAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) 
    throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual, objectIndividual;
    String propertyURI = axiom.getProperty().getPropertyName();
    String subjectIndividualName = axiom.getSubject().getIndividualName();
    String objectIndividualName = axiom.getObject().getIndividualName();
    edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    
    if (property == null) throw new OWLFactoryException("invalid property name '" + propertyURI + "'");
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new OWLFactoryException("invalid individual name '" + subjectIndividualName + "'");
 
    objectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, objectIndividualName); 
    if (objectIndividual == null) throw new OWLFactoryException("invalid individual name '" + objectIndividualName + "'");

    if (!subjectIndividual.hasPropertyValue(property, objectIndividual, false)) subjectIndividual.addPropertyValue(property, objectIndividual);
  } // write2OWLModel

  private static void write2OWLModel(OWLPropertyPropertyAssertionAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    String propertyURI = axiom.getProperty().getPropertyName();
    String subjectIndividualName = axiom.getSubject().getIndividualName();
    String objectPropertyName = axiom.getObject().getPropertyName();
    edu.stanford.smi.protegex.owl.model.RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyURI);
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
    edu.stanford.smi.protegex.owl.model.OWLProperty objectProperty;
    
    if (property == null) throw new OWLFactoryException("invalid property name '" + propertyURI + "'");
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new OWLFactoryException("invalid individual name '" + subjectIndividualName + "'");

    objectProperty = SWRLOWLUtil.getOWLProperty(owlModel, objectPropertyName); 
    if (objectProperty == null) throw new OWLFactoryException("invalid individual name '" + objectPropertyName + "'");

    if (!subjectIndividual.hasPropertyValue(property, objectProperty, false)) subjectIndividual.addPropertyValue(property, objectProperty);
  } // write2OWLModel

  private static void write2OWLModel(OWLSomeValuesFrom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    try {
      edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom someValuesFrom = SWRLOWLUtil.getOWLSomeValuesFrom(owlModel, axiom.asOWLClass().getClassName());
      edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, axiom.getProperty().getPropertyName());
      edu.stanford.smi.protegex.owl.model.RDFResource filler = SWRLOWLUtil.getClass(owlModel, axiom.getSomeValuesFrom().getClassName());
      
      someValuesFrom.setOnProperty(property);
      someValuesFrom.setFiller(filler); 
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("error writing someValuesFrom " + axiom + ": " + e.getMessage());
    } // try
  } // write2OWLModel

  private static void write2OWLModel(OWLSubClassAxiom axiom, edu.stanford.smi.protegex.owl.model.OWLModel owlModel) throws OWLFactoryException
  {
    String subClassName = axiom.getSubClass().getClassName();
    String superClassName = axiom.getSuperClass().getClassName();

    try {
      SWRLOWLUtil.addSuperClass(owlModel, subClassName, superClassName);
    } catch (SWRLOWLUtilException e) {
      throw new OWLFactoryException("exception creating OWLSubClassAxiom '" + axiom + "': " + e.getMessage());
    } // try
  } // write2OWLModel

  private static void buildDefiningClasses(OWLIndividualImpl owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  { 
    for (Object o : individual.getRDFTypes()) {
      RDFSClass cls = (RDFSClass)o; 
      if (!cls.isAnonymous() && cls instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) 
        owlIndividualImpl.addDefiningClass(OWLConversionFactory.createOWLClass((edu.stanford.smi.protegex.owl.model.OWLNamedClass)cls));
    } // for
  } // buildDefiningClasses

  private static void buildDefiningSuperclasses(OWLIndividualImpl owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual) 
    throws OWLFactoryException
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator superClassesIterator = definingClass.getNamedSuperclasses(true).iterator();
      while (superClassesIterator.hasNext()) {
        RDFSClass cls = (RDFSClass)superClassesIterator.next();
        if (cls instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
          OWLClass superClass = OWLConversionFactory.createOWLClass((edu.stanford.smi.protegex.owl.model.OWLNamedClass)cls);
          if (!owlIndividualImpl.getDefiningSuperclasses().contains(superClass)) owlIndividualImpl.addDefiningSuperclass(superClass);
        } // if
      } // while
    } // while
  } // buildDefiningSuperclasses

  private static void buildDefiningEquivalentClasses(OWLIndividualImpl owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator equivalentClassesIterator = definingClass.getEquivalentClasses().iterator();
      while (equivalentClassesIterator.hasNext()) {
        RDFSClass cls1 = (RDFSClass)equivalentClassesIterator.next();
        if (cls1 instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
          OWLClass equivalentClass = OWLConversionFactory.createOWLClass((edu.stanford.smi.protegex.owl.model.OWLNamedClass)cls1);
          if (!owlIndividualImpl.getDefiningEquivalentClasses().contains(equivalentClass)) {
            Iterator equivalentClassesSuperclassesIterator = cls1.getNamedSuperclasses(true).iterator();
            while (equivalentClassesSuperclassesIterator.hasNext()) {
              RDFSClass cls2 = (RDFSClass)equivalentClassesSuperclassesIterator.next();
              if (cls2 instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
                OWLClass equivalentClassSuperclass = OWLConversionFactory.createOWLClass((edu.stanford.smi.protegex.owl.model.OWLNamedClass)cls2);
                if (!owlIndividualImpl.getDefiningEquivalentClassSuperclasses().contains(equivalentClassSuperclass)) 
                  owlIndividualImpl.addDefiningEquivalentClassSuperclass(equivalentClassSuperclass);
              } // if
            } // if
            owlIndividualImpl.addDefiningEquivalentClass(equivalentClass);
          } // if
        } // if
      } // while
    } // while
  } // buildDefiningEquivalentClasses

  private static void buildSameAsIndividuals(OWLIndividualImpl owlIndividualImpl, edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.RDFProperty sameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(individual.getOWLModel());

    if (individual.hasPropertyValue(sameAsProperty)) {
      Collection individuals = (Collection)individual.getPropertyValues(sameAsProperty);
      Iterator individualsIterator = individuals.iterator();
      while (individualsIterator.hasNext()) {
        Object object = individualsIterator.next();
        if (!(object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue;
        edu.stanford.smi.protegex.owl.model.OWLIndividual sameAsIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
        owlIndividualImpl.addSameAsIndividual(OWLConversionFactory.createOWLIndividual(sameAsIndividual));
      } // while
    } // if
  } // buildSameAsIndividuals

} // OWLConversionFactory
