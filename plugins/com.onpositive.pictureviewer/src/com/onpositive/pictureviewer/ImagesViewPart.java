/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.jface.action.Action
 *  org.eclipse.jface.action.IAction
 *  org.eclipse.jface.action.IContributionItem
 *  org.eclipse.jface.action.IToolBarManager
 *  org.eclipse.jface.action.MenuManager
 *  org.eclipse.jface.action.Separator
 *  org.eclipse.jface.action.ToolBarManager
 *  org.eclipse.jface.resource.ImageDescriptor
 *  org.eclipse.swt.custom.CTabFolder
 *  org.eclipse.swt.custom.CTabItem
 *  org.eclipse.swt.dnd.Clipboard
 *  org.eclipse.swt.dnd.DragSource
 *  org.eclipse.swt.dnd.DragSourceEvent
 *  org.eclipse.swt.dnd.DragSourceListener
 *  org.eclipse.swt.dnd.FileTransfer
 *  org.eclipse.swt.dnd.TextTransfer
 *  org.eclipse.swt.dnd.Transfer
 *  org.eclipse.swt.events.DisposeEvent
 *  org.eclipse.swt.events.DisposeListener
 *  org.eclipse.swt.events.ModifyEvent
 *  org.eclipse.swt.events.ModifyListener
 *  org.eclipse.swt.events.TreeEvent
 *  org.eclipse.swt.events.TreeListener
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.graphics.ImageData
 *  org.eclipse.swt.graphics.Point
 *  org.eclipse.swt.layout.FillLayout
 *  org.eclipse.swt.layout.GridData
 *  org.eclipse.swt.layout.GridLayout
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Label
 *  org.eclipse.swt.widgets.Layout
 *  org.eclipse.swt.widgets.Listener
 *  org.eclipse.swt.widgets.Menu
 *  org.eclipse.swt.widgets.Text
 *  org.eclipse.swt.widgets.ToolBar
 *  org.eclipse.swt.widgets.Widget
 *  org.eclipse.ui.IActionBars
 *  org.eclipse.ui.IViewSite
 *  org.eclipse.ui.part.ViewPart
 *  org.eclipse.ui.plugin.AbstractUIPlugin
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.Activator;
import com.onpositive.pictureviewer.DefaultToolTip;
import com.onpositive.pictureviewer.GalleryTooltip;
import com.onpositive.pictureviewer.IImageEntry;
import com.onpositive.pictureviewer.IImageEntryCallback;
import com.onpositive.pictureviewer.IImageStore;
import com.onpositive.pictureviewer.IStoreImageListener;
import com.onpositive.pictureviewer.ImageCache;
import com.onpositive.pictureviewer.ImageTransferWrapper;
import com.onpositive.pictureviewer.ItemGroup;
import com.onpositive.pictureviewer.PlatformImages;
import com.onpositive.pictureviewer.SelectionImages;
import com.onpositive.pictureviewer.StringMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ImagesViewPart
extends ViewPart {
    private static final String GROP_RENDERER_TAG = "g";
	private static final int EXPAND_THRESHOLD = 20;
	private DefaultToolTip tooltip;
    ImageDescriptor zoom = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/zoom_in.gif");
    ImageDescriptor zoomout = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/zoom_out.gif");
    ImageDescriptor clearCo = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/clear_co.gif");
    ImageDescriptor collapse = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/collapseall.gif");
    ImageDescriptor expand = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/expandall.gif");
    private Text textFilter;
    private CTabFolder fld;
    private String pattern;

    public void dispose() {
        this.tooltip.hide();
        ImageCache.clearCache();
        super.dispose();
    }

    public void createPartControl(Composite parent) {
        PlatformImages store = PlatformImages.getInstance();
        SelectionImages store1 = SelectionImages.getSelectionImages();
        parent.setLayout((Layout)new GridLayout(1, false));
        Composite mn = new Composite(parent, 0);
        mn.setLayout((Layout)new GridLayout(3, false));
        Label ls = new Label(mn, 0);
        ls.setText("Filter:");
        mn.setLayoutData((Object)new GridData(768));
        this.textFilter = new Text(mn, 2048);
        this.textFilter.setLayoutData((Object)new GridData(768));
        this.textFilter.addModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) {
                ImagesViewPart.this.refilter(ImagesViewPart.this.textFilter);
            }
        });
        ToolBar bs = new ToolBar(mn, 0);
        ToolBarManager man = new ToolBarManager(bs);
        Action clearFlt = new Action("Clear Filter"){

            public void run() {
                ImagesViewPart.this.textFilter.setText("");
            }
        };
        clearFlt.setImageDescriptor(this.clearCo);
        man.add((IAction)clearFlt);
        man.update(true);
        this.fld = new CTabFolder(parent, 8389632);
        this.configureTab(store, this.fld);
        this.configureTab(store1, this.fld);
        this.fld.setSelection(0);
        Action action = new Action(){

            public void run() {
                CTabItem[] items = ImagesViewPart.this.fld.getItems();
                int n = items.length;
                int n2 = 0;
                while (n2 < n) {
                    CTabItem i = items[n2];
                    DefaultGalleryGroupRenderer rr = (DefaultGalleryGroupRenderer)i.getData(GROP_RENDERER_TAG);
                    ImagesViewPart.this.zoomIn(rr);
                    ++n2;
                }
            }
        };
        this.fld.setLayoutData((Object)new GridData(1808));
        action.setText("Zoom In");
        action.setImageDescriptor(this.zoom);
        Action action2 = new Action(){

            public void run() {
                CTabItem[] items = ImagesViewPart.this.fld.getItems();
                int n = items.length;
                int n2 = 0;
                while (n2 < n) {
                    CTabItem i = items[n2];
                    DefaultGalleryGroupRenderer rr = (DefaultGalleryGroupRenderer)i.getData(GROP_RENDERER_TAG);
                    ImagesViewPart.this.zoomOut(rr);
                    ++n2;
                }
            }
        };
        action2.setText("Zoom Out");
        action2.setImageDescriptor(this.zoomout);
        IActionBars actionBars = this.getViewSite().getActionBars();
        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        Action colapseA = new Action(){

            public void run() {
                CTabItem[] items = ImagesViewPart.this.fld.getItems();
                int n = items.length;
                int n2 = 0;
                while (n2 < n) {
                    CTabItem i = items[n2];
                    Collapser rr = (Collapser)i.getData("ga");
                    rr.collapse();
                    ++n2;
                }
            }
        };
        Action expand = new Action(){

            public void run() {
                CTabItem[] items = ImagesViewPart.this.fld.getItems();
                int n = items.length;
                int n2 = 0;
                while (n2 < n) {
                    CTabItem i = items[n2];
                    Collapser rr = (Collapser)i.getData("ga");
                    rr.expand();
                    ++n2;
                }
            }
        };
        expand.setText("Expand All");
        expand.setImageDescriptor(this.expand);
        colapseA.setText("Collapse All");
        colapseA.setImageDescriptor(this.collapse);
        toolBarManager.add((IAction)expand);
        toolBarManager.add((IAction)colapseA);
        toolBarManager.add((IContributionItem)new Separator());
        toolBarManager.add((IAction)action);
        toolBarManager.add((IAction)action2);
        actionBars.updateActionBars();
    }

    protected void refilter(Text textFilter2) {
        this.pattern = textFilter2.getText();
        CTabItem[] items = this.fld.getItems();
        int n = items.length;
        int n2 = 0;
        while (n2 < n) {
            CTabItem i = items[n2];
            Refresher rr = (Refresher)i.getData("r");
            rr.refresh();
            ++n2;
        }
    }

    private void configureTab(final IImageStore store, CTabFolder fld) {
        CTabItem item = new CTabItem(fld, 0);
        item.setText(store.getName());
        Composite cm = new Composite((Composite)fld, 0);
        cm.setLayout((Layout)new FillLayout());
        final Gallery gallery = new Gallery(cm, 268438016 | SWT.V_SCROLL);
        item.setControl((Control)cm);
//        gallery.setVertical(false);
        DefaultGalleryGroupRenderer groupRenderer = new DefaultGalleryGroupRenderer();
        gallery.setGroupRenderer(groupRenderer);
        gallery.setItemRenderer(new DefaultGalleryItemRenderer(){
        	
        	@Override
        	public void draw(GC gc, GalleryItem item, int index, int x, int y,
        			int width, int height) {
        		if (item.getImage() == null) {
        			IImageEntry entry = (IImageEntry)item.getData();
                    try {
                        item.setImage(ImageCache.getImage(item, entry));
                    }
                    catch (Exception e) {
                        Activator.log(e);
                    }
        		}
        		super.draw(gc, item, index, x, y, width, height);
        	}

//            protected Image getImage(GalleryItem item) {
//                IImageEntry entry = (IImageEntry)item.getData();
//                try {
//                    return ImageCache.getImage(entry);
//                }
//                catch (Exception e) {
//                    Activator.log(e);
//                    return null;
//                }
//            }
        });
        DragSource dragSource = new DragSource((Control)gallery, 1);
        dragSource.setTransfer(new Transfer[]{FileTransfer.getInstance()});
        dragSource.addDragListener(new DragSourceListener(){
            private String[] dataX;

            public void dragFinished(DragSourceEvent event) {
                this.dataX = null;
            }

            public void dragSetData(DragSourceEvent event) {
                event.data = this.dataX;
            }

            public void dragStart(DragSourceEvent event) {
                Object data;
                event.detail = 1;
                GalleryItem item2 = gallery.getItem(new Point(event.x, event.y));
                if (item2 != null && (data = item2.getData()) instanceof IImageEntry) {
                    IImageEntry e = (IImageEntry)data;
                    String file = e.getFile();
                    this.dataX = new String[]{file};
                    try {
                        event.image = e.getImage();
                    }
                    catch (IOException e1) {
                        Activator.log(e1);
                    }
                }
            }
        });
        final IImageEntryCallback cb = new IImageEntryCallback(){

			@Override
			public void imagesLoaded(List<GalleryItem> toUpdate) {
				for (GalleryItem galleryItem : toUpdate) {
					IImageEntry entry = (IImageEntry)galleryItem.getData();
                    try {
                    	galleryItem.setImage(ImageCache.getImage(galleryItem, entry));
                    }
                    catch (Exception e) {
                        Activator.log(e);
                    }
				}
				gallery.redraw();
			}
        };
        ImageCache.addCallback(cb);
        gallery.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent e) {
                ImageCache.removeCallback(cb);
            }
        });
        item.setData(GROP_RENDERER_TAG, groupRenderer);
