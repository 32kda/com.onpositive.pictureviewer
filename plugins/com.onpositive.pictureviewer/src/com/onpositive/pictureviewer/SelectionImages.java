/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.resources.IContainer
 *  org.eclipse.core.resources.IProject
 *  org.eclipse.core.resources.IResource
 *  org.eclipse.core.resources.IResourceChangeEvent
 *  org.eclipse.core.resources.IResourceChangeListener
 *  org.eclipse.core.resources.IResourceDelta
 *  org.eclipse.core.resources.IResourceDeltaVisitor
 *  org.eclipse.core.resources.IWorkspaceRoot
 *  org.eclipse.core.resources.ResourcesPlugin
 *  org.eclipse.core.runtime.CoreException
 */
package com.onpositive.pictureviewer;

import java.util.HashSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class SelectionImages
extends AbstractWorskspaceImageEntry {
    private static SelectionImages instance;

    public String getName() {
        return "Workspace";
    }

    private SelectionImages() {
        this.init();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener(){

            public void resourceChanged(IResourceChangeEvent event) {
                final HashSet<String> str = new HashSet<String>();
                try {
                    event.getDelta().accept(new IResourceDeltaVisitor(){

                        public boolean visit(IResourceDelta delta) throws CoreException {
                            IResource resource = delta.getResource();
                            if (resource instanceof IProject) {
                                str.add(resource.getName());
                                return false;
                            }
                            return true;
                        }
                    });
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                SelectionImages.this.reprocess(str);
            }

        });
    }

    public static SelectionImages getSelectionImages() {
        SelectionImages selectionImages;
        if (instance != null) {
            return instance;
        }
        instance = selectionImages = new SelectionImages();
        return selectionImages;
    }

    public IContainer getFile() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

}

