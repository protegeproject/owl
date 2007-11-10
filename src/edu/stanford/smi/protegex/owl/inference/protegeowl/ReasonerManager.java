package edu.stanford.smi.protegex.owl.inference.protegeowl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.DefaultProtegeDIGReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.util.DefaultUncaughtExceptionHandler;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 22, 2004<br><br>
 * 
 * @author Matthwe Horridge <matthew.horridge@cs.man.ac.uk> 
 * @author Tania Tudorache <tudorache@stanford.edu>
 */
public class ReasonerManager implements Disposable {

	public static final String CURRENT_REASONER_PROPERTY = "current.reasoner.class.name";
	
    private Map<OWLModel, ProtegeReasoner> owlModel2reasonerMap;    

    private static ReasonerManager instance;
    
    static {
    	//move this to a more central place?
    	initializeUncaughtExceptionHandler();
    }
    

    private ProjectListener projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            Project project = (Project) event.getSource();            
            disposeReasoner((OWLModel) project.getKnowledgeBase());            
        }
    };


    private ReasonerManager() {
        owlModel2reasonerMap = new HashMap<OWLModel, ProtegeReasoner>();
    }


    private static void initializeUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
		
	}


	public synchronized static ReasonerManager getInstance() {
        if (instance == null) {
            instance = new ReasonerManager();
        }

        return instance;
    }


    /**
     * Gets the DIG reasoner for the specified knowledge base
     *
     * @param owlModel The {@link OWLModel}
     * @return A reasoner to be used for reasoning over
     *         the specified knowledge base.
     * @deprecated         
     */
    public ProtegeOWLReasoner getReasoner(OWLModel owlModel) {
    	ProtegeReasoner reasoner = owlModel2reasonerMap.get(owlModel);
    	
    	if (reasoner == null) {
    		return (ProtegeOWLReasoner) getProtegeReasoner(owlModel, getDefaultDIGReasonerClass());
    	}
    	
    	if (reasoner instanceof ProtegeOWLReasoner) {
    		return (ProtegeOWLReasoner) reasoner;
    	}
    	
    	return (ProtegeOWLReasoner) replaceReasoner(owlModel, getDefaultDIGReasonerClass());
    }

    
    public ProtegeReasoner getProtegeReasoner(OWLModel owlModel) {
    	return getProtegeReasoner(owlModel, getDefaultReasonerClass());
    }
    
        
    private ProtegeReasoner getProtegeReasoner(OWLModel owlModel, Class reasonerJavaClass ) {
    	if (reasonerJavaClass == null) {
    		return null; 
    	}
    	
    	ProtegeReasoner reasoner = owlModel2reasonerMap.get(owlModel);

    	if (reasoner == null) {
    		reasoner = internalCreateReasoner(owlModel, reasonerJavaClass);

    		owlModel2reasonerMap.put(owlModel, reasoner);
    		owlModel.getProject().addProjectListener(projectListener);
    	}

    	return reasoner;
    }
    
    
    private ProtegeReasoner internalCreateReasoner(OWLModel owlModel, Class reasonerJavaClass) {
    	if (reasonerJavaClass == null) {    	
    		return null;
    	}
    	
    	return ReasonerPluginManager.getReasonerPluginInstance(owlModel, reasonerJavaClass);
    }

    
    /**
     * @deprecated Use <code>createProtegeReasoner(owlModel, DefaultProtegeOWLReasoner.class)</code>
     */
    public ProtegeOWLReasoner getReasoner(OWLModel owlModel, boolean createNew) {
        return createReasoner(owlModel);
    }


    /**
     * @deprecated Use <code>createProtegeReasoner(owlModel, DefaultProtegeOWLReasoner.class)</code>
     */
    public ProtegeOWLReasoner createReasoner(OWLModel owlModel) {
        return (ProtegeOWLReasoner) createProtegeReasoner(owlModel, getDefaultDIGReasonerClass());
    }
   
     
    public ProtegeReasoner createProtegeReasoner(OWLModel owlModel) {
		return createProtegeReasoner(owlModel, getDefaultReasonerClass());
    }
    
    public ProtegeReasoner createProtegeReasoner(OWLModel owlModel, Class reasonerJavaClass) {
    	return getProtegeReasoner(owlModel, reasonerJavaClass); 
    }
	
	
	public String getDefaultReasonerClassName() {
		String className = ApplicationProperties.getString(CURRENT_REASONER_PROPERTY, getDefaultDIGReasonerClass().getName());
		
		if (className.equals(ReasonerPluginMenuManager.NONE_REASONER)) {
			return ReasonerPluginMenuManager.NONE_REASONER;
		}
		
		//make sure that this class exists, otherwise use the DIG resoner as the default;
		Class reasonerClass = ReasonerPluginManager.getReasonerJavaClass(className);
		if (reasonerClass == null) {
			className = getDefaultDIGReasonerClass().getName();
		}
		
		return className;
	}
	
	public Class getDefaultReasonerClass() {
		return ReasonerPluginManager.getReasonerJavaClass(getDefaultReasonerClassName());
	}
	
	public void setDefaultReasonerClass(String javaClassName) {
		if (javaClassName == null) {
			javaClassName = ReasonerPluginMenuManager.NONE_REASONER;
		}
		ApplicationProperties.setString(ReasonerManager.CURRENT_REASONER_PROPERTY, javaClassName);				
	}
	
    
	
	private ProtegeReasoner replaceReasoner(OWLModel owlModel, Class reasonerJavaClass) {		
		disposeReasoner(owlModel);
		
		return createProtegeReasoner(owlModel, reasonerJavaClass);				
	}


	public void disposeReasoner(OWLModel owlModel) {
		ProtegeReasoner existingReasoner = owlModel2reasonerMap.get(owlModel);
		
		try {
			if (existingReasoner != null) {
				existingReasoner.dispose();
				
				owlModel.getProject().removeProjectListener(projectListener);
				owlModel2reasonerMap.remove(owlModel);
			}			
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at disposing reasoner " + existingReasoner, e);
		}
	}
	
	public Class getDefaultDIGReasonerClass() {
		return DefaultProtegeDIGReasoner.class;
	}
	
	
	public ProtegeReasoner setProtegeReasonerClass(OWLModel owlModel, Class reasonerJavaClass) {
		ProtegeReasoner reasoner = replaceReasoner(owlModel, reasonerJavaClass);
		
		return reasoner;
	}

	
	public ProtegeReasoner setProtegeReasonerClass(OWLModel owlModel, String reasonerJavaClassName) {
		Class reasonerJavaClass = null;
		
		if (reasonerJavaClassName != null && !reasonerJavaClassName.equals(ReasonerPluginMenuManager.NONE_REASONER)) {
			reasonerJavaClass = ReasonerPluginManager.getReasonerJavaClass(reasonerJavaClassName);
		}
		
		return setProtegeReasonerClass(owlModel, reasonerJavaClass);
	}

	
	public boolean hasAttachedReasoner(OWLModel owlModel) {
		return owlModel2reasonerMap.get(owlModel) != null;
	}
	
	
	/* Releases all the owl model <-> reasonser connections and 
	 * disposes all reasoners.
	 *
	 * @see edu.stanford.smi.protege.util.Disposable#dispose()
	 */
	public void dispose() {
		for (OWLModel owlModel : owlModel2reasonerMap.keySet()) {			
			disposeReasoner(owlModel);
		}
		owlModel2reasonerMap.clear();
		
		ReasonerUtil.getInstance().dispose();
	}
	
}


