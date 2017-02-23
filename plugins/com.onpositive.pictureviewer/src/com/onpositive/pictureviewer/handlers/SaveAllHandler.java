package com.onpositive.pictureviewer.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import com.onpositive.pictureviewer.AbstractImageStore;
import com.onpositive.pictureviewer.ItemGroup;
import com.onpositive.pictureviewer.PlatformImages;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SaveAllHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SaveAllHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		DirectoryDialog dialog = new DirectoryDialog(window.getShell());
		final String res = dialog.open();
		if (res != null) {
			Job saveJob = new Job("Save all icons") {
				
				@Override
				protected IStatus run(IProgressMonitor arg0) {
					Collection<ItemGroup> imageGroups = PlatformImages.getInstance().getContents();
					for (ItemGroup itemGroup : imageGroups) {
						AbstractImageStore.saveGroup(itemGroup, res);
					}
					return Status.OK_STATUS;
				}
			};
			saveJob.schedule();
		}
		return null;
	}
}
