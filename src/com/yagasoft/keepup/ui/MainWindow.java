/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/MainWindow.java
 *
 *			Modified: 29-May-2014 (18:16:48)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.yagasoft.keepup.ui.panels.LogPanel;
import com.yagasoft.keepup.ui.panels.QueuePanel;
import com.yagasoft.keepup.ui.panels.StatusBar;


public class MainWindow
{

	/** Frame. */
	private JFrame					frame;

	/** Panels for program features. */
	private Map<String, JPanel>		panels	= new HashMap<String, JPanel>();

	/** Bar to contain the feature switching buttons. */
	private JPanel					bar;

	/** Buttons names mapped to the button. */
	private Map<String, JButton>	buttons	= new HashMap<String, JButton>();

	/** Content panel. */
	private JPanel					contentPanel;

	/** Card layout to switch between features' panels. */
	private CardLayout				cardLayout;

	/** Lower panel. */
	private JPanel					lowerPanel;

	/** Queue panel. */
	private QueuePanel				queuePanel;

	/** Log panel. */
	private LogPanel				logPanel;

	/** Log panel. */
	private StatusBar				statusBar;

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

		// prep the top bar.
		bar = new JPanel(new FlowLayout());
		frame.add(bar, BorderLayout.NORTH);

		// prep the centre area.
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		frame.add(contentPanel, BorderLayout.CENTER);

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
	 * Adds a panel to the frame. Stored using its title to display it on the tool-bar.
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
			contentPanel.remove(panel);
			bar.remove(buttons.get(title));
		}

		// add the panel to the list, and to the content panel layout.
		panels.put(title, panel);
		contentPanel.add(panel, title);

		// add a button to be able to switch to this panel when needed.
		JButton button = new JButton(title);
		button.addActionListener(event -> switchToPanel(title));
		buttons.put(title, button);
		bar.add(button);
	}

	/**
	 * Switch to the panel with that name. All other panels are hidden.
	 *
	 * @param title
	 *            Title.
	 */
	public void switchToPanel(String title)
	{
		cardLayout.show(contentPanel, title);
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
