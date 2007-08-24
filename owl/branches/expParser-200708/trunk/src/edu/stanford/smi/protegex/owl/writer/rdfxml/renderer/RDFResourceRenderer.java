package edu.stanford.smi.protegex.owl.writer.rdfxml.renderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;

import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLCardinalityBase;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;
import edu.stanford.smi.protegex.owl.model.visitor.Visitable;
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
 * A renderer that translates various OWL and RDF classes and resource
 * to RDF/XML.  Note that Anonymous resource have their properties
 * written out as well.
 */
public class RDFResourceRenderer extends OWLModelVisitorAdapter {

    private Visitable resource;

    private XMLWriter writer;

    private TripleStore tripleStore;


    public RDFResourceRenderer(Visitable rdfResource, TripleStore tripleStore, XMLWriter writer) {
        this.resource = rdfResource;
        this.tripleStore = tripleStore;
        this.writer = writer;
    }


    public void write() {
        resource.accept(this);
    }


    /**
     * Renders an allValuesFrom, or universal restriction and its various
     * properties such as owl:onProperty, owl:allValuesFrom
     */
    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        renderQuantifierRestriction(owlAllValuesFrom, OWLNames.Slot.ALL_VALUES_FROM);
    }


    /**
     * Renders and instance of owl:AllDifferent, as an rdf collection
     * (rdf:parseType="Collection") of the distinct members.
     */
    public void visitOWLAllDifferent(OWLAllDifferent owlAllDifferent) {
        try {
            writer.writeStartElement(OWLNames.Cls.ALL_DIFFERENT);
            writer.writeStartElement(OWLNames.Slot.DISTINCT_MEMBERS);
            writer.writeAttribute(RDFNames.Slot.PARSE_TYPE, RDFNames.COLLECTION);
            for (Iterator it = owlAllDifferent.getDistinctMembers().iterator(); it.hasNext();) {
                RDFResource curRes = (RDFResource) it.next();
                curRes.accept(this);
            }
            writer.writeEndElement(); // end of owl:distinctMembers
            insertProperties(owlAllDifferent);
            writer.writeEndElement(); // end of owl:AllDifferent
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitOWLOntology(OWLOntology owlOntology) {
        try {
            writer.writeStartElement(OWLNames.Cls.ONTOLOGY);
            writer.writeAttribute(RDFNames.Slot.ABOUT, Util.getResourceAttributeName(owlOntology, writer));
            insertProperties(owlOntology);
            // Imports
            for (Iterator it = owlOntology.getImports().iterator(); it.hasNext();) {
                String curImp = (String) it.next();
                writer.writeStartElement(OWLNames.Slot.IMPORTS);
                writer.writeAttribute(RDFNames.Slot.RESOURCE, curImp);
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    /**
     * Renders an owl:Cardinality restriction using its associated
     * properties such as owl:onProperty.
     */
    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        renderCardinalityRestriction(owlCardinality, OWLNames.Slot.CARDINALITY);
    }


    /**
     * Renders an owl:ComplementClass.  If the complemented class is a
     * named class then it is inserted as an attribute.
     */
    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        try {
            writer.writeStartElement(OWLNames.Cls.NAMED_CLASS);
            writer.writeStartElement(OWLNames.Slot.COMPLEMENT_OF);
            RDFSClass complement = owlComplementClass.getComplement();
            Util.inlineObject(complement, tripleStore, writer);
            writer.writeEndElement(); // end of owl:complementOf
            insertProperties(owlComplementClass);
            writer.writeEndElement(); // end of owl:Class

        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    /**
     * Renders and owl:oneOf (enumerated) class.  The class is
     * rendered using an rdf collection.
     */
    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        try {
            writer.writeStartElement(OWLNames.Cls.NAMED_CLASS);
            writer.writeStartElement(OWLNames.Slot.ONE_OF);
            writer.writeAttribute(RDFNames.Slot.PARSE_TYPE, RDFNames.COLLECTION);
            TreeSet values = new TreeSet(new FrameComparator());
            values.addAll(owlEnumeratedClass.getOneOf());
            for (Iterator it = values.iterator(); it.hasNext();) {
                RDFResource resource = (RDFResource) it.next();
                new RDFResourceRenderer(resource, tripleStore, writer).write();
            }
            writer.writeEndElement(); // end of owl:oneOf
            insertProperties(owlEnumeratedClass);
            writer.writeEndElement(); // end of owl:Class

        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        try {
            // Will have restriction element to close!!
            writeRestrictionStart(owlHasValue);
            writer.writeStartElement(OWLNames.Slot.HAS_VALUE);
            Object value = owlHasValue.getHasValue();
            if (value instanceof RDFResource) {
                Util.inlineObject((RDFResource) value, tripleStore, writer);
            }
            else {
                RDFSLiteral litVal = owlHasValue.getOWLModel().asRDFSLiteral(value);
                if (litVal.getLanguage() == null) {
                    writer.writeAttribute(RDFNames.Slot.DATATYPE, litVal.getDatatype().getURI());
                }
                else {
                    writer.writeAttribute(Vocab.XML_LANG, litVal.getLanguage());
                }
                writer.writeTextContent(litVal.toString());

            }
            writer.writeEndElement(); // end of owl:hasValue
            insertProperties(owlHasValue);
            writer.writeEndElement(); // end of owl:Restriction
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        try {
            if (owlIndividual.isAnonymous() == false) {
                //Util.insertResourceAsElement(owlIndividual.getRDFType(), rdfwriter);
                writer.writeStartElement(RDFNames.Cls.DESCRIPTION);
                Util.insertAboutAttribute(owlIndividual, writer);
            }
            else {
                writer.writeStartElement(RDFNames.Cls.DESCRIPTION);
                Util.renderTypes(owlIndividual, tripleStore, owlIndividual.getOWLModel().getOWLThingClass(), writer);
                insertProperties(owlIndividual);
            }
            writer.writeEndElement(); // end of individual


        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        renderNAryLogicalClass(owlIntersectionClass, OWLNames.Slot.INTERSECTION_OF);
    }


    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        renderCardinalityRestriction(owlMaxCardinality, OWLNames.Slot.MAX_CARDINALITY);
    }


    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        renderCardinalityRestriction(owlMinCardinality, OWLNames.Slot.MIN_CARDINALITY);
    }


    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        try {
            writer.writeStartElement(OWLNames.Cls.NAMED_CLASS);
            Util.insertAboutAttribute(owlNamedClass, writer);
            writer.writeEndElement(); // End of named class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
        renderQuantifierRestriction(owlSomeValuesFrom, OWLNames.Slot.SOME_VALUES_FROM);
    }


    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        renderNAryLogicalClass(owlUnionClass, OWLNames.Slot.UNION_OF);
    }


    public void visitOWLDataRange(OWLDataRange owlDataRange) {
        try {

            writer.writeStartElement(OWLNames.Cls.DATA_RANGE);
            writer.writeStartElement(OWLNames.Slot.ONE_OF);
            // Gets rendered as a list
            renderValuesAsRDFList(owlDataRange.getOneOfValueLiterals(), owlDataRange.getOWLModel());
            writer.writeEndElement(); // end of owl:oneOf
            insertProperties(owlDataRange);
            writer.writeEndElement(); // end of owl:DataRange
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitRDFExternalResource(RDFExternalResource rdfExternalResource) {
        try {
            writer.writeStartElement(RDFNames.Slot.RESOURCE);
            writer.writeAttribute(RDFNames.Slot.ABOUT, rdfExternalResource.getResourceURI());
            writer.writeEndElement(); // end of external resource
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
        try {
            if (rdfsDatatype.isAnonymous()) {
                writer.writeStartElement(RDFSNames.Cls.DATATYPE);
                Util.insertProperties(rdfsDatatype, tripleStore, writer);
            }
            else {
                writer.writeAttribute(RDFNames.Slot.RESOURCE, rdfsDatatype.getURI());
            }
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        try {
            Util.insertResourceAsElement(rdfIndividual.getRDFType(), writer);
            Util.insertAboutAttribute(rdfIndividual, writer);
            insertProperties(rdfIndividual);
            writer.writeEndElement(); // end of individual
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitRDFList(RDFList rdfList) {
        renderValuesAsRDFList(rdfList.getValues(), rdfList.getOWLModel());
    }


    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        try {
            writer.writeStartElement(RDFSNames.Cls.NAMED_CLASS);
            Util.insertAboutAttribute(rdfsNamedClass, writer);
            insertProperties(rdfsNamedClass);
            writer.writeEndElement(); // end of rdfs:Class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void writeRestrictionStart(OWLRestriction restriction) {
        try {
            writer.writeStartElement(OWLNames.Cls.RESTRICTION);
            writer.writeStartElement(OWLNames.Slot.ON_PROPERTY);
            Util.insertResourceAttribute(restriction.getOnProperty(), writer);
            writer.writeEndElement(); // end of owl:onProperty
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderCardinalityRestriction(OWLCardinalityBase cardinalityBase, String keyWord) {
        try {
            writeRestrictionStart(cardinalityBase);
            writer.writeStartElement(keyWord);
            writer.writeAttribute(RDFNames.Slot.DATATYPE, Vocab.INT_DATATYPE);
            writer.writeTextContent(Integer.toString(cardinalityBase.getCardinality()));
            writer.writeEndElement(); // end of restriction type/filler
            insertProperties(cardinalityBase);
            writer.writeEndElement(); // end of owl:Restriction
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderNAryLogicalClass(OWLNAryLogicalClass logicalClass, String keyWord) {
        try {
            writer.writeStartElement(OWLNames.Cls.NAMED_CLASS);
            writer.writeStartElement(keyWord);
            writer.writeAttribute(RDFNames.Slot.PARSE_TYPE, RDFNames.COLLECTION);
            Collection ops = new TreeSet(new FrameComparator());
            ops.addAll(logicalClass.getOperands());
            for (Iterator it = ops.iterator(); it.hasNext();) {
                RDFResource curResource = (RDFResource) it.next();
                new RDFResourceRenderer(curResource, tripleStore, writer).write();
            }
            writer.writeEndElement(); // End of type element
            insertProperties(logicalClass);
            writer.writeEndElement(); // End of owl:Class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderQuantifierRestriction(OWLQuantifierRestriction quantifierRestriction, String keyWord) {
        try {
            writeRestrictionStart(quantifierRestriction);
            RDFResource filler = quantifierRestriction.getFiller();
            writer.writeStartElement(keyWord);
//            if (filler instanceof RDFSDatatype) {
//                writer.writeAttribute(RDFNames.Slot.RESOURCE, ((RDFSDatatype) filler).getURI());
//            }
//            else {
                Util.inlineObject(filler, tripleStore, writer);
            //}
            writer.writeEndElement(); // End of some or all element
            insertProperties(quantifierRestriction);
            writer.writeEndElement(); // End of owl:Restriction
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderValuesAsRDFList(Collection values, OWLModel model) {
        try {
            int counter = 0;
            for (Iterator it = values.iterator(); it.hasNext();) {
                writer.writeStartElement(RDFNames.Cls.LIST);
                writer.writeStartElement(RDFNames.Slot.FIRST);
                Object curVal = it.next();
                if (curVal instanceof RDFResource) {
                    Util.inlineObject((RDFResource) curVal, tripleStore, writer);
                }
                else {
                    RDFSLiteral curLiteral = model.asRDFSLiteral(curVal);
                    writer.writeAttribute(RDFNames.Slot.DATATYPE, curLiteral.getDatatype().getURI());
                    writer.writeTextContent(curLiteral.getPlainValue().toString());
                }
                writer.writeEndElement(); // End of rdf:first
                writer.writeStartElement(RDFNames.Slot.REST);
                if (it.hasNext() == false) {
                    writer.writeAttribute(RDFNames.Slot.RESOURCE, model.getRDFNil().getURI());
                }
                counter++;
            }
            for (int i = 0; i < counter; i++) {
                writer.writeEndElement(); // end rdf:rest
                writer.writeEndElement(); // end of rdf:List
            }
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void insertProperties(RDFResource resource) throws IOException {
        Util.insertProperties(resource, tripleStore, writer);
    }


}

