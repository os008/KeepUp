/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			License terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui/BrowserPanel.java
 *
 *			Modified: May 3, 2014 (3:02:20 PM)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ui.actions.FileToolBar;
import com.yagasoft.keepup.combinedstorage.ui.actions.FolderToolBar;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.remote.RemoteFile;


/**
 * The folders tree and files list GUI panels.
 */
public class BrowserPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= 1173503486389973440L;
	
	/** The text field destination. */
	private JTextField				textFieldWorkingFolder;
	
	/** Split pane. */
	private JSplitPane				splitPane;
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Folders tree fields.
	// ======================================================================================
	
	/** Folders tool bar. */
	private FolderToolBar			toolBarFolders;
	
	/** Scroll pane folders. */
	private JScrollPane				scrollPaneFolders;
	
	/** Tree of the folders. */
	private JTree					treeFolders;
	
	/** Root. */
	private DefaultMutableTreeNode	root;
	
	// --------------------------------------------------------------------------------------
	// #region Tree icons.
	
	ImageIcon						openFolder			= new ImageIcon(_keepup.class.getResource("images/open_folder.gif"));
	ImageIcon						closedFolder		= new ImageIcon(_keepup.class.getResource("images/closed_folder.gif"));
	
	// #endregion Tree icons.
	// --------------------------------------------------------------------------------------
	
	// ======================================================================================
	// #endregion Folders tree fields.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files table fields.
	// ======================================================================================
	
	/** Files tool bar. */
	private FileToolBar				toolBarFiles;
	
	/** Scroll pane files. */
	private JScrollPane				scrollPaneFiles;
	
	/** Table of the files. */
	private JTable					tableFiles;
	
	/** Table model. */
	private DefaultTableModel		tableModel;
	
	/** Column names. */
	private String[]				columnNames;
	
	/** Table data. */
	private Object[][]				tableData;
	
	// ======================================================================================
	// #endregion Files table fields.
	// //////////////////////////////////////////////////////////////////////////////////////
	
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
		
		splitPane = new JSplitPane();
		
		// --------------------------------------------------------------------------------------
		// #region Working folder.
		
		JPanel panelWorkingFolder = new JPanel(new BorderLayout());
		
		textFieldWorkingFolder = new JTextField();
		textFieldWorkingFolder.setEditable(false);
//		textFieldDestination.setMinimumSize(new Dimension(50, textFieldDestination.getMinimumSize().height));
//		textFieldDestination.setPreferredSize(new Dimension(200, textFieldDestination.getPreferredSize().height));
//		textFieldDestination.setMaximumSize(new Dimension(200, textFieldDestination.getMaximumSize().height));
		panelWorkingFolder.add(textFieldWorkingFolder, BorderLayout.CENTER);
		
		JButton buttonBrowse = new JButton("Browse");
		buttonBrowse.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				chooseAFolder();
			}
		});
		panelWorkingFolder.add(buttonBrowse, BorderLayout.EAST);
		
		add(panelWorkingFolder, BorderLayout.NORTH);
		
		// #endregion Working folder.
		// --------------------------------------------------------------------------------------
		
		// --------------------------------------------------------------------------------------
		// #region RemoteFolder tree.
		
		JPanel panelFolders = new JPanel(new BorderLayout());
		
		root = App.root.getNode();
		treeFolders = new JTree(root);
		
		// set tree node icons.
		DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
		treeRenderer.setLeafIcon(closedFolder);
		treeRenderer.setClosedIcon(closedFolder);
		treeRenderer.setOpenIcon(openFolder);
		treeFolders.setCellRenderer(treeRenderer);
		
		scrollPaneFolders = new JScrollPane(treeFolders);
		
		// a listener to prepare a node for expansion. Also, it must be used to decide if the node needs a '+'.
		treeFolders.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				if ( !treeFolders.hasBeenExpanded(event.getPath()))
				{
					expandingNode((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
				}
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{}
		});
		
//		// a listener to prepare a node for expansion. Also, it must be used to decide if the node needs a '+'.
//		treeFolders.addTreeExpansionListener(new TreeExpansionListener()
//		{
//			@Override
//			public void treeExpanded(TreeExpansionEvent event)
//			{
//				if (!treeFolders.hasBeenExpanded(event.getPath()))
//				{
//					expandingNode((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
//				}
//			}
//
//			@Override
//			public void treeCollapsed(TreeExpansionEvent event)
//			{}
//		});
		
		// when folder is selected, list its files.
		treeFolders.addTreeSelectionListener(new TreeSelectionListener()
		{
			
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				File<?>[] files = ((CombinedFolder) node.getUserObject()).getFilesArray(false);
				updateTable(files);
			}
		});
		
		panelFolders.add(scrollPaneFolders, BorderLayout.CENTER);
		
		toolBarFolders = new FolderToolBar();
		panelFolders.add(toolBarFolders, BorderLayout.NORTH);
		
		splitPane.setLeftComponent(panelFolders);
		
		// #endregion RemoteFolder tree.
		// --------------------------------------------------------------------------------------
		
		// --------------------------------------------------------------------------------------
		// #region RemoteFile table.
		
		JPanel panelFiles = new JPanel(new BorderLayout());
		
		columnNames = new String[] { "Name", "Size", "CSP" };
		tableData = new String[0][3];
		
		// create model and table from model.
		tableModel = new DefaultTableModel(tableData, columnNames);
		tableFiles = new JTable(tableModel);
		scrollPaneFiles = new JScrollPane(tableFiles);
		formatTable();
		panelFiles.add(scrollPaneFiles, BorderLayout.CENTER);
		
		toolBarFiles = new FileToolBar();
		panelFiles.add(toolBarFiles, BorderLayout.NORTH);
		
		splitPane.setRightComponent(panelFiles);
		
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
	
