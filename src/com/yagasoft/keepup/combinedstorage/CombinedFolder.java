/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/CombinedFolder.java
 * 
 *			Modified: 07-May-2014 (20:32:55)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;

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
	private String							name;

	/** Path of the folder. */
	private String							path;

	/** Parent. */
	protected CombinedFolder					parent;

	/** Folders indexed by their CSP name, and holding the same path and folder name. */
	HashMap<String, RemoteFolder<?>>			cspFolders			= new HashMap<String, RemoteFolder<?>>();

	/** Folders inside this folder mapped by path (tree implementation). */
	protected HashMap<String, CombinedFolder>	subFolders			= new HashMap<String, CombinedFolder>();

	/** The node in the tree that represents this folder visually. */
	private DefaultMutableTreeNode				node;

	/** Content listeners. */
	protected HashSet<ContentListener>			contentListeners	= new HashSet<ContentListener>();

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

		updateInfo(folder);
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
		CombinedFolder combinedFolder = findFolder(folder.getName());

		// if you can't find it, create a new combinedfolder for it
		if (combinedFolder == null)
		{
			combinedFolder = new CombinedFolder(folder);
			folder.addContentListener(combinedFolder);		// used for when the folder content changes.
			folder.addUpdateListener(combinedFolder);		// used for when the name changes.
			subFolders.put(combinedFolder.getPath(), combinedFolder);
		}
		else
		{
			// if found, then add it to the already existing combinedfolder.
			combinedFolder.addCspFolder(folder);
			folder.addContentListener(combinedFolder);
			folder.addUpdateListener(combinedFolder);
		}

		notifyContentListeners(UpdateType.ADD, combinedFolder);
	}

	/**
	 * Removes the child.
	 *
	 * @param folder
	 *            Folder.
	 */
	public synchronized void removeChild(RemoteFolder<?> folder)
	{
		CombinedFolder combinedFolder = findFolder(folder.getName());

		if (combinedFolder != null)
		{
			combinedFolder.removeCspFolder(folder);

			if (combinedFolder.getCspFolders().size() <= 0)
			{
				subFolders.remove(combinedFolder.getPath());
				notifyContentListeners(UpdateType.REMOVE, combinedFolder);
			}
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

					updateInfo(folder);
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
	public synchronized CombinedFolder findFolder(String name)
	{
		for (CombinedFolder folder : subFolders.values())
		{
			if (folder.getName().equals(name))
			{
				return folder;
			}
		}

		return null;
	}

	/**
	 * @see com.yagasoft.overcast.base.container.content.IContentListener#contentsChanged(com.yagasoft.overcast.base.container.content.ChangeEvent)
	 */
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

	/**
	 * @see com.yagasoft.overcast.base.container.update.IUpdateListener#containerUpdated(com.yagasoft.overcast.base.container.update.UpdateEvent)
	 */
	@Override
	public void containerUpdated(UpdateEvent event)
	{
		updateInfo((RemoteFolder<?>) event.getContainer());
		notifyContentListeners(UpdateType.NAME);
	}

	/**
	 * Update info.
	 *
	 * @param folder Folder.
	 */
	private void updateInfo(RemoteFolder<?> folder)
	{
		setName(folder.getName());
		setPath(folder.getPath());
	}
	
	/**
	 * Adds the content listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addContentListener(ContentListener listener)
	{
		contentListeners.add(listener);
	}

	/**
	 * Removes the content listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeContentListener(ContentListener listener)
	{
		contentListeners.remove(listener);
	}

	/**
	 * Notify content listeners.
	 *
	 * @param update
	 *            Update.
	 */
	public void notifyContentListeners(UpdateType update)
	{
		for (ContentListener listener : contentListeners)
		{
			listener.folderChanged(this, update, null);
		}
	}

	/**
	 * Notify content listeners.
	 *
	 * @param update
	 *            Update.
	 * @param content
	 *            Content.
	 */
	public void notifyContentListeners(UpdateType update, CombinedFolder content)
	{
		for (ContentListener listener : contentListeners)
		{
			listener.folderChanged(this, update, content);
		}
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
		if (name == null)
		{
			name = "root";
		}

		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		if ((path == null) || path.equals("/"))
		{
			setName("root");
			path = "/";
		}

		this.path = path;
	}


	/**
	 * @return the parent
	 */
	public CombinedFolder getParent()
	{
		return parent;
	}


	/**
	 * @param parent the parent to set
	 */
	public void setParent(CombinedFolder parent)
	{
		this.parent = parent;
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
