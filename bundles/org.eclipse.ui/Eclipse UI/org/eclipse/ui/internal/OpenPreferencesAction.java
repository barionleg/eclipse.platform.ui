package org.eclipse.ui.internal;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.help.*;

/**
 * Open the preferences dialog
 */
public class OpenPreferencesAction extends Action {
	protected IWorkbenchWindow window;
/**
 * Create a new <code>OpenPreferenceAction</code> and initialize it 
 * from the given resource bundle.
 */
public OpenPreferencesAction(IWorkbenchWindow window) {
	super(WorkbenchMessages.getString("OpenPreferences.text")); //$NON-NLS-1$
	this.window = window;
	setToolTipText(WorkbenchMessages.getString("OpenPreferences.toolTip")); //$NON-NLS-1$
}
/**
 * Perform the action: open the preference dialog.
 */
public void run() {
	PreferenceManager pm = WorkbenchPlugin.getDefault().getPreferenceManager();
	
	if (pm != null) {
		PreferenceDialog d = new PreferenceDialog(window.getShell(), pm);
		d.create();
		WorkbenchHelp.setHelp(d.getShell(), new Object[]{IHelpContextIds.PREFERENCE_DIALOG});
		d.open();	
	}
}
}
