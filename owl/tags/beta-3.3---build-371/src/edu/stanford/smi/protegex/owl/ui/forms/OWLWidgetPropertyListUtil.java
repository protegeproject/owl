package edu.stanford.smi.protegex.owl.ui.forms;

import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.widget.SlotWidget;

/**
 * Class for loadind/saving owl widget properties in the .forms file.
 * @author Tania Tudorache
 *
 */
public class OWLWidgetPropertyListUtil {

	public static Resource createOWLPropertyList(Resource widget, PropertyList widgetPropertyList) {
		if (widgetPropertyList == null || widgetPropertyList.getNames().size() == 0)
			return null;
			
		Resource formsPropList = widget.getModel().createResource(null, FormsNames.PropertyList);
		widget.addProperty(FormsNames.property_list, formsPropList);
				
		for (Iterator iter = widgetPropertyList.getNames().iterator(); iter.hasNext();) {
			String widgetPropName = (String) iter.next();
			createOWLPropertyList(widget, widgetPropertyList, widgetPropName, formsPropList);
		}
	
		return formsPropList;
	}

	private static void createOWLPropertyList(Resource widget, PropertyList widgetPropertyList, String widgetPropName, Resource formsPropList) {
		
		String stringValue = widgetPropertyList.getString(widgetPropName);		
		if (stringValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.String);
			resource.addProperty(FormsNames.name, widgetPropName);
			resource.addProperty(FormsNames.string_value, stringValue);
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}
		
		Integer intValue = widgetPropertyList.getInteger(widgetPropName);
		if (intValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.Integer);
			resource.addProperty(FormsNames.name, widgetPropName);
			resource.addProperty(FormsNames.integer_value, resource.getModel().createLiteral(intValue.intValue()));
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}
		
		Boolean boolValue = widgetPropertyList.getBoolean(widgetPropName);
		if (boolValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.Boolean);
			resource.addProperty(FormsNames.name, widgetPropName);			
			resource.addProperty(FormsNames.booleanValue, resource.getModel().createLiteral(boolValue.booleanValue()));
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}
		
		PropertyList propListValue = widgetPropertyList.getPropertyList(widgetPropName);
		if (propListValue != null){
			Resource resource = formsPropList.getModel().createResource(null, FormsNames.PropertyList);
			resource.addProperty(FormsNames.name, widgetPropName);
			Resource formsPropListInt = createOWLPropertyList(resource, propListValue);
			if (formsPropListInt != null)
				resource.addProperty(FormsNames.property_list, formsPropListInt);
			formsPropList.addProperty(FormsNames.properties, resource);
			return;
		}		
	}

	public static void loadFormsProperties(SlotWidget slotWidget, Resource widgetResource) {
		Statement formsPropertyListStmt = widgetResource.getProperty(FormsNames.property_list);
				
		if (formsPropertyListStmt == null)
			return;
		
		Resource formsPropertyList = formsPropertyListStmt.getResource();
		
		if (formsPropertyList == null)
			return;
		
		StmtIterator stmtIt = formsPropertyList.listProperties(FormsNames.properties);
		while (stmtIt.hasNext()) {
			Statement s = stmtIt.nextStatement();
			
			loadFormProperties(slotWidget, widgetResource, s.getResource());
		}
	}

	private static void loadFormProperties(SlotWidget slotWidget, Resource widgetResource, Resource resource) {
		if (resource == null)
			return;
		
		Statement typeStatement = resource.getProperty(RDF.type);
        if (typeStatement == null) 
        	return;
        
        Resource type = typeStatement.getResource();
		
        if (type.equals(FormsNames.String)) {
        	Statement nameStmt = resource.getProperty(FormsNames.name);        	
        	if (nameStmt != null) {
        		Statement valueStmt = resource.getProperty(FormsNames.string_value);
        		if (valueStmt != null) {
        			slotWidget.getDescriptor().getPropertyList().setString(nameStmt.getString(), valueStmt.getString());        			
        			return;
        		}
        	}
        }
        
        if (type.equals(FormsNames.Integer)) {
        	Statement nameStmt = resource.getProperty(FormsNames.name);        	
        	if (nameStmt != null) {
        		Statement valueStmt = resource.getProperty(FormsNames.integer_value);
        		if (valueStmt != null) {
        			slotWidget.getDescriptor().getPropertyList().setInteger(nameStmt.getString(), valueStmt.getInt());        			
        			return;
        		}
        	}
        }
        
        if (type.equals(FormsNames.Boolean)) {
        	Statement nameStmt = resource.getProperty(FormsNames.name);        	
        	if (nameStmt != null) {
        		Statement valueStmt = resource.getProperty(FormsNames.booleanValue);
        		if (valueStmt != null) {
        			slotWidget.getDescriptor().getPropertyList().setBoolean(nameStmt.getString(), valueStmt.getBoolean());        			
        			return;
        		}
        	}
        }
        
        
        
	}

}
