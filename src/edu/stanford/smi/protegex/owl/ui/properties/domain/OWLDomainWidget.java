package edu.stanford.smi.protegex.owl.ui.properties.domain;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A Widget for the domain of an RDFProperty.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDomainWidget extends AbstractPropertyWidget {

    private AddClassToDomainAction addAction;

    private AllowableAction removeAction;

    private OWLDomainTable table;

    private OWLDomainTableModel tableModel;

    public void initialize() {

        tableModel = new OWLDomainTableModel(null);
        table = new OWLDomainTable(tableModel);
        table.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                updateActions();
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        LabeledComponent lc = new OWLLabeledComponent("Domain  " + DefaultOWLUnionClass.OPERATOR, scrollPane);
        WidgetUtilities.addViewButton(lc, new ViewAction("View class", table) {
            public void onView(Object o) {
                getProject().show((Instance) o);
            }
        });
        addAction = new AddClassToDomainAction(this);
        updateActions();
        lc.addHeaderButton(addAction);
        removeAction = new AllowableAction("Remove from Domain",
                                           OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS), table) {
            public void actionPerformed(ActionEvent e) {
                RDFProperty property = (RDFProperty) getEditedResource();
                Collection sel = table.getSelection();
                for (Iterator it = sel.iterator(); it.hasNext();) {
                    RDFSClass cls = (RDFSClass) it.next();
                    removeFromDomain(cls, property);
                }
            }


            public void onSelectionChange() {
                Collection sel = table.getSelection();
                boolean allowed = false;
                if (!sel.isEmpty()) {
                    RDFProperty prop = (RDFProperty) getEditedResource();
                    for (Iterator it = sel.iterator(); it.hasNext();) {
                        RDFSClass cls = (RDFSClass) it.next();
                        if (cls != cls.getOWLModel().getOWLThingClass() &&
                            prop.getUnionDomain(false).contains(cls)) {
                            allowed = true;
                        }
                    }
                }
                setAllowed(allowed);
            }
        };
        lc.addHeaderButton(removeAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
               slot.getName().equals(Model.Slot.DIRECT_DOMAIN);
    }

    //TT: This method does the wrong thing. Should be fixed.
    private void removeFromDomain(RDFSClass cls, RDFProperty property) {
        try {
            beginTransaction("Remove " + cls.getBrowserText() + " from the domain of " + property.getBrowserText());
            if (property.isDomainDefined()) {
                property.removeUnionDomainClass(cls);
//                if (property.getUnionDomain(true).size() == 0){
//                    property.setDomain(cls.getOWLModel().getOWLThingClass());
//                }
            }
            else {
                Collection classes = new ArrayList(property.getUnionDomain(true));
                if (classes.remove(cls)) {
                    if (classes.size() == 1) {
                        property.setDomain((RDFSClass) classes.iterator().next());
                    }
                    else {
                       // System.out.println("classes = " + classes);
                        RDFSClass domain = cls.getOWLModel().createOWLUnionClass(classes);
                        property.setDomain(domain);
                    }
                }
            }
            property.synchronizeDomainAndRangeOfInverse();
        }
        catch (Exception ex) {
            OWLUI.handleError(getOWLModel(), ex);
        }
        finally {
            endTransaction();
        }
    }


    public void setEditable(boolean b) {
        super.setEditable(b);
        addAction.setEnabled(b);
        removeAction.setAllowed(b);
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance instanceof RDFProperty) {
            final RDFProperty newProperty = (RDFProperty) newInstance;
            tableModel.setSlot(newProperty);
        }
        else {
            tableModel.setSlot(null);
        }
        updateActions();
    }


    public void setValues(Collection values) {
        super.setValues(values);
        updateActions();
    }


    private void updateActions() {
        RDFProperty property = tableModel.getSlot();
        boolean enabled = property != null && property.isEditable();
        addAction.setEnabled(enabled);
    }
}
