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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class ConfigurationTest {

	private static final String OKAPI_LONGHORN_CONFIGURATION_USE_UNIQUE_WORKING_DIR = "/okapi-longhorn-configuration-unique-working-dir-true.xml";
	private static final String OKAPI_LONGHORN_CONFIGURATION = "/okapi-longhorn-configuration.xml";
	private static final String OKAPI_LONGHORN_CONFIGURATION_INVALID_WORKING_DIR = "/okapi-longhorn-configuration-invalid-working-dir.xml";
	private static final String OKAPI_LONGHORN_CONFIGURATION_INVALID_PROJECT_ID_STRATEGY = "/okapi-longhorn-configuration-invalid-project-id-strategy.xml";

	
	@Test
	public void addVersionToWorkingDirPathIfUseUniqueWorkingDirPathIsTrue() {
		InputStream configFileStream = this.getClass().getResourceAsStream(
				OKAPI_LONGHORN_CONFIGURATION_USE_UNIQUE_WORKING_DIR);
		Configuration conf = new Configuration(configFileStream);
		String workingDir = conf.getWorkingDirectory();
		Assert.assertTrue(workingDir.contains("M0."));
	}

	@Test
	public void dontAddVersionToWorkingDirPathIfUseUniqueWorkingDirPathIsFalse() {
		InputStream configFileStream = this.getClass().getResourceAsStream(OKAPI_LONGHORN_CONFIGURATION);
		Configuration conf = new Configuration(configFileStream);
		String workingDir = conf.getWorkingDirectory();
		Assert.assertFalse(workingDir.contains("M0."));
		//test default project id strategy
		Assert.assertEquals(ProjectIdStrategy.Counter, conf.getProjectIdStrategy());
	}

	@Test
	public void throwExceptionForInvalidFile() {
		try {
			InputStream configFileStream = this.getClass().getResourceAsStream(
					OKAPI_LONGHORN_CONFIGURATION_INVALID_WORKING_DIR);
			new Configuration(configFileStream);
			Assert.fail();
		}
		catch (RuntimeException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void throwExceptionIfNoVersionIsFoundAndUseUniqueWorkingDirIsTrue() {
		try {
			InputStream configFileStream = this.getClass().getResourceAsStream(
					OKAPI_LONGHORN_CONFIGURATION_INVALID_WORKING_DIR);
			Configuration conf = new Configuration();
			conf.setVersionPropertyFileName("");
			conf.loadFromFile(configFileStream);
			Assert.fail();
		}
		catch (RuntimeException e) {
			Assert.assertTrue(true);
		}

	}
	
	@Test
	public void throwExceptionIfInvalidProjectIdStrategy() {
		try {
			InputStream configFileStream = this.getClass().getResourceAsStream(
					OKAPI_LONGHORN_CONFIGURATION_INVALID_PROJECT_ID_STRATEGY);
			Configuration conf = new Configuration(configFileStream);
			Assert.fail();
		}
		catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void ignoreFileSeperatorOnEndOfWorkingDirPathWhenAddingVersion() {
		InputStream configFileStream = this.getClass().getResourceAsStream(
				OKAPI_LONGHORN_CONFIGURATION_USE_UNIQUE_WORKING_DIR);
		Configuration conf = new Configuration(configFileStream);
		String workingDir = conf.getWorkingDirectory();
		assertTrue("The configured directory does not contain a version extension",
				workingDir.contains("longhorn-files_M0."));

		Assert.assertEquals(ProjectIdStrategy.Counter, conf.getProjectIdStrategy());
	}
	
	@Test
	public void overrideWorkingDirInXMLConfig() {
		Configuration conf = new Configuration("overrideWorkingDir", 
				new ByteArrayInputStream(("<longhorn-config>\n" + 
						"	 <working-directory>testData/longhorn-files/</working-directory>\n" + 
						"</longhorn-config>").getBytes()));
		assertEquals("overrideWorkingDir",	conf.getWorkingDirectory());
	}
}
