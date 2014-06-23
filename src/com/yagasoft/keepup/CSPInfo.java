/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/CSPOptions.java
 *
 *			Modified: 23-Jun-2014 (17:22:06)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;

import com.yagasoft.overcast.base.csp.CSP;




/**
 * The Class CSPOptions.
 */
public class CSPInfo
{

	private String	cspName		= "";

	private String cspPackage = "";
	
	private CSP<?, ?, ?> cspObject = null;

	/** Enabled. */
	private boolean	enabled	= false;

	/** Id. */
	private String	userId		= "";


	public CSPInfo(String name, String packageString)
	{
		this.cspName = name;
		this.cspPackage = packageString;
	}

	/**
	 * @return the csp
	 */
	public String getCspName()
	{
		return cspName;
	}

	/**
	 * @param csp
	 *            the csp to set
	 */
	public void setCspName(String csp)
	{
		this.cspName = csp;
	}


	/**
	 * @return the cspPackage
	 */
	public String getCspPackage()
	{
		return cspPackage;
	}


	/**
	 * @param cspPackage the cspPackage to set
	 */
	public void setCspPackage(String cspPackage)
	{
		this.cspPackage = cspPackage;
	}

	
	/**
	 * @return the cspObject
	 */
	public CSP<?, ?, ?> getCspObject()
	{
		return cspObject;
	}

	
	/**
	 * @param cspObject the cspObject to set
	 */
	public void setCspObject(CSP<?, ?, ?> cspObject)
	{
		this.cspObject = cspObject;
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
	 */
	public void setUserId(String id)
	{
		this.userId = id;
	}
}
