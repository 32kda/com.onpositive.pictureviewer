/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.swt.graphics.Device
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.widgets.Display
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.IImageEntry;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class FileImageEntry
implements IImageEntry {
    private final String file;
    private final String relativePath;
    private final String name;

    public FileImageEntry(String file, String name, String relativePath) {
        this.file = file;
        this.name = name;
        this.relativePath = relativePath;
    }

    public int hashCode() {
        int result = 1;
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
        FileImageEntry other = (FileImageEntry)obj;
        if (this.file == null ? other.file != null : !this.file.equals(other.file)) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public InputStream getStream() throws IOException {
        return new FileInputStream(this.file);
    }

    public Image getImage() throws IOException {
        return new Image((Device)Display.getDefault(), this.getStream());
    }

    public String getName() {
        return this.name;
    }

    public String getFile() {
        return this.file;
    }

    public String getPath() {
        return this.relativePath;
    }
}

