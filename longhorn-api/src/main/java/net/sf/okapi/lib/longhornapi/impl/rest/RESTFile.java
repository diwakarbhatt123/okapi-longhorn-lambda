/*===========================================================================
  Copyright (C) 2011-2017 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
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
