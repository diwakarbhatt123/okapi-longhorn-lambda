/*
 * =========================================================================== Copyright (C) 2013 by the Okapi Framework
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
		assertEquals(WORKING_DIR_IN_TEMP, WorkspaceUtils.getWorkingDirectory());
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
