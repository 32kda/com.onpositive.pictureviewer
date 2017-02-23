/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.resources.IFile
 *  org.eclipse.core.runtime.CoreException
 *  org.eclipse.jface.viewers.TreeViewer
 *  org.eclipse.jface.viewers.Viewer
 *  org.eclipse.swt.custom.CLabel
 *  org.eclipse.swt.events.DisposeEvent
 *  org.eclipse.swt.events.DisposeListener
 *  org.eclipse.swt.graphics.Color
 *  org.eclipse.swt.graphics.Device
 *  org.eclipse.swt.graphics.Font
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.graphics.Point
 *  org.eclipse.swt.graphics.Rectangle
 *  org.eclipse.swt.layout.FillLayout
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Layout
 *  org.eclipse.swt.widgets.Listener
 *  org.eclipse.swt.widgets.Tree
 *  org.eclipse.swt.widgets.TreeItem
 *  org.eclipse.swt.widgets.Widget
 *  org.eclipse.ui.IPageListener
 *  org.eclipse.ui.IPartListener
 *  org.eclipse.ui.IStartup
 *  org.eclipse.ui.IViewReference
 *  org.eclipse.ui.IWindowListener
 *  org.eclipse.ui.IWorkbenchPage
 *  org.eclipse.ui.IWorkbenchPart
 *  org.eclipse.ui.IWorkbenchPartSite
 *  org.eclipse.ui.IWorkbenchWindow
 *  org.eclipse.ui.PlatformUI
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.AbstractImageStore;
import com.onpositive.pictureviewer.Activator;
import com.onpositive.pictureviewer.ColumnViewerToolTipSupport;
import java.lang.reflect.Method;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class StartupHandler
implements IStartup {
    public void earlyStartup() {
        Display.getDefault().asyncExec(new Runnable(){

            public void run() {
                Display.getDefault().addFilter(22, new Listener(){

                    public void handleEvent(Event event) {
                        if (event.widget instanceof Composite) {
                            Composite cm = (Composite)event.widget;
                            this.rec(cm);
                        }
                    }

                    private void rec(Composite cm) {
                        Control[] children = cm.getChildren();
                        int n = children.length;
                        int n2 = 0;
                        while (n2 < n) {
                            Tree m;
                            Control c = children[n2];
                            if (c instanceof Tree) {
                                m = (Tree)c;
                                String name = this.getClass().getName();
                                Object data = m.getData(name);
                                if (data == null) {
                                    MC ts = new MC(m, 2, false);
                                    m.setData(name, (Object)ts);
                                }
                            } else if (c instanceof Composite) {
                                this.rec((Composite)c);
                            }
                            ++n2;
                        }
                    }
                });
            }

        });
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        PlatformUI.getWorkbench().addWindowListener(new IWindowListener(){

            public void windowActivated(IWorkbenchWindow window) {
            }

            public void windowClosed(IWorkbenchWindow window) {
            }

            public void windowDeactivated(IWorkbenchWindow window) {
            }

            public void windowOpened(IWorkbenchWindow window) {
                StartupHandler.this.processWindow(window);
            }
        });
        IWorkbenchWindow[] arriWorkbenchWindow = workbenchWindows;
        int n = arriWorkbenchWindow.length;
        int n2 = 0;
        while (n2 < n) {
            IWorkbenchWindow w = arriWorkbenchWindow[n2];
            this.processWindow(w);
            ++n2;
        }
    }

    private void processWindow(IWorkbenchWindow w) {
        w.addPageListener(new IPageListener(){

            public void pageActivated(IWorkbenchPage page) {
            }

            public void pageClosed(IWorkbenchPage page) {
            }

            public void pageOpened(IWorkbenchPage page) {
                StartupHandler.this.processPage(page);
            }
        });
        IWorkbenchPage[] arriWorkbenchPage = w.getPages();
        int n = arriWorkbenchPage.length;
        int n2 = 0;
        while (n2 < n) {
            IWorkbenchPage p = arriWorkbenchPage[n2];
            this.processPage(p);
            ++n2;
        }
    }

    private void processPage(IWorkbenchPage p) {
        IViewReference[] references = p.getViewReferences();
        int n = references.length;
        int n2 = 0;
        while (n2 < n) {
            IViewReference v = references[n2];
            String id = v.getId();
            if (this.accept(id)) {
//                System.out.println("Found");
                IWorkbenchPart part = v.getPart(false);
                if (part != null) {
                    this.initPart(part);
                }
            }
            ++n2;
        }
        p.addPartListener(new IPartListener(){

            public void partActivated(IWorkbenchPart part) {
            }

            public void partBroughtToTop(IWorkbenchPart part) {
            }

            public void partClosed(IWorkbenchPart part) {
            }

            public void partDeactivated(IWorkbenchPart part) {
            }

            public void partOpened(IWorkbenchPart part) {
                String id = part.getSite().getId();
                if (StartupHandler.this.accept(id)) {
                    StartupHandler.this.initPart(part);
                }
            }
        });
    }

    private boolean accept(String id) {
        if (!(id.equals("org.eclipse.jdt.ui.ProjectsView") || id.equals("org.eclipse.jdt.ui.PackageExplorer") || id.equals("org.eclipse.ui.navigator.ProjectExplorer"))) {
            return false;
        }
        return true;
    }

    private void initPart(IWorkbenchPart part) {
        try {
            Method method;
            method = null;
            try {
                method = part.getClass().getMethod("getTreeViewer", new Class[0]);
            }
            catch (Exception v0) {}
            try {
                method = part.getClass().getMethod("getCommonViewer", new Class[0]);
            }
            catch (Exception v1) {}
            try {
                method = part.getClass().getMethod("getViewer", new Class[0]);
            }
            catch (Exception v2) {}
            if (method == null) {
                return;
            }
            final TreeViewer invoke = (TreeViewer)method.invoke((Object)part, new Object[0]);
            final String name = this.getClass().getName();
            Display.getDefault().asyncExec(new Runnable(){

                public void run() {
                    Object data = invoke.getData(name);
                    if (data == null) {
                        MC ts = new MC((Viewer)invoke, 2, false, invoke);
                        invoke.setData(name, (Object)ts);
                    }
                }
            });
        }
        catch (Exception e) {
            Activator.log(e);
        }
    }

    private final class MC
    extends ColumnViewerToolTipSupport {
        private final Tree invoke;
        private Image ima;

        private MC(Viewer viewer, int style, boolean manualActivation, TreeViewer invoke) {
            super(viewer, style, manualActivation);
            this.invoke = invoke.getTree();
        }

        public MC(Tree ts, int style, boolean manualActivation) {
            super((Control)ts, style, manualActivation);
            this.invoke = ts;
        }

        protected boolean shouldCreateToolTip(Event event) {
            TreeItem item = this.invoke.getItem(new Point(event.x, event.y));
            if (item == null) {
                return false;
            }
            Object data = item.getData();
            if (data instanceof IFile) {
                IFile fs = (IFile)data;
                String name2 = fs.getName();
                boolean image = AbstractImageStore.isImage(name2);
                return image;
            }
            return false;
        }

        protected Composite createToolTipContentArea(Event event, Composite parent) {
            final Image image = this.getImage(event);
            Image bgImage = this.getBackgroundImage(event);
            String text = this.getText(event);
            Color fgColor = this.getForegroundColor(event);
            Color bgColor = this.getBackgroundColor(event);
            Font font = this.getFont(event);
            FillLayout layout = (FillLayout)parent.getLayout();
            layout.marginWidth = 10;
            layout.marginHeight = 5;
            parent.setBackground(bgColor);
            CLabel label = new CLabel(parent, this.getStyle(event));
            if (text != null) {
                label.setText(text);
            }
            if (image != null) {
                label.setImage(image);
            }
            if (fgColor != null) {
                label.setForeground(fgColor);
            }
            if (bgColor != null) {
                label.setBackground(bgColor);
            }
            if (bgImage != null) {
                label.setBackgroundImage(image);
            }
            if (font != null) {
                label.setFont(font);
            }
            label.addDisposeListener(new DisposeListener(){

                public void widgetDisposed(DisposeEvent e) {
                    image.dispose();
                }
            });
            return label;
        }

        protected Image getImage(Event event) {
            TreeItem item = this.invoke.getItem(new Point(event.x, event.y));
            Object data = item.getData();
            if (data instanceof IFile) {
                IFile fs = (IFile)data;
                try {
                    this.ima = new Image((Device)Display.getCurrent(), fs.getContents(true));
                    return this.ima;
                }
                catch (CoreException e) {
                    Activator.log((Throwable)e);
                }
            }
            return null;
        }

        protected String getText(Event event) {
            TreeItem item = this.invoke.getItem(new Point(event.x, event.y));
            Object data = item.getData();
            if (data instanceof IFile) {
                Image image2 = this.ima;
                if (this.ima != null) {
                    Rectangle bounds = image2.getBounds();
                    return "(" + bounds.width + "," + bounds.height + ")";
                }
                return "";
            }
            return "";
        }

    }

}

