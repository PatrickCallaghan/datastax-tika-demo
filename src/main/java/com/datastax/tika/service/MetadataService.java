package com.datastax.tika.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.tika.dao.MetadataDao;
import com.datastax.tika.model.MetadataObject;

public class MetadataService {
	private static Logger logger = LoggerFactory.getLogger(MetadataService.class);
	private MetadataDao dao;
    private FileSystemOperations ops = new FileSystemOperations();

	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private String startLocation;

	public MetadataService(String startLocation) {		
		String contactPointsStr = PropertyHelper.getProperty("contactPoints", "localhost");
		this.dao = new MetadataDao(contactPointsStr.split(","));
		this.startLocation = startLocation;
	}	
	
	public List<KeyspaceMetadata> getKeyspaces() {
		return dao.getKeyspaces();
	}

	public MetadataObject processFile(File file) throws IOException, SAXException, TikaException {
		
		BodyContentHandler handler = new BodyContentHandler(-1);	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    MetadataObject metadataObject = new MetadataObject();
	    
	    try (InputStream stream = FileUtils.openInputStream(file)) {
	        parser.parse(stream, handler, metadata);
	        	        
	        metadataObject.setLastModified(metadata.getDate(Metadata.LAST_MODIFIED));
	        metadataObject.setContentType(metadata.get(Metadata.CONTENT_TYPE));
	        metadataObject.setCreatedDate(metadata.getDate(Metadata.CREATION_DATE));
	        metadataObject.setDocumentId(UUID.randomUUID().toString());
	        metadataObject.setVersion(Double.parseDouble(metadata.get("pdf:PDFVersion") != null ? metadata.get("pdf:PDFVersion") : "0"));
	        metadataObject.setContent(handler.toString());
	        metadataObject.setLink(file.getAbsolutePath());
	        metadataObject.setType("File");
	        
	        Map<String, String> metadataMap = new HashMap<String,String>();
	        for (String name : metadata.names()){
	        	metadataMap.put("md_" + name, metadata.get(name));   		        	
	        }
	        metadataObject.setMetadataMap(metadataMap);
	        logger.info(metadata.toString());
	    }	
	    
	    return metadataObject;
	}
	
	public MetadataObject processLink(URL url) throws IOException, SAXException, TikaException, URISyntaxException {
		
		BodyContentHandler handler = new BodyContentHandler(-1);	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    MetadataObject metadataObject = new MetadataObject();
	    URL originalURL = url;
	    
	    if (url.toString().contains("https://github.com/")){
	    	String newUrl = url.toString().replaceAll("https://github.com/", "https://raw.githubusercontent.com/").concat("/master/README.md");
	    	url = new URL(newUrl);
	    }
	    
	    logger.info("Opening Stream to " + url.toString());
	    
	    try (InputStream stream = url.openStream()) {
	        parser.parse(stream, handler, metadata);
	        	        
	        metadataObject.setLastModified(metadata.getDate(Metadata.LAST_MODIFIED));
	        metadataObject.setContentType(metadata.get(Metadata.CONTENT_TYPE));
	        metadataObject.setCreatedDate(metadata.getDate(Metadata.CREATION_DATE));
	        metadataObject.setDocumentId(UUID.randomUUID().toString());
	        metadataObject.setVersion(Double.parseDouble(metadata.get("pdf:PDFVersion") != null ? metadata.get("pdf:PDFVersion") : "0"));
	        metadataObject.setContent(handler.toString());
	        metadataObject.setLink(originalURL.toURI().toString());
	        metadataObject.setType("URL");
	        
	        Map<String, String> metadataMap = new HashMap<String,String>();
	        for (String name : metadata.names()){
	        	metadataMap.put("md_" + name, metadata.get(name));   		        	
	        }
	        metadataObject.setMetadataMap(metadataMap);
	        logger.info(metadata.toString());
	    }	
	    
	    return metadataObject;
	}
	
	public void sendFile(File source, MetadataObject metadata) {
		
	    	    
	    try {
			ops.addFile(source.getAbsolutePath(), source.getAbsolutePath().substring(this.startLocation.length()), getConf());
			metadata.setLink(source.getAbsolutePath().substring(this.startLocation.length() + 1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void insertMetadataObject(MetadataObject metadata){		
		dao.saveMetadataObject(metadata);
	}

	public void mkdir(File file) {
		try {
			ops.mkdir(file.getAbsolutePath().substring(this.startLocation.length()), getConf());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Configuration getConf(){
	    String hdfsPath = "dsefs://localhost:5598";

	    Configuration conf = new Configuration();
	    conf.set("fs.defaultFS", hdfsPath);
	    conf.set("fs.dsefs.impl", "com.datastax.bdp.fs.hadoop.DseFileSystem");

	    return conf;
	}
}
