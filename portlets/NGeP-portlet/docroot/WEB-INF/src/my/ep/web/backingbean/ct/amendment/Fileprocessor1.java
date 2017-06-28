package my.ep.web.backingbean.ct.amendment;


import my.ep.web.util.template.DocumentGenerator;


public class Fileprocessor1 {
	
	public Fileprocessor1(){
	}

	public static String createPdf(String filePathDocx)
	  {
			String filePathPdf ="";
			try
					{
						
							filePathPdf = getFileName(filePathDocx)+".pdf";
							DocumentGenerator.getInstance().generateDocToPdfFile(filePathPdf);
					}
	    catch (Throwable e)
	    		{
	    				e.printStackTrace();
	    		}
			return filePathPdf;
	  }
	
	public static String getFileName(String fileName) {
	    int index = fileName.lastIndexOf('.');
	    if (index == -1) {
	        return fileName;
	    } else {
	        return fileName.substring(0, index);
	    }
	}
	  public static void main(String[] args) {
		      String filePath = "C:/Users/User/Desktop/ramdan.docx";
	         String str = createPdf(filePath);
	         System.out.println(str);
	  }
}
