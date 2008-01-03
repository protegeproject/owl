package edu.stanford.smi.protegex.owl.model.classdisplay.dl;

import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 25, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLSymbolGenerator implements OWLModelVisitor {

    private String symbol;

    public DLSymbolGenerator() {
        reset();
    }

    public void reset() {
        symbol = "<NONE>";
    }

    public String getSymbol() {
        return symbol;
    }

    public void visitOWLAllDifferent(OWLAllDifferent owlAllDifferent) {
        // NONE
    }

    private void setSymbol(char c) {
        symbol = String.valueOf(c);
    }

    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        setSymbol(owlAllValuesFrom.getOperator());
    }

    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        setSymbol(owlCardinality.getOperator());
    }

    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        setSymbol(owlComplementClass.getOperatorSymbol());
    }

    public void visitOWLDataRange(OWLDataRange owlDataRange) {
        // NONE
    }

    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        // NONE
    }

    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        // NONE
    }

    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        setSymbol(DefaultOWLSomeValuesFrom.OPERATOR);
    }

    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        // NONE
    }

    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        setSymbol(owlIntersectionClass.getOperatorSymbol());
    }

    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        setSymbol(owlMaxCardinality.getOperator());
    }

    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        setSymbol(owlMinCardinality.getOperator());
    }

    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        // NONE
    }

    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        // NONE
    }

    public void visitOWLOntology(OWLOntology owlOntology) {
        // NONE
    }

    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom someValuesFrom) {
        setSymbol(someValuesFrom.getOperator());
    }

    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        setSymbol(owlUnionClass.getOperatorSymbol());
    }

    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
        // NONE
    }

    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        // NONE
    }

    public void visitRDFList(RDFList rdfList) {
        // NONE
    }

    public void visitRDFProperty(RDFProperty rdfProperty) {
        // NONE
    }

    public void visitRDFSLiteral(RDFSLiteral rdfsLiteral) {
        // NONE
    }

    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        // NONE
    }

    public void visitRDFUntypedResource(RDFUntypedResource rdfUntypedResource) {
        // NONE
    }

	public void visitSWRLIndividual(SWRLIndividual swrlIndividual) {		
		visitOWLIndividual(swrlIndividual);
	}

	public void visitSWRLAtomListIndividual(SWRLAtomList swrlAtomList) {
		visitRDFList(swrlAtomList);
		
	}

}
