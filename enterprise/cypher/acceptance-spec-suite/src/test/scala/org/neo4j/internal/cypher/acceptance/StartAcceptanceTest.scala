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
package org.neo4j.internal.cypher.acceptance

import org.neo4j.cypher.{ExecutionEngineFunSuite, QueryStatisticsTestSupport}
import org.neo4j.internal.cypher.acceptance.CypherComparisonSupport.Configs

class StartAcceptanceTest extends ExecutionEngineFunSuite with QueryStatisticsTestSupport with CypherComparisonSupport {

  // START gets executed on legacy cypher, because it's deprecated and will be removes in 4.0. Therefore it is only
  // executable on the community interpreted runtime.
  val expectedToSucceed = Configs.CommunityInterpreted - Configs.Version3_3
  val expectedToSucceedNoCost = Configs.CommunityInterpreted - Configs.Cost3_1 - Configs.Cost2_3 - Configs.Version3_3

  test("START n=node:index(key = \"value\") RETURN n") {
    val node = createNode()
    graph.inTx {
      graph.index().forNodes("index").add(node, "key", "value")
    }

    val result = executeWith(expectedToSucceed, """START n=node:index(key = "value") RETURN n""").toList

    result should equal(List(Map("n"-> node)))
  }

  test("START n=node:index(\"key:value\") RETURN n") {
    val node = createNode()
    graph.inTx {
      graph.index().forNodes("index").add(node, "key", "value")
    }

    val result = executeWith(expectedToSucceed, """START n=node:index("key:value") RETURN n""").toList

    result should equal(List(Map("n"-> node)))
  }

  test("start + where") {
    val node = createNode(Map("prop" -> 42))
    val otherNode = createNode(Map("prop" -> 21))

    graph.inTx {
      graph.index().forNodes("index").add(node, "key", "value")
      graph.index().forNodes("index").add(otherNode, "key", "value")
    }

    val result = executeWith(expectedToSucceed, """START n=node:index("key:value") WHERE n.prop = 42 RETURN n""").toList

    result should equal(List(Map("n"-> node)))
  }

  test("Relationship explicit index") {
    val node = createNode(Map("prop" -> 42))
    val otherNode = createNode(Map("prop" -> 21))
    val relationship = relate(node, otherNode)

    graph.inTx {
      val relationshipIndex = graph.index().forRelationships("relIndex")
      relationshipIndex.add(relationship, "key", "value")
    }

    val query = "START r=relationship:relIndex('key:*') RETURN r"
    val result = executeWith(expectedToSucceedNoCost, query)

    result.toList should equal(List(Map("r"-> relationship)))
  }

  test("START n=node(0) RETURN n") {
    val node = createNode()
    createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node(0) RETURN n").toList

    result should equal(List(Map("n"-> node)))
  }

  test("START n=node({id}) RETURN n") {
    val node = createNode()
    createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node({id}) RETURN n",
      params = Map("id" -> node.getId)).toList

