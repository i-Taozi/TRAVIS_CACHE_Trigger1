/*
 *
 * Copyright (c) 2006-2020, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.config.trait;

public final class HasTypeMapperUtil {

    private HasTypeMapperUtil() {}

    /**
     * The attribute under which the name of the {@code TypeMapper} is stored in
     * the configuration file.
     */
    public static final String TYPE_MAPPER = "typeMapper";
    /**
     * The attribute under which the database type is stored in the
     * configuration file. This is used initially to determine the default
     * {@code TypeMapper} to use.
     */
    public static final String DATABASE_TYPE  = "databaseType";

}
