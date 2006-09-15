package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.LazyTreeNodeFrameComparator;
import edu.stanford.smi.protege.ui.SlotSubslotNode;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.*;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLPropertySubpropertyRoot extends LazyTreeRoot {

    private OWLModel owlModel;

    private ModelListener modelListener = new ModelAdapter() {
        public void propertyCreated(RDFProperty property) {
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


        public void propertyDeleted(RDFProperty property) {
            List properties = (List) getUserObject();
            boolean changed = properties.remove(property);
            if (changed) {
                childRemoved(property);
            }
        }
    };

    public PropertyListener propertyListener = new PropertyAdapter() {
        public void superpropertyAdded(RDFProperty property, RDFProperty superproperty) {
            if (property.getSuperpropertyCount() == 1 && isSuitable(property)) {
                removeChild(property);
            }
        }


        public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty) {
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
		public void typeAdded(RDFResource resource,
		                      RDFSClass type) {
			if(resource instanceof RDFProperty) {
				addChild((RDFProperty) resource);
				removeChild((RDFProperty) resource);
			}
		}


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
		super(topLevelProperties);
		this.owlModel = owlModel;
		this.owlModel.addModelListener(modelListener);
		this.owlModel.addPropertyListener(propertyListener);
		this.owlModel.addResourceListener(resourceListener);
	}

	public boolean isSuitable(RDFProperty rdfProperty) {
		return true;
	}


    public OWLPropertySubpropertyRoot(OWLModel owlModel) {
        super(getValidSlots(owlModel));
        owlModel.addModelListener(modelListener);
        owlModel.addPropertyListener(propertyListener);
	    owlModel.addResourceListener(resourceListener);
        this.owlModel = owlModel;
    }


    public LazyTreeNode createNode(Object o) {
        return new SlotSubslotNode(this, (Slot) o);
    }


    public void dispose() {
        super.dispose();
        owlModel.removeModelListener(modelListener);
        owlModel.removePropertyListener(propertyListener);
    }


    public Comparator getComparator() {
        return new LazyTreeNodeFrameComparator();
    }


    private static Collection getValidSlots(OWLModel owlModel) {
        List results = new ArrayList(owlModel.getVisibleUserDefinedRDFProperties());
        Iterator i = results.iterator();
        while (i.hasNext()) {
            Slot slot = (Slot) i.next();
            if (slot.getDirectSuperslotCount() > 0) {
                i.remove();
            }
        }
        results.remove(owlModel.getRDFProperty(Model.Slot.FROM));
        results.remove(owlModel.getRDFProperty(Model.Slot.TO));
        results.removeAll(Arrays.asList(owlModel.getSystemAnnotationProperties()));
        Collections.sort(results, new FrameComparator());
        return results;
    }
}