    result should equal(List(Map("n"-> node)))
  }

  test("START n=node(0,1) RETURN n") {
    val node1 = createNode()
    val node2 = createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node(0,1) RETURN n").toList

    result should equal(List(Map("n" -> node1), Map("n" -> node2)))
  }

  test("START n=node({id}) RETURN n, id-> [0,1]") {

    val node1 = createNode()
    val node2 = createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node({id}) RETURN n",
      params = Map("id" -> List(node1.getId, node2.getId))).toList

    result should equal(List(Map("n" -> node1), Map("n" -> node2)))
  }

  test("START n=node(0),m=node(1) RETURN n") {
    val node1 = createNode()
    val node2 = createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node(0),m=node(1) RETURN n,m").toList

    result should equal(List(Map("n"-> node1, "m" -> node2)))
  }

  test("START n=node({id1}),m=node({id2}) RETURN n") {
    val node1 = createNode()
    val node2 = createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node({id1}),m=node({id2}) RETURN n,m",
      params = Map("id1" -> node1.getId, "id2" -> node2.getId)).toList

    result should equal(List(Map("n"-> node1, "m" -> node2)))
  }

  test("START n=node(*) RETURN n") {
    val node1 = createNode()
    val node2 = createNode()
    val result = executeWith(expectedToSucceedNoCost, "START n=node(*) RETURN n").toList

    result should equal(List(Map("n"-> node1), Map("n" -> node2)))
  }

  test("START r=rel(0) RETURN r") {
    val rel = relate(createNode(), createNode()).getId
    val result = executeWith(expectedToSucceedNoCost, "START r=rel(0) RETURN id(r)").toList

    result should equal(List(Map("id(r)"-> rel)))
  }

  test("START r=rel({id}) RETURN r") {
    val rel = relate(createNode(), createNode()).getId
    relate(createNode(), createNode()).getId
    val result = executeWith(expectedToSucceedNoCost, "START r=rel({id}) RETURN id(r)",
      params = Map("id" -> rel)).toList

    result should equal(List(Map("id(r)"-> rel)))
  }

  test("START r=rel(0,1) RETURN r") {
    val rel1 = relate(createNode(), createNode()).getId
    val rel2 = relate(createNode(), createNode()).getId

    val result = executeWith(expectedToSucceedNoCost, "START r=rel(%d,%d) RETURN id(r)".format(rel1, rel2)).toList

    result should equal(List(Map("id(r)"-> rel1),Map("id(r)"-> rel2)))
  }

  test("START r=rel({id}) RETURN r, id -> [0,1]") {
    val rel1 = relate(createNode(), createNode()).getId
    val rel2 = relate(createNode(), createNode()).getId
    val result = executeWith(expectedToSucceedNoCost, "START r=rel({id}) RETURN id(r)",
      params = Map("id" -> List(rel1,rel2))).toList

    result should equal(List(Map("id(r)"-> rel1),Map("id(r)"-> rel2)))
  }

  test("START r=rel(0),rr=rel(1) RETURN r") {
    val rel1 = relate(createNode(), createNode()).getId
    val rel2 = relate(createNode(), createNode()).getId

    val result = executeWith(expectedToSucceedNoCost, "START r=rel(%d),rr=rel(%d) RETURN id(r), id(rr)".format(rel1, rel2)).toList

    result should equal(List(Map("id(r)"-> rel1, "id(rr)"-> rel2)))
  }

  test("START r=rel({id1}),rr=rel({id2}) RETURN r") {
    val rel1 = relate(createNode(), createNode()).getId
    val rel2 = relate(createNode(), createNode()).getId
    val result = executeWith(expectedToSucceedNoCost, "START r=rel({id1}),rr=rel({id2}) RETURN id(r), id(rr)",
      params = Map("id1" -> rel1, "id2" -> rel2)).toList

    result should equal(List(Map("id(r)"-> rel1, "id(rr)"-> rel2)))
  }

  test("START r=rel(*) RETURN r") {
    val rel1 = relate(createNode(), createNode()).getId
    val rel2 = relate(createNode(), createNode()).getId
    val result = executeWith(expectedToSucceedNoCost, "START r=rel(*) RETURN id(r)").toList

    result should equal(List(Map("id(r)"-> rel1),Map("id(r)"-> rel2)))
  }

  test("Relationship explicit index mk II") {
    val node = createNode(Map("prop" -> 42))
    val otherNode = createNode(Map("prop" -> 21))
    val relationship = relate(node, otherNode)

    graph.inTx {
      val relationshipIndex = graph.index().forRelationships("relIndex")
      relationshipIndex.add(relationship, "key", "value")
    }

    val query = "START r=relationship:relIndex('key:*') MATCH (a)-[r]-(b) RETURN r"
    val result = executeWith(expectedToSucceedNoCost, query)

    result.toList should equal(List(
      Map("r"-> relationship),
      Map("r"-> relationship)
    ))
  }

  test("Relationship explicit index mk III") {
    val node = createNode(Map("prop" -> 42))
    val otherNode = createNode(Map("prop" -> 21))
    val relationship = relate(node, otherNode)

    graph.inTx {
      val relationshipIndex = graph.index().forRelationships("relIndex")
      relationshipIndex.add(relationship, "key", "value")
    }

    val query = "START r=relationship:relIndex('key:*') MATCH (a)-[r]->(b) RETURN r"
    val result = executeWith(expectedToSucceedNoCost, query)

    result.toList should equal(List(
      Map("r"-> relationship)
    ))
  }

  test("Should return unique relationship on rel-id") {
    val a = createNode()
    val b = createNode()
    val c = createNode()
    val ab = relate(a, b)
    val ac = relate(a, c)
    val result = executeWith(expectedToSucceedNoCost,
      """start a=node(0), ab=relationship(0)
        |match (a)-[ab]->(b)
        |return b
      """.stripMargin)

    result.toList should equal(List(Map("b" -> b)))
  }

  test("Should return unique node on node-id") {
    val a = createNode()
    val b = createNode()
    val c = createNode()
    val ab = relate(a, b)
    val ac = relate(a, c)
    val result = executeWith(expectedToSucceedNoCost,
      """start a=node(0), b=node(1)
        |match (a)-[ab]->(b)
        |return b
      """.stripMargin)

    result.toList should equal(List(Map("b" -> b)))
  }

  test("Should return unique relationship on multiple rel-id") {
    val a = createNode()
    val b = createNode()
    val c = createNode()
    val d = createNode()
    val e = createNode()
    val ab = relate(a, b)
    val ac = relate(a, c)
    val bd = relate(b, d)
    val ce = relate(c, e)

    val result = executeWith(expectedToSucceedNoCost,
      """ start a = node(% d), ab = relationship(% d), bd = relationship(% d)
        |match (a)-[ab]->(b)-[bd]->(d)
        |return b, d
      """.format(a.getId, ab.getId, bd.getId).stripMargin)

    result.toList should equal(List(Map("b" -> b, "d" -> d)))
  }

  test("Should return unique relationship on rel-ids") {
    val a = createNode()
    val b = createNode()
    val c = createNode()
    val d = createNode()
    val ab = relate(a, b)
    val ac = relate(a, c)
    val ad = relate(a, d)

    val result = executeWith(expectedToSucceedNoCost,
      """ start a = node(% d), ab = relationship(% d, % d)
        |match (a)-[ab]->(b)
        |return b
      """.format(a.getId, ab.getId, ac.getId).stripMargin)

    result.toSet should equal(Set(Map("b" -> c), Map("b" -> b)))
  }

  test("should return correct results on combined node and relationship index starts") {
    val node = createNode()
    val resultNode = createNode()
    val rel = relate(node, resultNode)
    relate(node, createNode())

    graph.inTx {
      graph.index().forNodes("nodes").add(node, "key", "A")
      graph.index().forRelationships("rels").add(rel, "key", "B")
    }

    val result = innerExecuteDeprecated("START n=node:nodes(key = 'A'), r=rel:rels(key = 'B') MATCH (n)-[r]->(b) RETURN b", Map())
    result.toList should equal(List(Map("b" -> resultNode)))
  }

  test("Should not return deleted nodes from explicit index") {
    // setup: create index, add a node
    val node = createNode()
    graph.inTx {
      graph.index().forNodes("index").add(node, "key", "value")
    }

    // check if we find the node
    innerExecuteDeprecated("start n=node:index(key = 'value') return n", Map.empty).size should equal(1)

    // when
    graph.inTx {
      node.delete()
    }

    // nothing found in the index after deletion
    val result = executeWith(expectedToSucceed, "start n=node:index(key = 'value') return n")

    result.size should equal(0)
  }
}
