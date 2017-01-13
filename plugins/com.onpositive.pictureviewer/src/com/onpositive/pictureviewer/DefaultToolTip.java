/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.swt.custom.CLabel
 *  org.eclipse.swt.graphics.Color
 *  org.eclipse.swt.graphics.Font
 *  org.eclipse.swt.graphics.Image
 *  org.eclipse.swt.widgets.Composite
 *  org.eclipse.swt.widgets.Control
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Widget
 */
package com.onpositive.pictureviewer;

import com.onpositive.pictureviewer.ToolTip;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

public class DefaultToolTip
extends ToolTip {
    private String text;
    private Color backgroundColor;
    private Font font;
    private Image backgroundImage;
    private Color foregroundColor;
    private Image image;
    private int style = 32;

    public DefaultToolTip(Control control) {
        super(control);
    }

    public DefaultToolTip(Control control, int style, boolean manualActivation) {
        super(control, style, manualActivation);
    }

    protected Composite createToolTipContentArea(Event event, Composite parent) {
        Image image = this.getImage(event);
        Image bgImage = this.getBackgroundImage(event);
        String text = this.getText(event);
        Color fgColor = this.getForegroundColor(event);
        Color bgColor = this.getBackgroundColor(event);
        Font font = this.getFont(event);
        CLabel label = new CLabel(parent, this.getStyle(event));
        if (text != null) {
            label.setText(text);
        }
        if (image != null) {
            label.setImage(image);
        }
        if (fgColor != null) {
            label.setForeground(fgColor);
        }
        if (bgColor != null) {
            label.setBackground(bgColor);
        }
        if (bgImage != null) {
            label.setBackgroundImage(image);
        }
        if (font != null) {
            label.setFont(font);
        }
        return label;
    }

    protected int getStyle(Event event) {
        return this.style;
    }

    protected Image getImage(Event event) {
        return this.image;
    }

    protected Color getForegroundColor(Event event) {
        return this.foregroundColor == null ? event.widget.getDisplay().getSystemColor(28) : this.foregroundColor;
    }

    protected Color getBackgroundColor(Event event) {
        return this.backgroundColor == null ? event.widget.getDisplay().getSystemColor(29) : this.backgroundColor;
    }

    protected Image getBackgroundImage(Event event) {
        return this.backgroundImage;
    }

    protected Font getFont(Event event) {
        return this.font;
    }

    protected String getText(Event event) {
        return this.text;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setText(String text) {
        this.text = text;
    }
}

