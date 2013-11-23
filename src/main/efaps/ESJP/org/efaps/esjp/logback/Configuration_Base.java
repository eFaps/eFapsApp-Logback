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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.util.EFapsException;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

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
     * Session key to store the Map in the Session;
     */
    public final String SESSION_KEY = "org.efaps.esjp.logback.Configuration";

    private static String LOGGERCONTEXT = "ch.qos.logback.classic.LoggerContext";

    private static final String CONTEXTINTERFACE = "ch.qos.logback.core.Context";

    private static final String JORANCONFIG = "ch.qos.logback.classic.joran.JoranConfigurator";

    private static final String LEVEL = "ch.qos.logback.classic.Level";

    /**
     * Updates the LogBack Configuration.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return new empty Return
     * @throws EFapsException on error
     */
    public Return update(final Parameter _parameter)
        throws EFapsException
    {
        @SuppressWarnings("unchecked") final Map<String, String> map = (Map<String, String>) Context.getThreadContext()
                        .getSessionAttribute(this.SESSION_KEY);
        final ILoggerFactory logContext = LoggerFactory.getILoggerFactory();
        if (logContext.getClass().getName().contains(Configuration_Base.LOGGERCONTEXT)) {
            try {
                final Method getLoggerList = logContext.getClass().getMethod("getLoggerList");
                final List<?> loggerList = (List<?>) getLoggerList.invoke(logContext);
                for (final Object logger : loggerList) {
                    final String value = _parameter.getParameterValue(map.get(logName(logger)));
                    if (value != null) {
                        if ("INHERITED".equalsIgnoreCase(value)) {
                            if (getLevel(logger) != null && !"ROOT".equals(logName(logger))) {
                                setLevel(logger, null);
                            }
                        } else {
                            if (getLevel(logger) == null) {
                                setLevel(logger, getLevel4Name(logger, value));
                            } else if (!value.equalsIgnoreCase(String.valueOf(getLevel(logger)))) {
                                setLevel(logger, getLevel4Name(logger, value));
                            }
                        }
                    }
                }
            } catch (final EFapsException e) {

            } catch (final NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        Context.getThreadContext().removeSessionAttribute(this.SESSION_KEY);
        return new Return();
    }

    /**
     * @param _parameter Parameter as passed from the eFaps API
     * @return Snipllet containgn list of loggers
     * @throws EFapsException on error
     */
    public Return loggersFieldValue(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();
        final ILoggerFactory logContext = LoggerFactory.getILoggerFactory();
        if (logContext.getClass().getName().contains(Configuration_Base.LOGGERCONTEXT)) {
            try {
                final Method getLoggerList = logContext.getClass().getMethod("getLoggerList");
                final List<?> loggerList = (List<?>) getLoggerList.invoke(logContext);

                final Map<String, String> map = new HashMap<String, String>();
                Context.getThreadContext().setSessionAttribute(this.SESSION_KEY, map);

                html.append("<table>")
                        .append("<tr><th>")
                        .append(DBProperties.getProperty("org.efaps.esjp.logback.Configuration.LoggerName"))
                        .append("</th><th>")
                        .append(DBProperties.getProperty("org.efaps.esjp.logback.Configuration.EffectiveLevel"))
                        .append("</th><th>")
                        .append(DBProperties.getProperty("org.efaps.esjp.logback.Configuration.Level"))
                        .append("</th><th>")
                        .append("</th></tr>");
                int i = 0;
                for (final Object logger : loggerList)
                {
                    final String name = logName(logger);
                    map.put(name, "log" + i);
                    html.append("<tr><td>").append(name).append("</td><td>")
                            .append(getEffectiveLevel(logger)).append("</td><td>")
                            .append(getLevelDropDown(logger, "log" + i)).append("</td></tr>");
                    i++;
                }
                html.append("</table>");
            } catch (final EFapsException e) {

            } catch (final NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            html.append(DBProperties.getProperty(Configuration.class.getName() + ".noLogback"));
        }
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }

    /**
     * Loads a LogBack Configuration file and parses it using the
     * JoranConfigurater from LogBack. Resets the Logging Context and configures
     * a new one from the given file.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return new empty Return
     * @throws EFapsException on error
     */
    public Return load(final Parameter _parameter)
        throws EFapsException
    {
        final Context context = Context.getThreadContext();
        final Context.FileParameter fileItem = context.getFileParameters().get("upload");
        try {
            final ILoggerFactory logContext = LoggerFactory.getILoggerFactory();
            if (logContext.getClass().getName().contains(Configuration_Base.LOGGERCONTEXT)) {
                final Class<?> logContextInter = Class.forName(Configuration_Base.CONTEXTINTERFACE);
                final Class<?> configurator = Class.forName(Configuration_Base.JORANCONFIG);
                final Object configInstance = configurator.newInstance();

                final Method method = configurator.getMethod("setContext", new Class[] { logContextInter });
                method.invoke(configInstance, logContext);
                // the context was probably already configured by default
                // configuration rules
                final Method reset = logContext.getClass().getMethod("reset");
                reset.invoke(logContext);

                final Method doConfigure = configurator.getMethod("doConfigure", new Class[] { InputStream.class });
                doConfigure.invoke(configInstance, fileItem.getInputStream());
            }
        } catch (final IOException e) {
            throw new EFapsException(this.getClass(), "load", e);
        } catch (final ClassNotFoundException e) {
            // TODO Auto-generated catch block
        } catch (final InstantiationException e) {
            // TODO Auto-generated catch block
        } catch (final IllegalAccessException e) {
            // TODO Auto-generated catch block
        } catch (final NoSuchMethodException e) {
            // TODO Auto-generated catch block
        } catch (final SecurityException e) {
            // TODO Auto-generated catch block
        } catch (final IllegalArgumentException e) {
            // TODO Auto-generated catch block
        } catch (final InvocationTargetException e) {
            // TODO Auto-generated catch block
        }
        return new Return();
    }

    /**
     * Get an Snipplet containinf a dropdown.
     *
     * @param _loggerLogger thee dropdown is wanted for
     * @return StringBuilder
     */
    protected StringBuilder getLevelDropDown(final Object _logger,
                                             final String _key)
        throws SecurityException, Exception
    {
        final StringBuilder ret = new StringBuilder();
        final Object level = getLevel(_logger);
        ret.append("<select name=\"").append(_key).append("\" size=\"1\">")
                        .append("<option ").append(level == null ? "selected=\"selected\"" : "")
                        .append(" value=\"").append("INHERITED").append("\">").append("INHERITED").append("</option>");

        appendOption(ret, level, getLevel4Name(_logger, "ALL"));
        appendOption(ret, level, getLevel4Name(_logger, "TRACE"));
        appendOption(ret, level, getLevel4Name(_logger, "DEBUG"));
        appendOption(ret, level, getLevel4Name(_logger, "INFO"));
        appendOption(ret, level, getLevel4Name(_logger, "WARN"));
        appendOption(ret, level, getLevel4Name(_logger, "ERROR"));
        appendOption(ret, level, getLevel4Name(_logger, "OFF"));

        ret.append("</select>");
        return ret;
    }

    /**
     * Append an option to the StringBuilder.
     *
     * @param _bldr StringBuilder to append to
     * @param _current current level
     * @param _target target level
     */
    protected void appendOption(final StringBuilder _bldr,
                                final Object _current,
                                final Object _target)
        throws Exception
    {
        _bldr.append("<option ")
                .append(_current != null && getLevelInt(_current) == getLevelInt(_target)
                        ? "selected=\"selected\"" : "")
                .append(" value=\"").append(_target).append("\">")
                .append(_target).append("</option>");
    }

    protected Object getEffectiveLevel(final Object _logger)
        throws Exception
    {
        final Method getEffectiveLevel = _logger.getClass().getMethod("getEffectiveLevel");
        return getEffectiveLevel.invoke(_logger);
    }

    protected String logName(final Object _logger)
        throws Exception
    {
        final Method getName = _logger.getClass().getMethod("getName");
        final String ret = (String) getName.invoke(_logger);
        return ret;
    }

    protected Object getLevel(final Object _logger)
        throws Exception
    {
        final Method getLevel = _logger.getClass().getMethod("getLevel");
        return getLevel.invoke(_logger);
    }

    protected void setLevel(final Object _logger,
                            final Object _level)
        throws Exception
    {
        Object temp;
        if (_level == null) {
            temp = getLevel(_logger);
        } else {
            temp = _level;
        }
        final Method setLevel = _logger.getClass().getMethod("setLevel", temp.getClass());
        setLevel.invoke(_logger, _level);
    }

    protected Object getLevel4Name(final Object _logger,
                                   final String _name)
        throws Exception, SecurityException
    {
        final Class<?> level = _logger.getClass().getClassLoader().loadClass(Configuration_Base.LEVEL);
        final Method toLevel = level.getMethod("toLevel", String.class);
        return toLevel.invoke(null, _name);
    }

    protected Integer getLevelInt(final Object _level)
        throws Exception
    {
        final Method toInteger = _level.getClass().getMethod("toInteger");
        return (Integer) toInteger.invoke(_level);
    }
}
