package edu.stanford.smi.protegex.owl.ui.forms;

import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;


/**
 * Class for loadind/saving owl widget properties in the .forms file.
 * @author Tania Tudorache
 *
 */
public class OWLWidgetPropertyListUtil {
	
	public static final String GRAPHWIDGET_JAVA_CLASS_NAME = "edu.stanford.smi.protegex.widget.graph.GraphWidget";

	public static final String GRAPHWIDGET_NAME_SEPARATOR = "_";
	
	public static Resource createOWLPropertyList(OWLModel owlModel, Resource widget, PropertyList widgetPropertyList) {
		if (widgetPropertyList == null || widgetPropertyList.getNames().size() == 0)
			return null;
			
		Resource formsPropList = widget.getModel().createResource(null, FormsNames.PropertyList);
		widget.addProperty(FormsNames.property_list, formsPropList);
				
		for (Iterator iter = widgetPropertyList.getNames().iterator(); iter.hasNext();) {
			String widgetPropName = (String) iter.next();
			createOWLPropertyList(owlModel, widget, widgetPropertyList, widgetPropName, formsPropList);
		}
	
		return formsPropList;
	}

	private static void createOWLPropertyList(OWLModel owlModel, Resource widget, PropertyList widgetPropertyList, String widgetPropName, Resource formsPropList) {
				
		Statement javaClassNameStmt = widget.getProperty(ProtegeFormsNames.javaClassName);
	    String javaClassName = null;
	    if (javaClassNameStmt != null) {
	    	javaClassName = javaClassNameStmt.getString();
	    }
		
		String stringValue = widgetPropertyList.getString(widgetPropName);		
		if (stringValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.String);
			resource.addProperty(FormsNames.name, getAbsoluteWidgetPropertyName(owlModel, widgetPropName, javaClassName));
			resource.addProperty(FormsNames.string_value, stringValue);
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}
		
