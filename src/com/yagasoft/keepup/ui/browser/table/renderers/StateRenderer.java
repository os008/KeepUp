/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.browser.table.renderers/StateRenderer.java
 *
 *			Modified: 25-Jun-2014 (04:55:13)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.browser.table.renderers;


import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.backup.State;


/**
 * The Class StateRenderer.
 */
public class StateRenderer extends DefaultTableCellRenderer
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 4069046002589244426L;

	/**
	 * Instantiates a new state renderer.
	 */
	public StateRenderer()
	{
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected
			, boolean hasFocus, int row, int column)
	{
		String imageName = null;

		if (value != null)
		{
			switch ((State) value)
			{
				case ADD:
				case MODIFY:
					imageName = "modified";
					break;
				case DELETE:
					imageName = "deleted";
					break;
				case SYNCED:
					imageName = "syncd";
					break;
				case REMOVE:
				case REMOVE_ALL:
				default:
					imageName = null;
					break;
			}
		}

		if (imageName != null)
		{
			// look for the image.
			String imgLocation = "images/states/" + imageName + ".gif";
			URL imageURL = _keepup.class.getResource(imgLocation);

			// set image inside cell
			setIcon(new ImageIcon(imageURL));
		}
		else
		{
			setIcon(null);
			setText(null);
		}

		return this;
	}

}
