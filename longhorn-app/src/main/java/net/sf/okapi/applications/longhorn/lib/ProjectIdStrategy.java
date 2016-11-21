package net.sf.okapi.applications.longhorn.lib;

public enum ProjectIdStrategy {
	UUID,	/* Generate a new UUID for each project */
	Counter /* Use an incrementing counter, this will first scan working directory for all existing projects */
}
