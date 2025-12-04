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
package org.efaps.esjp.logback.rest.dto;

import java.io.Serializable;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = LoggerDto.Builder.class)
@EFapsUUID("db87e680-c863-48df-a61e-32bdc3cbcf9e")
@EFapsApplication("eFapsApp-Logback")
public class LoggerDto implements Serializable
{

    private static final long serialVersionUID = 1L;
    private final String name;
    private final String appender;
    private final String level;
    private final String effectiveLevel;

    private LoggerDto(Builder builder)
    {
        this.name = builder.name;
        this.appender = builder.appender;
        this.level = builder.level;
        this.effectiveLevel = builder.effectiveLevel;
    }

    public String getName()
    {
        return name;
    }

    public String getLevel()
    {
        return level;
    }

    public String getEffectiveLevel()
    {
        return effectiveLevel;
    }

    public String getAppender()
    {
        return appender;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String name;
        private String appender;
        private String level;
        private String effectiveLevel;

        private Builder()
        {
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withAppender(String appender)
        {
            this.appender = appender;
            return this;
        }

        public Builder withLevel(String level)
        {
            this.level = level;
            return this;
        }

        public Builder withEffectiveLevel(String effectiveLevel)
        {
            this.effectiveLevel = effectiveLevel;
            return this;
        }

        public LoggerDto build()
        {
            return new LoggerDto(this);
        }
    }
}
