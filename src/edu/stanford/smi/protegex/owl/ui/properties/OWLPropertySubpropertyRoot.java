package edu.stanford.smi.protegex.owl.ui.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.LazyTreeNodeFrameComparator;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceAdapter;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLPropertySubpropertyRoot extends LazyTreeRoot {

    private OWLModel owlModel;

    private ModelListener modelListener = new ModelAdapter() {
        @Override
		public void propertyCreated(RDFProperty property, KnowledgeBaseEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            if (property.getSuperproperties(false).isEmpty() && isSuitable(property)) {
                List properties = (List) getUserObject();
                int index = 0;
                if (index < 0) {
                    index = -(index + 1);
                }
                properties.add(index, property);
                childAdded(property, index);
                if (getChildCount() == 1) {
                    notifyNodeStructureChanged(OWLPropertySubpropertyRoot.this);
                }
            }
        }


        @Override
		public void propertyDeleted(RDFProperty property, KnowledgeBaseEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            List properties = (List) getUserObject();
            boolean changed = properties.remove(property);
            if (changed) {
                childRemoved(property);
            }
        }
        
      	public void resourceReplaced(RDFResource oldResource, RDFResource newResource, String oldName) {
      		Object userObj = getUserObject();
      		
      		if (userObj != null && userObj.equals(oldResource)) {
      			reload(newResource);
      		}
    	}
        
        
    };

    public PropertyListener propertyListener = new PropertyAdapter() {
        @Override
		public void superpropertyAdded(RDFProperty property, RDFProperty superproperty, SlotEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            if (property.getSuperpropertyCount() == 1 && isSuitable(property)) {
                removeChild(property);
            }
        }


        @Override
		public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty, SlotEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            if (property.getSuperpropertyCount() == 0 && isSuitable(property)) {
                addChild(property);
            }
        }


        private void removeChild(Slot slot) {
            List slots = (List) getUserObject();
            slots.remove(slot);
            childRemoved(slot);
        }


        private void addChild(Slot slot) {
            List slots = (List) getUserObject();
            slots.add(slot);
            childAdded(slot);
        }
    };

	public ResourceListener resourceListener = new ResourceAdapter() {
		@Override
		public void typeAdded(RDFResource resource,
		                      RDFSClass type) {
			if(resource instanceof RDFProperty) {
				addChild((RDFProperty) resource);
				removeChild((RDFProperty) resource);
			}
		}


		@Override
		public void typeRemoved(RDFResource resource,
		                        RDFSClass type) {
			if(resource instanceof RDFProperty) {
				removeChild((RDFProperty) resource);
				addChild((RDFProperty) resource);
			}
		}

		private void removeChild(RDFProperty slot) {
			List slots = (List) getUserObject();
			if(slots.contains(slot) && isSuitable(slot) == false) {
				slots.remove(slot);
				childRemoved(slot);
			}
		}


        private void addChild(RDFProperty slot) {
            List slots = (List) getUserObject();
            if(slots.contains(slot) == false &&
               slot.getSuperpropertyCount() == 0 &&
               isSuitable(slot)) {
		        slots.add(slot);
		        childAdded(slot);
	        }
        }
	};

	public OWLPropertySubpropertyRoot(OWLModel owlModel, Collection topLevelProperties) {
		this(owlModel, topLevelProperties, OWLUI.getSortPropertiesTreeOption());
	}
	
	public OWLPropertySubpropertyRoot(OWLModel owlModel, Collection topLevelProperties, boolean isSorted) {
		super(topLevelProperties, isSorted);
		this.owlModel = owlModel;
		this.owlModel.addModelListener(modelListener);
		this.owlModel.addPropertyListener(propertyListener);
		this.owlModel.addResourceListener(resourceListener);
	}

	
	public OWLPropertySubpropertyRoot(OWLModel owlModel) {
		this(owlModel, OWLUI.getSortPropertiesTreeOption());
	}
	
    public OWLPropertySubpropertyRoot(OWLModel owlModel, boolean isSorted) {
        super(getValidSlots(owlModel), isSorted);
        owlModel.addModelListener(modelListener);
        owlModel.addPropertyListener(propertyListener);
	    owlModel.addResourceListener(resourceListener);
        this.owlModel = owlModel;
    }


    @Override
	public LazyTreeNode createNode(Object o) {
        return new OWLPropertySubpropertyNode(this, (Slot) o);
    }


    @Override
	public void dispose() {
        super.dispose();
        owlModel.removeModelListener(modelListener);
        owlModel.removePropertyListener(propertyListener);
    }


    @Override
	public Comparator getComparator() {
        return new LazyTreeNodeFrameComparator();
    }


    @SuppressWarnings("unchecked")
	private static Collection getValidSlots(OWLModel owlModel) {
        List results = new ArrayList(owlModel.getVisibleUserDefinedRDFProperties());
        Iterator i = results.iterator();
        while (i.hasNext()) {
            Slot slot = (Slot) i.next();
            if (slot.getDirectSuperslotCount() > 0) {
                i.remove();
            }
        }
        results.removeAll(Arrays.asList(owlModel.getSystemAnnotationProperties()));
        
        if (OWLUI.getSortPropertiesTreeOption()) {
        	Collections.sort(results, new FrameComparator());
        }
        return results;
    }
    
	public boolean isSuitable(RDFProperty rdfProperty) {
		return true;
	}

}
