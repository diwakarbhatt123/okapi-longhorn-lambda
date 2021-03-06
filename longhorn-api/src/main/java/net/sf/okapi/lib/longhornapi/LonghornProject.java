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

package net.sf.okapi.lib.longhornapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.longhornapi.impl.rest.transport.XMLStepConfigOverrideList;

/**
 * Provides abstract methods for interacting with one of the temporary projects
 * available on a Longhorn web-service instance.
 */
public interface LonghornProject {

	/**
	 * Pushes a batch configuration file that was exported from Rainbow to the project.
	 * It will be used to process the input files when {@link #executePipeline()} is called.
	 * 
	 * @param bconf A batch configuration file that was exported from Rainbow
	 * @throws FileNotFoundException If the file does not exist
	 */
	void addBatchConfiguration(File bconf) throws FileNotFoundException;
	
	/**
	 * Pushes a batch configuration file that was exported from Rainbow to the project.
	 * It will be used to process the input files when {@link #executePipeline()} is called.
	 * 
	 * @param bconf A batch configuration file that was exported from Rainbow
	 * @param overrides A list of Pipeline Step Parameters that should override the settings
	 * 	specified in the pipeline in the bconf file
	 * @throws FileNotFoundException If the file does not exist
	 */
	void addBatchConfiguration(File bconf, XMLStepConfigOverrideList overrides)
				throws FileNotFoundException;
	/**
	 * Adds an input file to the project. It will be processed when {@link #executePipeline()} is called.
	 * 
	 * @param inputFile The file to be pushed to the service
	 * @param relativePath The relative path of the file that shall be used to store it in the project
	 * @throws FileNotFoundException  If the file does not exist
	 */
	void addInputFile(File inputFile, String relativePath) throws FileNotFoundException;
	
	/**
	 * Allows to add any number of input files to the project with one call.
	 * The specified file is expected to be a zip archive.
	 * The archive will be extracted into the project (with the relative file paths).
	 * 
	 * @param zipFile The zip archive with the files to be pushed to the service
	 * @throws FileNotFoundException  If the file does not exist
	 */
	void addInputFilesFromZip(File zipFile) throws FileNotFoundException;
	
	/**
	 * Returns all input files that have been added to this project so far.
	 * 
	 * @return A list of all input files in this project
	 */
	ArrayList<LonghornFile> getInputFiles();
	
	/**
	 * Executes the pipeline from the previously added batch configuration
	 * on all input files in this project.
	 */
	void executePipeline();
	
	/**
	 * Executes the pipeline from the previously added batch configuration
	 * on all input files in this project and sets the source and target language
	 * of the project to the parameter values.
	 * 
	 * @param sourceLanguage source language for pipeline
	 * @param targetLanguage target language for pipeline
	 * @throws NullPointerException if one of the parameters is <code>null</code>
	 */
	void executePipeline(String sourceLanguage, String targetLanguage) throws NullPointerException;

	/**
	 * Executes the pipeline from the previously added batch configuration
	 * on all input files in this project and sets the source and target languages
	 * of the project to the parameter values.
	 *
	 * @param sourceLanguage source language for pipeline
	 * @param targetLanguages target languages for pipeline
	 * @throws NullPointerException if one of the parameters is <code>null</code>
	 */
	void executePipeline(String sourceLanguage, List<String> targetLanguages) throws NullPointerException;
	
	/**
	 * Returns all output files that were generated when the project's pipeline was executed.
	 * 
	 * @return A list of all output files in this project
	 */
	ArrayList<LonghornFile> getOutputFiles();
	
	/**
	 * Returns all output files that were generated when the project's pipeline was executed.
	 * All files are stored in a single zip archive. The entries in the zip archive have
	 * the same relative file paths as the output files in the project.
	 * 
	 * @return The content of the zip file
	 * @throws IllegalStateException If there are no output files
	 */
	InputStream getOutputFilesAsZip() throws IllegalStateException;
	
	/**
	 * Deletes this project from the web-service.
	 */
	void delete();

}
