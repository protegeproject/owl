package edu.stanford.smi.protegex.owl.swrl.ui.code;

import edu.stanford.smi.protege.ui.InstanceDisplay;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * A panel which can be used to edit an OWL expression in a multi-line dialog.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTextAreaPanel extends JPanel implements ModalDialogFactory.CloseCallback {

    private OWLModel owlModel;

    private SWRLSymbolPanel symbolPanel;

    private SWRLTextArea textArea;


    public SWRLTextAreaPanel(OWLModel owlModel) {
        this(owlModel, null);
    }


    public SWRLTextAreaPanel(OWLModel anOWLModel, SWRLImp imp) {
        this.owlModel = anOWLModel;
        symbolPanel = new SWRLSymbolPanel(anOWLModel, false, false);
        textArea = new SWRLTextArea(anOWLModel, symbolPanel) {
            protected void checkExpression(String text) throws Throwable {
                owlModel.getOWLClassDisplay().getParser().checkClass(owlModel, text);
            }
        };
        if (imp != null && imp.getHead() != null) {
            String text = imp.getBrowserText();
            textArea.setText(text);
            textArea.reformatText();
        }
        symbolPanel.setSymbolEditor(textArea);

        InstanceDisplay id = new InstanceDisplay(anOWLModel.getProject(), false, false);
        id.setInstance(imp);

        setLayout(new BorderLayout(0, 8));
        add(BorderLayout.NORTH, id);
        add(BorderLayout.CENTER, new JScrollPane(textArea));
        add(BorderLayout.SOUTH, symbolPanel);
        setPreferredSize(new Dimension(600, 400));
    }


    public boolean canClose(int result) {
        if (result == ModalDialogFactory.OPTION_OK) {
            String uniCodeText = textArea.getText();
            if (uniCodeText.length() == 0) {
                return false;
            }
            else {
                try {
                    SWRLParser parser = new SWRLParser(owlModel);
                    parser.parse(uniCodeText);
                    return true;
                }
                catch (Exception ex) {
                    symbolPanel.displayError(ex);
                    return false;
                }
            }
        }
        else
            return true;

    }


    public SWRLImp getResultAsImp() {
        try {
            String uniCodeText = textArea.getText();
            SWRLParser parser = new SWRLParser(owlModel);
            parser.setParseOnly(false);
            return parser.parse(uniCodeText);
        }
        catch (Exception ex) {
            return null;
        }
    }


    public String getResultAsString() {
        return textArea.getText();
    }


    public static boolean showEditDialog(Component parent, OWLModel owlModel, SWRLImp imp) {
        SWRLTextAreaPanel panel = new SWRLTextAreaPanel(owlModel, imp);
        String title = "Edit SWRL Rule";
        if (ProtegeUI.getModalDialogFactory().showDialog(parent, panel, title, ModalDialogFactory.MODE_OK_CANCEL, panel) == ModalDialogFactory.OPTION_OK) {
            try {
                imp.setExpression(panel.getResultAsString());
                return true;
            }
            catch (Exception ex) {
                System.err.println("[SWRLTextAreaPanel]  Fatal error");
                ex.printStackTrace();
            }
        }
        return false;
    }
}
