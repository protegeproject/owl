package edu.stanford.smi.protegex.owl.ui.components.singleliteral;

import edu.stanford.smi.protegex.owl.model.RDFProperty;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SingleLiteralAreaComponent extends AbstractSingleLiteralComponent {

    public SingleLiteralAreaComponent(RDFProperty predicate) {
        super(predicate);
    }


    protected JTextComponent createTextComponent() {
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }


    protected Component createTextComponentHolder(JTextComponent textComponent) {
        return new JScrollPane(textComponent);
    }
}
