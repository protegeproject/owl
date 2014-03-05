package edu.stanford.smi.protegex.owl.swrl.model.impl;

import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.swrl.model.SQWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;

public class DefaultSWRLAtomList extends DefaultRDFList implements SWRLAtomList
{
	private boolean isInHead = false;

	public DefaultSWRLAtomList(KnowledgeBase kb, FrameID id)
	{
		super(kb, id);
	}

	public DefaultSWRLAtomList()
	{
	}

	@Override
	public void setInHead(boolean isInHead)
	{
		this.isInHead = isInHead;
	}

	@Override
	public String getBrowserText()
	{
		StringBuilder sb = new StringBuilder();
		boolean atomProcessed = false;
		boolean collectionBuiltInEncountered = false;
		boolean collectionOperationEncountered = false;
		int currentColumn = 0;
		final int maxColumnWidth = 120;

		if (isInHead)
			sb.append("\n");

		if (getValues() != null) {
			Iterator<?> iterator = getValues().iterator();
			while (iterator.hasNext()) {
				Instance instance = (Instance)iterator.next();
				String atomRendering = SWRLUtil.getSWRLBrowserText(instance, "ATOM");
				int atomRenderingWidth = atomRendering.length();

				if (instance instanceof SWRLBuiltinAtom) {
					SWRLBuiltin builtIn = ((SWRLBuiltinAtom)instance).getBuiltin();

					if (builtIn == null) {
						sb.append("\n<DELETED_BUILTIN>\n");
						currentColumn = 0;
					} else {
						String builtInName = builtIn.getPrefixedName();

						if (!isInHead
								&& (SQWRLNames.isSQWRLCollectionMakeBuiltIn(builtInName) || SQWRLNames
										.isSQWRLCollectionGroupByBuiltIn(builtInName)) && !collectionBuiltInEncountered) {
							collectionBuiltInEncountered = true;
							if (currentColumn + 2 >= maxColumnWidth) {
								sb.append("\n" + SWRLParser.RING_CHAR + "  ");
								currentColumn = 2;
							} else {
								sb.append(" " + SWRLParser.RING_CHAR + " \n");
								currentColumn = 0;
							}

							if ((currentColumn + atomRenderingWidth) >= maxColumnWidth) {
								sb.append("\n");
								currentColumn = atomRenderingWidth;
							} else
								currentColumn += atomRenderingWidth;
							sb.append(atomRendering);
						} else if (!isInHead && SQWRLNames.getCollectionOperationBuiltInNames().contains(builtInName)
								&& !collectionOperationEncountered && atomProcessed) {
							collectionOperationEncountered = true;
							if (currentColumn + 2 >= maxColumnWidth) {
								sb.append("\n" + SWRLParser.RING_CHAR + "  ");
								currentColumn = 3;
							} else {
								sb.append(" " + SWRLParser.RING_CHAR + " \n");
								currentColumn = 0;
							}

							if ((currentColumn + atomRenderingWidth) >= maxColumnWidth) {
								sb.append("\n");
								currentColumn = atomRenderingWidth;
							} else
								currentColumn += atomRenderingWidth;
							sb.append(atomRendering);
						} else { // Non SQWRL make/group/operation built-in
							if (atomProcessed) {
								if (currentColumn + 2 >= maxColumnWidth) {
									sb.append("\n" + SWRLParser.AND_CHAR + "  ");
									currentColumn = 2;
								} else {
									sb.append(" " + SWRLParser.AND_CHAR + " ");
									currentColumn += 3;
								}
							}

							if ((currentColumn + atomRenderingWidth) >= maxColumnWidth) {
								sb.append("\n");
								currentColumn = atomRenderingWidth;
							} else
								currentColumn += atomRenderingWidth;
							sb.append(atomRendering);
						}
					}
				} else { // Not a built-in atom
					if (atomProcessed) {
						if (currentColumn + 2 >= maxColumnWidth) {
							sb.append("\n" + SWRLParser.AND_CHAR + "  ");
							currentColumn = 2;
						} else {
							sb.append(" " + SWRLParser.AND_CHAR + " ");
							currentColumn += 3;
						}
					}

					if ((currentColumn + atomRenderingWidth) >= maxColumnWidth) {
						sb.append("\n");
						currentColumn = atomRenderingWidth;
					} else
						currentColumn += atomRenderingWidth;
					sb.append(atomRendering);
				}
				atomProcessed = true;
			}
		} else
			sb.append("<DELETED_ATOM_LIST>");

		return sb.toString();
	}

	@Override
	public void getReferencedInstances(Set<RDFResource> set)
	{
		final OWLModel owlModel = getOWLModel();
		RDFList li = this;
		while (li != null && !li.equals(owlModel.getRDFNil())) {
			set.add(li);
			Object value = li.getFirst();
			if (value instanceof SWRLIndividual) {
				SWRLIndividual individual = (SWRLIndividual)value;
				set.add(individual);
				individual.getReferencedInstances(set);
			}
			li = li.getRest();
		}
	}

	@Override
	public void accept(OWLModelVisitor visitor)
	{
		visitor.visitSWRLAtomListIndividual(this);
	}
}
