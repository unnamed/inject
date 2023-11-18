/*
 * This file is part of inject, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.inject.error;

import team.unnamed.inject.Injector;

import java.util.List;

/**
 * Represents an object that can contain a collection of errors
 * and then, can report all attached errors.
 *
 * <p>The recollection of all generated errors while configuring
 * an {@link Injector}, instantiating a class, injecting fields or
 * methods is very important. We need to report all the errors,
 * not only the first (Uncaught exceptions breaks the execution)</p>
 */
public interface ErrorAttachable {

    /**
     * Stores all the {@code errorMessages} in this object
     *
     * @param errorMessages The reported error messages
     */
    void attach(String... errorMessages);

    /**
     * Attaches the specified error using the specified
     * reason/title/header.
     *
     * @param header The error header
     * @param error The error
     */
    void attach(String header, Throwable error);

    /**
     * Stores all the errors of the specified {@code attachable}
     * into this object
     *
     * @param attachable The error attachable
     */
    void attachAll(ErrorAttachable attachable);

    /**
     * Gets an immutable copy of all the attached
     * error messages.
     *
     * @return The attached error messages
     */
    List<String> getErrorMessages();

    /**
     * Applies a snapshot, memento design pattern
     *
     * @param errorMessages The error messages
     */
    void applySnapshot(List<String> errorMessages);

    /**
     * Formats the error messages in one message
     *
     * @return The formatted error messages
     */
    String formatMessages();

    /**
     * Returns the current error count
     *
     * @return The error count
     */
    int errorCount();

    /**
     * @return True if the errors has been attached
     * to this object.
     */
    boolean hasErrors();

    /**
     * Removes all the attached errors and reports it
     * to the user
     */
    void reportAttachedErrors();

}
