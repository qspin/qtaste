package com.qspin.qtaste.sikuli;

import java.awt.Rectangle;
import java.io.Serializable;

import com.qspin.qtaste.testsuite.QTasteException;
import org.sikuli.script.Region;

public final class Area implements Serializable {
	

	/** Serial version UID */
	private static final long serialVersionUID = -2553914470459169961L;
	
	private Rectangle rect;
	
	public Area(Region r)
	{
		this.rect = r.getRect();
	}
	
	public String toString()
	{
		return this.rect.toString();
	}
	
	/**
	 * Simulates a click on the center of the area.
	 */
	public void click()
	{
		new Region(this.rect).click();
	}

    /**
     * Simulates a click on the specified image of the area.
     * @param fileName The image to find.
     * @throws QTasteException if the file doesn't exist or if no occurrence is found.
     */
    public void click(String fileName) throws QTasteException
    {
        try {
            new Region(this.rect).click(fileName);
        }
        catch(Exception ex)
        {
            throw new QTasteException(ex.getMessage(), ex);
        }
    }
	
	/**
	 * Simulates a mouse located on the area center.
	 */
	public void hover()
	{
		new Region(this.rect).hover();
	}

    /**
     * Simulates a mouse located on the specified image of the area.
     * @param fileName The image to find.
     * @throws QTasteException if the file doesn't exist or if no occurrence is found.
     */
    public void hover(String fileName) throws QTasteException
    {
        try {
            new Region(this.rect).hover(fileName);
        }
        catch(Exception ex)
        {
            throw new QTasteException(ex.getMessage(), ex);
        }
    }

	/**
	 * Simulates a right click on the center of the area.
	 *
	 */
	public void rightClick()
	{
		new Region(this.rect).rightClick();
	}

    /**
     * Simulates a right click on the specified image of the area.
     * @param fileName The image to find.
     * @throws QTasteException if the file doesn't exist or if no occurrence is found.
     */
    public void rightClick(String fileName) throws QTasteException
    {
       try {
            new Region(this.rect).rightClick(fileName);

        }
        catch(Exception ex)
        {
            throw new QTasteException(ex.getMessage(), ex);
        }
    }

    /**
     * Simulates a double click on the center of the area.
     *
     */
    public void doubleClick()
    {
        new Region(this.rect).doubleClick();
    }

    /**
     * Simulates a double click on the specified image of the area.
     * @param fileName The image to find.
     * @throws QTasteException if the file doesn't exist or if no occurrence is found.
     */
    public void doubleClick(String fileName) throws QTasteException
    {
        try {
            new Region(this.rect).doubleClick(fileName);

        }
        catch(Exception ex)
        {
            throw new QTasteException(ex.getMessage(), ex);
        }
    }


    /**
	 * Write the text in the area.
	 * @param text The text to write.
	 */
	public void write(String text)
	{
		new Region(this.rect).type(text);
	}


    /**
     * Returns the X coordinate of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the X coordinate of the bounding <code>Rectangle</code>.
     */
    public double getX() {
        return rect.getX();
    }

    /**
     * Returns the Y coordinate of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the Y coordinate of the bounding <code>Rectangle</code>.
     */
    public double getY() {
        return rect.getY();
    }

    /**
     * Returns the width of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the width of the bounding <code>Rectangle</code>.
     */
    public double getW() {
        return rect.getWidth();
    }

    /**
     * Returns the height of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the height of the bounding <code>Rectangle</code>.
     */
    public double getH() {
        return rect.getHeight();
    }

    /**
     * Gets the bounding <code>Rectangle</code> of this <code>Rectangle</code>.
     * <p>
     * @return    a new <code>Rectangle</code>, equal to the
     * bounding <code>Rectangle</code> for this <code>Rectangle</code>.
     */
    public Rectangle getBounds() {
        return rect;
    }

    /**
     * Extract the text contained in the region using OCR.
     * @return the text as a string. Multiple lines of text are separated by intervening ‘n’.
     */
    public String text() {
        return new Region(this.rect).text();
    }

}
