package com.atal.print;

import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 * printing
 * 
 * @author tall008
 *
 */
public class PrintingDocument {

	
	private String result;
	
	private static String archiveFolder = "D:/process/archives/";
	
    public PrintingDocument(String documentPdf, String printerName) {
        PDDocument document;
		try {
			File file = new File(archiveFolder + documentPdf);
			System.out.println(file.getName());
			document = PDDocument.load(file);
			PrintService myPrintService = findPrintService(printerName);
	        PrinterJob job = PrinterJob.getPrinterJob();
	        job.setPageable(new PDFPageable(document));
	        job.setPrintService(myPrintService);
	        job.print();
		} catch (Exception e) {
			System.err.println("PrintingDocument " + e);
			setResult("ko");
			return;
		}
		setResult("ok");
    }      
    
    /**
     * @param printerName
     * @return
     */
    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
