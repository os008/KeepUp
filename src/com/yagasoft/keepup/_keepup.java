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
	public static final boolean	DEBUG	= true;

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

		Logger.info("KEEPUP: started!");

		App.initApp();
	}

}
