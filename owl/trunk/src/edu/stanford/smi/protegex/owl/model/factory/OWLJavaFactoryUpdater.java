package edu.stanford.smi.protegex.owl.model.factory;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParserException;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLJavaFactoryUpdater {

    private int count = 0;

    private Set metaclasses = new HashSet();

    private Set metaslots = new HashSet();

    private MergingNarrowFrameStore mnfs;


    public OWLJavaFactoryUpdater(KnowledgeBase kb) {
        this(kb, kb.getFrames());
    }


    public OWLJavaFactoryUpdater(final KnowledgeBase kb, final Collection frames) {

//        AbstractTask task = new AbstractTask("Running OWLJavaFactoryUpdater", true, ((OWLModel)kb).getTaskManager(), 0, frames.size()) {
//	        public void runTask()
//	                throws Exception {
		        mnfs = MergingNarrowFrameStore.get(kb);

				findMetaclasses(kb);

				//long startTime = System.currentTimeMillis();

				Instance instance = null;
				try {
					int prog = 0;
					for (Iterator it = frames.iterator(); it.hasNext();) {
						prog++;
						instance = (Instance) it.next();
						final Instance newInstance = createNewFrame(instance);
						if (instance.getClass() != newInstance.getClass()) {
							mnfs.replaceFrame(newInstance);
//							if ((++count % 10000) == 0) {
//								log("" + count + ": Replacing " + instance.getName() + " from " + instance.getClass() + " to " + newInstance.getClass());
//							}
						}
//						setProgress(prog);
					}
				}
				catch (ClassCastException ccx) {
					Collection types = instance.getDirectTypes();
					for (Iterator it = types.iterator(); it.hasNext();) {
						Instance type = (Instance) it.next();
						if (!(type instanceof Cls)) {
							throw new ProtegeOWLParserException("The resource " + instance.getName() +
									" has the rdf:type " + type.getName() + " which is not a class but a " + type.getClass().getName(),
									"In many cases the problem is a missing owl:imports statement to the classes file which defines the correct type of " + type.getName());
						}
					}
					ccx.printStackTrace();
					throw new RuntimeException("Failed to convert type of " + instance);
				}
				catch (Exception ex) {
					ex.printStackTrace();
					throw new RuntimeException("Failed to convert type of " + instance);
				}

				kb.flushCache();

				//long endTime = System.currentTimeMillis();
				//log("Completed after " + (endTime - startTime) + " ms for " + count + " frames.");
//	        }
//        };
//	    try {
//		    task.setProgressIndeterminate(false);
//		    ((OWLModel) kb).getTaskManager().run(task);
//	    }
//	    catch(Exception e) {
//		    e.printStackTrace();
//	    }
    }


    public Instance createNewFrame(Instance instance) {
        if (isCls(instance)) {
            return instance.getKnowledgeBase().getFrameFactory().createCls(instance.getFrameID(), instance.getDirectTypes());
        }
        else if (isSlot(instance)) {
            return instance.getKnowledgeBase().getFrameFactory().createSlot(instance.getFrameID(), instance.getDirectTypes());
        }
        else if (!(instance instanceof Facet)) {
            return instance.getKnowledgeBase().getFrameFactory().createSimpleInstance(instance.getFrameID(), instance.getDirectTypes());
        }
        return instance;
    }


    private void findMetaclasses(KnowledgeBase kb) {
        findSubclasses(metaclasses, kb.getCls(Model.Cls.CLASS));
        findSubclasses(metaslots, kb.getCls(Model.Cls.SLOT));
    }


    private void findSubclasses(Set metaclasses, Frame frame) {
        metaclasses.add(frame);
        Slot directSubclassesSlot = frame.getKnowledgeBase().getSlot(Model.Slot.DIRECT_SUBCLASSES);
        Collection subclasses = mnfs.getValues(frame, directSubclassesSlot, null, false);
        for (Iterator it = subclasses.iterator(); it.hasNext();) {
            Frame subclass = (Frame) it.next();
            if (!metaclasses.contains(subclass)) {
                findSubclasses(metaclasses, subclass);
            }
        }
    }


    private boolean isCls(Instance instance) {
        Collection types = instance.getDirectTypes();
        for (Iterator it = types.iterator(); it.hasNext();) {
            Instance type = (Instance) it.next();
            if (type.equals(instance)) {
                if (metaclasses.contains(type)) {
                    return true;
                }
                else {
                    metaclasses.add(type);
                    updateFrame(type);
                }
            }
            if (!(type instanceof Cls)) {
                updateFrame(type);
            }
            if (metaclasses.contains(type)) {
                return true;
            }
        }
        return false;
    }


    private boolean isSlot(Instance instance) {
        Collection types = instance.getDirectTypes();
        for (Iterator it = types.iterator(); it.hasNext();) {
            Instance type = (Instance) it.next();
            if (metaslots.contains(type)) {
                return true;
            }
        }
        return false;
    }


    private void log(String message) {
        System.out.println("[OWLJavaFactoryUpdater] " + message);
    }


    public void updateFrame(Instance instance) {
        Instance newInstance = createNewFrame(instance);
        if (instance.getClass() != newInstance.getClass()) {
            mnfs.replaceFrame(newInstance);
        }
    }


    public static void run(Instance resource) {
        run(resource.getKnowledgeBase(), Collections.singleton(resource));
    }


    public static void run(KnowledgeBase kb, Collection instances) {
        new OWLJavaFactoryUpdater(kb, instances);
    }


    /**
     * Completely replaces all occurances of all frames to their correct Java type
     * according to the current FrameFactory.  This method tolerates frames that have
     * a completely wrong type, e.g. Slots can be converted into Clses etc.
     *
     * @param owlModel the OWLModel
     */
    public static void run(JenaOWLModel owlModel) {
        new OWLJavaFactoryUpdater(owlModel);
    }
}
