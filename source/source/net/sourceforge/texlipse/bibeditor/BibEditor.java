package net.sourceforge.texlipse.bibeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class BibEditor extends FormEditor {

	public final static String ID = "net.sourceforge.texlipse.bibeditor.BibEditor";
	private BibViewEditor viewEditor;
	private BibTextEditor textEditor;

	public BibEditor() {
		textEditor = new BibTextEditor(this);
		viewEditor = new BibViewEditor(this, Messages.BibEditor_id, Messages.BibViewEditor_title);
	}

	@Override
	protected void addPages() {
		try {
			addPage(0, viewEditor);
			int pageIndex = addPage(textEditor, getEditorInput());
			setPageText(pageIndex, Messages.BibSourceEditor_title);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		textEditor.doSaveAs();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public BibDocumentModel getDocumentModel() {
		return textEditor.getDocumentModel();
	}

	public BibTextEditor getTextEditor() {
		return textEditor;
	}

	public void updateViewEditor() {
		viewEditor.update(textEditor.getDocumentModel());
	}
}
