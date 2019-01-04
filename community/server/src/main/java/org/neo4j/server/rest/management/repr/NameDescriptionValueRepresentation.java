/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.rest.management.repr;

import org.neo4j.server.rest.repr.ObjectRepresentation;
import org.neo4j.server.rest.repr.Representation;
import org.neo4j.server.rest.repr.ValueRepresentation;

public class NameDescriptionValueRepresentation extends ObjectRepresentation
{
    private String name;
    private String description;
    private Representation value;

    public NameDescriptionValueRepresentation( String name, String description, Representation value )
    {
        super( "nameDescriptionValue" );
        this.name = name;
        this.description = description;
        this.value = value;
    }

    @Mapping( "name" )
    public ValueRepresentation getName()
    {
        return ValueRepresentation.string( name );
    }

    @Mapping( "description" )
    public ValueRepresentation getDescription()
    {
        return ValueRepresentation.string( description );
    }

    @Mapping( "value" )
    public Representation getValue()
    {
        return value;
    }

}
