/*
 * Copyright 2003 - 2017 The eFaps Team
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
 */


package org.efaps.esjp.logback.jersey;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Possibility to log Jersey.
 *
 * @author The eFaps Team
 */
@EFapsUUID("ec07f828-b787-42f7-a1b3-556dee0a3f4b")
@EFapsApplication("eFapsApp-Logback")
public class JerseyLogFilter
    implements ClientRequestFilter, ClientResponseFilter
{
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyLogFilter.class);

    /** Maximum size of request/response body in bytes that will be logged. */
    private static final int DEFAULT_MAX_BODY_SIZE = 20 * 1024;

    /** The m max body size. */
    private final int mMaxBodySize = DEFAULT_MAX_BODY_SIZE;

    /** The logger. */
    private Logger logger;

    @Override
    public void filter(final ClientRequestContext _requestContext)
        throws IOException
    {
        if (!isLoggingEnabled()) {
            return;
        }
        final String msg = String.format("Executing %s on %s, headers: %s, \nBody: %s", _requestContext.getMethod(),
                        _requestContext.getUri(), _requestContext.getStringHeaders(),
                        _requestContext.getEntity());
        logMessage(msg);
    }

    @Override
    public void filter(final ClientRequestContext _requestContext,
                       final ClientResponseContext _responseContext)
        throws IOException
    {
        if (!isLoggingEnabled()) {
            return;
        }
        final StringBuilder bodyMsg = new StringBuilder();
        if (_responseContext.hasEntity()) {
            _responseContext.setEntityStream(logMessageBodyInputStream(bodyMsg, _responseContext.getEntityStream()));
        }
        final String msg = String.format("Response status: %s %s,\nHeaders: %s, \nBody: %s",
                       _responseContext.getStatus(), _responseContext.getStatusInfo(),
                       _responseContext.getHeaders(), bodyMsg);
        logMessage(msg);
    }

    /**
     * Determines if the request having the supplied request context has a body
     * or not.
     *
     * @param _inRequestContext the in request context
     * @return True if request has a body, false otherwise.
     */
    protected boolean isRequestBodyless(final ClientRequestContext _inRequestContext)
    {
        final String theRequestHttpMethod = _inRequestContext.getMethod();
        final boolean theBodylessFlag = "GET".equals(theRequestHttpMethod)
                        || "DELETE".equals(theRequestHttpMethod)
                        || "HEAD".equals(theRequestHttpMethod);
        return theBodylessFlag;
    }

    /**
     * Logs the contents of the supplied input stream to the supplied string
     * builder, as to produce
     * a log message.
     * If the length of the data in the input stream exceeds the maximum body
     * size configured on the
     * logging filter then the exceeding data is not logged.
     *
     * @param _inLogMessageStringBuilder the in log message string builder
     * @param _inBodyInputStream the in body input stream
     * @return An input stream from which the message body can be read from.
     *         Will be the original input stream if it supports mark and reset.
     * @throws IOException If error occurs reading from input stream.
     */
    protected InputStream logMessageBodyInputStream(final StringBuilder _inLogMessageStringBuilder,
                                                    final InputStream _inBodyInputStream)
        throws IOException
    {
        InputStream theResultEntityStream = _inBodyInputStream;

        if (!_inBodyInputStream.markSupported()) {
            theResultEntityStream = new BufferedInputStream(_inBodyInputStream);
        }
        theResultEntityStream.mark(mMaxBodySize + 1);
        final byte[] theEntityBytes = new byte[mMaxBodySize + 1];
        final int theEntitySize = theResultEntityStream.read(theEntityBytes);
        _inLogMessageStringBuilder.append(new String(theEntityBytes, 0, Math.min(theEntitySize, mMaxBodySize)));
        if (theEntitySize > mMaxBodySize) {
            _inLogMessageStringBuilder.append(" [additional data truncated]");
        }
        theResultEntityStream.reset();
        return theResultEntityStream;
    }

    /**
     * Determines whether logging is enabled or not.
     * This method should be overridden by subclasses that wish to use some
     * other way of logging than Log4J.
     *
     * @return True if this logging filter is to log requests and responses,
     *         false otherwise.
     */
    protected boolean isLoggingEnabled()
    {
        return getLogger().isDebugEnabled() || getLogger().isTraceEnabled();
    }

    /**
     * Logs the supplied message.
     * This method should be overridden by subclasses that wish to use some
     * other way of logging than Log4J.
     *
     * @param _msg the msg
     */
    protected void logMessage(final String _msg)
    {
        getLogger().debug(_msg);
    }

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    public Logger getLogger()
    {
        final Logger ret;
        if (logger == null) {
            ret = LOGGER;
        } else {
            ret = logger;
        }
        return ret;
    }

    /**
     * Sets the logger.
     *
     * @param _logger the new logger
     * @return the jersey log filter
     */
    public JerseyLogFilter setLogger(final Logger _logger)
    {
        logger = _logger;
        return this;
    }
}
