package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.query.Query;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;

public class JenaFrameStore implements NarrowFrameStore {
  private Map<RemoteSession, Integer> transactionNesting = new HashMap<RemoteSession, Integer>();
  private OWLModel protegeKb;
  private OntModel model;
  private boolean inConversion = false;
  
  private List<SlotPropertyMapping> mappings;
  
  public JenaFrameStore(OWLModel kb, OntModel model) {
    this.model = model;
    protegeKb = kb;
    SystemFrames sf = kb.getSystemFrames();
    mappings = new ArrayList<SlotPropertyMapping>();
    mappings.add(new SlotPropertyMapping(sf.getDirectDomainSlot(), RDFS.domain, false));
    mappings.add(new SlotPropertyMapping(sf.getDirectInstancesSlot(), RDF.type, true));
    mappings.add(new SlotPropertyMapping(sf.getDirectTypesSlot(), RDF.type, false));
    mappings.add(new SlotPropertyMapping(sf.getDirectSubclassesSlot(), RDFS.subClassOf, true));
    mappings.add(new SlotPropertyMapping(sf.getDirectSubslotsSlot(), RDFS.subPropertyOf, true));
    mappings.add(new SlotPropertyMapping(sf.getDirectSuperclassesSlot(), RDFS.subClassOf, false));
    mappings.add(new SlotPropertyMapping(sf.getDirectSuperslotsSlot(), RDFS.subPropertyOf, true));
  }
  
  public void setInConversion(boolean inConversion) {
    this.inConversion = inConversion;
  }
  
  private int getTransactionNesting() {
    RemoteSession session = ServerFrameStore.getCurrentSession();
    Integer nesting = transactionNesting.get(session);
    if (nesting == null) {
      transactionNesting.put(session, 0);
      nesting = 0;
    }
    return nesting;
  }
  
  private RDFObject buildFrame(RDFNode node) {
    if (node instanceof Literal) {
      return new DefaultRDFSLiteral(protegeKb, (Literal) node);
    }
    Resource resource = (Resource) node;
    Set types = resource.listProperties(RDF.type).toSet();
    if (types.contains(OWL.Ontology)) {
      return new DefaultOWLOntology(protegeKb, resource);
    }
    if (types.contains(OWL.AnnotationProperty) 
        || types.contains(OWL.DatatypeProperty) 
        || types.contains(OWL.ObjectProperty)) {
      if (types.contains(OWL.ObjectProperty)) {
        return new DefaultOWLObjectProperty(protegeKb, (Property) resource.as(Property.class));
      }
      else {
        return new DefaultOWLDatatypeProperty(protegeKb, (Property) resource.as(Property.class));
      }
    }
    throw new RuntimeException("Could not build protege frame from from jena node - " + node);
  }
  
  private SlotPropertyMapping slot2Mapping(Slot slot) {
    for (SlotPropertyMapping map : mappings) {
      if (slot.equals(map.getSlot())) {
        return map;
      }
    }
    return null;
  }
  
  private SlotPropertyMapping property2Mapping(Property property) {
    for (SlotPropertyMapping map : mappings) {
      if (property.equals(map.getProperty())) {
        return map;
      }
    }
    return null;
  }
  
  private class SlotPropertyMapping {
    private Slot slot;
    private Property property;
    boolean inverted;
    
    public boolean isInverted() {
      return inverted;
    }

    public Property getProperty() {
      return property;
    }

    public Slot getSlot() {
      return slot;
    }

    public SlotPropertyMapping(Slot slot, Property property, boolean inverted) {
      this.slot = slot;
      this.property = property;
      this.inverted = inverted;
    }
    
    
  }

  
  /*--------------------------------------------------------------------
   * Narrow Frame Store Interfaces.....
   */
  
