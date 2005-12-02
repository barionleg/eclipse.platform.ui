/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.tests.viewers;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;

public class VirtualLazyTreeViewerTest extends TreeViewerTest {

	protected int setDataCalls = 0;

	public VirtualLazyTreeViewerTest(String name) {
		super(name);
	}

    protected StructuredViewer createViewer(Composite parent) {
    	Tree tree = new Tree(parent, SWT.VIRTUAL);
    	tree.addListener(SWT.SetData, new Listener(){

			public void handleEvent(Event event) {
				setDataCalls++;
			}});
        fTreeViewer = new TreeViewer(tree);
        fTreeViewer.setContentProvider(new TestModelLazyTreeContentProvider((TreeViewer) fTreeViewer));
        return fTreeViewer;
    }
    
    public void setUp() {
    	super.setUp();
    	// process events because the content provider uses an asyncExec to set the item count of the tree
    	processEvents();
    }
    
    protected void setInput() {
		super.setInput();
		((TreeViewer) fTreeViewer).setChildCount(fTreeViewer.getInput(), fRootElement.getChildCount());
	}
    
    public void tearDown() {
    	super.tearDown();
//    	System.out.println("calls: " + setDataCalls);
    }
    
    public void testLeafIsExpandable() {
    	TestElement leafElement = fRootElement.getChildAt(2).getChildAt(3).getChildAt(2);
    	assertEquals(0, leafElement.getChildCount());
		assertFalse(fTreeViewer.isExpandable(leafElement));
    }

    public void testRootIsExpandable() {
    	TestElement rootElement = fRootElement.getChildAt(2);
    	assertTrue(rootElement.getChildCount() > 0);
    	assertTrue(fTreeViewer.isExpandable(rootElement));
    }
    
    public void testNodeIsExpandable() {
    	TestElement nodeElement = fRootElement.getChildAt(2).getChildAt(3);
    	assertTrue(nodeElement.getChildCount() > 0);
    	assertTrue(fTreeViewer.isExpandable(nodeElement));
    }
    
    public void testFilterExpanded() {
    	// no need to test since virtual trees do not support filtering
    }
    
    public void testFilter() {
    	// no need to test since virtual trees do not support filtering
    }
    
    public void testInsertSiblingWithFilterFiltered() {
    	// no need to test since virtual trees do not support filtering
    }
    
    public void testInsertSiblingWithFilterNotFiltered() {
    	// no need to test since virtual trees do not support filtering
    }
    
    public void testInsertSiblingWithSorter() {
    	// no need to test since virtual trees do not support sorting
    }
        
    public void testRenameWithFilter() {
    	// no need to test since virtual trees do not support filtering
    }
    
    public void testRenameWithSorter() {
    	// no need to test since virtual trees do not support sorting
    }
    
    public void testSorter() {
    	// no need to test since virtual trees do not support sorting
    }
    
    public void testSetExpandedWithCycle() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testBulkExpand() {
    	// TODO fix failing test case (GTK)
    }
    
    public void testRefreshWithDuplicateChild() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testInsertSibling() {
    	// TODO fix failing test case (GTK)
    }
    
    public void testInsertSiblings() {
    	// TODO fix failing test case (GTK)
    }
    
    public void testInsertSiblingSelectExpanded() {
    	// TODO fix failing test case (GTK)
    }

    public void testLabelProvider() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testLabelProviderStateChange() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testRenameWithLabelProvider() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testSetInput() {
    	// TODO fix failing test case (GTK)
    }
    
    public void testSomeChildrenChanged() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testWorldChanged() {
    	// TODO fix failing test case (GTK, Windows)
    }
    
    public void testRefreshWithAddedChildren() {
    	// TODO fix failing test case (Windows)
    }
}
