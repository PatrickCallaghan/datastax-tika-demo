DSE Document MetaData Search using Tika
================================================
This demo will load all the documents in a directory into DSE while extracting the metadata for indexing into DSE Search. 

To create the schema, run the following

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup" -DcontactPoints=localhost
	
To create the solr core to make our table searchable, run the following

	dsetool create_core tika.metadata generateResources=true
	
To run the document loader, run  
	
	mvn clean compile exec:java -Dexec.mainClass="com.datastax.tika.Main"  -DcontactPoints=localhost 
	
This will process all the files in the src/main/resources/files directory. 
	