
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.BuiltInArgumentImpl;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;

public class P3SWRLLiteralArgumentReference extends BuiltInArgumentImpl implements SWRLLiteralArgumentReference
{
	private DataValue dataValue;

	public P3SWRLLiteralArgumentReference(DataValue dataValue)
	{
		this.dataValue = dataValue;
	}

	public DataValue getLiteral()
	{
		return dataValue;
	}

	public String toString()
	{
		return dataValue.toString();
	}

	// TODO: fix
	public int compareTo(BuiltInArgument argument)
	{
		return dataValue.compareTo(((SWRLLiteralArgumentReference)argument).getLiteral());
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3SWRLLiteralArgumentReference impl = (P3SWRLLiteralArgumentReference)obj;
		return (getLiteral() == impl.getLiteral() || (getLiteral() != null && getLiteral().equals(impl.getLiteral())));
	} 

	public int hashCode()
	{
		int hash = 12;
		hash = hash + (null == getLiteral() ? 0 : getLiteral().hashCode());
		return hash;
	} 
}
