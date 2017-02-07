package net.sf.okapi.lib.longhornapi.impl.rest.transport;

public class StepConfigOverride {

	private String stepClassName;
	private String stepParams;
	
	public StepConfigOverride() {
	}

	/**
	 * @return the stepClassName
	 */
	public String getStepClassName() {
		return stepClassName;
	}
	
	/**
	 * @param stepClassName the stepClassName to set
	 */
	public void setStepClassName(String stepClassName) {
		this.stepClassName = stepClassName;
	}
	
	/**
	 * @return the stepParams
	 */
	public String getStepParams() {
		return stepParams;
	}

	/**
	 * @param stepParams the stepParams to set
	 */
	public void setStepParams(String stepParams) {
		this.stepParams = stepParams;
	}
}