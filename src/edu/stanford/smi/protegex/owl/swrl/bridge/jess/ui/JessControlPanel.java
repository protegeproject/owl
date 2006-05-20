
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class JessControlPanel extends JPanel 
{
  private SWRLJessBridge bridge;

  public JessControlPanel(SWRLJessBridge bridge) 
  {
    JTextArea textArea;
    JButton button;

    this.bridge = bridge;

    setLayout(new FlowLayout());

    textArea = createTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(900, 300));

    add(scrollPane);

    button = createButton("OWL+SWRL->Jess", "Translate SWRL rules and relevant OWL knowledge to Jess", new ImportActionListener(bridge, textArea, this));
    add(button);

    button = createButton("Run Jess", "Run the Jess rule engine", new RunActionListener(bridge, textArea, this));
    add(button);

    button = createButton("Jess->OWL", "Translate asserted Jess knowledge to OWL knowledge", new ExportActionListener(bridge, textArea, this));
    add(button);

    textArea.append("Press the \"OWL+SWRL->Jess\" button to transfer SWRL rules and relevant OWL knowledge to Jess.\n");
    textArea.append("Press the \"Run Jess\" button to Run the Jess rule engine.\n");
    textArea.append("Press the \"Jess->OWL\" button to transfer the inferred Jess knowledge to OWL knowledge.\n\n");
    textArea.append("IMPORTANT: The Jess rule engine is currently ignoring OWL restrictions. To ensure consistency,\n");
    textArea.append("a reasoner should be run on an OWL knowledge base before SWRL rules and OWL knowledge are\n");
    textArea.append("transferred to Jess. Also, if inferred knowledge from Jess is inserted back into an OWL\n");
    textArea.append("knowledge base, a reasoner should again be executed to ensure that the new knowledge does not\n");
    textArea.append("conflict with OWL constraints in that knowledge base.\n");
    textArea.append("cf. http://protege.stanford.edu/plugins/owl/swrl/SWRLBridge.html#limitations for details.");
  } // JessControlPanel

  private JButton createButton(String text, String toolTipText, ActionListener listener)
  {
    JButton button = new JButton(text);
    button.setToolTipText(toolTipText);
    button.setPreferredSize(new Dimension(160, 30));
    button.addActionListener(listener);

    return button;
  } // createButton

  private JTextArea createTextArea()
  {
    JTextArea textArea = new JTextArea(10, 80);
    textArea.setLineWrap(true);
    textArea.setEditable(false);
    return textArea;
  } // createTextArea

  private class ListenerBase
  {
    protected SWRLJessBridge bridge;
    protected JTextArea textArea;
    protected JessControlPanel controlPanel;

    public ListenerBase(SWRLJessBridge bridge, JTextArea textArea, JessControlPanel controlPanel)
    {
      this.bridge = bridge;
      this.textArea = textArea;
      this.controlPanel = controlPanel;
    } // ListenerBase
  } // BridgeHolder

  private class ImportActionListener extends ListenerBase implements ActionListener
  {
    public ImportActionListener(SWRLJessBridge bridge, JTextArea textArea, JessControlPanel controlPanel) { super(bridge, textArea, controlPanel); }

    public void actionPerformed(ActionEvent event) 
    {
      try {
        bridge.resetBridge();
        bridge.importSWRLRulesAndOWLKnowledge();
        bridge.exportSWRLRulesAndOWLKnowledge();
        textArea.setText("");
        textArea.append("SWRL rule and relevant OWL knowledge successfully converted to Jess knowledge.\n");
        textArea.append("Number of SWRL rules exported to Jess: " + bridge.getNumberOfImportedSWRLRules() + "\n");
        textArea.append("Number of OWL classes exported to Jess: " + bridge.getNumberOfImportedClasses() + "\n");
        textArea.append("Number of OWL individuals exported to Jess: " + bridge.getNumberOfImportedIndividuals() + "\n");
        textArea.append("Number of OWL properties exported to Jess: " + bridge.getNumberOfImportedProperties() + "\n");
        textArea.append("Look at the \"Jess Rules\" tab for the Jess rules.\n");
        textArea.append("Look at the \"Imported Jess Classes\" tab for the Jess class definitions.\n");
        textArea.append("Look at the \"Imported Jess Properties\" tab for the Jess property assertions.\n");
        textArea.append("Look at the \"Imported Jess Individuals\" tab for the Jess individual assertions.\n");
        textArea.append("Press the \"Jess->OWL\" button to translate the asserted facts to OWL knowledge.\n");
      } catch (SWRLRuleEngineBridgeException e) {
        textArea.append("Exception importing SWRL rules and OWL knowledge: " + e.toString() + "\n");
      } // try

      controlPanel.getParent().setVisible(false);
      controlPanel.getParent().setVisible(true);
      controlPanel.getParent().validate();

    } // ActionPerformed
  } // ImportActionListener

  private class RunActionListener extends ListenerBase implements ActionListener
  {
    public RunActionListener(SWRLJessBridge bridge, JTextArea textArea, JessControlPanel controlPanel) { super(bridge, textArea, controlPanel); }

    public void actionPerformed(ActionEvent event) 
    {
      try {
        bridge.runRuleEngine();

        textArea.setText("");
        textArea.append("Succesful run of Jess rule engine.\n");
        textArea.append("Number of individuals reclassified by Jess: " + bridge.getNumberOfAssertedIndividuals() + "\n");
        textArea.append("Number of properties asserted by Jess: " + bridge.getNumberOfAssertedProperties() + "\n");
        textArea.append("Look at the \"Asserted Jess Individuals\" tab to see the asserted Jess individuals.\n");
        textArea.append("Look at the \"Asserted Jess Properties\" tab to see the asserted Jess properties.\n");
        textArea.append("Press the \"Jess->OWL\" button to translate the asserted facts to OWL knowledge.\n");
      } catch (SWRLRuleEngineBridgeException e) {
        textArea.append("Exception running Jess rule engine: " + e.toString() + "\n");
      } // try

      controlPanel.getParent().setVisible(false);
      controlPanel.getParent().setVisible(true);
      controlPanel.getParent().validate();

    } // ActionPerformed
  } // RunActionListener

  private class ExportActionListener extends ListenerBase implements ActionListener
  {
    public ExportActionListener(SWRLJessBridge bridge, JTextArea textArea, JessControlPanel controlPanel) { super(bridge, textArea, controlPanel); }

    public void actionPerformed(ActionEvent event) 
    {
      try {
        bridge.writeAssertedIndividualsAndProperties2OWL();
        textArea.setText("");
        textArea.append("Succesfully transferred asserted Jess facts to OWL knowledge.\n");
        textArea.append("Number of individuals reclassified: " + bridge.getNumberOfAssertedIndividuals() + "\n");
        textArea.append("Number of properties asserted: " + bridge.getNumberOfAssertedProperties() + "\n");
      } catch (SWRLRuleEngineBridgeException e) {
        textArea.append("Exception exporting Jess knowledge to OWL: " + e.toString() + "\n");
      } // try

      controlPanel.getParent().setVisible(false);
      controlPanel.getParent().setVisible(true);
      controlPanel.getParent().validate();

    } // ActionPerformed
  } // ExportActionListener

} // JessControlPanel




