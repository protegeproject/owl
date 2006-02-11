package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A JPanel for modal dialogs that can be used to String-matching search for a certain
 * value of a selected Slot.
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Oct-2005
 */
public class FindResultsPanel extends JComponent {

    private static final String SEARCH_AS_YOU_TYPE_LABEL = "Search as you type";
    private static final String SEARCH_PATTERN_LABEL = "Search Pattern";
    private static final String SAVE_RESULTS_LABEL = "Save Results";

    private java.util.List renameListeners = new ArrayList();

    private Find find;

    private JButton saveResultButton;

    private JComboBox searchTypeCombo;

    private JTextField textField;

    private JCheckBox searchAsYouTypeCheckbox;

    private JPanel mainPanel;

    private AbstractFindResultsView view;

    private static boolean searchAsYouType = true;


    private DocumentListener searchAsYouTypeListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            refresh(); // should refine the existing search rather than from scratch
        }

        public void removeUpdate(DocumentEvent e) {
            refresh();
        }

        public void changedUpdate(DocumentEvent e) {
            refresh();
        }
    };

    // action to send these results to the Results panel at the south of the main window
    private Action saveResultsAction = new AbstractAction(SAVE_RESULTS_LABEL, Icons.getDownIcon()) {
        public void actionPerformed(ActionEvent e) {
            view.requestDispose();
            setSaveResultsEnabled(false);
            RetainFindResultsPanel resultsPanel =
                    new RetainFindResultsPanel(find.getModel(), FindResultsPanel.this);
            ResultsPanelManager.addResultsPanel(find.getModel(), resultsPanel, true);
        }
    };


    public FindResultsPanel(Find find, AbstractFindResultsView view) {
        super();

        setLayout(new BorderLayout());

        this.find = find;
        this.view = view;

        textField = createTextField();

        // when in the dialog, this component never gets a componentShown
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                requestFocus();
            }
        });

        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectResource();
                }
            }
        });

        view.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume();
                }
            }

            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        e.consume(); // do not break
                    case KeyEvent.VK_SPACE:
                        selectResource();
                        break;
                    default:
                        if (!e.isActionKey()) requestFocus();
                }
            }
        });

        searchTypeCombo = createTypeCombo();

        searchAsYouTypeCheckbox = createSearchAsYouType();
        refreshSearchAsYouType();

        final JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(view.getBackground());

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(SEARCH_PATTERN_LABEL));
        northPanel.add(searchTypeCombo);
        northPanel.add(textField);
        northPanel.add(searchAsYouTypeCheckbox);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        fireResultsChanged();
    }


    public void setSearcher(Find s) {
        this.find = s;
    }

    public void setSaveResultsEnabled(boolean canSave) {
        if (canSave) {
            if (saveResultButton == null) {
                saveResultButton = new JButton(saveResultsAction);
                mainPanel.add(saveResultButton, BorderLayout.SOUTH);
            }
        }
        else {
            if (saveResultButton != null) {
                mainPanel.remove(saveResultButton);
                saveResultButton = null;
            }
        }
    }

    public void refresh() {
        refresh(find.getSearchType());
    }

    /**
     * Should run the search and refresh in a seperate thread
     * @param searchType
     */
    public void refresh(int searchType) {
        String string = textField.getText();
        find.startSearch(string, searchType);
//        Map matches = find.getResults();
//        view.setResults(matches);
        fireResultsChanged();
    }


    private void fireResultsChanged() {
        for (Iterator i = renameListeners.iterator(); i.hasNext();) {
            ((RenameListener) i.next()).rename(find.getSummaryText(), this);
        }
    }

    public void selectResource() {
        view.selectResource();
    }

    public void addRenameListener(RenameListener listener) {
        renameListeners.add(listener);
    }

    public void requestFocus() {
        textField.requestFocus();
    }


    private JTextField createTextField() {
        JTextField tField = new JTextField();
        FontMetrics metrics = getFontMetrics(tField.getFont());
        int textheight = metrics.getHeight() + metrics.getDescent();
        tField.setPreferredSize(new Dimension(120, textheight));
        tField.setText(find.getLastSearch());

        tField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        if (!searchAsYouType) refresh(); // NO BREAK
                    case KeyEvent.VK_DOWN:
                        view.requestFocus();
                        break;
                }
            }
        });

        return tField;
    }


    private JComboBox createTypeCombo() {
        JComboBox combo = new JComboBox();
        combo.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                value = Find.searchTypeString[((Integer)value).intValue()];
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        combo.addItem(new Integer(Find.CONTAINS));
        combo.addItem(new Integer(Find.STARTS_WITH));
        combo.addItem(new Integer(Find.ENDS_WITH));
        combo.addItem(new Integer(Find.EXACTLY_MATCHES));
        combo.setSelectedItem(new Integer(find.getSearchType()));
        combo.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                int newValue = ((Integer)e.getItem()).intValue();
                int oldValue = FindResultsPanel.this.find.getSearchType();
                if (oldValue != newValue){
                    refresh(newValue);
                }
            }
        });

        return combo;
    }

    private JCheckBox createSearchAsYouType() {
        JCheckBox cBox = new JCheckBox(new AbstractAction(SEARCH_AS_YOU_TYPE_LABEL) {
        public void actionPerformed(ActionEvent e) {
            refreshSearchAsYouType();
        }
        });
        cBox.setSelected(searchAsYouType);
        return cBox;
    }

    private void refreshSearchAsYouType(){
        searchAsYouType = searchAsYouTypeCheckbox.isSelected();
            if (searchAsYouType) {
                textField.getDocument().addDocumentListener(searchAsYouTypeListener);
            }
            else {
                textField.getDocument().removeDocumentListener(searchAsYouTypeListener);
            }
    }
}