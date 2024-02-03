/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.esjp.logback.util;

import java.util.UUID;

import org.efaps.admin.common.SystemConfiguration;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.cache.CacheReloadException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("2ada2f30-c485-4fc4-b611-3c33530ac467")
@EFapsApplication("eFapsApp-Logback")
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
