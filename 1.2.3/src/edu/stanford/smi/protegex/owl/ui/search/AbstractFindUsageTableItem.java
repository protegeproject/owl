package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;

/**
 * An entry in the FindUsageTableModel.
 * 
 */
public abstract class AbstractFindUsageTableItem implements FindUsageTableItem {

    protected RDFResource host;

    protected int type;
    
    protected Object usage;


    protected AbstractFindUsageTableItem(int type, RDFResource host, Object usage) {
        this.type = type;
        this.host = host;
        this.usage = usage;
    }


    /* (non-Javadoc)
	 * @see edu.stanford.smi.protegex.owl.ui.search.FindUsageTableItemInterf#contains(edu.stanford.smi.protege.model.Frame)
	 */
    public boolean contains(Frame frame) {	
		return host.equals(frame) || usage.equals(frame);
	}

    
    public Object getUsage() {
    	return usage;
    }
    
    public int getType() {
    	return type;
    }
    
    public RDFResource getHost() {
    	return host;    
    }
    

    /* (non-Javadoc)
	 * @see edu.stanford.smi.protegex.owl.ui.search.FindUsageTableItemInterf#getIcon()
	 */
    public Icon getIcon() {
        switch (type) {
            case SUPERCLASS:
                return OWLIcons.getImageIcon(OWLIcons.RDFS_SUBCLASS_OF);
            case EQUIVALENT_CLASS:
                return OWLIcons.getImageIcon(OWLIcons.OWL_EQUIVALENT_CLASS);
            case DISJOINT_CLASS:
                return OWLIcons.getImageIcon(OWLIcons.OWL_DISJOINT_CLASSES);
            case RANGE:
                return OWLIcons.getImageIcon(OWLIcons.RDFS_RANGE);
            case VALUE:
                return OWLIcons.getImageIcon(OWLIcons.OWL_OBJECT_PROPERTY);
            case PROPERTY_VALUE:
            	return OWLIcons.getBackspaceIcon();
        }
        return null;
    }
}
