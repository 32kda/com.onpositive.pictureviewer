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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
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
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ImagesViewPart extends ViewPart {
	
	private static final String REFRESHER_ID = "refresher";
	private static final String COLLAPSER_ID = "collapser";
	private static final int SIZE_STEP = 16;

	private class CopyAction extends Action {
		

		public CopyAction() {
			super("Copy", SWT.PUSH);
		}
		
		public void run() {
			IImageEntry[] selection = ImagesViewPart.this.getSelection();
			if (selection == null || selection.length == 0) {
				return;
			}
            boolean imageTransfer = ImageTransferWrapper.isAvalable();
            StringBuilder builderData = new StringBuilder();
            String[] fileData = new String[selection.length];
            for (int i = 0; i < selection.length; i++) {
            	fileData[i] = selection[i].getFile();
            	if (i>=1) {
            		builderData.append(';');	
            	}
            	builderData.append(selection[i].getPath());
			}
            ImageData imageData = null; 
            if (imageTransfer) {
            	try {
            		imageData = selection[0].getImage().getImageData();
            	}
            	catch (IOException e) {
            		Activator.log(e);
            	}
            }
            Clipboard clipboard = new Clipboard(Display.getCurrent());
            Object[] contents = imageTransfer?new Object[]{fileData, builderData.toString(), imageData}:new Object[]{fileData, builderData.toString()};
            Transfer[] transfers = imageTransfer?new Transfer[]{FileTransfer.getInstance(), TextTransfer.getInstance(), (Transfer)ImageTransferWrapper.getInstance()}:
            	new Transfer[]{FileTransfer.getInstance(), TextTransfer.getInstance()};
			clipboard.setContents(contents, transfers);
            clipboard.dispose();
        }
		
	}
	
    private static final int ITEM_WIDTH = 72;
	private static final int ITEM_HEIGHT = 56;
	private static final String GROP_RENDERER_TAG = "g";
	private static final int EXPAND_THRESHOLD = 20;
	private DefaultToolTip tooltip;
    ImageDescriptor zoom = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/zoom_in.gif");
    ImageDescriptor zoomout = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/zoom_out.gif");
    ImageDescriptor clearCo = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/clear_co.gif");
    ImageDescriptor collapse = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/collapseall.gif");
    ImageDescriptor expand = AbstractUIPlugin.imageDescriptorFromPlugin((String)"com.onpositive.pictureviewer", (String)"/icons/expandall.gif");
    private Text textFilter;
    private CTabFolder tabFolder;
    private String pattern;
    private Map<CTabItem, Gallery> tabGalleries = new HashMap<>();
    private SelectionProviderAdapter selectionHandler = new SelectionProviderAdapter(this); 

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
        this.tabFolder = new CTabFolder(parent, 8389632);
        this.configureTab(store, this.tabFolder);
        this.configureTab(store1, this.tabFolder);
        this.tabFolder.setSelection(0);
        this.tabFolder.addSelectionListener(new SelectionAdapter() {
        	
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		selectionHandler.refresh();
        	}
        	
		});
        Action action = new Action(){

            public void run() {
                CTabItem[] items = ImagesViewPart.this.tabFolder.getItems();
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
        this.tabFolder.setLayoutData((Object)new GridData(1808));
        action.setText("Zoom In");
        action.setImageDescriptor(this.zoom);
        Action action2 = new Action(){

            public void run() {
                CTabItem[] items = ImagesViewPart.this.tabFolder.getItems();
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
        Action colapseAll = new Action(){

            public void run() {
                setAllExpanded(false);
            }

        };
        Action expandAll = new Action(){

            public void run() {
            	setAllExpanded(true);
            }
        };
        expandAll.setText("Expand All");
        expandAll.setImageDescriptor(this.expand);
        colapseAll.setText("Collapse All");
        colapseAll.setImageDescriptor(this.collapse);
        toolBarManager.add((IAction)expandAll);
        toolBarManager.add((IAction)colapseAll);
        toolBarManager.add((IContributionItem)new Separator());
        toolBarManager.add((IAction)action);
        toolBarManager.add((IAction)action2);
        actionBars.updateActionBars();
        
        final IHandlerService handlerService = (IHandlerService) (getSite()).getService(IHandlerService.class);
        final IHandlerActivation activation = handlerService
        		.activateHandler(IWorkbenchCommandConstants.EDIT_COPY,
        		new ActionHandler(new CopyAction()));

		// clean up by deactivating the handler. The focus tracker will
		// be removed on dispose automatically
		tabFolder.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				handlerService.deactivateHandler(activation);
			}
		});
        
        getSite().setSelectionProvider(selectionHandler);
    }
    
	private void setAllExpanded(boolean expanded) {
		CTabItem[] items = ImagesViewPart.this.tabFolder.getItems();
        int n = items.length;
        int n2 = 0;
        while (n2 < n) {
            CTabItem i = items[n2];
            Collapser rr = (Collapser)i.getData(COLLAPSER_ID);
            if (expanded) {
            	rr.expand();
            } else {
            	rr.collapse();
            }
            ++n2;
        }
	}


    protected void refilter(Text textFilter2) {
        this.pattern = textFilter2.getText();
        CTabItem[] items = this.tabFolder.getItems();
        int n = items.length;
        int n2 = 0;
        while (n2 < n) {
            CTabItem i = items[n2];
            Refresher rr = (Refresher)i.getData(REFRESHER_ID);
            rr.refresh();
            ++n2;
        }
    }

    private void configureTab(final IImageStore store, CTabFolder fld) {
        CTabItem item = new CTabItem(fld, SWT.NONE);
        item.setText(store.getName());
        Composite cm = new Composite((Composite)fld, SWT.NONE);
        cm.setLayout((Layout)new FillLayout());
        final Gallery gallery = new Gallery(cm, 268438016 | SWT.V_SCROLL | SWT.MULTI);
        tabGalleries.put(item, gallery);
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

        });
        configureDragAndCopy(gallery);
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
        gallery.addSelectionListener(new SelectionAdapter() {
        	
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		selectionHandler.refresh();
        	}
        	
		});
        item.setData(GROP_RENDERER_TAG, groupRenderer);
