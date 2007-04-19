package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.ui.SpringUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.Disposable;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * A Component that can be used to display type(s) and name of an Instance.
 * The name is displayed in a JTextField and may be edited.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InstanceNameComponent extends JPanel implements Disposable {

    private FrameListener frameListener = new FrameAdapter() {
        public void nameChanged(FrameEvent event) {
            updateAll();
        }
    };

    private Instance instance;

    private JLabel leftLabel;

    private JLabel rightLabel;

    private InstanceNameEditor textField;


    public InstanceNameComponent() {
        leftLabel = ComponentFactory.createLabel();
        rightLabel = ComponentFactory.createLabel();
        textField = new InstanceNameEditor();
        
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        add(leftLabel);
        add(textField);
        add(rightLabel);
        
        SpringUtilities.makeCompactGrid(this, 1, 3, 0, 0, 5,0);
    }


    public void dispose() {
        removeListener();
    }


    protected Instance getInstance() {
        return instance;
    }


    protected String getTypeText(Instance instance) {
        StringBuffer typeText = new StringBuffer();
        Iterator i = instance.getDirectTypes().iterator();
        while (i.hasNext()) {
            Cls type = (Cls) i.next();
            typeText.append(type.getBrowserText());
            if (i.hasNext()) {
                typeText.append(", ");
            }
        }
        return typeText.toString();
    }


    private void removeListener() {
        if (instance != null) {
            instance.removeFrameListener(frameListener);
        }
    }


    public void setInstance(Instance instance) {
        removeListener();
        textField.setInstance(instance);
        this.instance = instance;
        if (instance != null) {
            instance.addFrameListener(frameListener);
        }
        updateAll();
    }


    private void updateAll() {
        if (instance != null) {
            leftLabel.setIcon(instance.getIcon());
            String browserText = instance.getBrowserText();
            String name = instance.getName();
            if (name.equals(browserText)) {
                leftLabel.setText("");
            }
            else {
                leftLabel.setText(browserText + " -   Internal name:");
            }
            textField.setText(name);
            String typeText = getTypeText(instance);
            rightLabel.setText("  (instance of " + typeText + ")  ");
            setEditable(instance.isEditable());
        }
        else {
            leftLabel.setIcon(null);
            leftLabel.setText("");
            rightLabel.setText("");
            textField.setText("");
            setEditable(false);
        }
    }

    // wrappers (mostly to keep the API from changing)

    public void setText(String text) {
        textField.setText(text);
    }

    public void selectAll() {
        textField.selectAll();
    }

    public void setEditable(boolean value) {
        textField.setEditable(value);
    }

    protected String getInvalidTextDescription(String text) {
        return textField.getInvalidTextDescription(text);
    }

    protected boolean validateText(String text) {
        return textField.validateText(text);
    }

    protected void commitChanges() {
        textField.attemptCommit();
    }
    
    public void setEnabled(boolean enabled) {
    	setEditable(enabled);
    	super.setEnabled(enabled);
    };
}
