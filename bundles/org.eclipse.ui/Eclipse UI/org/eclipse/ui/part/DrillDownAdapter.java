package org.eclipse.ui.part;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.util.*;
import java.util.List;

/**
 * Implements a simple web style navigation metaphor for a <code>TreeViewer</code>.  
 * Home, back, and "drill into" functions are supported for the viewer,
 * <p>
 * To use the <code>DrillDownAdapter</code> ..
 * </p>
 * <ul>
 * <li>Create an instance of <code>TreeViewer</code>. </li>
 * <li>Create a <code>DrillDownAdapter</code> for the viewer. </li>
 * <li>Create a container for your viewer with a toolbar or a popup menu.
 *		Add actions for "goBack", "goHome", and "goInto" to either one by calling
 *		</code>addNavigationActions</code> with the popup menu or toolbar.</li>
 * </ol>
 * <p>
 * If the input for the underlying viewer is changed by something other than the 
 * adapter the <code>reset</code> method should be called.  This will clear
 * the drill stack and update the navigation buttons to reflect the new 
 * state of the underlying viewer.
 * </p>
 * </p>
 */
public class DrillDownAdapter implements ISelectionChangedListener 
{
	private TreeViewer fChildTree;
	private DrillStack fDrillStack;
	private Action homeAction;
	private Action backAction;
	private Action forwardAction;
/**
 * Allocates a new DrillDownTreePart.
 *
 * @param tree the target tree for refocusing
 */
public DrillDownAdapter(TreeViewer tree) {
	fDrillStack = new DrillStack();
	fChildTree = tree;
}
/**
 * Adds actions for "go back", "go home", and "go into" to a menu manager.
 *
 * @param manager is the target manager to update
 */
public void addNavigationActions(IMenuManager manager) {
	createActions();
	manager.add(homeAction);
	manager.add(backAction);
	manager.add(forwardAction);
	updateNavigationButtons();
}
/**
 * Adds actions for "go back", "go home", and "go into" to a tool bar manager.
 *
 * @param manager is the target manager to update
 */
public void addNavigationActions(IToolBarManager toolBar) {
	createActions();
	toolBar.add(homeAction);
	toolBar.add(backAction);
	toolBar.add(forwardAction);
	updateNavigationButtons();
}
/**
 * Returns whether expansion is possible for the current selection.  This
 * will only be true if it has children.
 *
 * @param element the object to test for expansion
 * @return <code>true</code> if expansion is possible; otherwise 
 *		return <code>false</code
 */
public boolean canExpand(IAdaptable element) {
	return fChildTree.isExpandable(element);
}
/**
 * Returns whether "go back" is possible for child tree.  This is only possible 
 * if the client has performed one or more drilling operations.
 *
 * @return <code>true</code> if "go back" is possible; <code>false</code> otherwise
 */
public boolean canGoBack() {
	return fDrillStack.canGoBack();
}
/**
 * Returns whether "go home" is possible for child tree.  This is only possible 
 * if the client has performed one or more drilling operations.
 *
 * @return <code>true</code> if "go home" is possible; <code>false</code> otherwise
 */
public boolean canGoHome() {
	return fDrillStack.canGoHome();
}
/**
 * Returns whether "go into" is possible for child tree.  This is only possible 
 * if the current selection in the client has one item and it has children.
 *
 * @return <code>true</code> if "go into" is possible; <code>false</code> otherwise
 */
public boolean canGoInto() {
	IStructuredSelection oSelection = (IStructuredSelection) fChildTree.getSelection();
	if (oSelection == null || oSelection.size() != 1)
		return false;
	IAdaptable anElement = (IAdaptable) oSelection.getFirstElement();
	return canExpand(anElement);
}
/**
 * Create the actions for navigation.
 *
 * @param tree the target tree for refocusing
 */
private void createActions() {
	// Only do this once.
	if (homeAction != null)
		return;

	// Home.	
	homeAction = new Action(WorkbenchMessages.getString("GoHome.text")) { //$NON-NLS-1$
		public void run() {
			goHome();
		}
	};
	homeAction.setToolTipText(WorkbenchMessages.getString("GoHome.toolTip")); //$NON-NLS-1$
	ImageDescriptor image = WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_CTOOL_HOME_NAV);
	homeAction.setImageDescriptor(image);
	homeAction.setHoverImageDescriptor(image);

