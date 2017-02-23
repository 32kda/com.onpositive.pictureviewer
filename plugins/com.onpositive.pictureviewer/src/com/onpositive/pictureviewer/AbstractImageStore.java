/*
 * Decompiled with CFR 0_118.
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.FileImageEntry;
import com.onpositive.pictureviewer.IImageEntry;
import com.onpositive.pictureviewer.IImageStore;
import com.onpositive.pictureviewer.IStoreImageListener;
import com.onpositive.pictureviewer.ItemGroup;
import com.onpositive.pictureviewer.JarImageEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractImageStore
implements IImageStore {
    HashSet<IStoreImageListener> images = new HashSet<IStoreImageListener>();
    protected ArrayList<ItemGroup> imageList = new ArrayList<ItemGroup>();

    @Override
    public synchronized Collection<ItemGroup> getContents() {
        return new ArrayList<ItemGroup>(this.imageList);
    }

    public void init() {
        Thread s = new Thread(){

            public void run() {
                AbstractImageStore.this.initPlatform();
            }
        };
        s.start();
    }

    synchronized void reprocess(HashSet<String> names) {
        File file = this.getFile();
        HashSet<ItemGroup> toRemove = new HashSet<ItemGroup>();
        HashSet<ItemGroup> toAdd = new HashSet<ItemGroup>();
        for (ItemGroup currentGroup : this.imageList) {
            if (!names.contains(currentGroup.getName())) continue;
            toRemove.add(currentGroup);
            names.remove(currentGroup.getName());
            ArrayList<IImageEntry> entries = new ArrayList<IImageEntry>();
            ItemGroup newGroup = new ItemGroup(currentGroup.getName(), entries);
            File file2 = new File(file, currentGroup.getName());
            this.parse(entries, file2, file2);
            if (entries.isEmpty()) continue;
            toAdd.add(newGroup);
        }
        for (String name : names) {
            ArrayList<IImageEntry> entries = new ArrayList<IImageEntry>();
            ItemGroup newGroup = new ItemGroup(name, entries);
            File file2 = new File(file, name);
            this.parse(entries, file2, file2);
            if (entries.isEmpty()) continue;
            toAdd.add(newGroup);
        }
        this.imageList.removeAll(toRemove);
        this.imageList.addAll(toAdd);
        this.fireChanged();
    }

    public abstract File getFile();

    protected void initPlatform() {
        this.imageList.clear();
        this.fireChanged();
        File file = this.getFile();
        if (file != null && file.exists()) {
            File[] listFiles = file.listFiles();
            int n = listFiles.length;
            int n2 = 0;
            while (n2 < n) {
                File curFile = listFiles[n2];
                if (curFile.getName().charAt(0) != '.') {
                    ArrayList<IImageEntry> entries = new ArrayList<IImageEntry>();
                    ItemGroup group = new ItemGroup(curFile.getName(), entries);
                    this.parse(entries, curFile, curFile);
                    if (group.getChildCount() > 0) {
                        this.fetched(group);
                    }
                }
                ++n2;
            }
        }
        this.fireChanged();
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

    private void parse(ArrayList<IImageEntry> ls, File f, File root) {
        if (f.getName().endsWith(".jar")) {
            try {
                JarFile jar = new JarFile(f);
                try {
                    this.process(ls, jar, f);
                }
                finally {
                    jar.close();
                }
            }
            catch (IOException v0) {}
        } else if (f.isDirectory()) {
            if (f.getName().charAt(0) != '.') {
                File[] listFiles  = f.listFiles();
                int n = listFiles.length;
                int n2 = 0;
                while (n2 < n) {
                    File fa = listFiles[n2];
                    this.parse(ls, fa, root);
                    ++n2;
                }
            }
        } else if (AbstractImageStore.isImage(f.getName())) {
            int length = root.getAbsolutePath().length() + 1;
            String absolutePath = f.getAbsolutePath();
            absolutePath = absolutePath.replace('\\', '/');
            ls.add(new FileImageEntry(absolutePath, f.getName(), absolutePath.substring(length)));
        }
    }

    private void process(ArrayList<IImageEntry> ls, JarFile jar, File f) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry e = entries.nextElement();
            String name2 = e.getName();
            String name = name2;
            if (!AbstractImageStore.isImage(name)) continue;
            String nm = name2;
            int lastIndexOf = nm.lastIndexOf('/');
            if (lastIndexOf > -1) {
                nm = nm.substring(lastIndexOf + 1);
            }
            String substring = nm;
            ls.add(new JarImageEntry(f.getAbsolutePath(), name2, substring));
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
    
	public static void saveGroup(ItemGroup group, String folder) {
		String name = group.getName();
		name = new File(name).getName();
		int idx = name.indexOf('_');
		if (idx > 0) {
			name = name.substring(0, idx);
		}
		File dir = new File(folder, name);
		dir.mkdirs();
		for (int i = 0; i < group.getChildCount(); i++) {
			IImageEntry image = group.getImage(i);
			String fileName = image.getFile();
			File srcFile = new File(fileName);
			File destFile = new File(dir, srcFile.getName());
			if (!destFile.exists()) {
				try  
				{
					Files.copy(new File(fileName).toPath(), destFile.toPath());
				} catch (IOException e) {
					Activator.log(e);
				}
			}
		}

	}

}

