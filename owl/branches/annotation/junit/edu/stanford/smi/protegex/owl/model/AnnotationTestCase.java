package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

public class AnnotationTestCase extends AbstractJenaTestCase {
    
    public void testSimpleAnnotation() {
        String annotationName = "foo";
        RDFProperty annotation = owlModel.createAnnotationProperty(annotationName);
        assertFalse(annotation.hasProtegeType((RDFSClass) owlModel.getOWLDatatypePropertyMetaClassCls()));
        assertFalse(annotation.hasProtegeType((RDFSClass) owlModel.getOWLObjectPropertyMetaClassCls()));
        try {
            owlModel = reload((JenaOWLModel) owlModel);
        } catch (Exception e) {
            assertFalse(true);
        }
        annotation = owlModel.getRDFProperty(annotationName);
        assertFalse(annotation.hasProtegeType((RDFSClass) owlModel.getOWLDatatypePropertyMetaClassCls()));
        assertFalse(annotation.hasProtegeType((RDFSClass) owlModel.getOWLObjectPropertyMetaClassCls()));
    }

}
