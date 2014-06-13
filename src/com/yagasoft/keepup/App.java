/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 *
 *			Modified: 26-May-2014 (22:21:43)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.yagasoft.keepup.backup.ui.BackupController;
import com.yagasoft.keepup.backup.ui.BackupPanel;
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
import com.yagasoft.keepup.ui.BrowserPanel;
import com.yagasoft.keepup.ui.FileTable;
import com.yagasoft.keepup.ui.MainWindow;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.operation.Operation;
import com.yagasoft.overcast.base.container.operation.OperationState;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.base.csp.CSP;
import com.yagasoft.overcast.exception.AuthorisationException;
import com.yagasoft.overcast.exception.CSPBuildException;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;
import com.yagasoft.overcast.exception.TransferException;
import com.yagasoft.overcast.implement.google.Google;


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

	/** CSPs list currently loaded. */
	public static HashMap<String, CSP<?, ?, ?>>	csps;

	/** Constant: Root of the tree. */
	public static final CombinedFolder			ROOT		= new CombinedFolder(null);

	// --------------------------------------------------------------------------------------
	// #region GUI.

	/** Main window. */
	public static CombinedStoragePanel			combinedStoragePanel;

	/** Browser panel. */
	public static CSBrowserPanel				csBrowserPanel;

	/** Folders tree. */
	public static CSTree						csFoldersTree;

	/** Tree controller. */
	public static CSTreeController				treeController;

	/** Table controller. */
	public static CSTableController				tableController;

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
		loadOptions();
		initCSPs();
	}

	/**
	 * Constructs the main window of the combined storage app, and sets it to be displayed.
	 */
	private static void initGUI()
	{
		MainWindow mainWindow = new MainWindow();

		// combined storage feature gui
		csFoldersTree = new CSTree(ROOT.getNode());
		csFilesTable = new CSTable(
				new String[] { "Name", "Size", "CSP" }
				, new float[] { 0.5f, 0.25f, 0.25f }
				, new int[] { 1 });
		csBrowserPanel = new CSBrowserPanel(csFoldersTree, csFilesTable);
		combinedStoragePanel = new CombinedStoragePanel(csBrowserPanel);
		mainWindow.addPanel("Combined Storage", combinedStoragePanel);

		// backup feature GUI
		localFoldersTree = new LocalTree();
		localFilesTable = new LocalTable(
				new String[] { "Name", "Size", "Status" }
				, new float[] { 0.65f, 0.20f, 0.15f }
				, new int[] { 1 });
		localBrowserPanel = new BrowserPanel(localFoldersTree, localFilesTable);
		watchedFilesTable = new FileTable(
				new String[] { "Name", "Path", "Size", "Status"  }
				, new float[] { 0.0f, 0.65f, 0.20f, 0.15f  }
				, new int[] { 2 });
		backupPanel = new BackupPanel(localBrowserPanel, watchedFilesTable);
		mainWindow.addPanel("Backup", backupPanel);

		// save options when application is closing.
		mainWindow.getFrame().addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				saveOptions();
			}
		});

		mainWindow.switchToPanel("Combined Storage");
		combinedStoragePanel.getBrowserPanel().resetDivider(mainWindow.getFrame().getWidth() / 3);

		mainWindow.getFrame().setVisible(true);
	}

	/**
	 * Inits the view controllers.
	 */
	public static void initControllers()
	{
		treeController = new CSTreeController(csFoldersTree);
		tableController = new CSTableController(csFilesTable);
		treeController.addTreeSelectionListener(tableController);

		localTableController = new LocalTableController(localFilesTable);
		localFoldersTree.addSelectionListener(localTableController);

		watchTableController = new WatcherTableController(watchedFilesTable);
		watcher = new Watcher();
		watcher.addListener(watchTableController);

		backupPanelController = new BackupController(backupPanel, localTableController, watchTableController);
		backupPanelController.addListener(watcher);
	}

	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 *
	 * @throws CSPBuildException
	 */
	private static void initCSPs()
	{
		csps = new HashMap<String, CSP<?, ?, ?>>();

		// init CSPs in parallel.
		ExecutorService executor = Executors.newCachedThreadPool();

		executor.execute(() ->
		{
			try
			{
				addCSP(Google.getInstance("os1983@gmail.com"));
			}
			catch (AuthorisationException | CSPBuildException e)
			{
				Msg.showError(e.getMessage());
				e.printStackTrace();
			}
		});

		// executor.execute(() ->
		// {
		// try
		// {
		// addCSP(Dropbox.getInstance("os008@hotmail.com", 65234));
		// }
		// catch (AuthorisationException | CSPBuildException e)
		// {
		// Msg.showError(e.getMessage());
		// e.printStackTrace();
		// }
		// });

		try
		{
			executor.shutdown();

			// wait for the threads to finish before expanding the root
			// -- caused problems with showing expansion icon of children
			if (executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS))
			{
				// show the root as expanded at the start.
				csFoldersTree.expandPathToNode(ROOT.getNode());
			}
		}
		catch (InterruptedException e)
		{
			Msg.showError(e.getMessage());
			e.printStackTrace();
		}

		// if ((ubuntuUser == null) || (ubuntuPass == null))
		// {
		// ubuntuUser = Msg.getInput("Please, enter Ubuntu One's username:");
		// ubuntuPass = Msg.getPassword("Please, enter Ubuntu One's password:");
		// }
		//
		// addCSP(new Ubuntu(ubuntuUser, ubuntuPass));
	}

	/**
	 * Initialises the folders tree's root by using the in-memory folder list.
	 */
	public static void initTree()
	{
		// get each CSP list if available.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			try
			{
				csp.buildFileTree(false);
			}
			catch (OperationException | CreationException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Load options from file.
	 */
	@SuppressWarnings("unchecked")
	public static void loadOptions()
	{
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

	// ======================================================================================
	// #endregion Initialisation.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Update free space, and display it.
	 */
	public static void updateFreeSpace()
	{
		HashMap<String, Long> freeSpace = new HashMap<String, Long>();

		csps.values().parallelStream()
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

		combinedStoragePanel.getStatusBar().updateFreeSpace(freeSpace);
	}

	/**
	 * Refresh tree by going through the folders already loaded, and making sure they're up to date.
	 * It does NOT load the tree from scratch.
	 */
	public static void refreshTree()
	{
		treeController.updateTree();
	}

	public static void navigateBackward()
	{
		treeController.navigateBackward();
	}

	public static void navigateForward()
	{
		treeController.navigateForward();
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Folders operations.
	// ======================================================================================

	/**
	 * Creates the folder.
	 *
	 * @param parent
	 *            Parent.
	 * @param name
	 *            Name.
	 */
	@SuppressWarnings("incomplete-switch")
	public static void createFolder(CombinedFolder parent, String name)
	{
		// no parent, then choose root.
		if (parent == null)
		{
			parent = ROOT;
		}

		HashSet<RemoteFolder<?>> newFolders = new HashSet<RemoteFolder<?>>();

		final CombinedFolder threadedParent = parent;

		// go over the csps list, and create a folder for each.
		csps.values().parallelStream()
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
					newFolders.add(newFolder);
					newFolder.setName(name);
				}
			}
			catch (CreationException e)
			{
				Msg.showError(e.getMessage());
				e.printStackTrace();
			}
		});

		// create the folders that don't exist at their csp.
		for (final RemoteFolder<?> newFolder : newFolders)
		{
			new Thread(() ->
			{
				try
				{
					// get the csp-related parent path from the combinedfolder, and create the new folder in it.
					newFolder.create(threadedParent.getPath()
							, event ->
							{
								switch (event.getState())
								{
									case COMPLETED:
										threadedParent.addChild(newFolder);		// success, add the new folder.
										break;

									case FAILED:
										Msg.showError("Failed to create: '" + event.getContainer().getPath() + "'.");
										break;
								}
							});
				}
				catch (CreationException e)
				{
					Msg.showError(e.getMessage());
					e.printStackTrace();
				}
			}).start();
		}
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
		return treeController.getSelectedFolder();
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
			tableController.updateTable(folder.getFilesList(false));
		}
		else
		{
			ROOT.updateCombinedFolder(true);
			tableController.updateTable(ROOT.getFilesList(false));
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
	public static void downloadFile(List<RemoteFile<?>> files)
	{
		// no files, then no need to proceed.
		if (files.size() == 0)
		{
			Logger.error("Nothing to download!");
			Msg.showError("Please, choose a file first from the files list.");
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

			for (RemoteFile<?> file : files)
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
			for (RemoteFile<?> file : files)
			{
				if (parent.searchByName(file.getName(), false).size() > 0)
				{
					if (Msg.showQuestion("Overwrite: '" + file.getPath() + "'?") != 0)
					{
						continue;
					}
				}

				combinedStoragePanel.getQueuePanel().addTransferJob(
						file.getCsp().download(file, parent, true, combinedStoragePanel.getQueuePanel()), "Download");
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
	 */
	public static void uploadFile(List<LocalFile> files, CombinedFolder parent)
	{
		// choose best fitting to upload to its root.
		if (parent == null)
		{
			parent = ROOT;
		}

		// choose best fitting to upload to.
		RemoteFolder<?> parentRemoteFolder;
		CSP<?, ?, ?> csp;

		try
		{
			csp = chooseCsp(files);
			parentRemoteFolder = parent.getCspFolders().get(csp.getName());
		}
		catch (OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
			return;
		}

		try
		{
			// upload each file to the folder.
			for (LocalFile file : files)
			{
				// create the remote folder before uploading to it at the csp.
				if (parentRemoteFolder == null)
				{
					parentRemoteFolder = csp.getAbstractFactory().createFolder();
					parentRemoteFolder.setName(parent.getName());
					parentRemoteFolder.create(
							parent.getPath().substring(0, (parent.getPath().lastIndexOf("/" + parent.getName())))
							, event -> {});
				}
				else
				{
					// make sure the file doesn't exist, and ask to overwrite if exists.
					if (parentRemoteFolder.searchByName(file.getName(), false).size() > 0)
					{
						if (Msg.showQuestion("Overwrite: '" + file.getPath() + "'?") != 0)
						{
							continue;
						}
					}
				}

				// add file job to the gui queue.
				combinedStoragePanel.getQueuePanel().addTransferJob(
						parentRemoteFolder.getCsp().upload(file, parentRemoteFolder, true, combinedStoragePanel.getQueuePanel()),
						"Upload");
			}
		}
		catch (TransferException | CreationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}

	/**
	 * Rename file.
	 *
	 * @param files
	 *            Files.
	 * @param newName
	 *            New name.
	 */
	public static void renameFile(final List<RemoteFile<?>> files, final String newName)
	{
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
	public static void copyFiles(List<RemoteFile<?>> files)
	{
		filesInHand = files;
		fileAction = FileActions.COPY;
	}

	/**
	 * Move files and hold their reference in memory.
	 *
	 * @param files
	 *            Files.
	 */
	public static void moveFiles(List<RemoteFile<?>> files)
	{
		filesInHand = files;
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
	public static void deleteFiles(List<RemoteFile<?>> files)
	{
		for (final RemoteFile<?> file : files)
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
	public static List<RemoteFile<?>> getSelectedFiles()
	{
		return tableController.getSelectedFiles().parallelStream()
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
	public static void addCSP(final CSP<?, ?, ?> csp)
	{
		try
		{
			csp.initTree();
			csp.getRemoteFileTree().addOperationListener(ROOT, Operation.ADD);
			csp.getRemoteFileTree().addOperationListener(ROOT, Operation.REMOVE);
			ROOT.addCspFolder(csp.getRemoteFileTree());
			csps.put(csp.getName(), csp);
			updateFreeSpace();
			initTree();
		}
		catch (OperationException e)
		{
			e.printStackTrace();
		}
	}

	public static CSP<?, ?, ?>[] getCspsArray()
	{
		return csps.values().toArray(new CSP<?, ?, ?>[csps.values().size()]);
	}

	/**
	 * Gets the CSP with the least space to fit the files passed.
	 *
	 * @param files
	 *            Files.
	 * @return The best fit CSP.
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
		for (CSP<?, ?, ?> csp : csps.values())
		{
			fits.put(fit(csp, filesSize), csp);
		}

		// sort the fits.
		return fits.get(fits.keySet().parallelStream()
				.filter(fit -> fit < 0.95)
				.max((a, b) -> a.compareTo(b)));
	}

	// ======================================================================================
	// #endregion CSP operations.
	// //////////////////////////////////////////////////////////////////////////////////////

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
