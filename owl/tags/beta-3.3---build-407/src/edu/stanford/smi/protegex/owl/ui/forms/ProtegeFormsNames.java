/* CVS $Id: ProtegeFormsNames.java,v 1.2 2005/12/31 14:08:16 matthewhorridge Exp $ */
package edu.stanford.smi.protegex.owl.ui.forms; 

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
/**
 * Vocabulary definitions from file:/C:/protege-owl/owl/schemagen-temp.owl 
 * @author Auto-generated by schemagen on 27 Oct 2005 15:05 
 */
public class ProtegeFormsNames {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.owl-ontologies.com/forms/protege.owl#";
    
    public static final String NAME = "http://www.owl-ontologies.com/forms/protege.owl";
    
    public static final String PROTEGE_FORMS_ONTOLOGY = "http://www.owl-ontologies.com/forms/protege.owl";
    
    public static final String ABSOLUTE_FORMS_ONTOLOGY = "http://www.owl-ontologies.com/forms/absolute.owl";
    
    public static final String FORMS_ONTOLOGY = "http://www.owl-ontologies.com/forms/forms.owl";
    
    public static final String FORMS_RELATIVE_DIR = "forms";
    
    public static final String PROTEGE_FORMS_FILENAME = "protegeForms.owl";
    
    public static final String ABSOLUTE_FORMS_FILENAME = "absolute.owl";
    
    public static final String FORMS_FILENAME = "forms.owl";
    
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final DatatypeProperty javaClassName = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/protege.owl#javaClassName" );
        
    public static final OntClass HeaderWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#HeaderWidget" );
    
    public static final OntClass SingleResourceWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#SingleResourceWidget" );
    
    public static final OntClass OWLRangeWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#OWLRangeWidget" );
    
    public static final OntClass MultiLiteralWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#MultiLiteralWidget" );
    
    public static final OntClass OWLDatatypePropertyTypesWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#OWLDatatypePropertyTypesWidget" );
    
    public static final OntClass SingleLiteralWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#SingleLiteralWidget" );
    
    public static final OntClass OWLDomainWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#OWLDomainWidget" );
    
    public static final OntClass MultiResourceWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#MultiResourceWidget" );
    
    public static final OntClass RDFPropertyTypesWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#RDFPropertyTypesWidget" );
    
    public static final OntClass OWLObjectPropertyTypesWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/protege.owl#OWLObjectPropertyTypesWidget" );
    
}
