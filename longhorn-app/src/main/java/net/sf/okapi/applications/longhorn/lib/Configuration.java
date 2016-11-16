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

package net.sf.okapi.applications.longhorn.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configuration {

	private static final String DOUBLE_BACKSLASH = "\\\\";
	private static final String DOUBLESLASH = "//";
	private static final String SLASH = "/";
	private static final String VERSION_PROPERTY = "version";
	private static final String WORKING_DIRECTORY = "working-directory";
	private static final String USE_UNIQUE_WORKING_DIRECTORY = "use-unique-working-directory";
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String DEF_WORKING_DIR = System.getProperty("user.home") + File.separator +
			"Okapi-Longhorn-Files";
	private static final String PROJECT_ID_STRATEGY = "project-id-strategy";
	private String versionPropertyFileName = "/version.properties";
	private String workingDirectory;
	private ProjectIdStrategy projIdStrategy = ProjectIdStrategy.UUID;

	public Configuration() {
		LOGGER.info("The default working directory for Okapi Longhorn will be used, " +
				"because no other was specified: " + DEF_WORKING_DIR);
		workingDirectory = DEF_WORKING_DIR;
	}

	public Configuration(String workingDir) {
		workingDir = workingDir.replace("\\", File.separator);
		workingDir = workingDir.replace(SLASH, File.separator);
		workingDirectory = workingDir;
	}

	public Configuration(InputStream confXml) {
		loadFromFile(confXml);
	}

	private String getAPIVersion() {
		String path = getVersionPropertyFileName();
		InputStream stream = getClass().getResourceAsStream(path);
		if (stream == null)
			return null;
		Properties props = new Properties();
		try {
			props.load(stream);
			stream.close();
			return (String) props.get(VERSION_PROPERTY);
		}
		catch (IOException e) {
			return null;
		}
	}

	public void loadFromFile(InputStream confXml) {
		workingDirectory = null;
		try {
			Document Doc = createDocumentBuilderForFile(confXml);
			workingDirectory = getWorkingDirectory(Doc);
			boolean useUniqueWorkingDir = getUseUniqueWorkingDir(Doc);
			if (useUniqueWorkingDir) {
				workingDirectory = cleanUpPathAndRemoveLastFileSeperator(workingDirectory);
				String version = getAPIVersion();
				if (version == null) {
					LOGGER.warn("No version file found. Can't create unique working directoy for longhorn.");
					throw new RuntimeException("UseUniqueWorkingDir is set to true but no version for longhorn found.");
				}
				workingDirectory = workingDirectory + "_M" + version;
			}
			projIdStrategy = getProjectIdStrategy(Doc);
		}
		catch (DOMException e) {
			throw new RuntimeException(e);
		}
		catch (SAXException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		if (workingDirectory == null)
			throw new IllegalArgumentException("Working directory not specified in configuration file");
	}

	private String cleanUpPathAndRemoveLastFileSeperator(String path) {
		path = path.replace(DOUBLE_BACKSLASH, File.separator);
		path = path.replace(DOUBLESLASH, File.separator);
		path = path.replace(SLASH, File.separator);
		if (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	private String getWorkingDirectory(Document Doc) {
		NodeList nodeList = Doc.getElementsByTagName(WORKING_DIRECTORY);
		return readTextContent(nodeList);
	}

	private Document createDocumentBuilderForFile(InputStream confXml) throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
		Fact.setValidating(false);
		Document Doc = Fact.newDocumentBuilder().parse(confXml);
		return Doc;
	}

	private String readTextContent(NodeList nodeList) {
		String textContent = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			textContent = nodeList.item(i).getTextContent();
		}
		return textContent;
	}

	private boolean getUseUniqueWorkingDir(Document Doc) {
		NodeList nodeList = Doc.getElementsByTagName(USE_UNIQUE_WORKING_DIRECTORY);
		String useUniqueWorkingDirString = readTextContent(nodeList);
		boolean useUniqueWorkingDir = Boolean.parseBoolean(useUniqueWorkingDirString);
		return useUniqueWorkingDir;
	}
	
	private ProjectIdStrategy getProjectIdStrategy(Document Doc) {
		NodeList nodeList = Doc.getElementsByTagName(PROJECT_ID_STRATEGY);
		if(nodeList.getLength()>0) {
			try {
				return ProjectIdStrategy.valueOf(readTextContent(nodeList));
			} catch (IllegalArgumentException e) {
				LOGGER.warn("Invalid configuration value for "+PROJECT_ID_STRATEGY+". Allowed values are:"+Arrays.asList(ProjectIdStrategy.values()));
				throw e;
			}
		} 
		return ProjectIdStrategy.UUID;
	}


	public String getWorkingDirectory() {
		return workingDirectory;
	}

	protected String getVersionPropertyFileName() {
		return versionPropertyFileName;
	}

	protected void setVersionPropertyFileName(String versionPropertyFileName) {
		this.versionPropertyFileName = versionPropertyFileName;
	}

	public ProjectIdStrategy getProjectIdStrategy() {
		return projIdStrategy;
	}
}
