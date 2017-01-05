/*==========================================================================
Copyright 2013-2017 EPAM Systems

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

import com.epam.wilma.domain.http.WilmaHttpRequest;
import com.epam.wilma.domain.http.WilmaHttpResponse;
import com.epam.wilma.domain.stubconfig.interceptor.RequestInterceptor;
import com.epam.wilma.domain.stubconfig.interceptor.ResponseInterceptor;
import com.epam.wilma.domain.stubconfig.parameter.ParameterList;

/**
 * Interceptor for both Requests and Responses, to disable message logging in case its body contains "SKIPLOG" text.
 */
public class IndividualMessageLoggingInterceptor implements RequestInterceptor, ResponseInterceptor {

    @Override
    public void onRequestReceive(final WilmaHttpRequest request, final ParameterList parameters) {
        if (request.getBody().contains("SKIPLOG")) {
            request.setLoggingEnabled(false);
        }
    }

    @Override
    public void onResponseReceive(final WilmaHttpResponse response, final ParameterList parameters) {
        if (response.getBody().contains("SKIPLOG")) {
            response.setLoggingEnabled(false);
        }
    }

}
