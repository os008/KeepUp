/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.menu.panels.options/OptionsPanel.java
 *
 *			Modified: 23-Jun-2014 (21:17:05)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.menu.panels.options;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.yagasoft.keepup.CSPInfo;


/**
 * The Class Options.
 */
public class OptionsPanel extends JPanel implements ActionListener
{
	
	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= -1146451184847401905L;
	
	/** Frame. */
	private JFrame					frame;
	
	/** Csps. */
	public static Set<CSPInfo>		csps;
	
	/** Tabbed pane. */
	private JTabbedPane				tabbedPane;
	
	/** Csp manager panel. */
	private CSPManagerPanel			cspManagerPanel;
	
	/** Button ok. */
	private JButton					buttonOk;
	
	/** Button cancel. */
	private JButton					buttonCancel;
	
	/** Listeners. */
	private Set<IOptionsListener>	listeners			= new HashSet<IOptionsListener>();
	
	/**
	 * Create the panel.
	 */
	public OptionsPanel()
	{
		initGUI();
		addPanels();
	}
	
	/**
	 * Inits the gui.
	 */
	private void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		// buttons
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		
		buttonOk = new JButton("OK");
		buttonOk.addActionListener(this);
		buttonsPanel.add(buttonOk);
		
		buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(this);
		buttonsPanel.add(buttonCancel);
		
		add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Adds the panels.
	 */
	private void addPanels()
	{
		cspManagerPanel = new CSPManagerPanel(csps);
		tabbedPane.addTab("CSP Manager", cspManagerPanel);
	}
	
	/**
	 * Sets the csp settings.
	 */
	public void setCSPSettings()
	{
		cspManagerPanel.setSettings();
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == buttonCancel)
		{
			clearListeners();
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		else if (e.getSource() == buttonOk)
		{
			setCSPSettings();
			notifyListeners();
			clearListeners();
			frame.dispose();
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	/**
	 * Adds the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addListener(IOptionsListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Notify listeners.
	 */
	private void notifyListeners()
	{
		listeners.parallelStream()
				.forEach(listener -> listener.optionsSet());
	}
	
	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeListener(IOptionsListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Clear listeners.
	 */
	public void clearListeners()
	{
		listeners.clear();
	}
	
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
	
}
