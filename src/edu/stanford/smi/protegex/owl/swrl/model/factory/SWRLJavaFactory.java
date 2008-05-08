package edu.stanford.smi.protegex.owl.swrl.model.factory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.FrameTypeId2OWLJavaClass;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLVariable;

public class SWRLJavaFactory extends OWLJavaFactory {

    private OWLModel owlModel;

    /**
     * A Hashtable from Protege classes to Java classes from this package
     */
    private HashMap<String, Class<? extends SWRLIndividual>> classMap = new HashMap<String, Class<? extends SWRLIndividual>>();

    public SWRLJavaFactory(OWLModel owlModel) {
        super((AbstractOWLModel) owlModel);
        this.owlModel = owlModel;

        classMap.put(SWRLNames.Cls.ATOM_LIST, DefaultSWRLAtomList.class);
        classMap.put(SWRLNames.Cls.BUILTIN_ATOM, DefaultSWRLBuiltinAtom.class);
        classMap.put(SWRLNames.Cls.BUILTIN, DefaultSWRLBuiltin.class);
        classMap.put(SWRLNames.Cls.CLASS_ATOM, DefaultSWRLClassAtom.class);
        classMap.put(SWRLNames.Cls.DATA_RANGE_ATOM, DefaultSWRLDataRangeAtom.class);
        classMap.put(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM, DefaultSWRLDatavaluedPropertyAtom.class);
        classMap.put(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM, DefaultSWRLDifferentIndividualsAtom.class);
        classMap.put(SWRLNames.Cls.IMP, DefaultSWRLImp.class);
        classMap.put(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM, DefaultSWRLIndividualPropertyAtom.class);
        classMap.put(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM, DefaultSWRLSameIndividualAtom.class);
        classMap.put(SWRLNames.Cls.VARIABLE, DefaultSWRLVariable.class);
    } // SWRLJavaFactory


    @Override
    public SimpleInstance createSimpleInstance(FrameID id, Collection directTypes) {
        Constructor<? extends SWRLIndividual> constructor;
        Object[] args = {owlModel, id};


        if (directTypes.size() == 1) {
            Cls typeCls = (Cls) directTypes.iterator().next();
            Class<? extends SWRLIndividual> c = getJavaTypeFromSWRLType(typeCls);
            if (c != null) {
                try {
                    constructor = c.getConstructor(new Class[]{
                            KnowledgeBase.class,
                            FrameID.class
                    });
                    return constructor.newInstance(args);
                }
                catch (Exception e) {
                    System.err.println("Fatal Error: Could not create SimpleInstance from OWL metaclass " + c.getName());
                    Log.getLogger().log(Level.SEVERE, "Exception caught", e);
                    return new DefaultSimpleInstance(owlModel, id);
                } // try

            } // if
        } // if

        return super.createSimpleInstance(id, directTypes);

    } // createSimpleInstance


    private Class<? extends SWRLIndividual> getJavaTypeFromSWRLType(Cls type) {
        String typeClsName = type.getName();
        Class<? extends SWRLIndividual> c = classMap.get(typeClsName);
        if (c == null) {
            Iterator supers = type.getSuperclasses().iterator();
            while (c == null && supers.hasNext()) {
                Frame frame = (Frame) supers.next();
                c = classMap.get(frame.getName());
            }
        }
        return c;
    }
    
    
    @Override
    public boolean isCorrectJavaImplementationClass(FrameID id, Collection types, Class clas) {
        for (Object o : types) {
            if (o instanceof Cls) {
                if (getJavaTypeFromSWRLType((Cls) o) != null) {
                    return false;
                }
            }
        }
        return super.isCorrectJavaImplementationClass(id, types, clas);
    }
    
    @Override
    public Frame createFrameFromClassId(int javaClassId, FrameID id) {
 
        if (javaClassId >= SWRLFrameTypeId.SWRL_FRAME_TYPE_ID_BASE &&
                javaClassId <= SWRLFrameTypeId.SWRL_FRAME_TYPE_ID_END) {
            int index = javaClassId - SWRLFrameTypeId.SWRL_FRAME_TYPE_ID_BASE;
            SWRLFrameTypeId typeId = SWRLFrameTypeId.values()[index];
            Frame frame = createInstance(id, typeId.getJavaClass());
            if (frame != null) {
                return frame;
            }
        }
        return super.createFrameFromClassId(javaClassId, id);
    }
    
    @Override
    public int getJavaClassId(Frame frame) {    
        for (SWRLFrameTypeId frameTypeId : SWRLFrameTypeId.values()) {
            try {
                Class<? extends Instance> javaType = frameTypeId.getJavaClass();
                if (javaType.isInstance(frame)) {
                    return frameTypeId.getFrameTypeId();
                }
            } catch (Exception e) {
                // this should never happen!
                Log.getLogger().log(Level.WARNING, "Error at getting the Java class Id for: " + frame , e);
            }
        }

        return super.getJavaClassId(frame);
    
    }

} // SWRLJavaFactory
