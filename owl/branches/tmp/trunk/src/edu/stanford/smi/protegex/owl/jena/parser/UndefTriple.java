package edu.stanford.smi.protegex.owl.jena.parser;

import java.net.URI;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

public class UndefTriple {

	private AResource tripleSubj;
	private AResource triplePred;
	private Object tripleObj;
	private String undef;
	
	
	public UndefTriple(AResource subj, AResource pred, AResource obj, String undef) {
		this.tripleSubj = subj;
		this.triplePred = pred;
		this.tripleObj = obj;
		this.undef = undef;
	}

	public UndefTriple(AResource subj, AResource pred, ALiteral obj, String undef) {
		this.tripleSubj = subj;
		this.triplePred = pred;
		this.tripleObj = obj;
		this.undef = undef;
	}

	
	public Object getTripleObj() {
		return tripleObj;
	}

	public AResource getTriplePred() {
		return triplePred;
	}

	public AResource getTripleSubj() {
		return tripleSubj;
	}

	public String getUndef() {
		return undef;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UndefTriple))
			return false;
		
		return ((UndefTriple)obj).getTripleSubj().equals(getTripleSubj()) &&
		((UndefTriple)obj).getTriplePred().equals(getTriplePred()) &&
		((UndefTriple)obj).getTripleObj().equals(getTripleObj()) &&
		((UndefTriple)obj).getUndef().equals(getUndef());
	
	}
	
	@Override
	public String toString() {
		return "(" + getTripleSubj() + "  " + getTriplePred() + "  " + getTripleObj() + ")" + "  undef: " + getUndef();
	}


}
