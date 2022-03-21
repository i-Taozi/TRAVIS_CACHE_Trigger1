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
package com.speedment.runtime.core;

import com.speedment.runtime.config.Project;

/**
 * This interface defines how a Speedment application can obtain its
 * configuration meta data. For example, in a form of a json file
 *
 * @author  Per Minborg
 * @since   2.0.0
 */
public interface ApplicationMetadata {

    /**
     * Transforms the metadata into a {@link Project} document.
     * 
     * @return  the created project document.
     */
    Project makeProject();
}
