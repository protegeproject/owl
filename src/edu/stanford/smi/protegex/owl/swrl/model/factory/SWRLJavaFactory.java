package edu.stanford.smi.protegex.owl.swrl.model.factory;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class SWRLJavaFactory extends OWLJavaFactory {

    private OWLModel owlModel;

    /**
     * A Hashtable from Protege metaclass names to Java class names from this package
     */
    private static Hashtable classNames = new Hashtable();


    static {

        classNames.put(SWRLNames.Cls.ATOM, "SWRLAtom");
        classNames.put(SWRLNames.Cls.BUILTIN_ATOM, "SWRLBuiltinAtom");
        classNames.put(SWRLNames.Cls.CLASS_ATOM, "SWRLClassAtom");
        classNames.put(SWRLNames.Cls.DATA_RANGE_ATOM, "SWRLDataRangeAtom");
        classNames.put(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM, "SWRLDataValuedPropertyAtom");
        classNames.put(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM, "SWRLDifferentIndividualsAtom");
        classNames.put(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM, "SWRLIndividualPropertyAtom");
        classNames.put(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM, "SWRLSameIndividualAtom");
        classNames.put(SWRLNames.Cls.BUILTIN, "SWRLBuiltin");
        classNames.put(SWRLNames.Cls.IMP, "SWRLImp");
        classNames.put(SWRLNames.Cls.VARIABLE, "SWRLVariable");
        classNames.put(SWRLNames.Cls.ATOM_LIST, "SWRLAtomList");
    }


    /**
     * A Hashtable from Protege classes to Java classes from this package
     */
    private HashMap classMap = new HashMap();

    private final static String CLASSNAME_PREFIX = "edu.stanford.smi.protegex.owl.swrl.model.impl.Default";


    public SWRLJavaFactory(OWLModel owlModel) {
        super((AbstractOWLModel) owlModel);
        this.owlModel = owlModel;

        classMap.put(SWRLNames.Cls.ATOM, DefaultSWRLAtom.class);
        classMap.put(SWRLNames.Cls.BUILTIN_ATOM, DefaultSWRLBuiltinAtom.class);
        classMap.put(SWRLNames.Cls.CLASS_ATOM, DefaultSWRLClassAtom.class);
        classMap.put(SWRLNames.Cls.DATA_RANGE_ATOM, DefaultSWRLDataRangeAtom.class);
        classMap.put(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM, DefaultSWRLDatavaluedPropertyAtom.class);
        classMap.put(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM, DefaultSWRLDifferentIndividualsAtom.class);
        classMap.put(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM, DefaultSWRLIndividualPropertyAtom.class);
        classMap.put(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM, DefaultSWRLSameIndividualAtom.class);
        classMap.put(SWRLNames.Cls.BUILTIN, DefaultSWRLBuiltin.class);
        classMap.put(SWRLNames.Cls.IMP, DefaultSWRLImp.class);
        classMap.put(SWRLNames.Cls.VARIABLE, DefaultSWRLVariable.class);
        classMap.put(SWRLNames.Cls.ATOM_LIST, DefaultSWRLAtomList.class);

    } // SWRLJavaFactory


    public SimpleInstance createSimpleInstance(FrameID id, Collection directTypes) {
        Constructor constructor;
        Object[] args = {owlModel, id};
        Class c;

        if (directTypes.size() == 1) {
            Cls typeCls = (Cls) directTypes.iterator().next();
            String typeClsName = typeCls.getName();
            c = (Class) classMap.get(typeClsName);
            if (c == null) {
                Iterator supers = typeCls.getSuperclasses().iterator();
                while (c == null && supers.hasNext()) {
                    Frame frame = (Frame) supers.next();
                    c = (Class) classMap.get(frame.getName());
                }
            }
            if (c != null) {

                try {
                    constructor = c.getConstructor(new Class[]{
                            KnowledgeBase.class,
                            FrameID.class
                    });
                    return (SimpleInstance) constructor.newInstance(args);
                }
                catch (Exception e) {
                    System.err.println("Fatal Error: Could not create SimpleInstance from OWL metaclass " + c.getName());
                    e.printStackTrace();
                    return new DefaultSimpleInstance(owlModel, id);
                } // try

            } // if
        } // if

        return super.createSimpleInstance(id, directTypes);

    } // createSimpleInstance


    /**
     * Creates instances of the corresponding subclass of DefaultCls if one of the metaclasses of
     * the directTypes list is an SWRL metaclass.
     */

    public Cls createCls(FrameID id, Collection directTypes) {
        String className, javaClassName;
        Class[] parameterTypes = {KnowledgeBase.class, FrameID.class};
        Object[] args = {owlModel, id};
        Constructor constructor;
        Cls cls;
        Class c;

        if (directTypes.size() == 1) {
            cls = (Cls) classMap.get(directTypes.iterator().next());
            if (cls != null) {
                className = cls.getName();
                javaClassName = (String) classNames.get(className);

                try {
                    if (javaClassName != null) {
                        c = Class.forName(CLASSNAME_PREFIX + javaClassName);
                        constructor = c.getConstructor(parameterTypes);
                        return (Cls) constructor.newInstance(args);
                    } // if
                }
                catch (Exception e) {
                    System.err.println("Fatal Error: Could not create Cls from OWL metaclass " + javaClassName);
                    e.printStackTrace();
                    return new DefaultOWLNamedClass(owlModel, id);
                } // try
            } // if
        } // if

        return super.createCls(id, directTypes);

    } // createCls


    private Cls createCls(String javaClassName, FrameID id) {
        try {
            Class clazz = Class.forName(CLASSNAME_PREFIX + javaClassName);
            Class[] parameterTypes = {KnowledgeBase.class, FrameID.class};
            Constructor constructor = clazz.getConstructor(parameterTypes);
            Object[] args = {owlModel, id};
            return (Cls) constructor.newInstance(args);
        }
        catch (Exception ex) {
            System.err.println("Fatal Error: Could not create Cls from OWL metaclass " + javaClassName);
            ex.printStackTrace();
            return new DefaultOWLNamedClass(owlModel, id);
        }
    }


    public boolean isCorrectJavaImplementationClass(FrameID id, Collection types, Class clas) {
        return false;
    }

} // SWRLJavaFactory
