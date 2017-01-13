/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.resources.IContainer
 *  org.eclipse.core.resources.IFile
 *  org.eclipse.core.resources.IResource
 *  org.eclipse.core.runtime.CoreException
 *  org.eclipse.core.runtime.IAdaptable
 *  org.eclipse.core.runtime.IPath
 *  org.eclipse.core.runtime.IProgressMonitor
 *  org.eclipse.core.runtime.Path
 *  org.eclipse.jface.action.IAction
 *  org.eclipse.jface.dialogs.IInputValidator
 *  org.eclipse.jface.dialogs.InputDialog
 *  org.eclipse.jface.dialogs.MessageDialog
 *  org.eclipse.jface.viewers.ISelection
 *  org.eclipse.jface.viewers.StructuredSelection
 *  org.eclipse.swt.SWTException
 *  org.eclipse.swt.dnd.Clipboard
 *  org.eclipse.swt.dnd.Transfer
 *  org.eclipse.swt.dnd.TransferData
 *  org.eclipse.swt.events.SelectionEvent
 *  org.eclipse.swt.events.SelectionListener
 *  org.eclipse.swt.graphics.ImageData
 *  org.eclipse.swt.graphics.ImageLoader
 *  org.eclipse.swt.layout.GridData
 *  org.eclipse.swt.layout.GridLayout
 *  org.eclipse.swt.widgets.Combo
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Label
 *  org.eclipse.swt.widgets.Layout
 *  org.eclipse.swt.widgets.Shell
 *  org.eclipse.ui.IObjectActionDelegate
 *  org.eclipse.ui.IWorkbenchPart
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.Activator;
import com.onpositive.pictureviewer.ImageTransferWrapper;
import java.util.ArrayList;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class PasteImageAction
implements IObjectActionDelegate {
    private ISelection selection;
    private static final String[] FILE_TYPES = new String[]{"JPG", "GIF", "PNG", "TIFF"};
    private static final int[] FILE_TYPE_CODES = new int[]{4, 2, 5, 6};
    private Clipboard fClipboard;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void run(IAction action) {
        if (!ImageTransferWrapper.isAvalable()) {
            MessageDialog.openError((Shell)Display.getCurrent().getActiveShell(), (String)"Paste Image action requires Eclipse 3.4", (String)"Paste Image action requires Eclipse 3.4");
            return;
        }
        Clipboard clipboard = this.fClipboard != null ? this.fClipboard : new Clipboard(Display.getDefault());
        try {
            Object adapter;
            TransferData[] availableTypes = clipboard.getAvailableTypes();
            ArrayList<TransferData> supportedData = new ArrayList<TransferData>();
            TransferData[] arrtransferData = availableTypes;
            int n = arrtransferData.length;
            int n2 = 0;
            while (n2 < n) {
                TransferData d = arrtransferData[n2];
                boolean supportedType = ImageTransferWrapper.isSupportedType(d);
                if (supportedType) {
                    supportedData.add(d);
                }
                ++n2;
            }
            if (supportedData.isEmpty()) {
                MessageDialog.openInformation((Shell)Display.getCurrent().getActiveShell(), (String)"Unable to paste", (String)"There is no pastable images in the cliboard.");
                return;
            }
            StructuredSelection selection = (StructuredSelection)this.selection;
            if (selection.isEmpty()) {
                MessageDialog.openInformation((Shell)Display.getCurrent().getActiveShell(), (String)"Unable to paste", (String)"Please select container to paste.");
                return;
            }
            Object firstElement = selection.getFirstElement();
            IContainer res = null;
            if (firstElement instanceof IResource) {
                IResource mn = (IResource)firstElement;
                if (mn instanceof IContainer) {
                    res = (IContainer)mn;
                } else if (mn instanceof IFile) {
                    IFile fl = (IFile)mn;
                    res = fl.getParent();
                }
            }
            if (firstElement instanceof IAdaptable && ((adapter = ((IAdaptable) firstElement).getAdapter(IResource.class)) != null)) {
                IResource mn = (IResource)adapter;
                if (mn instanceof IContainer) {
                    res = (IContainer)mn;
                } else if (mn instanceof IFile) {
                    IFile fl = (IFile)mn;
                    res = fl.getParent();
                }
            }
            if (res == null) {
                MessageDialog.openInformation((Shell)Display.getCurrent().getActiveShell(), (String)"Unable to paste", (String)"Please select container to paste.");
                return;
            }
            for (TransferData d : supportedData) {
                ImageData contents;
                boolean supportedType = ImageTransferWrapper.isSupportedType(d);
                if (!supportedType) {
                    MessageDialog.openInformation((Shell)Display.getCurrent().getActiveShell(), (String)"Strange Error", (String)"Unsupported transfer type here.");
                }
                if ((contents = (ImageData)clipboard.getContents((Transfer)ImageTransferWrapper.getInstance())) == null) continue;
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.data = new ImageData[]{contents};
                imageLoader.logicalScreenHeight = contents.height;
                imageLoader.logicalScreenWidth = contents.width;
                NameEndExtensionDialog dialog = new NameEndExtensionDialog(Display.getCurrent().getActiveShell());
                int open = dialog.open();
                if (open != 0) continue;
                String value = dialog.getValue();
                this.save(imageLoader, value, res, dialog.selection);
            }
            return;
        }
        finally {
            if (this.fClipboard == null) {
                clipboard.dispose();
            }
        }
    }

    private void save(ImageLoader imageLoader, String value, IContainer res, int types) {
        IFile file = res.getFile((IPath)new Path(value));
        if (file.exists() && !MessageDialog.openConfirm((Shell)Display.getCurrent().getActiveShell(), (String)"File already exists", (String)"File with specified name already exists. Overwrite?")) {
            return;
        }
        IPath rawLocation = file.getRawLocation();
        try {
            try {
                imageLoader.save(rawLocation.toOSString(), FILE_TYPE_CODES[types]);
            }
            catch (SWTException e) {
                Activator.log((Throwable)e);
                MessageDialog.openError((Shell)Display.getCurrent().getActiveShell(), (String)"Error during image saving", (String)e.getMessage());
            }
            file.refreshLocal(1, null);
        }
        catch (CoreException e) {
            Activator.log((Throwable)e);
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    private final class NameEndExtensionDialog
    extends InputDialog {
        int selection;
        private NameEndExtensionDialog(Shell parentShell) {
            super(parentShell, "Please specify file name and format", "Please specify file name:", "", new IInputValidator(){

                public String isValid(String newText) {
                    return null;
                }
            });
        }

        protected Control createDialogArea(Composite parent) {
            Composite createDialogArea = (Composite)super.createDialogArea(parent);
            Control[] arrcontrol = createDialogArea.getChildren();
            int n = arrcontrol.length;
            int n2 = 0;
            while (n2 < n) {
                Control c = arrcontrol[n2];
                c.setLayoutData((Object)new GridData());
                ++n2;
            }
            GridLayout layout = (GridLayout)createDialogArea.getLayout();
            layout.numColumns += 4;
            Label l = new Label(createDialogArea, 0);
            l.setText("Format:");
            final Combo m = new Combo(createDialogArea, 2056);
            m.setItems(FILE_TYPES);
            m.addSelectionListener(new SelectionListener(){

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    NameEndExtensionDialog.this.selection = m.getSelectionIndex();
                }
            });
            m.select(3);
            return createDialogArea;
        }

//        /* synthetic */ NameEndExtensionDialog(PasteImageAction pasteImageAction, Shell shell, NameEndExtensionDialog nameEndExtensionDialog) {
//            NameEndExtensionDialog nameEndExtensionDialog2;
//            nameEndExtensionDialog2(pasteImageAction, shell);
//        }

    }

}

