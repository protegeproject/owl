package edu.stanford.smi.protegex.owl.ui.search;

import javax.swing.Icon;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public interface FindUsageTableItem {

	public final static int SUPERCLASS = 0;

	public final static int EQUIVALENT_CLASS = 1;

	public final static int DISJOINT_CLASS = 2;

	public final static int RANGE = 3;

	public final static int VALUE = 4;
	
	public final static int PROPERTY_VALUE = 5;

	public boolean contains(Frame frame);

	public Icon getIcon();	
		
	public RDFResource getHost();
	
	public Object getUsage();
	
	public int getType();

}