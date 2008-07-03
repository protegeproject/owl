package edu.stanford.smi.protegex.owl.javacode;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFIndividual;

public abstract class AbstractCodeGeneratorIndividual extends DefaultRDFIndividual {
    private static final long serialVersionUID = -406831192956223749L;
    
    private Map<String, Class> protege2ImplementationMap = new HashMap<String, Class>();

    public AbstractCodeGeneratorIndividual() {
        
    }
    
    public AbstractCodeGeneratorIndividual(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }
    
    @SuppressWarnings("unchecked")
    protected abstract Map<String, Class> getProtege2ImplementationMap();  
    
    // Can't java 5 here because the super class does not
    @SuppressWarnings("unchecked")
    @Override
    public RDFResource as(Class javaInterface) {
        return as(this, javaInterface);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean canAs(Class javaInterface) {
        return canAs(this, javaInterface);
    }

    @Override
    public Collection getPropertyValuesAs(RDFProperty property,
                                           Class javaInterface) {
        Collection results = new ArrayList();
        for (Iterator it = getPropertyValues(property).iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFIndividual) {
                RDFIndividual  resource = (RDFIndividual) o;
                if (canAs(resource, javaInterface)) {
                    results.add(as(resource, javaInterface));
                }
                else {
                    results.add(resource);
                }
            }
            else {
                results.add(o);
            }
        }
        return results;
    }
    
    @Override
    public Iterator listPropertyValuesAs(RDFProperty property, Class javaInterface) {
        return getPropertyValuesAs(property, javaInterface).iterator();
    }
    
    
    private boolean canAs(RDFResource resource, Class javaInterface) {
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return true;
        }
        return getJavaImplementation(resource, javaInterface) != null;
    }
    
    private RDFResource as(RDFResource resource, Class javaInterface) {
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return resource;
        }
        Class type = getJavaImplementation(resource, javaInterface);
        try {
            Constructor con = type.getConstructor(new Class[] { OWLModel.class, FrameID.class});
            return (RDFResource) con.newInstance(new Object[] { getOWLModel(), resource.getFrameID() });
        }
        catch (Throwable t) {
            ClassCastException classcast = new ClassCastException("Resource " + resource + " could not be cast to type " + javaInterface);
            classcast.initCause(t);
            throw classcast;
        }
    }
    
    private <X> Class<? extends X> getJavaImplementation(RDFResource resource, Class<? extends X> javaInterface) {
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
