package net.sourceforge.texlipse.bibeditor;

import net.sourceforge.texlipse.model.ReferenceEntry;

import org.eclipse.jface.viewers.LabelProvider;

public class ReferenceLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof ReferenceEntry){
			ReferenceEntry ref = (ReferenceEntry) element;
			return ref.author + ", " + ref.year;
		}
		else
			return super.getText(null);
	}
}
