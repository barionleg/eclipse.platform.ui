package org.eclipse.ui.help;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.help.IContext;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.widgets.Control;
import java.util.*;

/**
 * For determining the help context for controls in a wizard page.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 */ 
public class DialogPageContextComputer implements IContextComputer {
	private IDialogPage page;
	private ArrayList contextList;
	private Object context;
/**
 * Creates a new context computer for the given dialog page and help context.
 *
 * @param page the dialog page
 * @param helpContext a single help context id (type <code>String</code>) or
 *  help context object (type <code>IContext</code>)
 */
public DialogPageContextComputer(IDialogPage page, Object helpContext) {
	Assert.isTrue(helpContext instanceof String || helpContext instanceof IContext);
	this.page = page;
	context = helpContext;
}
/**
 * Add the contexts to the context list.
 *
 * @param object the contexts (<code>Object[]</code> or <code>IContextComputer</code>)
 * @param event the help event 
 */
private void addContexts(Object object, HelpEvent event) {
	Assert.isTrue(object instanceof Object[] || object instanceof IContextComputer);
	Object[] contexts;
	if (object instanceof IContextComputer) 
		// get local contexts
		contexts = ((IContextComputer)object).getLocalContexts(event);
	else
		contexts = (Object[])object;

	// copy the contexts into our list	
	for (int i = 0; i < contexts.length; i++) 
		contextList.add(contexts[i]); 
}
/**
 * Add the contexts for the given control to the context list.
 *
 * @param event the control from which to obtain the contexts
 * @param event the help event 
 */
private void addContextsForControl(Control control, HelpEvent event) {
	// See if there is are help contexts on the control
	Object object = WorkbenchHelp.getHelp(control);

	if (object == null || object == this)
		// We need to check for this in order to avoid recursion
		return;
		
	addContexts(object, event); 
}
/* (non-Javadoc)
 * Method declared on IContextComputer.
 */
public Object[] computeContexts(HelpEvent event) {
	contextList = new ArrayList();
	
	// Add the local context
	contextList.add(context);
	
	// Add the contexts for the page
	addContextsForControl(page.getControl(), event);
	
	// Add the contexts for the container shell	
	addContextsForControl(page.getControl().getShell(), event); 

	// Return the contexts
	return contextList.toArray();
}
/**
 * Returns the context set on this page.
 *
 * @return the context set on this page. (type <code>String</code>) or
 *  help context object (type <code>IContext</code>)
 */
private Object getContext() {
	return context;
}
/* (non-Javadoc)
 * Method declared on IContextComputer.
 */
public Object[] getLocalContexts(HelpEvent event) {
	return new Object[] {context};
}
}
