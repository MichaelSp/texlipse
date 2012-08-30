package net.sourceforge.texlipse.bibeditor;

import java.util.List;

import net.sourceforge.texlipse.bibparser.BibOutlineContainer;
import net.sourceforge.texlipse.model.ReferenceEntry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BibEditor extends FormEditor implements IPageChangedListener, IResourceChangeListener {

	private static final Logger log = LoggerFactory.getLogger(BibEditor.class);
	public final static String ID = BibEditor.class.getName();
	private BibViewEditor viewEditor;
	private BibTextEditor textEditor;
	private BibDocumentModel documentModel;
	private boolean dirtyFlag;

	public BibEditor() {
		textEditor = new BibTextEditor(this);
		viewEditor = new BibViewEditor(this, Messages.BibEditor_id, Messages.BibViewEditor_title);
	}

	@Override
	protected void addPages() {

		this.documentModel = new BibDocumentModel(this);
		
		addPageChangedListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
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
	public boolean isDirty() {
		return textEditor.isDirty();
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

	public BibTextEditor getTextEditor() {
		return textEditor;
	}

	public void updateViewEditor() {
		viewEditor.update(documentModel.getReferenceList());
	}

	public void pageChanged(PageChangedEvent evt) {
		if (evt.getSelectedPage() == viewEditor)
			updateViewEditor();
	}
	
	public IDocumentProvider getDocumentProvider() {
		return textEditor.getDocumentProvider();
	}

	public IProject getProject() {
		return textEditor.getProject();
	}

	public void updateEntryList(List<ReferenceEntry> entryList) {
		textEditor.updateOutlinePage(entryList);
		textEditor.updateCodeFolder(entryList);
		viewEditor.update(entryList);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		class ChangedResourceDeltaVisitor implements IResourceDeltaVisitor {

			public boolean visit(IResourceDelta delta) throws CoreException {
				if (delta.getKind()!= IResourceChangeEvent.POST_CHANGE)
					return false;
				
				log.debug("visit " + delta.getResource()+ " - " + getEditorInput());
				documentModel.update();
				return true;
			}
		};

		try {
			ChangedResourceDeltaVisitor visitor = new ChangedResourceDeltaVisitor();
			event.getDelta().accept(visitor);
		} catch (CoreException ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	public BibDocumentModel getDocumentModel() {
		return documentModel;
	}

	public void update() {
		documentModel.update();
	}
}