		Integer intValue = widgetPropertyList.getInteger(widgetPropName);
		if (intValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.Integer);
			resource.addProperty(FormsNames.name, getAbsoluteWidgetPropertyName(owlModel, widgetPropName, javaClassName));
			resource.addProperty(FormsNames.integer_value, resource.getModel().createLiteral(intValue.intValue()));
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}
		
		Boolean boolValue = widgetPropertyList.getBoolean(widgetPropName);
		if (boolValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.Boolean);
			resource.addProperty(FormsNames.name, getAbsoluteWidgetPropertyName(owlModel, widgetPropName, javaClassName));			
			resource.addProperty(FormsNames.booleanValue, resource.getModel().createLiteral(boolValue.booleanValue()));
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}
		
		PropertyList propListValue = widgetPropertyList.getPropertyList(widgetPropName);
		if (propListValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.PropertyList);
			resource.addProperty(FormsNames.name, getAbsoluteWidgetPropertyName(owlModel, widgetPropName, javaClassName));
			Resource formsPropListInt = createOWLPropertyList(owlModel, resource, propListValue);
			if (formsPropListInt != null)
				resource.addProperty(FormsNames.property_list, formsPropListInt);
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}		
	}

	
	/**
	 * Handle the GraphWidget specially because of the naming problem
	 * @param owlModel
	 * @param widgetPropName
	 * @param javaClassName
	 * @return
	 */
	private static String getAbsoluteWidgetPropertyName(OWLModel owlModel, String widgetPropName, String javaClassName) {
		if (javaClassName == null || !javaClassName.equals(GRAPHWIDGET_JAVA_CLASS_NAME)) {
			return widgetPropName;
		}
		
		//try to get the classname from the property key
		int index = widgetPropName.lastIndexOf(GRAPHWIDGET_NAME_SEPARATOR);
		
		if (index == -1 || index == widgetPropName.length()) {
			return widgetPropName;
		}
		
		String localClassName = widgetPropName.substring(0, index);
		String afterSeparatorPropName = widgetPropName.substring(index);
		
		String absoluteResourceURI = null;
		
		try {
			absoluteResourceURI = owlModel.getURIForResourceName(localClassName);
		} catch (Exception e) {
			return widgetPropName;
		}
				
		return (absoluteResourceURI == null ? widgetPropName : absoluteResourceURI + afterSeparatorPropName);
	}

	
	
	public static void loadFormsProperties(OWLModel owlModel, SlotWidget slotWidget, Resource widgetResource) {
		Statement formsPropertyListStmt = widgetResource.getProperty(FormsNames.property_list);
				
		if (formsPropertyListStmt == null)
			return;
		
		Resource formsPropertyList = formsPropertyListStmt.getResource();
		
		if (formsPropertyList == null)
			return;
		
		StmtIterator stmtIt = formsPropertyList.listProperties(FormsNames.properties);
		while (stmtIt.hasNext()) {
			Statement s = stmtIt.nextStatement();
			
			loadFormProperties(owlModel, slotWidget, widgetResource, s.getResource());
		}
	}

	private static void loadFormProperties(OWLModel owlModel, SlotWidget slotWidget, Resource widgetResource, Resource resource) {
		if (resource == null)
			return;
		
		Statement typeStatement = resource.getProperty(RDF.type);
        if (typeStatement == null) 
        	return;
        
        Resource type = typeStatement.getResource();
        
		Statement javaClassNameStmt = widgetResource.getProperty(ProtegeFormsNames.javaClassName);
	    String javaClassName = null;
	    if (javaClassNameStmt != null) {
	    	javaClassName = javaClassNameStmt.getString();
	    }
		
        if (type.equals(FormsNames.String)) {
        	Statement nameStmt = resource.getProperty(FormsNames.name);        	
        	if (nameStmt != null) {
        		Statement valueStmt = resource.getProperty(FormsNames.string_value);
        		if (valueStmt != null) {
        			slotWidget.getDescriptor().getPropertyList().setString(getLocalWidgetPropertyName(owlModel, nameStmt.getString(), javaClassName), valueStmt.getString());        			
        			return;
        		}
        	}
        }
        
        if (type.equals(FormsNames.Integer)) {
        	Statement nameStmt = resource.getProperty(FormsNames.name);        	
        	if (nameStmt != null) {
        		Statement valueStmt = resource.getProperty(FormsNames.integer_value);
        		if (valueStmt != null) {
        			slotWidget.getDescriptor().getPropertyList().setInteger(getLocalWidgetPropertyName(owlModel, nameStmt.getString(), javaClassName), valueStmt.getInt());        			
        			return;
        		}
        	}
        }
        
        if (type.equals(FormsNames.Boolean)) {
        	Statement nameStmt = resource.getProperty(FormsNames.name);        	
        	if (nameStmt != null) {
        		Statement valueStmt = resource.getProperty(FormsNames.booleanValue);
        		if (valueStmt != null) {
        			slotWidget.getDescriptor().getPropertyList().setBoolean(getLocalWidgetPropertyName(owlModel, nameStmt.getString(), javaClassName), valueStmt.getBoolean());        			
        			return;
        		}
        	}
        }       
        
	}
	
	
	private static String getLocalWidgetPropertyName(OWLModel owlModel, String widgetPropName, String javaClassName) {
		if (javaClassName == null || !javaClassName.equals(GRAPHWIDGET_JAVA_CLASS_NAME)) {
			return widgetPropName;
		}
		
		//try to get the classname from the property key
		int index = widgetPropName.lastIndexOf(GRAPHWIDGET_NAME_SEPARATOR);
		
		if (index == -1 || index == widgetPropName.length()) {
			return widgetPropName;
		}
		
		String absoluteClassName = widgetPropName.substring(0, index);
		String afterSeparatorPropName = widgetPropName.substring(index);
		
		String localClassName = null; 
			
		try {
			localClassName = owlModel.getResourceNameForURI(absoluteClassName);
		} catch (Exception e) {
			return widgetPropName;
		}
				
		return (localClassName == null ? widgetPropName : localClassName + afterSeparatorPropName);
	}

}
