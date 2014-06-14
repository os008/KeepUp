
package com.yagasoft.keepup.backup.ui.browser;


/*
 Swing, Second Edition
 by Matthew Robinson, Pavel Vorobiev
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class LocalTree extends JPanel
{
	
	private static final long				serialVersionUID	= -8897046346378540458L;
	public static final ImageIcon			ICON_COMPUTER		= new ImageIcon("computer.gif");
	public static final ImageIcon			ICON_DISK			= new ImageIcon("disk.gif");
	public static final ImageIcon			ICON_FOLDER			= new ImageIcon("folder.gif");
	public static final ImageIcon			ICON_EXPANDEDFOLDER	= new ImageIcon("expandedfolder.gif");
	
	protected JTree							m_tree;
	protected DefaultTreeModel				m_model;
	protected JTextField					m_display;
	
	/** Tree node selection listeners. */
	protected Set<ITreeSelectionListener>	selectionListeners	= new HashSet<ITreeSelectionListener>();
	
	public LocalTree()
	{
		super(new BorderLayout());
		setSize(400, 300);
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new IconData(ICON_COMPUTER, null, "Computer"));
		
		DefaultMutableTreeNode node;
		File[] roots = File.listRoots();
		
		for (File root : roots)
		{
			node = new DefaultMutableTreeNode(new IconData(ICON_DISK, null, new FileNode(root)));
			top.add(node);
			node.add(new DefaultMutableTreeNode(new Boolean(true)));
		}
		
		m_model = new DefaultTreeModel(top);
		m_tree = new JTree(m_model);
		
		m_tree.putClientProperty("JTree.lineStyle", "Angled");
		
		TreeCellRenderer renderer = new IconCellRenderer();
		m_tree.setCellRenderer(renderer);
		m_tree.addTreeExpansionListener(new DirExpansionListener());
		m_tree.addTreeSelectionListener(new DirSelectionListener());
		m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		m_tree.setShowsRootHandles(true);
		m_tree.setEditable(false);
		
		JScrollPane s = new JScrollPane();
		s.getViewport().add(m_tree);
		add(s, BorderLayout.CENTER);
		
		m_display = new JTextField();
		m_display.setEditable(false);
		add(m_display, BorderLayout.NORTH);
	}
	
	DefaultMutableTreeNode getTreeNode(TreePath path)
	{
		return (DefaultMutableTreeNode) (path.getLastPathComponent());
	}
	
	FileNode getFileNode(DefaultMutableTreeNode node)
	{
		if (node == null)
		{
			return null;
		}
		
		Object obj = node.getUserObject();
		
		if (obj instanceof IconData)
		{
			obj = ((IconData) obj).getObject();
		}
		
		if (obj instanceof FileNode)
		{
			return (FileNode) obj;
		}
		else
		{
			return null;
		}
	}
	
	public void addSelectionListener(ITreeSelectionListener listener)
	{
		selectionListeners.add(listener);
	}
	
	public void notifySelectionListeners(String path)
	{
		selectionListeners.parallelStream().forEach(listener -> listener.localTreeSelectionChanged(path));
	}
	
	public void removeSelectionListener(ITreeSelectionListener listener)
	{
		selectionListeners.remove(listener);
	}
	
	public void clearSelectionListeners()
	{
		selectionListeners.clear();
	}
	
	// Make sure expansion is threaded and updating the tree model
	// only occurs within the event dispatching thread.
	class DirExpansionListener implements TreeExpansionListener
	{
		
		@Override
		public void treeExpanded(TreeExpansionEvent event)
		{
			final DefaultMutableTreeNode node = getTreeNode(event.getPath());
			final FileNode fnode = getFileNode(node);
			
			Thread runner = new Thread()
			{
				
				@Override
				public void run()
				{
					if ((fnode != null) && fnode.expand(node))
					{
						Runnable runnable = () -> m_model.reload(node);
						SwingUtilities.invokeLater(runnable);
					}
				}
			};
			runner.start();
		}
		
		@Override
		public void treeCollapsed(TreeExpansionEvent event)
		{}
	}
	
	class DirSelectionListener implements TreeSelectionListener
	{
		
		@Override
		public void valueChanged(TreeSelectionEvent event)
		{
			DefaultMutableTreeNode node = getTreeNode(event.getPath());
			FileNode fnode = getFileNode(node);
			String path = "";
			
			if (fnode != null)
			{
				path = fnode.getFile().getAbsolutePath();
			}
			
			m_display.setText(path);
			notifySelectionListeners(path);
		}
	}
	
	public static void main(String argv[])
	{
		JFrame frame = new JFrame();
		frame.add(new LocalTree());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(400, 512);
	}
}


