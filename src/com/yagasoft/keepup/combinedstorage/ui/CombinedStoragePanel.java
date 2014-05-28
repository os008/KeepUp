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
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
	
	/** Lower panel. */
	private JPanel				lowerPanel;
	
	/** Queue panel. */
	private QueuePanel			queuePanel;
	
	/** Log panel. */
	private LogPanel			logPanel;
	
	/** Log panel. */
	private StatusBar			statusBar;
	
	/**
	 * Create the frame.
	 *
	 * @param browserPanel
	 *            Browser panel.
	 */
	public CombinedStoragePanel(CSBrowserPanel browserPanel)
	{
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		this.browserPanel = browserPanel;
		
		initWindow();
	}
	
	/**
	 * Construct the panels in the main window.
	 */
	private void initWindow()
	{
		// --------------------------------------------------------------------------------------
		// #region Lower panel.
		
		lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
		
		queuePanel = new QueuePanel();
		queuePanel.setPreferredSize(new Dimension(WIDTH, 100));
		lowerPanel.add(queuePanel);
		
		logPanel = new LogPanel();
		logPanel.setPreferredSize(new Dimension(WIDTH, 100));
		lowerPanel.add(logPanel);
		
		statusBar = new StatusBar();
		statusBar.setPreferredSize(new Dimension(WIDTH, 25));
		lowerPanel.add(statusBar);
		// lowerPanel.setPreferredSize(new Dimension(WIDTH, 300));
		
		// #endregion Lower panel.
		// --------------------------------------------------------------------------------------
		
		add(browserPanel, BorderLayout.CENTER);
		add(lowerPanel, BorderLayout.SOUTH);
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
	 * Gets the lower panel.
	 *
	 * @return the lowerPanel
	 */
	public JPanel getLowerPanel()
	{
		return lowerPanel;
	}
	
	/**
	 * Sets the lower panel.
	 *
	 * @param lowerPanel
	 *            the lowerPanel to set
	 */
	public void setLowerPanel(JPanel lowerPanel)
	{
		this.lowerPanel = lowerPanel;
	}
	
	/**
	 * Gets the queue panel.
	 *
	 * @return the queuePanel
	 */
	public QueuePanel getQueuePanel()
	{
		return queuePanel;
	}
	
	/**
	 * Sets the queue panel.
	 *
	 * @param queuePanel
	 *            the queuePanel to set
	 */
	public void setQueuePanel(QueuePanel queuePanel)
	{
		this.queuePanel = queuePanel;
	}
	
	/**
	 * Gets the log panel.
	 *
	 * @return the logPanel
	 */
	public LogPanel getLogPanel()
	{
		return logPanel;
	}
	
	/**
	 * Sets the log panel.
	 *
	 * @param logPanel
	 *            the logPanel to set
	 */
	public void setLogPanel(LogPanel logPanel)
	{
		this.logPanel = logPanel;
	}
	
	/**
	 * @return the statusBar
	 */
	public StatusBar getStatusBar()
	{
		return statusBar;
	}
	
	/**
	 * @param statusBar
	 *            the statusBar to set
	 */
	public void setStatusBar(StatusBar statusBar)
	{
		this.statusBar = statusBar;
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
