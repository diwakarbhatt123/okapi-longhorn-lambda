/*
 * =========================================================================== Copyright (C) 2011 by the Okapi Framework
 * contributors ----------------------------------------------------------------------------- This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA See also the full
 * LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.applications.longhorn.lib;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {

	private static final String OKAPI_LONGHORN_CONFIGURATION_USE_UNIQUE_WORKING_DIR = "/okapi-longhorn-configuration-unique-working-dir-true.xml";
	private static final String OKAPI_LONGHORN_CONFIGURATION = "/okapi-longhorn-configuration.xml";
	private static final String OKAPI_LONGHORN_CONFIGURATION_INVALID_WORKING_DIR = "/okapi-longhorn-configuration-invalid-working-dir.xml";

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
	public void ignoreFileSeperatorOnEndOfWorkingDirPathWhenAddingVersion() {
		InputStream configFileStream = this.getClass().getResourceAsStream(
				OKAPI_LONGHORN_CONFIGURATION_USE_UNIQUE_WORKING_DIR);
		Configuration conf = new Configuration(configFileStream);
		String workingDir = conf.getWorkingDirectory();
		assertTrue("The configured directory does not contain a version extension",
				workingDir.contains("longhorn-files_M0."));

	}
}
