/*
 * Copyright (c) 2002-2020 "Neo4j,"
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
package org.neo4j.cluster;

import java.util.concurrent.Executor;

import org.neo4j.cluster.com.message.MessageSender;
import org.neo4j.cluster.com.message.MessageSource;
import org.neo4j.cluster.protocol.atomicbroadcast.ObjectInputStreamFactory;
import org.neo4j.cluster.protocol.atomicbroadcast.ObjectOutputStreamFactory;
import org.neo4j.cluster.protocol.atomicbroadcast.multipaxos.AcceptorInstanceStore;
import org.neo4j.cluster.protocol.election.ElectionCredentialsProvider;
import org.neo4j.cluster.timeout.TimeoutStrategy;
import org.neo4j.kernel.configuration.Config;

/**
 * Factory for instantiating ProtocolServers.
 *
 * @see ProtocolServer
 */
public interface ProtocolServerFactory
{
    ProtocolServer newProtocolServer( InstanceId me, TimeoutStrategy timeouts, MessageSource input, MessageSender output,
                                      AcceptorInstanceStore acceptorInstanceStore,
                                      ElectionCredentialsProvider electionCredentialsProvider,
                                      Executor stateMachineExecutor,
                                      ObjectInputStreamFactory objectInputStreamFactory,
                                      ObjectOutputStreamFactory objectOutputStreamFactory,
                                      Config config );
}
