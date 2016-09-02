package com.qspin.qtaste.sikuli;

import java.awt.Rectangle;
import java.io.Serializable;

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
	 * Simulates a mouse located on the area center. 
	 */
	public void hover()
	{
		new Region(this.rect).hover();
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
	 * Write the text in the area.
	 * @param text The text to write.
	 */
	public void write(String text)
	{
		new Region(this.rect).type(text);
	}
}
