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

import java.util.ArrayList;
import java.util.Collections;

public class CounterProjectIdStrategy {

	public static String generateNewProjectId() {
		ArrayList<String> projectIds = WorkspaceUtils.getProjectIds();
		
		ArrayList<Integer> takenProjectIds = new ArrayList<Integer>();
		for(String id : projectIds) {
			try {
				takenProjectIds.add(Integer.parseInt(id));
			} catch (NumberFormatException e) {
				//ignore ids that are not numbers
			}
		}

		if (takenProjectIds.isEmpty())
			return "1";

		Collections.sort(takenProjectIds);
		// List is in numerical order, so we can simply increase the last value by 1
		Integer highestId = takenProjectIds.get(takenProjectIds.size() - 1);
		return Integer.toString(highestId + 1);
	}

}
