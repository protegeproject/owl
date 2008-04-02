package edu.stanford.smi.protegex.owl.writer.rdfxml.renderer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.Util;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: March 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An RDF/XML renderer that renders axioms for classes,
 * properties and individuals.  For example, axioms for
 * classes include subclass axioms, equivalent class axioms,
 * disjoint axioms etc.
 */
public class RDFAxiomRenderer extends OWLModelVisitorAdapter {

    private XMLWriter writer;

    private RDFResource resource;

    private TripleStore tripleStore;


    public RDFAxiomRenderer(RDFResource resource,
                            TripleStore tripleStore,
                            XMLWriter writer) {
        this.writer = writer;
        this.tripleStore = tripleStore;
        this.resource = resource;
    }


    protected RDFAxiomRenderer() {

    }


    public static RenderableAxiomsChecker getChecker() {
        return new RDFAxiomRenderer().new RenderableAxiomsChecker();
    }


    public void write() {
        resource.accept(this);
    }


    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        renderClassAxioms(owlAllValuesFrom);
    }


    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        renderClassAxioms(owlCardinality);
    }


    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        renderClassAxioms(owlComplementClass);
    }


    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        renderClassAxioms(owlEnumeratedClass);
    }


    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        renderClassAxioms(owlHasValue);
    }


    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        renderClassAxioms(owlIntersectionClass);
    }


    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        renderClassAxioms(owlMaxCardinality);
    }


    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        renderClassAxioms(owlMinCardinality);
    }


    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
        renderClassAxioms(owlSomeValuesFrom);
    }


    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        renderClassAxioms(owlUnionClass);
    }


    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        renderPropertyAxioms(owlDatatypeProperty);
    }


    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        renderIndividualAxioms(owlIndividual);
    }


    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        renderClassAxioms(owlNamedClass);
    }


    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        renderPropertyAxioms(owlObjectProperty);
    }


    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        renderIndividualAxioms(rdfIndividual);
    }


    public void visitRDFProperty(RDFProperty rdfProperty) {
        renderPropertyAxioms(rdfProperty);
    }


    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        renderClassAxioms(rdfsNamedClass);
    }


    private void renderClassAxioms(RDFSClass cls) {
        try {
            // Attempt to abreviate the RDF/XML slightly by
            // rendering the first type triple in the specified
            // triplestore as the name of the element.
            RDFResource type = Util.getType(cls, tripleStore);
            if (type != null) {
                Util.insertResourceAsElement(type, writer);
            }
            else {
                writer.writeStartElement(RDFNames.Cls.DESCRIPTION);
            }
            Util.insertIDOrAboutAttribute(cls, tripleStore, writer);

            // Render types
            renderTypes(cls, type);

            HashSet renderedEquivalentClasses = new HashSet();
            HashSet renderedNamedClassOperands = new HashSet();

            // Render equivalent classes
            RDFProperty equivClassProp = cls.getOWLModel().getRDFProperty(OWLNames.Slot.EQUIVALENT_CLASS);
            for (Iterator it = tripleStore.listObjects(cls, equivClassProp); it.hasNext();) {
                RDFResource curSuper = (RDFResource) it.next();
                if (curSuper instanceof OWLNamedClass) {
                    renderedNamedClassOperands.add(curSuper);
                }
                else if (curSuper instanceof OWLIntersectionClass) {
                    for (Iterator opIt = ((OWLIntersectionClass) curSuper).getOperands().iterator(); opIt.hasNext();) {
                        Object curOp = opIt.next();
                        if (curOp instanceof OWLNamedClass) {
                            renderedNamedClassOperands.add(curOp);
                        }
                    }
                }
                // Equivalent classes
                renderedEquivalentClasses.add(curSuper);
                writer.writeStartElement(OWLNames.Slot.EQUIVALENT_CLASS);
                Util.inlineObject(curSuper, tripleStore, writer);
                writer.writeEndElement(); // end of owl:equivalentClass
            }

            // Render the subclass triples
            int processedSupers = 0;
            RDFProperty subClassOfProp = cls.getOWLModel().getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
            for (Iterator it = tripleStore.listObjects(cls, subClassOfProp); it.hasNext();) {
                RDFSClass curSupCls = (RDFSClass) it.next();
                // Check that the current super hasn't already been rendered as
                // an equivalent class - if not, render it.
                if (renderedEquivalentClasses.contains(curSupCls) == false &&
                        renderedNamedClassOperands.contains(curSupCls) == false) {
                    // If the class only has one subClassOf triple that has an
                    // object of owl:Thing then don't render this triple - this
                    // is consistent with the Protege2Jena converter.
                    boolean isOWLThing = curSupCls.equals(curSupCls.getOWLModel().getOWLThingClass());
                    if (isOWLThing == false ||
                            isOWLThing && it.hasNext() ||
                            isOWLThing && processedSupers > 0) {
                        writer.writeStartElement(RDFSNames.Slot.SUB_CLASS_OF);
                        Util.inlineObject(curSupCls, tripleStore, writer);
                        writer.writeEndElement(); // end of rdfs:ubClassOf
                    }
                }
                processedSupers++;
            }

            // Annotations, sameAs, differentFrom etc. and other misc properties
            Util.insertProperties(cls, tripleStore, writer);
            writer.writeEndElement(); // end of getRDFType element
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderPropertyAxioms(RDFProperty property) {
        try {
            RDFResource type = Util.getType(property, tripleStore);
            if (type != null) {
                Util.insertResourceAsElement(type, writer);
            }
            else {
                writer.writeStartElement(RDFNames.Cls.DESCRIPTION);
            }

            Util.insertIDOrAboutAttribute(property, tripleStore, writer);
            renderTypes(property, type);
            // Super properties rendered as property values
            // Domain - special handling to filter out owl:Thing
            RDFSClass domain = property.getDomain(false);
            if (domain != null && domain.equals(property.getOWLModel().getOWLThingClass()) == false) {
                writer.writeStartElement(RDFSNames.Slot.DOMAIN);
                Util.inlineObject(domain, tripleStore, writer);
                writer.writeEndElement();
            }

            // Range
            // Rendered with property values
            Util.insertProperties(property, tripleStore, writer);
            writer.writeEndElement(); // End of property
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderIndividualAxioms(RDFIndividual individual) {
        try {
            RDFResource type = Util.getType(individual, tripleStore);
            if (type != null) {
                Util.insertResourceAsElement(type, writer);
            }
            else {
                writer.writeStartElement(RDFNames.Cls.DESCRIPTION);
            }
            Util.insertIDOrAboutAttribute(individual, tripleStore, writer);
            renderTypes(individual, type);
            Util.insertProperties(individual, tripleStore, writer);
            writer.writeEndElement(); // end of owl:Class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderTypes(RDFResource resource,
                             RDFResource excludeType)
            throws IOException {
        Util.renderTypes(resource, tripleStore, excludeType, writer);
    }


    public class RenderableAxiomsChecker extends OWLModelVisitorAdapter {

        private boolean renderable;


        public RenderableAxiomsChecker() {

        }


        public boolean isRenderable(RDFResource resource) {
            renderable = false;
            resource.accept(this);
            return renderable;
        }


        public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
            renderable = true;
        }


        public void visitOWLCardinality(OWLCardinality owlCardinality) {
            renderable = true;
        }


        public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
            renderable = true;
        }


        public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
            renderable = true;
        }


        public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
            renderable = true;
        }


        public void visitOWLHasValue(OWLHasValue owlHasValue) {
            renderable = true;
        }


        public void visitOWLIndividual(OWLIndividual owlIndividual) {
            renderable = true;
        }


        public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
            renderable = true;
        }


        public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
            renderable = true;
        }


        public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
            renderable = true;
        }


        public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
            renderable = true;
        }


        public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
            renderable = true;
        }


        public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
            renderable = true;
        }


        public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
            renderable = true;
        }


        public void visitRDFIndividual(RDFIndividual rdfIndividual) {
            renderable = true;
        }


        public void visitRDFProperty(RDFProperty rdfProperty) {
            renderable = true;
        }


        public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
            renderable = true;
        }
    }

}

