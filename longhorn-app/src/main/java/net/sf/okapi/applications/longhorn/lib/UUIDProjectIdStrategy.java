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

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class UUIDProjectIdStrategy {

	private static final TimeBasedGenerator genUUID = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
	private static final int MAX_PROJECT_CREATE_ATTEMPTS = 50;
	
	public static String generateNewProjectId() {
		String projId = null;
		int numAttempts = 0;
		File projectPath = null;
		
		while(numAttempts<MAX_PROJECT_CREATE_ATTEMPTS) {
			projId = genUUID.generate().toString();
			numAttempts++;
			
			projectPath = new File(WorkspaceUtils.getProjectPath(projId));
			if (!projectPath.exists()) {
				break;
			}
			//project exists, try again
		}
		if(projectPath.exists()) {
			throw new IllegalStateException("Could not create new project, no unique name after "+numAttempts+" attempts.");
		}
		return projId;
	}

}
