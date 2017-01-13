/*
 * Decompiled with CFR 0_118.
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.IImageEntry;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ItemGroup {
    String name;
    ArrayList<IImageEntry> childItems = new ArrayList<IImageEntry>();

    public ItemGroup(String name, ArrayList<IImageEntry> childItems) {
        this.name = name;
        this.childItems = childItems;
    }

    public String getName() {
        return this.name;
    }

    public int getChildCount() {
        return this.childItems.size();
    }

    public IImageEntry getImage(int num) {
        return this.childItems.get(num);
    }
}

