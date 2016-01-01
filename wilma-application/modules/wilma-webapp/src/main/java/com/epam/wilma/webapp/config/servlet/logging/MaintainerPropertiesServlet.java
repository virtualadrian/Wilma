package com.epam.wilma.webapp.config.servlet.logging;
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.wilma.webapp.config.servlet.helper.MaintainerPropertiesJsonBuilder;
import com.epam.wilma.webapp.configuration.WebAppConfigurationAccess;
import com.epam.wilma.webapp.configuration.domain.MaintainerProperties;
import com.epam.wilma.webapp.configuration.domain.PropertyDTO;


/**
 * This servlet provides the message file maintainer properties as a JSON message.
 * @author Marton_Sereg
 *
 */
@Component
public class MaintainerPropertiesServlet extends HttpServlet {

    private static final String APPLICATION_JSON = "application/json";
    private MaintainerProperties maintainerProperties;

    @Autowired
    private MaintainerPropertiesJsonBuilder maintainerPropertiesJsonBuilder;
    @Autowired
    private WebAppConfigurationAccess configurationAccess;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(APPLICATION_JSON);
        PrintWriter out = resp.getWriter();
        getMaintainerProperties();
        out.write(maintainerPropertiesJsonBuilder.buildMaintainerPropertiesJson(maintainerProperties));
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void getMaintainerProperties() {
        if (maintainerProperties == null) {
            PropertyDTO properties = configurationAccess.getProperties();
            maintainerProperties = properties.getMaintainerProperties();
        }
    }

}
