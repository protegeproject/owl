package edu.stanford.smi.protegex.owl.ui.forms;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

/**
 * An object capable of loading forms files into a Protege Project.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AbsoluteFormsLoader {

    private OWLModel owlModel;

    private Project project;


    public AbsoluteFormsLoader(OWLModel owlModel) {
        this.owlModel = owlModel;
        this.project = owlModel.getProject();
    }


    public void load(URL fileURL) throws IOException {
        InputStream is = fileURL.openStream();
        load(is);
    }


    public void load(InputStream is) throws IOException {
        OntModel model = ModelFactory.createOntologyModel();
        model.read(is, "http://dummy.de/aldi.owl", FileUtils.langXMLAbbrev);
        is.close();

        model.add(FormsNames.FormWidget, RDF.type, OWL.Class);
        OntClass formWidgetClass = model.getOntClass(FormsNames.FormWidget.getURI());

        Iterator formWidgets = formWidgetClass.listInstances();
        while (formWidgets.hasNext()) {
            Resource formWidget = (Resource) formWidgets.next();
            addFormWidget(formWidget);
        }
        project.clearCachedWidgets();
    }


    public void loadAll() throws Exception {

        File systemFormsFile = new File(ProtegeOWL.getPluginFolder(), AbsoluteFormsGenerator.FILE_NAME);
        if (systemFormsFile.exists()) {
            System.out.println("Loading system forms from " + systemFormsFile);
            load(new FileInputStream(systemFormsFile));
        }

        Iterator it = owlModel.getTripleStoreModel().listUserTripleStores();
        while (it.hasNext()) {
            TripleStore ts = (TripleStore) it.next();
            URI ontologyFileURI = new URI(ts.getName());
            Repository rep = owlModel.getRepositoryManager().getRepository(ontologyFileURI);
            String formsFileName = AbsoluteFormsGenerator.getFormsFileURI(ts, owlModel);
            URI formsFileURI = new URI(formsFileName);
            if (rep != null) {
                if (rep.contains(formsFileURI)) {
                    System.out.println("Adding forms from " + formsFileURI);
                    load(rep.getInputStream(formsFileURI));
                }
            }
            else {
                try {
                    File file = null;
                    if (ts == owlModel.getTripleStoreModel().getTopTripleStore()) {
                        formsFileName = ((JenaOWLModel) owlModel).getOWLFilePath() + AbsoluteFormsGenerator.SUFFIX;
                        file = new File(formsFileName);
                    }
                    else {
                        file = new File(formsFileURI);
                    }
                    if (file.exists()) {
                        System.out.println("Adding forms from " + file);
                        load(new FileInputStream(file));
                    }
                }
                catch (Exception ex) {
                    // Ignore
                }
            }
        }
    }


    private void addFormWidget(Resource formWidget) {
        Statement forClassStmt = formWidget.getProperty(FormsNames.forClass);
        if (forClassStmt != null) {
            String forClassURI = forClassStmt.getResource().getURI();
            String forClassName = owlModel.getResourceNameForURI(forClassURI);
            if (forClassName != null) {
                Cls cls = project.getKnowledgeBase().getCls(forClassName);
                if (cls != null) {
                    ClsWidget clsWidget = project.getDesignTimeClsWidget(cls);
                    WidgetDescriptor wd = clsWidget.getDescriptor();
                    // wd.setName(forClassName);
                    wd.setDirectlyCustomizedByUser(true);
                    PropertyList propertyList = wd.getPropertyList();
                    StmtIterator widgets = formWidget.listProperties(FormsNames.widgets);
                    while (widgets.hasNext()) {
                        Statement s = widgets.nextStatement();
                        if (s.getResource() != null) {
                            Resource widgetResource = s.getResource();
                            addFormWidget(clsWidget, widgetResource);
                        }
                    }
                }
            }
        }
    }


    private void addFormWidget(ClsWidget clsWidget, Resource widgetResource) {
        Statement forPropertyStmt = widgetResource.getProperty(FormsNames.forProperty);
        String slotName = null;
        if (forPropertyStmt != null) {
            String forPropertyURI = forPropertyStmt.getResource().getURI();
            String forPropertyName = owlModel.getResourceNameForURI(forPropertyURI);
            if (forPropertyName != null && owlModel.getRDFResource(forPropertyName) instanceof RDFProperty) {
                slotName = forPropertyName;
            }
        }
        else {
            Statement commentStmt = widgetResource.getProperty(RDFS.comment);
            if(commentStmt != null) {
                slotName = commentStmt.getString();
            }
            else {
                slotName = ProtegeNames.Slot.INFERRED_TYPE;
            }
        }
        if (slotName != null) {
            Slot slot = project.getKnowledgeBase().getSlot(slotName);
            String widgetClassName = getWidgetClassName(widgetResource);
            if (widgetClassName != null) {
                clsWidget.replaceWidget(slot, widgetClassName);
                SlotWidget slotWidget = clsWidget.getSlotWidget(slot);
                setWidgetBounds(slotWidget.getDescriptor(), widgetResource);
            }
        }
    }


    private int getInt(Resource widgetResource, Property property) {
        Statement s = widgetResource.getProperty(property);
        if (s != null) {
            return s.getInt();
        }
        else {
            return 0;
        }
    }


    private String getWidgetClassName(Resource widgetResource) {
        Statement typeStatement = widgetResource.getProperty(RDF.type);
        if (typeStatement != null) {
            Resource type = typeStatement.getResource();
            Statement classNameStmt = type.getProperty(ProtegeFormsNames.javaClassName);
            if (classNameStmt != null) {
                return classNameStmt.getString();
            }
        }
        return null;
    }


    private void setWidgetBounds(WidgetDescriptor wd, Resource widgetResource) {
        Iterator layoutData = widgetResource.listProperties(FormsNames.layoutData);
        while (layoutData.hasNext()) {
            Statement layoutStatement = (Statement) layoutData.next();
            if (layoutStatement.getResource() != null) {
                Resource layoutResource = layoutStatement.getResource();
                if (layoutResource.hasProperty(RDF.type, AbsoluteLayoutNames.AbsoluteLayoutData)) {
                    int x = getInt(layoutResource, AbsoluteLayoutNames.x);
                    int y = getInt(layoutResource, AbsoluteLayoutNames.y);
                    int width = getInt(layoutResource, AbsoluteLayoutNames.width);
                    int height = getInt(layoutResource, AbsoluteLayoutNames.height);
                    wd.setBounds(new Rectangle(x, y, width, height));
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(new FileInputStream("travel.owl"));
        AbsoluteFormsLoader loader = new AbsoluteFormsLoader(owlModel);
        loader.load(new File("travel.owl.forms").toURL());
    }


    /**
     * Don't use this method - just for testing right now
     */
    public static boolean useNewFormMechanism_DontUseThisMethod() {
        return ApplicationProperties.getBooleanProperty("NewOWLFormsMechanism", false);
    }
}
