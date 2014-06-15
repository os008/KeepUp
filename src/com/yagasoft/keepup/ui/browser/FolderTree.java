
package com.yagasoft.keepup.ui.browser;


import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup._keepup;


public abstract class FolderTree<FolderType extends Comparable<FolderType>> extends JPanel
{
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Folders tree fields.
	// ======================================================================================
	
	private static final long		serialVersionUID	= 1143822856730946410L;
	
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
	
	public FolderTree(DefaultMutableTreeNode root)
	{
		setLayout(new BorderLayout());
		
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
	@SuppressWarnings("unchecked")
	public FolderType getSelectedFolder()
	{
		// get the selected folder as a path object.
		Object selectedPath = treeFolders.getLastSelectedPathComponent();
		
		// if there's something selected ...
		if (selectedPath != null)
		{
			return (FolderType) ((DefaultMutableTreeNode) selectedPath).getUserObject();
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
				if (((FolderType) childNode.getUserObject()).compareTo((FolderType) children.nextElement()
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
	
}
