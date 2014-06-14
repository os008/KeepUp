/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui/CombinedStoragePanel.java
 *
 *			Modified: 27-May-2014 (17:44:52)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui;


import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.yagasoft.keepup.combinedstorage.ui.browser.CSBrowserPanel;


/**
 * Class containing all components related to the GUI.
 */
public class CombinedStoragePanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 1305077722000332217L;
	
	/** Browser panel. */
	private CSBrowserPanel		browserPanel;
	
	/**
	 * Create the frame.
	 *
	 * @param browserPanel
	 *            Browser panel.
	 */
	public CombinedStoragePanel(CSBrowserPanel browserPanel)
	{
		// setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		this.browserPanel = browserPanel;
		
		initWindow();
	}
	
	/**
	 * Construct the panels in the main window.
	 */
	private void initWindow()
	{
		add(browserPanel, BorderLayout.CENTER);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================
	
	/**
	 * Gets the browser panel.
	 *
	 * @return the browserPanel
	 */
	public CSBrowserPanel getBrowserPanel()
	{
		return browserPanel;
	}
	
	/**
	 * Sets the browser panel.
	 *
	 * @param browserPanel
	 *            the browserPanel to set
	 */
	public void setBrowserPanel(CSBrowserPanel browserPanel)
	{
		this.browserPanel = browserPanel;
	}
	
	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
	
	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////
	
}
