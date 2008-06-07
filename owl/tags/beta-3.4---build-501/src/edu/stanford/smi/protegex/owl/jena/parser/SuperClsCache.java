package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.Collection;
import java.util.HashSet;

import edu.stanford.smi.protege.model.Frame;

public class SuperClsCache {
	
	//maybe it should be singleton?
	
	private HashSet<Frame> framesWithNoSuperClass = new HashSet<Frame>();
	
	
	public boolean hasFrameDeclaredSuperclass(Frame frame) {
		return !framesWithNoSuperClass.contains(frame);
	}
	
	public void addFrame(Frame frame) {
		if (frame != null) {
			framesWithNoSuperClass.add(frame);
		}
	}
	
	public void removeFrame(Frame frame) {
		if (frame != null) {
			framesWithNoSuperClass.remove(frame);
		}
	}
	
	public void clearCache() {
		framesWithNoSuperClass.clear();		
	}

	public Collection<Frame> getCachedFramesWithNoSuperclass() {
		return framesWithNoSuperClass;
	}
	
}
