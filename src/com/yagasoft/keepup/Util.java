/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/Util.java
 *
 *			Modified: 25-Jun-2014 (02:54:57)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import com.yagasoft.keepup.backup.State;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.exception.OperationException;


/**
 * The Class Util.
 */
public final class Util
{

	/**
	 * Human readable size conversion.<br/>
	 * <br />
	 * Credit: 'aioobe' at 'StackOverFlow.com'
	 *
	 * @param bytes
	 *            Size in bytes.
	 * @return Human readable size.
	 */
	public static String humanReadableSize(long bytes)
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

	/**
	 * Gets the MD5 corresponding to the passed string.
	 *
	 * <p>
	 * Credit: Javin Paul<br />
	 * (at http://javarevisited.blogspot.com/2013/03/generate-md5-hash-in-java-string-byte-array-example-tutorial.html)
	 * </p>
	 *
	 * @param string
	 *            String.
	 * @return the MD5 in hexadecimal
	 */
	public static String getMD5(String string)
	{
		try
		{
			byte[] bytesOfMessage = string.getBytes("UTF-8");

			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] hash = md5.digest(bytesOfMessage);

			// converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2 * hash.length);

			for (byte b : hash)
			{
				sb.append(String.format("%02x", b & 0xff));
			}

			return sb.toString();
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Calculate hashed name for a file to be uploaded to the server as a revision in the backup system.
	 *
	 * @param container
	 *            Container.
	 * @return hashed name
	 */
	public static String calculateHashedName(Container<?> container)
	{
		try
		{
			// refresh container's meta data (not done in real time)
			container.updateFromSource();
		}
		catch (OperationException e)
		{
			Logger.except(e);
			e.printStackTrace();
		}

		// prepare the path hash to be the start of the filename on the server
		String pathHash = getMD5(container.getPath());
		// form the new filename as the path hash plus the modified date, this will keep revisions of the file.
		return pathHash + container.getDate();
	}

	/**
	 * Form the path to the parent, using a prefix, and removing ':' and '\'
	 * because they are windows based and unsupported.
	 *
	 * @param container
	 *            Container.
	 * @return String
	 */
	public static String formRemoteBackupParent(Container<?> container)
	{
		return "/keepup_backup/" + container.getParent().getPath().replace(":", "").replace("\\", "/");
	}

	/**
	 * Gets the {@link State} from a string for backup system.
	 *
	 * @param stateString
	 *            State string.
	 * @return the state
	 */
	public static State getState(String stateString)
	{
		switch (stateString)
		{
			case "ADD":
				return State.ADD;

			case "MODIFY":
				return State.MODIFY;

			case "DELETE":
				return State.DELETE;

			case "SYNCED":
				return State.SYNCED;

			case "REMOVE_ALL":
				return State.REMOVE_ALL;

			case "REMOVE":
				return State.REMOVE;
		}

		return null;
	}

	/**
	 * Gets the selected files.
	 *
	 * @param files
	 *            Files.
	 * @return the selected files
	 */
	public static List<RemoteFile<?>> convertListToRemote(List<File<?>> files)
	{
		return files.parallelStream()
				.map(file -> (RemoteFile<?>) file)
				.collect(Collectors.toList());
	}

	/**
	 * Convert list.
	 *
	 * @param <newType>
	 *            the generic type
	 * @param list
	 *            List.
	 * @param newClass
	 *            New class.
	 * @return List
	 */
	@SuppressWarnings("unused")
	private static <newType> List<newType> convertList(List<?> list, Class<newType> newClass)
	{
		return list.parallelStream()
				.map(element -> newClass.cast(element))
				.collect(Collectors.toList());
	}

	/**
	 * Instantiates a new util.
	 */
	private Util()
	{}

}
