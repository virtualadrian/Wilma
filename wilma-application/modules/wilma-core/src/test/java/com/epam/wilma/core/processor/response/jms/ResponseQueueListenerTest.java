package com.epam.wilma.core.processor.response.jms;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.epam.wilma.core.processor.entity.MessageExtractor;
import com.epam.wilma.domain.exception.ApplicationException;
import com.epam.wilma.domain.exception.SystemException;
import com.epam.wilma.domain.http.WilmaHttpResponse;
import com.epam.wilma.sequence.SequenceManager;

/**
 * Provides unit tests for the {@link ResponseQueueListener} class.
 * @author Tunde_Kovacs
 *
 */
public class ResponseQueueListenerTest {

    @Mock
    private MessageExtractor messageExtractor;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private Queue loggerQueue;
    @Mock
    private WilmaHttpResponse response;
    @Mock
    private JmsResponseMessageCreator jmsResponseMessageCreator;
    @Mock
    private JmsResponseMessageCreatorFactory messageCreatorFactory;
    @Mock
    private ObjectMessage objectMessage;
    @Mock
    private Message message;
    @Mock
    private SequenceManager manager;

    @InjectMocks
    private ResponseQueueListener underTest;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        given(messageCreatorFactory.create(response, true)).willReturn(jmsResponseMessageCreator);
        given(messageCreatorFactory.create(response, false)).willReturn(jmsResponseMessageCreator);
    }

    @Test
    public void testOnMessageShouldDecompressResponse() throws ApplicationException, JMSException {
        //GIVEN
        underTest.setFiDecompressionEnabled(true);
        given(objectMessage.getObject()).willReturn(response);
        //WHEN
        underTest.onMessage(objectMessage);
        //THEN
        verify(messageExtractor).extract(response);
    }

    @Test
    public void testOnMessageShouldNotDecompressResponseWhenFISafeguardEnabled() throws ApplicationException, JMSException {
        //GIVEN
        underTest.setFiDecompressionEnabled(false);
        given(objectMessage.getObject()).willReturn(response);
        //WHEN
        underTest.onMessage(objectMessage);
        //THEN
        verify(messageExtractor, never()).extract(response);
    }

    @Test
    public void testOnMessageShouldNotDecompressBase64WhenFISafeguardEnabled() throws ApplicationException, JMSException {
        //GIVEN
        underTest.setFiDecompressionEnabled(false);
        given(objectMessage.getObject()).willReturn(response);
        //WHEN
        underTest.onMessage(objectMessage);
        //THEN
        verify(messageExtractor, never()).extract(response);
    }

    @Test
    public void testOnMessageShouldSendResponseToLoggerQueue() throws JMSException {
        //GIVEN
        given(objectMessage.getObject()).willReturn(response);
        //WHEN
        underTest.onMessage(objectMessage);
        //THEN
        verify(jmsTemplate).send(loggerQueue, jmsResponseMessageCreator);
    }

    @Test(expectedExceptions = SystemException.class)
    public void testOnMessageShouldThrowNewRuntimeExcpetionWhenCannotGetWilmaResponseFromMessage() throws JMSException {
        //GIVEN
        given(objectMessage.getObject()).willThrow(new JMSException("exception"));
        //WHEN
        underTest.onMessage(objectMessage);
        //THEN exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnMessageWhenMessageIsNotObjectMessageShouldThrowIllegalArgumentException() {
        //GIVEN in setUp
        //WHEN
        underTest.onMessage(message);
        //THEN it should throw exception
    }

    @Test
    public void testOnMessageShouldCallSequenceManagarToSaveTheResponse() throws JMSException {
        //GIVEN
        given(objectMessage.getObject()).willReturn(response);
        //WHEN
        underTest.onMessage(objectMessage);
        //THEN
        verify(manager).tryToSaveResponseIntoSequence(response);
    }

}
