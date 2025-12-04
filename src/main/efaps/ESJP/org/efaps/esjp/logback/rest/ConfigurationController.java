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
package org.efaps.esjp.logback.rest;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsListener;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.cluster.ClusterCommunication;
import org.efaps.cluster.IClusterMsgListener;
import org.efaps.cluster.StreamableWrapper;
import org.efaps.esjp.logback.rest.dto.LoggerDto;
import org.efaps.util.EFapsException;
import org.jgroups.JChannel;
import org.jgroups.ObjectMessage;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("b288b652-30e7-4eaa-b51e-3ffbbf93281f")
@EFapsApplication("eFapsApp-Logback")
@Path("/logback/configuration")
@EFapsListener
public class ConfigurationController
    implements IClusterMsgListener
{

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationController.class);
    private static String LOGGERCONTEXT = "ch.qos.logback.classic.LoggerContext";
    private static final String LEVEL = "ch.qos.logback.classic.Level";

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getLoggers()
        throws EFapsException
    {
        final ILoggerFactory logContext = LoggerFactory.getILoggerFactory();
        final List<LoggerDto> loggers = new ArrayList<>();
        if (logContext.getClass().getName().contains(LOGGERCONTEXT)) {
            try {
                final Method getLoggerList = logContext.getClass().getMethod("getLoggerList");
                final List<?> loggerList = (List<?>) getLoggerList.invoke(logContext);
                for (final Object logger : loggerList) {
                    final var name = logName(logger);
                    final var effectivelevel = String.valueOf(getEffectiveLevel(logger));
                    final var level = getLevel(logger);
                    final var appender = getAppenderName(logger);
                    loggers.add(LoggerDto.builder()
                                    .withName(name)
                                    .withLevel(level == null ? null : String.valueOf(level))
                                    .withEffectiveLevel(effectivelevel)
                                    .withAppender(appender)
                                    .build());
                }
            } catch (final Exception e) {
                LOG.error("Catched", e);
            }
        }
        return Response.ok(loggers).build();
    }

    @PUT
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response updateLoggers(final List<LoggerDto> loggers)
        throws EFapsException
    {
        for (final var dto : loggers) {
            try {
                final var logger = getLogger(dto.getName());
                if (logger != null) {
                    if (StringUtils.isEmpty(dto.getLevel())) {
                        setLevel(logger, null);
                    } else {
                        setLevel(logger, getLevel4Name(logger, dto.getLevel()));
                    }
                    inform(dto);
                }
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return Response.ok().build();
    }

    protected Object getLogger(final String name)
        throws Exception
    {
        final ILoggerFactory logContext = LoggerFactory.getILoggerFactory();
        final Method exists = logContext.getClass().getMethod("exists", String.class);
        return exists.invoke(logContext, name);
    }

    protected String getAppenderName(final Object _logger)
        throws Exception
    {
        final StringBuilder ret = new StringBuilder();
        final Method iteratorForAppenders = _logger.getClass().getMethod("iteratorForAppenders");
        final Iterator<?> iter = (Iterator<?>) iteratorForAppenders.invoke(_logger);
        while (iter.hasNext()) {
            final Object appender = iter.next();
            final Method getName = appender.getClass().getMethod("getName");
            ret.append(getName.invoke(appender));
        }
        return ret.toString();
    }

    protected Object getLevel(final Object logger)
        throws Exception
    {
        final Method getLevel = logger.getClass().getMethod("getLevel");
        return getLevel.invoke(logger);
    }

    protected Object getEffectiveLevel(final Object logger)
        throws Exception
    {
        final Method getEffectiveLevel = logger.getClass().getMethod("getEffectiveLevel");
        return getEffectiveLevel.invoke(logger);
    }

    protected String logName(final Object logger)
        throws Exception
    {
        final Method getName = logger.getClass().getMethod("getName");
        final String ret = (String) getName.invoke(logger);
        return ret;
    }

    protected Object getLevel4Name(final Object _logger,
                                   final String _name)
        throws Exception, SecurityException
    {
        final Class<?> level = _logger.getClass().getClassLoader().loadClass(LEVEL);
        final Method toLevel = level.getMethod("toLevel", String.class);
        return toLevel.invoke(null, _name);
    }

    protected void setLevel(final Object logger,
                            final Object level)
        throws Exception
    {
        Object temp;
        if (level == null) {
            temp = getLevel(logger);
        } else {
            temp = level;
        }
        final Method setLevel = logger.getClass().getMethod("setLevel", temp.getClass());
        setLevel.invoke(logger, level);
    }

    protected void inform(final LoggerDto dto)
        throws Exception
    {
        final JChannel channel = ClusterCommunication.getChannel();
        if (channel != null) {
            final var msg = new ObjectMessage();

            final var object = new StreamableWrapper(dto);
            msg.setObject(object);
            channel.send(msg);
        }
    }

    @Override
    public int getWeight()
    {
        return 0;
    }

    @Override
    public boolean onPayload(final Serializable obj)
    {
        LOG.info("payload: {}", obj);
        if (obj instanceof final LoggerDto dto) {
            LOG.info("received loggerDto: {}", dto);
            try {
                final var logger = getLogger(dto.getName());
                if (logger != null) {
                    if (StringUtils.isEmpty(dto.getLevel())) {
                        setLevel(logger, null);
                    } else {
                        setLevel(logger, getLevel4Name(logger, dto.getLevel()));
                    }
                }
            } catch (final Exception e) {
                LOG.error("Catched", e);
            }
            return true;
        }
        return false;
    }
}
