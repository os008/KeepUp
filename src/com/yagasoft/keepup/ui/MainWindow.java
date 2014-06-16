/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/MainWindow.java
 *
 *			Modified: 16-Jun-2014 (15:03:55)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.yagasoft.keepup.ui.panels.LogPanel;
import com.yagasoft.keepup.ui.panels.QueuePanel;
import com.yagasoft.keepup.ui.panels.StatusBar;


public class MainWindow
{
	
	/** Frame. */
	private JFrame				frame;
	
	/** Panels for program features. */
	private Map<String, JPanel>	panels	= new HashMap<String, JPanel>();
	
	/** Menu bar. */
	@SuppressWarnings("unused")
	private JMenuBar			menuBar;
	
	/** Tabbed pane. */
	private JTabbedPane			tabbedPane;
	
	/** Lower panel. */
	private JPanel				lowerPanel;
	
	/** Queue panel. */
	private QueuePanel			queuePanel;
	
	/** Log panel. */
	private LogPanel			logPanel;
	
	/** Log panel. */
	private StatusBar			statusBar;
	
	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame("KeepUp - Google & Dropbox");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50, 768, 512);
		
		initWindow();
	}
	
	/**
	 * Construct the panels in the main window.
	 */
	private void initWindow()
	{
		
		// prep the centre area.
		tabbedPane = new JTabbedPane();
		frame.add(tabbedPane, BorderLayout.CENTER);
		
		// --------------------------------------------------------------------------------------
		// #region Lower panel.
		
		lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
		
		queuePanel = new QueuePanel();
		queuePanel.setPreferredSize(new Dimension(frame.getWidth(), 100));
		lowerPanel.add(queuePanel);
		
		logPanel = new LogPanel();
		logPanel.setPreferredSize(new Dimension(frame.getWidth(), 100));
		lowerPanel.add(logPanel);
		
		statusBar = new StatusBar();
		statusBar.setPreferredSize(new Dimension(frame.getWidth(), 25));
		lowerPanel.add(statusBar);
		// lowerPanel.setPreferredSize(new Dimension(WIDTH, 300));
		
		frame.add(lowerPanel, BorderLayout.SOUTH);
		
		// #endregion Lower panel.
		// --------------------------------------------------------------------------------------
	}
	
	/**
	 * Sets the menu bar.
	 *
	 * @param menuBar
	 *            the new menu bar
	 */
	public void setMenuBar(JMenuBar menuBar)
	{
		this.menuBar = menuBar;
		frame.add(menuBar, BorderLayout.NORTH);
	}
	
	/**
	 * Adds a panel to the frame. Stored using its title to display it on the tab bar.
	 *
	 * @param title
	 *            Title.
	 * @param panel
	 *            Panel.
	 */
	public void addPanel(String title, JPanel panel)
	{
		// make sure there is no panel with the same name.
		if (panels.containsValue(panel))
		{
			tabbedPane.remove(panel);
		}
		
		// add the panel to the list, and to the tabbed pane.
		panels.put(title, panel);
		tabbedPane.addTab(title, panel);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================
	
	/**
	 * @return the frame
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}
	
	/**
	 * @return the panels
	 */
	public Map<String, JPanel> getPanels()
	{
		return panels;
	}
	
	/**
	 * @param panels
	 *            the panels to set
	 */
	public void setPanels(Map<String, JPanel> panels)
	{
		this.panels = panels;
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
	
	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////
	
}
