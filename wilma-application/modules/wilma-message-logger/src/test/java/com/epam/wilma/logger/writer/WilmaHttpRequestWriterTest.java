package com.epam.wilma.logger.writer;
/*==========================================================================
Copyright 2013-2015 EPAM Systems

This file is part of Wilma.

Wilma is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Wilma is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wilma.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.epam.wilma.common.helper.LogFilePathProvider;
import com.epam.wilma.common.helper.WilmaConstants;
import com.epam.wilma.domain.http.WilmaHttpRequest;

/**
 * Provides unit tests for the {@link WilmaHttpRequestWriter} class.
 * @author Tunde_Kovacs
 */
public class WilmaHttpRequestWriterTest {

    private static final String TARGET_FOLDER = "messages";
    private static final String COULD_NOT_WRITE_MESSAGE_ERROR = "Could not write message to file:src/test/resources/outputFile.txt!";
    private static final String OUTPUT_FILE = "src/test/resources/outputFile.txt";
    private static final String REQUEST_TYPE = "req";
    private static final String TITLE = "201306271455.0001";
    private static final String REQUEST_LINE = "request line";
    private static final String REMOTE_ADDR = "remote.addr";
    private static final String HEADERS = "headers";
    private static final String BODY = "body";
    private static final String MESSAGE_ID = "201306271455.0001";
    private static final String FI_SUFFIX = "FI";
    private static final int OUTPUT_BUFFER_SIZE = 262144;

    @Mock
    private Logger logger;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WilmaHttpRequest request;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private LogFilePathProvider logFilePath;
    @Mock
    private BufferedWriter bufferedWriter;
    @Mock
    private BufferedWriterFactory bufferedWriterFactory;
    @Mock
    private File directory;
    @Mock
    private DirectoryFactory directoryFactory;

    @InjectMocks
    private WilmaHttpRequestWriter underTest;

