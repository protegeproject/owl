package edu.stanford.smi.protegex.owl.jena.creator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;

public class OwlProjectFromStreamCreator extends AbstractOwlProjectCreator {
    private InputStream stream;
    private String xmlBase;

    public OwlProjectFromStreamCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public OwlProjectFromStreamCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Project create(Collection errors) throws IOException {
        Project project = Project.createNewProject(factory, errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        try {
            parser.run(stream, xmlBase);
        }
        catch  (IOException e) {
            errors.add(e);
        }
        return project;
    }
    
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public void setXmlBase(String xmlBase) {
        this.xmlBase = xmlBase;
    }

}
