DSE Document MetaData Search using Tika
================================================
This demo will load all the documents in a directory into DSE while extracting the metadata for indexing into DSE Search. It will also look in the links.txt to search for links that it can index. If it finds a github repository, it will index the README.md file.

To create the schema, run the following

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup" -DcontactPoints=localhost
	
To create the solr core to make our table searchable, run the following

	dsetool create_core tika.metadata generateResources=true
	
To run the document loader, run  
	
	mvn clean compile exec:java -Dexec.mainClass="com.datastax.tika.Main"  -DcontactPoints=localhost 
	
This will process all the files in the src/main/resources/files directory. 

To query the data in DSE, use cqlsh and run

	select * from tika.metadata
	
You can use the solr admin ui at 

	http://localhost:8983/solr
	

