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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.internal.core.PDECore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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
					if (event.getKey().equals("workspace_target_handle") &&
							event.getNewValue() != "") { //Was "platform_path" before
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
    
    public List<File> getTPPlugins() {

    	if (HAS_PDE) {
    		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
    		ServiceReference<ITargetPlatformService> ref = bundleContext.getServiceReference(ITargetPlatformService.class);
    		ITargetPlatformService service = bundleContext.getService(ref);
    		try {
    			ITargetDefinition def = service.getWorkspaceTargetDefinition();
    			List<File> files = getFilesForDef(def);
    			if (files != null) {
					return files;
				} else {
					Set<File> resultSet = new HashSet<File>();
					ITargetHandle[] targets = service.getTargets(new NullProgressMonitor());
					for (ITargetHandle targetHandle : targets) {
						List<File> filesForDef = getFilesForDef(targetHandle.getTargetDefinition());
						if (filesForDef != null) {
							resultSet.addAll(filesForDef);
						}
					}
					return new ArrayList<>(resultSet);
				}
    			
			} catch (Exception e) {
				Activator.log(e);
			}
    	}
		return null;
    }
    
    protected List<File> getFilesForDef(ITargetDefinition def) {
    	if (def != null) {
    		if (!def.isResolved()) {
				def.resolve(new NullProgressMonitor());
			}
    		TargetBundle[] bundles = def.getAllBundles();
    		return Arrays.asList(bundles).stream().map(bundle -> getFile(bundle)).filter(file -> file != null).collect(Collectors.toList());
		}
		return null;
    }
    
    protected File getFile(TargetBundle bundle) {
    	URI curLocation = bundle.getBundleInfo().getLocation();
    	if (curLocation != null) {
    		File file = new File(curLocation);
    		if (file.exists()) {
    			return file;
    		} else {
    			File parentFile = new File(bundle.getBundleInfo().getBaseLocation());
    			file = new File(parentFile, curLocation.getPath());
    			if (file.exists()) {
        			return file;
        		}
    		}
		}
		return null;
    }

    public List<File> getJarFiles() {
    	List<File> tpPlugins = getTPPlugins();
    	if (tpPlugins != null) {
			return tpPlugins;
		}
    	if (HAS_PDE) {
    		this.location = TargetPlatform.getLocation();
    		File file = new File(this.location, "plugins");
    		return Arrays.asList(file.listFiles());
    	}
		try {
			File installation = new File(Platform.getInstallLocation().getURL().toURI());
			File pluginDir = new File(installation, "plugins");
			if (pluginDir.exists()) {
				return Arrays.asList(pluginDir.listFiles());
			}
		} catch (URISyntaxException e) {
			//Should not happen
			Activator.log(e);
		}
		return null;
    }

}

