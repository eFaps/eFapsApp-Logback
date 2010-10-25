/*
 * Copyright 2003 - 2010 The eFaps Team
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

package org.efaps.esjp.logback;

import java.io.IOException;
import java.util.List;

import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.util.EFapsException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Class is used to configure the LogBack logger during runtime.
 *
 * @author The eFaps Team
 * @version $Id: $
 */
@EFapsUUID("6ada2f30-c358-4fc4-b611-3c33530ac467")
@EFapsRevision("$Rev: 5276 $")
public abstract class Configuration_Base
{
    /**
     * Updates the LogBack Configuration.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return  new empty Return
     * @throws EFapsException on error
     */
    public Return update(final Parameter _parameter)
    {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (final Logger logger : lc.getLoggerList()) {
            final String value = _parameter.getParameterValue(logger.getName());
            if (value != null) {
                if ("INHERITED".equalsIgnoreCase(value)) {
                    if (logger.getLevel() != null) {
                        logger.setLevel(null);
                    }
                } else {
                    if (logger.getLevel() == null) {
                        logger.setLevel(Level.toLevel(value));
                    } else if (!value.equalsIgnoreCase(logger.getLevel().levelStr)) {
                        logger.setLevel(Level.toLevel(value));
                    }
                }
            }
        }
        return new Return();
    }

    /**
     *  @param _parameter Parameter as passed from the eFaps API
     * @return Snipllet containgn list of loggers
     */
    public Return loggersFieldValue(final Parameter _parameter)
    {
        final Return ret = new Return();

        final StringBuilder html = new StringBuilder();
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final List<Logger> loggerList = lc.getLoggerList();

        html.append("<table>")
            .append("<tr><th>")
            .append(DBProperties.getProperty("org.efaps.esjp.logback.Configuration.LoggerName")).append("</th><th>")
            .append(DBProperties.getProperty("org.efaps.esjp.logback.Configuration.EffectiveLevel")).append("</th><th>")
            .append(DBProperties.getProperty("org.efaps.esjp.logback.Configuration.Level")).append("</th><th>")
            .append("</th></tr>");
        for (final Logger logger : loggerList)
        {
           html.append("<tr><td>").append(logger.getName()).append("</td><td>")
               .append(logger.getEffectiveLevel().levelStr).append("</td><td>")
               .append(getLevelDropDown(logger)).append("</td></tr>");

        }
        html.append("</table>");
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }


    /**
     * Get an Snipplet containinf a dropdown.
     *
     * @param _loggerLogger thee dropdown is wanted for
     * @return StringBuilder
     */
    protected StringBuilder getLevelDropDown(final Logger _logger)
    {
        final StringBuilder ret = new StringBuilder();
        ret.append("<select name=\"").append(_logger.getName()).append("\" size=\"1\">")
            .append("<option ").append(_logger.getLevel() == null ? "selected=\"selected\"" : "")
                    .append(" value=\"").append("INHERITED").append("\">").append("INHERITED").append("</option>");
        appendOption(ret, _logger.getLevel(), Level.ALL);
        appendOption(ret, _logger.getLevel(), Level.TRACE);
        appendOption(ret, _logger.getLevel(), Level.DEBUG);
        appendOption(ret, _logger.getLevel(), Level.INFO);
        appendOption(ret, _logger.getLevel(), Level.WARN);
        appendOption(ret, _logger.getLevel(), Level.ERROR);
        appendOption(ret, _logger.getLevel(), Level.OFF);

        ret.append("</select>");
        return ret;
    }

    /**
     * Append an option to the StringBuilder.
     *
     * @param _bldr     StringBuilder to append to
     * @param _current  current level
     * @param _target   target level
     */
    protected void appendOption(final StringBuilder _bldr,
                                final Level _current,
                                final Level _target) {
        _bldr.append("<option ")
            .append(_current != null && _current.levelInt == _target.levelInt ? "selected=\"selected\"" : "")
            .append(" value=\"").append(_target.levelStr).append("\">")
            .append(_target.levelStr).append("</option>");
    }

    /**
     * Loads a LogBack Configuration file and parses it using the
     * JoranConfigurater from LogBack. Resets the Logging Context and
     * configures a new one from the given file.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return  new empty Return
     * @throws EFapsException on error
     */
    public Return load(final Parameter _parameter)
        throws EFapsException
    {
        final Context context = Context.getThreadContext();
        final Context.FileParameter fileItem = context.getFileParameters().get("upload");
        try {
            final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            // the context was probably already configured by default configuration
            // rules
            lc.reset();
            configurator.doConfigure(fileItem.getInputStream());
        } catch (final IOException e) {
            throw new EFapsException(this.getClass(), "load", e);
        } catch (final JoranException e) {
            throw new EFapsException(this.getClass(), "load", e);
        }
        return new Return();
    }
}
