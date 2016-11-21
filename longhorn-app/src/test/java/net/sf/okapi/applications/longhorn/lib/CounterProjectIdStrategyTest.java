package net.sf.okapi.applications.longhorn.lib;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class CounterProjectIdStrategyTest {

	@Test
	public void testGenerateNewProjectId() {
		String projectIdStr = CounterProjectIdStrategy.generateNewProjectId();
		//check that the returned project id is an integer
		Integer.parseInt(projectIdStr);
		//check that no project already exists for the new project id
		Assert.assertFalse(new File(WorkspaceUtils.getProjectPath(projectIdStr)).exists());
	}

}
