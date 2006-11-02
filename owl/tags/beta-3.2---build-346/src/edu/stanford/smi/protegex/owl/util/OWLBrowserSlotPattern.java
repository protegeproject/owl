package edu.stanford.smi.protegex.owl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jena.owlsyntax;
import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

/**
 * The OWL browser slot pattern used to display the name of OWL elements. It treats the RDFSLiterals 
 * in a special manner based on the default language.
 * @author ttania
 *
 */
public class OWLBrowserSlotPattern extends BrowserSlotPattern{
	
	public OWLBrowserSlotPattern(List elements) {
		super(elements);		
	}

	public OWLBrowserSlotPattern(Slot slot) {
		super(slot);
	}
	
    public String getBrowserText(Instance instance) {    	
        StringBuffer buffer = new StringBuffer();
        Iterator i = getElements().iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof Slot) {
                buffer.append(getText((Slot) o, instance));
            } else {
                buffer.append(o);
            }
        }
        return (buffer.length() == 0) ? null : (buffer.toString());
    }

    private String getText(Slot slot, Instance instance) {
		String text = null;
		String defaultLang = null;
		Collection values = null;
			
		if (slot instanceof RDFProperty) {
			values = ((RDFResource) instance)
					.getPropertyValues((RDFProperty) slot);
			defaultLang = getDefaultLanguage(instance.getKnowledgeBase());
		} else
			values = instance.getDirectOwnSlotValues(slot);

		if (values.size() > 1) { // multiple values
//			TODO: find a more efficient implementation of this!!
			Collection rdfLabelsWithNullLang = new ArrayList();
			Collection rdfLabelsWithNonNullLang = new ArrayList();
			
			StringBuffer buffer = new StringBuffer();
			int valuesNo = 0;
			
			if (defaultLang == null || slot.getValueType() != ValueType.STRING) { //no default language
				valuesNo = getBrowserTextFromValues(instance, values, null, buffer);
			} else {//default language set
				
				for (Iterator iter = values.iterator(); iter.hasNext();) {
					Object o = (Object) iter.next();
					if ((o instanceof RDFSLiteral && (((RDFSLiteral)o).getLanguage() == null)) || (o instanceof String))
						rdfLabelsWithNullLang.add(o);
					else
						rdfLabelsWithNonNullLang.add(o);
				}
				
				valuesNo = getBrowserTextFromValues(instance, rdfLabelsWithNonNullLang, defaultLang, buffer);
				
				if (valuesNo == 0)
					valuesNo = getBrowserTextFromValues(instance, rdfLabelsWithNullLang, defaultLang, buffer);				
			}
					
			if (valuesNo > 1) {
				buffer.insert(0, "{");
				buffer.insert(buffer.length(), "}");
			}

			if (valuesNo > 0) {
				text = buffer.toString();
			} else
				text = instance.getName();
		} else { // single value
			Object o = CollectionUtilities.getFirstItem(values);
			if (o == null)
				return instance.getName();

			text = getText(o, instance, defaultLang);
			if (text == null)
				text = instance.getName();
		}

		return text;
	}
        
    private int getBrowserTextFromValues(Instance instance, Collection values, String lang, StringBuffer buffer) {   	
    	 boolean isFirst = true;
    	 int valuesNo = 0;
    	 
    	 Iterator i = values.iterator();
         while (i.hasNext()) {
             Object o = i.next();
             String partialText = getText(o, instance, lang);
             if (partialText != null) {
                 if (isFirst) {
                     isFirst = false;
                 } else {
                     buffer.append(", ");
                 }                	
             	buffer.append(partialText);
             	valuesNo ++;               	
             }
         }         
         return valuesNo;
    }

	private String getText(Object o, Instance instance, String lang) {
        String text;
        if (o == null) {
            text = "";
        } else if (o instanceof Frame) {
            if (o.equals(instance)) {
                text = "<recursive call>";
            } else {
                text = ((Frame) o).getBrowserText();
            }
        } else if (o instanceof RDFSLiteral) {
        	text = getLangBrowserText(o, lang);
        } else {
            text = o.toString();
        }
        return text;
    }

	
    private String getDefaultLanguage(KnowledgeBase kb) {
    	if (kb == null || !(kb instanceof OWLModel))
    		return null;
    	
    	return ((OWLModel) kb).getDefaultLanguage();		
	}

	private String getLangBrowserText(Object value, String defaultLanguage) {
		if (value == null || !(value  instanceof RDFSLiteral))
			return null;
		
		RDFSLiteral rdfsValue = (RDFSLiteral) value;
		   	
    	if (defaultLanguage == null) {
			if (rdfsValue.getLanguage() == null) {
				String text = rdfsValue.getString();			
				return text;
			}
		} else { //default language is not null
			if (rdfsValue.getLanguage().equals(defaultLanguage)) {
				String text = ((RDFSLiteral)value).getString();				
				return text;
			} 
		}		
	    
		return null;
	}
	
    public String toString() {
        return "OWLBrowserSlotPattern(" + getSerialization() + ")";
    }
}
