/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/BrowserPanel.java
 *
 *			Modified: 11-Mar-2014 (21:13:09)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;

import com.yagasoft.keepup.App;
import com.yagasoft.overcast.container.File;
import com.yagasoft.overcast.container.Folder;
import com.yagasoft.overcast.container.remote.RemoteFile;


/**
 * The folders tree and files list GUI panels.
 */
public class BrowserPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= 1173503486389973440L;
	
	/** Split pane. */
	private JSplitPane				splitPane;
	
	/** Tree of the folders. */
	private JTree					treeFolders;
	
	/** Scroll pane folders. */
	private JScrollPane				scrollPaneFolders;
	
	/** Root. */
	private DefaultMutableTreeNode	root;
	
	/** Table of the files. */
	private JTable					tableFiles;
	
	/** Scroll pane files. */
	private JScrollPane				scrollPaneFiles;
	
	/** Column names. */
	private String[]				columnNames;
	
	/** Table data. */
	private Object[][]				tableData;
	
	/** Table model. */
	private DefaultTableModel		tableModel;
	
	/**
	 * Create the panel.
	 */
	public BrowserPanel()
	{
		initGUI();
	}
	
	/**
	 * Initialises objects, and forms the split pane.
	 */
	private void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		
		//
		splitPane = new JSplitPane();
		
		// --------------------------------------------------------------------------------------
		// #region RemoteFolder tree.
		
		root = new DefaultMutableTreeNode("root");
		treeFolders = new JTree(root);
		scrollPaneFolders = new JScrollPane(treeFolders);
		
		// a listener to prepare a node for expansion. Also, it must be used to decide if the node needs a '+'.
		treeFolders.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				// get folders inside the expanding folder.
				for (Folder<?> folder : ((Folder<?>) ((DefaultMutableTreeNode) event.getPath().getLastPathComponent())
						.getUserObject()).getFoldersArray())
				{
					folder.buildTree(0);		// update its contents.
				}
				
				updateTree();
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{}
		});
		
		// when folder is selected, list its files.
		treeFolders.addTreeSelectionListener(new TreeSelectionListener()
		{
			
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				File<?>[] files = null;
				
				// if the selection is 'root' then combine files list at 'App', else, load files in the selected folder.
				files = (node.getUserObject() instanceof String) ?
						App.getRootFiles()
						: ((Folder<?>) node.getUserObject()).getFilesArray();
				
				Arrays.sort(files);
				updateTable(files);
			}
		});
		
		splitPane.setLeftComponent(scrollPaneFolders);
		
		// #endregion RemoteFolder tree.
		// --------------------------------------------------------------------------------------
		
		// --------------------------------------------------------------------------------------
		// #region RemoteFile table.
		
		columnNames = new String[] { "Name", "Size", "CSP" };
		tableData = new String[0][3];
		
		// create model and table from model.
		tableModel = new DefaultTableModel(tableData, columnNames);
		tableFiles = new JTable(tableModel);
		scrollPaneFiles = new JScrollPane(tableFiles);
		formatTable();
		
		splitPane.setRightComponent(scrollPaneFiles);
		
		// #endregion RemoteFile table.
		// --------------------------------------------------------------------------------------
		
		add(splitPane, BorderLayout.CENTER);
		
		resetDivider(150);
	}
	
	/**
	 * Resets the position of the divider between the tree and the files' list.
	 * 
	 * @param position
	 *            position of the divider.
	 */
	public void resetDivider(int... position)
	{
		if (position.length == 0)
		{
			splitPane.setDividerLocation(App.mainWindow.getWidth() / 3);
		}
		else
		{
			splitPane.setDividerLocation(position[0]);
		}
	}
	
	/**
	 * Refresh tree.
	 */
	public void updateTree()
	{
		@SuppressWarnings("unchecked") Enumeration<DefaultMutableTreeNode> nodes = root.children();
		
		while (nodes.hasMoreElements())
		{
			fillTreeNode(nodes.nextElement());
		}
	}
	
	/**
	 * Fill the tree using the sent list recursively.
	 * 
	 * @param folderArray
	 *            Folder array.
	 */
	public void updateTree(Folder<?>[] folderArray)
	{
		root = new DefaultMutableTreeNode("root");
		
		for (Folder<?> folder : folderArray)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
			fillTreeNode(node);
			root.add(node);
			
			// treeFolders.expandPath(new TreePath(node));
		}
		
		treeFolders.setModel(new DefaultTreeModel(root));
	}
	
	/**
	 * Fill node with sub-folders and files contained within recursively and adds them to the tree.
	 * 
	 * @param node
	 *            Node to be used.
	 */
	private void fillTreeNode(DefaultMutableTreeNode node)
	{
		node.removeAllChildren();
		
		for (Folder<?> folder : ((Folder<?>) node.getUserObject()).getFoldersArray())
		{
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(folder);
			fillTreeNode(subNode);
			node.add(subNode);
		}
	}
	
	/**
	 * Set how the table behaves visually.
	 */
	private void formatTable()
	{
		// set columns to be right aligned.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		tableFiles.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
		
		// columns can't be selected.
		tableFiles.setColumnSelectionAllowed(false);
		tableFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		adjustColumns(App.mainWindow == null ? 768 : App.mainWindow.getWidth());
	}
	
	/**
	 * Adjust the columns of the table to look best.
	 * 
	 * @param width
	 *            Width of the app frame.
	 */
	public void adjustColumns(int width)
	{
		tableFiles.getColumnModel().getColumn(0).setPreferredWidth(width - 630);
		
		tableFiles.getColumnModel().getColumn(1).setPreferredWidth(40);
		tableFiles.getColumnModel().getColumn(1).setMinWidth(40);
		
		tableFiles.getColumnModel().getColumn(2).setPreferredWidth(100);
		tableFiles.getColumnModel().getColumn(2).setMinWidth(100);
	}
	
	/**
	 * Update table with the files passed.
	 * 
	 * @param fileArray
	 *            File array.
	 */
	public void updateTable(File<?>[] fileArray)
	{
		tableData = new Object[fileArray.length][3];
		
		for (int i = 0; i < fileArray.length; i++)
		{
			tableData[i][0] = fileArray[i];
			tableData[i][1] = humanReadableSize(fileArray[i].getSize());
			tableData[i][2] = fileArray[i].getCsp().getName();
		}
		
		tableFiles.setModel(new DefaultTableModel(tableData, columnNames));
		
		formatTable();
	}
	
	/**
	 * Human readable size conversion.<br/>
	 * <br />
	 * Credit: 'aioobe' at 'StackOverFlow.com'
	 * 
	 * @param bytes
	 *            Size in bytes.
	 * @return Human readable size.
	 */
	private String humanReadableSize(long bytes)
	{
		int unit = 1024;
		
		if (bytes < unit)
		{
			return bytes + " B";
		}
		
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = ("KMGTPE").charAt(exp - 1) + ("i");
		
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	@SuppressWarnings("rawtypes")
	public RemoteFile<?>[] getSelectedFiles()
	{
		// get the data in the table.
		Vector rows = ((DefaultTableModel) tableFiles.getModel()).getDataVector();
		int[] selectedRows = tableFiles.getSelectedRows();
		
		RemoteFile<?>[] files = new RemoteFile<?>[selectedRows.length];
		int index = 0;
		
		for (int rowIndex : selectedRows)
		{
			files[index] = (RemoteFile<?>) ((Vector) rows.get(rowIndex)).get(0);
			index++;
		}
		
		return files;
	}
}
