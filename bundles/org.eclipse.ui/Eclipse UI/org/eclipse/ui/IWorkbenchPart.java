package org.eclipse.ui;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Image;

/**
 * A workbench part is a visual component within a workbench page.  There
 * are two subtypes: view and editor, as defined by <code>IViewPart</code> and
 * <code>IEditorPart</code>.  
 * <p>
 * A view is typically used to navigate a hierarchy of information (like the 
 * workspace), open an editor, or display properties for the active editor.  
 * Modifications made in a view are saved immediately.  
 * </p><p>
 * An editor is typically used to edit or browse a document or input object. 
 * The input is identified using an <code>IEditorInput</code>.  Modifications made 
 * in an editor part follow an open-save-close lifecycle model.
 * </p><p>
 * This interface is not intended to be directly implemented or extended by
 * clients.
 * </p><p>
 * The lifecycle of a workbench part is as follows:
 * <ul>
 * 	<li>When a part extension is created:
 *    <ul>
 *		<li>instantiate the part</li>
 *		<li>create a part site</li>
 *		<li>call <code>part.init(site)</code></li>
 *		<li>add part to presentation by calling 
 *        <code>part.createControl(parent)</code> to create actual widgets</li>
 *		<li>fire <code>partOpened</code> event to all listeners</li>
 *		<li>activate the part</li>
 *	  </ul>
 *   </li>
 *  <li>When a part is activated or gets focus:
 *    <ul>
 *		<li>call <code>part.setFocus()</code></li>
 *		<li>fire <code>partActivated</code> event to all listeners</li>
 *	  </ul>
 *   </li>
 *  <li>When a part is closed:
 *    <ul>
 *		<li>if save is needed, do save; if it fails or is canceled return</li>
 *		<li>if part is active, deactivate part</li>
 *		<li>fire <code>partClosed</code> event to all listeners</li>
 *		<li>remove part from presentation; part controls are disposed as part
 *         of the SWT widget tree
 *		<li>call <code>part.dispose()</code></li>
 *	  </ul>
 *   </li>
 * </ul>
 * </p>
 * <p>
 * After <code>createPartControl</code> has been called, the implementor may 
 * safely reference the controls created.  When the part is closed 
 * these controls will be disposed as part of an SWT composite.  This
 * occurs before the <code>IWorkbenchPart.dispose</code> method is called.
 * If there is a need to free SWT resources the part should define a dispose 
 * listener for its own control and free those resources from the dispose
 * listener.  If the part invokes any method on the disposed SWT controls 
 * after this point an <code>SWTError</code> will be thrown.  
 * </p>
 * <p>
 * The last method called on <code>IWorkbenchPart</code> is <code>dispose</code>.  
 * This signals the end of the part lifecycle.
 * </p>
 * <p>
 * Workbench parts implement the <code>IAdaptable</code> interface; extensions
 * are managed by the platform's adapter manager.
 * </p>
 *
 * @see IViewPart
 * @see IEditorPart
 */
public interface IWorkbenchPart extends IAdaptable {
	
	/**
	 * The property id for <code>getTitle</code> and <code>getTitleImage</code>.
	 */
	public static final int PROP_TITLE = 0x01;
/**
 * Adds a listener for changes to properties of this workbench part.
 * Has no effect if an identical listener is already registered.
 * <p>
 * The properties ids are as follows:
 * <ul>
 *   <li><code>IWorkbenchPart.PROP_TITLE</code> </li>
 *   <li><code>IEditorPart.PROP_INPUT</code> </li>
 *   <li><code>IEditorPart.PROP_DIRTY</code> </li>
 * </ul>
 * </p>
 *
 * @param listener a property listener
 */
public void addPropertyListener(IPropertyListener listener);
/**
 * Creates the SWT controls for this workbench part.
 * <p>
 * Clients should not call this method (the workbench calls this method at
 * appropriate times).
 * </p>
 * <p>
 * For implementors this is a multi-step process:
 * <ol>
 *   <li>Create one or more controls within the parent.</li>
 *   <li>Set the parent layout as needed.</li>
 *   <li>Register any global actions with the <code>IActionService</code>.</li>
 *   <li>Register any popup menus with the <code>IActionService</code>.</li>
 *   <li>Register a selection provider with the <code>ISelectionService</code>
 *     (optional). </li>
 * </ol>
 * </p>
 *
 * @param parent the parent control
 */
public void createPartControl(Composite parent);
/**
 * Disposes of this workbench part.
 * <p>
 * This is the last method called on the <code>IWorkbenchPart</code>.  At this
 * point the part controls have been disposed as part of an SWT composite.  
 * If the part invokes any method on the disposed SWT controls an
 * <code>SWTError</code> will be thrown.
 * </p>
 * <p>
 * Within this method a part may release any resources, fonts, images, etc.&nbsp; 
 * held by this part.  It is also very important to deregister all listeners
 * from the workbench.
 * </p>
 * <p>
 * Clients should not call this method (the workbench calls this method at
 * appropriate times).
 * </p>
 */
public void dispose();
/**
 * Returns the site for this workbench part.
 *
 * @return the part site
 */
public IWorkbenchPartSite getSite();
/**
 * Returns the title of this workbench part. If this value changes 
 * the part must fire a property listener event with 
 * <code>PROP_TITLE</code>.
 * <p>
 * The title is used to populate the title bar of this part's visual
 * container.  
 * </p>
 *
 * @return the workbench part title
 */
public String getTitle();
/**
 * Returns the title image of this workbench part.  If this value changes 
 * the part must fire a property listener event with 
 * <code>PROP_TITLE</code>.
 * <p>
 * The title image is usually used to populate the title bar of this part's
 * visual container. Since this image is managed by the part itself, callers
 * must <b>not</b> dispose the returned image.
 * </p>
 *
 * @return the title image
 */
public Image getTitleImage();
/**
 * Returns the title tool tip text of this workbench part. If this value 
 * changes the part must fire a property listener event with 
 * <code>PROP_TITLE</code>.
 * <p>
 * The tool tip text is used to populate the title bar of this part's 
 * visual container.  
 * </p>
 *
 * @return the workbench part title tool tip
 */
public String getTitleToolTip();
/**
 * Removes the given property listener from this workbench part.
 * Has no affect if an identical listener is not registered.
 *
 * @param listener a property listener
 */
public void removePropertyListener(IPropertyListener listener);
/**
 * Asks this part to take focus within the workbench.
 * <p>
 * Clients should not call this method (the workbench calls this method at
 * appropriate times).
 * </p>
 */
public void setFocus();
}