  public void addValues(Frame frame, Slot slot, Facet facet,
                        boolean isTemplate, Collection values) {

    if (isTemplate || facet != null) {
      throw  new UnsupportedOperationException("Setting template slots and facets not handled yet");
    }
    if (!(frame instanceof RDFResource)) {
      return; // somebody elses problem...
    }
    
    Resource subject  = ((RDFResource) frame).getJenaResource();
    boolean inverted = false;
    Property property;
    if (slot instanceof RDFProperty) {
      property = ((RDFProperty) slot).getJenaResource();
    }
    else {
      SlotPropertyMapping map = slot2Mapping(slot);
      if (map == null) {
        throw new UnsupportedOperationException("setting slot " + slot + " not implemented yet");
      }
      property = map.getProperty();
      inverted = map.isInverted();
    }

    for (Object value : values) {
      if  (inverted) {
        ((RDFResource) value).getJenaResource().addProperty(property, subject);
      }
      else {
        subject.addProperty(property, ((RDFObject) value).getJenaResource());
      }
    }
  }

  public boolean beginTransaction(String name) {
    int nesting = getTransactionNesting();
    if (nesting == 0) {
      model.begin();
    }
    transactionNesting.put(ServerFrameStore.getCurrentSession(), nesting + 1);
    return true;
  }

  public void close() {
    model.close();
  }

  public boolean commitTransaction() {
    int  nesting = getTransactionNesting();
    if (nesting == 1) {
      model.commit();
    }
    if (nesting > 0) {
      transactionNesting.put(ServerFrameStore.getCurrentSession(), nesting - 1);
    }
    return true;
  }
  
  public boolean rollbackTransaction() {
    int  nesting = getTransactionNesting();
    if (nesting == 1) {
      model.abort();
    }
    if (nesting > 0) {
      transactionNesting.put(ServerFrameStore.getCurrentSession(), nesting - 1);
    }
    return true;
  }

  public void deleteFrame(Frame frame) {
    
    // TODO Auto-generated method stub
  }

  public Set executeQuery(Query query) {
    // TODO Auto-generated method stub
    return null;
  }

  public Set getClosure(Frame frame, Slot slot, Facet facet, boolean isTemplate) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getClsCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  public NarrowFrameStore getDelegate() {
    return null;
  }

  public int getFacetCount() {
    return 0;
  }

  public Frame getFrame(FrameID id) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getFrameCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  public Set<Frame> getFrames() {
    // TODO Auto-generated method stub
    return null;
  }

  public Set<Frame> getFrames(Slot slot, Facet facet, boolean isTemplate,
                              Object value) {
    // TODO Auto-generated method stub
    return null;
  }

  public Set<Frame> getFramesWithAnyValue(Slot slot, Facet facet,
                                          boolean isTemplate) {
    // TODO Auto-generated method stub
    return null;
  }

  public Set<Frame> getMatchingFrames(Slot slot, Facet facet,
                                      boolean isTemplate, String value,
                                      int maxMatches) {
    // TODO Auto-generated method stub
    return null;
  }

  public Set<Reference> getMatchingReferences(String value, int maxMatches) {
    // TODO Auto-generated method stub
    return null;
  }

  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  public Set<Reference> getReferences(Object value) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getSimpleInstanceCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getSlotCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  public List getValues(Frame frame, Slot slot, Facet facet, boolean isTemplate) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getValuesCount(Frame frame, Slot slot, Facet facet,
                            boolean isTemplate) {
    // TODO Auto-generated method stub
    return 0;
  }

  public void moveValue(Frame frame, Slot slot, Facet facet,
                        boolean isTemplate, int from, int to) {
    // TODO Auto-generated method stub

  }

  public void removeValue(Frame frame, Slot slot, Facet facet,
                          boolean isTemplate, Object value) {
    // TODO Auto-generated method stub

  }

  public void replaceFrame(Frame frame) {
    // TODO Auto-generated method stub

  }



  public void setName(String name) {
    // TODO Auto-generated method stub

  }

  public void setValues(Frame frame, Slot slot, Facet facet,
                        boolean isTemplate, Collection values) {
    // TODO Auto-generated method stub

  }

}
