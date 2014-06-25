/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 *
 *			Modified: 24-Jun-2014 (16:45:32)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.DB.Table;
import com.yagasoft.keepup.backup.scheduler.Scheduler;
import com.yagasoft.keepup.backup.ui.BackupController;
import com.yagasoft.keepup.backup.ui.BackupPanel;
import com.yagasoft.keepup.backup.ui.browser.LocalTable;
import com.yagasoft.keepup.backup.ui.browser.LocalTableController;
import com.yagasoft.keepup.backup.ui.browser.LocalTree;
import com.yagasoft.keepup.backup.ui.watcher.WatcherPanel;
import com.yagasoft.keepup.backup.ui.watcher.WatcherTableController;
import com.yagasoft.keepup.backup.watcher.Watcher;
import com.yagasoft.keepup.backup.watcher.WatcherDB;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ui.CombinedStoragePanel;
import com.yagasoft.keepup.combinedstorage.ui.browser.CSBrowserPanel;
import com.yagasoft.keepup.combinedstorage.ui.browser.table.CSTable;
import com.yagasoft.keepup.combinedstorage.ui.browser.table.CSTableController;
import com.yagasoft.keepup.combinedstorage.ui.browser.tree.CSTree;
import com.yagasoft.keepup.combinedstorage.ui.browser.tree.CSTreeController;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.keepup.ui.MainWindow;
import com.yagasoft.keepup.ui.browser.BrowserPanel;
import com.yagasoft.keepup.ui.menu.panels.options.OptionsPanel;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.operation.Operation;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.csp.CSP;
import com.yagasoft.overcast.exception.CSPBuildException;
import com.yagasoft.overcast.exception.OperationException;


/**
 * This class contains the central control for the program. All other classes return to this one to ask for services.
 */
public final class App
{

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Fields.
	// ======================================================================================

	/** Constant: VERSION. */
	public static final String					VERSION		= "2.11.0130";

	/** Last directory used. */
	private static String						lastDirectory;

	//--------------------------------------------------------------------------------------
	// #region CSPs.

	/** csps mapped by their name, and the full package name. */
	public static Map<String, CSPInfo>			csps		= new HashMap<String, CSPInfo>();

	/*
	 *  init the info of each CSP available to this software.
	 *  it should pass the name of the CSP and the package path to the constructor
	 *  of the object of CSPInfo.
	 */
	static
	{
		CSPInfo cspInfo = new CSPInfo("Google", "com.yagasoft.overcast.implement.google.Google");
		csps.put(cspInfo.getCspName(), cspInfo);

		cspInfo = new CSPInfo("Dropbox", "com.yagasoft.overcast.implement.dropbox.Dropbox");
		csps.put(cspInfo.getCspName(), cspInfo);
	}

	/** CSPs list currently loaded. */
	public static HashMap<String, CSP<?, ?, ?>>	enabledCsps	= new HashMap<String, CSP<?, ?, ?>>();

	// #endregion CSPs.
	//--------------------------------------------------------------------------------------

	/** Constant: Root of the tree. */
	public static final CombinedFolder			ROOT		= new CombinedFolder(null);

	// --------------------------------------------------------------------------------------
	// #region GUI.

	/** Main window. */
	public static MainWindow					mainWindow;

	public static JFrame						mainFrame;

	/** Combined storage panel. */
	public static CombinedStoragePanel			combinedStoragePanel;

	/** Browser panel. */
	public static CSBrowserPanel				csBrowserPanel;

	/** Folders tree. */
	public static CSTree						csFoldersTree;

	/** Tree controller. */
	public static CSTreeController				csTreeController;

	/** Table controller. */
	public static CSTableController				csTableController;

	/** Files table. */
	public static CSTable						csFilesTable;

	/** Main window. */
	public static BackupPanel					backupPanel;

	public static BackupController				backupPanelController;

	/** Browser panel. */
	public static BrowserPanel					localBrowserPanel;

	/** Folders tree. */
	public static LocalTree						localFoldersTree;

	/** Table controller. */
	public static LocalTableController			localTableController;

	/** Files table. */
	public static LocalTable					localFilesTable;

	/** Table controller. */
	public static WatcherTableController		watchTableController;

	/** Files table. */
	public static WatcherPanel					watchedFilesPanel;

	public static SystemTray					systemTray;

	public static TrayIcon						trayIcon;

	public static Image							appIcon		= new ImageIcon(_keepup.class
																	.getResource("images/icon.png")).getImage();

	// #endregion GUI.
	// --------------------------------------------------------------------------------------

