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

package net.sf.okapi.applications.longhorn;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.httpclient.HttpStatus;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.longhorn.lib.ProjectUtils;
import net.sf.okapi.applications.longhorn.lib.WorkspaceUtils;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.longhornapi.impl.rest.transport.XMLStepConfigOverrideList;
import net.sf.okapi.lib.longhornapi.impl.rest.transport.XMLStringList;

/**
 * Handles Web-Service requests and delegates them to Rainbow/Okapi.
 * Also does the handling of the input and output files.
 * 
 *
 * Basic workflow for processing files with the web-service:
 * 
 * <ol>
 *	<li> POST	/projects/new
 *	<li> POST	/projects/1/batchConfiguration
 *	<li> POST	/projects/1/inputFiles.zip
 *	<li> PUT	/projects/1/inputFiles/a.html
 *	<li> PUT	/projects/1/inputFiles/b.html
 *	<li> PUT	/projects/1/inputFiles/c.html
 *	<li> POST	/projects/1/tasks/execute
 *	<li> GET	/projects/1/outputFiles
 *	<li> GET	/projects/1/outputFiles/a.out.html
 *	<li> GET	/projects/1/outputFiles/b.out.html
 *	<li> GET	/projects/1/outputFiles/c.out.html
 *	<li> GET	/projects/1/outputFiles.zip
 *	<li> DEL	/projects/1
 * </ol>
 */
@Path("/projects")
public class RESTInterface {
	private static final Logger LOG = LoggerFactory.getLogger(RESTInterface.class);

	//TODO DEL for input file
	//TODO DEL for output file
	//TODO DEL /projects/outputFiles to clear output directory
	//TODO GET for batch conf

	/**
	 * Create a new project to work with.
	 * 
	 * @return The new project's URI
	 */
	@POST
	@Path("/new")
	public Response createProject(@Context UriInfo uriInfo) {
		String projId = ProjectUtils.createNewProject();
		
		URI projectUri = uriInfo.getAbsolutePath().resolve(projId + "");
		LOG.info("Created project " + projId + " with URI " + projectUri);
		return Response.created(projectUri).build();
	}

