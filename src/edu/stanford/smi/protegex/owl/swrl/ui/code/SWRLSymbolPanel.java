package edu.stanford.smi.protegex.owl.swrl.ui.code;

import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.code.SymbolPanel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLSymbolPanel extends SymbolPanel 
{
  private Action andAction;
  private ResourceSelectionAction builtinAction;
  private Action createVariableAction;
  private Action datatypeAction;
  private Action differentFromAction;
  private Action impAction;
  private Action insertVariableAction;
  private JButton insertVariableButton;
  private Action openBracketsAction;
  private Action closeBracketsAction;
  private Action openParenthesisAction;
  private Action closeParenthesisAction;
  private Action sameAsAction;
  
  public SWRLSymbolPanel(OWLModel owlModel, boolean closeable, boolean draggable) 
  {
    super(owlModel, closeable, draggable, true);
  }

  protected String getDisplayErrorMessage(Throwable ex) {
    if (ex instanceof SWRLParseException) {
      return ((SWRLParseException) ex).getMessage();
    }
    else {
      return "" + ex;
    }
  }

  public Dimension getPreferredSize() {
    Dimension pref = super.getPreferredSize();
    return new Dimension(500, pref.height);
  }

  private Collection getUsedVariableNames() {
    String text = getSymbolEditor().getText();
    Collection result = new HashSet();
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '?') {
        String varName = getVariableName(text, i);
        if (varName.length() > 0) {
          result.add(varName);
        }
      }
    }
    return result;
  }
  
  private String getVariableName(String text, int beginIndex) {
    int index = beginIndex + 1;
    while (index < text.length() &&
           Character.isJavaIdentifierPart(text.charAt(index))) {
      index++;
    }
    return text.substring(beginIndex + 1, index);
  }

  protected void initMiddleBar(JToolBar toolBar) 
  {
    datatypeAction = new InsertXMLSchemaDatatypeAction();
    addButton(toolBar, datatypeAction);
    
    andAction = new AbstractAction("Insert conjunction (" + SWRLParser.AND_CHAR + ")",
                                   SWRLIcons.getInsertAndIcon()) {
        public void actionPerformed(ActionEvent e) {
          getSymbolEditor().insertText("  " + SWRLParser.AND_CHAR + "  ");
        }
      };
    addButton(toolBar, andAction);
    
    impAction = new AbstractAction("Insert implication (" + SWRLParser.IMP_CHAR + ")",
                                   SWRLIcons.getInsertImpIcon()) {
        public void actionPerformed(ActionEvent e) {
          getSymbolEditor().insertText("  " + SWRLParser.IMP_CHAR + "  ");
        }
      };
    addButton(toolBar, impAction);
    
    toolBar.addSeparator();
    
    openParenthesisAction = new AbstractAction("Insert open parenthesis: (",
                                               SWRLIcons.getOpenParenthesis()) {
        public void actionPerformed(ActionEvent e) {
          getSymbolEditor().insertText("(", 1);
        }
      };
    addButton(toolBar, openParenthesisAction);
    
        closeParenthesisAction = new AbstractAction("Insert close parenthesis: )",
                                                    SWRLIcons.getCloseParenthesis()) {
            public void actionPerformed(ActionEvent e) {
              getSymbolEditor().insertText(")", 1);
            }
          };
        addButton(toolBar, closeParenthesisAction);
        
        toolBar.addSeparator();

        openBracketsAction = new AbstractAction("Insert open brackets: [",
                                                SWRLIcons.getOpenBrackets()) {
            public void actionPerformed(ActionEvent e) {
              getSymbolEditor().insertText("[", 1);
            }
          };
        addButton(toolBar, openBracketsAction);
        
        closeBracketsAction = new AbstractAction("Insert close brackets: ]",
                                                 SWRLIcons.getCloseBrackets()) {
            public void actionPerformed(ActionEvent e) {
                getSymbolEditor().insertText("]", 1);
            }
          };
        addButton(toolBar, closeBracketsAction);
  }
  
  
  protected void initTopBar(JToolBar toolBar) 
  {
    classAction.activateComboBox(addButton(toolBar, classAction));
    propertyAction.activateComboBox(addButton(toolBar, propertyAction));
    individiualAction.activateComboBox(addButton(toolBar, individiualAction));
    toolBar.addSeparator();
    createVariableAction = new CreateVariableAction();
    addButton(toolBar, createVariableAction);
    insertVariableAction = new InsertVariableAction();
    insertVariableButton = addButton(toolBar, insertVariableAction);
    
    toolBar.addSeparator();
    
    builtinAction = new InsertBuiltinAction();
    builtinAction.activateComboBox(addButton(toolBar, builtinAction));
    
    differentFromAction = new AbstractAction("Insert differentFrom",
                                             SWRLIcons.getDifferentFromIcon()) {
        public void actionPerformed(ActionEvent e) {
          getSymbolEditor().insertText("differentFrom(", 14);
        }
      };
    addButton(toolBar, differentFromAction);
    sameAsAction = new AbstractAction("Insert sameAs",
                                      SWRLIcons.getSameAsIcon()) {
        public void actionPerformed(ActionEvent e) {
          getSymbolEditor().insertText("sameAs(", 7);
        }
      };
    addButton(toolBar, sameAsAction);
    
    toolBar.addSeparator();
  }

  private void insertVariable(ActionEvent e) {
    Set vars = new HashSet(getUsedVariableNames());
    final Collection instances = getOWLModel().getRDFSNamedClass(SWRLNames.Cls.VARIABLE).getInstances(true);
    for (Iterator it = instances.iterator(); it.hasNext();) {
      RDFResource resource = (RDFResource) it.next();
      vars.add(resource.getName());
    }
    String[] symbols = (String[]) vars.toArray(new String[0]);
    Arrays.sort(symbols);
    
    JPopupMenu menu = new JPopupMenu();
    for (int i = 0; i < symbols.length; i++) {
            String symbol = symbols[i];
            final JMenuItem item = new JMenuItem(symbol, SWRLIcons.getVariableIcon());
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  String text = (String) item.getText();
                  getSymbolEditor().insertText("?" + text);
                }
              });
            menu.add(item);
    }
    if (menu.getComponentCount() > 0) {
      menu.show(insertVariableButton, 0, insertVariableButton.getHeight());
    }
  }

  private class InsertBuiltinAction extends ResourceSelectionAction {
    
    InsertBuiltinAction() { super("Insert builtin...", SWRLIcons.getBuiltinIcon()); }

    public void resourceSelected(RDFResource resource) 
    {
      String str = resource.getBrowserText() + "(";
      getSymbolEditor().insertText(str);
    }

    public Collection getSelectableResources() 
    {
      java.util.List result = new ArrayList(new SWRLFactory(getOWLModel()).getBuiltins());
      Collections.sort(result, new FrameComparator());
      return result;
    }

    public RDFResource pickResource() {
      Collection frames = getSelectableResources();
      return ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(SWRLSymbolPanel.this, getOWLModel(), frames,
                                                                                "Select the builtin to insert");
    }
  }
  
  private class CreateVariableAction extends AbstractAction 
  {
    CreateVariableAction() {
      super("Insert new variable",
            OWLIcons.getCreateIcon(SWRLIcons.VARIABLE, SWRLIcons.class));
    }
    
    
    public void actionPerformed(ActionEvent e) {
      String variableName = getNextVariableName();
      if (variableName != null) {
        getSymbolEditor().insertText("?" + variableName);
      }
    }
    
    private String getNextVariableName() {
      Collection usedNames = getUsedVariableNames();
      Collection existingVariableNames = new ArrayList();
      OWLModel owlModel = getOWLModel();
      SWRLFactory factory = new SWRLFactory(owlModel);
      final Collection variables = factory.getVariables();
      for (Iterator it = variables.iterator(); it.hasNext();) {
        SWRLVariable variable = (SWRLVariable) it.next();
        existingVariableNames.add(variable.getName());
      }
      String chars = "xyzabcdefghijklmnopqrstuvwXYZABCDEFGHIJKLMNOPQRSTUVW";
      for (int i = 0; i < chars.length(); i++) {
        char c = chars.charAt(i);
        String name = "" + c;
                if (!usedNames.contains(name)) {
                  if (owlModel.getRDFResource(name) == null || owlModel.getRDFResource(name) instanceof SWRLVariable) {
                    return name;
                  }
                }
      }
      return null;
    }
  }
  
  private class InsertVariableAction extends AbstractAction 
  {  
    InsertVariableAction() { super("Insert existing variable...", OWLIcons.getAddIcon(SWRLIcons.VARIABLE, SWRLIcons.class)); }

    public void actionPerformed(ActionEvent e) { insertVariable(e); }
  }

  private class InsertXMLSchemaDatatypeAction extends AbstractAction 
  {
    InsertXMLSchemaDatatypeAction() { super("Insert XML Schema datatype", OWLIcons.getImageIcon(OWLIcons.XSD_DATATYPE)); }

    public void actionPerformed(ActionEvent e) 
    {
      OWLModel owlModel = getOWLModel();
      Collection datatypes = owlModel.getRDFSDatatypes();
      RDFResource datatype = ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(SWRLSymbolPanel.this, owlModel,
                                                                                                datatypes, 
                                                                                                "Select the XML Schema datatype to insert");
      if (datatype != null) { getSymbolEditor().insertText(datatype.getLocalName()); }
    }
  }
}
