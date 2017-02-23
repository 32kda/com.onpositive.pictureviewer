/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.Preferences
 *  org.eclipse.core.runtime.Preferences$IPropertyChangeListener
 *  org.eclipse.core.runtime.Preferences$PropertyChangeEvent
 *  org.eclipse.pde.core.plugin.TargetPlatform
 *  org.eclipse.pde.internal.core.PDECore
 */
package com.onpositive.pictureviewer;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.internal.core.PDECore;

@SuppressWarnings("restriction")
public class PlatformImages
extends AbstractImageStore
implements IImageStore {
	private static final boolean HAS_PDE = Platform.getBundle("org.eclipse.pde.core") != null;
    private String location;
    private static PlatformImages instance;

    public String getName() {
        return HAS_PDE?"Target Platform":"Eclipse";
    }

    private PlatformImages() {
    	if (HAS_PDE) {
	    	IEclipsePreferences node = InstanceScope.INSTANCE.getNode(PDECore.PLUGIN_ID);
	    	node.addPreferenceChangeListener(new IPreferenceChangeListener() {
				
				@Override
				public void preferenceChange(PreferenceChangeEvent event) {
					if (event.getKey().equals("workspace_target_handle")) { //Was "platform_path" before
	                    PlatformImages.this.initPlatform();
	                }
				}
			});
    	}
        this.init();
    }

    public static PlatformImages getInstance() {
        if (instance == null) {
            instance = new PlatformImages();
        }
        return instance;
    }

    public File getFile() {
    	if (HAS_PDE) {
    		this.location = TargetPlatform.getLocation();
    		File file = new File(this.location, "plugins");
    		return file;
    	}
		try {
			File installation = new File(Platform.getInstallLocation().getURL().toURI());
			File pluginDir = new File(installation, "plugins");
			if (pluginDir.exists()) {
				return pluginDir;
			}
		} catch (URISyntaxException e) {
			//Should not happen
			Activator.log(e);
		}
		return null;
    }

}