	/** Array of files in copied or moved to memory. */
	static List<RemoteFile<?>>			filesInHand;

	/**
	 * FileActions that can be performed on the one "in hand".
	 */
	enum FileActions
	{
		COPY,
		MOVE
	}

	/** File action to be performed on filesInHand. */
	static FileActions	fileAction;

	static Watcher		watcher;

	private static Scheduler	scheduler;

	private static WatcherDB watcherDB;

	// ======================================================================================
	// #endregion Fields.
	// //////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Initialisation.
	// ======================================================================================

	/**
	 * Prepare the app for usage.
	 *
	 * @throws CSPBuildException
	 */
	public static void initApp()
	{
		GUI.initGUI();
		GUI.initControllers();
		DB.initDB();
		loadOptions();
		initCSPs();
		initBackup();
	}

	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 *
	 * @throws CSPBuildException
	 */
	private static void initCSPs()
	{
		Logger.info("KEEPUP: CSPS: initialising ...");

		// init CSPs in parallel.
		ExecutorService executor = Executors.newCachedThreadPool();

		// authorise, get free space, and load the root tree of each CSP
		for (CSPInfo cspInfo : csps.values())
		{
			if (cspInfo.isEnabled())
			{
				executor.execute(() ->
				{
					try
					{
						cspInfo.setCspObject((CSP<?, ?, ?>) Class.forName(cspInfo.getCspPackage())
								.getMethod("getInstance", String.class, String.class)
								.invoke(cspInfo.getCspPackage(), cspInfo.getUserId(), ""));

						enabledCsps.put(cspInfo.getCspName(), cspInfo.getCspObject());
						initCSP(cspInfo.getCspObject());
					}
					catch (Exception e)
					{
						Msg.showError(e.getMessage());
						e.printStackTrace();
						cspInfo.setEnabled(false);
					}
				});
			}
		}

		try
		{
			executor.shutdown();

			// wait for the threads to finish before expanding the root
			// -- caused problems with showing expansion icon of children before finishing loading
			if (executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS))
			{
				GUI.updateFreeSpace();
				GUI.initTree();

				// show the root as expanded at the start.
				csFoldersTree.expandPathToNode(ROOT.getNode());
			}
		}
		catch (InterruptedException e)
		{
			Msg.showError(e.getMessage());
			e.printStackTrace();
		}

		// add CSPs to options panel to be used with the CSP manager.
		OptionsPanel.csps = new HashSet<CSPInfo>(csps.values());

