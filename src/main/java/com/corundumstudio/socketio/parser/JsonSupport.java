/**
 * Copyright 2012 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corundumstudio.socketio.parser;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * JSON infrastructure interface.
 * Allows to implement custom realizations
 * to JSON support operations.
 *
 */
public interface JsonSupport {

    AckArgs readAckArgs(ByteBufInputStream src, Class<?> argType) throws IOException;

    <T> T readValue(ByteBufInputStream src, Class<T> valueType) throws IOException;

    void writeValue(ByteBufOutputStream out, Object value) throws IOException;

    String writeValueAsString(Object value) throws IOException;

    <T> T readValue(String src, Class<T> valueType) throws IOException;

    void addEventMapping(String eventName, Class<?> eventClass);

    void addJsonClass(Class<?> clazz);

    void removeEventMapping(String eventName);

}
