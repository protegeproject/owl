package edu.stanford.smi.protegex.owl.inference.protegeowl;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.HashMap;
import java.util.Map;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 22, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerManager {

    public Map reasonerMap;

    private static ReasonerManager instance;

    private ProjectListener projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            Project project = (Project) event.getSource();
            project.removeProjectListener(this);
            reasonerMap.remove(project.getKnowledgeBase());
        }
    };


    private ReasonerManager() {
        reasonerMap = new HashMap();
    }


    public synchronized static ReasonerManager getInstance() {
        if (instance == null) {
            instance = new ReasonerManager();
        }

        return instance;
    }


    /**
     * Gets the reasoner for the specified knowledge base
     *
     * @param kb The <code>OWLModel</code>.
     * @return A reasoner to be used for reasoning over
     *         the specified knowledge base.
     */
    public ProtegeOWLReasoner getReasoner(OWLModel kb) {
        ProtegeOWLReasoner reasoner = (ProtegeOWLReasoner) reasonerMap.get(kb);

        if (reasoner == null) {
            reasoner = new DefaultProtegeOWLReasoner(kb);

            reasonerMap.put(kb, reasoner);
            kb.getProject().addProjectListener(projectListener);
        }

        return reasoner;
    }


    /**
     * @deprecated The <code>createReasoner</code> method should be used instead.
     */
    public ProtegeOWLReasoner getReasoner(OWLModel kb, boolean createNew) {
        return new DefaultProtegeOWLReasoner(kb);
    }


    public ProtegeOWLReasoner createReasoner(OWLModel model) {
        return new DefaultProtegeOWLReasoner(model);
    }
}

