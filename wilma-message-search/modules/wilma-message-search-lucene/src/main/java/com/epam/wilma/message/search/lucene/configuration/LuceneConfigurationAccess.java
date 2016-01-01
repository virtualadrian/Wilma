package com.epam.wilma.message.search.lucene.configuration;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.wilma.message.search.configuration.ConfigurationAccessBase;
import com.epam.wilma.message.search.properties.PropertyHolder;

/**
 * Configures the module with the necessary properties.
 * @author Tunde_Kovacs
 *
 */
@Component
public class LuceneConfigurationAccess implements ConfigurationAccessBase {

    private PropertyDto properties;

    @Autowired
    private PropertyHolder propertyHolder;

    /**
     * Returns a {@link PropertyDto} holding all module specific properties.
     * @return the propertiesDTO object
     */
    public PropertyDto getProperties() {
        return properties;
    }

    @Override
    public void loadProperties() {
        String indexPath = propertyHolder.get("lucene.index.folder");
        String messageDirectories = propertyHolder.get("message.folders");
        String reindexTimer = propertyHolder.get("lucene.reindex.cron");
        properties = new PropertyDto(indexPath, messageDirectories, reindexTimer);
    }
}
