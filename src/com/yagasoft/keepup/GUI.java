/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/GUI.java
 *
 *			Modified: 25-Jun-2014 (02:52:47)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.ui.BackupController;
import com.yagasoft.keepup.backup.ui.BackupPanel;
import com.yagasoft.keepup.backup.ui.browser.LocalTable;
import com.yagasoft.keepup.backup.ui.browser.LocalTableController;
import com.yagasoft.keepup.backup.ui.browser.LocalTree;
import com.yagasoft.keepup.backup.ui.watcher.WatcherPanel;
import com.yagasoft.keepup.backup.ui.watcher.WatcherTableController;
import com.yagasoft.keepup.backup.watcher.Watcher;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ui.CombinedStoragePanel;
import com.yagasoft.keepup.combinedstorage.ui.browser.CSBrowserPanel;
import com.yagasoft.keepup.combinedstorage.ui.browser.table.CSTable;
import com.yagasoft.keepup.combinedstorage.ui.browser.table.CSTableController;
import com.yagasoft.keepup.combinedstorage.ui.browser.tree.CSTree;
import com.yagasoft.keepup.combinedstorage.ui.browser.tree.CSTreeController;
import com.yagasoft.keepup.ui.MainWindow;
import com.yagasoft.keepup.ui.browser.BrowserPanel;
import com.yagasoft.keepup.ui.browser.table.renderers.FilePathRenderer;
import com.yagasoft.keepup.ui.browser.table.renderers.StateRenderer;
import com.yagasoft.keepup.ui.menu.MenuBar;
import com.yagasoft.keepup.ui.toolbars.FileToolBar;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;


/**
 * The Class GUI.
 */
public final class GUI
{
	
	////////////////////////////////////////////////////////////////////////////////////////
	// #region Initialisation.
	//======================================================================================
	
