/*
 * Copyright 2024 Thoughtworks, Inc.
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

package com.thoughtworks.go.config.preprocessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ParamStateMachineUnitTest {

    private ParamHandler handler;

    @BeforeEach
    public void setUp() throws Exception {
        handler = mock(ParamHandler.class);
    }

    @Test
    public void shouldClearPatternWhenFound() throws Exception {
        ParamStateMachine stateMachine = new ParamStateMachine();
        stateMachine.process("#{pattern}", handler);

        assertThat(ParamStateMachine.ReaderState.IN_PATTERN.pattern.length(), is(0));
        verify(handler).handlePatternFound(any(StringBuilder.class));
    }

    @Test
    public void shouldClearPatternWhenParameterCannotBeResolved() throws Exception {
        ParamStateMachine stateMachine = new ParamStateMachine();
        doThrow(new IllegalStateException()).when(handler).handlePatternFound(any(StringBuilder.class));

        try {
            stateMachine.process("#{pattern}", handler);
        } catch (Exception e) {
            //Ignore to assert on the pattern
        }
        assertThat(ParamStateMachine.ReaderState.IN_PATTERN.pattern.length(), is(0));
        verify(handler).handlePatternFound(any(StringBuilder.class));
    }

    @Test
    public void shouldHandleHashSeenStateWithAnotherHash() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("##", handler);

        verify(handler).handlePatternStarted('#');
    }

    @Test
    public void shouldHandleEmptyInput() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("", handler);

        verify(handler, never()).handlePatternStarted('#');
        verify(handler, never()).handleNotInPattern(anyChar());
        verify(handler, never()).handlePatternFound(any(StringBuilder.class));
        verify(handler).handleAfterResolution(ParamStateMachine.ReaderState.NOT_IN_PATTERN);
    }

    @Test
    public void shouldHandleInPatternStateWithCurlyBraceOpen() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("##{", handler);

        verify(handler).handlePatternStarted('#');
    }

    @Test
    public void shouldHandleNotInPatternStateWithNonHashChar() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("abc", handler);

        verify(handler, never()).handlePatternStarted('#');
        verify(handler, never()).handlePatternFound(any(StringBuilder.class));
        verify(handler).handleAfterResolution(ParamStateMachine.ReaderState.NOT_IN_PATTERN);
    }

    @Test
    public void shouldHandleInPatternStateWithCurlyBraceClose() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("#{pattern}", handler);

        verify(handler).handlePatternFound(any(StringBuilder.class));
    }

    @Test
    public void shouldHandleInvalidPatternState() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("#pattern#", handler);

        verify(handler, never()).handlePatternStarted('#');
        verify(handler, never()).handlePatternFound(any(StringBuilder.class));
        verify(handler).handleAfterResolution(ParamStateMachine.ReaderState.INVALID_PATTERN);
    }

    @Test
    public void shouldHandleInPatternStateWhenWithCurlyBraceOpen() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("#{pattern#", handler);

        verify(handler, never()).handlePatternStarted('#');
        verify(handler, never()).handlePatternFound(any(StringBuilder.class));
        verify(handler).handleAfterResolution(ParamStateMachine.ReaderState.IN_PATTERN);
    }

    @Test
    public void shouldHandleCompletePatternFollowedByCharsOutsidePattern() {
        ParamStateMachine stateMachine = new ParamStateMachine();

        stateMachine.process("#{pattern}abc", handler);

        verify(handler).handlePatternFound(any(StringBuilder.class));
        verify(handler).handleNotInPattern('a');
        verify(handler).handleNotInPattern('b');
        verify(handler).handleNotInPattern('c');
        verify(handler).handleAfterResolution(ParamStateMachine.ReaderState.NOT_IN_PATTERN);
    }
}