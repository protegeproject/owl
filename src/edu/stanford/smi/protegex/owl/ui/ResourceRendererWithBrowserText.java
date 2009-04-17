package edu.stanford.smi.protegex.owl.ui;

import javax.swing.Icon;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameWithBrowserTextRenderer;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class ResourceRendererWithBrowserText extends
		FrameWithBrowserTextRenderer {
	
	private static final long serialVersionUID = 5885951703279077675L;

	@Override
	protected Icon getFbtIcon(FrameWithBrowserText fbt) {
		String iconName = fbt.getIconName();
		Frame frame = fbt.getFrame();
		if (iconName != null) {			
			return (frame.getKnowledgeBase() instanceof OWLModel) ?
					frame.isEditable() ? OWLIcons.getImageIcon(iconName) : OWLIcons.getReadOnlyClsIcon(iconName) :
					super.getFbtIcon(fbt);
		} else {
			return null;
		}
	}
}
