package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnnotationsValueEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextArea textArea;

    private AnnotationsTableCellHolder multiLineHolder;

    private JTextField textField;

    private AnnotationsTableCellHolder singleLineHolder;

    private JComponent editorComponent;
            
    private AnnotationsTableCellHolder comboBoxHolder;

    private JTable table;

    private Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");

    public static final int EDITING_MARGIN = 30;


    public AnnotationsValueEditor(OWLModel owlModel, JTable t) {
        this.table = t;
        textArea = new JTextArea();
        OWLUI.addCopyPastePopup(textArea);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_ENTER:
                        if (e.isControlDown()) {
                            stopCellEditing();
                            e.consume();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        cancelCellEditing();
                        break;
                }
            }
        });
        textArea.setFocusable(true);
        JScrollPane sp = new JScrollPane(textArea);
        sp.setBorder(focusBorder);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        multiLineHolder = new AnnotationsTableCellHolder(sp, BorderLayout.CENTER);
        textField = new JTextField();
        OWLUI.addCopyPastePopup(textField);
        singleLineHolder = new AnnotationsTableCellHolder(textField, BorderLayout.CENTER);

    }


    public Object getCellEditorValue() {
    	if (editorComponent instanceof JTextComponent) {
    		return ((JTextComponent)editorComponent).getText().trim();
    	} else if (editorComponent instanceof JComboBox) {
    		return ((JComboBox)editorComponent).getSelectedItem();
    	} else {
    		return editorComponent.toString();
    	}
    }


    public Component getTableCellEditorComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final int row,
                                                 final int column) {
        RDFProperty property = (RDFProperty) table.getValueAt(row, AnnotationsTableModel.COL_PROPERTY);
        
        if (property.isReadOnly()) {
        	return null;
        }
        
        RDFResource range = property.getRange();
        if (property.getOWLModel().getXSDboolean().equals(range)) {
        	        	
            JComboBox comboBox = new JComboBox(new Boolean[]{
                    Boolean.FALSE,
                    Boolean.TRUE
            });
            
            editorComponent = comboBox;
        	
            return comboBox;
        }
        
        else if (range instanceof OWLDataRange) {
            Collection allowedValues = getOWLDataRangeValues(property, value);
            
            if (allowedValues == null || allowedValues.size() == 0) {
            	return null;
            }
            
            JComboBox comboBox = new JComboBox(allowedValues.toArray());
            
            comboBox.setSelectedItem(value);
            
            editorComponent = comboBox;
            focusTextField();
            
            return comboBox;
  
        }     
        
        else if (AnnotationsTable.isMultiLineProperty(property)) {
            textArea.setText(value != null ? value.toString() : "");
            int rowHeight = getRowHeight(table, row);
            if (table.getRowHeight(row) != rowHeight) {
                table.setRowHeight(row, rowHeight);
            }
            editorComponent = textArea;
            focusTextField();
            return multiLineHolder;
        }
        else {
            textField.setText(value != null ? value.toString() : "");
            editorComponent = textField;
            focusTextField();
            return singleLineHolder;
        }
    }


    protected Collection getOWLDataRangeValues(RDFProperty property, Object value) {		
    	RDFResource range = property.getRange();
		return ((OWLDataRange) range).getOneOfValues();
	}


	private void focusTextField() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                editorComponent.requestFocus();
            }
        });
    }


	public boolean isCellEditable(EventObject e) {		
		// Only edit cell if the user has double
		// clicked it.
		if(e instanceof MouseEvent) {
			return ((MouseEvent) e).getClickCount() == 2;
		}
		else {
			return super.isCellEditable(e);
		}		
	}


    private int getRowHeight(JTable table,
                             int row) {
        Object val = table.getValueAt(row, AnnotationsTableModel.COL_VALUE);
	    if(val == null) {
		    val = "";
	    }
        int preferredHeight = 0;
        View v = textArea.getUI().getRootView(textArea);
        v.setSize(table.getColumnModel().getColumn(AnnotationsTableModel.COL_VALUE).getWidth(), Integer.MAX_VALUE);
        preferredHeight = (int) v.getPreferredSpan(View.Y_AXIS) + EDITING_MARGIN;
        JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
        if (sp != null) {
            int maxHeight = sp.getViewport().getViewRect().height;
            if (preferredHeight > maxHeight) {
                preferredHeight = maxHeight;
            }
        }
        return preferredHeight;
    }


	public JTable getTable() {
		return table;
	}


}

