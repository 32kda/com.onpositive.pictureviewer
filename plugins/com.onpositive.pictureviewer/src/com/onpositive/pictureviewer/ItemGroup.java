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
    private String name;
    private ArrayList<IImageEntry> childItems = new ArrayList<IImageEntry>();

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemGroup other = (ItemGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ItemGroup [name=" + name + "]";
	}
}

