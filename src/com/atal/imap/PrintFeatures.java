package com.atal.imap;

public class PrintFeatures {

	private boolean couleur;
	private String documentName;
	
	public boolean isCouleur() {
		return couleur;
	}

	public String getPrinterName() {
		if(isCouleur()){
			return "Color Printer";
		}
		return "White and Black";
	}

	public void setCouleur(boolean couleur) {
		this.couleur = couleur;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}


}
