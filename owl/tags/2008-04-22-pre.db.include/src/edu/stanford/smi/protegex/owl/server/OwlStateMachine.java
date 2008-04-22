package edu.stanford.smi.protegex.owl.server;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.server.framestore.background.ServerCacheStateMachine;
import edu.stanford.smi.protege.server.framestore.background.ServerCachedState;
import edu.stanford.smi.protege.util.Log;

/*
 * TODO - this class must be made pluggable and this implementation goes with the OWL stuff.
 * 
 */
public class OwlStateMachine implements ServerCacheStateMachine {
  private static transient Logger log = Log.getLogger(OwlStateMachine.class);
  
  private FrameStore fs;
  private final Object kbLock;
  
  private Map<StateAndSlot, OwlState> transitionMap = new HashMap<StateAndSlot, OwlState>();
  
  

  public OwlStateMachine(FrameStore fs, Object kbLock) {
    this.fs = fs;
    this.kbLock = kbLock;
    synchronized (kbLock) {
      addTransition(OwlState.Start, Model.Slot.DIRECT_SUPERCLASSES, OwlState.OwlExpr);
      
      addTransition(OwlState.Start, "owl:equivalentClass", OwlState.OwlExpr);
      
      
      addTransition(OwlState.OwlExpr, "owl:intersectionOf", OwlState.RDFList);
      addTransition(OwlState.OwlExpr, Model.Slot.DIRECT_SUPERCLASSES, OwlState.End);
      addTransition(OwlState.OwlExpr, "owl:someValuesFrom", OwlState.End);
      
      addTransition(OwlState.RDFList, "rdf:rest", OwlState.RDFList);
      addTransition(OwlState.RDFList, "rdf:first", OwlState.OwlExpr);
      
      addTransition(OwlState.Start, Model.Slot.DIRECT_INSTANCES, OwlState.UserIndividual);

    }
  }
  
  private void addTransition(OwlState start, String slotName, OwlState end) {
    Slot slot = null;
    try {
      Frame sframe = fs.getFrame(slotName);
      if (sframe == null || !(sframe instanceof Slot)) {
        if (log.isLoggable(Level.FINE)) {
          log.fine("frame found for transition " + 
              start + ", " + slotName + "/" + sframe + " -> " + end + " but not a slot");
        }
        return;
      }
      slot = (Slot) sframe;
      transitionMap.put(new StateAndSlot(start, slot), end);
    } catch (Exception e) {
      if (log.isLoggable(Level.FINE)) {
        log.fine("Exception caught creating transition " + 
            start + ", " + slotName+ " -> " + end + ": " + e);
        log.log(Level.FINER, "Exception = ", e);
      }
    }
  }
  
  public ServerCachedState getInitialState() {
      return OwlState.Start;
  }
  
  public OwlState nextState(ServerCachedState state, Frame beginningFrame, Slot slot, Frame endingFrame) {
    if (!(state instanceof OwlState)) {
        return null;
    }
    OwlState endState = transitionMap.get(new StateAndSlot((OwlState) state, slot));
    synchronized (kbLock) {
      if (endState != null && OwlState.allowTransition(fs, (OwlState) state, beginningFrame,  endState, endingFrame)) {
        return endState;
      }
    }
    return null;
  }

  private class StateAndSlot {
    private OwlState state;
    private Slot slot;
    
    public Slot getSlot() {
      return slot;
    }

    public OwlState getState() {
      return state;
    }

    public StateAndSlot(OwlState state, Slot slot) {
      this.state = state;
      this.slot = slot;
    }
    
    public int hashCode() {
      return state.ordinal() + slot.hashCode();
    }
    
    public boolean equals(Object o) {
      if (o == null || !(o instanceof StateAndSlot)) {
        return false;
      }
      StateAndSlot other = (StateAndSlot) o;
      return other.state.equals(state) && other.slot.equals(slot);
    }
    
  }
}
