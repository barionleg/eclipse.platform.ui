package org.eclipse.ui.internal.dialogs;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.ui.internal.*;
import org.eclipse.help.*;
import org.eclipse.ui.help.*;
import org.eclipse.jface.action.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.custom.BusyIndicator;
/**
 * Holds the information for an item appearing in the welcome editor
 */
public class WelcomeItem {
	private String text;
	private int[][] boldRanges;
	private int[][] helpRanges;
	private String[] helpIds;
	private int[][] actionRanges;
	private String[] actionPluginIds;
	private String[] actionClasses;
/**
 * Creates a new welcome item
 */
public WelcomeItem(String text, int[][] boldRanges, int[][] actionRanges, String[] actionPluginIds, String[] actionClasses, int[][] helpRanges, String[] helpIds) {
	this.text = text;
	this.boldRanges = boldRanges;
	this.actionRanges = actionRanges;
	this.actionPluginIds = actionPluginIds;
	this.actionClasses = actionClasses;
	this.helpRanges = helpRanges;
	this.helpIds = helpIds;
}
/**
 * Returns the action ranges (character locations)
 */
public int[][] getActionRanges() {
	return actionRanges;
}
/**
 * Returns the bold ranges (character locations)
 */
public int[][] getBoldRanges() {
	return boldRanges;
}
/**
 * Returns the help ranges (character locations)
 */
public int[][] getHelpRanges() {
	return helpRanges;
}
/**
 * Returns the text to display
 */
public String getText() {
	return text;
}
/**
 * Returns true is a link (action or help) is present at the given character location
 */
public boolean isLinkAt(int offset) {
	// Check if there is a link at the offset
	for (int i = 0; i < helpRanges.length; i++){
		if (offset > helpRanges[i][0] && offset <= helpRanges[i][0] + helpRanges[i][1]) {
			return true;
		}
	}

	// Check if there is an action link at the offset
	for (int i = 0; i < actionRanges.length; i++){
		if (offset > actionRanges[i][0] && offset <= actionRanges[i][0] + actionRanges[i][1]) {
			return true;
		}
	}
	return false;
}
/**
 * Logs a error to the workbench log
 */
public void logActionLinkError(String actionPluginId, String actionClass) {
	WorkbenchPlugin.log(WorkbenchMessages.getString("WelcomeItem.unableToLoadClass") + actionPluginId + " " + actionClass); //$NON-NLS-1$	//$NON-NLS-2$
}
/**
 * Open a help topic
 */
private void openHelpTopic(String topic) {
	IHelp helpSupport = WorkbenchHelp.getHelpSupport();
	if (helpSupport != null)
		helpSupport.displayHelp(topic);
}
/**
 * Run an action
 */
private void runAction(String pluginId, String className) {
	IPluginDescriptor desc = Platform.getPluginRegistry().getPluginDescriptor(pluginId);
	Class actionClass;
	IAction action;
	try {
		actionClass = desc.getPluginClassLoader().loadClass(className);
	} catch (ClassNotFoundException e) {
		logActionLinkError(pluginId, className);
		return;
	}
	try {
		action = (IAction)actionClass.newInstance();
	} catch (InstantiationException e) {
		logActionLinkError(pluginId, className);
		return;
	} catch (IllegalAccessException e) {
		logActionLinkError(pluginId, className);
		return;
	} catch (ClassCastException e) {
		logActionLinkError(pluginId, className);
		return;
	}
	action.run();
}
/**
 * Triggers the link at the given offset (if there is one)
 */
public void triggerLinkAt(int offset) {
	// Check if there is a help link at the offset
	for (int i = 0; i < helpRanges.length; i++){
		if (offset > helpRanges[i][0] && offset <= helpRanges[i][0] + helpRanges[i][1]) {
			// trigger the link
			final String id = helpIds[i];
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					openHelpTopic(id);
				}
			});
			return;
		}
	}

	// Check if there is an action link at the offset
	for (int i = 0; i < actionRanges.length; i++){
		if (offset > actionRanges[i][0] && offset <= actionRanges[i][0] + actionRanges[i][1]) {
			// trigger the link
			final String id = actionPluginIds[i];
			final String className = actionClasses[i];
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					runAction(id, className);
				}
			});
			return;
		}
	}
}
}
