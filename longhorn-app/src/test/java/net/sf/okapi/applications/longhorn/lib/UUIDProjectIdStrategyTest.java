package net.sf.okapi.applications.longhorn.lib;

import java.io.File;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

public class UUIDProjectIdStrategyTest {

	@Test
	public void testGenerateNewProjectId() {
		String projectIdStr = UUIDProjectIdStrategy.generateNewProjectId();
		//check that the returned project id is a valid uuid
		UUID.fromString(projectIdStr);
		//check that no project already exists for the new project id
		Assert.assertFalse(new File(WorkspaceUtils.getProjectPath(projectIdStr)).exists());
	}

}
