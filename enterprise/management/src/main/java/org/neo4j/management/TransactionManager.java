/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) with the
 * Commons Clause, as found in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package org.neo4j.management;

import org.neo4j.jmx.Description;
import org.neo4j.jmx.ManagementInterface;

@ManagementInterface( name = TransactionManager.NAME )
@Description( "Information about the Neo4j transaction manager" )
public interface TransactionManager
{
    String NAME = "Transactions";

    @Description( "The number of currently open transactions" )
    long getNumberOfOpenTransactions();

    @Description( "The highest number of transactions ever opened concurrently" )
    long getPeakNumberOfConcurrentTransactions();

    @Description( "The total number started transactions" )
    long getNumberOfOpenedTransactions();

    @Description( "The total number of committed transactions" )
    long getNumberOfCommittedTransactions();

    @Description( "The total number of rolled back transactions" )
    long getNumberOfRolledBackTransactions();

    @Description( "The id of the latest committed transaction" )
    long getLastCommittedTxId();
}
