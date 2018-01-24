/**
 * 
 */
package com.atal.print;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.atal.imap.GmailImap;
import com.atal.imap.PrintFeatures;

/**
 * @author tall008
 *
 */
public class PrintServiceCustom {

	static PrintFeatures printerNoirBlanc = new PrintFeatures();
	static PrintFeatures printerCouleur = new PrintFeatures();
	private static String archiveFolder = "D:/process/archives/";
	private static String binaryFolder = "D:/process/binary/";

	private static void print() throws Exception {

		while(true){
			List<PrintFeatures> doc2print = GmailImap.fetchEmails();
			for (PrintFeatures printFeatures : doc2print) {
				new MoveFile(binaryFolder + printFeatures.getDocumentName(),archiveFolder + printFeatures.getDocumentName());
				PrintingDocument printingDocument = new PrintingDocument(printFeatures.getDocumentName(),
						printFeatures.getPrinterName());
				System.out.println("PrintingDocument " + printingDocument.getResult());
			}
			System.out.println("Start Waiting 10 seconds");
			int i = 0;
			while (i<=10) {
				i++;
				TimeUnit.SECONDS.sleep(1);
				System.out.printf("===");
			}
			System.out.println("");
			System.out.println("End Waiting 10 seconds");
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			print();
		} catch (Exception e) {
			System.err.println("main Exception " + e);
		}

	}

}
