package org.eclipse.ui.internal.dialogs;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.internal.model.AdaptableList;
import org.eclipse.ui.internal.registry.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.*;

/**
 * The export wizard allows the user to choose which nested export wizard to run.
 * The set of available wizards comes from the export wizard extension point.
 */
public class ExportWizard extends Wizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	

	//the list selection page
	class SelectionPage extends WorkbenchWizardListSelectionPage {
		SelectionPage(IWorkbench w, IStructuredSelection ss, AdaptableList e, String s) {
			super(w, ss, e, s);
		}
		protected IWizardNode createWizardNode(WorkbenchWizardElement element) {
			return new WorkbenchWizardNode(this, element) {
				public IWorkbenchWizard createWizard() throws CoreException {
					return (IWorkbenchWizard)wizardElement.createExecutableExtension();
				}
			};
		}
	}
/**
 * Creates the wizard's pages lazily.
 */
public void addPages() {
	addPage(
		new SelectionPage(
			this.workbench, 
			this.selection, 
			getAvailableExportWizards(), 
			WorkbenchMessages.getString("ExportWizard.selectDestination")));  //$NON-NLS-1$
}
/**
 * Returns the export wizards that are available for invocation.
 */
protected AdaptableList getAvailableExportWizards() {
	return new WizardsRegistryReader(IWorkbenchConstants.PL_EXPORT).getWizards();
}
/**
 * Initializes the wizard.
 */
public void init(IWorkbench aWorkbench,IStructuredSelection currentSelection) {
	this.workbench = aWorkbench;
	this.selection = currentSelection;
	
	setWindowTitle(WorkbenchMessages.getString("ExportWizard.title")); //$NON-NLS-1$
	setDefaultPageImageDescriptor(WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_EXPORT_WIZ));
	setNeedsProgressMonitor(true);
}
/**
 * Subclasses must implement this <code>IWizard</code> method 
 * to perform any special finish processing for their wizard.
 */
public boolean performFinish() {
	((SelectionPage)getPages()[0]).saveWidgetValues();
	return true;
}
}
