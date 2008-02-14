package edu.stanford.smi.protegex.owl.jena.creator;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;



public class OwlProjectFromReaderCreator extends AbstractOwlProjectCreator {
    private Reader reader;
    private String xmlBase;

    public OwlProjectFromReaderCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public OwlProjectFromReaderCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Project create() {
        Collection errors = new ArrayList();
        Project project = Project.createNewProject(factory, errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        try {
            parser.run(reader, xmlBase);
        }
        catch (IOException e) {
            errors.add(e);
        }
        handleErrors(errors);
        return project;
    }
    
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setXmlBase(String xmlBase) {
        this.xmlBase = xmlBase;
    }

}
