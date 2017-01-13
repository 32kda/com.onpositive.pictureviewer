/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.IPath
 *  org.eclipse.swt.graphics.Device
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.widgets.Display
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.Activator;
import com.onpositive.pictureviewer.IImageEntry;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class JarImageEntry
implements IImageEntry {
    private final String file;
    private final String entryPath;
    private final String name;
    static WeakHashMap<String, JarFile> files = new WeakHashMap<String, JarFile>();

    public JarImageEntry(String file, String entryPath, String name) {
        this.file = file;
        this.entryPath = entryPath;
        this.name = name;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.entryPath == null ? 0 : this.entryPath.hashCode());
        result = 31 * result + (this.file == null ? 0 : this.file.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        JarImageEntry other = (JarImageEntry)obj;
        if (this.entryPath == null ? other.entryPath != null : !this.entryPath.equals(other.entryPath)) {
            return false;
        }
        if (this.file == null ? other.file != null : !this.file.equals(other.file)) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public Image getImage() throws IOException {
        JarFile file = files.get(this.file);
        if (file == null) {
            file = new JarFile(new File(this.file));
            files.put(this.file, file);
        }
        JarEntry entry = file.getJarEntry(this.entryPath);
        InputStream inputStream = file.getInputStream(entry);
        Image image = new Image((Device)Display.getDefault(), inputStream);
        inputStream.close();
        return image;
    }

    public String getName() {
        return this.name;
    }

    public String getFile() {
        try {
            File file2 = Activator.getDefault().getStateLocation().toFile();
            File createTempFile = new File(file2, this.name);
            JarFile file = new JarFile(new File(this.file));
            JarEntry entry = file.getJarEntry(this.entryPath);
            InputStream inputStream = file.getInputStream(entry);
            FileOutputStream st = new FileOutputStream(createTempFile);
            BufferedInputStream sa = new BufferedInputStream(inputStream);
            while (sa.available() >= 0) {
                int read = sa.read();
                if (read == -1) break;
                st.write(read);
            }
            st.close();
            file.close();
            createTempFile.deleteOnExit();
            return createTempFile.getAbsolutePath();
        }
        catch (IOException e) {
            Activator.log(e);
            return null;
        }
    }

    public String getPath() {
        if (this.entryPath.length() > 0 && this.entryPath.charAt(0) == '/') {
            return this.entryPath.substring(1);
        }
        return this.entryPath;
    }

    public InputStream getStream() {
        try (JarFile file = new JarFile(new File(this.file));) {
	        JarEntry entry = file.getJarEntry(this.entryPath);
	        InputStream inputStream = file.getInputStream(entry);
//	        file.close();
	        return inputStream;
        } catch (IOException e) {
        	Activator.log(e);
		}
		return null;
    }
}

