package org.eclipse.ui.views.properties;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.widgets.Composite;
/**
 * Standard implementation for property descriptors.
 * <p>
 * The required attributes of property descriptors (id and display name) are
 * passed to the constructor; the optional attributes can be configured using
 * the various set methods (all have reasonable default values):
 * <ul>
 *   <li><code>setDescription</code></li>
 *   <li><code>setCategory</code></li>
 *   <li><code>setLabelProvider</code></li>
 *   <li><code>setHelpContexts</code></li>
 * </ul>
 * Subclasses should reimplement <code>getPropertyEditor</code> to provide a
 * cell editor for changing the value; otherwise the property will be 
 * effectively read only.
 * </p>
 * <p>
 * There are several concrete subclasses provided in this package that cover
 * the most common cases:
 * <ul>
 *   <li><code>TextPropertyDescriptor</code> - edits with a 
 *      <code>TextCellEditor</code></li>
 *   <li><code>CheckboxPropertyDescriptor - edits with a 
 *      <code>CheckboxCellEditor</code></code></li>
 *   <li><code>ComboBoxPropertyDescriptor - edits with a
 *      <code>ComboBoxCellEditor</code></code></li>
 *   <li><code>ColorPropertyDescriptor - edits with a 
 *      <code>ColorCellEditor</code></code></li>
 * </ul>
 * </p>
 */
public class PropertyDescriptor implements IPropertyDescriptor {
	
	/**
	 * The property id.
	 */
	private Object id;

	/**
	 * The name to display for the property.
	 */
	private String display;
	
	/**
	 * Category name, or <code>null</code> if none (the default).
	 */
	private String category = null;

	/**
	 * Description of the property, or <code>null</code> if none (the default). 
	 */
	private String description = null;

	/**
	 * The help context ids, or <code>null</code> if none (the default). 
	 */
	private Object helpIds;

	/**
	 * The flags used to filter the property.
	 */
	private String[] filterFlags;

	/**
	 * The object that provides the property value's text and image, or 
	 * <code>null</code> if the default label provider is used (the default).
	 */
	private ILabelProvider labelProvider = null;

	/**
	 * The object to validate the values in the cell editor, or <code>null</code>
	 * if none (the default).
	 */
	private ICellEditorValidator validator;

