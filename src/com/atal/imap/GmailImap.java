package com.atal.imap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

public class GmailImap {

	private static final String EMAIL_ID = "youremail@gmail.com";
	private static final String PASSWORD = "yourpassword";
	private static String confFolder = "D:/process/conf/";
	private static String binaryFolder = "D:/process/binary/";
	
	
	/**
	 * read id
	 * 
	 * @return List<String>
	 * @throws FileNotFoundException exception
	 * @throws IOException exception
	 */
	private static List<String> readIds() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(confFolder + "ids.txt"))) {
			List<String> sb = new ArrayList<String>();
		    String line = br.readLine();
		    while (line != null) {
		    	sb.add(line);
		        line = br.readLine();
		    }
		    return sb;
		}
	}
	
	/**
	 * write id
	 * 
	 * @param lines lines
	 * @throws IOException exception
	 */
	private static void writeIds(List<String> lines) throws IOException {
		PrintWriter writer = new PrintWriter(confFolder + "ids.txt", "UTF-8");
		for (int i = 0; i < lines.size(); i++) {
			writer.println(lines.get(i));
		}
		writer.close();
	}

	
	
	public static List<PrintFeatures> fetchEmails() {
		
		List<PrintFeatures> printFeatures = new ArrayList<PrintFeatures>();
		// set properties
		Properties properties = new Properties();
		// You can use imap or imaps , *s -Secured
		properties.put("mail.store.protocol", "imaps");
		// Host Address of Your Mail
		properties.put("mail.imaps.host", "imap.gmail.com");
		// Port number of your Mail Host
		properties.put("mail.imaps.port", "993");

		// properties.put("mail.imaps.timeout", "10000");

		try {
			List<String> ids = readIds();
			List<String> idsnew = new ArrayList<String>();
			// create a session
			Session session = Session.getDefaultInstance(properties, null);
			// SET the store for IMAPS
			Store store = session.getStore("imaps");

			System.out.println("Connection initiated......");
			// Trying to connect IMAP server
			store.connect(EMAIL_ID, PASSWORD);
			System.out.println("Connection is ready :)");

			// Get inbox folder
			Folder inbox = store.getFolder("inbox");
			// SET read-only format (*You can set read and write)
			inbox.open(Folder.READ_ONLY);
			// Inbox email count
			int messageCount = inbox.getMessageCount();
			System.out.println("Total Messages in INBOX:- " + messageCount);

			for (int i = 1; i < messageCount+1; i++) {
				Message message = inbox.getMessage(i);
				Enumeration<Header> headers = message.getAllHeaders();
				while (headers.hasMoreElements()) {
				    Header header = (Header) headers.nextElement();
				    if(!ids.contains(header.getValue()) && "Message-ID".equals(header.getName())) {
				    	System.out.printf("%s: %s%n", header.getName(), header.getValue());
				    	idsnew.add(header.getValue());
				    	System.out.println("Mail Subject:- " + message.getSubject());
						System.out.println("Mail Content:- " + message.getContent().toString());
						String fileName = saveAttachments(message);
						if(!fileName.equals("")) {
							printFeatures.add(getPrintFeature(message.getSubject(), fileName));
						}
						System.out.println("------------------------------------------------------------");
				    } else if ("Message-ID".equals(header.getName())){
				    	System.out.println("Already exist "+ header.getValue());
				    }
				}
			}
			idsnew.addAll(ids);
			writeIds(idsnew);
			inbox.close(true);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return printFeatures;

	}
	
	
	/**
	 * create print feature
	 * 
	 * @param messageid id message
	 * @param subject sub
	 * @return PrintFeatures
	 */
	private static PrintFeatures getPrintFeature(String subject, String fileName) {
		PrintFeatures features = new PrintFeatures();
		features.setCouleur(false);
		if(subject.toLowerCase().contains("couleur")) {
			features.setCouleur(true);
		}
		features.setDocumentName(fileName);
		return features;
	}
	
	/**
	 * save document
	 * 
	 * @param msg
	 * @throws Exception
	 */
	private static String saveAttachments(Message msg) throws Exception {

		if (msg.getContent() instanceof Multipart) {
			Multipart multipart = (Multipart) msg.getContent();

			for (int i = 0; i < multipart.getCount(); i++) {
				Part part = multipart.getBodyPart(i);
				String disposition = part.getDisposition();

				if ((disposition != null) && ((disposition.equalsIgnoreCase(Part.ATTACHMENT)
						|| (disposition.equalsIgnoreCase(Part.INLINE))))) {
					MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
					String fileName = mimeBodyPart.getFileName();
					System.out.println("fileName " + fileName);
					File fileToSave = new File(binaryFolder + fileName);
					mimeBodyPart.saveFile(fileToSave);
					return fileName;
				}
			}
		}
		return "";
	}
}
