package edu.stanford.smi.protegex.owl.javacode;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public class ProtegeJavaMapping {
    
    private static Map<String, Class> protege2ImplementationMap = new HashMap<String, Class>();
    private static Map<Class, Class> interface2ImplementationMap = new HashMap<Class, Class>();

    public static void add(String protegeClassName, 
                           Class<? extends RDFResource> javaInterface, 
                           Class<? extends RDFResource> javaImplementation) {
        protege2ImplementationMap.put(protegeClassName, javaImplementation);
        interface2ImplementationMap.put(javaInterface, javaImplementation);
    }
    
    @SuppressWarnings("unchecked")
    public static <X> X create(OWLModel owlModel, Class<? extends X> javaInterface, String name) {
        Class<? extends X> implementation = (Class<? extends X>) interface2ImplementationMap.get(javaInterface);
        if (implementation == null) {
            return null;
        }
        if (name == null) {
            name = owlModel.getNextAnonymousResourceName();
        }
        return constructImplementation(owlModel, implementation, new FrameID(name));
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Class> getProtege2ImplementationMap() {
        return Collections.unmodifiableMap(protege2ImplementationMap);
    }
    
    public static boolean canAs(RDFResource resource, Class<? extends RDFResource> javaInterface) {
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return true;
        }
        return getJavaImplementation(resource, javaInterface) != null;
    }
    
    public static <X extends RDFResource> X as(RDFResource resource, Class<? extends X> javaInterface) {
        OWLModel owlModel = resource.getOWLModel();
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return javaInterface.cast(resource);
        }
        Class<? extends X> type = getJavaImplementation(resource, javaInterface);
        return constructImplementation(owlModel, type, resource.getFrameID());
    }
    
    private static <X> X constructImplementation(OWLModel owlModel, Class<? extends X> implType, FrameID id) {
        try {
            Constructor<? extends X> con = implType.getConstructor(new Class[] { OWLModel.class, FrameID.class});
            return con.newInstance(new Object[] { owlModel, id });
        }
        catch (Throwable t) {
            ClassCastException classcast = new ClassCastException("Resource " + id.getName() + " could not be cast to type " + implType);
            classcast.initCause(t);
            throw classcast;
        }
    }
    
    private static <X> Class<? extends X> getJavaImplementation(RDFResource resource, Class<? extends X> javaInterface) {
        for (Object o  : resource.getProtegeTypes()) {
            if  (!(o instanceof Cls)) {
                continue;
            }
            Cls type = (Cls) o;
            Class<?> javaImplementationClass = getProtege2ImplementationMap().get(type.getName());
            if (javaInterface.isAssignableFrom(javaImplementationClass)) {
                return javaImplementationClass.asSubclass(javaInterface);
            }
        }
        return null;
    }
    
}
