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
