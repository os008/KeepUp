/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.menu.panels.options/CSPManagerPanel.java
 * 
 *			Modified: 23-Jun-2014 (21:17:24)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.menu.panels.options;


import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.yagasoft.keepup.CSPInfo;


/**
 * The Class CSPManagerPanel.
 */
public class CSPManagerPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -4884612100609411713L;
	
	/** Csps. */
	protected Set<CSPInfo>		csps;
	
	/** Csp panels. */
	protected Set<CSPPanel>		cspPanels;
	
	/**
	 * Create the frame.
	 *
	 * @param csps
	 *            Csps.
	 */
	public CSPManagerPanel(Set<CSPInfo> csps)
	{
		this.csps = csps;
		cspPanels = new HashSet<CSPPanel>();
		
		initGUI();
	}
	
	/**
	 * Inits the gui.
	 */
	private void initGUI()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		for (CSPInfo cspInfo : csps)
		{
			CSPPanel panel = new CSPPanel(cspInfo);
			
			// create a panel for each csp and save it for later use.
			cspPanels.add(panel);
			// add it to this panel.
			add(panel);
		}
	}
	
	/**
	 * Sets the settings.
	 */
	public void setSettings()
	{
		for (CSPPanel cspPanel : cspPanels)
		{
			cspPanel.setSettings();
		}
	}
}
