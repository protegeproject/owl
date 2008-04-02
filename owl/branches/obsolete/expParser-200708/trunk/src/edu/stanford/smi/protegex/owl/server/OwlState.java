package edu.stanford.smi.protegex.owl.server;


import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.server.framestore.background.ServerCachedState;



public enum OwlState implements ServerCachedState {
  Start, SubClass, OwlExpr, RDFList, End;
  
  public boolean entryCondition(FrameStore fs, Frame f) {
    switch (this) {
    case OwlExpr:
      return isOWLAnonymous(fs, f);
    case SubClass:
      return !isOWLAnonymous(fs, f);
    default:
      return true;
    }
  }
  
  private boolean isOWLAnonymous(FrameStore fs, Frame f) {
    return fs.getFrameName(f).startsWith("@");
  }
}
