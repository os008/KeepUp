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
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


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
		
		// prep the top bar.
		bar = new JPanel(new FlowLayout());
		frame.add(bar, BorderLayout.NORTH);
		
		// prep the centre area.
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		frame.add(contentPanel);
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
	
	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////
	
}