//        gr.setDrawVertically(false);
        groupRenderer.setItemHeight(56);
        groupRenderer.setItemWidth(72);
        this.fillContextMenu(gallery, groupRenderer);
        this.tooltip = new GalleryTooltip((Control)gallery, gallery);
        final ArrayList<Object> images = new ArrayList<Object>(store.getContents());
        this.prepareImages(images);
        gallery.addListener(SWT.SetData, new Listener(){

            public void handleEvent(Event event) {
                GalleryItem item = (GalleryItem)event.item;
                GalleryItem parentItem = item.getParentItem();
                if (parentItem == null) {
                    int index = gallery.indexOf(item);
                    ItemGroup itemGroup = (ItemGroup)images.get(index);
                    item.setText(itemGroup.getName());
                    item.setData((Object)itemGroup);
                    item.setItemCount(itemGroup.getChildCount());
                } else {
                    int indexOf = parentItem.indexOf(item);
                    ItemGroup ga = (ItemGroup)parentItem.getData();
                    item.setItemCount(0);
                    IImageEntry image = ga.getImage(indexOf);
                    item.setText(image.getName());
                    item.setData((Object)image);
                }
            }
        });
        final HashSet<ItemGroup> expanded = new HashSet<>();
        item.setData("ga", (Object)new Collapser(){

            public void collapse() {
                int a = 0;
                while (a < gallery.getItemCount()) {
                    GalleryItem item2 = gallery.getItem(a);
                    item2.setExpanded(false);
                    ++a;
                }
                expanded.clear();
            }

            public void expand() {
                int a = 0;
                while (a < gallery.getItemCount()) {
                    GalleryItem item2 = gallery.getItem(a);
                    item2.setExpanded(true);
                    expanded.add((ItemGroup) item2.getData());
                    ++a;
                }
            }
        });
        gallery.addTreeListener(new TreeListener(){

            public void treeCollapsed(TreeEvent e) {
                expanded.remove(e.data);
            }

            public void treeExpanded(TreeEvent e) {
                expanded.add((ItemGroup) e.data);
            }
        });
        final IStoreImageListener storeImageListener = new IStoreImageListener(){

            public void platformChanged() {
            	ArrayList<ItemGroup> contents = new ArrayList<>(store.getContents());
                Display.getDefault().asyncExec(new Runnable(){

                    public void run() {
                        refresh(gallery, images, expanded, contents);
                    }
                });
            }

        };
        item.setData("r", (Object)new Refresher(){

            public void refresh() {
                ArrayList<ItemGroup> contents = new ArrayList<>(store.getContents());
                ImagesViewPart.this.refresh(gallery, images, expanded, new ArrayList<>(contents));
            }
        });
        store.addListener(storeImageListener);
        gallery.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent e) {
                store.removeListener(storeImageListener);
            }
        });
        gallery.setItemCount(images.size());
    }

    private void refresh(Gallery gallery, ArrayList<Object> images, HashSet<ItemGroup> expanded, ArrayList<?> contents) {
        images.clear();
        int passedCount = 0;
        if (this.pattern == null || this.pattern.trim().length() == 0) {
            images.addAll(contents);
            passedCount += contents.size();
        } else {
            StringMatcher patternMatcher = new StringMatcher("*" + this.pattern + "*", true, false);
            for (Object o : contents) {
                if (!(o instanceof ItemGroup)) continue;
                ItemGroup group = (ItemGroup)o;
                if (patternMatcher.match(group.getName())) {
                    images.add(o);
                    passedCount += group.getChildCount();
                    continue;
                }
                ArrayList<IImageEntry> z = new ArrayList<IImageEntry>();
                int a = 0;
                while (a < group.getChildCount()) {
                    IImageEntry image = group.getImage(a);
                    if (patternMatcher.match(image.getName())) {
                        z.add(image);
                    }
                    ++a;
                }
                if (z.isEmpty()) continue;
                passedCount += z.size();
                images.add(new ItemGroup(group.getName(), z));
            }
        }
        this.prepareImages(images);
        gallery.setItemCount(images.size());
        gallery.clearAll();
        if (passedCount <= EXPAND_THRESHOLD) {
        	for (int i = 0; i < images.size(); i++) {
        		gallery.getItem(i).setExpanded(true);
			}
        } else {
	        for (Object o : expanded) {
	            int indexOf = images.indexOf(o);
	            if (indexOf == -1) continue;
	            gallery.getItem(indexOf).setExpanded(true);
	        }
        }
    }

    private void prepareImages(ArrayList<Object> images) {
        Collections.sort(images, new Comparator<Object>(){

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof ItemGroup) {
                    if (o2 instanceof ItemGroup) {
                        ItemGroup i1 = (ItemGroup)o1;
                        ItemGroup i2 = (ItemGroup)o2;
                        return i1.getName().compareTo(i2.getName());
                    }
                    return -1;
                }
                return 1;
            }
        });
    }

    private void fillContextMenu(final Gallery gallery, final DefaultGalleryGroupRenderer gr) {
        MenuManager manager = new MenuManager();
        manager.add((IAction)new Action("Copy", 1){

            public void run() {
                GalleryItem[] selection = gallery.getSelection();
                boolean av = ImageTransferWrapper.isAvalable();
                Object[] data = new Object[selection.length * (av ? 3 : 2)];
                int a = 0;
                GalleryItem[] arrgalleryItem = selection;
                int n = arrgalleryItem.length;
                int n2 = 0;
                while (n2 < n) {
                    GalleryItem s = arrgalleryItem[n2];
                    IImageEntry entry = (IImageEntry)s.getData();
                    data[a++] = new String[]{entry.getFile()};
                    data[a++] = entry.getPath();
                    if (av) {
                        try {
                            data[a++] = entry.getImage().getImageData();
                        }
                        catch (IOException e) {
                            Activator.log(e);
                        }
                    }
                    ++n2;
                }
                Clipboard clipboard = new Clipboard(Display.getCurrent());
                clipboard.setContents(data, new Transfer[]{FileTransfer.getInstance(), TextTransfer.getInstance(), (Transfer)ImageTransferWrapper.getInstance()});
                clipboard.dispose();
            }
        });
        manager.add((IContributionItem)new Separator());
        Action action = new Action("Zoom In", 1){

            public void run() {
                ImagesViewPart.this.zoomIn(gr);
            }
        };
        action.setImageDescriptor(this.zoom);
        manager.add((IAction)action);
        Action action2 = new Action("Zoom Out", 1){

            public void run() {
                ImagesViewPart.this.zoomOut(gr);
            }
        };
        action2.setImageDescriptor(this.zoomout);
        manager.add((IAction)action2);
        Menu createContextMenu = manager.createContextMenu((Control)gallery);
        gallery.setMenu(createContextMenu);
    }

    public void setFocus() {
    }

    private void zoomOut(DefaultGalleryGroupRenderer gr) {
        int itemHeight = gr.getItemHeight();
        int itemWidth = gr.getItemWidth();
        if (itemHeight > 40 && itemWidth > 56) {
            itemHeight -= 16;
            itemWidth -= 16;
        }
        gr.setItemSize(itemWidth, itemHeight);
    }

    private void zoomIn(DefaultGalleryGroupRenderer gr) {
        int itemHeight = gr.getItemHeight();
        int itemWidth = gr.getItemWidth();
        if (itemHeight < 128) {
            itemHeight += 16;
            itemWidth += 16;
        }
        gr.setItemSize(itemWidth, itemHeight);
    }

    public static abstract class Collapser {
        public abstract void collapse();

        public abstract void expand();
    }

    static abstract class Refresher {
        Refresher() {
        }

        public abstract void refresh();
    }

}

