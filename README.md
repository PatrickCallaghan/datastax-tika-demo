DSE Document MetaData Search using Tika
================================================
This demo will load all the documents in a directory into DSE while extracting the metadata for indexing into DSE Search. It will also look in the links.txt to search for links that it can index. If it finds a github repository, it will index the README.md file.

To run this project you will need to have a DataStax Enterprise instance running in SearchAnalytics mode with DSEFS enabled. You may need to change the min_fee_space when you enable DSEFS if running locally - https://docs.datastax.com/en/dse/6.0/dse-dev/datastax_enterprise/analytics/enablingDsefs.html

To use DSEFS programmatically we need to add 2 jars to the maven project. 

The first is the byos (bring your own spark) libs that contain the dsefs implementation classes.
```
mvn install:install-file -Dfile=/Users/patrickcallaghan/dse-6.0.1/clients/dse-byos_2.11-6.0.1.jar -DgroupId=com.datastax -DartifactId=byos -Dversion=1.0 -Dpackaging=jar
```

And the other is the standard scala libs for the version of scala from the byos above - in this case, 2.11. 
```
mvn install:install-file -Dfile=/Users/patrickcallaghan/tools/scala-library/scala-library-2.11.8.jar  -DgroupId=com.datastax -DartifactId=scala-library -Dversion=1.0 -Dpackagine=jar
```

To create the schema, run the following

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup" -DcontactPoints=localhost
	
To create the solr core to make our table searchable, run the following

	dsetool create_core tika.metadata generateResources=true
	
To run the document loader, run the following with a file location of where you want to load files from. By default, it is will process all the files in the src/main/resources/files directory.
	
	mvn clean compile exec:java -Dexec.mainClass="com.datastax.tika.Main"  -DcontactPoints=localhost -DfileLocation=/tmp/files 

To query the data in DSE, use cqlsh and run

	select * from tika.metadata
	
You can use the solr admin ui at 

	http://localhost:8983/solr
	

