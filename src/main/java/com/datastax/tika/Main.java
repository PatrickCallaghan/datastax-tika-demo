package com.datastax.tika;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.datastax.demo.utils.Timer;
import com.datastax.tika.model.MetadataObject;
import com.datastax.tika.service.MetadataService;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private List<String> keywords = Arrays.asList("architecture", "white paper");

	public Main() {

		// Examples of using variables passed in using -DcontactPoints
		MetadataService service = new MetadataService();

		logger.info("Paring Documents");

		Timer timer = new Timer();

		// Do something here.
		// For all docs

		List<File> files = listf("src/main/resources/files");
		

		
		for (File file : files) {
			if (file.isDirectory()){
				continue;
			}
			
			logger.info("Processing " + file.getAbsolutePath());
			try {
				MetadataObject metadata = service.processFile(file);
				service.insertMetadataObject(metadata);
			
			} catch (IOException | SAXException | TikaException e) {
				e.printStackTrace();
			}
		}

		timer.end();
		logger.info("Test took " + timer.getTimeTakenSeconds() + " secs.");
		System.exit(0);
	}

	public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        return resultList;
    } 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();

		System.exit(0);
	}

}
