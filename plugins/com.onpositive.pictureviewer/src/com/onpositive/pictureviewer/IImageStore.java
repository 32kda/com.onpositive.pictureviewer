/*
 * Decompiled with CFR 0_118.
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.IStoreImageListener;
import java.util.Collection;

public interface IImageStore {
    public void addListener(IStoreImageListener var1);

    public void removeListener(IStoreImageListener var1);

    public String getName();

    public Collection<ItemGroup> getContents();
}

