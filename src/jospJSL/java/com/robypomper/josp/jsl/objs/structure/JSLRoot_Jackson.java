/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

import java.util.Collection;


/**
 * JSON JODRoot's implementation.
 * <p>
 * This class allow to load an object structure from JSON string. To manage JSON
 * it use the Jackson library.
 * <p>
 * Basically it parse the root element with {@link JSLRoot} type and the for any
 * contains
 */
public class JSLRoot_Jackson extends AbsJSLRoot {

    // Class constants

    public static final String COMPONENTS = "components";


    // Constructor

    /**
     * Default constructor for Jackson parsing.
     * <p>
     * This constructor allow Jackson params injection to be used with
     * {@link com.fasterxml.jackson.databind.ObjectReader#readValue(String)}
     * method. Example:
     *
     * <code>
     * ObjectMapper objMapper = new ObjectMapper();
     *
     * InjectableValues.Std injectVars = new InjectableValues.Std();
     * injectVars.addValue(JODStructure.class, this);
     * injectVars.addValue(JODExecutorMngr.class, executorMngr);
     * objMapper.setInjectableValues(injectVars);
     *
     * return objMapper.readerFor(JODRoot_Jackson.class).readValue(structureStr);
     * </code>
     * <p>
     * This constructor is used by the <code>DefaultJSLRemoteObject#loadStructure(String)</code>
     * method.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     */
    @SuppressWarnings("JavadocReference")
    public JSLRoot_Jackson(@JacksonInject final JSLRemoteObject remoteObject) {
        super(remoteObject);
    }


    // Jackson properties

    /**
     * This method act as json property {@value #COMPONENTS}.
     * <p>
     * It set current root's sub components and start the structure
     * initialization chain for each sub component added.
     *
     * @param containsList the list containing the pairs name/comp's properties.
     */
    @JsonProperty(COMPONENTS)
    public void setComponents_Jackson(Collection<Object> containsList) throws JSLRemoteObject.ParsingException {
        try {
            setComponents(createFromContains(containsList));
        } catch (JSLRemoteObject.ComponentInitException e) {
            assert false;   // This is an implementation error
        }
    }

}
