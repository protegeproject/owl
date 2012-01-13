package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.bridge.ArgumentFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.CollectionArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.DataPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ObjectPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLIndividualArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLVariableReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3SWRLIndividualArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3SWRLVariableReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.DataValueImpl;

public class ArgumentFactoryImpl extends ArgumentFactory
{
	public SWRLVariableReference createVariableArgument(String variableName)
	{
		return new P3SWRLVariableReference(variableName);
	}

	public ClassArgument createClassArgument(String classURI)
	{
		return new ClassArgumentImpl(classURI);
	}

	public ObjectPropertyArgument createObjectPropertyArgument(String propertyURI)
	{
		return new ObjectPropertyArgumentImpl(propertyURI);
	}

	public DataPropertyArgument createDataPropertyArgument(String propertyURI)
	{
		return new DataPropertyArgumentImpl(propertyURI);
	}

	public SWRLIndividualArgumentReference createIndividualArgument(String individualURI)
	{
		return new P3SWRLIndividualArgumentReference(individualURI);
	}

	public SWRLLiteralArgumentReference createDataValueArgument(DataValue dataValue)
	{
		return new P3SWRLLiteralArgumentReference(dataValue);
	}

	public SWRLLiteralArgumentReference createDataValueArgument(String s)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(s));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(boolean b)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(b));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(Boolean b)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(b));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(int i)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(i));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(long l)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(l));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(float f)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(f));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(double d)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(d));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(short s)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(s));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(Byte b)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(b));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(XSDType xsd)
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(xsd));
	}

	public SWRLLiteralArgumentReference createDataValueArgument(Object o) throws DataValueConversionException
	{
		return new P3SWRLLiteralArgumentReference(new DataValueImpl(o));
	}

	public MultiArgument createMultiArgument()
	{
		return new MultiArgumentImpl();
	}

	public MultiArgument createMultiArgument(List<BuiltInArgument> arguments)
	{
		return new MultiArgumentImpl(arguments);
	}

	public CollectionArgument createCollectionArgument(String collectionName, String collectionGroupID)
	{
		return new CollectionArgumentImpl(collectionName, collectionGroupID);
	}
}