//	public void expandAll()
//	{
//	  int row = 0;
//	  while (row < treeFolders.getRowCount()) {
//		  treeFolders.expandRow(row);
//		row++;
//	  }
//	}
//
	/**
	 * Pops up a windows to choose a folder, and then updates the chosen folder global var.
	 */
	public void chooseAFolder()
	{
		LocalFolder selectedFolder = Browse.chooseFolder();
		
		// if a folder was chosen ...
		if (selectedFolder != null)
		{
			App.setLastDirectory(selectedFolder.getPath());
		}
	}
	
	/**
	 * Update destination folder in the text field.
	 */
	public void updateDestinationFolder(String path)
	{
		textFieldWorkingFolder.setText(path);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Tree methods.
	// ======================================================================================
	
	/**
	 * Refresh tree.
	 */
	public void updateTree()
	{
		updateNode(root, true);
	}
	
	/**
	 * Refresh node's name.
	 * 
	 * @param node
	 *            the node
	 */
	public void updateNodeName(DefaultMutableTreeNode node)
	{
		((DefaultTreeModel) treeFolders.getModel()).nodeChanged(node);
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
	 * Gets the selected folder.
	 * 
	 * @return the selected folder
	 */
	public CombinedFolder getSelectedFolder()
	{
		// get the selected folder as a path object.
		Object selectedPath = treeFolders.getLastSelectedPathComponent();
		
		// if there's something selected ...
		if (selectedPath != null)
		{
			return (CombinedFolder) ((DefaultMutableTreeNode) selectedPath).getUserObject();
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
		
		if (treeFolders.isExpanded(new TreePath(node.getPath())))
		{
			((CombinedFolder) childNode.getUserObject()).updateCombinedFolder(true);
		}
		
//		treeModel.reload(node);
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
	
	// ======================================================================================
	// #endregion Tree methods.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Table methods.
	// ======================================================================================
	
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
		Arrays.sort(fileArray);
		
		tableData = new Object[fileArray.length][3];
		
		for (int i = 0; i < fileArray.length; i++)
		{
			tableData[i][0] = fileArray[i];
			tableData[i][1] = App.humanReadableSize(fileArray[i].getSize());
			tableData[i][2] = fileArray[i].getCsp().getName();
		}
		
		tableFiles.setModel(new DefaultTableModel(tableData, columnNames));
		tableFiles.revalidate();
		
		formatTable();
	}
	
	/**
	 * Gets the selected files.
	 * 
	 * @return the selected files
	 */
	@SuppressWarnings("rawtypes")
	public RemoteFile<?>[] getSelectedFiles()
	{
		// get the data in the table.
		Vector rows = ((DefaultTableModel) tableFiles.getModel()).getDataVector();
		// get selected rows.
		int[] selectedRows = tableFiles.getSelectedRows();
		
		// files to be returned.
		RemoteFile<?>[] files = new RemoteFile<?>[selectedRows.length];
		int index = 0;
		
		// go through the rows' numbers, fetch them, fetch the file stored there, and put it in the returned list.
		for (int rowIndex : selectedRows)
		{
			files[index] = (RemoteFile<?>) ((Vector) rows.get(rowIndex)).get(0);
			index++;
		}
		
		return files;
	}
	
	// ======================================================================================
	// #endregion Table methods.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================
	
	public JTree getTreeFolders()
	{
		return treeFolders;
	}
	
	public void setTreeFolders(JTree treeFolders)
	{
		this.treeFolders = treeFolders;
	}
	
	public FileToolBar getToolBarFiles()
	{
		return toolBarFiles;
	}
	
	public void setToolBarFiles(FileToolBar toolBarFiles)
	{
		this.toolBarFiles = toolBarFiles;
	}
	
	public JTable getTableFiles()
	{
		return tableFiles;
	}
	
	public void setTableFiles(JTable tableFiles)
	{
		this.tableFiles = tableFiles;
	}
	
	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////
	
}
