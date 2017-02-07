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

package net.sf.okapi.applications.longhorn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.okapi.common.StreamUtil;
import net.sf.okapi.lib.longhornapi.LonghornFile;
import net.sf.okapi.lib.longhornapi.LonghornProject;
import net.sf.okapi.lib.longhornapi.LonghornService;
import net.sf.okapi.lib.longhornapi.impl.rest.RESTService;
import net.sf.okapi.lib.longhornapi.impl.rest.transport.StepConfigOverride;
import net.sf.okapi.lib.longhornapi.impl.rest.transport.XMLStepConfigOverrideList;
import net.sf.okapi.steps.textmodification.Parameters;
import net.sf.okapi.steps.textmodification.TextModificationStep;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceTest {
	private static final String SERVICE_BASE_URL = "http://localhost:9095/okapi-longhorn";
	private static LonghornService ws;
	private static LonghornProject emptyProj;
	private static LonghornProject preparedProj;
	private static File inputFile;
	private static File inputZip;
	
	@BeforeClass
	public static void setup() throws Exception {
		ws = new RESTService(new URI(SERVICE_BASE_URL));
		
		URL input1 = ServiceTest.class.getResource("/rawdocumenttofiltereventsstep.html");
		inputFile = new File(input1.toURI());
		
		URL input2 = ServiceTest.class.getResource("/more_files.zip");
		inputZip = new File(input2.toURI());
		
		URL bconfUrl = ServiceTest.class.getResource("/html_segment_and_text_mod.bconf");
		File bconf = new File(bconfUrl.toURI());
		
		preparedProj = ws.createProject();
		preparedProj.addBatchConfiguration(bconf);
		preparedProj.addInputFile(inputFile, inputFile.getName());
		preparedProj.addInputFile(inputFile, "samefile/" + inputFile.getName());
	}
	
	@Before
	public void prep() throws Exception {
		emptyProj = ws.createProject();
	}
	
	@After
	public void cleanup() {
		if (emptyProj != null)
			emptyProj.delete();
	}
	
	@AfterClass
	public static void cleanupFinal() {
		if (preparedProj != null)
			preparedProj.delete();
	}
	
	@Test
	public void wrongUrlThrowsException() throws Exception {
		try {
			ws = new RESTService(new URI(SERVICE_BASE_URL + "wrong_url"));
			fail("Invalid URL should have caused Exception");
		}
		catch (IllegalArgumentException e) {
		}
	}
	
	@Test
	public void createAndDeleteProjects() {
		int projCountBefore = ws.getProjects().size();
		
		LonghornProject proj = ws.createProject();
		assertEquals(projCountBefore + 1, ws.getProjects().size());
		
		proj.delete();
		assertEquals(projCountBefore, ws.getProjects().size());
	}
	
	@Test
	public void newProjectIsEmpty() {
		assertEquals(0, emptyProj.getInputFiles().size());
		assertEquals(0, emptyProj.getOutputFiles().size());
	}
	
	@Test
	public void addingInputFile() throws FileNotFoundException {
		emptyProj.addInputFile(inputFile, inputFile.getName());
		assertEquals(1, emptyProj.getInputFiles().size());
		assertEquals(0, emptyProj.getOutputFiles().size());
		ArrayList<LonghornFile> inputFiles = emptyProj.getInputFiles();
		assertEquals("rawdocumenttofiltereventsstep.html", inputFiles.get(0).getRelativePath());
	}
	
	@Test
	public void addingInputFileInSubdir() throws FileNotFoundException {
		emptyProj.addInputFile(inputFile, "samefile/" + inputFile.getName());
		assertEquals(1, emptyProj.getInputFiles().size());
		assertEquals(0, emptyProj.getOutputFiles().size());
		ArrayList<LonghornFile> inputFiles = emptyProj.getInputFiles();
		assertEquals("samefile/rawdocumenttofiltereventsstep.html", inputFiles.get(0).getRelativePath());
	}
	
	@Test
	public void addingInputFilesFromZip() throws FileNotFoundException {
		emptyProj.addInputFilesFromZip(inputZip);
		
		ArrayList<LonghornFile> inputFiles = emptyProj.getInputFiles();
		assertEquals(2, inputFiles.size());
		assertEquals(0, emptyProj.getOutputFiles().size());

		// Are the input file names as expected (with 1 file in a sub-directory)?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : inputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("searchandreplacestep.html"));
		assertTrue(relFilePaths.contains("subdir1/segmentationstep.html"));
	}
	
	@Test
	public void addBconfWithDuplicateOverridParams() throws FileNotFoundException {
		
		XMLStepConfigOverrideList overrideParams = new XMLStepConfigOverrideList();
		StepConfigOverride item = new StepConfigOverride();
		item.setStepClassName("abcd");
		item.setStepParams("def");
		overrideParams.add(item);
		
		item = new StepConfigOverride();
		item.setStepClassName("abcd");
		item.setStepParams("def22");
		overrideParams.add(item);
		try {
			URL bconfUrl = ServiceTest.class.getResource("/html_segment_and_text_mod.bconf");
			File bconf = new File(bconfUrl.toURI()); 
			emptyProj.addBatchConfiguration(bconf, overrideParams);
			
			fail("Test should have resulted in an exception for duplicate step class name");
		} catch(Exception e) {
			assertTrue(e.getMessage().contains("Duplicate"));
		}
	}
	
	@Test
	public void addBconfWithBadOverridParams() throws FileNotFoundException {
		
		XMLStepConfigOverrideList overrideParams = new XMLStepConfigOverrideList();
		StepConfigOverride item = new StepConfigOverride();
		item.setStepClassName("abcd");
		item.setStepParams("def");
		overrideParams.add(item);
		try {
			URL bconfUrl = ServiceTest.class.getResource("/html_segment_and_text_mod.bconf");
			File bconf = new File(bconfUrl.toURI());
			emptyProj.addBatchConfiguration(bconf, overrideParams);			
			fail("Test should have thrown exception due to wrong override params.");
		} catch(Exception w) {
			assertTrue(w.getMessage().contains("does not exist"));
		}
	}
	
	@Test
	public void addBconfWithOverridParams() throws FileNotFoundException {
		
		TextModificationStep tmsStep = new TextModificationStep();
		Parameters p = (Parameters)tmsStep.getParameters();
		p.setApplyToBlankEntries(false);
		
		XMLStepConfigOverrideList overrideParams = new XMLStepConfigOverrideList();
		StepConfigOverride item = new StepConfigOverride();
		item.setStepClassName("net.sf.okapi.steps.textmodification.TextModificationStep");
		item.setStepParams(p.toString());
		overrideParams.add(item);
		
		try
		{
		URL bconfUrl = ServiceTest.class.getResource("/html_segment_and_text_mod.bconf");
		File bconf = new File(bconfUrl.toURI());
		
		emptyProj.addBatchConfiguration(bconf, overrideParams);
		}
		catch(Exception w) {
			assertTrue(w.getMessage().contains("override params"));
		}
		
	}
	
	@Test
	public void executePipelineCreatesOutputFiles() throws FileNotFoundException {
		preparedProj.executePipeline();

		ArrayList<LonghornFile> inputFiles = preparedProj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = preparedProj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
	}
	
	@Test
	public void executePipelineWithLangParametersCreatesOutputFiles() throws FileNotFoundException {
		preparedProj.executePipeline("en", "de");

		ArrayList<LonghornFile> inputFiles = preparedProj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = preparedProj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
	}
	
	@Test
	public void executePipelineWithMultipleTargetLangsCreatesOutputFiles() throws FileNotFoundException {
		preparedProj.executePipeline("en", Arrays.asList("de", "it", "fr"));

		ArrayList<LonghornFile> inputFiles = preparedProj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = preparedProj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
	}
	
	@Test
	public void fetchSingleOutputFile() throws IOException {
		preparedProj.executePipeline();

		ArrayList<LonghornFile> outputFiles = preparedProj.getOutputFiles();
		assertEquals(2, outputFiles.size());
		
		for (LonghornFile of : outputFiles) {
			File outputFile = downloadFileToTemp(of.openStream());
			
			assertNotNull(outputFile);
			assertTrue(outputFile.exists());
			assertTrue(outputFile.length() > 0);
			outputFile.delete();
		}
	}
	
	@Test
	public void fetchOutputFilesAsZip() throws FileNotFoundException {
		preparedProj.executePipeline();

		InputStream zippedOutputFiles = preparedProj.getOutputFilesAsZip();
		assertNotNull(zippedOutputFiles);
	}
	
	@Test
	public void fetchOutputFilesAsZipThrowsExceptionForNoFiles() throws FileNotFoundException {
		try {
			emptyProj.getOutputFilesAsZip();
			fail("No output files should cause an exception.");
		}
		catch (IllegalStateException e) {
		}
	}
	
	@Test
	public void fetchOutputFileAsZip() throws FileNotFoundException {
		preparedProj.executePipeline();

		ArrayList<LonghornFile> outputFiles = preparedProj.getOutputFiles();
		LonghornFile firstOutputFile = outputFiles.get(0);
		InputStream zippedFile = firstOutputFile.openStreamToZip();
		assertNotNull(zippedFile);
	}

    @Test
	public void executePipelineForFilesWithNoExtension() throws Exception {
		URL input = ServiceTest.class.getResource("/test_xml");
		File inputFileWithNoExtension = new File(input.toURI());

		URL bconfUrl = ServiceTest.class.getResource(
            "/map_no_extension_to_xml.bconf");
		File bconf = new File(bconfUrl.toURI());

		emptyProj.addBatchConfiguration(bconf);
        emptyProj.addInputFile(
            inputFileWithNoExtension, inputFileWithNoExtension.getName());
        emptyProj.executePipeline();

		ArrayList<LonghornFile> inputFiles = emptyProj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = emptyProj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("test_xml"));
	}
	
	private File downloadFileToTemp(InputStream remoteFile) throws IOException {
		
		File tempFile = File.createTempFile("~okapi-5_", "outfile");
		StreamUtil.copy(remoteFile, tempFile);
		remoteFile.close();
		return tempFile;
	}
}
