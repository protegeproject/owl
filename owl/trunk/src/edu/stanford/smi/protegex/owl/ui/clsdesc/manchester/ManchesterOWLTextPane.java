package edu.stanford.smi.protegex.owl.ui.clsdesc.manchester;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.classparser.manchester.ManchesterOWLParserUtil;
import edu.stanford.smi.protegex.owl.ui.code.*;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceIgnoreCaseComparator;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ManchesterOWLTextPane extends JTextPane implements KeyListener {

    private JComboBox comboBox;

    private SymbolErrorDisplay errorDisplay;

    private ResourceNameMatcher resourceNameMatcher;

    private OWLModel model;

    private KeyEvent previousKeyPressed;

    public final static int SCALABLE_FRAME_COUNT = 1000;

    private SyntaxConverter syntaxConverter;

    // Flag that can (optionally) be used by checkUniCodeExpression
    // methods in subclasses to determine if they should modify their
    // expression checking behaviour. Some editors may chose not to
    // signal an error for correct but incomplete rules while they are
    // being entered but will signal an error if an attempt is made to
    // save such a rule. inSaveTestMode is set to true to indicate to
    // checkUniCodeExpression that it being used to determine if an
    // expression is saveable; when it is false, it signals that the
    // method is used to perform normal interactive input checking.
    //
    // Subclasses that do not distinguish between edit and save mode
    // testing can ignore this flag.

    private boolean inSaveTestMode = false;

    private SymbolEditorHandler editorHandler;

    private DocumentListener docListener;

    private Map styleMap;

    private Style defaultStyle;

    private static final Color RESTRICTION_KEYWORD_COLOR = Color.MAGENTA.darker();

    private static final Color LOGICAL_OPERAND_KEYWORD_COLOR = Color.CYAN.darker();

    private static Font font = new JList().getFont();


    public ManchesterOWLTextPane(OWLModel kb,
                                 SymbolErrorDisplay errorDisplay,
                                 ResourceNameMatcher resourceNameMatcher,
                                 SyntaxConverter syntaxConverter) {
        model = kb;
        this.errorDisplay = errorDisplay;
        this.resourceNameMatcher = resourceNameMatcher;
        this.syntaxConverter = syntaxConverter;
        this.resourceNameMatcher = new OWLResourceNameMatcher();
        setFont(font);
        setBackground(Color.white);
        removeAll();
        addKeyListener(this);
        addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                if (previousKeyPressed != null) {
                    int keyCode = previousKeyPressed.getKeyCode();
                    if (!isIdChar(previousKeyPressed.getKeyChar()) &&
                            keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_DELETE) {
                        closeComboBox();
                    }
                }
            }
        });

        initColorMap();
        docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                performHighlighting();
            }


            public void removeUpdate(DocumentEvent e) {
                performHighlighting();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        };
        getDocument().addDocumentListener(docListener);
        setFocusable(true);
        setupBrackertMatcher();
        Window owner = (Window) Application.getMainWindow();
        if (owner != null) {
            SuggestionPopup suggestionPopup = new SuggestionPopup(owner, model);
            suggestionPopup.setCurrentEditorPane(this);
        }
    }


    public int getPreferredHeight(int width) {
        View v = getUI().getRootView(this);
        v.setSize(width, Integer.MAX_VALUE);
        return (int) v.getPreferredSpan(View.Y_AXIS);
    }


    private void initColorMap() {
        StyledDocument doc = (StyledDocument) getDocument();
        Style restrictionKWStyle = doc.addStyle("rs", null);
        StyleConstants.setForeground(restrictionKWStyle, RESTRICTION_KEYWORD_COLOR);
        StyleConstants.setBold(restrictionKWStyle, true);
        Style logicalKWStyle = doc.addStyle("lk", null);
        StyleConstants.setForeground(logicalKWStyle, LOGICAL_OPERAND_KEYWORD_COLOR);
        StyleConstants.setBold(logicalKWStyle, true);
        styleMap = new HashMap();
        styleMap.put(ManchesterOWLParserUtil.getAllKeyword(), restrictionKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getSomeKeyword(), restrictionKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getHasKeyword(), restrictionKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getMinKeyword(), restrictionKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getMaxKeyword(), restrictionKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getExactKeyword(), restrictionKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getAndKeyword(), logicalKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getOrKeyword(), logicalKWStyle);
        styleMap.put(ManchesterOWLParserUtil.getNotKeyword(), logicalKWStyle);
        defaultStyle = doc.addStyle("def", null);
    }


    public void setSymbolEditorHandler(SymbolEditorHandler editorHandler) {
        this.editorHandler = editorHandler;
    }


    private void performHighlighting() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                String text = getText();
                StringTokenizer tokenizer = new StringTokenizer(text, " ()[]{}", true);
                int start = 0;
                StyledDocument doc = (StyledDocument) getDocument();
                while (tokenizer.hasMoreTokens()) {
                    String curToken = tokenizer.nextToken();
                    Style style = (Style) styleMap.get(curToken);
                    if (style != null) {
                        doc.setCharacterAttributes(start, curToken.length(), style, true);
                    }
                    else {
                        doc.setCharacterAttributes(start, curToken.length(), defaultStyle, true);
                    }
                    start += curToken.length();
                }
            }
        });
        t.start();
    }


    private void acceptSelectedResource() {
        String text = getText();
        int pos = getCaretPosition();
        int i = pos - 1;
        while (i >= 0 && isIdChar(text.charAt(i))) {
            i--;
        }
        String prefix = text.substring(i + 1, pos);
        RDFResource resource = ((RDFResource) comboBox.getSelectedItem());
        extendPartialName(prefix, resourceNameMatcher.getInsertString(resource));
        updateErrorDisplay();
        closeComboBox();
    }

