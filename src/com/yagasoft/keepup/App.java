/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 *
 *			Modified: 23-Jun-2014 (20:03:14)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.scheduler.Scheduler;
import com.yagasoft.keepup.backup.ui.BackupController;
import com.yagasoft.keepup.backup.ui.BackupPanel;
import com.yagasoft.keepup.backup.ui.StateRenderer;
import com.yagasoft.keepup.backup.ui.browser.LocalTable;
import com.yagasoft.keepup.backup.ui.browser.LocalTableController;
import com.yagasoft.keepup.backup.ui.browser.LocalTree;
import com.yagasoft.keepup.backup.ui.watcher.WatcherTableController;
import com.yagasoft.keepup.backup.watcher.Watcher;
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
import com.yagasoft.keepup.ui.browser.table.FileTable;
import com.yagasoft.keepup.ui.browser.table.renderers.FilePathRenderer;
import com.yagasoft.keepup.ui.menu.MenuBar;
import com.yagasoft.keepup.ui.menu.panels.options.OptionsPanel;
import com.yagasoft.keepup.ui.toolbars.FileToolBar;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.operation.Operation;
import com.yagasoft.overcast.base.container.operation.OperationState;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.base.container.transfer.UploadJob;
import com.yagasoft.overcast.base.csp.CSP;
import com.yagasoft.overcast.exception.CSPBuildException;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;
import com.yagasoft.overcast.exception.TransferException;


/**
 * This class contains the central control for the program. All other classes return to this one to ask for services.
 */
public final class App
{
	
	/** Constant: VERSION. */
	public static final String					VERSION		= "2.03.0070";
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Fields.
	// ======================================================================================
	
	/** Options file path. */
	private static Path							optionsFile	= Paths.get(System.getProperty("user.dir") + "/etc/options.dat");
	
	/** Options as a map. */
	private static HashMap<String, Object>		options		= null;
	
	// private static String ubuntuUser;
	// private static String ubuntuPass;
	
	// csps mapped by their name, and the full package name.
	public static Map<String, CSPInfo>			csps		= new HashMap<String, CSPInfo>();
	
	static
	{
		CSPInfo cspInfo = new CSPInfo("Google", "com.yagasoft.overcast.implement.google.Google");
		cspInfo.setUserId("os1983@gmail.com");
		cspInfo.setEnabled(true);
		csps.put(cspInfo.getCspName(), cspInfo);
		
		cspInfo = new CSPInfo("Dropbox", "com.yagasoft.overcast.implement.dropbox.Dropbox");
		cspInfo.setUserId("os008@hotmail.com");
		cspInfo.setEnabled(true);
		csps.put(cspInfo.getCspName(), cspInfo);
	}
	
	/** CSPs list currently loaded. */
	public static HashMap<String, CSP<?, ?, ?>>	enabledCsps	= new HashMap<String, CSP<?, ?, ?>>();
	
	/** Constant: Root of the tree. */
	public static final CombinedFolder			ROOT		= new CombinedFolder(null);
	
	// --------------------------------------------------------------------------------------
	// #region GUI.
	
	/** Main window. */
	public static MainWindow					mainWindow;
	
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
	public static FileTable						watchedFilesTable;
	
	// #endregion GUI.
	// --------------------------------------------------------------------------------------
	
	/** Last directory used. */
	private static String						lastDirectory;
	
	/** Array of files in copied or moved to memory. */
	private static List<RemoteFile<?>>			filesInHand;
	
	/**
	 * FileActions that can be performed on the one "in hand".
	 */
	enum FileActions
	{
		COPY,
		MOVE
	}
	
	/** File action to be performed on filesInHand. */
	private static FileActions	fileAction;
	
	private static Watcher		watcher;
	