	// Back.
	backAction = new Action(WorkbenchMessages.getString("GoBack.text")) { //$NON-NLS-1$
		public void run() {
			goBack();
		}
	};
	backAction.setToolTipText(WorkbenchMessages.getString("GoBack.toolTip")); //$NON-NLS-1$
	image = WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_CTOOL_BACKWARD_NAV);
	backAction.setImageDescriptor(image);
	backAction.setHoverImageDescriptor(image);

	// Forward.
	forwardAction = new Action(WorkbenchMessages.getString("GoInto.text")) { //$NON-NLS-1$
		public void run() {
			goInto();
		}
	};
	forwardAction.setToolTipText(WorkbenchMessages.getString("GoInto.toolTip")); //$NON-NLS-1$
	image = WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_CTOOL_FORWARD_NAV);
	forwardAction.setImageDescriptor(image);
	forwardAction.setHoverImageDescriptor(image);

	// Update the buttons when a selection change occurs.
	fChildTree.addSelectionChangedListener(this);
	updateNavigationButtons();
}
/**
 * Expands the given items in the tree.  The list of items passed should be
 * derived by calling <code>getExpanded</code>.
 *
 * @param items is a list of items within the tree which should be expanded
 */
private void expand(List items) { 
	fChildTree.setExpandedElements(items.toArray()); 
}
/**
 * Returns a list of elements corresponding to expanded nodes in 
 * child tree.
 *
 * @return a list of expandd elements
 */
private List getExpanded() {
	return Arrays.asList(fChildTree.getExpandedElements());
}
/**
 * Reverts the input for the tree back to the state when <code>goInto</code>
 * was last called.
 * <p>
 * A frame is removed from the drill stack.  Then that frame is used to reset the
 * input and expansion state for the child tree.
 * </p>
 */
public void goBack() {
	IAdaptable currentInput = (IAdaptable) fChildTree.getInput();
	DrillFrame oFrame = fDrillStack.goBack();
	IAdaptable input = oFrame.getElement();
	fChildTree.setInput(input);
	expand(oFrame.getExpansion());
	// if there was a selection, it should have been preserved,
	// but if not, select the element that was drilled into
	if (fChildTree.getSelection().isEmpty())
		fChildTree.setSelection(new StructuredSelection(currentInput), true);
	updateNavigationButtons();
}
/**
 * Reverts the input for the tree back to the state when the adapter was
 * created.
 * <p>
 * All of the frames are removed from the drill stack.  Then the oldest frame is 
 * used to reset the input and expansion state for the child tree.
 * </p>
 */
public void goHome() {
	IAdaptable currentInput = (IAdaptable) fChildTree.getInput();
	DrillFrame oFrame = fDrillStack.goHome();
	IAdaptable input = oFrame.getElement();
	fChildTree.setInput(input);
	expand(oFrame.getExpansion());
	// if there was a selection, it should have been preserved,
	// but if not, select the element that was last drilled into
	if (fChildTree.getSelection().isEmpty())
		fChildTree.setSelection(new StructuredSelection(currentInput), true);
	updateNavigationButtons();
}
/**
 * Sets the input for the tree to the current selection.
 * <p>
 * The current input and expansion state are saved in a frame and added to the 
 * drill stack.  Then the input for the tree is changed to be the current selection.  
 * The expansion state for the tree is maintained during the operation.
 * </p><p>
 * On return the client may revert back to the previous state by invoking 
 * <code>goBack</code> or <code>goHome</code>.
 * </p>
 */
public void goInto() {
	IStructuredSelection sel = (IStructuredSelection) fChildTree.getSelection();
	IAdaptable element = (IAdaptable) sel.getFirstElement();
	goInto(element);
}
/**
 * Sets the input for the tree to a particular item in the tree.
 * <p>
 * The current input and expansion state are saved in a frame and added to the 
 * drill stack.  Then the input for the tree is changed to be <code>newInput</code>.  
 * The expansion state for the tree is maintained during the operation.
 * </p><p>
 * On return the client may revert back to the previous state by invoking 
 * <code>goBack</code> or <code>goHome</code>.
 * </p>
 *
 * @param newInput the new input element
 */
public void goInto(IAdaptable newInput) {
	// If we can drill ..
	if (canExpand(newInput)) 
	{
		// Save the old state.
		IAdaptable oldInput = (IAdaptable)fChildTree.getInput();
		List expandedList = getExpanded();
		fDrillStack.add(new DrillFrame(oldInput, "null", expandedList));//$NON-NLS-1$
		
		// Install the new state.
		fChildTree.setInput(newInput);
		expand(expandedList);
		updateNavigationButtons();
	}
}
/**
 * Resets the drill down adapter. 
 * <p>
 * This method is typically called when the input for the underlying view
 * is reset by something other than the adapter.
 * On return the drill stack has been cleared and the navigation buttons
 * reflect the new state of the underlying viewer.
 * </p>
 */
public void reset() {
	fDrillStack.reset();
	updateNavigationButtons();
}
/**
 * Updates the navigation buttons when a selection change occurs
 * in the tree.
 */
public void selectionChanged(SelectionChangedEvent event) {
	updateNavigationButtons();
}
/**
 * Updates the enabled state for each navigation button.  
 */
protected void updateNavigationButtons() {
	if (homeAction != null) {
		homeAction.setEnabled(canGoHome());
		backAction.setEnabled(canGoBack());
		forwardAction.setEnabled(canGoInto());
	}
}
}
