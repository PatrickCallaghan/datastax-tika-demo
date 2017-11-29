package com.datastax.tika;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.datastax.demo.utils.Timer;
import com.datastax.tika.model.MetadataObject;
import com.datastax.tika.service.MetadataService;

public class ProcessFile {

	private static Logger logger = LoggerFactory.getLogger(ProcessFile.class);

	public ProcessFile(String filename) {

		// Examples of using variables passed in using -DcontactPoints
		MetadataService service = new MetadataService();

		logger.info("Paring Documents");
		File file = new File(filename);

		Timer timer = new Timer();

		logger.info("Processing " + file.getAbsolutePath());
		try {
			MetadataObject metadata = service.processFile(file);
			//service.sendFile(file);
			service.insertMetadataObject(metadata);

		} catch (IOException | SAXException | TikaException e) {
			e.printStackTrace();
		}
		timer.end();
		logger.info("Test took " + timer.getTimeTakenSeconds() + " secs.");
		System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ProcessFile(args[0]);

		System.exit(0);
	}

}
