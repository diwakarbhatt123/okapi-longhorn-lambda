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
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class WorkspaceUtilsTest {

	private static final String LONGHORN_WORKDIR = "LONGHORN_WORKDIR";
	private static final String WORKING_DIR_IN_TEMP = System.getProperty("java.io.tmpdir") + File.separator + "longhorn-files";

	@Test
	public void loadWorkingDirFromSystemConfig() {
		System.setProperty(LONGHORN_WORKDIR, WORKING_DIR_IN_TEMP);
		assertEquals(new File(WORKING_DIR_IN_TEMP), new File(WorkspaceUtils.getWorkingDirectory()));
	}

	@Test
	public void sortDoesNotReturnTheSameCollectionObject() {
		Collection<File> unsortedFiles = new ArrayList<File>();
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertNotSame(unsortedFiles, sortedFiles);
	}

	@Test
	public void orderedListWithFilesDoesntChange() {
		File f1 = new File("a.xml");
		File f2 = new File("b.xml");
		Collection<File> unsortedFiles = Arrays.asList(f1, f2);
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertEquals(f1, sortedFiles.get(0));
		assertEquals(f2, sortedFiles.get(1));
	}

	@Test
	public void orderedListWithFilesInSameDirectoryDoesntChange() {
		File f1 = new File("1/a.xml");
		File f2 = new File("1/b.xml");
		Collection<File> unsortedFiles = Arrays.asList(f1, f2);
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertEquals(f1, sortedFiles.get(0));
		assertEquals(f2, sortedFiles.get(1));
	}

	@Test
	public void orderedListWithFilesInDifferentDirectoryDoesntChange() {
		File f1 = new File("1/a.xml");
		File f2 = new File("2/a.xml");
		Collection<File> unsortedFiles = Arrays.asList(f1, f2);
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertEquals(f1, sortedFiles.get(0));
		assertEquals(f2, sortedFiles.get(1));
	}

	@Test
	public void unorderedListWithFilesGetsSorted() {
		File f1 = new File("a.xml");
		File f2 = new File("b.xml");
		Collection<File> unsortedFiles = Arrays.asList(f2, f1);
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertEquals(f1, sortedFiles.get(0));
		assertEquals(f2, sortedFiles.get(1));
	}

	@Test
	public void unorderedListWithFilesInSameDirectoryGetsSorted() {
		File f1 = new File("1/a.xml");
		File f2 = new File("1/b.xml");
		Collection<File> unsortedFiles = Arrays.asList(f2, f1);
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertEquals(f1, sortedFiles.get(0));
		assertEquals(f2, sortedFiles.get(1));
	}

	@Test
	public void unorderedListWithFilesInDifferentDirectoryGetsSorted() {
		File f1 = new File("1/a.xml");
		File f2 = new File("2/a.xml");
		Collection<File> unsortedFiles = Arrays.asList(f2, f1);
		ArrayList<File> sortedFiles = WorkspaceUtils.sortFilesByPath(unsortedFiles);

		assertEquals(f1, sortedFiles.get(0));
		assertEquals(f2, sortedFiles.get(1));
	}

}
