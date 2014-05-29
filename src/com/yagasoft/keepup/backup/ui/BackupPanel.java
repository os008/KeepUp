
package com.yagasoft.keepup.backup.ui;


import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.yagasoft.keepup.ui.BrowserPanel;


public class BackupPanel extends JPanel
{

	private static final long	serialVersionUID	= -8746598032427301425L;
	protected BrowserPanel		browserPanel;

	/**
	 * Create the panel.
	 */
	public BackupPanel(BrowserPanel browserPanel)
	{
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
}
