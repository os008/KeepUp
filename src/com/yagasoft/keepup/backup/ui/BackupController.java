/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui/BackupController.java
 *
 *			Modified: 12-Jun-2014 (23:21:39)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yagasoft.keepup.backup.ui.browser.LocalTableController;
import com.yagasoft.keepup.backup.ui.watcher.WatcherTableController;
import com.yagasoft.keepup.backup.watcher.Change;
import com.yagasoft.overcast.base.container.Container;


/**
 * The Class BackupController.
 */
public class BackupController implements ActionListener
{

	/** Panel. */
	protected BackupPanel				panel;

	/** Local table controller. */
	protected LocalTableController		localTableController;

	/** Watch table controller. */
	protected WatcherTableController	watchTableController;

	/** Listeners. */
	protected Set<IAddRemoveListener>	listeners	= new HashSet<IAddRemoveListener>();

	/**
	 * Instantiates a new backup controller.
	 *
	 * @param panel
	 *            Panel.
	 * @param localTableController
	 *            Local table controller.
	 * @param watchTableController
	 *            Watch table controller.
	 */
	public BackupController(BackupPanel panel, LocalTableController localTableController,
			WatcherTableController watchTableController)
	{
		this.panel = panel;
		this.localTableController = localTableController;
		this.watchTableController = watchTableController;

		panel.addButtonListener(this);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == panel.addSelectedButton)
		{
			notifyListeners(localTableController.getSelectedFiles(), Change.ADD);
		}
		else if (e.getSource() == panel.addAllButton)
		{
			notifyListeners(localTableController.getAllFiles(), Change.ADD);
		}
		else if (e.getSource() == panel.removeSelectedButton)
		{
			notifyListeners(watchTableController.getSelectedFiles(), Change.REMOVE);		// just notify that the button was pressed
		}
		else if (e.getSource() == panel.removeAllButton)
		{
			notifyListeners(watchTableController.getAllFiles(), Change.REMOVE_ALL);		// just notify that the button was pressed
		}
	}

	/**
	 * Notify listeners.
	 *
	 * @param containers
	 *            Containers.
	 * @param change
	 *            Change.
	 */
	protected void notifyListeners(List<? extends Container<?>> containers, Change change)
	{
		listeners.parallelStream()
				.forEach(listener -> listener.containersAddedRemoved(containers, change));
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addListener(IAddRemoveListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeListener(IAddRemoveListener listener)
	{
		listeners.remove(listener);
	}

}
