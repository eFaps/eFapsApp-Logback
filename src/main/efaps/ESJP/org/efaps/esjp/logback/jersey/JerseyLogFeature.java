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
package org.efaps.esjp.logback.jersey;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.slf4j.Logger;

/**
 * The Class JerseyLogFeature.
 */
@EFapsUUID("cbcc3660-d789-4967-9091-41bb29221026")
@EFapsApplication("eFapsApp-Logback")
@Provider
public class JerseyLogFeature
    implements Feature
{

    /** The logger. */
    private Logger logger;

    @Override
    public boolean configure(final FeatureContext _context)
    {
        final JerseyLogFilter filter = new JerseyLogFilter();
        if (getLogger() != null) {
            filter.setLogger(filter.getLogger());
        }
        _context.register(filter);
        return true;
    }

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Sets the logger.
     *
     * @param _logger the new logger
     */
    public void setLogger(final Logger _logger)
    {
        this.logger = _logger;
    }
}
