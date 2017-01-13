/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.resources.IFile
 *  org.eclipse.core.runtime.CoreException
 *  org.eclipse.core.runtime.IPath
 *  org.eclipse.swt.graphics.Device
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.widgets.Display
 */
package com.onpositive.pictureviewer;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class IFileImageEntry
implements IImageEntry {
    private final IFile file;
    private final String name;

    public IFileImageEntry(IFile file) {
        this.name = file.getName();
        this.file = file;
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
        IFileImageEntry other = (IFileImageEntry)obj;
        if (this.file == null ? other.file != null : !this.file.equals((Object)other.file)) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public InputStream getStream() throws IOException {
        try {
            return this.file.getContents();
        }
        catch (CoreException e) {
            throw new IOException((Throwable)e);
        }
    }

    public Image getImage() throws IOException {
        InputStream stream = this.getStream();
        try {
            Image image = new Image((Device)Display.getDefault(), stream);
            return image;
        }
        finally {
            stream.close();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getFile() {
        return this.file.getFullPath().toPortableString();
    }

    public String getPath() {
        return this.file.getProjectRelativePath().toPortableString();
    }
}