    @BeforeMethod
    public void setUp() {
        underTest = spy(new WilmaHttpRequestWriter());
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "logger", logger);
    }

    @Test
    public void testWriteShouldWriteContentToWriter() throws IOException {
        //GIVEN
        doReturn(OUTPUT_FILE).when(underTest).getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        given(bufferedWriterFactory.createBufferedWriter(OUTPUT_FILE, OUTPUT_BUFFER_SIZE)).willReturn(bufferedWriter);
        given(request.getExtraHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        given(request.getRequestLine()).willReturn(REQUEST_LINE);
        given(request.getRemoteAddr()).willReturn(REMOTE_ADDR);
        given(request.getHeaders().toString()).willReturn(HEADERS);
        given(request.getBody()).willReturn(BODY);
        //WHEN
        underTest.write(request, true);
        //THEN
        verify(bufferedWriter).append(REMOTE_ADDR + " " + REQUEST_LINE);
        verify(bufferedWriter).append(WilmaConstants.WILMA_LOGGER_ID + ":" + MESSAGE_ID);
        verify(bufferedWriter).append(BODY);
    }

    @Test
    public void testWriteShouldNotAppendBodyWhenItIsNull() throws IOException {
        //GIVEN
        doReturn(OUTPUT_FILE).when(underTest).getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        given(bufferedWriterFactory.createBufferedWriter(OUTPUT_FILE, OUTPUT_BUFFER_SIZE)).willReturn(bufferedWriter);
        given(request.getBody()).willReturn(null);
        given(request.getExtraHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        given(request.getHeaders().toString()).willReturn(HEADERS);
        given(request.getRequestLine()).willReturn(REQUEST_LINE);
        given(request.getRemoteAddr()).willReturn(REMOTE_ADDR);
        //WHEN
        underTest.write(request, true);
        //THEN
        verify(bufferedWriter, never()).append(null);
    }

    @Test
    public void testWriteWhenBufferedWriterThrowsIOExceptionShouldLogError() throws IOException {
        //GIVEN
        IOException e = new IOException();
        doReturn(OUTPUT_FILE).when(underTest).getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        given(bufferedWriterFactory.createBufferedWriter(OUTPUT_FILE, OUTPUT_BUFFER_SIZE)).willThrow(e);
        given(request.getExtraHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        //WHEN
        underTest.write(request, true);
        //THEN
        verify(logger).error(COULD_NOT_WRITE_MESSAGE_ERROR, e);
    }

    @Test
    public void testWriteWhenCannotCloseFileShouldLogError() throws IOException {
        //GIVEN
        IOException e = new IOException();
        doReturn(OUTPUT_FILE).when(underTest).getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        given(bufferedWriterFactory.createBufferedWriter(OUTPUT_FILE, OUTPUT_BUFFER_SIZE)).willReturn(bufferedWriter);
        given(request.getExtraHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        willThrow(e).given(bufferedWriter).close();
        //WHEN
        underTest.write(request, true);
        //THEN
        verify(logger).error(COULD_NOT_WRITE_MESSAGE_ERROR, e);
    }

    @Test
    public void testWriteWhenWriterIsNullShouldDoNothing() throws IOException {
        //GIVEN
        doReturn(OUTPUT_FILE).when(underTest).getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        given(bufferedWriterFactory.createBufferedWriter(OUTPUT_FILE, OUTPUT_BUFFER_SIZE)).willReturn(null);
        given(request.getExtraHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        //WHEN
        underTest.write(request, true);
        //THEN
        verify(bufferedWriter, times(0)).append(Mockito.anyString());
    }

    @Test
    public void testGetOutpuFileNameShouldReturnCorrectNameWhenBodyIsDecompressed() {
        //GIVEN
        String expected = TARGET_FOLDER + "//" + TITLE + REQUEST_TYPE + ".txt";
        given(logFilePath.getLogFilePath().toAbsolutePath().toString()).willReturn(TARGET_FOLDER);
        given(request.getHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        given(directoryFactory.createNewDirectory(TARGET_FOLDER)).willReturn(directory);
        given(directory.exists()).willReturn(true);
        //WHEN
        String actual = underTest.getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        //THEN
        assertEquals(actual, expected);
        verify(directory, times(0)).mkdir();
    }

    @Test
    public void testGetOutpuFileNameShouldReturnCorrectNameWhenBodyIsNotDecompressed() {
        //GIVEN
        String expected = TARGET_FOLDER + "//" + TITLE + REQUEST_TYPE + FI_SUFFIX + ".txt";
        given(logFilePath.getLogFilePath().toAbsolutePath().toString()).willReturn(TARGET_FOLDER);
        given(request.getHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        given(directoryFactory.createNewDirectory(TARGET_FOLDER)).willReturn(directory);
        given(directory.exists()).willReturn(true);
        //WHEN
        String actual = underTest.getOutputFileName(REQUEST_TYPE, MESSAGE_ID, false);
        //THEN
        assertEquals(actual, expected);
        verify(directory, times(0)).mkdir();
    }

    @Test
    public void testGetOutpuFileNameShouldMakeDirIfDoesNotExist() {
        //GIVEN
        String expected = TARGET_FOLDER + "//" + TITLE + REQUEST_TYPE + ".txt";
        given(logFilePath.getLogFilePath().toAbsolutePath().toString()).willReturn(TARGET_FOLDER);
        given(request.getHeader(WilmaConstants.WILMA_LOGGER_ID.getConstant())).willReturn(MESSAGE_ID);
        given(directoryFactory.createNewDirectory(TARGET_FOLDER)).willReturn(directory);
        given(directory.exists()).willReturn(false);
        //WHEN
        String actual = underTest.getOutputFileName(REQUEST_TYPE, MESSAGE_ID, true);
        //THEN
        assertEquals(actual, expected);
        verify(directory, times(1)).mkdir();
    }

}