	/**
	 * @return A list of all existing project folders (Integers in numerical order)
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_XML)
	public XMLStringList getProjects() {
		
		ArrayList<String> projIds = WorkspaceUtils.getProjectIds();
		return new XMLStringList(projIds);
	}

	/**
	 * Deletes a project directory. Should be used to clean up after all processing is done.
	 * 
	 * @param projId The id of the project to delete
	 * @return
	 */
	@DELETE
	@Path("/{projId}")
	public Response deleteProject(@PathParam("projId") String projId) {
		LOG.info("Deleting project " + projId);
		Util.deleteDirectory(WorkspaceUtils.getProjectPath(projId), false);

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	/**
	 * Installs the posted batch configuration file in the project with the given id.
	 * This batch configuration will be used to process the input files.
	 * 
	 * @param projId The id of the project the batch configuration shall be added to
	 * @param input The batch configuration file as part of a multi-part form. The parameter must have the name from <code>WorkspaceUtils.BATCH_CONF_PARAM</code>
	 * @return
	 */
	@POST
	@Path("/{projId}/batchConfiguration")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response addBatchConfigurationFile(@PathParam("projId") String projId, MultipartFormDataInput input) {

		try {
			File tmpFile = input.getFormDataPart(WorkspaceUtils.BATCH_CONF_PARAM, File.class, null);
			String str = input.getFormDataPart(WorkspaceUtils.OVERRIDE_STEPS_PARAM, String.class, null);
			
			ProjectUtils.addBatchConfig(projId, tmpFile, XMLStepConfigOverrideList.unmarshal(str));
			LOG.info("Adding batch config to project " + projId);
			tmpFile.delete();
		}
		catch (Exception e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(e.getClass().getName()+":"+e.getMessage()).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}
	
	/**
	 * Stores the posted file in the input file's directory of the project with the given id.
	 * 
	 * @param projId The id of the project the file shall be added to
	 * @param filename The file's original filename
	 * @param input The input file as part of a multi-part form. The parameter must have the name from
	 *            <code>WorkspaceUtils.BATCH_CONF_PARAM</code>
	 * @return
	 */
	@PUT
	@POST
	@Path("/{projId}/inputFiles/{filename:.+}")
	public Response addProjectInputFile(@PathParam("projId") String projId, @PathParam("filename") String filename,
			MultipartFormDataInput input) {
		
		try {
			File tmpFile = input.getFormDataPart(WorkspaceUtils.INPUT_FILE_PARAM, File.class, null);
			ProjectUtils.addInputFile(projId, tmpFile, filename);
			LOG.info("Added input file " + filename + " to project " + projId);
			tmpFile.delete();
		}
		catch (IOException e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			return Response.status(status).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}
	
	/**
	 * @param projId The id of a project
	 * @return A list of the names of all input files uploaded yet
	 */
	@GET
	@Path("/{projId}/inputFiles")
	@Produces(MediaType.TEXT_XML)
	public XMLStringList getProjectInputFiles(@PathParam("projId") String projId) {
		LOG.info("Getting project input files for project " + projId);
		ArrayList<String> inputFiles = WorkspaceUtils.getInputFileNames(projId);
		logProjectFiles(projId, inputFiles, "Input");
		return new XMLStringList(inputFiles);
	}

	/**
	 * Retrieve one of the input files that were added to the project before.
	 * 
	 * @param projId The id of a project
	 * @param filename The name of the input file to fetch
	 * @return The specified file from the project
	 */
	@GET
	@Path("/{projId}/inputFiles/{filename:.+}")
	@Produces(MediaType.WILDCARD)
	public File getProjectInputFile(
			@PathParam("projId") String projId, @PathParam("filename") String filename) {
		LOG.info("Getting project input file " + filename + " for project " + projId);
		return WorkspaceUtils.getInputFile(projId, filename);
	}

	/**
	 * Executes the uploaded batch configuration on the input files that have been added.
	 * 
	 * @param projId The id of the project to be executed
	 * @return
	 */
	@POST
	@Path("/{projId}/tasks/execute")
	public Response executeProject(@PathParam("projId") String projId) {

		try {
			LOG.info("Executing project " + projId);
			ProjectUtils.executeProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			String type = MediaType.TEXT_PLAIN;
			String body = e.toString();
			return Response.status(status).type(type).entity(body).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	/**
	 * Executes the uploaded batch configuration on the input files that have been added.
	 * 
	 * @param projId The id of the project to be executed
	 * @param sourceLanguage source language for pipeline
	 * @param targetLanguage target language for pipeline
	 * @return
	 */
	@POST
	@Path("/{projId}/tasks/execute/{source}/{target}")
	public Response executeProject(@PathParam("projId") String projId, @PathParam("source") String sourceLanguage, @PathParam("target") String targetLanguage) {

		try {
			LOG.info("Executing project " + projId + " with sourceLanguage " + sourceLanguage +
					 " and targetLanguage " + targetLanguage);
			ProjectUtils.executeProject(projId, sourceLanguage, targetLanguage, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			String type = MediaType.TEXT_PLAIN;
			String body = e.toString();
			return Response.status(status).type(type).entity(body).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}
	
	/**
	 * @param projId The id of a local project
	 * @return A list of the names of all output files that have been generated in that project
	 */
	@GET
	@Path("/{projId}/outputFiles")
	@Produces(MediaType.TEXT_XML)
	public XMLStringList getProjectOutputFiles(@PathParam("projId") String projId) {
		LOG.info("Getting project output files for project " + projId);
		ArrayList<String> outputFiles = WorkspaceUtils.getOutputFileNames(projId);
		logProjectFiles(projId, outputFiles, "Output");
		return new XMLStringList(outputFiles);
	}

	/**
	 * Retrieve one of the output files generated by Okapi/Rainbow.
	 * 
	 * @param projId The id of a project
	 * @param filename The name of the input file to fetch
	 * @return The specified file from the project
	 */
	@GET
	@Path("/{projId}/outputFiles/{filename:.+}")
	@Produces(MediaType.WILDCARD)
	public File getProjectOutputFile(
			@PathParam("projId") String projId, @PathParam("filename") String filename) {
		LOG.info("Getting file " + filename + " for project " + projId);
		return WorkspaceUtils.getOutputFile(projId, filename);
	}

	@POST
	@Path("/{projId}/inputFiles.zip")
	public Response addProjectInputFilesFromArchive(@PathParam("projId") String projId, MultipartFormDataInput input) {
		
		try {
			LOG.info("Adding project files from zip to project " + projId);
			File tmpFile = input.getFormDataPart(WorkspaceUtils.INPUT_FILE_PARAM, File.class, null);
			ProjectUtils.addInputFilesFromArchive(projId, tmpFile);
			tmpFile.delete();
		}
		catch (IOException e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			return Response.status(status).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	@GET
	@Path("/{projId}/outputFiles.zip")
	@Produces(MediaType.WILDCARD)
	public File getProjectOutputFilesAsArchive(
			@PathParam("projId") String projId) throws IOException {
		
		//TODO how to do exception handling?
		LOG.info("Fetching output files as archive for project " + projId);
		return WorkspaceUtils.getOutputFilesAsArchive(projId);
	}

	/**
	 * Retrieve one of the output files generated by Okapi/Rainbow inside a ZIP file.
	 * 
	 * @param projId The id of a project
	 * @param filename The name of the input file to fetch (with '.zip' appended)
	 * @return The specified file from the project inside a ZIP.
	 */
	@GET
	@Path("/{projId}/outputFile.zip/{filename:.+}.zip")
	@Produces("application/zip")
	public File getProjectOutputFileAsZip(
			@PathParam("projId") String projId, @PathParam("filename") String filename) throws IOException {

		LOG.info("Fetching output file " + filename + " for project " + projId);
		final File desiredFile = WorkspaceUtils.getOutputFile(projId, filename);

		File tempZip = File.createTempFile("~okapi-4_", ".zip");
		if (0 == WorkspaceUtils.zip(Collections.singleton(desiredFile),
				WorkspaceUtils.getOutputDirPath(projId) + File.separator, tempZip))
			throw new RuntimeException("Error while addind the output files to an archive.");

		return tempZip;
	}

	/**
	 * Executes the uploaded batch configuration on the input files that have been added.
	 * 
	 * @param projId The id of the project to be executed
	 * @param sourceLanguage source language for pipeline
	 * @param targetLanguages target languages, the first of which is used for the pipeline, the rest stored for
	 *            Textpool leveraging.
	 */
	@POST
	@Path("/{projId}/tasks/execute/{sourceLanguage}")
	public Response executeProject(
			@PathParam("projId") final String projId,
			@PathParam("sourceLanguage") final String sourceLanguage,
			@QueryParam("targets") final List<String> targetLanguages) {

		try {
			List<LocaleId> targetLocales = LocaleId.convertToLocaleIds(targetLanguages);
			ProjectUtils.executeProject(projId, sourceLanguage, targetLanguages.get(0), targetLocales);
		}
		catch (Exception e) {
			e.printStackTrace();
			final int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			final String type = MediaType.TEXT_PLAIN;
			final String body = createStacktraceString(e);
			return Response.status(status).type(type).entity(body).build();
		}

		return Response.status(HttpStatus.SC_OK).build();
	}

	private String createStacktraceString(final Exception e) {
		final StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	private void logProjectFiles(String projId, List<String> files, String type) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(type + " files for project " + projId + ": " + files);
		}
	}
}
