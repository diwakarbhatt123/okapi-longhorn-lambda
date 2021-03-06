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
	private String workingDirectory = null;
	private ProjectIdStrategy projIdStrategy = ProjectIdStrategy.Counter;

	@Deprecated
	public Configuration() {
		setDefaultWorkingDirectory();
	}

	@Deprecated
	public Configuration(String workingDir) {
		workingDir = workingDir.replace("\\", File.separator);
		workingDir = workingDir.replace(SLASH, File.separator);
		workingDirectory = workingDir;
	}

	@Deprecated
	public Configuration(InputStream confXml) {
		loadFromFile(confXml);
	}

	/**
	 * Both parameters of the constructor are optional (may be null). If no working directory is specified either in
	 * the first param workingDir or inside the XML file param confXml, a default directory in the user's home directory
	 * will be used.
	 * 
	 * Sample XML configuration file:
	 * <pre>
	 * {@code
	 * <longhorn-config>
	 * 	<use-unique-working-directory>True</use-unique-working-directory>
	 * 	<working-directory>testData/longhorn-files/</working-directory>
	 *  <!-- For allowed project-id-strategy values see - net.sf.okapi.applications.longhorn.lib.ProjectIdStrategy -->
	 * 	<project-id-strategy>Counter</project-id-strategy>
	 * </longhorn-config>
	 * }
	 * </pre>
	 * 
	 * @param workingDir Optional. Path to working directory. Path specified here overrides the working-directory specified in the XML config.
	 * @param confXml Optional. InputStream for configuration XML file.
	 */
	public Configuration(String workingDir, InputStream confXml) {
		loadFromFile(workingDir, confXml);
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

	private void loadFromFile(String workingDir, InputStream confXml) {
		if(workingDir != null) {
			workingDirectory = cleanUpPathAndRemoveLastFileSeperator(workingDir);
		}
		if(confXml != null) {
			//load configuration details from file
			try {
				Document Doc = createDocumentBuilderForFile(confXml);
				if(workingDirectory == null) {
					//read working directory location from file if it's not already specified in system property
					workingDirectory = getWorkingDirectory(Doc);
					workingDirectory = cleanUpPathAndRemoveLastFileSeperator(workingDirectory);
				}
				if (getUseUniqueWorkingDir(Doc)) {
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
		}
		if (workingDirectory == null) {
			setDefaultWorkingDirectory();
		}
	}

	private void setDefaultWorkingDirectory() {
		LOGGER.info("The default working directory for Okapi Longhorn will be used, " +
				"because no other was specified: " + DEF_WORKING_DIR);
		workingDirectory = DEF_WORKING_DIR;
	}

	@Deprecated
	public void loadFromFile(InputStream confXml) {
		if (confXml == null) {
			throw new IllegalArgumentException("Invalid XML stream");
		}
		workingDirectory = null;
		loadFromFile(null, confXml);
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
		return ProjectIdStrategy.Counter;
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
