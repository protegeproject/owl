package edu.stanford.smi.protegex.owl.swrl.ui.code;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLIncompleteRuleException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextArea;

/**
 * A SymbolTextArea with special support for editing SWRL expressions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTextArea extends SymbolTextArea {

    public SWRLTextArea(OWLModel owlModel, SymbolErrorDisplay errorDisplay) {
        super(owlModel, errorDisplay, new SWRLResourceNameMatcher(), new SWRLSyntaxConverter(owlModel));
        SWRLTextField.initKeymap(this);
    }


    protected void checkUniCodeExpression(String uniCodeText) throws Throwable {
        SWRLParser parser = new SWRLParser(getOWLModel());
        try {
            parser.parse(uniCodeText);
        }
        catch (SWRLIncompleteRuleException e) {
            // Ignore incomplete rules on input checking. (Unlike
            // SymbolTextField, SymbolTextArea only calls
            // checkUniCodeExpression when it is checking an expression
            // for errors, not when it is determining if an expression
            // can be saved.
        } // try
    } // checkUniCodeExpression


    public void reformatText() {
        String text = getText();
        text = text.replaceAll("" + SWRLParser.AND_CHAR + "  ", "" + SWRLParser.AND_CHAR + "\n");
        text = text.replaceAll("" + SWRLParser.IMP_CHAR, "\n  " + SWRLParser.IMP_CHAR);
        setText(text);
    }
}
