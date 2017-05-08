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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import net.sf.okapi.lib.longhornapi.LonghornFile;
import net.sf.okapi.lib.longhornapi.LonghornProject;
import net.sf.okapi.lib.longhornapi.impl.rest.RESTFile.Filetype;
import net.sf.okapi.lib.longhornapi.impl.rest.transport.XMLStepConfigOverrideList;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;

/**
 * Implementation of {@link LonghornProject} for Longhorn's RESTful interface.
 */
public class RESTProject implements LonghornProject {
	private URI projUri;

	protected RESTProject() {
	}
	
	protected RESTProject(URI projUri) {
		this.projUri = projUri;
	}
	
	protected RESTProject(URI serviceUri, String projId) {
		String newProjUri = serviceUri.toString();
		if (!newProjUri.endsWith("/"))
			newProjUri += "/";
		newProjUri += "projects/";
		newProjUri += projId;
		
		try {
			this.projUri = new URI(newProjUri);
		}
		catch (URISyntaxException e) {
			// Should not happen, because the URI was used in the Service before
			throw new RuntimeException(e);
		}
	}
	
	protected URI getProjectURI() {
		return projUri;
	}

	@Override
	public void addBatchConfiguration(File bconf) throws FileNotFoundException {
		this.addBatchConfiguration(bconf, null);
	}
	
	@Override
	public void addBatchConfiguration(File bconf, XMLStepConfigOverrideList overrideStepParams) throws FileNotFoundException {
		List<Part> parts = new ArrayList<>(2);	
		parts.add(new FilePart("batchConfiguration", bconf.getName(), bconf));
		
		try {
			if(null!=overrideStepParams) {
				parts.add(new StringPart("overrideStepParams", XMLStepConfigOverrideList.marshal(overrideStepParams)));
			}
			Util.post(projUri + "/batchConfiguration", parts.toArray(new Part[0]));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addInputFile(File inputFile, String relativePath) throws FileNotFoundException {
		String uri = projUri + "/inputFiles/" + relativePath;
		Part[] inputParts = {
				new FilePart("inputFile", inputFile.getName(), inputFile)};
		try {
			Util.put(uri, inputParts);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete() {
		try {
			Util.delete(projUri.toString());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void executePipeline() {
		try {
			Util.post(projUri + "/tasks/execute", null);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void executePipeline(String sourceLanguage, String targetLanguage) {
		if (sourceLanguage == null || targetLanguage == null)
			throw new NullPointerException();
		
		try {
			Util.post(projUri + "/tasks/execute/" + sourceLanguage + "/" + targetLanguage, null);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void executePipeline(final String sourceLanguage,
			final List<String> targetLanguages) throws NullPointerException {
		if (sourceLanguage == null || targetLanguages == null)
			throw new NullPointerException();

		try {
			Util.post(this.getProjectURI() + "/tasks/execute/" + sourceLanguage +
					"?targets=" + StringUtils.join(targetLanguages, "&targets="), null);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public ArrayList<LonghornFile> getInputFiles() {
		try {
			ArrayList<String> filenames = Util.getList(projUri + "/inputFiles");
			ArrayList<LonghornFile> files = new ArrayList<LonghornFile>();
			
			for (String filename : filenames) {
				files.add(new RESTFile(this, Filetype.input, filename));
			}
			
			return files;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArrayList<LonghornFile> getOutputFiles() {
		try {
			ArrayList<String> filenames = Util.getList(projUri + "/outputFiles");
			ArrayList<LonghornFile> files = new ArrayList<LonghornFile>();
			
			for (String filename : filenames) {
				files.add(new RESTFile(this, Filetype.output, filename));
			}
			
			return files;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addInputFilesFromZip(File zipFile) throws FileNotFoundException {
		Part[] parts = {
				new FilePart("inputFile", zipFile.getName(), zipFile)};
		try {
			Util.post(projUri + "/inputFiles.zip", parts);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getOutputFilesAsZip() throws IllegalStateException {
		if (getOutputFiles().isEmpty()) {
			// An empty list of files can't be zipped, so throw an exception
			throw new IllegalStateException("There are no output files available.");
		}
		try {
			URI remoteFile = new URI(projUri + "/outputFiles.zip");
			return remoteFile.toURL().openStream();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return projUri.toString();
	}

}
