package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.TargetSWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLEntityReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSameIndividualAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSubClassAxiomReference;

/**
 * Default implementation of an OWL axiom convertor that takes OWL axioms and converts them to a native rule engine implementation. 
 * This implementation defines some of the standard tasks that will likely be common to many rule engines.   
 * 
 * A target rule engine implementation should implement the abstract methods. As can be seen, the bridge currently only supports a
 * small number of OWL axioms.  
 */
public abstract class DefaultOWLAxiomConvertor extends DefaultConvertor implements OWLAxiomConvertor 
{
  protected abstract void defineOWLClassDeclaration(String classURI) throws TargetSWRLRuleEngineException;
  protected abstract void defineOWLIndividualDeclaration(String individualURI) throws TargetSWRLRuleEngineException;
  protected abstract void defineOWLPropertyDeclaration(String propertyURI) throws TargetSWRLRuleEngineException;
  
  protected abstract void defineOWLClassAssertionAxiom(String classURI, String individualURI) throws TargetSWRLRuleEngineException;
  protected abstract void defineOWLObjectPropertyAssertionAxiom(String propertyURI, String subjectIndividualURI, String objectIndividualURI) 
  	throws TargetSWRLRuleEngineException;
  protected abstract void defineOWLDataPropertyAssertionAxiom(String propertyURI, String subjectIndividualURI, OWLLiteralReference objectLiteral) 
  	throws TargetSWRLRuleEngineException;

  protected abstract void defineOWLSameIndividualAxiom(String individual1URI, String individual2URI) throws TargetSWRLRuleEngineException;
  protected abstract void defineOWLDifferentIndividualsAxiom(String individual1URI, String individual2URI) throws TargetSWRLRuleEngineException;
  
  protected abstract void defineOWLClassPropertyAssertion(String propertyURI, String subjectIndividualURI, String objectValueClassURI) 
		throws TargetSWRLRuleEngineException;
  protected abstract void defineOWLPropertyPropertyAssertion(String propertyURI, String subjectIndividualURI, String objectValuePropertyURI) 
		throws TargetSWRLRuleEngineException;
  
  public DefaultOWLAxiomConvertor(SWRLRuleEngineBridge bridge) { super(bridge); }
	
