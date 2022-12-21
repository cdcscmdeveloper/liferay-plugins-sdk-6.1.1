/*package my.ep.web.util.template;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.aspose.pdf.kit.PdfFileEditor;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.GifImage;
import com.lowagie.text.pdf.codec.TiffImage;







*//**
 * Copyright � 2012 Archisoft. All rights reserved.
*//*

*//**
 * * Merge multiple documents into one document with indent.
 *//*

public class PDFGenerator {
	
	private static Logger log = Logger.getLogger(PDFGenerator.class);
	 
	public static void generatePDF(String value) {
		 
//		 StringBuffer buf = new StringBuffer();
//		 buf.append("<html>");
		    
		    // put in some style
//		    buf.append("<head><style language='text/css'>");
//		    buf.append("h3 { border: 1px solid #aaaaff; background: #ccccff; ");
//		    buf.append("padding: 1em; text-transform: capitalize; font-family: sansserif; font-weight: normal;}");
//		    buf.append("p { margin: 1em 1em 4em 3em; } p:first-letter { color: red; font-size: 150%; }");
//		    buf.append("h2 { background: #5555ff; color: white; border: 10px solid black; padding: 3em; font-size: 200%; }");
//		    buf.append("</style></head>");
		    
		    
		    // generate the body
//		    buf.append("<body>");
//		    buf.append("<p><img src='samples/100bottles.jpg'/></p>");
//		    for(int i=99; i>0; i--) {
//		        buf.append("<h3>"+i+" bottles of beer on the wall, "
//		                + i + " bottles of beer!</h3>");
//		        buf.append("<p>Take one down and pass it around, "
//		                + (i-1) + " bottles of beer on the wall</p>\n");
//		    }
//		    buf.append("<h2>No more bottles of beer on the wall, no more bottles of beer. ");
//		    buf.append("Go to the store and buy some more, 99 bottles of beer on the wall.</h2>");
//		    buf.append("</body>");
//		    buf.append("</html>");
		    
		    try{
		    
		    // parse the markup into an xml Document
		    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    Document doc = builder.parse(new StringBufferInputStream(value));
		    
		    ITextRenderer renderer = new ITextRenderer();
		    renderer.setDocument(doc, null);
		    
		    String outputFile = "E:/ConvertedPDF.pdf";
		    OutputStream os = new FileOutputStream(outputFile);
		    renderer.layout();
		    renderer.createPDF(os);
		    os.close();
		    System.out.println("===ended==");
		}
		    catch (Exception e)
	    	{
		    	log.error("convert PDF - " + e.getMessage());
	    	}
	 }
	
	//PDFBOX free
	public static void execute()
    {
        try
        {	
        	PDDocument document = new PDDocument();
        	PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
        	document.addPage(page); 
        	PDPageContentStream contentStream = new PDPageContentStream(document, page);
        	
        	InputStream in = new FileInputStream(new File("E:/draft4_login.jpg"));
        	PDXObjectImage img = null;
//        	img = new PDJpeg(document, new FileInputStream("C:/Users/admin/Downloads/downloaded_optimus.jpg"));
        	img = new PDJpeg(document, in);
        	contentStream.drawXObject(img, 0, 0, 1200, 700); // out of memory when open pdf
//        	contentStream.drawImage(img, 10, 300);
        	contentStream.close();
        	document.save("E:/ImageMergerPDF.pdf");
        	document.close();
        	
//        	PDFMergerUtility merger = new PDFMergerUtility();
//        	merger.addSource("E:/100bottles.pdf");
//        	merger.addSource("E:/ConvertedPDF.pdf");
//        	merger.addSource("E:/ImageMergerPDF.pdf");
//        	OutputStream bout2 = new BufferedOutputStream(new FileOutputStream("E:/mergerPDF.pdf"));
//
//        	merger.setDestinationStream(bout2);
//        	merger.mergeDocuments();

        }catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }
	
	//PDFBOX free
		public static void execute1()
	    {
	        try
	        {	
	        	
	        	PDFMergerUtility merger = new PDFMergerUtility();
	        	merger.addSource("E:/100bottles.pdf");
	        	merger.addSource("E:/ConvertedPDF.pdf");
	        	OutputStream bout2 = new BufferedOutputStream(new FileOutputStream("E:/mergerPDF.pdf"));
	
	        	merger.setDestinationStream(bout2);
	        	merger.mergeDocuments();

	        }catch(Exception ex)
	        {
	            ex.printStackTrace();
	        }

	    }
//	
	//ASPOSE pay (using PdfFileEditor)
//	public static void execute()
//    {
//        try
//        {	
//        	  //read the input file
//        	   String inFile1 = "E:/100bottles.pdf";
//        	   String inFile2 = "E:/1001bottles.pdf";
//        	   String outFile = "E:/kitOut1.pdf";
//        	   FileInputStream inStream1 = new FileInputStream(inFile1);
//        	   FileInputStream inStream2 = new FileInputStream(inFile2);
//
//        	   //concatenate two files
//        	   OutputStream outputStream = new FileOutputStream(outFile);
//        	   PdfFileEditor editor = new PdfFileEditor();
//        	   editor.concatenate(inStream1, inStream2, outputStream);
//        	   
//        	   //close the output FileOutputStream
//        	   outputStream.close();
//        	   System.out.println("===concatenate end===");
//        }catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
//
//    }
		
		// using iText to add image to pdf
		public static void simpleImages() throws Exception {
			com.lowagie.text.Document document = new com.lowagie.text.Document();
			PdfWriter.getInstance(document, new FileOutputStream("E:/simplePDF.pdf"));
			document.open();
			
			// Code 1
			document.add(new Paragraph("Simple Image"));
			com.lowagie.text.Image image = com.lowagie.text.Image.getInstance("E:/draft4_login.jpg");
			image.setAbsolutePosition(50, 500); // positioning image
			
			image.scaleAbsolute(500, 280); // adjust image size
			
			document.add(image);
			System.out.println("===done==");
		
			document.close();
		}
}

*/