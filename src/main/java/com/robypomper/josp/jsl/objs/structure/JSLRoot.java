/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
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
 ******************************************************************************/

package com.robypomper.josp.jsl.objs.structure;


/**
 * Base root component representation.
 * <p>
 * The root component is a special container that add specific properties related
 * to the object they belong to.
 */
public interface JSLRoot extends JSLContainer {

    // Root's properties

    /**
     * Object's model is defined by object maker.
     *
     * @return the object's model.
     */
    String getModel();

    /**
     * Object's maker name.
     *
     * @return the object's brand.
     */
    String getBrand();

    /**
     * Object's 2nd description.
     * <p>
     * This description help end-user to understand what the object is and what
     * it can do for them.
     *
     * @return the object's long description.
     */
    String getDescr_long();

}
