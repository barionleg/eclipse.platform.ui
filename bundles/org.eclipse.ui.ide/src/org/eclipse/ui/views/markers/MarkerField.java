/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.views.markers;

import java.net.URL;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.core.resources.IMarker;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ViewerCell;

import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.internal.views.markers.MarkerEntry;
import org.eclipse.ui.internal.views.markers.MarkerSupportInternalUtilities;

/**
 * MarkerField is the abstract superclass of the definition of the content
 * providers for columns in a Markers View.
 * 
 * @since 3.4
 * 
 */
public abstract class MarkerField {

	private static final String ATTRIBUTE_FILTER_CLASS = "filterClass"; //$NON-NLS-1$
	private static final String ATTRIBUTE_FILTER_CONFIGURATION_CLASS = "filterConfigurationClass"; //$NON-NLS-1$

	IConfigurationElement configurationElement;
	private ResourceManager imageManager;


	/**
	 * Compare item1 and item2 for sorting purposes.
	 * 
	 * @param item1
	 * @param item2
	 * @return Either:
	 *         <li>a negative number if the value of item1 is less than the
	 *         value of item2 for this field.
	 *         <li><code>0</code> if the value of item1 and the value of
	 *         item2 are equal for this field.
	 *         <li>a positive number if the value of item1 is greater than the
	 *         value of item2 for this field.
	 */
	public int compare(MarkerItem item1, MarkerItem item2) {
		return getValue(item1).compareTo(getValue(item2));
	}

	/**
	 * Generate the filter for the receiver from the configurationElement.
	 * 
	 * @return MarkerFieldFilter or <code>null</code>.
	 */
	public final MarkerFieldFilter generateFilter() {
		try {
			if (configurationElement.getAttribute(ATTRIBUTE_FILTER_CLASS) == null)
				return null;
			Object filter = IDEWorkbenchPlugin.createExtension(
					configurationElement, ATTRIBUTE_FILTER_CLASS);
			if (filter == null)
				return null;
			MarkerFieldFilter fieldFilter = (MarkerFieldFilter) filter;
			fieldFilter.setField(this);
			return fieldFilter;
		} catch (CoreException e) {
			MarkerSupportInternalUtilities.handle(e);
			return null;
		}
	}

	/**
	 * Create a FilterConfigurationArea for the receiver.
	 * 
	 * @return FilterConfigurationArea or <code>null</code>
	 */
	public final FilterConfigurationArea generateFilterArea() {
		try {
			if (configurationElement
					.getAttribute(ATTRIBUTE_FILTER_CONFIGURATION_CLASS) == null)
				return null;
			FilterConfigurationArea area = (FilterConfigurationArea) IDEWorkbenchPlugin
					.createExtension(configurationElement,
							ATTRIBUTE_FILTER_CONFIGURATION_CLASS);
			if (area != null)
				area.setField(this);
			return area;
		} catch (CoreException e) {
			MarkerSupportInternalUtilities.handle(e);
			return null;
		}
	}

	/**
	 * @return The image to be displayed in the column header for this field or
	 *         <code>null<code>.
	 */
	public Image getColumnHeaderImage() {
		String path = configurationElement
				.getAttribute(MarkerSupportConstants.ATTRIBUTE_ICON);
		if (path == null)
			return null;
		URL url = BundleUtility.find(configurationElement.getContributor()
				.getName(), path);
		if (url == null)
			return null;
		return IDEWorkbenchPlugin.getDefault().getResourceManager()
				.createImageWithDefault(ImageDescriptor.createFromURL(url));
	}

	/**
	 * Return the text to be displayed in the column header for this field.
	 * 
	 * @return String
	 * @see #getColumnTooltipText() this is the default column tooltip text
	 */
	public String getColumnHeaderText() {
		return configurationElement
				.getAttribute(MarkerSupportConstants.ATTRIBUTE_NAME);
	}

	/**
	 * Return the text for the column tooltip.
	 * 
	 * @return String
	 * @see #getColumnHeaderText()
	 */
	public String getColumnTooltipText() {
		return getColumnHeaderText();
	}

	/**
	 * Get the number of characters that should be reserved for the receiver.
	 * 
	 * @param control
	 *            the control to scale from
	 * @return int
	 */
	public int getDefaultColumnWidth(Control control) {
		return 15 * getFontWidth(control);
	}

