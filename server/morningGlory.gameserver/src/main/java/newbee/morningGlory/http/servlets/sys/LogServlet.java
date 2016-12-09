package newbee.morningGlory.http.servlets.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-3-22 下午2:47:48
 */

public class LogServlet extends HttpServlet {
	private static final long serialVersionUID = 5620975982758879763L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String type = request.getParameter("type");
		String file = request.getParameter("file");
		
		response.setContentType( "text/html" );
		response.setCharacterEncoding("utf-8");
		
		if( type == null || type.isEmpty() )
		{
			showList(request, response);
		}
		else if( type.equals("view") )
		{
			showLog(request, response,file);
		}
		else if( type.equals("download") )
		{
			download(request, response,file);
		}
		
	}

	private void download(HttpServletRequest request, HttpServletResponse response , String fileName)throws ServletException, IOException {

		response.setContentType( "text" );
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-disposition",  
                "attachment; filename=" + fileName );
		
		try
		{
		File logFile = new File(new File("").getAbsolutePath() + "/log/" + fileName  );

		FileInputStream bis =new FileInputStream(logFile);//创建文件输入流                        
        OutputStream bos = response.getOutputStream();
	
		byte[] buff = new byte[1024];
        int readCount = 0;
        readCount = bis.read(buff);

		while(readCount  != -1 ) {
			bos.write(buff, 0, readCount); 
            readCount = bis.read(buff);
		}
		
		if (bis!=null)
            bis.close();            
        if (bos!=null)
            bos.close();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			response.getWriter().println( ex.getMessage() );
		}

	}
	
	private void showLog(HttpServletRequest request, HttpServletResponse response , String fileName)throws ServletException, IOException {
		
		File logFile = new File(new File("").getAbsolutePath() + "/log/" + fileName  );

		response.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
		response.getWriter().print("<head>");
		response.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
		response.getWriter().print("</head>");
		response.getWriter().print("<body>");
		response.getWriter().print("<pre>");
		
		FileReader fr=new FileReader(logFile);//创建文件输入流             
		BufferedReader in=new BufferedReader(fr);//包装文件输入流，可整行读取
		String line;
		int i = 0;
		while((line=in.readLine()) != null || i < 1000) {
			i ++;
			response.getWriter().println( line );
		}
		
		in.close();
		
		response.getWriter().print("</pre>");
		response.getWriter().print("</body>");
		response.getWriter().print("</html>");
	}
	
	private void showList(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		response.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
		response.getWriter().print("<head>");
		response.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
		response.getWriter().print("</head>");
		response.getWriter().print("<body>");
		response.getWriter().print("<pre>");
		response.getWriter().println( new File("").getAbsolutePath() );
		response.getWriter().println("<b>注:'查看'仅列出Log的前1000行</b>");
		
		File logDir = new File(new File("").getAbsolutePath() + "/log"  );
		if( logDir.exists() && logDir.isDirectory() )
		{
			for( File file : logDir.listFiles() )
			{
				if( file.isFile() )
				{
					String fileHtml = String.format("%s %s <a href='log?type=view&file=%s'>查看</a> <a href='log?type=download&file=%s'>下载</a>" ,
							file.getName(),
							( file.length() < 1024 )? "1K" : ( file.length() < 1024 * 1024 ) ? file.length()/1024 +"K" : file.length()/1024/1024 +"M",
							file.getName() , file.getName()
							);
					
					response.getWriter().println( fileHtml );
				}
			}
		}

		response.getWriter().print("</pre>");
		response.getWriter().print("</body>");
		response.getWriter().print("</html>");
	}
	
}
