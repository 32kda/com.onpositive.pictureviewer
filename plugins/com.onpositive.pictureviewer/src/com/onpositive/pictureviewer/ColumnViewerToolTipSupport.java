/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.jface.viewers.TreeViewer
 *  org.eclipse.jface.viewers.Viewer
 *  org.eclipse.swt.graphics.Point
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Tree
 *  org.eclipse.swt.widgets.TreeItem
 *  org.eclipse.swt.widgets.Widget
 */
package com.onpositive.pictureviewer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ColumnViewerToolTipSupport
extends DefaultToolTip {
	private static final int POPUP_DELAY = 200;
	private static final int POPUP_HIDE_DELAY = 400;
	private Viewer viewer;
    private Control tree;
//    private static final String VIEWER_CELL_KEY = "org.eclipse.jface_VIEWER_CELL_KEY";
//    private static final int DEFAULT_SHIFT_X = 10;
//    private static final int DEFAULT_SHIFT_Y = 0;

    protected ColumnViewerToolTipSupport(Viewer viewer, int style, boolean manualActivation) {
        super(viewer.getControl(), style, manualActivation);
        this.viewer = viewer;
    }

    protected ColumnViewerToolTipSupport(Control ts, int style, boolean manualActivation) {
        super(ts, style, manualActivation);
        this.tree = ts;
    }

    public static void enableFor(Viewer viewer) {
        new com.onpositive.pictureviewer.ColumnViewerToolTipSupport(viewer, 2, false);
    }

    public static void enableFor(Viewer viewer, int style) {
        new com.onpositive.pictureviewer.ColumnViewerToolTipSupport(viewer, style, false);
    }

    protected Object getToolTipArea(Event event) {
        TreeViewer tv = (TreeViewer)this.viewer;
        if (tv == null) {
            // empty if block
        }
        TreeItem item2 = (tv != null ? tv.getTree() : (Tree)this.tree).getItem(new Point(event.x, event.y));
        return item2;
    }

    protected Composite createToolTipContentArea(Event event, Composite parent) {
        Object cell = this.getData("org.eclipse.jface_VIEWER_CELL_KEY");
        this.setData("org.eclipse.jface_VIEWER_CELL_KEY", null);
        return this.createViewerToolTipContentArea(event, cell, parent);
    }

    protected Composite createViewerToolTipContentArea(Event event, Object cell, Composite parent) {
        return super.createToolTipContentArea(event, parent);
    }

    protected boolean shouldCreateToolTip(Event event) {
        if (!super.shouldCreateToolTip(event)) {
            return false;
        }
        Tree curTree = (Tree) (tree != null ? tree : ((TreeViewer) viewer).getTree());
        TreeItem item = curTree.getItem(new Point(event.x, event.y));
        curTree.setToolTipText("");
        if (item != null) {
            this.setPopupDelay(POPUP_DELAY);
            this.setHideDelay(POPUP_HIDE_DELAY);
            this.setShift(new Point(10, 0));
            this.setData("org.eclipse.jface_VIEWER_CELL_KEY", (Object)item);
            return true;
        }
        return false;
    }

    protected void afterHideToolTip(Event event) {
        super.afterHideToolTip(event);
        this.setData("org.eclipse.jface_VIEWER_CELL_KEY", null);
        Tree curTree = (Tree) (tree != null ? tree : ((TreeViewer) viewer).getTree());
        if (event != null && event.widget != curTree) {
            curTree.setFocus();
        }
    }
}

