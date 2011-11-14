package edu.stanford.smi.protegex.owl.swrl.portability;

import java.net.URI;

// TODO: kill this
public class IRI 
{
	private String full;	
	
	public IRI(String uri) { full = uri; }
	public IRI(URI uri) { full = uri.toString();	}
  public String toString() { return full; }
}
