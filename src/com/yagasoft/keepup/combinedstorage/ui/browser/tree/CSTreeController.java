/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.tree/CSTreeController.java
 *
 *			Modified: 20-Jun-2014 (22:58:33)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.tree;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ContentListener;
import com.yagasoft.keepup.combinedstorage.UpdateType;
import com.yagasoft.keepup.combinedstorage.ui.search.SearchPanel;
import com.yagasoft.keepup.ui.browser.tree.FolderTreeController;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;


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
		// when the search is triggered, call the 'search' method.
		foldersTree.addSearchListener(event -> search());
		// get notified of changes in the root node, this will also be passed on to the root's children automatically.
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

	/**
	 * Initiates the search action using the word inserted in the text field.
	 */
	public void search()
	{
		List<File<?>> result = new ArrayList<File<?>>();
		String searchWord = ((CSTree) view).getSearchText();

		for (Container<?> container : getSelectedFolder().findContainer(searchWord, true, true))
		{
			if ( !container.isFolder())
			{
				result.add((File<?>) container);
			}
		}

		App.showSubWindow(new SearchPanel(result), "Result search for " + searchWord + " in " + getSelectedFolder());
	}

}
