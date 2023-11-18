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

import team.unnamed.inject.util.Validate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Collection of static util methods for
 * ease the handling of {@link Throwable}s
 * and error messages.
 *
 * <p>We maintain this util class private because
 * it can change in any update, if a class is made
 * public, we need to maintain that class compatible
 * with old versions, that can limit the development
 * a bit.</p>
 */
final class Errors {

    private Errors() {
    }

    /**
     * Prints the stack trace of the specified {@code throwable}
     * to a {@link StringWriter} and returns the printed stack
     * trace.
     *
     * @param throwable The throwable
     * @return The throwable stack trace
     */
    static String getStackTrace(Throwable throwable) {
        Validate.notNull(throwable);
        // The StringWriter doesn't require a flush() or close()
        StringWriter writer = new StringWriter();
        // The PrintWriter just flushes the delegated writer
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    static String formatErrorMessages(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            builder.append("\n");
            builder.append(i + 1);
            builder.append(") ");
            builder.append(messages.get(i));
        }
        return builder.toString();
    }

}
