/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.watcher/Change.java
 * 
 *			Modified: 13-Jun-2014 (16:50:40)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.watcher;


/**
 * The Enum Change.
 */
public enum State
{
	
	/** Add. */
	ADD,
	
	MODIFY,
	
	DELETE,
	
	/** Remove. */
	REMOVE,
	
	/** Remove all. */
	REMOVE_ALL,
	
	/** Synced. */
	SYNCED
}
