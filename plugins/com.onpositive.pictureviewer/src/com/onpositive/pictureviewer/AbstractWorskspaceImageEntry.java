/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.resources.IContainer
 *  org.eclipse.core.resources.IFile
 *  org.eclipse.core.resources.IProject
 *  org.eclipse.core.resources.IResource
 *  org.eclipse.core.resources.IWorkspaceRoot
 *  org.eclipse.core.runtime.CoreException
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.IFileImageEntry;
import com.onpositive.pictureviewer.IImageEntry;
import com.onpositive.pictureviewer.IImageStore;
import com.onpositive.pictureviewer.IStoreImageListener;
import com.onpositive.pictureviewer.ItemGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractWorskspaceImageEntry
implements IImageStore {
    HashSet<IStoreImageListener> images = new HashSet<IStoreImageListener>();
    protected ArrayList<ItemGroup> imageList = new ArrayList<>();

    @Override
    public synchronized Collection<ItemGroup> getContents() {
        return new ArrayList<ItemGroup>(this.imageList);
    }

    public void init() {
        Thread s = new Thread(){

            public void run() {
                AbstractWorskspaceImageEntry.this.initPlatform();
            }
        };
        s.start();
    }

    synchronized void reprocess(HashSet<String> str) {
        IContainer file = this.getFile();
        HashSet<ItemGroup> toRemove = new HashSet<ItemGroup>();
        HashSet<ItemGroup> toAdd = new HashSet<ItemGroup>();
        for (ItemGroup itemGroup : this.imageList) {
            if (!str.contains(itemGroup.getName())) continue;
            toRemove.add(itemGroup);
            str.remove(itemGroup.getName());
            ArrayList<IImageEntry> za = new ArrayList<IImageEntry>();
            ItemGroup newF = new ItemGroup(itemGroup.getName(), za);
            IProject file2 = ((IWorkspaceRoot)file).getProject(itemGroup.getName());
            this.parse(za, (IResource)file2);
            if (za.isEmpty()) continue;
            toAdd.add(newF);
        }
        for (String s : str) {
            ArrayList<IImageEntry> za = new ArrayList<IImageEntry>();
            ItemGroup newF = new ItemGroup(s, za);
            IProject file2 = ((IWorkspaceRoot)file).getProject(s);
            this.parse(za, (IResource)file2);
            if (za.isEmpty()) continue;
            toAdd.add(newF);
        }
        this.imageList.removeAll(toRemove);
        this.imageList.addAll(toAdd);
        this.fireChanged();
    }

    public abstract IContainer getFile();

    protected void initPlatform() {
        this.imageList.clear();
        this.fireChanged();
        IContainer container = this.getFile();
        if (container != null && container.exists()) {
            try {
                IResource[] listFiles = container.members();
                int n = listFiles.length;
                int n2 = 0;
                while (n2 < n) {
                    IResource resource = listFiles[n2];
                    if (isValid(resource) && resource.getName().charAt(0) != '.') {
                        ArrayList<IImageEntry> imgs = new ArrayList<IImageEntry>();
                        ItemGroup group = new ItemGroup(resource.getName(), imgs);
                        this.parse(imgs, resource);
                        if (group.getChildCount() > 0) {
                            this.fetched(group);
                        }
                    }
                    ++n2;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.fireChanged();
    }

	private boolean isValid(IResource resurce) {
		return resurce.exists() && !resurce.isHidden() && resurce.getProject().isOpen(); 
	}

    private synchronized void fetched(ItemGroup group) {
        this.imageList.add(group);
        this.fireChanged();
    }

    private synchronized void fireChanged() {
        for (IStoreImageListener l : this.images) {
            l.platformChanged();
        }
    }

    private void parse(ArrayList<IImageEntry> imageEntries, IResource resource) {
        if (resource instanceof IContainer) {
            if (resource.getName().charAt(0) != '.') {
                try {
                    IResource[] listFiles = ((IContainer)resource).members();
                    int n = listFiles.length;
                    int n2 = 0;
                    while (n2 < n) {
                        IResource child = listFiles[n2];
                        this.parse(imageEntries, child);
                        ++n2;
                    }
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        } else if (AbstractWorskspaceImageEntry.isImage(resource.getName())) {
            imageEntries.add(new IFileImageEntry((IFile)resource));
        }
    }

    public static boolean isImage(String name) {
        if (!(name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".jpeg") || name.endsWith(".tiff"))) {
            return false;
        }
        return true;
    }

    @Override
    public synchronized void addListener(IStoreImageListener e) {
        this.images.add(e);
    }

    @Override
    public synchronized void removeListener(IStoreImageListener e) {
        this.images.remove(e);
    }

}

