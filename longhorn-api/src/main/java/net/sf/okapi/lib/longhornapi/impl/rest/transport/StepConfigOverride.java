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