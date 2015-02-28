package com.epam.wilma.router.command;
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

import java.util.Map;

import com.epam.wilma.domain.stubconfig.StubDescriptor;

/**
 * Those operations which want to modify the stubDescriptors collection of {@link RoutingService} need to implement this interface.
 * According to the Command Pattern.
 * @author Tibor_Kovacs
 */
public interface StubDescriptorModificationCommand {

    /**
     * This is the operation.
     * @param stubDescriptors the actual collection
     * @return the modified collection
     */
    Map<String, StubDescriptor> modify(Map<String, StubDescriptor> stubDescriptors);

}