class IconCellRenderer
		extends JLabel
		implements TreeCellRenderer
{
	
	private static final long	serialVersionUID	= 6860672454817752545L;
	protected Color				m_textSelectionColor;
	protected Color				m_textNonSelectionColor;
	protected Color				m_bkSelectionColor;
	protected Color				m_bkNonSelectionColor;
	protected Color				m_borderSelectionColor;
	
	protected boolean			m_selected;
	
	public IconCellRenderer()
	{
		super();
		m_textSelectionColor = UIManager.getColor(
				"Tree.selectionForeground");
		m_textNonSelectionColor = UIManager.getColor(
				"Tree.textForeground");
		m_bkSelectionColor = UIManager.getColor(
				"Tree.selectionBackground");
		m_bkNonSelectionColor = UIManager.getColor(
				"Tree.textBackground");
		m_borderSelectionColor = UIManager.getColor(
				"Tree.selectionBorderColor");
		setOpaque(false);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus)
	
	{
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		setText(obj.toString());
		
		if (obj instanceof Boolean)
		{
			setText("Retrieving data...");
		}
		
		if (obj instanceof IconData)
		{
			IconData idata = (IconData) obj;
			if (expanded)
			{
				setIcon(idata.getExpandedIcon());
			}
			else
			{
				setIcon(idata.getIcon());
			}
		}
		else
		{
			setIcon(null);
		}
		
		setFont(tree.getFont());
		setForeground(sel ? m_textSelectionColor :
				m_textNonSelectionColor);
		setBackground(sel ? m_bkSelectionColor :
				m_bkNonSelectionColor);
		m_selected = sel;
		return this;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Color bColor = getBackground();
		Icon icon = getIcon();
		
		g.setColor(bColor);
		int offset = 0;
		if ((icon != null) && (getText() != null))
		{
			offset = (icon.getIconWidth() + getIconTextGap());
		}
		g.fillRect(offset, 0, getWidth() - 1 - offset,
				getHeight() - 1);
		
		if (m_selected)
		{
			g.setColor(m_borderSelectionColor);
			g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		}
		super.paintComponent(g);
	}
}


class IconData
{
	
	protected Icon		m_icon;
	protected Icon		m_expandedIcon;
	protected Object	m_data;
	
	public IconData(Icon icon, Object data)
	{
		m_icon = icon;
		m_expandedIcon = null;
		m_data = data;
	}
	
	public IconData(Icon icon, Icon expandedIcon, Object data)
	{
		m_icon = icon;
		m_expandedIcon = expandedIcon;
		m_data = data;
	}
	
	public Icon getIcon()
	{
		return m_icon;
	}
	
	public Icon getExpandedIcon()
	{
		return m_expandedIcon != null ? m_expandedIcon : m_icon;
	}
	
	public Object getObject()
	{
		return m_data;
	}
	
	@Override
	public String toString()
	{
		return m_data.toString();
	}
}


class FileNode
{
	
	protected File	m_file;
	
	public FileNode(File file)
	{
		m_file = file;
	}
	
	public File getFile()
	{
		return m_file;
	}
	
	@Override
	public String toString()
	{
		return m_file.getName().length() > 0 ? m_file.getName() :
				m_file.getPath();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean expand(DefaultMutableTreeNode parent)
	{
		DefaultMutableTreeNode flag =
				(DefaultMutableTreeNode) parent.getFirstChild();
		if (flag == null)
		{
			return false;
		}
		Object obj = flag.getUserObject();
		if ( !(obj instanceof Boolean))
		{
			return false;      // Already expanded
		}
		
		parent.removeAllChildren();  // Remove Flag
		
		File[] files = listFiles();
		if (files == null)
		{
			return true;
		}
		
		Vector v = new Vector();
		
		for (File file : files)
		{
			File f = file;
			if ( !(f.isDirectory()))
			{
				continue;
			}
			
			FileNode newNode = new FileNode(f);
			
			boolean isAdded = false;
			for (int i = 0; i < v.size(); i++)
			{
				FileNode nd = (FileNode) v.elementAt(i);
				if (newNode.compareTo(nd) < 0)
				{
					v.insertElementAt(newNode, i);
					isAdded = true;
					break;
				}
			}
			if ( !isAdded)
			{
				v.addElement(newNode);
			}
		}
		
		for (int i = 0; i < v.size(); i++)
		{
			FileNode nd = (FileNode) v.elementAt(i);
			IconData idata = new IconData(LocalTree.ICON_FOLDER,
					LocalTree.ICON_EXPANDEDFOLDER, nd);
			DefaultMutableTreeNode node = new
					DefaultMutableTreeNode(idata);
			parent.add(node);
			
			if (nd.hasSubDirs())
			{
				node.add(new DefaultMutableTreeNode(
						new Boolean(true)));
			}
		}
		
		return true;
	}
	
	public boolean hasSubDirs()
	{
		File[] files = listFiles();
		if (files == null)
		{
			return false;
		}
		for (File file : files)
		{
			if (file.isDirectory())
			{
				return true;
			}
		}
		return false;
	}
	
	public int compareTo(FileNode toCompare)
	{
		return m_file.getName().compareToIgnoreCase(
				toCompare.m_file.getName());
	}
	
	protected File[] listFiles()
	{
		if ( !m_file.isDirectory())
		{
			return null;
		}
		try
		{
			return m_file.listFiles();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null,
					"Error reading directory " + m_file.getAbsolutePath(),
					"Warning", JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}
}
