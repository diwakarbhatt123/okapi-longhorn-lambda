/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.longhornapi.impl.rest;

import java.io.InputStream;
import java.net.URI;

import net.sf.okapi.lib.longhornapi.LonghornFile;


/**
 * Implementation of {@link LonghornFile} for Longhorn's RESTful interface.
 */
public class RESTFile implements LonghornFile {
	private RESTProject project;
	private Filetype type;
	private String relativePath;
	
	public static enum Filetype {
		input,
		output
	};
	
	protected RESTFile () {
	}
	
	protected RESTFile (RESTProject project, Filetype type, String relativePath) {
		this.project = project;
		this.type = type;
		this.relativePath = relativePath;
	}

	@Override
	public InputStream openStream() {
		try {
			URI remoteFile = new URI(project.getProjectURI() + getPathRelativeToProject());
			return remoteFile.toURL().openStream();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getPathRelativeToProject() {
		switch (type) {
		case input:
			return "/inputFiles/" + relativePath;
		case output:
			return "/outputFiles/" + relativePath;
		default: throw new RuntimeException("Illegal type: " + type);
		}
	}

	@Override
	public InputStream openStreamToZip() {
		
		try {
			final URI remoteFile = new URI(project.getProjectURI() + "/outputFile.zip/" + relativePath + ".zip");
			return remoteFile.toURL().openStream();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getRelativePath() {
		return relativePath;
	}
	
	@Override
	public String toString() {
		return getPathRelativeToProject();
	}

}
