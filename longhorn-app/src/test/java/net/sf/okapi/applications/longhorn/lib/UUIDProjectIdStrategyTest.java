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