//        gr.setDrawVertically(false);
        groupRenderer.setItemHeight(ITEM_HEIGHT);
        groupRenderer.setItemWidth(ITEM_WIDTH);
        this.fillContextMenu(gallery, groupRenderer);
        this.tooltip = new GalleryTooltip((Control)gallery, gallery);
        final ArrayList<ItemGroup> imageGroups = new ArrayList<ItemGroup>(store.getContents());
        this.prepareImages(imageGroups);
        gallery.addListener(SWT.SetData, new Listener(){

            public void handleEvent(Event event) {
                GalleryItem item = (GalleryItem)event.item;
                GalleryItem parentItem = item.getParentItem();
                if (parentItem == null) {
                    int index = gallery.indexOf(item);
                    ItemGroup itemGroup = (ItemGroup)imageGroups.get(index);
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
        item.setData(COLLAPSER_ID, new Collapser(){

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
            	final ArrayList<ItemGroup> contents = new ArrayList<>(store.getContents());
                Display.getDefault().asyncExec(new Runnable(){

                    public void run() {
                        refresh(gallery, imageGroups, expanded, contents);
                    }
                });
            }

        };
        item.setData(REFRESHER_ID, (Object)new Refresher(){

            public void refresh() {
                ArrayList<ItemGroup> contents = new ArrayList<>(store.getContents());
                ImagesViewPart.this.refresh(gallery, imageGroups, expanded, new ArrayList<>(contents));
            }
        });
        store.addListener(storeImageListener);
        gallery.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent e) {
                store.removeListener(storeImageListener);
            }
        });
        gallery.setItemCount(imageGroups.size());
    }

    public IImageEntry[] getSelection() {
    	CTabItem selectedTab = tabFolder.getSelection();
    	if (selectedTab != null) {
    		Gallery gallery = tabGalleries.get(selectedTab);
    		GalleryItem[] selection = gallery.getSelection();
    		if (selection.length > 0 && selection[0].getData() instanceof IImageEntry) {
    			IImageEntry[] result = new IImageEntry[selection.length];
    			for (int i = 0; i < result.length; i++) {
					result[i] = (IImageEntry) selection[i].getData(); 
				}
    			return result;
    		}
    	}
		return null;
    	
    }
    
	private void configureDragAndCopy(final Gallery gallery) {
		DragSource dragSource = new DragSource((Control)gallery, DND.DROP_COPY);
        dragSource.setTransfer(new Transfer[]{FileTransfer.getInstance(), TextTransfer.getInstance(), ImageTransfer.getInstance()});
        dragSource.addDragListener(new DragSourceListener(){
            private String[] dataX;
            private ImageData imgData;

            public void dragFinished(DragSourceEvent event) {
                this.dataX = null;
                this.imgData = null;
            }

            public void dragSetData(DragSourceEvent event) {
            	if (this.dataX != null && this.dataX[0] != null) {
	            	if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
	            		event.data = this.dataX;
	            	} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	                	event.data = new File(this.dataX[0]).getName();
	                } else if (ImageTransfer.getInstance().isSupportedType(event.dataType) && event.image != null) {
	                	event.data = imgData;
	                } 
            	}
            }

            public void dragStart(DragSourceEvent event) {
                Object data;
                event.detail = DND.DROP_COPY;
                GalleryItem item2 = gallery.getItem(new Point(event.x, event.y));
                IImageEntry[] selection = getSelection();
                if (selection != null && selection.length > 0) {
                	try {
                        event.image = selection[0].getImage();
                        if (event.image != null) {
                        	imgData = event.image.getImageData();
                        }
                    }
                    catch (IOException e1) {
                        Activator.log(e1);
                    }
                	this.dataX = new String[selection.length];
                	for (int i = 0; i < selection.length; i++) {
                		this.dataX[i] = selection[i].getFile();
					}
                } else if (item2 != null && (data = item2.getData()) instanceof IImageEntry) {
                    IImageEntry e = (IImageEntry)data;
                    String file = e.getFile();
                    this.dataX = new String[]{file};
                    try {
                        event.image = e.getImage();
                        if (event.image != null) {
                        	imgData = event.image.getImageData();
                        }

                    }
                    catch (IOException e1) {
                        Activator.log(e1);
                    }
                } else {
                	event.doit = false;
                }
            }
        });
	}

    private void refresh(Gallery gallery, ArrayList<ItemGroup> images, HashSet<ItemGroup> expanded, ArrayList<ItemGroup> contents) {
        images.clear();
        int passedCount = 0;
        if (this.pattern == null || this.pattern.trim().length() == 0) {
            images.addAll((Collection<? extends ItemGroup>) contents);
            passedCount += contents.size();
        } else {
            StringMatcher patternMatcher = new StringMatcher("*" + this.pattern + "*", true, false);
            for (ItemGroup group : contents) {
                if (patternMatcher.match(group.getName())) {
                    images.add(group);
                    passedCount += group.getChildCount();
                    continue;
                }
                ArrayList<IImageEntry> filteredGroup = new ArrayList<IImageEntry>();
                int a = 0;
                while (a < group.getChildCount()) {
                    IImageEntry image = group.getImage(a);
                    if (patternMatcher.match(image.getName())) {
                        filteredGroup.add(image);
                    }
                    ++a;
                }
                if (filteredGroup.isEmpty()) continue;
                passedCount += filteredGroup.size();
                images.add(new ItemGroup(group.getName(), filteredGroup));
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
	        for (ItemGroup group : expanded) {
	            int indexOf = images.indexOf(group);
	            if (indexOf == -1) continue;
	            gallery.getItem(indexOf).setExpanded(true);
	        }
        }
    }

    private void prepareImages(ArrayList<ItemGroup> imageGroups) {
        Collections.sort(imageGroups, new Comparator<Object>(){

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
        manager.add(new CopyAction());
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
        if (itemHeight > 40 && itemWidth > ITEM_HEIGHT) {
            itemHeight -= SIZE_STEP;
            itemWidth -= SIZE_STEP;
        }
        gr.setItemSize(itemWidth, itemHeight);
    }

    private void zoomIn(DefaultGalleryGroupRenderer gr) {
        int itemHeight = gr.getItemHeight();
        int itemWidth = gr.getItemWidth();
        if (itemHeight < 128) {
            itemHeight += SIZE_STEP;
            itemWidth += SIZE_STEP;
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

