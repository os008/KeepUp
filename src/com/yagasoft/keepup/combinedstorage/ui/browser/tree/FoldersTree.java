/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.tree/FoldersTree.java
 *
 *			Modified: 07-May-2014 (16:15:01)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.tree;


import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ui.actions.FolderToolBar;


/**
 * The Class FoldersTree.
 */
public class FoldersTree extends JPanel implements TreeSelectionListener
{

	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= -427791932709838315L;

	/** Folders tool bar. */
	private FolderToolBar			toolBarFolders;

	/** Selected node. */
	private SelectedNode			selectedNode;

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Folders tree fields.
	// ======================================================================================

	/** Scroll pane folders. */
	private JScrollPane				scrollPaneFolders;

	/** Tree of the folders. */
	private JTree					treeFolders;

	/** Root. */
	private DefaultMutableTreeNode	root;

	// --------------------------------------------------------------------------------------
	// #region Tree icons.

	/** Open folder. */
	ImageIcon						openFolder			= new ImageIcon(_keepup.class.getResource("images/open_folder.gif"));

	/** Closed folder. */
	ImageIcon						closedFolder		= new ImageIcon(_keepup.class.getResource("images/closed_folder.gif"));

	// #endregion Tree icons.
	// --------------------------------------------------------------------------------------

	// ======================================================================================
	// #endregion Folders tree fields.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new folders tree.
	 *
	 * @param root
	 *            Root.
	 */
	public FoldersTree(DefaultMutableTreeNode root)
	{
		setLayout(new BorderLayout());

		toolBarFolders = new FolderToolBar();
		add(toolBarFolders, BorderLayout.NORTH);

		this.root = root;
		treeFolders = new JTree(root);

		// set tree node icons.
		DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
		treeRenderer.setLeafIcon(closedFolder);
		treeRenderer.setClosedIcon(closedFolder);
		treeRenderer.setOpenIcon(openFolder);
		treeFolders.setCellRenderer(treeRenderer);

		scrollPaneFolders = new JScrollPane(treeFolders);
		add(scrollPaneFolders, BorderLayout.CENTER);

		treeFolders.addTreeSelectionListener(this);
		selectedNode = new SelectedNode(root.getUserObject());
	}

	/**
	 * Expand path to node.
	 *
	 * @param node
	 *            Node.
	 */
	public void expandPathToNode(DefaultMutableTreeNode node)
	{
		treeFolders.expandPath(new TreePath(node.getPath()));
	}

	/**
	 * Refresh node's name. Should be used when the object stored at a node changes.
	 *
	 * @param node
	 *            the node
	 */
	public void updateNodeName(DefaultMutableTreeNode node)
	{
		((DefaultTreeModel) treeFolders.getModel()).nodeChanged(node);
	}

	/**
	 * Gets the selected folder.
	 *
	 * @return the selected folder
	 */
	public Object getSelectedFolder()
	{
		// get the selected folder as a path object.
		Object selectedPath = treeFolders.getLastSelectedPathComponent();

		// if there's something selected ...
		if (selectedPath != null)
		{
			return ((DefaultMutableTreeNode) selectedPath).getUserObject();
		}

		// nothing is selected.
		return null;
	}

	/**
	 * Adds the node to tree.
	 *
	 * @param childNode
	 *            the child node
	 * @param node
	 *            the node
	 */
	@SuppressWarnings("unchecked")
	public void addNodeToTree(DefaultMutableTreeNode childNode, DefaultMutableTreeNode node)
	{
		DefaultTreeModel treeModel = ((DefaultTreeModel) treeFolders.getModel());

		Enumeration<DefaultMutableTreeNode> children = node.children();

		int index = 0;

		// insert node in a sorted order.
		while (true && !node.isNodeChild(childNode))
		{
			if (children.hasMoreElements())
			{
				// check nodes names to figure out where to put the new one.
				if (((CombinedFolder) childNode.getUserObject()).compareTo((CombinedFolder) children.nextElement()
						.getUserObject()) < 0)
				{
					treeModel.insertNodeInto(childNode, node, index);
				}

				index++;
			}
			else
			{
				treeModel.insertNodeInto(childNode, node, index);
			}
		}
	}

	/**
	 * Removes the node from the tree.
	 *
	 * @param childNode
	 *            Child node to remove.
	 */
	public void removeNodeFromTree(DefaultMutableTreeNode childNode)
	{
		DefaultTreeModel treeModel = ((DefaultTreeModel) treeFolders.getModel());
		treeModel.removeNodeFromParent(childNode);
	}

	/**
	 * Adds the tree expand listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addTreeExpandListener(TreeWillExpandListener listener)
	{
		treeFolders.addTreeWillExpandListener(listener);
	}

	/**
	 * Removes the tree expand listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeTreeExpandListener(TreeWillExpandListener listener)
	{
		treeFolders.removeTreeWillExpandListener(listener);
	}

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		// When a new folder is selected, change the object in the SelectedNode.
		selectedNode.setSelectedFolder(getSelectedFolder());
	}

	/**
	 * Adds the tree selection observer.
	 *
	 * @param observer
	 *            Observer.
	 */
	public void addTreeSelectionObserver(Observer observer)
	{
		selectedNode.addObserver(observer);
	}

	/**
	 * Removes the tree selection observer.
	 *
	 * @param observer
	 *            Observer.
	 */
	public void removeTreeSelectionObserver(Observer observer)
	{
		selectedNode.deleteObserver(observer);
	}

	/**
	 * Gets the tree folders.
	 *
	 * @return the tree folders
	 */
	public JTree getTreeFolders()
	{
		return treeFolders;
	}

	/**
	 * Sets the tree folders.
	 *
	 * @param treeFolders
	 *            the new tree folders
	 */
	public void setTreeFolders(JTree treeFolders)
	{
		this.treeFolders = treeFolders;
	}

	/**
	 * @return the root
	 */
	public DefaultMutableTreeNode getRoot()
	{
		return root;
	}

	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(DefaultMutableTreeNode root)
	{
		this.root = root;
	}

	/**
	 * A class SelectedNode which is used to inform observers if the selected node in the folders tree has changed.<br />
	 * Probably will be used to update the table view of the currently selected folder.
	 */
	private class SelectedNode extends Observable
	{

		/** Selected folder. */
		protected Object	selectedFolder;

		/**
		 * Instantiates a new selected node.
		 *
		 * @param selectedFolder
		 *            Selected folder.
		 */
		public SelectedNode(Object selectedFolder)
		{
			this.selectedFolder = selectedFolder;
		}

		/**
		 * Gets the selected folder.
		 *
		 * @return the selectedFolder
		 */
		@SuppressWarnings("unused")
		public Object getSelectedFolder()
		{
			return selectedFolder;
		}

		/**
		 * Sets the selected folder.
		 *
		 * @param selectedFolder
		 *            the selectedFolder to set
		 */
		public void setSelectedFolder(Object selectedFolder)
		{
			this.selectedFolder = selectedFolder;
			setChanged();
			notifyObservers(selectedFolder);
		}

	}
}
