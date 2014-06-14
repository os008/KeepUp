/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui/AddRemoveListener.java
 *
 *			Modified: 12-Jun-2014 (23:20:33)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui;


import java.util.List;

import com.yagasoft.keepup.backup.watcher.State;
import com.yagasoft.overcast.base.container.Container;


/**
 * The listener interface for receiving addRemove events.
 * The class that is interested in processing a addRemove
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addAddRemoveListener<code> method. When
 * the addRemove event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AddRemoveEvent
 */
public interface IAddRemoveListener
{
	
	/**
	 * Containers added or removed from the backup list.
	 *
	 * @param containers
	 *            Containers.
	 * @param state
	 *            State.
	 */
	void containersAddedRemoved(List<? extends Container<?>> containers, State state);
}
