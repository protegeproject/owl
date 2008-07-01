package edu.stanford.smi.protegex.owl.inference.ui;

import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordRenderer;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTask;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 21, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerProgressModalDialog extends JDialog {

    private JButton okButton;

    private JButton cancelButton;

    private JProgressBar progBar;

    private JLabel messageLabel;

    private String description;

    private String message;


    private JTree tree;

    private DefaultMutableTreeNode root;

    private HashMap treeNodeMap;

    private ReasonerTask curTask;


    public ReasonerProgressModalDialog(Frame owner, String title) {
        super(owner, title, true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocation(getToolkit().getScreenSize().width / 2 - getWidth() / 2,
                getToolkit().getScreenSize().height / 2 - getHeight() / 2);
        createUI();
    }


    private void createUI() {
        // Build UI
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        getContentPane().setLayout(new BorderLayout());
        createLogRecordTree(panel);
        createProgressPanel(panel);
        createButtonPanel(panel);
        getContentPane().add(panel);
        setupKeyClose();
    }


    private void createLogRecordTree(JPanel parentPanel) {
        // Create map to map log records to tree nodes
        treeNodeMap = new HashMap();
// Create the log record tree
        root = new DefaultMutableTreeNode("Reasoner log");
        tree = new JTree(new DefaultTreeModel(root));
        parentPanel.add(new JScrollPane(tree));
        tree.setCellRenderer(new ReasonerLogRecordRenderer());
// Let the renderer determine the height of the tree cell
        tree.setRowHeight(0);
    }


    private void createProgressPanel(JPanel parentPanel) {
        // Panel with message label and progress bar
        JPanel pan = new JPanel(new BorderLayout(7, 7));
        pan.add(messageLabel = new JLabel(), BorderLayout.NORTH);
        progBar = new JProgressBar();
        pan.add(progBar, BorderLayout.SOUTH);
        parentPanel.add(pan, BorderLayout.NORTH);
    }


    private void createButtonPanel(JPanel parentPanel) {
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        okButton.setDefaultCapable(false);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (curTask != null) {
                    curTask.setRequestAbort();
                }

                cancelButton.setEnabled(false);
            }
        });

        // Panel to hold OK Cancel buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 7, 7));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        JPanel buttonPanelHolder = new JPanel(new BorderLayout());
        buttonPanelHolder.add(buttonPanel, BorderLayout.EAST);
        parentPanel.add(buttonPanelHolder, BorderLayout.SOUTH);
        parentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }


    private void setupKeyClose() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    closeDialog();
                }
            }
        });
    }


    public void setMessage(String message) {
        this.message = message;
        updateLabel();
    }


    public void setDescription(String description) {
        this.description = description;
        updateLabel();
    }


    protected void updateLabel() {
        messageLabel.setText(description + ": " + message);
    }


    public void setOKButtonEnabled(boolean b) {
        okButton.setEnabled(b);
    }


    public void setCancelButtonEnabled(boolean b) {
        cancelButton.setEnabled(b);
    }


    public void setProgressBarMaxValue(int value) {
        progBar.setMaximum(value);
    }


    public void setProgress(int progress) {
        if (progBar.isIndeterminate()) {
            progBar.setIndeterminate(false);
        }
        progBar.setValue(progress);
    }


    public void setProgressIndeterminate(boolean b) {
        progBar.setIndeterminate(b);
    }


    public void postLogRecord(ReasonerLogRecord logRecord) {
        // Make it the caller responsibility to ensure that this
        // method is invoked from the event dispatch thread
        DefaultTreeModel model = ((DefaultTreeModel) tree.getModel());
        DefaultMutableTreeNode node;
        treeNodeMap.put(logRecord, node = new DefaultMutableTreeNode(logRecord));
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treeNodeMap.get(logRecord.getParent());
        if (parentNode == null) {
            parentNode = root;
        }
        model.insertNodeInto(node, parentNode, parentNode.getChildCount());
        tree.expandPath(new TreePath(parentNode.getPath()));
        tree.scrollPathToVisible(new TreePath(root.getLastLeaf().getPath()));
    }


    public void setReasonerDescription(String description) {
        setTitle(description);
    }


    // Sets the current reasoner task - this
    // makes it possible to cancel the reaonser task
    public void setTask(ReasonerTask task) {
        curTask = task;
    }


    /**
     * Cleans up the dialog contents and disposes of it.
     */
    private void closeDialog() {
        if (okButton.isEnabled()) {
            treeNodeMap.keySet().removeAll(treeNodeMap.keySet());
            root.removeAllChildren();
            ReasonerProgressModalDialog.this.dispose();
        }
    }
}

