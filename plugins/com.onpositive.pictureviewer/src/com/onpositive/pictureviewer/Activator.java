/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.ILog
 *  org.eclipse.core.runtime.IStatus
 *  org.eclipse.core.runtime.Status
 *  org.eclipse.ui.plugin.AbstractUIPlugin
 *  org.osgi.framework.BundleContext
 */
package com.onpositive.pictureviewer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator
extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "com.onpositive.pictureviewer";
    private static Activator plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }

    public static void log(Throwable th) {
        th.printStackTrace();
        Activator.getDefault().getLog().log((IStatus)new Status(4, "com.onpositive.pictureviewer", 4, th.getMessage(), th));
    }
}

