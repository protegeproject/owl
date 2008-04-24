package edu.stanford.smi.protegex.owl.inference.util;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 20, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerUtil {

    private static ReasonerUtil instance;

    private HashMap namedClsesMap;

    private HashMap propertiesMap;

    private HashMap individualsMap;

    private ProjectListener projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            Project project = (Project) event.getSource();
            project.removeProjectListener(projectListener);
            ((OWLModel) project.getKnowledgeBase()).removeModelListener(modelListener);
            initHashMaps();
        }
    };

    private ModelListener modelListener = new ModelAdapter() {
        public void classCreated(RDFSClass cls) {
            invalidateNamedClsCache(cls.getOWLModel());
        }


        public void classDeleted(RDFSClass cls) {
            invalidateNamedClsCache(cls.getOWLModel());
        }


        public void propertyCreated(RDFProperty property) {
            invalidatePropertiesCache(property.getOWLModel());
        }


        public void propertyDeleted(RDFProperty property) {
            invalidatePropertiesCache(property.getOWLModel());
        }


        public void individualCreated(RDFResource resource) {
            invalidateIndividualsCache(resource.getOWLModel());
        }


        public void individualDeleted(RDFResource resource) {
            invalidateIndividualsCache(resource.getOWLModel());
        }
    };

    private long lastClassificationTime;


    protected ReasonerUtil() {
        initHashMaps();
        lastClassificationTime = 0;
    }


    public static synchronized ReasonerUtil getInstance() {
        if (instance == null) {
            instance = new ReasonerUtil();
        }

        return instance;
    }


    private void initHashMaps() {
        namedClsesMap = new HashMap();
        propertiesMap = new HashMap();
        individualsMap = new HashMap();

    }


    /**
     * Returns a collection of named classes.  This collection
     * is cached.  If named classes are added or deleted, the
     * cache is emptied an rebuilt.
     *
     * @param kb The knowledge base that contains the named classes
     */
    public Collection getNamedClses(OWLModel kb) {
        // If we haven't got a cache for the knowledge base
        // then create one.
        if (namedClsesMap.containsKey(kb) == false) {
            namedClsesMap.put(kb, kb.getUserDefinedOWLNamedClasses());

            kb.addModelListener(modelListener);
            kb.getProject().addProjectListener(projectListener);
        }


        Collection namedClses = (Collection) namedClsesMap.get(kb);

        if (namedClses == null) {
            namedClses = kb.getUserDefinedOWLNamedClasses();

            namedClsesMap.put(kb, namedClses);
        }

        return namedClses;
    }


    public Collection getProperties(OWLModel kb) {
        // If we haven't got a cache for the knowledge base
        // then create one.
        if (propertiesMap.containsKey(kb) == false) {
            propertiesMap.put(kb, kb.getUserDefinedOWLProperties());

            kb.addModelListener(modelListener);
        }


        Collection properties = (Collection) propertiesMap.get(kb);

        if (properties == null) {
            properties = kb.getUserDefinedOWLProperties();

            propertiesMap.put(kb, properties);
        }

        return properties;
    }


    public Collection getIndividuals(OWLModel kb) {
        if (individualsMap.containsKey(kb) == false) {
            individualsMap.put(kb, getOWLIndividuals(kb));
            kb.addModelListener(modelListener);
        }

        Collection individuals = (Collection) individualsMap.get(kb);

        if (individuals == null) {
            individuals = getOWLIndividuals(kb);
            individualsMap.put(kb, individuals);
        }

        return individuals;
    }


    private Collection getOWLIndividuals(OWLModel model) {
        IndividualsFilter filter = new IndividualsFilter(model);
        return filter.getOWLIndividuals();
    }


    private class IndividualsFilter extends OWLModelVisitorAdapter {

        private Collection individuals;

        private OWLModel model;


        public IndividualsFilter(OWLModel model) {
            this.model = model;
        }


        public Collection getOWLIndividuals() {
            individuals = new ArrayList();
            for (Iterator it = model.getRDFResources().iterator(); it.hasNext();) {
                RDFResource curRes = (RDFResource) it.next();
                curRes.accept(this);
            }
            return individuals;
        }


        public void visitOWLIndividual(OWLIndividual owlIndividual) {
            individuals.add(owlIndividual);
        }
    }


    public void invalidateNamedClsCache(OWLModel kb) {
        namedClsesMap.put(kb, null);
    }


    public void invalidatePropertiesCache(OWLModel kb) {
        propertiesMap.put(kb, null);
    }


    public void invalidateIndividualsCache(OWLModel kb) {
        individualsMap.put(kb, null);
    }


    public long getLastClassificationTime() {
        return lastClassificationTime;
    }


    public void setLastClassificationTime() {
        lastClassificationTime = System.currentTimeMillis();
    }


    public OWLNamedClass getNamedReferent(OWLAnonymousClass cls) {
        OWLAnonymousClass expRoot, newExpRoot;

        expRoot = cls.getExpressionRoot();

        while (true) {
            newExpRoot = expRoot.getExpressionRoot();

            if (newExpRoot == expRoot) {
                break;
            }

            expRoot = newExpRoot;
        }

        // Check to see if the class has any named sub classes
        OWLNamedClass namedCls = null;

        Iterator subclsIt = cls.getNamedSubclasses().iterator();

        if (subclsIt.hasNext()) {
            namedCls = (OWLNamedClass) subclsIt.next();
        }

        return namedCls;
    }
}

