
package edu.stanford.smi.protegex.owl.swrl.ui.table;

import java.awt.BorderLayout;

import javax.swing.text.JTextComponent;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextField;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorHandler;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;

public class SWRLSymbolEditor extends SymbolEditorComponent
{
	private final SWRLTextField textField;

	public SWRLSymbolEditor(OWLModel model, SymbolErrorDisplay errorDisplay)
	{
		super(model, errorDisplay, false);
		this.textField = new SWRLTextField(model, errorDisplay);
		setLayout(new BorderLayout());
		add(this.textField);
	}

	@Override
	public JTextComponent getTextComponent()
	{
		return this.textField;
	}

	@Override
	protected void parseExpression() throws Exception
	{
		SWRLParser parser = new SWRLParser(getModel());
		parser.parse(this.textField.getText());
	}

	@Override
	public void setSymbolEditorHandler(SymbolEditorHandler symbolEditorHandler)
	{
		super.setSymbolEditorHandler(symbolEditorHandler);
		this.textField.setSymbolEditorHandler(symbolEditorHandler);
	}
}