	/**
	 * Constructs the main window of the combined storage app, and sets it to be displayed.
	 */
	static void initGUI()
	{
		Logger.info("KEEPUP: GUI: INIT GUI components ...");
		
		App.mainWindow = new MainWindow();
		App.mainFrame = App.mainWindow.getFrame();
		App.mainFrame.setIconImage(App.appIcon);
		
		// add the menu bar.
		App.mainWindow.setMenuBar(new MenuBar());
		
		Map<Class<?>, TableCellRenderer> renderers = new HashMap<Class<?>, TableCellRenderer>();
		
		// combined storage feature gui
		App.csFoldersTree = new CSTree(App.ROOT.getNode());
		renderers = new HashMap<Class<?>, TableCellRenderer>();		// use default renderers
		App.csFilesTable = new CSTable(
				new String[] { "Name", "Size", "CSP" }
				, new float[] { 1f, 65f, 80f }
				, new int[] { 1 }
				, renderers);
		App.csBrowserPanel = new CSBrowserPanel(App.csFoldersTree, App.csFilesTable);
		App.combinedStoragePanel = new CombinedStoragePanel(App.csBrowserPanel);
		App.mainWindow.addPanel("Combined Storage", App.combinedStoragePanel);
		
		// backup feature GUI
		App.localFoldersTree = new LocalTree();
		renderers = new HashMap<Class<?>, TableCellRenderer>();		// use default renderers
		renderers.put(State.class, new StateRenderer());		// render the states as icons
		App.localFilesTable = new LocalTable(
				new String[] { "Name", "Size", "Status" }
				, new float[] { 1f, 65f, 50f }
				, new int[] { 1 }
				, renderers);
		App.localBrowserPanel = new BrowserPanel(App.localFoldersTree, App.localFilesTable);
		
		renderers = new HashMap<Class<?>, TableCellRenderer>();
		renderers.put(File.class, new FilePathRenderer());		// render a file as its path
		renderers.put(State.class, new StateRenderer());		// render the states as icons
		App.watchedFilesPanel = new WatcherPanel(
				new String[] { "Path", "Size", "Status" }
				, new float[] { 1f, 65f, 50f }
				, new int[] { 1 }
				, renderers);
		App.backupPanel = new BackupPanel(App.localBrowserPanel, App.watchedFilesPanel);
		App.mainWindow.addPanel("Backup", App.backupPanel);
		
		// save options and close DB when application is closing.
		App.mainFrame.addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowIconified(WindowEvent e)
			{
				super.windowIconified(e);
				
				minimiseToTray();
			}
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				
				App.saveOptions();
				DB.closeDB();
			}
		});
		
		App.combinedStoragePanel.getBrowserPanel().resetDivider((App.mainFrame.getWidth() / 3) + 22);
		
		App.mainFrame.setVisible(true);
		
		Logger.info("KEEPUP: GUI: FINISHED init GUI.");
	}
	
	/**
	 * Inits the view controllers.
	 */
	public static void initControllers()
	{
		Logger.info("KEEPUP: GUI: INIT GUI controllers ...");
		
		List<Function<File<?>, Object>> columnFunctions = new ArrayList<Function<File<?>, Object>>();
		
		// combined storage tree and table
		App.csTreeController = new CSTreeController(App.csFoldersTree);
		columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> Util.humanReadableSize(file.getSize()));
		columnFunctions.add(file -> file.getCsp());
		App.csTableController = new CSTableController(App.csFilesTable, columnFunctions);
		App.csTreeController.addTreeSelectionListener(App.csTableController);
		App.csFilesTable.addToolBar(new FileToolBar(App.csTableController));
		
		App.watcher = new Watcher();
		// local tree and table
		columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> Util.humanReadableSize(file.getSize()));
		columnFunctions.add(file -> App.watcher.getContainerState(file.getPath()));
		App.localTableController = new LocalTableController(App.localFilesTable, columnFunctions);
		App.localFoldersTree.addSelectionListener(App.localTableController);
		App.watcher.addListener(App.localTableController);
		
		// watcher table
		columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> Util.humanReadableSize(file.getSize()));
		columnFunctions.add(file -> App.watcher.getContainerState(file));
		App.watchTableController = new WatcherTableController(App.watchedFilesPanel, columnFunctions);
		App.watcher.addListener(App.watchTableController);
		
		// backup panel
		App.backupPanelController = new BackupController(App.backupPanel, App.localTableController, App.watchTableController);
		App.backupPanelController.addListener(App.watcher);
		
		Logger.info("KEEPUP: GUI: FINISHED init controllers.");
	}
	
	/**
	 * Initialises the folders tree's root by using the in-memory folder list.
	 */
	public static void initTree()
	{
		// get each CSP list if available.
		App.enabledCsps.values().parallelStream()
				.forEach(csp ->
				{
					try
					{
						csp.buildFileTree(false);
					}
					catch (OperationException | CreationException e)
					{
						e.printStackTrace();
					}
				});
	}
	
	//======================================================================================
	// #endregion Initialisation.
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Refresh tree by going through the folders already loaded, and making sure they're up to date.
	 * It does NOT load the tree from scratch.
	 */
	public static void refreshTree()
	{
		App.csTreeController.updateTree();
	}
	
	/**
	 * Navigate backward.
	 */
	public static void navigateBackward()
	{
		App.csTreeController.navigateBackward();
	}
	
	/**
	 * Navigate forward.
	 */
	public static void navigateForward()
	{
		App.csTreeController.navigateForward();
	}
	
	/**
	 * Updates the selected folder. It grabs the files in the folder, and then passes them to
	 * {@link CSBrowserPanel#updateTable(File[])}.
	 */
	public static void updateTable()
	{
		CombinedFolder folder = GUI.getSelectedFolder();
		
		if (folder != null)
		{
			folder.updateCombinedFolder(true);
			App.csTableController.updateTable(folder.getFilesList(false));
		}
		else
		{
			App.ROOT.updateCombinedFolder(true);
			App.csTableController.updateTable(App.ROOT.getFilesList(false));
		}
	}
	
	/**
	 * Creates an action to be taken that is related to a panel to be opened.
	 * This action is a new frame to include the panel passed,
	 * and disabling for the main window.
	 *
	 * @param panel
	 *            the panel.
	 * @param title
	 *            Title of the frame.
	 * @return J frame
	 */
	public static JFrame showSubWindow(JPanel panel, String title)
	{
		// create a frame for the panel.
		JFrame frame = new JFrame(title);
		
		// open the frame relative to the main window.
		Point mainWindowLocation = App.mainFrame.getLocation();
		frame.setLocation((int) mainWindowLocation.getX() + 50, (int) mainWindowLocation.getY() + 50);
		
		// when the frame is closed, dispose of it and return focus to the main window.
		frame.addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				
				frame.dispose();
				GUI.setMainWindowFocusable(true);
			}
			
		});
		
		// add the passed panel to the frame.
		frame.add(panel);
		// show the frame.
		frame.setVisible(true);
		// fit the frame to panel.
		frame.pack();
		
		// disable the main window.
		GUI.setMainWindowFocusable(false);
		
		return frame;
	}
	
	/**
	 * Set the focus state of the main window.
	 * This is used when a window is opened on top of this main window
	 * to force the user to finish working with it first.
	 *
	 * @param focusable
	 *            true for allowing focus using the mouse click.
	 */
	public static void setMainWindowFocusable(boolean focusable)
	{
		App.mainFrame.setFocusableWindowState(focusable);
		App.mainFrame.setEnabled(focusable);
		
		// bring it to front.
		if (focusable)
		{
			App.mainFrame.setVisible(true);
		}
	}
	
	/**
	 * Minimises the app to the system tray.
	 */
	public static void minimiseToTray()
	{
		if ( !SystemTray.isSupported())
		{
			return;
		}
		
		try
		{
			App.systemTray = SystemTray.getSystemTray();
			
			PopupMenu trayMenu = new PopupMenu();
			MenuItem menuItem;
			menuItem = new MenuItem("Restore");
			menuItem.addActionListener(event -> GUI.restoreWindow());
			trayMenu.add(menuItem);
			
			App.trayIcon = new TrayIcon(App.appIcon, "KeepUp", trayMenu);
			
			App.trayIcon.addMouseListener(new MouseAdapter()
			{
				
				@Override
				public void mouseClicked(MouseEvent e)
				{
					super.mouseClicked(e);
					
					if (e.getClickCount() >= 2)
					{
						GUI.restoreWindow();
					}
				}
			});
			
			App.systemTray.add(App.trayIcon);
			
			App.mainFrame.setVisible(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Restores the app from the system tray, and brings it to the front.
	 */
	public static void restoreWindow()
	{
		App.mainFrame.setVisible(true);
		App.mainFrame.setExtendedState(Frame.NORMAL);
		App.mainFrame.toFront();
		App.systemTray.remove(App.trayIcon);
	}
	
	/**
	 * Update free space, and display it.
	 */
	public static void updateFreeSpace()
	{
		Logger.info("KEEPUP: FREESPACE: updating ...");
		
		HashMap<String, Long> freeSpace = new HashMap<String, Long>();
		
		App.enabledCsps.values().stream()
				.forEach(csp ->
				{
					try
					{
						freeSpace.put(csp.getName(), new Long(csp.calculateRemoteFreeSpace()));
					}
					catch (OperationException e)
					{
						e.printStackTrace();
					}
				});
		
		App.mainWindow.getStatusBar().updateFreeSpace(freeSpace);
	}
	
	/**
	 * Gets the selected folder.
	 *
	 * @return the selected folder
	 */
	public static CombinedFolder getSelectedFolder()
	{
		return App.csTreeController.getSelectedFolder();
	}
	
	/**
	 * Singleton!
	 */
	private GUI()
	{}
	
}
