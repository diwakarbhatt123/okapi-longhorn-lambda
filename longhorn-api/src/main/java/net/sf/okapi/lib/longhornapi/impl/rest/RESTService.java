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

import java.net.URI;
import java.util.ArrayList;

import net.sf.okapi.lib.longhornapi.LonghornProject;
import net.sf.okapi.lib.longhornapi.LonghornService;

/**
 * Implementation of {@link LonghornService} for the communication with Longhorn's RESTful interface.
 */
public class RESTService implements LonghornService {
	private URI baseUri;
	
	protected RESTService() {
	}
	
	/**
	 * Default constructor
	 * 
	 * @param baseUri i.e. http://myserver:9095/okapi-longhorn
	 * @throws IllegalArgumentException if the URL is unreachable or does not correspond to a Longhorn web-service
	 */
	public RESTService(URI baseUri) throws IllegalArgumentException {
		this.baseUri = baseUri;
		
		try {
			// Check if service is reachable
			getProjects();
		}
		catch (RuntimeException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public URI getBaseUri() {
		return baseUri;
	}

	@Override
	public LonghornProject createProject() {

		try {
			URI projUri = Util.createProject(baseUri);
			return new RESTProject(projUri);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArrayList<LonghornProject> getProjects() {
		
		try {
			ArrayList<String> projIds = Util.getList(baseUri.toString() + "/projects");
			ArrayList<LonghornProject> projects = new ArrayList<LonghornProject>();
			
			for (String projId : projIds) {
				projects.add(new RESTProject(baseUri, projId));
			}
			
			return projects;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return baseUri.toString();
	}

}
