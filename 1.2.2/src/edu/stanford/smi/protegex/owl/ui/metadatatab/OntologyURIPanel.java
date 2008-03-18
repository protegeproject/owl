package edu.stanford.smi.protegex.owl.ui.metadatatab;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyURIPanel extends JPanel {

    public static final String URI_BASE_PROPERTY = "OntURIBase";

    public static final String URI_BASE_APPEND_YEAR_PROPERTY = "OntURIBaseAppendYear";

    public static final String URI_BASE_APPEND_MONTH_PROPERTY = "OntURIBaseAppendMonth";

    public static final String URI_BASE_APPEND_DAY_PROPERTY = "OntURIBaseAppendDay";

    public static final String DEFAULT_BASE = "http://www.owl-ontologies.com";

    private JTextField textField;

    private ArrayList changeListeners;


    public OntologyURIPanel(boolean showHelp, boolean showDefaultSettingsButton) {
        changeListeners = new ArrayList();
        createUI(showHelp, showDefaultSettingsButton);
    }


    public void addChangeListener(ChangeListener lsnr) {
        changeListeners.add(lsnr);
    }


    public void removeChangeListener(ChangeListener lsnr) {
        changeListeners.remove(lsnr);
    }


    private void setDefautOntologyURIBase() {
        String defaultBase = ApplicationProperties.getString(URI_BASE_PROPERTY);
        if (defaultBase == null) {
            defaultBase = DEFAULT_BASE;
            ApplicationProperties.setString(URI_BASE_PROPERTY, DEFAULT_BASE);
        }
        if (defaultBase != null) {
            defaultBase = getOntologyURIBase(defaultBase,
                    ApplicationProperties.getBooleanProperty(URI_BASE_APPEND_YEAR_PROPERTY, false),
                    ApplicationProperties.getBooleanProperty(URI_BASE_APPEND_MONTH_PROPERTY, false),
                    ApplicationProperties.getBooleanProperty(URI_BASE_APPEND_DAY_PROPERTY, false));
            String fileName = "Ontology";
            fileName += System.currentTimeMillis() / 1000;
            fileName += ".owl";
            textField.setText(defaultBase + fileName);
        }
    }


    private void fireChangeEvent() {
        for (Iterator it = new ArrayList(changeListeners).iterator(); it.hasNext();) {
            ChangeListener lsnr = (ChangeListener) it.next();
            lsnr.stateChanged(new ChangeEvent(this));
        }
    }


    public static String getOntologyURIBase(String defaultBase,
                                            boolean appendYear,
                                            boolean appendMonth,
                                            boolean appendDay) {
        if (defaultBase != null && defaultBase.trim().length() > 0) {
            if (defaultBase.endsWith("/") == false) {
                defaultBase += "/";
            }
            if (appendYear) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                defaultBase += year + "/";
                if (appendMonth) {
                    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    defaultBase += month + "/";
                    if (appendDay) {
                        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        defaultBase += day + "/";
                    }
                }
            }
        }
        return defaultBase;
    }


    public URI getOntologyURI() {
        if (validateName()) {
            try {
                return new URI(textField.getText());
            }
            catch (URISyntaxException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }


    private void createUI(boolean showHelp, boolean showDefaultSettingsButton) {
        setLayout(new BorderLayout(7, 7));
        textField = new JTextField();
        setDefautOntologyURIBase();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validateName();
                fireChangeEvent();
            }


            public void removeUpdate(DocumentEvent e) {
                validateName();
                fireChangeEvent();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        });
        JPanel holder = new JPanel(new BorderLayout(3, 3));
        holder.add(textField, BorderLayout.NORTH);
        if (showDefaultSettingsButton) {
            JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonHolder.add(new JButton(new AbstractAction("Default settings...") {
                public void actionPerformed(ActionEvent e) {
                    OntologyURIDefaultSettingsPanel.showDialog(OntologyURIPanel.this);
                    setDefautOntologyURIBase();
                }
            }));
            holder.add(buttonHolder, BorderLayout.SOUTH);
        }
        LabeledComponent lc = new LabeledComponent("Ontology URI (Usually a URL starting with http://)", holder);
        add(lc, BorderLayout.NORTH);
        if (showHelp) {
            add(OWLUI.createHelpPanel(HELP_TEXT, null), BorderLayout.SOUTH);
        }
        validateName();
    }


    private boolean validateName() {
        try {
            URI uri = new URI(textField.getText().trim());
            // Should be absolute
            if (uri.isAbsolute()) {
                textField.setForeground(Color.BLACK);
                return true;
            }
            else {
                textField.setForeground(Color.RED);
                return false;
            }
        }
        catch (URISyntaxException e) {
            textField.setForeground(Color.RED);
            return false;
        }
    }


    private static final String HELP_TEXT = "<p>Please specify a URI for this ontology." +
            "</p>This URI will be used by " +
            "other ontologies that wish to import this ontology." +
            "<p>In general, it is recommended " +
            "that a URI which corresponds to the location of the " +
            "ontology on the web should be used.  The URI should " +
            "therefore resemble a HTTP URL, for example " +
            "http://www.mydomain.com/myontology</p>";

}