  public void generateOWLAxiom(OWLAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
  	if (axiom instanceof OWLDataPropertyAssertionAxiomReference) {
  		OWLDataPropertyAssertionAxiomReference owlDatatypePropertyAssertionAxiom = (OWLDataPropertyAssertionAxiomReference)axiom;
  		generateOWLDataPropertyAssertionAxiom(owlDatatypePropertyAssertionAxiom);
	    } else if (axiom instanceof OWLObjectPropertyAssertionAxiomReference) {
	      OWLObjectPropertyAssertionAxiomReference owlObjectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiomReference)axiom;
	      generateOWLObjectPropertyAssertionAxiom(owlObjectPropertyAssertionAxiom);
	    } else if (axiom instanceof OWLClassPropertyAssertionAxiomReference) {
	      OWLClassPropertyAssertionAxiomReference owlClassPropertyAssertionAxiom = (OWLClassPropertyAssertionAxiomReference)axiom;
	      generateOWLClassPropertyAssertionAxiom(owlClassPropertyAssertionAxiom);
	    } else if (axiom instanceof OWLPropertyPropertyAssertionAxiomReference) {
	      OWLPropertyPropertyAssertionAxiomReference owlPropertyPropertyAssertionAxiom = (OWLPropertyPropertyAssertionAxiomReference)axiom;
	      generateOWLPropertyPropertyAssertionAxiom(owlPropertyPropertyAssertionAxiom);
	    } else if (axiom instanceof OWLSameIndividualAxiomReference) {
	      OWLSameIndividualAxiomReference owlSameIndividualAxiom = (OWLSameIndividualAxiomReference)axiom;
	      generateOWLSameIndividualAxiom(owlSameIndividualAxiom);
	    } else if (axiom instanceof OWLDifferentIndividualsAxiomReference) {
	      OWLDifferentIndividualsAxiomReference owlDifferentIndividualsAxiom = (OWLDifferentIndividualsAxiomReference)axiom;
	      generateOWLDifferentIndividualsAxiom(owlDifferentIndividualsAxiom);
	    } else if (axiom instanceof OWLClassAssertionAxiomReference) {
	      OWLClassAssertionAxiomReference owlClassAssertionAxiom = (OWLClassAssertionAxiomReference)axiom;
	      generateOWLClassAssertionAxiom(owlClassAssertionAxiom);
	    } else if (axiom instanceof OWLDeclarationAxiomReference) {
	      OWLDeclarationAxiomReference owlDeclarationAxiom = (OWLDeclarationAxiomReference)axiom;
	      generateOWLDeclarationAxiom(owlDeclarationAxiom);
	    } else if (axiom instanceof OWLSubClassAxiomReference) {
	      OWLSubClassAxiomReference owlSubClassAxiom = (OWLSubClassAxiomReference)axiom;
	      generateOWLSubclassAxiom(owlSubClassAxiom);
	    } else throw new TargetSWRLRuleEngineException("OWL axiom " + axiom.getClass() + " not supported");
  }
  	
  public void generateOWLClassDeclaration(OWLClassReference owlClass) throws TargetSWRLRuleEngineException
  {
    Set<String> classURIs = new HashSet<String>();

    classURIs.add(owlClass.getURI()); // For SWRL Full

    for (String classURI : classURIs) {
    	defineOWLClassDeclaration(classURI);
    } // for
  } 

  public void generateOWLPropertyDeclaration(OWLPropertyReference owlProperty) throws TargetSWRLRuleEngineException
  {
    Set<String> propertyURIs = new HashSet<String>();

    propertyURIs.add(owlProperty.getURI());  // For SWRL Full

    for (String propertyURI : propertyURIs) {
    	defineOWLPropertyDeclaration(propertyURI);
    } // for
  } 

  public void generateOWLIndividualDeclaration(OWLNamedIndividualReference individual) throws TargetSWRLRuleEngineException
  {
    String individualURI = individual.getURI();

    defineOWLIndividualDeclaration(individualURI);
  }
 
  // TODO: Here, we go beyond just declaring the supplied class assertion axiom but also declare the transitive closure of
  // the individual's classes and also the same and different individual axioms for that individual. This should not be 
  // happening here and should be taken care of by the generateOWLSubClassAssertion, generateOWLEquivalentClassAssertion,
  // generateOWLSameIndividualAxiom, and generateOWLDifferentIndividualsAxiom methods. However, the SWRLAndSQWRLProcessor
  // class does not currently generate these axioms.
  public void generateOWLClassAssertionAxiom(OWLClassAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    Set<OWLClassReference> definingClasses = new HashSet<OWLClassReference>();
    String individualURI = axiom.getIndividual().getURI();
    
    definingClasses.add(axiom.getDescription());

    calculateTransitiveSuperAndEquivalentClassClosure(axiom.getIndividual().getTypes(), definingClasses);
    
    for (OWLClassReference owlClass : definingClasses) {
      String classURI = owlClass.getURI();
    	defineOWLClassAssertionAxiom(classURI, individualURI);

      for (OWLNamedIndividualReference sameIndividual : axiom.getIndividual().getSameIndividuals()) {
      	String sameIndividualURI = sameIndividual.getURI();
      	defineOWLSameIndividualAxiom(individualURI, sameIndividualURI);
      	defineOWLClassAssertionAxiom(classURI, sameIndividualURI);
      } // for
      
      for (OWLNamedIndividualReference differentIndividual : axiom.getIndividual().getDifferentIndividuals()) {
      	String differentIndividualURI = differentIndividual.getURI();
      	defineOWLDifferentIndividualsAxiom(individualURI, differentIndividualURI);
        } // for
    } // for

    defineOWLSameIndividualAxiom(individualURI, individualURI);
  }

  // TODO: Here, we go beyond just declaring the supplied property assertion axiom but also declare the transitive closure of
  // the equivalent and super properties and consider the same as relationships for the subject individuals.
  // This should not be happening here and should be taken care of by the generateOWLSubPropertyAssertion 
  // and generateOWLEquivalentPropertyAssertion methods. However, the SWRLAndSQWRLProcessor class does not currently generate these axioms.
  // Currently, the OWLOntology.getPropertyAssertionAxioms(propertyURI) call already considers equivalent and sub-property relationships. 
  public void generateOWLDataPropertyAssertionAxiom(OWLDataPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    OWLPropertyReference property = axiom.getProperty();
    Set<OWLPropertyReference> definingProperties = new HashSet<OWLPropertyReference>();
    OWLNamedIndividualReference subjectIndividual = axiom.getSubject();
    Set<OWLNamedIndividualReference> subjectIndividuals = new HashSet<OWLNamedIndividualReference>();
    OWLLiteralReference objectLiteral = axiom.getObject();

    definingProperties.add(property);
    calculateTransitiveSuperAndEquivalentPropertyClosure(property.getSuperProperties(), definingProperties);

    subjectIndividuals.add(subjectIndividual);
    subjectIndividuals.addAll(subjectIndividual.getSameIndividuals());

    for (OWLPropertyReference definingProperty : definingProperties) {
      String propertyURI = definingProperty.getURI();
      for (OWLNamedIndividualReference owlIndividual : subjectIndividuals) {
	    	String subjectIndividualURI = owlIndividual.getURI();
	    	defineOWLDataPropertyAssertionAxiom(propertyURI, subjectIndividualURI, objectLiteral);
      } // for     
    } /// for      
  } 

  // TODO: Here, we go beyond just declaring the supplied property assertion axiom but also declare the transitive closure of
  // the equivalent and super properties and consider the same as relationships for both subject and object individuals. 
  // This should not be happening here and should be taken care of by the generateOWLSubPropertyAssertion 
  // and generateOWLEquivalentPropertyAssertion methods. However, the SWRLAndSQWRLProcessor class does not currently generate these axioms.
  // Currently, the OWLOntology.getPropertyAssertionAxioms(propertyURI) call already considers equivalent and sub-property relationships.
  public void generateOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    OWLPropertyReference property = axiom.getProperty();
    Set<OWLPropertyReference> definingProperties = new HashSet<OWLPropertyReference>();
    OWLNamedIndividualReference subjectIndividual = axiom.getSubject();
    OWLNamedIndividualReference objectIndividual = axiom.getObject();
    Set<OWLNamedIndividualReference> subjectIndividuals = new HashSet<OWLNamedIndividualReference>();
    Set<OWLNamedIndividualReference> objectIndividuals = new HashSet<OWLNamedIndividualReference>();

    definingProperties.add(property);
    calculateTransitiveSuperAndEquivalentPropertyClosure(property.getTypes(), definingProperties);

    subjectIndividuals.add(subjectIndividual);
    subjectIndividuals.addAll(subjectIndividual.getSameIndividuals());

    objectIndividuals.add(objectIndividual);
    objectIndividuals.addAll(objectIndividual.getSameIndividuals());

    for (OWLPropertyReference definingProperty : definingProperties) {
      String propertyURI = definingProperty.getURI();
      for (OWLNamedIndividualReference owlIndividual1 : subjectIndividuals) {
    	String subjectIndividualURI = owlIndividual1.getURI();
        for (OWLNamedIndividualReference owlIndividual2 : objectIndividuals) {
          String objectIndividualURI = owlIndividual2.getURI();
          defineOWLObjectPropertyAssertionAxiom(propertyURI, subjectIndividualURI, objectIndividualURI);
        } // for
      } // for
    } // for
  }

  public void generateOWLClassPropertyAssertionAxiom(OWLClassPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    OWLPropertyReference property = axiom.getProperty();
    Set<OWLPropertyReference> definingProperties = new HashSet<OWLPropertyReference>();
    OWLNamedIndividualReference subjectIndividual = axiom.getSubject();
    OWLClassReference objectClass = axiom.getObject();
    Set<OWLNamedIndividualReference> subjectIndividuals = new HashSet<OWLNamedIndividualReference>();
    Set<String> objectClassURIs = new HashSet<String>();

    definingProperties.add(property);
    calculateTransitiveSuperAndEquivalentPropertyClosure(property.getTypes(), definingProperties);

    subjectIndividuals.add(subjectIndividual);
    subjectIndividuals.addAll(subjectIndividual.getSameIndividuals());

    objectClassURIs.add(objectClass.getURI());
    
    for (OWLPropertyReference definingProperty : definingProperties) {
      String propertyURI =definingProperty.getURI();
      for (OWLNamedIndividualReference owlIndividual : subjectIndividuals) {
	    	String subjectIndividualURI = owlIndividual.getURI();
        for (String objectClassURI : objectClassURIs) {
  	    	defineOWLClassPropertyAssertion(propertyURI, subjectIndividualURI, objectClassURI);
        } // for
      } // for
    } // for
  } 

  public void generateOWLPropertyPropertyAssertionAxiom(OWLPropertyPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    OWLPropertyReference property = axiom.getProperty();
    Set<OWLPropertyReference> definingProperties = new HashSet<OWLPropertyReference>();
    OWLNamedIndividualReference subjectIndividual = axiom.getSubject();
    OWLPropertyReference objectProperty = axiom.getObject();
    Set<OWLNamedIndividualReference> subjectIndividuals = new HashSet<OWLNamedIndividualReference>();
    Set<String> objectPropertyURIs = new HashSet<String>();

    definingProperties.add(property);
    calculateTransitiveSuperAndEquivalentPropertyClosure(property.getTypes(), definingProperties);

    subjectIndividuals.add(subjectIndividual);
    subjectIndividuals.addAll(subjectIndividual.getSameIndividuals());

    objectPropertyURIs.add(objectProperty.getURI());

    for (OWLPropertyReference definingProperty : definingProperties) {
      String propertyURI = definingProperty.getURI();
      for (OWLNamedIndividualReference owlIndividual : subjectIndividuals) {
    	String subjectIndividualURI = owlIndividual.getURI();
        for (String objectPropertyURI : objectPropertyURIs) {
        	defineOWLPropertyPropertyAssertion(propertyURI, subjectIndividualURI, objectPropertyURI);
        } // for
      } // for
    } // for
  }

  public void generateOWLSameIndividualAxiom(OWLSameIndividualAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    String individualURI1 = axiom.getIndividual1().getURI();
    String individualURI2 = axiom.getIndividual2().getURI();
    
    defineOWLSameIndividualAxiom(individualURI1, individualURI2);
    defineOWLSameIndividualAxiom(individualURI2, individualURI1);
    defineOWLSameIndividualAxiom(individualURI1, individualURI1);
  } 

  public void generateOWLDifferentIndividualsAxiom(OWLDifferentIndividualsAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    List<String> allDifferentsSoFar = new ArrayList<String>();
    
    for (OWLNamedIndividualReference individual1 : axiom.getIndividuals()) {
      String individualURI1 = individual1.getURI();
      for (String individualURI2 : allDifferentsSoFar) {
      	defineOWLDifferentIndividualsAxiom(individualURI1, individualURI2);
      	defineOWLDifferentIndividualsAxiom(individualURI2, individualURI1);
      } // for
      allDifferentsSoFar.add(individualURI1);
    } // for
  } 

  public void generateOWLDeclarationAxiom(OWLDeclarationAxiomReference axiom) throws TargetSWRLRuleEngineException
	{
	  OWLEntityReference owlEntity = axiom.getEntity();
	
	  if (owlEntity instanceof OWLClassReference) generateOWLClassDeclaration((OWLClassReference)owlEntity);
	  else if (owlEntity instanceof OWLPropertyReference) generateOWLPropertyDeclaration((OWLPropertyReference)owlEntity);
	  else if (owlEntity instanceof OWLNamedIndividualReference) generateOWLIndividualDeclaration((OWLNamedIndividualReference)owlEntity);
	  else throw new TargetSWRLRuleEngineException("OWL declaration axiom with entity " + owlEntity.getClass() + " not supported");
	}

  public void generateOWLSubclassAxiom(OWLSubClassAxiomReference axiom) throws TargetSWRLRuleEngineException
  {
    OWLClassReference owlSubClass = axiom.getSubClass();
    OWLClassReference owlSuperClass = axiom.getSuperClass();

    generateOWLClassDeclaration(owlSubClass);
    generateOWLClassDeclaration(owlSuperClass);
    
    // TODO: we do nothing for the moment because generateOWLClassAssertionAxiomRepresentation effectively takes care of this axiom.
  }
  
  private void calculateTransitiveSuperAndEquivalentClassClosure(Set<OWLClassReference> classes, Set<OWLClassReference> closure)
  {
		for (OWLClassReference cls : classes) {
			if (!closure.contains(cls)) {
				closure.add(cls);
				calculateTransitiveSuperAndEquivalentClassClosure(cls.getSuperClasses(), closure);
				calculateTransitiveSuperAndEquivalentClassClosure(cls.getEquivalentClasses(), closure);
			} // if
		} // for
  } 

  private void calculateTransitiveSuperAndEquivalentPropertyClosure(Set<OWLPropertyReference> properties, Set<OWLPropertyReference> closure)
  {
		for (OWLPropertyReference property : properties) {
			if (!closure.contains(property)) {
				closure.add(property);
				calculateTransitiveSuperAndEquivalentPropertyClosure(property.getSuperProperties(), closure);
				calculateTransitiveSuperAndEquivalentPropertyClosure(property.getEquivalentProperties(), closure);
			} // if
		} // for
  } 
} 
