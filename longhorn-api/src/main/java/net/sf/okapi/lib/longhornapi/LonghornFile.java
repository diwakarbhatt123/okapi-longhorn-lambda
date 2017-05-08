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

package net.sf.okapi.lib.longhornapi;

import java.io.InputStream;

/**
 * A file that's part of an {@link LonghornProject}.
 */
public interface LonghornFile {
	
	/**
	 * @return The relative path of the file that is used to store it in the project
	 */
	String getRelativePath();
	
	/**
	 * @return The content of the file
	 */
	InputStream openStream();
	
	/**
	 * @return The content of a newly created zip file, containing this file
	 */
	InputStream openStreamToZip();
}