	/**
	 * Return the editing support for entries for this field. Return null if it
	 * cannot be in-line edited.
	 * 
	 * @param viewer
	 *            the viewer this will be applied to
	 * @return {@link EditingSupport} or <code>null</code>.
	 */
	public EditingSupport getEditingSupport(ColumnViewer viewer) {
		return null;
	}

	/**
	 * Determine the average width of font used by the control.
	 * 
	 * @param control
	 * @return int
	 */
	public final int getFontWidth(Control control) {
		GC gc = new GC(control.getDisplay());
		int width = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();
		return width;
	}

	/**
	 * Return the id for the receiver.
	 * 
	 * @return String
	 */
	public String getId() {
		return configurationElement
				.getAttribute(MarkerSupportConstants.ATTRIBUTE_ID);
	}

	/**
	 * Return the value for a marker.
	 * 
	 * @param marker
	 * @return String
	 */
	public String getMarkerValue(IMarker marker) {
		return getValue(new MarkerEntry(marker));
	}

	/**
	 * Get the severity of the element.
	 * 
	 * @param element
	 * @return int
	 * @see IMarker#SEVERITY_ERROR
	 * @see IMarker#SEVERITY_WARNING
	 * @see IMarker#SEVERITY_INFO
	 */
	public final int getSeverity(MarkerItem element) {
		if (element.isConcrete())
			return element.getAttributeValue(IMarker.SEVERITY, -1);
		return 0;
	}

	/**
	 * @param item
	 * @return The String value of the object for this particular field to be
	 *         displayed to the user.
	 */
	public abstract String getValue(MarkerItem item);

	/**
	 * Set the configuration element used by the receiver.
	 * 
	 * @param element
	 */
	public final void setConfigurationElement(IConfigurationElement element) {
		configurationElement = element;
	}

	/**
	 * Annotate the image with indicators for whether or not help or quick fix
	 * are available.
	 * 
	 * @param item
	 *            the item being decorated
	 * @param image
	 *            the image being overlaid
	 * @return Image
	 */
	public Image annotateImage(MarkerItem item, Image image) {
		ImageDescriptor[] descriptors = new ImageDescriptor[5];
		if (item.isConcrete()) {
			IMarker marker = item.getMarker();
			// If there is no image get the full image rather than the decorated
			// one
			if (marker != null) {
				String contextId = IDE.getMarkerHelpRegistry().getHelp(marker);
				if (contextId != null) {
					if (image == null)
						image = JFaceResources.getImage(Dialog.DLG_IMG_HELP);
					else
						descriptors[IDecoration.TOP_RIGHT] = IDEWorkbenchPlugin
								.getIDEImageDescriptor(MarkerSupportInternalUtilities.IMG_MARKERS_HELP_DECORATION_PATH);
				}
				if (IDE.getMarkerHelpRegistry().hasResolutions(marker)) {
					if (image == null)
						image = getImageManager()
								.createImage(
										IDEInternalWorkbenchImages
												.getImageDescriptor(IDEInternalWorkbenchImages.IMG_ELCL_QUICK_FIX_ENABLED));
					else
						descriptors[IDecoration.BOTTOM_RIGHT] = IDEWorkbenchPlugin
								.getIDEImageDescriptor(MarkerSupportInternalUtilities.IMG_MARKERS_QUICK_FIX_DECORATION_PATH);
				}

				if (descriptors[IDecoration.TOP_RIGHT] != null
						|| descriptors[IDecoration.BOTTOM_RIGHT] != null)
					image = getImageManager().createImage(
							new DecorationOverlayIcon(image, descriptors));
			}
		}
		return image;

	}

	/**
	 * Return the image manager used by the receiver.
	 * @return ResourceManager
	 */
	private ResourceManager getImageManager() {
		if(imageManager == null)
			return JFaceResources.getResources();
		return imageManager;
	}

	/**
	 * Update the contents of the cell.
	 * 
	 * @param cell
	 */
	public void update(ViewerCell cell) {
		cell.setText(getValue((MarkerItem) cell.getElement()));

	}

	/**
	 * Set the imageManager. This is not normally required to be send if using
	 * a {@link MarkerSupportView} as this is done for you.
	 * @param manager
	 */
	public final void setImageManager(ResourceManager manager) {
		this.imageManager = manager;
	}

}
