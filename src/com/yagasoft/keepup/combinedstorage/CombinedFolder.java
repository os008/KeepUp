/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/CombinedFolder.java
 * 
 *			Modified: 23-Jun-2014 (20:21:31)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.operation.IOperationListener;
import com.yagasoft.overcast.base.container.operation.Operation;
import com.yagasoft.overcast.base.container.operation.OperationEvent;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.exception.AccessException;
import com.yagasoft.overcast.exception.OperationException;


/**
 * The Class CombinedFolder.
 */
public class CombinedFolder implements Comparable<CombinedFolder>, IOperationListener
{

	/** Folder name. */
	private String								name;

	/** Path of the folder. */
	private String								path;

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

	protected void registerForOperations(Container<?> container)
	{
		registerForOperations(container, this);
	}

	protected void registerForOperations(Container<?> container, CombinedFolder observer)
	{
		container.addOperationListener(observer, Operation.ADD);			// used for when the folder content changes.
		container.addOperationListener(observer, Operation.REMOVE);		// used for when the folder content changes.
		container.addOperationListener(observer, Operation.UPDATE);		// used for when the name changes.
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
		registerForOperations(folder);
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
			subFolders.put(combinedFolder.getPath(), combinedFolder);
		}
		else
		{
			// if found, then add it to the already existing combinedfolder.
			combinedFolder.addCspFolder(folder);
		}

		combinedFolder.setParent(this);

		registerForOperations(folder, combinedFolder);

		notifyContentListeners(UpdateType.ADD, combinedFolder);
	}

	/**
	 * Removes a child.
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

			if (combinedFolder.getCspFolders().isEmpty())
			{
				subFolders.remove(combinedFolder.getPath());
				combinedFolder.setParent(null);
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
			new Thread(() ->
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
			}).start();
		}
	}

	/**
	 * Returns a list containing all children in the folders with the same name,
	 * from all the CSPs. This list should be used only in tables,
	 * as the children are a mix of completely different types.
	 *
	 * @param update
	 *            Update online?
	 * @return the childrens array
	 */
	public List<Object> getChildrenList(boolean update)
	{
		List<Object> children = new ArrayList<Object>();
		children.addAll(getFoldersList(update));
		children.addAll(getFilesList(update));

		return children;
	}

	/**
	 * Returns a list containing folders in the folders with the same name,
	 * from all the CSPs.
	 *
	 * @param update
	 *            Update online?
	 * @return the folders list
	 */
	public List<CombinedFolder> getFoldersList(boolean update)
	{
		// go through the folders' list and update them if the flag is passed.
		if (update)
		{
			cspFolders.values().parallelStream()
					.forEach(folder ->
					{
						try
						{
							folder.updateFromSource(true, false);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					});
		}

		// return the subfolders as a list.
		return new ArrayList<CombinedFolder>(subFolders.values());
	}

	/**
	 * Returns a list containing files in the folders with the same name,
	 * from all the CSPs.
	 *
	 * @param update
	 *            Update online?
	 * @return the files list
	 */
	public List<RemoteFile<?>> getFilesList(boolean update)
	{
		// go through the folders' list.
		return cspFolders.values().parallelStream()
				.flatMap(folder ->
				{
					try
					{
						if (update)
						{
							folder.updateFromSource(true, false);
						}
						// continue the pipeline using the file list in this folder
						return folder.getFilesList().stream();
					}
					catch (OperationException e)
					{
						e.printStackTrace();
						// a problem, so return an empty stream to continue
						return Stream.empty();
					}
				})
				.map(file -> (RemoteFile<?>) file)		// convert the file type
				.sorted()		// sort
				.collect(Collectors.toList());		// return the array of files

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
	 * Finds the {@link CombinedFolder} sub-folder with the same name as the one passed.
	 *
	 * @param name
	 *            Name to search for.
	 * @return Combined folder found, or null if not
	 */
	public synchronized CombinedFolder findFolder(String name)
	{
		// get the subfolder that match this name
		return subFolders.values().parallelStream()
				.filter(folder -> folder.getName().equals(name))	// keep only matches
				.findFirst()
				.orElse(null);
	}

	/**
	 * Finds containers by searching in this combined folder for the name.
	 * This will search in all CSPs starting at the same path.
	 *
	 * @param name
	 *            Name.
	 * @param recursive
	 *            Recursive.
	 * @return Container list
	 */
	public synchronized List<Container<?>> findContainer(String name, boolean partial, boolean recursive)
	{
		return cspFolders.values().parallelStream()
				// replace each folder with a stream containing its children that match the name
				.flatMap(folder -> folder.searchByName(name, partial, recursive).parallelStream())
				.collect(Collectors.toList());
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void operationChange(OperationEvent event)
	{
		switch (event.getOperation())
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

			case UPDATE:
				updateInfo((RemoteFolder<?>) event.getContainer());
				notifyContentListeners(UpdateType.NAME);
				break;
		}
	}

	/**
	 * Update info.
	 *
	 * @param folder
	 *            Folder.
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
		contentListeners.parallelStream()
				.forEach(listener -> listener.folderChanged(this, update, null));
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
		contentListeners.parallelStream()
				.forEach(listener -> listener.folderChanged(this, update, content));
	}

	/**
	 * Clear csp folders.
	 */
	public void clearCspFolders()
	{
		for (CombinedFolder folder : subFolders.values())
		{
			notifyContentListeners(UpdateType.REMOVE, folder);
		}
		
		cspFolders = new HashMap<String, RemoteFolder<?>>();
		subFolders = new HashMap<String, CombinedFolder>();
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
	 * @param parent
	 *            the parent to set
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