	/**
	 * Indicates if the descriptor is compatible with other descriptors of this
	 * type. <code>false</code> by default.
	 */
	private boolean incompatible = false;
/**
 * Creates a new property descriptor with the given id and display name
 */
public PropertyDescriptor(Object id, String displayName) {
	Assert.isNotNull(id);
	Assert.isNotNull(displayName);
	this.id = id;
	this.display = displayName;
}
/**
 * The <code>PropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns <code>null</code>.
 * <p>
 * Since no cell editor is returned, the property is read only.
 * </p>
 */
public CellEditor createPropertyEditor(Composite parent) {
	return null;
}
/**
 * Returns <code>true</code> if this property descriptor is to be always 
 * considered incompatible with any other property descriptor.
 * This prevents a property from displaying during multiple 
 * selection.
 *
 * @return <code>true</code> to indicate always incompatible
 */
protected boolean getAlwaysIncompatible() {
	return incompatible;
}
/**
 * The <code>PropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value set by
 * the <code>setCategory</code> method. If unset, this method returns
 * <code>null</code> indicating the default category.
 *
 * @see #setCategory
 */
public String getCategory() {
	return category;
}
/**
 * The <code>PropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value set by
 * the <code>setDescription</code> method. If unset, this method returns
 * <code>null</code> indicating no description.
 *
 * @see #setDescription
 */
public String getDescription() {
	return description;
}
/**
 * The <code>SimplePropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value supplied
 * on the constructor.
 */
public String getDisplayName() {
	return display;
}
/**
 * The <code>SimplePropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value set by
 * the <code>setFilterFlags</code> method. If unset, this method returns
 * <code>null</code>.
 * <p>
 * Valid values for these flags are declared as constants on 
 *  <code>IPropertySheetEntry</code>
 */
public String[] getFilterFlags() {
	return filterFlags;
}
/**
 * The <code>SimplePropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value set by
 * the <code>setHelpContextId</code> method. If unset, this method returns
 * <code>null</code>.
 *
 * @see #setHelpContextIds
 */
public Object getHelpContextIds() {
	return helpIds;
}
/**
 * The <code>PropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value supplied
 * on the constructor.
 */
public Object getId() {
	return id;
}
/**
 * The <code>PropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns the value set by
 * the <code>setProvider</code> method or, if no value has been set
 * it returns a <code>LabelProvider</code>
 *
 * @see #setLabelProvider
 */
public ILabelProvider getLabelProvider() {
	if (labelProvider != null)
		return labelProvider;
	else
		return new LabelProvider();	
}
/**
 * Returns the input validator for editing the property.
 *
 * @return the validator used to verify correct values for this property,
 *   or <code>null</code>
 */
protected ICellEditorValidator getValidator() {
	return validator;
}
/**
 * The <code>SimplePropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method returns true if the other
 * property has the same id and category and <code>getAlwaysIncompatible()</code>
 * returns false
 */
public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {
	if (getAlwaysIncompatible()) 
		return false;

	// Compare id		
	Object id1 = getId();
	Object id2 = anotherProperty.getId();
	if (!id1.equals(id2))
		return false;

	// Compare Category (may be null)
	if (getCategory() == null) {
		if (anotherProperty.getCategory() != null)
			return false;
	} else {
		if (!getCategory().equals(anotherProperty.getCategory()))
			return false;
	}

	return true;
}
/**
 * Sets a flag indicating whether this property descriptor is to be always 
 * considered incompatible with any other property descriptor.
 * Setting this flag prevents a property from displaying during multiple 
 * selection.
 *
 * @param flag <code>true</code> to indicate always incompatible
 */
public void setAlwaysIncompatible(boolean flag) {
	incompatible = flag;
}
/**
 * Sets the category for this property descriptor.
 * 
 * @param category the category for the descriptor, or <code>null</code> if none
 * @see #getCategory
 */
public void setCategory(String category) {
	this.category = category;
}
/**
 * Sets the description for this property descriptor.
 * The description should be limited to a single line so that it can be
 * displayed in the status line.
 * 
 * @param description the description, or <code>null</code> if none
 * @see #getDescription
 */
public void setDescription(String description) {
	this.description = description;
}
/**
 * Sets the the filter flags for this property descriptor.
 * The description should be limited to a single line so that it can be
 * displayed in the status line.
 * <p>
 * Valid values for these flags are declared as constants on 
 *  <code>IPropertySheetEntry</code>
 * </p>
 * 
 * @param value the filter flags
 * @see #getFilterFlags
 */
public void setFilterFlags(String value[]) {
	filterFlags = value;
}
/**
 * Sets the help context ids for this property descriptor.
 *
 * The value can be either
 * 1) a mixed-type array of context ids (type <code>String</code>) and/or 
 * 	help contexts (type <code>IContext</code>) or
 * 2) an instance of <code>IContextComputer</code>
 * </p>
 * <p>
 * Use an <code>IContextComputer</code> when the help contexts cannot be 
 * computed in advance, use return an array.
 * 
 * @param contextIds the help context ids, or <code>null</code> if none
 * @see #getHelpContextIds
  */
public void setHelpContextIds(Object contextIds) {
	helpIds = contextIds;
}
/**
 * Sets the label provider for this property descriptor.
 * <p>
 * If no label provider is set an instance of <code>LabelProvider</code>
 * will be created as the default when needed.
 * </p>
 * 
 * @param labelProvider the label provider for the descriptor, or 
 *   <code>null</code> if the default label provider should be used
 * @see #getLabelProvider
 */
public void setLabelProvider(ILabelProvider provider) {
	labelProvider = provider;
}
/**
 * Sets the input validator for the cell editor for this property descriptor.
 * <p>
 * [Issue: This method should be unnecessary is the cell editor's own
 *  validator is used.
 * ]
 * </p>
 * 
 * @param validator the cell input validator, or <code>null</code> if none
 */
public void setValidator(ICellEditorValidator validator) {
	this.validator = validator;
}
}
