/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.tree/CSTreeController.java
 *
 *			Modified: 28-May-2014 (16:08:33)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.tree;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ContentListener;
import com.yagasoft.keepup.combinedstorage.UpdateType;


/**
 * The Class TreeController.
 */
public class CSTreeController implements TreeWillExpandListener, ContentListener, Observer
{

	/** Controlled view. */
	protected CSTree					view;

	/** Tree. */
	protected JTree						tree;

	/** Root. */
	protected DefaultMutableTreeNode	root;

	protected Deque<TreePath>			backStack		= new ArrayDeque<TreePath>();
	protected Deque<TreePath>			forwardStack	= new ArrayDeque<TreePath>();

	/**
	 * Instantiates a new tree controller.
	 *
	 * @param foldersTree
	 *            Folders tree.
	 */
	public CSTreeController(CSTree foldersTree)
	{
		view = foldersTree;
		tree = foldersTree.getTreeFolders();
		view.addTreeExpandListener(this);

		root = foldersTree.getRoot();
		((CombinedFolder) root.getUserObject()).addContentListener(this);

		view.addTreeSelectionObserver(this);
	}

	/**
	 * Gets the selected folder.
	 *
	 * @return the selected folder
	 */
	public CombinedFolder getSelectedFolder()
	{
		// get the selected folder.
		Object selectedNode = view.getSelectedFolder();

		// if there's something selected, cast and return it.
		return (selectedNode == null) ? null : (CombinedFolder) selectedNode;
	}

	/**
	 * Adds the node to tree.
	 *
	 * @param childNode
	 *            the child node
	 * @param node
	 *            the node
	 */
	public void addNodeToTree(DefaultMutableTreeNode childNode, DefaultMutableTreeNode node)
	{
		view.addNodeToTree(childNode, node);

		if (tree.isExpanded(new TreePath(node.getPath())))
		{
			((CombinedFolder) childNode.getUserObject()).updateCombinedFolder(true);
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
		view.removeNodeFromTree(childNode);
	}

	/**
	 * Refresh tree.
	 */
	public void updateTree()
	{
		updateNode(root, true);
	}

	/**
	 * Refresh node non-recursively.
	 *
	 * @param node
	 *            the node
	 */
	public void updateNode(DefaultMutableTreeNode node)
	{
		updateNode(node, false);
	}

	/**
	 * Refresh node passed. If recursive refreshes the children nodes.
	 * This differs from {@link #loadNode(DefaultMutableTreeNode, boolean)} in that it doesn't go through new nodes, just the
	 * existing ones.
	 *
	 * @param node
	 *            the node
	 * @param recursively
	 *            recursively?
	 */
	@SuppressWarnings("unchecked")
	public void updateNode(DefaultMutableTreeNode node, boolean recursively)
	{
		if (recursively)
		{
			Enumeration<DefaultMutableTreeNode> nodes = node.children();

			while (nodes.hasMoreElements())
			{
				DefaultMutableTreeNode childNode = nodes.nextElement();
				updateNode(childNode, recursively);
			}
		}

		((CombinedFolder) node.getUserObject()).updateCombinedFolder(true);
	}

	/**
	 * Loads the whole tree.<br />
	 * WARNING: might take a VERY long time.
	 */
	public void loadTree()
	{
		loadNode(root, true);
	}

	/**
	 * Loads node non-recursively.
	 *
	 * @param node
	 *            the node
	 */
	public void loadNode(DefaultMutableTreeNode node)
	{
		loadNode(node, false);
	}

	/**
	 * Loads the children of passed node only. If recursive, then loads the ones under (beware of time).
	 *
	 * @param node
	 *            the node
	 * @param recursively
	 *            recursively?
	 */
	@SuppressWarnings("unchecked")
	public void loadNode(DefaultMutableTreeNode node, boolean recursively)
	{
		((CombinedFolder) node.getUserObject()).updateCombinedFolder(true);

		if (recursively)
		{
			Enumeration<DefaultMutableTreeNode> nodes = node.children();

			while (nodes.hasMoreElements())
			{
				((CombinedFolder) nodes.nextElement().getUserObject()).updateCombinedFolder(true);
			}
		}
	}

	/**
	 * A listener to prepare a node for expansion. Also, it must be used to decide if the node needs a '+'.<br />
	 * <br />
	 *
	 * @param event
	 *            Event.
	 * @throws ExpandVetoException
	 *             the expand veto exception
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
	{
		if ( !tree.hasBeenExpanded(event.getPath()))
		{
			expandingNode((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
		}
	}

	/**
	 * Expanding node action. Loads the children of the expanding node to show the expansion indicator next to them if possible
	 * and if they have children.
	 *
	 * @param node
	 *            the node
	 */
	@SuppressWarnings("unchecked")
	private void expandingNode(DefaultMutableTreeNode node)
	{
		Enumeration<DefaultMutableTreeNode> nodes = node.children();

		while (nodes.hasMoreElements())
		{
			((CombinedFolder) nodes.nextElement().getUserObject()).updateCombinedFolder(true);
		}
	}

	/**
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
	{}

	/**
	 * @see com.yagasoft.keepup.combinedstorage.ContentListener#folderChanged(com.yagasoft.keepup.combinedstorage.CombinedFolder,
	 *      com.yagasoft.keepup.combinedstorage.UpdateType, com.yagasoft.keepup.combinedstorage.CombinedFolder)
	 */
	@Override
	public void folderChanged(CombinedFolder folder, UpdateType update, CombinedFolder content)
	{
		switch (update)
		{
			case ADD:
				view.addNodeToTree(content.getNode(), folder.getNode());
				content.addContentListener(this);
				break;

			case MODIFY:
				break;

			case NAME:
				view.updateNodeName(folder.getNode());
				break;

			case REMOVE:
				content.removeContentListener(this);
				view.removeNodeFromTree(content.getNode());
				break;

		}
	}

	public void navigateBackward()
	{
		if (backStack.size() > 1)
		{
			forwardStack.push(backStack.pop());		// get current folder navigated to, and put save it for later.

			TreePath path = backStack.peek();		// get last folder navigated to.

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			// if the folder was deleted! ...
			if ((node).getParent() == null && node.toString() != "root")
			{
				navigateBackward();		// go back one more time.
			}
			else
			{
				tree.setSelectionPath(path);		// select this folder from history.
			}
		}
	}

	public void navigateForward()
	{
		if ( !forwardStack.isEmpty())
		{
			backStack.push(forwardStack.pop());		// put the forward folder as if it's been selected.
			TreePath path = backStack.peek();		// read it again.

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			// if the folder was deleted! ...
			if ((node).getParent() == null && node.toString() != "root")
			{
				navigateForward();		// go forward one more time.
			}
			else
			{
				tree.setSelectionPath(path);
			}
		}
	}

	/**
	 * Monitors the view for changes in folder selection.
	 *
	 * @param o
	 *            O.
	 * @param arg
	 *            Arg.
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		TreePath currentPath = new TreePath(((CombinedFolder) arg).getNode().getPath());

		if ( !backStack.isEmpty() && currentPath.equals(backStack.peekFirst()))
		{
			return;
		}

		forwardStack.clear();
		backStack.push(currentPath);
	}

}
