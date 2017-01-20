/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.onpositive.pictureviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ImageCache {
	
	private static class Loader implements Runnable {

		@Override
		public void run() {
			List<GalleryItem> toUpdate = new ArrayList<GalleryItem>();
			synchronized (ImageCache.class) {
				List<IImageEntry> toLoad = new ArrayList<>();
				toLoad.addAll(loadedStack);
				loadedStack.clear();
				
				toUpdate.addAll(itemsToUpdate);
				itemsToUpdate.clear();
				
				for (IImageEntry imageEntry : toLoad) {
					try {
						Image image = imageEntry.getImage();
						ImageCache.images.put(imageEntry, image);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			notifyLoad(toUpdate);
		}
		
	}
	
	private static final int MAX_ENTRIES = 500;
	
    private static final ScheduledExecutorService scheduler =
    	       Executors.newScheduledThreadPool(5);
	
	private static LinkedHashMap<IImageEntry, Image> images = new LinkedHashMap<IImageEntry, Image>() {

		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(java.util.Map.Entry<IImageEntry,Image> entry) {
			return size() > MAX_ENTRIES;
		};
		
		public Image remove(Object key) {
			Image img = super.remove(key);
			img.dispose();
			return img;
		};
		
	};

	private static HashSet<IImageEntryCallback> callbacks = new HashSet<IImageEntryCallback>();

	private static LinkedBlockingQueue<IImageEntry> loadedStack = new LinkedBlockingQueue<>();
	private static LinkedBlockingQueue<GalleryItem> itemsToUpdate = new LinkedBlockingQueue<>();
	
	private static Image LOADING_IMG = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/hourglass.png").createImage();

//	private static int total = 0;

//	private static Thread imageLoader = new Thread() {
//		public void run() {
//			try {
//				while(loadedStack.size() > 0) {
//					IImageEntry image2 = (IImageEntry) ImageCache.loadedStack
//							.take();
//	//				long l0 = System.currentTimeMillis();
//	//				long l1 = l0;
//					try {
//						Image im = image2.getImage();
//						ImageCache.total += 1;
//	//					System.out.println(ImageCache.total);
//	//					System.currentTimeMillis();
//	
//						synchronized (ImageCache.class) {
//							try {
//								ImageCache.images.put(image2, im);
//								if (ImageCache.images.size() > 500) {
//									Image remove = (Image) ImageCache.images
//											.remove(ImageCache.images.keySet()
//													.iterator().next());
//									remove.dispose();
//									System.out.println("Disposing:"
//											+ ImageCache.images.size());
//								}
//							} catch (Exception e) {
//								Activator.log(e);
//							}
//						}
//					} catch (Error e) {
//						Activator.log(e);
//	
//						while (ImageCache.loadedStack.size() > 100) {
//							ImageCache.loadedStack.remove();
//						}
//	//					image2 = (IImageEntry) ImageCache.loadedStack.poll();
//	//					l1 = System.currentTimeMillis();
//	//					if (l1 - l0 < 300L)
//	//						if (image2 != null)
//	//							;
//					}
//				}
//				notifyLoad();
//			} catch (Exception e1) {
//				Activator.log(e1);
//			}
//		}
//	};

//	static {
//		imageLoader.setPriority(10);
//		imageLoader.setDaemon(true);
//		imageLoader.start();
//	}

	public static void addCallback(IImageEntryCallback cb) {
		callbacks.add(cb);
	}

	public static void removeCallback(IImageEntryCallback cba) {
		callbacks.remove(cba);
	}

	public static Image getImage(GalleryItem item, IImageEntry entry) throws IOException {
		synchronized (ImageCache.class) {
			Image image = (Image) images.get(entry);
			if (image != null) {
				return image;
			}
			if (loadedStack.isEmpty()) {
				scheduler.schedule(new Loader(), 100, TimeUnit.MILLISECONDS);
			}
			loadedStack.add(entry);
			itemsToUpdate.add(item);
		}
		return LOADING_IMG;
	}

	public static void clearCache() {
		for (Image m : images.values()) {
			m.dispose();
		}
		images.clear();
	}

	private static void notifyLoad(final List<GalleryItem> toUpdate) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				for (IImageEntryCallback c : ImageCache.callbacks)
					c.imagesLoaded(toUpdate);
			}
		});
	}
}