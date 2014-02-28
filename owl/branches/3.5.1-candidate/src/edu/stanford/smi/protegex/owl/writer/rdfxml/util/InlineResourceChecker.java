package edu.stanford.smi.protegex.owl.writer.rdfxml.util;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: March 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A class that can be used to check if a certain
 * type of resource can be inlined. Typically named
 * resources such as named classes and individuals
 * can be inlined as tag attributes in RDF/XML.  This class
 * follows the visitor pattern - users should create an instance
 * and then call the accept method on an instance of Visitable, passing
 * the instance of this class as an argument.  Having done this
 * the <code>isCanInline</code> method should be used to check
 * if the resource concerned can be inlined.
 */
public class InlineResourceChecker extends OWLModelVisitorAdapter {

    private boolean canInline;


    public InlineResourceChecker() {
        canInline = false;
    }


    /**
     * This method should be used to check if the most
     * recent visted resource can be inlined.
     *
     * @return <code>true</code> if the most recent visited
     *         resource can be inlined as an attribute, or <code>false</code> if the
     *         resource needs to be inserted as a child element.
     */
    public boolean isCanInline() {
        return canInline;
    }


    @Override
    public void visitOWLOntology(OWLOntology owlOntology) {
        canInline = true;
    }


    @Override
    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        canInline = true;
    }


    @Override
    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        if (owlIndividual.getOWLModel().isAnonymousResourceName(owlIndividual.getName()) == false) {
            canInline = true;
        }
    }


	@Override
    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
		canInline = rdfsDatatype.isAnonymous() == false;
	}


    @Override
    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        canInline = true;
    }


    @Override
    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        canInline = true;
    }


    public void visitRDFExternalResource(RDFExternalResource rdfExternalResource) {
        canInline = true;
    }


    @Override
    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        if (rdfIndividual.getOWLModel().isAnonymousResourceName(rdfIndividual.getName()) == false) {
            canInline = true;
        }
    }


    @Override
    public void visitRDFProperty(RDFProperty rdfProperty) {
        canInline = true;
    }


    @Override
    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        canInline = true;
    }

    @Override
    public void visitRDFUntypedResource(RDFUntypedResource rdfUntypedResource) {
        if (rdfUntypedResource.getOWLModel().isAnonymousResourceName(rdfUntypedResource.getName()) == false) {
            canInline = true;
        }
    }
}

