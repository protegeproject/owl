package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultRDFSDatatypeFactory implements RDFSDatatypeFactory {

    private OWLModel owlModel;


    public DefaultRDFSDatatypeFactory(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    public RDFSDatatype createAnonymousDatatype(RDFSDatatype baseType) {
        String name = owlModel.getNextAnonymousResourceName();
        return createDatatype(baseType, name);
    }


    public RDFSDatatype createDatatype(RDFSDatatype baseType, String name) {
        RDFSDatatype datatype = owlModel.createRDFSDatatype(name);
        RDFProperty property = owlModel.getRDFProperty(XSPNames.BASE);
        datatype.setPropertyValue(property, baseType);
        return datatype;
    }


    public void setLength(RDFSDatatype datatype, int value) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.LENGTH);
        if (value >= 0) {
            datatype.setPropertyValue(property, new Integer(value));
        }
        else {
            datatype.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    public void setMaxExclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.MAX_EXCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMaxInclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.MAX_INCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMaxLength(RDFSDatatype datatype, int value) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.MAX_LENGTH);
        if (value >= 0) {
            datatype.setPropertyValue(property, new Integer(value));
        }
        else {
            datatype.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    public void setMinExclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.MIN_EXCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMinInclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.MIN_INCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMinLength(RDFSDatatype datatype, int value) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.MIN_LENGTH);
        if (value >= 0) {
            datatype.setPropertyValue(property, new Integer(value));
        }
        else {
            datatype.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    public void setPattern(RDFSDatatype datatype, String value) {
        RDFProperty property = owlModel.getRDFProperty(XSPNames.PATTERN);
        datatype.setPropertyValue(property, value);
    }
}
