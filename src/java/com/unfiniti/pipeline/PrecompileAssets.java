import org.apache.tools.ant.Task;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.zip.GZIPOutputStream;

public class PrecompileAssets
{
  private String staticRoot;
  private String manifestFile;
  private Boolean optimizeAssets = false;
  private Boolean gzipAssets = false;
  
  public void setStaticRoot(String path) {
      staticRoot = path;
  }
  
  public void setManifestFile(String path) {
      manifestFile = path;
  }
  
  public void setOptimize(Boolean optimize) {
      optimizeAssets = optimize;
  }
  
  public void setGzip(Boolean gzip) {
      gzipAssets = gzip;
  }
  
  public void execute()
  {
    System.out.println("Running...");
    System.out.println("ROOT : " + staticRoot);
    System.out.println("MANIFEST : " + manifestFile);
    System.out.println("OPTIMIZE : " + optimizeAssets);
    System.out.println("GZIP : " + gzipAssets);
    
    File dir = new File(staticRoot);

    try
    {
      PrintWriter output = new PrintWriter(new FileWriter(manifestFile,true));
      
    	System.out.println("Getting all files in " + dir.getCanonicalPath() + " including those in subdirectories");
    	String[] extensions = { "bmp", "css", "gif", "ico", "jpg", "jpeg", "js", "pdf", "png", "txt", "xml" };
      
    	List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
    	for (File file : files)
    	{
    	  String fileName = file.getName();
    	  String filePath = file.getCanonicalPath();
    	  //System.out.println(filePath);
    	  String currentDir = filePath.substring(0, filePath.lastIndexOf(fileName));
    	  String relativePathToFile = filePath.substring(filePath.lastIndexOf("assets") + 7, filePath.lastIndexOf(fileName));
    	  //System.out.println(relativePathToFile);
    		String hash = SHASum(file);
    		String[] fileNameAttributes = fileName.split("\\.(?=[^\\.]+$)");
    		String processedFileName = fileNameAttributes[0] + "-" + hash + "." + fileNameAttributes[1];
    		File processedFileHandle = new File(currentDir + processedFileName);
    		
    		if (optimizeAssets && (fileNameAttributes[1].equals("css") || fileNameAttributes[1].equals("js")))
    		{
    		  try
    		  {
      			Options o = new Options(); // use defaults
      			YuiCompressor.compress(fileNameAttributes[1], filePath, currentDir + fileNameAttributes[0] + "-" + hash + "." + fileNameAttributes[1], o);
      			FileUtils.deleteQuietly(file); // remove original file
      		}
      		catch (Exception e) {
      			System.out.println("UNABLE TO COMPRESS FILE!\n" + e.getMessage());
      		}
    		}
    		else {
    		  FileUtils.moveFile(file, processedFileHandle);
    		}
    		
    		if (gzipAssets) {
    		  writeGZipFile(processedFileHandle);
    		}
        
    		output.printf("%s=%s" + System.getProperty("line.separator"), relativePathToFile + fileName, relativePathToFile + processedFileName);
    	}
    	output.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void hashAssets()
  {
  
  }
  
  public void writeGZipFile(File file)
  {
    try
    {
      FileInputStream in = new FileInputStream(file);
      GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(file.getCanonicalPath() + ".gz"));
      
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
      }
      in.close();

      out.finish();
      out.close();
    }
    catch (Exception e) {
      System.out.println("Unable to create .gz " + "\n" + e.getMessage());
    }
  }
  
  public static String SHASum(File file) throws NoSuchAlgorithmException
  {
    MessageDigest md = MessageDigest.getInstance("SHA1");
    
    FileInputStream fis = null;
    byte[] mdbytes = null;
    
    try
    {
      fis = new FileInputStream(file);
      byte[] dataBytes = new byte[1024];

      int nread = 0; 
      while ((nread = fis.read(dataBytes)) != -1) {
        md.update(dataBytes, 0, nread);
      };
      mdbytes = md.digest();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    //convert the byte to hex format method 2
    StringBuffer hexString = new StringBuffer();
    for (int i=0; i < mdbytes.length; i++) {
      hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
    }

    return hexString.toString();
  }
}


























