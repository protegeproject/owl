package edu.stanford.smi.protegex.owl.model.framestore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.event.FacetEvent;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.AbstractFrameStoreInvocationHandler;
import edu.stanford.smi.protege.model.framestore.ReferenceImpl;
import edu.stanford.smi.protege.model.query.Query;
import edu.stanford.smi.protege.model.query.QueryCallback;
import edu.stanford.smi.protege.model.query.QueryCallbackClone;
import edu.stanford.smi.protege.util.AbstractEvent;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;

/**
 * A FrameStoreInvocationHandler that uses a DefaultOWLFrameFactory to replace all
 * references to a given frame with new ones.  This is needed for the OWL database
 * backend to solve a bootstrapping recursion problem.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFrameFactoryInvocationHandler extends AbstractFrameStoreInvocationHandler {

    private OWLJavaFactory ff;

    private OWLNamedClass owlNamedClassClass;

    private static OWLFrameFactoryInvocationHandler recentInstance;

    private static Set<String> systemClses = new HashSet<String>();

    private Cls thingCls;

    private OWLJavaFactoryUpdater updater;


    static {
        systemClses.add(Model.Cls.THING);
        systemClses.add(Model.Cls.CLASS);
        systemClses.add(Model.Cls.ROOT_META_CLASS);
        systemClses.add(Model.Cls.STANDARD_CLASS);
        systemClses.add(Model.Cls.SYSTEM_CLASS);
    }


    public OWLFrameFactoryInvocationHandler() {
        recentInstance = this;
    }


    private Object convert(Object o) {
        if (o instanceof Collection) {
            return convertCollection((Collection) o);
        }
        else if (o instanceof Instance) {
            return convertInstance((Instance) o);
        }
        else if (o instanceof Reference) {
            return convertReference((Reference) o);
        }
        else if (o instanceof AbstractEvent) {
            return convertEvent((AbstractEvent) o);
        }
        return o;
    }


    public Collection convertCollection(Collection values) {
        if (values.size() == 0) {
            return values;
        }
        else {
            Collection result = null;
            if (values instanceof Set) {
                result = new HashSet(values.size());
            }
            else {
                result = new ArrayList(values.size());
            }
            for (final Iterator it = values.iterator(); it.hasNext();) {
                result.add(convert(it.next()));
            }
            return result;
        }
    }


    private AbstractEvent convertEvent(AbstractEvent event) {
        if (event instanceof ClsEvent) {
            return new ClsEvent((Cls) convert(event.getSource()),
                    event.getEventType(),
                    convert(event.getArgument1()),
                    convert(event.getArgument2()));
        }
        else if (event instanceof FacetEvent) {
            return new FacetEvent((Facet) convert(event.getSource()),
                    event.getEventType(),
                    (Frame) convert(((FacetEvent) event).getFrame()),
                    (Slot) convert(((FacetEvent) event).getSlot()));
        }
        else if (event instanceof FrameEvent) {
            return new FrameEvent((Frame) convert(event.getSource()),
                    event.getEventType(),
                    convert(event.getArgument1()),
                    convert(event.getArgument2()));
        }
        else if (event instanceof InstanceEvent) {
            return new InstanceEvent((Instance) convert(event.getSource()),
                    event.getEventType(),
                    convert(event.getArgument()));
        }
        else if (event instanceof KnowledgeBaseEvent) {
            final KnowledgeBaseEvent old = (KnowledgeBaseEvent) event;
            final Frame frame = (Frame) convert(old.getFrame());
            KnowledgeBase source = (KnowledgeBase) old.getSource();
            if (source == null) {
                source = frame.getKnowledgeBase();
            }
            return new KnowledgeBaseEvent(source,
                    old.getEventType(),
                    frame,
                    convert(old.getArgument2()),
                    convert(old.getArgument3()));
        }
        else if (event instanceof SlotEvent) {
            final SlotEvent old = (SlotEvent) event;
            return new SlotEvent((Slot) convert(old.getSlot()),
                    old.getEventType(),
                    (Frame) convert(old.getFrame()));
        }
        return event;
    }


    public Instance convertInstance(Instance instance) {
        if (ff == null) {
            final AbstractOWLModel owlModel = (AbstractOWLModel) instance.getKnowledgeBase();
            ff = new OWLJavaFactory(owlModel);
            owlNamedClassClass = new DefaultOWLNamedClass(owlModel, OWLNames.ClsID.NAMED_CLASS);
            thingCls = new DefaultOWLNamedClass(owlModel, Model.ClsID.THING);
        }
        if (instance instanceof Cls) {
            if (Model.ClsID.THING.equals(instance.getFrameID())) {
                return thingCls;
            }
            if ((instance.isSystem() &&
                    instance.getFrameID().getLocalPart() < 9000 &&
                    !Model.ClsID.DIRECTED_BINARY_RELATION.equals(instance.getFrameID())) || instance instanceof RDFSClass) { // && systemClses.contains(instance.getName())) {
                if (owlNamedClassClass.equals(instance)) {
                    return owlNamedClassClass;
                }
                return instance; //new DefaultCls(instance.getKnowledgeBase(), instance.getFrameID());
            }
            else if (isDefaultNamedCls(instance)) {
                return new DefaultOWLNamedClass(instance.getKnowledgeBase(), instance.getFrameID());
            }
            return ff.createCls(instance.getFrameID(), instance.getDirectTypes());
        }
        else if (instance instanceof Slot) {
            Collection directTypes = instance.getDirectTypes();
            Slot slot = ff.createSlot(instance.getFrameID(), directTypes);
            if (!(slot instanceof RDFProperty) && !slot.isSystem() && !directTypes.isEmpty()) {
                if (updater == null) {
                    updater = new OWLJavaFactoryUpdater(slot.getKnowledgeBase(), Collections.singleton(slot));
                }
                return updater.createNewFrame(slot);
            }
            else {
                return slot;
            }
        }
        else if (instance instanceof Facet) {
            return ff.createFacet(instance.getFrameID(), instance.getDirectTypes());
        }
        else {
            if (instance == null) {
                return null;
            }
            else {
                Collection directTypes = instance.getDirectTypes();
                if (directTypes.isEmpty() && instance instanceof RDFList) {
                    return instance;
                }
                else {
                    return ff.createSimpleInstance(instance.getFrameID(), directTypes);
                }
            }
        }
    }


    private Reference convertReference(Reference reference) {
        final Frame frame = convertInstance((Instance) reference.getFrame());
        final Slot slot = (Slot) convertInstance(reference.getSlot());
        final Facet facet = (Facet) convertInstance(reference.getFacet());
        return new ReferenceImpl(frame, slot, facet, reference.isTemplate());
    }


    protected Object handleInvoke(Method method, Object[] args) {
        final Object result = invoke(method, args);
        return convert(result);
    }
    
    protected void executeQuery(Query q, QueryCallback qc) {
      QueryCallback mycallback = new QueryCallbackClone(qc) {
        public void provideQueryResults(Set<Frame> results) {
          convert(results);
          super.provideQueryResults(results);
        }
      };
      getDelegate().executeQuery(q, mycallback);
    }


    private boolean isDefaultNamedCls(Instance instance) {
        final FrameID id = instance.getFrameID();
        return OWLNames.ClsID.NAMED_CLASS.equals(id) ||
                RDFNames.ClsID.PROPERTY.equals(id) ||
                RDFSNames.ClsID.NAMED_CLASS.equals(id);
    }


    /**
     * Sets the FrameFactory that shall be used for frame generation in the
     * most recently created OWLFrameFactoryInvocationHandler.
     *
     * @param ff the (subclass of) OWLJavaFactory
     */
    public final static void setFrameFactory(OWLJavaFactory ff) {
        recentInstance.ff = ff;
    }
}
