/*
 * Copyright 2003 - 2013 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.esjp.logback.util;

import java.util.UUID;

import org.efaps.admin.common.SystemConfiguration;
import org.efaps.util.cache.CacheReloadException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public final class Logback
{
	/**
	 * Singelton.
	 */
	private Logback()
	{
	}

	/**
	 * @return the SystemConfigruation for logback
	 * @throws CacheReloadException on error
	 */
	public static SystemConfiguration getSysConfig()
			throws CacheReloadException
			{

		// logback-Configuration
		return SystemConfiguration.get(UUID
				.fromString("2de47719-b3ed-4396-9f92-28a2d668c022"));
	}
}
