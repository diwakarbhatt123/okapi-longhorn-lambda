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