	private static Scheduler	scheduler;
	
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
		initGUI();
		initControllers();
		initDB();
		loadOptions();
		initCSPs();
		initBackup();
	}
	
	/**
	 * Constructs the main window of the combined storage app, and sets it to be displayed.
	 */
	private static void initGUI()
	{
		Logger.info("GUI: initialising GUI components ...");
		
		mainWindow = new MainWindow();
		
		// add the menu bar.
		mainWindow.setMenuBar(new MenuBar());
		
		Map<Class<?>, TableCellRenderer> renderers = new HashMap<Class<?>, TableCellRenderer>();
		
		// combined storage feature gui
		csFoldersTree = new CSTree(ROOT.getNode());
		renderers = new HashMap<Class<?>, TableCellRenderer>();		// use default renderers
		csFilesTable = new CSTable(
				new String[] { "Name", "Size", "CSP" }
				, new float[] { 0.5f, 0.25f, 0.25f }
				, new int[] { 1 }
				, renderers);
		csBrowserPanel = new CSBrowserPanel(csFoldersTree, csFilesTable);
		combinedStoragePanel = new CombinedStoragePanel(csBrowserPanel);
		mainWindow.addPanel("Combined Storage", combinedStoragePanel);
		
		// backup feature GUI
		localFoldersTree = new LocalTree();
		renderers = new HashMap<Class<?>, TableCellRenderer>();		// use default renderers
		renderers.put(State.class, new StateRenderer());		// render the states as icons
		localFilesTable = new LocalTable(
				new String[] { "Name", "Size", "Status" }
				, new float[] { 0.65f, 0.20f, 0.15f }
				, new int[] { 1 }
				, renderers);
		localBrowserPanel = new BrowserPanel(localFoldersTree, localFilesTable);
		
		renderers = new HashMap<Class<?>, TableCellRenderer>();
		renderers.put(File.class, new FilePathRenderer());		// render a file as its path
		renderers.put(State.class, new StateRenderer());		// render the states as icons
		watchedFilesTable = new FileTable(
				new String[] { "Path", "Size", "Status" }
				, new float[] { 0.65f, 0.20f, 0.15f }
				, new int[] { 1 }
				, renderers);
		backupPanel = new BackupPanel(localBrowserPanel, watchedFilesTable);
		mainWindow.addPanel("Backup", backupPanel);
		
		// save options and close DB when application is closing.
		mainWindow.getFrame().addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				saveOptions();
				DB.closeDB();
			}
		});
		
		combinedStoragePanel.getBrowserPanel().resetDivider((mainWindow.getFrame().getWidth() / 3) + 22);
		
		mainWindow.getFrame().setVisible(true);
	}
	
	/**
	 * Inits the view controllers.
	 */
	public static void initControllers()
	{
		Logger.info("GUI: initialising GUI controllers ...");
		
		List<Function<File<?>, Object>> columnFunctions = new ArrayList<Function<File<?>, Object>>();
		
		// combined storage tree and table
		csTreeController = new CSTreeController(csFoldersTree);
		columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> humanReadableSize(file.getSize()));
		columnFunctions.add(file -> file.getCsp());
		csTableController = new CSTableController(csFilesTable, columnFunctions);
		csTreeController.addTreeSelectionListener(csTableController);
		csFilesTable.addToolBar(new FileToolBar(csTableController));
		
		watcher = new Watcher();
		// local tree and table
		columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> humanReadableSize(file.getSize()));
		columnFunctions.add(file -> watcher.getContainerState(file));
		localTableController = new LocalTableController(localFilesTable, columnFunctions);
		localFoldersTree.addSelectionListener(localTableController);
		
		// watcher table
		columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> humanReadableSize(file.getSize()));
		columnFunctions.add(file -> watcher.getContainerState(file));
		watchTableController = new WatcherTableController(watchedFilesTable, columnFunctions);
		watcher.addListener(watchTableController);
		
		// backup panel
		backupPanelController = new BackupController(backupPanel, localTableController, watchTableController);
		backupPanelController.addListener(watcher);
	}
	
	private static void initDB()
	{
		if ( !DB.ready)
		{
			Logger.error("DB: problem with DB!");
			Msg.showErrorAndConfirm("Problem with DB! Closing application ...");
			System.exit(1);
		}
	}
	
	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 *
	 * @throws CSPBuildException
	 */
	private static void initCSPs()
	{
		Logger.info("KEEPUP: initialising CSPs ...");
		
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
				updateFreeSpace();
				initTree();
				
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
	}
	
	/**
	 * Initialises the folders tree's root by using the in-memory folder list.
	 */
	public static void initTree()
	{
		// get each CSP list if available.
		enabledCsps.values().parallelStream()
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
	
	/**
	 * Load options from file.
	 */
	@SuppressWarnings("unchecked")
	public static void loadOptions()
	{
		Logger.info("KEEPUP: loading options ...");
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(optionsFile.toString()));
			options = (HashMap<String, Object>) in.readObject();
			in.close();
			
			options.keySet().parallelStream()
					.forEach(option ->
					{
						switch (option)
						{
							case "lastDirectory":
								String directory = (String) options.get("lastDirectory");
								
								if (Files.notExists(Paths.get(directory)))
								{
									setLastDirectory(System.getProperty("user.home"));
								}
								else
								{
									setLastDirectory(directory);
								}
								
								break;
						
						// case "ubuntuUser":
						// ubuntuUser = (String) options.get("ubuntuUser");
						// break;
						//
						// case "ubuntuPass":
						// ubuntuPass = (String) options.get("ubuntuPass");
						// break;
							}
						});
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
			options = new HashMap<String, Object>();
			setLastDirectory(System.getProperty("user.home"));
		}
	}
	
	/**
	 * Save options to file.
	 */
	public static void saveOptions()
	{
		Logger.info("KEEPUP: saving options ...");
		
		try
		{
			options.put("lastDirectory", lastDirectory);
			// options.put("ubuntuUser", ubuntuUser);
			// options.put("ubuntuPass", ubuntuPass);
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(optionsFile.toString()));
			out.writeObject(options);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void initBackup()
	{
		Logger.info("BACKUP: initialising backup system ...");
		
		scheduler = new Scheduler();
		watcher.addListener(scheduler);
		scheduler.addListener(watcher);
	}
	
	// ======================================================================================
	// #endregion Initialisation.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Update free space, and display it.
	 */
	public static void updateFreeSpace()
	{
		Logger.info("KEEPUP: updating freespace ...");
		
		HashMap<String, Long> freeSpace = new HashMap<String, Long>();
		
		enabledCsps.values().stream()
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
		
		mainWindow.getStatusBar().updateFreeSpace(freeSpace);
	}
	
	/**
	 * Refresh tree by going through the folders already loaded, and making sure they're up to date.
	 * It does NOT load the tree from scratch.
	 */
	public static void refreshTree()
	{
		csTreeController.updateTree();
	}
	
	public static void navigateBackward()
	{
		csTreeController.navigateBackward();
	}
	
	public static void navigateForward()
	{
		csTreeController.navigateForward();
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Folders operations.
	// ======================================================================================
	
	/**
	 * Creates the folder.
	 *
	 * @param parentPath
	 *            parent path.
	 * @param name
	 *            Name.
	 */
	public static void createFolder(String parentPath, String name)
	{
		CombinedFolder parent = App.searchForFolder(parentPath);
		
		if (parent == null)
		{
			Msg.showError("KEEPUP: " + parentPath + " doesn't exist to create " + name);
			return;
		}
		
		enabledCsps.values().parallelStream()
				.forEach(csp ->
				{
					try
					{
						RemoteFolder<?> newFolder = csp.getAbstractFactory().createFolder();
						newFolder.setName(name);
						newFolder.create(parentPath, null);
					}
					catch (CreationException e)
					{
						// if error is related to anything other than existence, then display
						if ( !e.getMessage().contains("exists"))
						{
							Msg.showError(e.getMessage());
							e.printStackTrace();
						}
					}
				});
	}
	
	/**
	 * Creates the folder.
	 *
	 * @param parent
	 *            Parent.
	 * @param name
	 *            Name.
	 */
	public static void createFolder(CombinedFolder parent, String name)
	{
		// no parent, then choose root.
		if (parent == null)
		{
			parent = ROOT;
		}
		
		HashSet<RemoteFolder<?>> newFolders = new HashSet<RemoteFolder<?>>();
		
		final CombinedFolder threadedParent = parent;
		
		// go over the csps list, and create a folder in memory for each.
		enabledCsps.values().parallelStream()
				.forEach(csp ->
				{
					try
					{
						// try to find the existing combinedfolder.
						CombinedFolder result = threadedParent.findFolder(name);
						
						// if it doesn't exist, or the csp folder isn't added ...
						if ((result == null) || !result.getCspFolders().containsKey(csp.getName()))
						{
							// create the csp folder.
							RemoteFolder<?> newFolder = csp.getAbstractFactory().createFolder();
							newFolder.setName(name);
							newFolders.add(newFolder);
							newFolder.create(threadedParent.getPath(), null);
						}
					}
					catch (CreationException e)
					{
						if ( !e.getMessage().contains("exists"))
						{
							Msg.showError(e.getMessage());
							e.printStackTrace();
						}
					}
				});
	}
	
	/**
	 * Rename folder.
	 *
	 * @param folder
	 *            Folder.
	 * @param newName
	 *            New name.
	 */
	public static void renameFolder(final CombinedFolder folder, final String newName)
	{
		for (final RemoteFolder<?> remoteFolder : folder.getCspFolders().values())
		{
			new Thread(() ->
			{
				try
				{
					remoteFolder.rename(newName
							, event ->
							{
								if (event.getState() == OperationState.FAILED)
								{
									Msg.showError("Failed to rename folder: " + event.getContainer().getName());
								}
							});
				}
				catch (OperationException e)
				{
					e.printStackTrace();
					Msg.showError(e.getMessage());
				}
			}).start();
		}
	}
	
	/**
	 * Delete folder.
	 *
	 * @param folder
	 *            Folder.
	 */
	@SuppressWarnings("incomplete-switch")
	public static void deleteFolder(CombinedFolder folder)
	{
		for (final RemoteFolder<?> remoteFolder : folder.getCspFolders().values())
		{
			new Thread(() ->
			{
				try
				{
					remoteFolder.delete(event ->
					{
						switch (event.getState())
						{
							case FAILED:
								Msg.showError("Failed to delete: '" + event.getContainer().getPath() + "'.");
								break;
						}
					});
				}
				catch (OperationException e)
				{
					e.printStackTrace();
					Msg.showError(e.getMessage());
				}
				
			}).start();
		}
	}
	
	/**
	 * Gets the selected folder.
	 *
	 * @return the selected folder
	 */
	public static CombinedFolder getSelectedFolder()
	{
		return csTreeController.getSelectedFolder();
	}
	
	// ======================================================================================
	// #endregion Folders operations.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Updates the selected folder. It grabs the files in the folder, and then passes them to
	 * {@link CSBrowserPanel#updateTable(File[])}.
	 */
	public static void updateTable()
	{
		CombinedFolder folder = getSelectedFolder();
		
		if (folder != null)
		{
			folder.updateCombinedFolder(true);
			csTableController.updateTable(folder.getFilesList(false));
		}
		else
		{
			ROOT.updateCombinedFolder(true);
			csTableController.updateTable(ROOT.getFilesList(false));
		}
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files operations.
	// ======================================================================================
	
	/**
	 * Download selected files to the selected folder.
	 *
	 * @param files
	 *            Files list.
	 */
	public static void downloadFiles(List<File<?>> files)
	{
		// no files, then no need to proceed.
		if (files.isEmpty())
		{
			Logger.error("Nothing to download!");
			Msg.showError("Please, choose a file first from the files list.");
			return;
		}
		
		if (files.get(0).isLocal())
		{
			return;
		}
		
		try
		{
			// prepare folder object ...
			LocalFolder parent = new LocalFolder(getLastDirectory());
			// parent.buildTree(false);
			
			// --------------------------------------------------------------------------------------
			// #region Make sure there's enough space.
			
			Long filesSize = 0L;
			
			for (File<?> file : files)
			{
				filesSize += file.getSize();
			}
			
			// no space, then no need to proceed.
			if (parent.getLocalFreeSpace() <= filesSize)
			{
				Logger.error("Not enough space!");
				Msg.showError("Please, free some space on local disk.");
				return;
			}
			
			// #endregion Make sure there's enough space.
			// --------------------------------------------------------------------------------------
			
			// ... download all files passed to that folder.
			for (RemoteFile<?> file : convertListToRemote(files))
			{
				if ( !parent.searchByName(file.getName(), false, false).isEmpty())
				{
					if ( !Msg.askQuestion("Overwrite: '" + file.getPath() + "'?"))
					{
						continue;
					}
				}
				
				mainWindow.getQueuePanel().addTransferJob(
						file.getCsp().download(file, parent, true, mainWindow.getQueuePanel()), "Download");
			}
		}
		catch (TransferException | OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}
	
	/**
	 * Upload selected file to the selected folder.
	 *
	 * @param file
	 *            File.
	 * @param parent
	 *            Parent remote folder.
	 * @param overwrite
	 *            Overwrite? If nothing passed, then the user will be asked if exists.
	 * @return Upload job
	 */
	public static UploadJob<?, ?> uploadFile(LocalFile file, CombinedFolder parent, boolean... overwrite)
	{
		List<LocalFile> fileList = new ArrayList<LocalFile>();
		fileList.add(file);
		
		List<UploadJob<?, ?>> uploadJobs = uploadFiles(fileList, parent, overwrite);
		
		if ((uploadJobs != null) && !uploadJobs.isEmpty())
		{
			return uploadJobs.get(0);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Upload selected files to the selected folder.
	 *
	 * @param files
	 *            Files.
	 * @param parent
	 *            Parent remote folder.
	 * @param overwrite
	 *            Overwrite? If nothing passed, then the user will be asked if exists.
	 * @return List
	 */
	public static List<UploadJob<?, ?>> uploadFiles(List<LocalFile> files, CombinedFolder parent, boolean... overwrite)
	{
		// choose best fitting to upload to its root.
		if (parent == null)
		{
			parent = ROOT;
		}
		
		List<UploadJob<?, ?>> uploadJobs = new ArrayList<UploadJob<?, ?>>();
		
		// choose best fitting to upload to.
		RemoteFolder<?> parentRemoteFolder;
		CSP<?, ?, ?> csp;
		
		try
		{
			if ((csp = chooseCsp(files)) == null)
			{
				return null;
			}
			
			parentRemoteFolder = parent.getCspFolders().get(csp.getName());
		}
		catch (OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
			return uploadJobs;
		}
		
		try
		{
			// upload each file to the folder.
			outerLoop:
			for (LocalFile file : files)
			{
				// check if file exists on any of the CSPs
				List<Container<?>> existingContainers = parent.findContainer(file.getName(), false, false);
				
				// found something
				if ( !existingContainers.isEmpty())
				{
					// check each container returned
					for (Container<?> container : existingContainers)
					{
						// if it's a file and has the same name
						if ( !container.isFolder() && (container.getName() == file.getName()))
						{
							// ask to overwrite
							if (((overwrite.length <= 0) && !Msg.askQuestion("Overwrite: '" + file.getPath() + "'?"))
									|| ((overwrite.length > 0) && !overwrite[0]))
							{
								continue outerLoop;		// skip file.
							}
							else
							{	// overwrite
								container.delete();
							}
						}
					}
				}
				
				// create the remote folder before uploading to it at the csp.
				if (parentRemoteFolder == null)
				{
					parentRemoteFolder = csp.getAbstractFactory().createFolder();
					parentRemoteFolder.setName(parent.getName());
					parentRemoteFolder.create(
							parent.getPath().substring(0, (parent.getPath().lastIndexOf("/" + parent.getName())))
							, event -> {});
				}
				
				UploadJob<?, ?> uploadJob = parentRemoteFolder.getCsp()
						.upload(file, parentRemoteFolder, true, mainWindow.getQueuePanel());
				
				// add file job to the gui queue.
				mainWindow.getQueuePanel().addTransferJob(uploadJob, "Upload");
				
				uploadJobs.add(uploadJob);
			}
		}
		catch (TransferException | CreationException | OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
		
		return uploadJobs;
	}
	
	/**
	 * Rename file.
	 *
	 * @param files
	 *            Files.
	 * @param newName
	 *            New name.
	 */
	public static void renameFile(final List<File<?>> files, final String newName)
	{
		if (files.isEmpty() || files.get(0).isLocal())
		{
			return;
		}
		
		new Thread(() ->
		{
			try
			{
				files.get(0).rename(newName);
				updateTable();
			}
			catch (OperationException e)
			{
				e.printStackTrace();
				Msg.showError("Failed to rename file: " + files.get(0).getName() + " => " + e.getMessage());
			}
		}).start();
	}
	
	/**
	 * Copy files and hold their reference in memory.
	 *
	 * @param files
	 *            Files.
	 */
	public static void copyFiles(List<File<?>> files)
	{
		if (files.isEmpty() || files.get(0).isLocal())
		{
			return;
		}
		
		filesInHand = convertListToRemote(files);
		fileAction = FileActions.COPY;
	}
	
	/**
	 * Move files and hold their reference in memory.
	 *
	 * @param files
	 *            Files.
	 */
	public static void moveFiles(List<File<?>> files)
	{
		if (files.isEmpty() || files.get(0).isLocal())
		{
			return;
		}
		
		filesInHand = convertListToRemote(files);
		fileAction = FileActions.MOVE;
	}
	
	/**
	 * Paste files.
	 *
	 * @param folder
	 *            Folder.
	 */
	public static void pasteFiles(CombinedFolder folder)
	{
		for (RemoteFile<?> file : filesInHand)
		{
			RemoteFolder<?> remoteFolder = folder.getCspFolders().get(file.getCsp().getName());
			
			// create the remote folder before uploading to it at the csp.
			if (remoteFolder == null)
			{
				try
				{
					remoteFolder = file.getCsp().getAbstractFactory().createFolder();
					remoteFolder.setName(folder.getName());
					remoteFolder.create(
							folder.getPath().substring(0, (folder.getPath().lastIndexOf("/" + folder.getName())))
							, event -> {});
				}
				catch (CreationException e)
				{
					e.printStackTrace();
					Msg.showError(e.getMessage());
					continue;
				}
				
			}
			
			// make sure the user wants to overwrite if necessary.
			if (remoteFolder.searchById(file.getId(), false) != null)
			{
				if ( !Msg.askConfirmation("Overwrite: " + file.getName() + " in " + folder.getPath()))
				{
					return;
				}
			}
			
			try
			{
				switch (fileAction)
				{
					case COPY:
						file.copy(remoteFolder, true);
						updateTable();
						break;
					
					case MOVE:
						file.move(remoteFolder, true);
						updateTable();
						break;
				}
			}
			catch (OperationException e)
			{
				e.printStackTrace();
				Msg.showError("Failed to copy/move file: " + file.getName() + " => " + e.getMessage());
			}
		}
	}
	
	/**
	 * Delete passed files.
	 *
	 * @param files
	 *            the files.
	 */
	public static void deleteFiles(List<File<?>> files)
	{
		if (files.isEmpty() || files.get(0).isLocal())
		{
			return;
		}
		
		for (final RemoteFile<?> file : convertListToRemote(files))
		{
			new Thread(() ->
			{
				try
				{
					file.delete();
					updateTable();
				}
				catch (OperationException e)
				{
					e.printStackTrace();
					Msg.showError("Failed to delete file: " + file.getName() + " => " + e.getMessage());
				}
			}).start();
		}
	}
	
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
	 * Gets the selected files.
	 *
	 * @return the selected files
	 */
	public static List<RemoteFile<?>> convertListToRemote(List<File<?>> files)
	{
		return files.parallelStream()
				.map(file -> (RemoteFile<?>) file)
				.collect(Collectors.toList());
	}
	
	// ======================================================================================
	// #endregion Files operations.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region CSP operations.
	// ======================================================================================
	
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
			Logger.error("Not enough space!");
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
	 * Search for file in all CSPs using the path passed.
	 *
	 * @param path
	 *            Path.
	 * @return List of files with the same path in all CSPs.
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
	 * @return combined folder found, or not if not.
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
			result.updateCombinedFolder(true);
			result = result.findFolder(splitPath.remove(0));
		}
		
		// if part of the path is not found ...
		if ( !splitPath.isEmpty() || (result == null))
		{
			return null;		// ... return nothing.
		}
		else
		{	// ... or search for the folder in the end node, might return null.
			return result.findFolder(containerName);
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
	 * @return
	 */
	public static JFrame showSubWindow(JPanel panel, String title)
	{
		// create a frame for the panel.
		JFrame frame = new JFrame(title);
		
		// open the frame relative to the main window.
		Point mainWindowLocation = mainWindow.getFrame().getLocation();
		frame.setLocation((int) mainWindowLocation.getX() + 50, (int) mainWindowLocation.getY() + 50);
		
		// when the frame is closed, dispose of it and return focus to the main window.
		frame.addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				frame.dispose();
				setMainWindowFocusable(true);
			}
			
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				frame.dispose();
				setMainWindowFocusable(true);
			}
		});
		
		// add the passed panel to the frame.
		frame.add(panel);
		// show the frame.
		frame.setVisible(true);
		// fit the frame to panel.
		frame.pack();
		
		// disable the main window.
		setMainWindowFocusable(false);
		
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
		mainWindow.getFrame().setFocusableWindowState(focusable);
		mainWindow.getFrame().setEnabled(focusable);
		
		// bring it to front.
		if (focusable)
		{
			mainWindow.getFrame().setVisible(true);
		}
	}
	
	/**
	 * Human readable size conversion.<br/>
	 * <br />
	 * Credit: 'aioobe' at 'StackOverFlow.com'
	 *
	 * @param bytes
	 *            Size in bytes.
	 * @return Human readable size.
	 */
	public static String humanReadableSize(long bytes)
	{
		int unit = 1024;
		
		if (bytes < unit)
		{
			return bytes + " B";
		}
		
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = ("KMGTPE").charAt(exp - 1) + ("i");
		
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	/**
	 * Gets the MD5 corresponding to the passed string.
	 *
	 * <p>
	 * Credit: Javin Paul<br />
	 * (at http://javarevisited.blogspot.com/2013/03/generate-md5-hash-in-java-string-byte-array-example-tutorial.html)
	 * </p>
	 *
	 * @param string
	 *            String.
	 * @return the MD5 in hexadecimal
	 */
	public static String getMD5(String string)
	{
		try
		{
			byte[] bytesOfMessage = string.getBytes("UTF-8");
			
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] hash = md5.digest(bytesOfMessage);
			
			// converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2 * hash.length);
			
			for (byte b : hash)
			{
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString();
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Form the path to the parent, using a prefix, and removing ':' and '\'
	 * because they are windows based and unsupported.
	 */
	public static String formRemoteBackupParent(Container<?> container)
	{
		return "/keepup_backup/" + container.getParent().getPath().replace(":", "").replace("\\", "/");
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
