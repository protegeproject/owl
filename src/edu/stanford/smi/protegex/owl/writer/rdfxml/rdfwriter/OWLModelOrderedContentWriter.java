package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.writer.rdfxml.renderer.RDFAxiomRenderer;
import edu.stanford.smi.protegex.owl.writer.rdfxml.renderer.RDFResourceRenderer;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 8, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLModelOrderedContentWriter implements RDFXMLContentWriter {


    private OWLModel model;

    private TripleStore tripleStore;

    private Comparator comparator;


    /**
     * Constructs an OWLModel RDF/XML rdfwriter to render the specified <code>OWLModel</code>.
     *
     * @param model       The model to be rendered.
     * @param tripleStore The triplestore to be rendered.
     */
    public OWLModelOrderedContentWriter(OWLModel model,
                                        TripleStore tripleStore,
                                        Comparator comparator) {
        this.model = model;
        this.tripleStore = tripleStore;
        this.comparator = comparator;
    }


    @SuppressWarnings({ "deprecation", "unchecked" })
	protected Collection getResources() {
        TreeSet resources = new TreeSet(comparator);
        // Resources that have this triplestore as their home triplestore
        for (Iterator it = tripleStore.listHomeResources(); it.hasNext();) {
            RDFResource curRes = (RDFResource) it.next();
            if (curRes.isSystem() == false &&
                    curRes.isAnonymous() == false) {
                resources.add(curRes);
            }
        }

        // Render the resources that there are statements about in this triple store
        for (Iterator it = model.getRDFProperties().iterator(); it.hasNext();) {
            RDFProperty curProp = (RDFProperty) it.next();
            for (Iterator subjIt = tripleStore.listSubjects(curProp); subjIt.hasNext();) {
                Object subj = subjIt.next();
                if (subj instanceof RDFResource) {
                    RDFResource curSubj = (RDFResource) subj;
                    if (curSubj.isAnonymous() == false &&
                            curSubj.isSystem() == false &&
                            resources.contains(curSubj) == false) {
                        resources.add(curSubj);
                    }
                }
            }
        }
        
        //remove unwanted non-system frames, such as :ONTOLOGY-POINTER
        try {
        	resources.removeAll(model.getSystemFrames().getOwlOntologyPointerClass().getInstances());        	
        } catch (Exception e) {
        	Log.getLogger().log(Level.WARNING, "Could not remove unwanted frames from OWL export", e);
        }       
        
        return resources;
    }


    public void writeContent(XMLWriter writer)
            throws IOException {
        HashSet renderedResources = new HashSet();
        // Render the appropriate ontology and its properties
        for (Iterator it = model.getOWLOntologies().iterator(); it.hasNext();) {
            OWLOntology ont = (OWLOntology) it.next();
            if (ont.getName().equals(tripleStore.getName())) {
                RDFResourceRenderer renderer = new RDFResourceRenderer(ont, tripleStore, writer);
                renderer.write();
                renderedResources.add(ont);
            }
        }

        // Render all different
        for (Iterator it = model.getOWLAllDifferents().iterator(); it.hasNext();) {
            OWLAllDifferent curAllDifferent = (OWLAllDifferent) it.next();
            if (model.getTripleStoreModel().getHomeTripleStore(curAllDifferent).equals(tripleStore)) {
                RDFResourceRenderer ren = new RDFResourceRenderer(curAllDifferent, tripleStore, writer);
                ren.write();
                renderedResources.add(curAllDifferent);
            }
        }

        // Render the resources that have this triplestore as their home
        // triplestore
        for (Iterator it = getResources().iterator(); it.hasNext();) {
            RDFResource curRes = (RDFResource) it.next();
            RDFAxiomRenderer axiomRenderer = new RDFAxiomRenderer(curRes, tripleStore, writer);
            axiomRenderer.write();
            renderedResources.add(curRes);
        }
    }
}

