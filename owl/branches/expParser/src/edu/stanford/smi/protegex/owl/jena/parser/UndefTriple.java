package edu.stanford.smi.protegex.owl.jena.parser;

import java.net.URI;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

public class UndefTriple {

	private AResource tripleSubj;
	private AResource triplePred;
	private AResource tripleObj;
	private String undef;
	//create another class!!!
	private ALiteral tripleLiteral;

	public UndefTriple(AResource subj, AResource pred, AResource obj, String undef) {
		this.tripleSubj = subj;
		this.triplePred = pred;
		this.tripleObj = obj;
		this.undef = undef;
	}

	public UndefTriple(AResource subj, AResource pred, ALiteral lit, String frameName) {
		this.tripleSubj = subj;
		this.triplePred = pred;
		this.tripleLiteral = lit;
		this.undef = undef;
	}

	public AResource getTripleObj() {
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
		
		boolean equalsPartial1 = TripleFrameCache.getResourceName(((UndefTriple)obj).getTripleSubj()).equals(TripleFrameCache.getResourceName(getTripleSubj())) &&
		TripleFrameCache.getResourceName(((UndefTriple)obj).getTriplePred()).equals(TripleFrameCache.getResourceName(getTriplePred())) &&		
		((UndefTriple)obj).getUndef().equals(getUndef());
		
		boolean equalsPartial2 = (this.tripleObj == null) ?   ((UndefTriple)obj).getTripleLiteral().equals(getTripleLiteral())
				: TripleFrameCache.getResourceName(((UndefTriple)obj).getTripleObj()).equals(TripleFrameCache.getResourceName(getTripleObj()));
		
		return equalsPartial1 && equalsPartial2;
	}
	
	@Override
	public String toString() {
		return "(" + getTripleSubj() + "  " + getTriplePred() + "  " + getTripleObj() + ")" + "  undef: " + getUndef();
	}

	public ALiteral getTripleLiteral() {
		return tripleLiteral;
	}

}
