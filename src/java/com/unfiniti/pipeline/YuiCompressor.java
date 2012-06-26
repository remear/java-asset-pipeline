import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class YuiCompressor {

  public static void compress(String extension, String inputFilename, String outputFilename, Options o) throws IOException
  {
    Reader in = null;
		Writer out = null;
		try
		{
		  in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);
		  out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
		  
		  if (extension.equals("css")) {
        CssCompressor compressor = new CssCompressor(in);
  			compressor.compress(out, o.lineBreakPos);
      }
      else if (extension.equals("js")) {
        JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
        out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
  			compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);
      }
      
			in.close(); in = null;
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
    
  } 
  
  /*
  public static void compressCSS(String inputFilename, String outputFilename, Options o) throws IOException {
		Reader in = null;
		Writer out = null;
		try {
			in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);

			CssCompressor compressor = new CssCompressor(in);
			in.close(); in = null;

			out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
			compressor.compress(out, o.lineBreakPos);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	public static void compressJavaScript(String inputFilename, String outputFilename, Options o) throws IOException {
		Reader in = null;
		Writer out = null;
		try {
			in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);

			JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
			in.close(); in = null;

			out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
			compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	*/
	
	private static class YuiCompressorErrorReporter implements ErrorReporter {
		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			if (line < 0) {
				System.out.println(message);
			} else {
				System.out.println("WARNING : " + line + ':' + lineOffset + ':' + message);
			}
		}

		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			if (line < 0) {
				System.out.println(message);
			} else {
				System.out.println("ERROR : " + line + ':' + lineOffset + ':' + message);
			}
		}

		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
			error(message, sourceName, line, lineSource, lineOffset);
			return new EvaluatorException(message);
		}
	}

	//private static Logger logger = Logger.getLogger(YuiCompressor.class.getName());
}
