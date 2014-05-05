/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			License terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/CombinedFolder.java
 *
 *			Modified: May 3, 2014 (9:42:29 AM)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yagasoft.keepup.App;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.content.ChangeEvent;
import com.yagasoft.overcast.base.container.content.IContentListener;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.base.container.update.IUpdateListener;
import com.yagasoft.overcast.base.container.update.UpdateEvent;
import com.yagasoft.overcast.exception.AccessException;
import com.yagasoft.overcast.exception.OperationException;


/**
 * The Class CombinedFolder.
 */
public class CombinedFolder implements Comparable<CombinedFolder>, IContentListener, IUpdateListener
{

	/** Folder name. */
	String								name;

	/** Path of the folder. */
	String								path;

	/** Folders indexed by their CSP name, and holding the same path and folder name. */
	HashMap<String, RemoteFolder<?>>	cspFolders	= new HashMap<String, RemoteFolder<?>>();

	/** The node in the tree that represents this folder visually. */
	private DefaultMutableTreeNode		node;

	/**
	 * Instantiates a new combined folder.
	 *
	 * @param firstFolder
	 *            First folder to add.
	 */
	public CombinedFolder(RemoteFolder<?> firstFolder)
	{
		if (firstFolder != null)
		{
			addCspFolder(firstFolder);
		}

		setNode(new DefaultMutableTreeNode(this));
	}

	/**
	 * Adds the folder to the list of folders with the same name.
	 *
	 * @param folder
	 *            Folder.
	 */
	public synchronized void addCspFolder(RemoteFolder<?> folder)
	{
		cspFolders.put(folder.getCsp().getName(), folder);
		folder.addContentListener(this);
		folder.addUpdateListener(this);

		if ((name == null) || (path == null))
		{
			name = folder.getName();
			path = folder.getPath();

			if ((name == null) || (path == null) || path.equals("/"))
			{
				name = "root";
				path = "/";
			}
		}
	}

	/**
	 * Removes the folder to the list of folders.
	 *
	 * @param folder
	 *            Folder.
	 */
	public synchronized void removeCspFolder(RemoteFolder<?> folder)
	{
		cspFolders.remove(folder.getCsp().getName());

		if (cspFolders.size() <= 0)
		{
			App.removeNodeFromTree(node);
		}
	}

	/**
	 * Creates a new {@link CombinedFolder} object, and a visual tree node, and then adds that node to the tree.
	 *
	 * @param folder
	 *            Folder to add.
	 */
	public synchronized void addChild(RemoteFolder<?> folder)
	{
		// search for the child in this folder.
		CombinedFolder combinedFolder = findNode(folder.getName());

		// if you can't find it, create a new combinedfolder for it
		if (combinedFolder == null)
		{
			combinedFolder = new CombinedFolder(folder);
			folder.addContentListener(combinedFolder);		// used for when the folder content changes.
			folder.addUpdateListener(combinedFolder);		// used for when the name changes.
		}
		else
		{
			// if found, then add it to the already existing combinedfolder.
			combinedFolder.addCspFolder(folder);
			folder.addContentListener(combinedFolder);
			folder.addUpdateListener(combinedFolder);
		}

		App.addNodeToTree(combinedFolder.getNode(), node);
	}

	public synchronized void removeChild(RemoteFolder<?> folder)
	{
		CombinedFolder combinedFolder = findNode(folder.getName());

		if (combinedFolder != null)
		{
			combinedFolder.removeCspFolder(folder);
		}
	}

	/**
	 * Updates all the folders in this CombinedFolder online.
	 *
	 * @param online
	 *            update from source?
	 */
	public void updateCombinedFolder(final boolean online)
	{
		// go through the folders' list.
		for (final RemoteFolder<?> folder : cspFolders.values())
		{
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					try
					{
						if (online)
						{
							folder.updateFromSource(true, false);
						}
					}
					catch (OperationException e)
					{
						e.printStackTrace();
					}

					name = folder.getName();
				}
			}).start();
		}
	}

	/**
	 * Returns an array containing the list of files in the folders with the same name,
	 * from all the CSPs.
	 *
	 * @param update
	 *            Update online?
	 * @return the files array
	 */
	public File<?>[] getFilesArray(boolean update)
	{
		// combined files list.
		ArrayList<File<?>> files = new ArrayList<File<?>>();

		// go through the folders' list.
		for (RemoteFolder<?> folder : cspFolders.values())
		{
			if (update)
			{
				try
				{
					folder.updateFromSource(true, false);
				}
				catch (OperationException e)
				{
					e.printStackTrace();
				}
			}

			files.addAll(folder.getFilesList());
		}

		// sort it to appear as contiguous.
		Collections.sort(files);

		return files.toArray(new File<?>[files.size()]);
	}

	/**
	 * Remove folders that don't exist anymore at their CSP.
	 */
	public void filterFoldersList()
	{
		for (RemoteFolder<?> folder : cspFolders.values())
		{
			try
			{
				if ( !folder.isExist())
				{
					removeCspFolder(folder);
				}
			}
			catch (AccessException | OperationException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Finds the {@link CombinedFolder} with the same name as the one passed contained within.
	 *
	 * @param name
	 *            Name to search for.
	 * @return Combined folder found
	 */
	@SuppressWarnings("unchecked")
	public synchronized CombinedFolder findNode(String name)
	{
		Enumeration<DefaultMutableTreeNode> children = node.children();

		while (children.hasMoreElements())
		{
			CombinedFolder child = (CombinedFolder) children.nextElement().getUserObject();

			if (child.name.equals(name))
			{
				return child;
			}
		}

		return null;
	}

	@Override
	public void contentsChanged(ChangeEvent event)
	{
		switch (event.getChange())
		{
			case ADD:
				if (event.getObject().isFolder())
				{
					addChild((RemoteFolder<?>) event.getObject());
				}
				break;

			case REMOVE:
				if (event.getObject().isFolder())
				{
					removeChild((RemoteFolder<?>) event.getObject());
				}
				break;
		}
	}

	@Override
	public void containerUpdated(UpdateEvent event)
	{
		name = event.getContainer().getName();
		App.updateNode(node);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return ((obj instanceof CombinedFolder) && path.equals(((CombinedFolder) obj).path));
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CombinedFolder folder)
	{
		return path.toLowerCase().compareTo(folder.path.toLowerCase());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================

	/**
	 * @return the node
	 */
	public DefaultMutableTreeNode getNode()
	{
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(DefaultMutableTreeNode node)
	{
		this.node = node;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return the cspFolders
	 */
	public HashMap<String, RemoteFolder<?>> getCspFolders()
	{
		return cspFolders;
	}

	/**
	 * @param cspFolders
	 *            the cspFolders to set
	 */
	public void setCspFolders(HashMap<String, RemoteFolder<?>> cspFolders)
	{
		this.cspFolders = cspFolders;
	}

	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////

}
