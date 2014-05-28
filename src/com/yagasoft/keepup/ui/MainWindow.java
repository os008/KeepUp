/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/MainWindow.java
 * 
 *			Modified: 27-May-2014 (17:18:02)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainWindow
{

	private JFrame				frame;
	private Map<String, JPanel>	panels	= new HashMap<String, JPanel>();
	
	private JPanel bar;
	private Map<String, JButton>	buttons	= new HashMap<String, JButton>();

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
		
		bar = new JPanel(new FlowLayout());
		frame.add(bar, BorderLayout.NORTH);
	}

	/**
	 * Adds a panel to the frame. Stored using its title to display it on the tool-bar.
	 *
	 * @param title Title.
	 * @param panel Panel.
	 */
	public void addPanel(String title, JPanel panel)
	{
		if (panels.containsValue(panel))
		{
			frame.remove(panel);
			bar.remove(buttons.get(title));
		}
		
		panels.put(title, panel);
		frame.add(panels.get(title), BorderLayout.CENTER);
		
		JButton button = new JButton(title);
		button.addActionListener(event -> switchToPanel(title));
		buttons.put(title, button);
		bar.add(button);
	}

	/**
	 * Switch to the panel with that name. All other panels are hidden.
	 *
	 * @param title Title.
	 */
	public void switchToPanel(String title)
	{
		panels.values().parallelStream().forEach(panel -> panel.setVisible(false));
		panels.get(title).setVisible(true);
		System.out.println("Test! " + title);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	//======================================================================================

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

	//======================================================================================
	// #endregion Getters and setters.
	////////////////////////////////////////////////////////////////////////////////////////

}
