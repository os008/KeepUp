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


import java.util.Enumeration;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ContentListener;
import com.yagasoft.keepup.combinedstorage.UpdateType;
import com.yagasoft.keepup.ui.browser.FolderTreeController;


/**
 * The Class TreeController.
 */
public class CSTreeController extends FolderTreeController<CombinedFolder> implements ContentListener
{
	
	/**
	 * Instantiates a new tree controller.
	 *
	 * @param foldersTree
	 *            Folders tree.
	 */
	public CSTreeController(CSTree foldersTree)
	{
		super(foldersTree);
		
		((CombinedFolder) root.getUserObject()).addContentListener(this);
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
	
}
