/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.elasticsearch;

import org.junit.Test;

import java.sql.*;
import java.util.Arrays;

/**
 * Tests for using Calcite with Spark as an internal engine, as implemented by
 * the {@link org.apache.calcite.adapter.spark} package.
 */
public class ElasticsearchAdapterTest {

    @Test
    public void testElasticSearch() {
        try {
            Class.forName("org.apache.calcite.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:calcite:model=/home/bylee/calcite-elasticsearch/elasticsearch/src/test/resources/elasticsearch-model.json");
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select * from \"testcreatetable\"");
            while (rs.next()) {
                System.out.println("Row: " + rs.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// End SparkAdapterTest.java