//    public void backspace() {
//        String selText = getSelectedText();
//        if (selText != null && selText.length() > 0) {
//            int start = getSelectionStart();
//            try {
//                getDocument().remove(start, getSelectionEnd() - start);
//            }
//            catch (BadLocationException ex) {
//                ex.printStackTrace();
//            }
//            setCaretPosition(start);
//        }
//        else {
//            int pos = getCaretPosition();
//            if (pos > 0) {
//                try {
//                    getDocument().remove(pos - 1, 1);
//                }
//                catch (BadLocationException ex) {
//                    ex.printStackTrace();
//                }
//                setCaretPosition(pos - 1);
//            }
//        }
//        updateErrorDisplay();
//        requestFocus();
//    }


    /**
     * Used for error checking during input.
     *
     * @param text
     * @throws Throwable ParseExceptions etc.
     */
    protected void checkUniCodeExpression(String text) throws Throwable {
        model.getOWLClassDisplay().getParser().checkClass(model, text);
    }
    // See inSaveTestMode comment above.


    protected boolean isInSaveTestMode() {
        return inSaveTestMode;
    }


    private void closeComboBox() {
        removeAll();
    }

//    public void displayError() {
//        try {
//            String uniCodeText = getText();
//            checkUniCodeExpression(uniCodeText);
//            // editedRestriction.checkFillerText(text);
//            errorDisplay.displayError((Throwable) null);
//        }
//        catch (Throwable ex) {
//            errorDisplay.displayError(ex);
//        }
//        requestFocus();
//    }


    private void extendPartialName(String prefix, String fullName) {
        try {
            getDocument().remove(getCaretPosition() - prefix.length(), prefix.length());
            getDocument().insertString(getCaretPosition(), fullName, null);
        }
        catch (BadLocationException ex) {
        }
    }


    private void extendPartialName(boolean autoInsert) {
        String text = getText();
        int pos = getCaretPosition();
        int i = pos - 1;
        while (i >= 0 && isIdChar(text.charAt(i))) {
            i--;
        }
        String prefix = text.substring(i + 1, pos);
        String leftString = text.substring(0, i + 1);
        java.util.List resources = resourceNameMatcher.getMatchingResources(prefix, leftString, model);
        if (autoInsert && resources.size() == 1) {
            RDFResource resource = (RDFResource) resources.get(0);
            extendPartialName(prefix, resourceNameMatcher.getInsertString(resource));
            closeComboBox();
        }
        else if (resources.size() > 1) {
            showComboBox(resources, i + 1);
        }
    }


    protected OWLModel getmodel() {
        return model;
    }


    /**
     * Gets the x (pixel) position of the start of a given character in the text.
     *
     * @param pos the character position
     * @return the pixel position
     */
    private int getXOfPosition(int pos) {
        Font font = getFont();
        FontMetrics metrics = getFontMetrics(font);
        return metrics.stringWidth(getText().substring(0, pos));
    }


    private void handleDown() {
        if (comboBox != null && comboBox.isVisible()) {
            int index = comboBox.getSelectedIndex();
            if (index < comboBox.getItemCount() - 1) {
                comboBox.setSelectedIndex(index + 1);
            }
        }
    }


    protected void handleEnter() {
        if (isComboBoxVisible()) {
            acceptSelectedResource();
        }
        else {
            inSaveTestMode = true;
            try {
                String uniCodeText = getText();
                checkUniCodeExpression(uniCodeText);
                if (editorHandler != null) {
                    editorHandler.stopEditing();
                }
            }
            catch (Throwable ex) {
                errorDisplay.displayError(ex);
                requestFocus();
            }
            inSaveTestMode = false;
        }
    }


    private void handleEscape() {
        if (isComboBoxVisible()) {
            closeComboBox();
        }
        else {
            if (editorHandler != null) {
                editorHandler.cancelEditing();
            }
        }
    }


    private void handleUp() {
        if (isComboBoxVisible()) {
            int index = comboBox.getSelectedIndex();
            if (index > 0) {
                comboBox.setSelectedIndex(index - 1);
            }
        }
    }


    public void insertText(String text) {
        insertText(text, text.length());
    }


    public void insertText(String text, int caretOffset) {
        String selText = getSelectedText();
        if (selText != null && selText.length() > 0) {
            int start = getSelectionStart();
            try {
                getDocument().remove(start, getSelectionEnd() - start);
                getDocument().insertString(start, text, null);
                setCaretPosition(start + caretOffset);
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        else {
            try {
                int pos = getCaretPosition();
                getDocument().insertString(pos, text, null);
                setCaretPosition(pos + caretOffset);
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        updateErrorDisplay();
        requestFocus();
    }


    private boolean isComboBoxVisible() {
        return comboBox != null && comboBox.isVisible() && comboBox.isShowing();
    }


    public static boolean isIdChar(char ch) {
        return Character.isJavaIdentifierPart(ch) || ch == ':' || ch == '-';
    }


    public void keyPressed(KeyEvent e) {
        updateErrorDisplay();
        previousKeyPressed = e;
        int code = e.getKeyCode();
        if (e.getKeyCode() == KeyEvent.VK_TAB || (e.getKeyChar() == ' ' &&
                (e.getModifiers() & java.awt.event.InputEvent.CTRL_MASK) != 0)) {
            extendPartialName(true);
            e.consume();
        }
        else {
            switch (code) {
                case KeyEvent.VK_DOWN: {
                    if (comboBox != null && comboBox.isVisible()) {
                        handleDown();
                        e.consume();
                    }
                    break;
                }
                case KeyEvent.VK_UP: {
                    if (comboBox != null && comboBox.isVisible()) {
                        handleUp();
                        e.consume();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    handleEscape();
                    e.consume();
                    break;
                }
                case KeyEvent.VK_ENTER: {
                    handleEnter();
                    e.consume();
                    break;
                }
            }
        }
    }


    public void keyReleased(KeyEvent e) {
        perhapsConvertSyntax(e);
        updateErrorDisplay();
        int code = e.getKeyCode();
        if (code != KeyEvent.VK_DOWN && code != KeyEvent.VK_UP) {
            refreshComboBox();
        }
    }


    public void keyTyped(KeyEvent e) {
        perhapsConvertSyntax(e);
    }


    private void perhapsConvertSyntax(KeyEvent e) {
        if (syntaxConverter != null) {
            char ch = e.getKeyChar();
            int code = e.getKeyCode();
            if (!isIdChar(ch) &&
                    code != KeyEvent.VK_BACK_SPACE &&
                    code != KeyEvent.VK_DELETE) {
                syntaxConverter.convertSyntax(this);
            }
        }
    }


    private void refreshComboBox() {
        if (isComboBoxVisible()) {
            String text = getText();
            int pos = getCaretPosition();
            int i = pos - 1;
            while (i >= 0 && isIdChar(text.charAt(i))) {
                i--;
            }
            String prefix = text.substring(i + 1, pos);
            String leftString = text.substring(0, i + 1);
            java.util.List frames = resourceNameMatcher.getMatchingResources(prefix, leftString, model);
            if (frames.size() == 0) {
                closeComboBox();
            }
            else {
                showComboBox(frames, i + 1);
            }
        }
    }


    void replaceText(String text) {
        setText(text);
        updateErrorDisplay();
        requestFocus();
    }


    /**
     * Establishs the reference from the TextField to the CellEditor that owns it.
     * This must be called after the CellEditor has been created.
     * The reference can not be passed in the constructor of this, because the constructor
     * of DefaultCellEditor already takes a JTextField as argument, so it has to be there
     * before the DefaultCellEditor can be created.
     * <p/>
     * //     * @param cellEditor the CellEditor
     */
//    public void setCellEditor(CellEditor cellEditor) {
//        this.cellEditor = cellEditor;
//    }
    private void showComboBox(java.util.List frames, int startIndex) {
        closeComboBox();
        edu.stanford.smi.protege.model.Frame[] fs = (edu.stanford.smi.protege.model.Frame[]) frames.toArray(new edu.stanford.smi.protege.model.Frame[0]);
        Arrays.sort(fs, new ResourceIgnoreCaseComparator());
        comboBox = new JComboBox(fs);
        comboBox.setBackground(Color.white);
        comboBox.setRenderer(new FrameRenderer());
        comboBox.setSize(comboBox.getPreferredSize().width + 20, 0);
        int x = getXOfPosition(startIndex) - 16;
        comboBox.setLocation(x, getHeight());

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((e.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
                    acceptSelectedResource();
                }
            }
        });
        add(comboBox);
        comboBox.showPopup();
    }


    private void updateErrorDisplay() {
        String uniCodeText = getText();
        try {
            //String text = OWLTextFormatter.getParseableString(uniCodeText);
            checkUniCodeExpression(uniCodeText);
            errorDisplay.displayError((Throwable) null);
            setBackground(Color.white);
        }
        catch (Throwable ex) {
            errorDisplay.setErrorFlag(true);
            setBackground(new Color(240, 240, 240));
        }
    }


    protected void updateSyntax() {
        OWLTextFormatter.updateSyntax(this, model);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Bracket matching


    private CaretListener caretListener = new CaretListener() {
        public void caretUpdate(CaretEvent e) {
            performMatching();
        }
    };

    private FocusListener focusListener = new FocusAdapter() {
        public void focusLost(FocusEvent e) {
            cleanupPrevious();
        }
    };

    private Highlighter.HighlightPainter matchedHighlightPainter;

    private Highlighter.HighlightPainter unmatchedHighlightPainter;

    private Object h0;

    private Object h1;

//	public MatchingBracketHighlighter(Color unmatchedColor, Color matchedColor) {
//		this.unmatchedHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(unmatchedColor);
//		this.matchedHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(matchedColor);
//	}

//	public MatchingBracketHighlighter() {
//		this(new Color(255, 190, 190), new Color(170,170, 255));
//	}


    private void setupBrackertMatcher() {
        addListeners();
        this.unmatchedHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 190, 190));
        this.matchedHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(170, 170, 255));
    }


    private void addListeners() {
        addCaretListener(caretListener);
        addFocusListener(focusListener);
    }


    private void performMatching() {

        if (getCaret().isVisible() == true) {
            Runnable runnable = new Runnable() {
                public void run() {
                    cleanupPrevious();

                    // Case where caret is before opening bracet |(
                    if (isCaretBeforeOpeningBracket()) {
                        doBeforeOpeningMatch();
                    }

                    // Case where caret is after opening bracket (|
                    // Don't match this

                    // Case where caret is before closing bracket |)
                    // Don't match this

                    // Case where caret is after closing bracket )|
                    if (isCaretAfterClosingBracket()) {
                        doAfterClosingMatch();
                    }
                }
            };

            Thread t = new Thread(runnable);
            t.start();
        }
    }


    private boolean isCaretBeforeOpeningBracket() {
        int caretPos = getCaretPosition();
        if (caretPos < getDocument().getLength() - 1) {
            return getText().charAt(caretPos) == '(';
        }
        else {
            return false;
        }
    }


    private boolean isCaretAfterClosingBracket() {
        int caretPos = getCaretPosition();
        if (caretPos > 1) {
            return getText().charAt(caretPos - 1) == ')';
        }
        else {
            return false;
        }

    }


    private void doBeforeOpeningMatch() {
        try {
            // Search forward for the matching
            // closing bracket
            int pos = matchForward();
            int caretPos = getCaretPosition();
            if (pos == -1) {
                h0 = getHighlighter().addHighlight(caretPos, caretPos + 1, unmatchedHighlightPainter);
                h1 = null;
            }
            else {
                h0 = getHighlighter().addHighlight(caretPos, caretPos + 1, matchedHighlightPainter);
                h1 = getHighlighter().addHighlight(pos, pos + 1, matchedHighlightPainter);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private void doAfterClosingMatch() {
        try {
            int pos = matchReverse();
            int caretPos = getCaretPosition();
            if (pos == -1) {
                h0 = getHighlighter().addHighlight(caretPos - 1, caretPos, unmatchedHighlightPainter);
                h1 = null;
            }
            else {
                h0 = getHighlighter().addHighlight(caretPos - 1, caretPos, matchedHighlightPainter);
                h1 = getHighlighter().addHighlight(pos, pos + 1, matchedHighlightPainter);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private int matchForward() {
        int pos = -1;
        int docLen = getDocument().getLength();
        int bracketCounter = 0;
        int fromPos = getCaretPosition() + 1;
        char [] chars = new char[docLen];
        getText().getChars(0, docLen, chars, 0);
        for (int i = fromPos; i < docLen; i++) {
            if (chars[i] == '(') {
                bracketCounter++;
            }
            if (chars[i] == ')' && bracketCounter == 0) {
                pos = i;
                break;
            }
            if (chars[i] == ')') {
                bracketCounter--;
            }
        }
        return pos;
    }


    private int matchReverse() {
        int fromPos = getCaretPosition() - 1;
        int pos = -1;
        try {
            char [] chars = new char[fromPos + 1];
            getDocument().getText(0, fromPos).getChars(0, fromPos, chars, 0);
            int bracketCounter = 0;
            for (int i = fromPos; i > -1; i--) {
                if (chars[i] == ')') {
                    bracketCounter++;
                }
                if (chars[i] == '(' && bracketCounter == 0) {
                    pos = i;
                    break;
                }
                if (chars[i] == '(') {
                    bracketCounter--;
                }
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return pos;
        }
        return pos;
    }


    private void cleanupPrevious() {
        if (h0 != null) {
            getHighlighter().removeHighlight(h0);
        }
        if (h1 != null) {
            getHighlighter().removeHighlight(h1);
        }
    }
}

