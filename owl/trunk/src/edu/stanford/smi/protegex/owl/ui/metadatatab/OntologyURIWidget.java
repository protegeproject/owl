package edu.stanford.smi.protegex.owl.ui.metadatatab;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 27, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyURIWidget extends AbstractPropertyWidget {

    private JTextField nameField;


    public static boolean isSuitable(Cls cls, edu.stanford.smi.protege.model.Slot slot, Facet facet) {
        return cls.getName().equals(OWLNames.Cls.ONTOLOGY);
    }


    public void initialize() {
        setLayout(new BorderLayout());
        nameField = new JTextField();
        LabeledComponent lc = new LabeledComponent("Ontology URI", nameField);
        add(lc, BorderLayout.NORTH);
        nameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setDefaultNamespace();
                }
            }
        });
        nameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                setDefaultNamespace();
            }
        });
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validateName();
            }


            public void removeUpdate(DocumentEvent e) {
                validateName();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        });
        nameField.setToolTipText(TOOL_TIP_TEXT);
        setToolTipText(TOOL_TIP_TEXT);
        updateOntologyName();
    }


    public void setInstance(Instance instance) {
        super.setInstance(instance);
        nameField.setEditable(isTopTripleStoreActive());
        updateOntologyName();

    }

    private boolean isTopTripleStoreActive() {
        TripleStoreModel tsm =getOWLModel().getTripleStoreModel();
        return tsm.getActiveTripleStore().equals(tsm.getTopTripleStore());
    }


    private boolean validateName() {
        if(nameField.getText().length() == 0) {
            return false;
        }
        try {
            new URI(nameField.getText());
            nameField.setForeground(Color.BLACK);
            return true;
        }
        catch (URISyntaxException e) {
            nameField.setForeground(Color.RED);
            return false;
        }
    }


    private void updateOntologyName() {
        //OWLOntology owlOntology = (OWLOntology) getInstance();
        // TODO: Shouldn't have to get the default ontology here
        OWLOntology ontology = getOWLModel().getDefaultOWLOntology();
        for(Iterator it = getOWLModel().getOWLOntologies().iterator(); it.hasNext(); ) {
            OWLOntology owlOntology = (OWLOntology) it.next();
            TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
            TripleStore activeTS = tsm.getActiveTripleStore();
            if(activeTS != null) {
                if(activeTS.contains(owlOntology, getOWLModel().getRDFTypeProperty(), getOWLModel().getOWLOntologyClass())) {
                    ontology = owlOntology;
                    break;
                }
            }
            else {
                throw new IllegalStateException("The active triple store is null!");
            }
        }

        if(ontology != null) {
            String uri = ontology.getURI();
            nameField.setText(uri);
        }
        //String uri = TripleStoreUtil.getFirstOntology(getOWLModel(), getOWLModel().getTripleStoreModel().getActiveTripleStore()).getURI();
        //nameField.setText(uri);
    }


    private void setDefaultNamespace() {
        if (validateName()) {
            if(isTopTripleStoreActive()) {
                String nameSpace = nameField.getText();
                if(nameSpace.endsWith("/") == false) {
                    nameSpace += "#";
                }
                getOWLModel().getNamespaceManager().setDefaultNamespace(nameSpace);
            }
            updateOntologyName();
        }
        else {
            updateOntologyName();
        }

    }


    private static final String TOOL_TIP_TEXT = "<html><body>This specifies the name of the ontology " +
            "(also known as the logical URI).<br>" +
            "The name is a URI that other ontologies will use to import<br>" +
            "this ontology.  It is recommended that it resembles a http URL that points<br>" +
            "to the location where the ontology can be downloaded from.</html></body>";
}

