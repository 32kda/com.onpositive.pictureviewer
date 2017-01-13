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

import com.onpositive.pictureviewer.AbstractImageEntry;
import com.onpositive.pictureviewer.IImageStore;
import java.io.File;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.internal.core.PDECore;

@SuppressWarnings("restriction")
public class PlatformImages
extends AbstractImageEntry
implements IImageStore {
    private String location;
    private static PlatformImages instance;

    public String getName() {
        return "Target Platform";
    }

    private PlatformImages() {
    	IEclipsePreferences node = InstanceScope.INSTANCE.getNode(PDECore.PLUGIN_ID);
    	node.addPreferenceChangeListener(new IPreferenceChangeListener() {
			
			@Override
			public void preferenceChange(PreferenceChangeEvent event) {
				if (event.getKey().equals("platform_path")) {
                    PlatformImages.this.initPlatform();
                }
			}
		});
//        Preferences preferences = PDECore.getDefault().getPluginPreferences();
//        Preferences.IPropertyChangeListener propertyChangeListener = new Preferences.IPropertyChangeListener(){
//
//            public void propertyChange(Preferences.PropertyChangeEvent event) {
//                if (event.getProperty().equals("platform_path")) {
//                    PlatformImages.this.initPlatform();
//                }
//            }
//        };
//        preferences.addPropertyChangeListener(propertyChangeListener);
        this.init();
    }

    public static PlatformImages getInstance() {
        if (instance == null) {
            instance = new PlatformImages();
        }
        return instance;
    }

    public File getFile() {
        this.location = TargetPlatform.getLocation();
        File file = new File(this.location, "plugins");
        return file;
    }

}

