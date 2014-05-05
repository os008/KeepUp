/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/_keepup.java
 *
 *			Modified: 05-May-2014 (01:58:38)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import com.yagasoft.logger.Logger;


/**
 * Entry-point to the program
 */
public class _keepup
{
	/** Enable debug-related logging throughout the program. */
	public static final boolean					DEBUG		= true;

//	static DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
//	static int number = 0;
//	static int index = 0;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the command-line arguments
	 */
	public static void main(String[] args)
	{
		if (DEBUG)
		{
			Logger.showLogger();
		}
		
		Logger.info("Started KeepUp.");

		App.initApp();

//		// test dynamic adding and removing from a GUI tree.
//
//		JFrame frame = new JFrame();
//		frame = new JFrame("KeepUp - Google + Ubuntu");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setBounds(50, 50, 768, 512);
//
//		JPanel panel = new JPanel(new BorderLayout(0, 0));
//		frame.setContentPane(panel);
//
//		final JTree tree = new JTree(root);
//		root.add(new DefaultMutableTreeNode("Test " + (1000 + new Random().nextInt(8999))));
//		panel.add(tree, BorderLayout.CENTER);
//
//		JButton button = new JButton("Add");
//		button.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				DefaultTreeModel treeModel = ((DefaultTreeModel) tree.getModel());
//
//				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode("Test " + (1000 + new Random().nextInt(8999)));
////				root.add(newChild);
//
//				Enumeration<DefaultMutableTreeNode> children = root.children();
//
//				int index = 0;
//
//				while (true)
//				{
//					if (children.hasMoreElements())
//					{
//						if (((String) newChild.getUserObject()).compareToIgnoreCase((String) children.nextElement().getUserObject()) < 0)
//						{
//							treeModel.insertNodeInto(newChild, root, index);
//							break;
//						}
//
//						index++;
//					}
//					else
//					{
//						treeModel.insertNodeInto(newChild, root, index);
//						break;
//					}
//				}
//
////				treeModel.nodeStructureChanged(root/*, new int[] {root.getIndex(newChild)}*/);
//
//				children = root.children();
//				while (children.hasMoreElements())
//				{
//					DefaultMutableTreeNode child = children.nextElement();
//					DefaultMutableTreeNode newChild2 = new DefaultMutableTreeNode("Test " + number++);
//					child.add(newChild2);
////					treeModel.nodesChanged(child, new int[] {child.getIndex(newChild2)});
//				}
//			}
//		});
//		panel.add(button, BorderLayout.SOUTH);
//
//		frame.setVisible(true);
	}

}
