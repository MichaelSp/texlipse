package net.sourceforge.texlipse.bibeditor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.sourceforge.texlipse.bibeditor.utils.ElementValueProvider;
import net.sourceforge.texlipse.bibeditor.utils.ListEditorComposite;
import net.sourceforge.texlipse.bibeditor.utils.ListEditorContentProvider;
import net.sourceforge.texlipse.model.ReferenceEntry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class BibViewEditor extends FormPage {
	public final static String ID = "net.sourceforge.texlipse.bibeditor.BibEditor";
	private static final String VALUE_PROVIDER = "ValueProvider";
	private static final String MODIFY_LISTENER = "ModifyListener";
	private final FormEditor bibEditor;
	private Section referencesSection;
	private GridData referencesSectionData;
	private ListEditorComposite<ReferenceEntry> propertiesEditor;
	private ListEditorComposite<ReferenceEntry> entryEditor;
	private List<ReferenceEntry> entrys;
	private Section detailSection;
	private GridData detailSectionData;
	private ReferenceEntry currentReference;
	private Text authorsText;
	private Text jornalText;
	private Text bibkeyText;
	private Text referenceUrlText;
	private Text yearText;

	public BibViewEditor(FormEditor editor, String id, String title) {
		super(editor, id, title);
		bibEditor = editor;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());

		Composite body = createBody(toolkit, form);
		Composite leftComposite = createComposite(toolkit, body);
		Composite rightComposite = createComposite(toolkit, body);

		createReferencesSection(toolkit, leftComposite);
		createDetailSection(toolkit, rightComposite);
	}

	private Composite createBody(FormToolkit toolkit, ScrolledForm form) {
		Composite body = form.getBody();
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.horizontalSpacing = 7;
		body.setLayout(gridLayout);
		toolkit.paintBordersFor(body);
		return body;
	}

	private Composite createComposite(FormToolkit toolkit, Composite body) {
		Composite composite = toolkit.createComposite(body, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginWidth = 0;
		compositeLayout.marginHeight = 0;
		composite.setLayout(compositeLayout);
		return composite;
	}

	private void createDetailSection(FormToolkit toolkit, Composite composite) {
		detailSection = toolkit.createSection(composite, defaultSectionStyle());
		detailSectionData = new GridData(SWT.FILL, SWT.FILL, true, true);
		detailSection.setLayoutData(detailSectionData);
		detailSection.setText(Messages.BibViewEditor_section_detail);
		detailSection.setData("name", "detailSection"); //$NON-NLS-1$ //$NON-NLS-2$
		detailSection.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				detailSectionData.grabExcessVerticalSpace = e.getState();
				detailSection.getParent().layout();
			}
		});

		Composite projectComposite = toolkit.createComposite(detailSection, SWT.NONE);
		projectComposite.setLayout(new GridLayout(2, false));
		detailSection.setClient(projectComposite);

		bibkeyText = createTextElement(toolkit, projectComposite, Messages.DetailPage_lblBibkey);

		Hyperlink urlLabel = toolkit.createHyperlink(projectComposite, Messages.DetailPage_lblUrl, SWT.NONE);
		urlLabel.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				openHyperlink(referenceUrlText.getText());
			}
		});

		referenceUrlText = createTextElement(toolkit, projectComposite, "");
		yearText = createTextElement(toolkit, projectComposite, Messages.DetailPage_lblYear);
		authorsText = createTextElement(toolkit, projectComposite, Messages.DetailPage_lblAuthors);
		jornalText = createTextElement(toolkit, projectComposite, Messages.DetailPage_lblJurnal);

		toolkit.paintBordersFor(projectComposite);
		projectComposite.setTabList(new Control[] { bibkeyText, referenceUrlText, yearText });

	}

	protected void openHyperlink(String url) {
		if (!url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
			url = url.trim();
			try {
				IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.NAVIGATION_BAR
						| IWorkbenchBrowserSupport.LOCATION_BAR, url, url, url);
				browser.openURL(new URL(url));
			} catch (PartInitException ex) {
				// TODO: log.error(ex.getMessage(), ex);
			} catch (MalformedURLException ex) {
				// TODO: log.error("Malformed url " + url, ex);
			}
		}
	}

	private Text createTextElement(FormToolkit toolkit, Composite projectComposite, String lblText) {
		if (!lblText.isEmpty())
			toolkit.createLabel(projectComposite, lblText, SWT.NONE);
		Text elem = toolkit.createText(projectComposite, null, SWT.NONE);
		GridData gd_Text = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_Text.widthHint = 150;
		elem.setLayoutData(gd_Text);
		elem.setData("name", "detail" + lblText); //$NON-NLS-1$ //$NON-NLS-2$
		setElementValueProvider(elem, new ElementValueProvider(lblText));
		setModifyListener(elem);
		return elem;
	}

	public void setElementValueProvider(Control control, ElementValueProvider provider) {
		control.setData(VALUE_PROVIDER, provider);
	}

	public void setModifyListener(final Control control) {
		Assert.isTrue(control instanceof CCombo || control instanceof Text || control instanceof Combo);

		ModifyListener ml = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				final ElementValueProvider provider = (ElementValueProvider) control.getData(VALUE_PROVIDER);
				if (provider == null) {
					throw new IllegalStateException("no value provider for " + control);
				}
			}
		};
		control.setData(MODIFY_LISTENER, ml);
	}

	private void createReferencesSection(FormToolkit toolkit, Composite composite) {
		referencesSection = toolkit.createSection(composite, defaultSectionStyle());
		referencesSectionData = new GridData(SWT.FILL, SWT.FILL, true, true);
		referencesSection.setLayoutData(referencesSectionData);
		referencesSection.setText(Messages.BibViewEditor_section_references);
		referencesSection.setData("name", "section"); //$NON-NLS-1$ //$NON-NLS-2$
		referencesSection.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				referencesSectionData.grabExcessVerticalSpace = e.getState();
				referencesSection.getParent().layout();
			}
		});

		Composite referencesComposite = toolkit.createComposite(referencesSection, SWT.NONE);
		referencesComposite.setLayout(new GridLayout(2, false));
		referencesSection.setClient(referencesComposite);

		entryEditor = new ListEditorComposite<ReferenceEntry>(referencesSection, SWT.NONE);
		referencesSection.setClient(entryEditor);
		entryEditor.getViewer().getTable().setData("name", "properties"); //$NON-NLS-1$ //$NON-NLS-2$

		entryEditor.setContentProvider(new ListEditorContentProvider<ReferenceEntry>());
		entryEditor.setLabelProvider(new ReferenceLabelProvider());

		entryEditor.setCreateButtonListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewReference();
			}
		});
		entryEditor.setRemoveButtonListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteReference(entryEditor.getSelection());
			}
		});
		entryEditor.setDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				editReference(entryEditor.getSelection());
			}
		});

		if (entrys != null)
			entryEditor.setInput(entrys);

		toolkit.paintBordersFor(entryEditor);
		toolkit.adapt(entryEditor);
	}

	private int defaultSectionStyle() {
		return ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE;
	}

	protected void editReference(List<ReferenceEntry> selection) {
		if (selection.size() != 1) {
			return;
		}
		currentReference = selection.get(0);
		bibkeyText.setText(currentReference.getkey(true));
		yearText.setText(currentReference.year);
		authorsText.setText(currentReference.author);
		jornalText.setText(currentReference.journal);
		referenceUrlText.setText(currentReference.getField("url"));
	}

	protected void deleteReference(List<ReferenceEntry> selection) {
		// TODO Auto-generated method stub

	}

	private void createNewReference() {
		// TODO Auto-generated method stub

	}

	public void update(BibDocumentModel bibDocumentModel) {
		entrys = bibDocumentModel.getReferenceList();
		if (entryEditor == null) {
			return;
		} else
			entryEditor.setInput(entrys);
	}

}
