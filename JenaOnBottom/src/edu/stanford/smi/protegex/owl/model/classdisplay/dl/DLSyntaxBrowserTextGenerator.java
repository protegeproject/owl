package edu.stanford.smi.protegex.owl.model.classdisplay.dl;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.model.visitor.Visitable;

import java.util.Iterator;
import java.util.Stack;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 25, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLSyntaxBrowserTextGenerator implements OWLModelVisitor {

    private StringBuffer buffer;

    private Stack<Visitable> resourceStack;

    public DLSyntaxBrowserTextGenerator() {
        buffer = new StringBuffer();
        resourceStack = new Stack();
    }

    public String getBrowserText() {
        return buffer.toString();
    }

    public void reset() {
        buffer = new StringBuffer();
    }

    protected StringBuffer getCurrentBuffer() {
        return buffer;
    }

    protected void write(String s) {
        getCurrentBuffer().append(s);
    }

    protected void write(char c) {
        getCurrentBuffer().append(c);
    }

    protected void writeSpace() {
        getCurrentBuffer().append(" ");
    }

    protected void writeOpenPar()  {
        if (resourceStack.size() > 1) {
            getCurrentBuffer().append("(");
        }
    }

    protected void writeClosePar() {
        if (resourceStack.size() > 1) {
            getCurrentBuffer().append(")");
        }
    }

    public void visitOWLAllDifferent(OWLAllDifferent owlAllDifferent) {

    }

    private void push(RDFSClass v) {
        resourceStack.push(v);
    }

    private void pop() {
        resourceStack.pop();
    }


    protected void writeCardinality(OWLCardinalityBase cardinalityBase) {
        writeOpenPar();
        write(cardinalityBase.getOperator());
        writeSpace();
        write(String.valueOf(cardinalityBase.getCardinality()));
        writeSpace();
        if (cardinalityBase.getOnProperty() != null) {
            cardinalityBase.getOnProperty().accept(this);
        }
        if(cardinalityBase.getQualifier() != null) {
            writeSpace();
            cardinalityBase.getQualifier().accept(this);
        }
        writeClosePar();
    }

    protected void writeQuantifier(OWLQuantifierRestriction quantifierRestriction) {
        writeOpenPar();
        write(quantifierRestriction.getOperator());
        writeSpace();
        if(quantifierRestriction.getOnProperty() != null) {
            quantifierRestriction.getOnProperty().accept(this);
        }
        writeSpace();
        if (quantifierRestriction.getFiller() != null) {
            quantifierRestriction.getFiller().accept(this);
        }
        writeClosePar();
    }

    protected void writeNAryLocical(OWLNAryLogicalClass logicalClass) {
        writeOpenPar();
        for(Iterator it = logicalClass.getOperands().iterator(); it.hasNext(); ) {
            RDFResource res = (RDFResource) it.next();
            res.accept(this);
            if(it.hasNext()) {
                writeSpace();
                write(logicalClass.getOperatorSymbol());
                writeSpace();
            }
        }
        writeClosePar();
    }

    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        push(owlAllValuesFrom);
        writeQuantifier(owlAllValuesFrom);
        pop();
    }

    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        push(owlCardinality);
        writeCardinality(owlCardinality);
        pop();
    }

    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        push(owlComplementClass);
        writeOpenPar();
        write(owlComplementClass.getOperatorSymbol());
        writeSpace();
        if (owlComplementClass.getComplement() != null) {
            owlComplementClass.getComplement().accept(this);
        }
        writeClosePar();
        pop();
    }

    public void visitOWLDataRange(OWLDataRange owlDataRange) {
        write("{");
        for(Iterator it = owlDataRange.getOneOfValueLiterals().iterator(); it.hasNext(); ) {
            RDFSLiteral literal = (RDFSLiteral) it.next();
            literal.accept(this);
            if(it.hasNext()) {
                writeSpace();
            }
        }
        write("}");
    }

    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        write(owlDatatypeProperty.getName());
    }

    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        push(owlEnumeratedClass);
        write("{");
        for(Iterator it = owlEnumeratedClass.getOneOf().iterator(); it.hasNext(); ) {
            RDFResource res = (RDFResource) it.next();
            res.accept(this);
            if(it.hasNext()) {
                writeSpace();
            }
        }
        write("}");
        pop();
    }

    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        // Has value should be written as someValuesFrom!
        push(owlHasValue);
        writeOpenPar();
        write(DefaultOWLSomeValuesFrom.OPERATOR);
        writeSpace();
        if (owlHasValue.getOnProperty() != null) {
            owlHasValue.getOnProperty().accept(this);
        }
        writeSpace();
        write("{");
        if (owlHasValue.getHasValue() != null) {
            Object value = owlHasValue.getHasValue();
            if(value instanceof RDFResource) {
                ((RDFResource) value).accept(this);
            }
            else {
                owlHasValue.getOWLModel().asRDFSLiteral(value).accept(this);
            }
        }
        write("}");
        writeClosePar();
        pop();
    }

    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        write(owlIndividual.getName());
    }

    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        push(owlIntersectionClass);
        writeNAryLocical(owlIntersectionClass);
        pop();
    }

    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        push(owlMaxCardinality);
        writeCardinality(owlMaxCardinality);
        pop();
    }

    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        push(owlMinCardinality);
        writeCardinality(owlMinCardinality);
        pop();
    }

    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        push(owlNamedClass);
        write(owlNamedClass.getName());
        pop();
    }

    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        write(owlObjectProperty.getName());
    }

    public void visitOWLOntology(OWLOntology owlOntology) {
        write("<OWLOntology>");
    }

    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom someValuesFrom) {
        push(someValuesFrom);
        writeQuantifier(someValuesFrom);
        pop();
    }

    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        push(owlUnionClass);
        writeNAryLocical(owlUnionClass);
        pop();
    }

    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
        write(rdfsDatatype.getBrowserText());
    }

    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        write(rdfIndividual.getName());
    }

    public void visitRDFList(RDFList rdfList) {
        write("<RDFList>");
    }

    public void visitRDFProperty(RDFProperty rdfProperty) {
        write(rdfProperty.getName());
    }

    public void visitRDFSLiteral(RDFSLiteral rdfsLiteral) {
        write(rdfsLiteral.getBrowserText());
    }

    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        push(rdfsNamedClass);
        pop();
    }

    public void visitRDFUntypedResource(RDFUntypedResource rdfUntypedResource) {
        write(rdfUntypedResource.getURI());
    }

}
