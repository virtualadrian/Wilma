package com.epam.wilma.webapp.config.servlet.sequence;
/*==========================================================================
Copyright 2013-2016 EPAM Systems

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
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.epam.wilma.core.toggle.sequence.SequenceHandlingToggle;

/**
 * Unit test for {@link SequenceHandlingStatusServlet}.
 * @author Adam_Csaba_Kiraly
 *
 */
public class SequenceHandlingStatusServletTest {

    @Mock
    private SequenceHandlingToggle sequenceHandlingToggle;

    @InjectMocks
    private SequenceHandlingStatusServlet underTest;

    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private PrintWriter printWriter;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDoGetShouldWriteJsonMessage() throws ServletException, IOException {
        //GIVEN
        given(resp.getWriter()).willReturn(printWriter);
        given(sequenceHandlingToggle.isOn()).willReturn(true);
        //WHEN
        underTest.doGet(req, resp);
        //THEN
        verify(resp).setContentType("application/json");
        verify(printWriter).write("{\"sequenceHandlingUsage\": true}");
        verify(printWriter).flush();
        verify(printWriter).close();
    }

    @Test
    public void testDoPostShouldCallDoGet() throws ServletException, IOException {
        //GIVEN
        given(resp.getWriter()).willReturn(printWriter);
        given(sequenceHandlingToggle.isOn()).willReturn(true);
        //WHEN
        underTest.doPost(req, resp);
        //THEN
        verify(resp).setContentType("application/json");
        verify(printWriter).write("{\"sequenceHandlingUsage\": true}");
        verify(printWriter).flush();
        verify(printWriter).close();
    }

}
