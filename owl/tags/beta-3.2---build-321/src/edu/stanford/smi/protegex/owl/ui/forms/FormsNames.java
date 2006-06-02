/* CVS $Id: FormsNames.java,v 1.2 2005/12/31 14:08:16 matthewhorridge Exp $ */
package edu.stanford.smi.protegex.owl.ui.forms;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
/**
 * Vocabulary definitions from file:/C:/protege-owl/owl/schemagen-temp.owl 
 * @author Auto-generated by schemagen on 27 Oct 2005 13:05 
 */
public class FormsNames {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.owl-ontologies.com/forms/forms.owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );

    public static final ObjectProperty forClass = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#forClass" );

    public static final ObjectProperty forProperty = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#forProperty" );

    public static final ObjectProperty layoutData = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#layoutData" );
    
    public static final ObjectProperty widgets = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#widgets" );
    
    public static final DatatypeProperty preferredWidth = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#preferredWidth" );
    
    public static final DatatypeProperty preferredHeight = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#preferredHeight" );
    
    public static final OntClass LayoutData = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#LayoutData" );
    
    public static final OntClass FormWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#FormWidget" );
    
    public static final OntClass Widget = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#Widget" );
    
}
