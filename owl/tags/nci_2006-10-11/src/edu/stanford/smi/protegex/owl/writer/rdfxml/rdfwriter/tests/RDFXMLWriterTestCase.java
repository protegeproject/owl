package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.tests;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

import java.util.ArrayList;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 8, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RDFXMLWriterTestCase extends AbstractRDFXMLWriterTestCases {

    private OWLNamedClass clsA;

    private OWLNamedClass clsB;

    private OWLNamedClass clsC;

    private OWLObjectProperty propP;

    private OWLDatatypeProperty propR;

    private OWLIndividual iA;

    private OWLIndividual iB;


    protected void setUp()
            throws Exception {
        super.setUp();
        clsA = owlModel.createOWLNamedClass("A");
        clsB = owlModel.createOWLNamedClass("B");
        clsC = owlModel.createOWLNamedClass("C");
        propP = owlModel.createOWLObjectProperty("p");
        propR = owlModel.createOWLDatatypeProperty("r");
        iA = owlModel.getOWLThingClass().createOWLIndividual("iA");
        iB = owlModel.getOWLThingClass().createOWLIndividual("iB");
    }


    public void testAllDifferent() {
        // All different
        OWLAllDifferent allDifferent = owlModel.createOWLAllDifferent();
        allDifferent.addDistinctMember(iA);
        allDifferent.addDistinctMember(iB);
        doCheck();
    }


    public void testSubClassOf() {
        clsA.addSuperclass(clsB);
        clsA.addSuperclass(clsC);
        doCheck();
    }


    public void testDisjointWith() {
        clsA.addDisjointClass(clsB);
        doCheck();
    }


    public void testObjectSomeValuesFrom() {
        clsA.addSuperclass(owlModel.createOWLSomeValuesFrom(propP, clsB));
        doCheck();
    }


    public void testDataSomeValuesFrom() {
        clsA.addSuperclass(owlModel.createOWLSomeValuesFrom(propR, owlModel.getXSDint()));
        doCheck();
    }


    public void testObjectAllValuesFrom() {
        clsB.addSuperclass(owlModel.createOWLAllValuesFrom(propP, clsB));
        doCheck();
    }


    public void testDataAllValuesFrom() {
        clsA.addSuperclass(owlModel.createOWLAllValuesFrom(propR, owlModel.getXSDint()));
        doCheck();
    }


    public void testObjectHasValue() {
        clsA.addSuperclass(owlModel.createOWLHasValue(propP, iA));
        doCheck();
    }


    public void testDataHasValue() {
        clsA.addSuperclass(owlModel.createOWLHasValue(propR, owlModel.createRDFSLiteral("26", owlModel.getXSDint())));
        doCheck();
    }


    public void testIntersectionOf() {
        OWLIntersectionClass intersectionClass = owlModel.createOWLIntersectionClass();
        intersectionClass.addOperand(clsB);
        intersectionClass.addOperand(clsC);
        clsA.addSuperclass(intersectionClass);
        doCheck();
    }


    public void testUnionOf() {
        OWLUnionClass unionClass = owlModel.createOWLUnionClass();
        unionClass.addOperand(clsB);
        unionClass.addOperand(clsC);
        clsA.addSuperclass(unionClass);
        doCheck();
    }


    public void testComplementOf() {
        OWLComplementClass complementClass = owlModel.createOWLComplementClass();
        complementClass.setComplement(clsB);
        clsA.addSuperclass(complementClass);
        doCheck();
    }


    public void testOneOf() {
        OWLEnumeratedClass enumeratedClass = owlModel.createOWLEnumeratedClass();
        enumeratedClass.addOneOf(iA);
        enumeratedClass.addOneOf(iB);
        clsA.addSuperclass(enumeratedClass);
        doCheck();
    }


    public void testMinCardinality() {
        clsA.addSuperclass(owlModel.createOWLMinCardinality(propP, 3));
        doCheck();
    }


    public void testCardinality() {
        clsA.addSuperclass(owlModel.createOWLCardinality(propP, 3));
        doCheck();
    }


    public void testMaxCardinality() {
        clsA.addSuperclass(owlModel.createOWLMaxCardinality(propP, 3));
        doCheck();
    }


    public void testMinQualifiedCardinality() {
        OWLMinCardinality cardinality = owlModel.createOWLMinCardinality(propP, 3);
        cardinality.setValuesFrom(clsA);
        clsA.addSuperclass(cardinality);
        doCheck();
    }


    public void testQualifiedCardinality() {
        OWLCardinality cardinality = owlModel.createOWLCardinality(propP, 3);
        cardinality.setValuesFrom(clsA);
        clsA.addSuperclass(cardinality);
        doCheck();
    }


    public void testMaxQualifiedCardinality() {
        OWLMaxCardinality cardinality = owlModel.createOWLMaxCardinality(propP, 3);
        cardinality.setValuesFrom(clsA);
        clsA.addSuperclass(cardinality);
        doCheck();
    }


    public void testPropertyCharacteristics() {
        propP.setFunctional(true);
        propP.setInverseFunctional(true);
        propP.setSymmetric(true);
        propP.setTransitive(true);
        doCheck();
    }


    public void testObjectPropertyRange() {
        propP.setRange(clsA);
        doCheck();
    }


    public void testObjectPropertyDomain() {
        propP.setDomain(clsA);
        doCheck();
    }


    public void testDatatypePropertyRange() {
        propR.setRange(owlModel.getXSDint());
        doCheck();
    }


    public void testDatatypePropertyDataRange() {
        RDFSLiteral [] literals = new RDFSLiteral[3];
        literals[0] = owlModel.createRDFSLiteral("26", owlModel.getXSDint());
        literals[1] = owlModel.createRDFSLiteral("33", owlModel.getXSDint());
        literals[2] = owlModel.createRDFSLiteral("44", owlModel.getXSDint());
        OWLDataRange dataRange = owlModel.createOWLDataRange(literals);
        propR.setRange(dataRange);
        doCheck();
    }

//	public void testInverseProperty() {
//		propP.setInverseProperty(owlModel.createOWLObjectProperty("propQ"));
//		doCheck();
//	}


    public void testRDFList() {
        ArrayList list = new ArrayList();
        list.add(iA);
        list.add(iB);
        list.add(clsA);
        RDFList rdfList = owlModel.createRDFList(list.iterator());
        iA.addPropertyValue(propP, rdfList);
        doCheck();
    }


    public void testSameAs() {
        iA.addSameAs(iB);
        doCheck();
    }


    public void testDifferentFrom() {
        iA.addDifferentFrom(iB);
        doCheck();
    }


    public void testAnnotationProperty() {
        OWLProperty prop = owlModel.createAnnotationOWLObjectProperty("anno");
        doCheck();
    }


    public void testBuiltInAnnotation() {
        OWLUtil.addComment(clsA, "A comment");
        doCheck();
    }


    public void testBuiltInAnonymousClassAnnotation() {
        OWLRestriction restriction = owlModel.createOWLSomeValuesFrom(propP, clsB);
        OWLUtil.addComment(restriction, "A comment");
        clsA.addSuperclass(restriction);
        doCheck();
    }


    public void testAnonymousIndividualChain() {
        RDFResource anonA = clsA.createOWLIndividual(owlModel.getNextAnonymousResourceName());
        RDFResource anonB = clsA.createOWLIndividual(owlModel.getNextAnonymousResourceName());
        RDFResource anonC = clsA.createOWLIndividual(owlModel.getNextAnonymousResourceName());
        iA.addPropertyValue(propP, anonA);
        anonA.addPropertyValue(propP, anonB);
        anonB.addPropertyValue(propP, anonC);
        doCheck();
    }


    public void testDatatypeRelationship() {
        iA.addPropertyValue(propR, owlModel.createRDFSLiteral("33", owlModel.getXSDint()));
        doCheck();
    }
}

