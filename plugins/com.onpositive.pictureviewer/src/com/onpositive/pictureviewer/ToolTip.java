/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.swt.SWT
 *  org.eclipse.swt.events.DisposeEvent
 *  org.eclipse.swt.events.DisposeListener
 *  org.eclipse.swt.graphics.GC
 *  org.eclipse.swt.graphics.Point
 *  org.eclipse.swt.graphics.Rectangle
 *  org.eclipse.swt.layout.FillLayout
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Layout
 *  org.eclipse.swt.widgets.Listener
 *  org.eclipse.swt.widgets.Monitor
 *  org.eclipse.swt.widgets.Shell
 *  org.eclipse.swt.widgets.Widget
 */
package com.onpositive.pictureviewer;

import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public abstract class ToolTip {
    private Control control;
    private int xShift = 3;
    private int yShift = 0;
    private int popupDelay = 0;
    private int hideDelay = 0;
    private ToolTipOwnerControlListener listener;
    private HashMap<String, Object> data;
    private static Shell CURRENT_TOOLTIP;
    public static final int RECREATE = 1;
    public static final int NO_RECREATE = 2;
    private TooltipHideListener hideListener;
    private Listener shellListener;
    private boolean hideOnMouseDown;
    private boolean respectDisplayBounds;
    private boolean respectMonitorBounds;
    private int style;
    private static Object currentArea;
    private static final boolean IS_OSX;

    static {
        IS_OSX = SWT.getPlatform().equals("carbon");
    }

    public ToolTip(Control control) {
        this(control, 1, false);
    }

    public ToolTip(Control control, int style, boolean manualActivation) {
        this.hideListener = new TooltipHideListener();
        this.hideOnMouseDown = true;
        this.respectDisplayBounds = true;
        this.respectMonitorBounds = true;
        this.control = control;
        this.style = style;
        this.control.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent e) {
                ToolTip.access$4(ToolTip.this, null);
                ToolTip.this.deactivate();
            }
        });
        this.listener = new ToolTipOwnerControlListener();
        this.shellListener = new Listener(){

            public void handleEvent(final Event event) {
                if (ToolTip.this.control != null && !ToolTip.this.control.isDisposed()) {
                    ToolTip.this.control.getDisplay().asyncExec(new Runnable(){

                        public void run() {
                            if (ToolTip.this.control.getDisplay().getActiveShell() != CURRENT_TOOLTIP) {
                                ToolTip.this.toolTipHide(CURRENT_TOOLTIP, event);
                            }
                        }
                    });
                }
            }

        };
        if (!manualActivation) {
            this.activate();
        }
    }

    public void setData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }
        this.data.put(key, value);
    }

    public Object getData(String key) {
        if (this.data != null) {
            return this.data.get(key);
        }
        return null;
    }

    public void setShift(Point p) {
        this.xShift = p.x;
        this.yShift = p.y;
    }

    public void activate() {
        this.deactivate();
        this.control.addListener(12, (Listener)this.listener);
        this.control.addListener(32, (Listener)this.listener);
        this.control.addListener(5, (Listener)this.listener);
        this.control.addListener(7, (Listener)this.listener);
        this.control.addListener(3, (Listener)this.listener);
        this.control.addListener(37, (Listener)this.listener);
    }

    public void deactivate() {
        this.control.removeListener(12, (Listener)this.listener);
        this.control.removeListener(32, (Listener)this.listener);
        this.control.removeListener(5, (Listener)this.listener);
        this.control.removeListener(7, (Listener)this.listener);
        this.control.removeListener(3, (Listener)this.listener);
        this.control.removeListener(37, (Listener)this.listener);
    }

    public boolean isRespectDisplayBounds() {
        return this.respectDisplayBounds;
    }

    public void setRespectDisplayBounds(boolean respectDisplayBounds) {
        this.respectDisplayBounds = respectDisplayBounds;
    }

    public boolean isRespectMonitorBounds() {
        return this.respectMonitorBounds;
    }

    public void setRespectMonitorBounds(boolean respectMonitorBounds) {
        this.respectMonitorBounds = respectMonitorBounds;
    }

    protected boolean shouldCreateToolTip(Event event) {
        if ((this.style & 2) != 0) {
            Object tmp = this.getToolTipArea(event);
            if (tmp == null) {
                this.hide();
                return false;
            }
            boolean rv = !tmp.equals(currentArea);
            return rv;
        }
        return true;
    }

    private boolean shouldHideToolTip(Event event) {
        if (event != null && event.type == 5 && (this.style & 2) != 0) {
            Object tmp = this.getToolTipArea(event);
            if (tmp == null) {
                this.hide();
                return false;
            }
            boolean rv = !tmp.equals(currentArea);
            return rv;
        }
        return true;
    }

    protected Object getToolTipArea(Event event) {
        return this.control;
    }

    public void show(Point location) {
        Event event = new Event();
        event.x = location.x;
        event.y = location.y;
        event.widget = this.control;
        this.toolTipCreate(event);
    }

    private Shell toolTipCreate(Event event) {
        if (this.shouldCreateToolTip(event)) {
            Shell shell = new Shell(this.control.getShell(), 540676);
            shell.setLayout((Layout)new FillLayout());
            this.toolTipOpen(shell, event);
            return shell;
        }
        return null;
    }

    private void toolTipShow(Shell tip, Event event) {
        if (!tip.isDisposed()) {
            currentArea = this.getToolTipArea(event);
            this.createToolTipContentArea(event, (Composite)tip);
            if (this.isHideOnMouseDown()) {
                this.toolTipHookBothRecursively((Control)tip);
            } else {
                this.toolTipHookByTypeRecursively((Control)tip, true, 7);
            }
            tip.pack();
            Point size = tip.getSize();
            Point location = this.fixupDisplayBounds(size, this.getLocation(size, event));
            Point cursorLocation = tip.getDisplay().getCursorLocation();
            if (cursorLocation.y == location.y && location.x < cursorLocation.x && location.x + size.x > cursorLocation.x) {
                location.y -= 2;
            }
            tip.setLocation(location);
            tip.setVisible(true);
        }
    }

    private Point fixupDisplayBounds(Point tipSize, Point location) {
        if (this.respectDisplayBounds || this.respectMonitorBounds) {
            Rectangle bounds;
            Point rightBounds;
            rightBounds = new Point(tipSize.x + location.x, tipSize.y + location.y);
            Monitor[] ms = this.control.getDisplay().getMonitors();
            if (this.respectMonitorBounds && ms.length > 1) {
                bounds = this.control.getMonitor().getBounds();
                Point p = new Point(location.x, location.y);
                int i = 0;
                while (i < ms.length) {
                    Rectangle tmp = ms[i].getBounds();
                    if (tmp.contains(p)) {
                        bounds = tmp;
                        break;
                    }
                    ++i;
                }
            } else {
                bounds = this.control.getDisplay().getBounds();
            }
            if (!bounds.contains(location) || !bounds.contains(rightBounds)) {
                if (rightBounds.x > bounds.x + bounds.width) {
                    location.x -= rightBounds.x - (bounds.x + bounds.width);
                }
                if (rightBounds.y > bounds.y + bounds.height) {
                    location.y -= rightBounds.y - (bounds.y + bounds.height);
                }
                if (location.x < bounds.x) {
                    location.x = bounds.x;
                }
                if (location.y < bounds.y) {
                    location.y = bounds.y;
                }
            }
        }
        return location;
    }

    public Point getLocation(Point tipSize, Event event) {
        return this.control.toDisplay(event.x + this.xShift, event.y + this.yShift);
    }

    private void toolTipHide(Shell tip, Event event) {
        if (tip != null && !tip.isDisposed() && this.shouldHideToolTip(event)) {
            this.control.getShell().removeListener(27, this.shellListener);
            currentArea = null;
            this.passOnEvent(tip, event);
            tip.dispose();
            CURRENT_TOOLTIP = null;
            this.afterHideToolTip(event);
        }
    }

    private void passOnEvent(Shell tip, Event event) {
        if (this.control != null && !this.control.isDisposed() && event != null && event.widget != this.control && event.type == 3) {
            final Display display = this.control.getDisplay();
            Point newPt = display.map((Control)tip, null, new Point(event.x, event.y));
            final Event newEvent = new Event();
            newEvent.button = event.button;
            newEvent.character = event.character;
            newEvent.count = event.count;
            newEvent.data = event.data;
            newEvent.detail = event.detail;
            newEvent.display = event.display;
            newEvent.doit = event.doit;
            newEvent.end = event.end;
            newEvent.gc = event.gc;
            newEvent.height = event.height;
            newEvent.index = event.index;
            newEvent.item = event.item;
            newEvent.keyCode = event.keyCode;
            newEvent.start = event.start;
            newEvent.stateMask = event.stateMask;
            newEvent.text = event.text;
            newEvent.time = event.time;
            newEvent.type = event.type;
            newEvent.widget = event.widget;
            newEvent.width = event.width;
            newEvent.x = newPt.x;
            newEvent.y = newPt.y;
            tip.close();
            display.asyncExec(new Runnable(){

                public void run() {
                    if (IS_OSX) {
                        try {
                            Thread.sleep(300);
                        }
                        catch (InterruptedException v0) {}
                        display.post(newEvent);
                        newEvent.type = 4;
                        display.post(newEvent);
                    } else {
                        display.post(newEvent);
                    }
                }
            });
        }
    }

    private void toolTipOpen(final Shell shell, final Event event) {
        if (CURRENT_TOOLTIP != null) {
            this.toolTipHide(CURRENT_TOOLTIP, null);
        }
        CURRENT_TOOLTIP = shell;
        this.control.getShell().addListener(27, this.shellListener);
        if (this.popupDelay > 0) {
            this.control.getDisplay().timerExec(this.popupDelay, new Runnable(){

                public void run() {
                    ToolTip.this.toolTipShow(shell, event);
                }
            });
        } else {
            this.toolTipShow(CURRENT_TOOLTIP, event);
        }
        if (this.hideDelay > 0) {
            this.control.getDisplay().timerExec(this.popupDelay + this.hideDelay, new Runnable(){

                public void run() {
                    ToolTip.this.toolTipHide(shell, null);
                }
            });
        }
    }

    private void toolTipHookByTypeRecursively(Control c, boolean add, int type) {
        if (add) {
            c.addListener(type, (Listener)this.hideListener);
        } else {
            c.removeListener(type, (Listener)this.hideListener);
        }
        if (c instanceof Composite) {
            Control[] children = ((Composite)c).getChildren();
            int i = 0;
            while (i < children.length) {
                this.toolTipHookByTypeRecursively(children[i], add, type);
                ++i;
            }
        }
    }

    private void toolTipHookBothRecursively(Control c) {
        c.addListener(3, (Listener)this.hideListener);
        c.addListener(7, (Listener)this.hideListener);
        if (c instanceof Composite) {
            Control[] children = ((Composite)c).getChildren();
            int i = 0;
            while (i < children.length) {
                this.toolTipHookBothRecursively(children[i]);
                ++i;
            }
        }
    }

    protected abstract Composite createToolTipContentArea(Event var1, Composite var2);

    protected void afterHideToolTip(Event event) {
    }

    public void setHideDelay(int hideDelay) {
        this.hideDelay = hideDelay;
    }

    public void setPopupDelay(int popupDelay) {
        this.popupDelay = popupDelay;
    }

    public boolean isHideOnMouseDown() {
        return this.hideOnMouseDown;
    }

    public void setHideOnMouseDown(final boolean hideOnMouseDown) {
        if (CURRENT_TOOLTIP != null && !CURRENT_TOOLTIP.isDisposed() && hideOnMouseDown != this.hideOnMouseDown) {
            this.control.getDisplay().syncExec(new Runnable(){

                public void run() {
                    if (CURRENT_TOOLTIP != null && CURRENT_TOOLTIP.isDisposed()) {
                        ToolTip.this.toolTipHookByTypeRecursively((Control)CURRENT_TOOLTIP, hideOnMouseDown, 3);
                    }
                }
            });
        }
        this.hideOnMouseDown = hideOnMouseDown;
    }

    public void hide() {
        this.toolTipHide(CURRENT_TOOLTIP, null);
    }

    static /* synthetic */ void access$4(ToolTip toolTip, HashMap<String, Object> hashMap) {
        toolTip.data = hashMap;
    }

    private class ToolTipOwnerControlListener
    implements Listener {

        public void handleEvent(Event event) {
            switch (event.type) {
                case 1: 
                case 3: 
                case 5: 
                case 12: 
                case 37: {
                    toolTipHide(CURRENT_TOOLTIP, event);
                    break;
                }
                case 32: {
                    toolTipCreate(event);
                    break;
                }
                case 7: {
                    if (CURRENT_TOOLTIP != null && !CURRENT_TOOLTIP.isDisposed() && CURRENT_TOOLTIP.getBounds().contains(control.toDisplay(event.x, event.y))) break;
                    toolTipHide(CURRENT_TOOLTIP, event);
                }
            }
        }

//        /* synthetic */ ToolTipOwnerControlListener(ToolTip toolTip, ToolTipOwnerControlListener toolTipOwnerControlListener) {
//            ToolTipOwnerControlListener toolTipOwnerControlListener2;
//            toolTipOwnerControlListener2(toolTip);
//        }
    }

    private class TooltipHideListener
    implements Listener {

       
        public void handleEvent(Event event) {
            if (event.widget instanceof Control) {
                Control c = (Control)event.widget;
                Shell shell = c.getShell();
                switch (event.type) {
                    case 3: {
                        if (!isHideOnMouseDown()) break;
                        toolTipHide(shell, event);
                        break;
                    }
                    case 7: {
                        Rectangle rect = shell.getBounds();
                        rect.x += 5;
                        rect.y += 5;
                        rect.width -= 10;
                        rect.height -= 10;
                        if (rect.contains(c.getDisplay().getCursorLocation())) break;
                        toolTipHide(shell, event);
                    }
                }
            }
        }

//        /* synthetic */ TooltipHideListener(ToolTip toolTip, TooltipHideListener tooltipHideListener) {
//            TooltipHideListener tooltipHideListener2;
//            tooltipHideListener2(toolTip);
//        }
    }

}

