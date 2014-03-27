/* Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 *			License terms are at the end of this file
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/_keepup.java
 *			 Version: 0.0.1
 *
 *			 Created: 9 Mar 2014 (16:06:02)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 7 x64
 *
 *		 Description:
 *
 */

package com.yagasoft.keepup;


import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.exception.CSPBuildException;


/**
 * Entry-point to the program
 */
public class _keepup
{
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the command-line arguments
	 */
	public static void main(String[] args)
	{
		Logger.post("Started!");
		
		try
		{
			App.initApp();
		}
		catch (CSPBuildException e)
		{
			e.printStackTrace();
			return;
		}
		
		App.initTree();
	}
	
}

/**
 * @author Ahmed
 * 
 */
/*
 * License terms:
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Except as contained in this notice, the name(s) of the above copyright
 * holders shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorisation.
 * 
 * The end-user documentation included with the redistribution, if any, must
 * include the following acknowledgement: "This product includes software developed
 * by Ahmed Osama el-Sawalhy (http://yagasoft.com) and his contributors", in
 * the same place and form as other third-party acknowledgements. Alternately, this
 * Acknowledgement may appear in the software itself, in the same form and location
 * as other such third-party acknowledgements.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
