##calcite compile

$ mvn package -DskipTests -Dcheckstyle.skip=ture

##elasticsearch connention  using sqlline

$ ./sqlline

sqlline>  !connet jdbc:calcite:model=elasticsearch/target/test-classes/elasticsearch-model.json admin admin




