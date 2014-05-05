/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/MainWindow.java
 *
 *			Modified: 12-Mar-2014 (20:21:01)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * Class containing all components related to the GUI.
 */
public class MainWindow extends JPanel
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 1305077722000332217L;

	/** Frame. */
	private JFrame				frame;

	/** Browser panel. */
	BrowserPanel				browserPanel;

	/** Lower panel. */
	private JPanel				lowerPanel;

	/** Queue panel. */
	QueuePanel					queuePanel;

	/** Log panel. */
	LogPanel					logPanel;

	/** Log panel. */
	StatusBar					statusBar;

	/**
	 * Create the frame.
	 */
	public MainWindow()
	{
		frame = new JFrame("KeepUp - Google + Dropbox");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50, 768, 512);
		frame.setContentPane(this);

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));

		initWindow();

		frame.addComponentListener(new ComponentAdapter()
		{

			@Override
			public void componentResized(ComponentEvent e)
			{
				super.componentResized(e);
				browserPanel.adjustColumns(frame.getWidth());
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				super.componentShown(e);
				browserPanel.adjustColumns(frame.getWidth());
			}

		});

		frame.setVisible(true);
	}

	/**
	 * Construct the panels in the main window.
	 */
	private void initWindow()
	{
		browserPanel = new BrowserPanel();

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
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public JFrame getFrame()
	{
		return frame;
	}

	/**
	 * Sets the frame.
	 *
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}

	/**
	 * Gets the browser panel.
	 *
	 * @return the browserPanel
	 */
	public BrowserPanel getBrowserPanel()
	{
		return browserPanel;
	}

	/**
	 * Sets the browser panel.
	 *
	 * @param browserPanel
	 *            the browserPanel to set
	 */
	public void setBrowserPanel(BrowserPanel browserPanel)
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
