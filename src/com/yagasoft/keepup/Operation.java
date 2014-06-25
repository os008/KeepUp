/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/Operation.java
 *
 *			Modified: 25-Jun-2014 (02:53:43)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.util.HashSet;
import java.util.List;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.operation.OperationState;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;


/**
 * The Class Operation.
 */
public final class Operation
{

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

		App.enabledCsps.values().parallelStream()
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
			parent = App.ROOT;
		}

		HashSet<RemoteFolder<?>> newFolders = new HashSet<RemoteFolder<?>>();

		final CombinedFolder threadedParent = parent;

		// go over the csps list, and create a folder in memory for each.
		App.enabledCsps.values().parallelStream()
				.forEach(csp ->
				{
					try
					{
						// try to find the existing combinedfolder.
						CombinedFolder result = threadedParent.findFolder(name, false);

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

	// ======================================================================================
	// #endregion Folders operations.
	// //////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files operations.
	// ======================================================================================

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
		if (files.isEmpty())
		{
			return;
		}

		new Thread(() ->
		{
			try
			{
				files.get(0).rename(newName);

				if ( !files.get(0).isLocal())
				{
					GUI.updateTable();
				}
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

		App.filesInHand = Util.convertListToRemote(files);
		App.fileAction = App.FileActions.COPY;
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

		App.filesInHand = Util.convertListToRemote(files);
		App.fileAction = App.FileActions.MOVE;
	}

	/**
	 * Paste files.
	 *
	 * @param folder
	 *            Folder.
	 */
	public static void pasteFiles(CombinedFolder folder)
	{
		for (RemoteFile<?> file : App.filesInHand)
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
				switch (App.fileAction)
				{
					case COPY:
						file.copy(remoteFolder, true);
						GUI.updateTable();
						break;

					case MOVE:
						file.move(remoteFolder, true);
						GUI.updateTable();
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
		if (files.isEmpty())
		{
			return;
		}

		for (final File<?> file : files)
		{
			new Thread(() ->
			{
				try
				{
					file.delete();

					if ( !file.isLocal())
					{
						GUI.updateTable();
					}
				}
				catch (OperationException e)
				{
					e.printStackTrace();
					Msg.showError("Failed to delete file: " + file.getName() + " => " + e.getMessage());
				}
			}).start();
		}
	}

	// ======================================================================================
	// #endregion Files operations.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Singleton!
	 */
	private Operation()
	{}

}
