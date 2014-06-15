
package com.yagasoft.keepup.ui.browser;


import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


public abstract class FolderTreeController<FolderType extends Comparable<FolderType>> implements TreeWillExpandListener,
		TreeSelectionListener
{
	
	/** Controlled view. */
	protected FolderTree<FolderType>	view;
	
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
	public FolderTreeController(FolderTree<FolderType> foldersTree)
	{
		view = foldersTree;
		tree = foldersTree.getTreeFolders();
		addTreeExpandListener(this);
		
		root = foldersTree.getRoot();
		
		addTreeSelectionListener(this);
	}
	
	/**
	 * Gets the selected folder.
	 *
	 * @return the selected folder
	 */
	public FolderType getSelectedFolder()
	{
		return view.getSelectedFolder();
	}
	
	/**
	 * Adds the tree expand listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addTreeExpandListener(TreeWillExpandListener listener)
	{
		tree.addTreeWillExpandListener(listener);
	}
	
	/**
	 * Removes the tree expand listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeTreeExpandListener(TreeWillExpandListener listener)
	{
		tree.removeTreeWillExpandListener(listener);
	}
	
	public void addTreeSelectionListener(TreeSelectionListener listener)
	{
		tree.addTreeSelectionListener(listener);
	}
	
	public void removeTreeSelectionListener(TreeSelectionListener listener)
	{
		tree.removeTreeSelectionListener(listener);
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath currentPath = e.getPath();
		
		if ( !backStack.isEmpty() && currentPath.equals(backStack.peekFirst()))
		{
			return;
		}
		
		forwardStack.clear();
		backStack.push(currentPath);
	}
	
	public void navigateBackward()
	{
		if (backStack.size() > 1)
		{
			forwardStack.push(backStack.pop());		// get current folder navigated to, and put save it for later.
			
			TreePath path = backStack.peek();		// get last folder navigated to.
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			
			// if the folder was deleted! ...
			if (((node).getParent() == null) && (node.toString() != "root"))
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
			if (((node).getParent() == null) && (node.toString() != "root"))
			{
				navigateForward();		// go forward one more time.
			}
			else
			{
				tree.setSelectionPath(path);
			}
		}
	}
	
}