		Logger.info("KEEPUP: CSPS: FINISHED init.");
	}

	/**
	 * Adds a csp, and initialise its tree.
	 *
	 * @param csp
	 *            csp.
	 */
	public static void initCSP(final CSP<?, ?, ?> csp)
	{
		try
		{
			csp.initTree();
			csp.getRemoteFileTree().addOperationListener(ROOT, Operation.ADD);
			csp.getRemoteFileTree().addOperationListener(ROOT, Operation.REMOVE);
			ROOT.addCspFolder(csp.getRemoteFileTree());
		}
		catch (OperationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Load options from file.
	 */
	public static void loadOptions()
	{
		Logger.info("KEEPUP: OPTIONS: load ...");

		String[][] option;

		for (CSPInfo cspInfo : csps.values())
		{
			//--------------------------------------------------------------------------------------
			// #region set user ID.

			option = DB.getRecord(Table.options, new String[] { "value" }
					, "category = '" + cspInfo.getCspName()
							+ "' AND option = 'userId'");
			if ((option.length > 0) && (option[0].length > 0))
			{
				cspInfo.setUserId(option[0][0]);
			}

			// #endregion set user ID.
			//--------------------------------------------------------------------------------------

			//--------------------------------------------------------------------------------------
			// #region set enabled status.

			option = DB.getRecord(Table.options, new String[] { "value" }
					, "category = '" + cspInfo.getCspName()
							+ "' AND option = 'enabled'");
			if ((option.length > 0) && (option[0].length > 0))
			{
				cspInfo.setEnabled(option[0][0].equals("true") ? true : false);
			}

			// #endregion set enabled status.
			//--------------------------------------------------------------------------------------

		}

		//--------------------------------------------------------------------------------------
		// #region set last directory.

		option = DB.getRecord(Table.options, new String[] { "value" }
				, "category = 'lastDirectory' AND option = 'path'");
		if ((option.length > 0) && (option[0].length > 0) && !option[0][0].equalsIgnoreCase("null"))
		{
			setLastDirectory(option[0][0]);
		}
		else
		{
			setLastDirectory(System.getProperty("user.home"));
		}

		// #endregion set last directory.
		//--------------------------------------------------------------------------------------

		Logger.info("KEEPUP: OPTIONS: done load.");
	}

	/**
	 * Save options to file.
	 */
	public static void saveOptions()
	{
		Logger.info("KEEPUP: OPTIONS: saving ...");

		for (CSPInfo cspInfo : csps.values())
		{
			DB.insertOrUpdate(Table.options, DB.optionsColumns
					, new String[] { cspInfo.getCspName(), "userId", cspInfo.getUserId() }
					, new int[] { 0, 1 });

			DB.insertOrUpdate(Table.options, DB.optionsColumns
					, new String[] { cspInfo.getCspName(), "enabled", cspInfo.isEnabled() + "" }
					, new int[] { 0, 1 });
		}

		DB.insertOrUpdate(Table.options, DB.optionsColumns
				, new String[] { "lastDirectory", "path", lastDirectory }
				, new int[] { 0, 1 });

		Logger.info("KEEPUP: OPTIONS: done saving.");
	}

	public static void initBackup()
	{
		Logger.info("KEEPUP: BACKUP: init system ...");

		if (enabledCsps.isEmpty())
		{
			return;
		}

		if (scheduler != null)
		{
			scheduler.stopBackupLoop();
			watcher.removeListener(scheduler);
			watcher.removeListener(watcherDB);
			watcher.clearContainers();
		}

		scheduler = new Scheduler();
		watcher.addListener(scheduler);
		scheduler.addListener(watcher);
		watcherDB = new WatcherDB(watcher);
		scheduler.addListener(watcherDB);

		Logger.info("KEEPUP: BACKUP: FINISHED init.");
	}

	// ======================================================================================
	// #endregion Initialisation.
	// //////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region CSP operations.
	// ======================================================================================

	/**
	 * Resets all CSPs by
	 * checking if the CSP was disabled, and if so destroy its instance.
	 * If it was enabled, then create a new instance.
	 *
	 * @return Object
	 */
	public static void resetCSPs()
	{
		boolean settingsChanged = false;

		// go through the CSPs' info and check if any changed their enabled status.
		for (CSPInfo cspInfo : csps.values())
		{
			// if disabled, then destroy.
			if ( !cspInfo.isEnabled() && enabledCsps.containsKey(cspInfo.getCspName()))
			{
				// no need to listen
				cspInfo.getCspObject().getRemoteFileTree().clearAllListeners();
				// null the instance
				cspInfo.getCspObject().destroyInstance();
				cspInfo.setCspObject(null);
				// remove from enabled CSPs
				enabledCsps.remove(cspInfo.getCspName());
				// collapse the root to avoid issues with expansion icon when updating enabled CSPs.
				csFoldersTree.getTreeFolders().collapsePath(new TreePath(ROOT.getNode()));
				// clear the tree from nodes
				ROOT.clearCspFolders();
				// flag it
				settingsChanged = true;
			}
			else if (cspInfo.isEnabled() && !enabledCsps.containsKey(cspInfo.getCspName()))
			{	// a CSP was enabled
				csFoldersTree.getTreeFolders().collapsePath(new TreePath(ROOT.getNode()));
				ROOT.clearCspFolders();
				settingsChanged = true;
			}
		}

		// something changed!
		if (settingsChanged)
		{
			// make sure the existing trees are nulled so that they generate update events for the visual tree.
			enabledCsps.values().parallelStream()
					.forEach(csp ->
					{
						csp.getRemoteFileTree().clearAllListeners();
						csp.resetTree();
					});

			// initialise all enabled CSPs again.
			initCSPs();
			initBackup();
		}
	}

	/**
	 * Gets the csps list.
	 *
	 * @return the csps list
	 */
	public static List<CSP<?, ?, ?>> getCspsList()
	{
		return new ArrayList<CSP<?, ?, ?>>(enabledCsps.values());
	}

	/**
	 * Gets the CSP with the least space to fit the file passed.
	 *
	 * @param file
	 *            File.
	 * @return The best fit CSP.
	 * @throws OperationException
	 *             the operation exception
	 */
	public static CSP<?, ?, ?> chooseCsp(LocalFile file) throws OperationException
	{
		List<LocalFile> fileList = new ArrayList<LocalFile>();
		fileList.add(file);

		return chooseCsp(fileList);
	}

	/**
	 * Gets the CSP with the least space to fit the files passed.
	 *
	 * @param files
	 *            Files.
	 * @return The best fit CSP or null if none fit.
	 * @throws OperationException
	 *             the operation exception
	 */
	public static CSP<?, ?, ?> chooseCsp(List<LocalFile> files) throws OperationException
	{
		long filesSize = 0L;

		// add up all the files' sizes.
		for (LocalFile file : files)
		{
			filesSize += file.getSize();
		}

		// get best fitting csp for the files at hand.
		CSP<?, ?, ?> bestFit = bestFit(filesSize);

		// if no fit, then not enough space on any csp.
		if (bestFit == null)
		{
			Logger.error("KEEPUP: CHOOSE CSP: not enough space!");
			Msg.showError("Please, free some space on any CSP.");
			return null;
		}
		else
		{
			return bestFit;
		}
	}

	/**
	 * Gets the % of the fit of the files inside the CSP's free space.
	 *
	 * @param csp
	 *            CSP.
	 * @param filesSize
	 *            Files size.
	 * @return % of the fit.
	 * @throws OperationException
	 *             the operation exception when free space can't be obtained.
	 */
	private static float fit(CSP<?, ?, ?> csp, long filesSize) throws OperationException
	{
		// get current free space, and divide the file size by it to get percentage of fit.
		return filesSize / (float) csp.calculateRemoteFreeSpace();
	}

	/**
	 * Gets the smallest fit for the files among all the CSPs.
	 *
	 * @param filesSize
	 *            Files size.
	 * @return Best fit CSP.
	 * @throws OperationException
	 *             the operation exception when free space can't be obtained.
	 */
	private static CSP<?, ?, ?> bestFit(long filesSize) throws OperationException
	{
		HashMap<Float, CSP<?, ?, ?>> fits = new HashMap<Float, CSP<?, ?, ?>>();

		// store each fit value for each csp in the map.
		for (CSP<?, ?, ?> csp : enabledCsps.values())
		{
			fits.put(fit(csp, filesSize), csp);
		}

		// return highest fit below 0.95
		return fits.get(fits.keySet().parallelStream()
				.filter(fit -> fit < 0.95)		// might cause issues if cutting it too close.
				.max((a, b) -> a.compareTo(b)).orElse(null));
	}

	// ======================================================================================
	// #endregion CSP operations.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Combine the files in all the available CSPs into a single list.
	 *
	 * @param update
	 *            update from source, or read from memory?
	 * @return the root files
	 */
	public static List<RemoteFile<?>> getFilesList(CombinedFolder folder, boolean update)
	{
		return folder.getFilesList(update);
	}

	/**
	 * Search for file in all CSPs using the path passed.
	 *
	 * @param path
	 *            Path.
	 * @return List of files with the same path in all CSPs, or empty list if it doesn't exist.
	 */
	public static List<RemoteFile<?>> searchForFile(String path)
	{
		return enabledCsps.values().parallelStream()
				// replace each csp with the file with the same path
				.map(csp ->
				{
					try
					{
						return csp.searchFileByPath(path);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					return null;
				})
				.filter(file -> file != null)
				.collect(Collectors.toList());
	}

	/**
	 * Search for combined folder using the path passed.
	 *
	 * @param path
	 *            Path.
	 * @return combined folder found, or null if not.
	 */
	public static CombinedFolder searchForFolder(String path)
	{
		ArrayList<String> splitPath = enabledCsps.values().iterator().next().splitPath(path);
		// get the name from the last entry in the path.
		String containerName = splitPath.remove(splitPath.size() - 1);

		// save intermediate nodes
		CombinedFolder result = ROOT;

		// search for each entry in the path ...
		while ((result != null) && !splitPath.isEmpty())
		{
			// search for the next node in this node.
			result = result.findFolder(splitPath.remove(0), true);
		}

		// if part of the path is not found ...
		if ( !splitPath.isEmpty() || (result == null))
		{
			return null;		// ... return nothing.
		}
		else
		{	// ... or search for the folder in the end node, might return null.
			return result.findFolder(containerName, true);
		}
	}

	/**
	 * @return the lastDirectory
	 */
	public static String getLastDirectory()
	{
		return lastDirectory;
	}

	/**
	 * @param lastDirectory
	 *            the lastDirectory to set
	 */
	public static void setLastDirectory(String lastDirectory)
	{
		App.lastDirectory = lastDirectory;
		combinedStoragePanel.getBrowserPanel().updateDestinationFolder(lastDirectory);
	}

	private App()
	{}

}
