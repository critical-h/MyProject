package com.sist.model;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.BufferedServletInputStream;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.sist.dao.*;
import com.sun.org.apache.regexp.internal.recompile;
public class Model {
	public void databoard_list(HttpServletRequest request){
		String page = request.getParameter("page");
		if(page==null)
			page="1";
		int curpage=Integer.parseInt(page);
		DataBoardDAO dao = new DataBoardDAO();
		List<DataBoardVO> list = dao.databoardListData(curpage);
		int totalpage=dao.databoardTotalPage();
		request.setAttribute("curpage", curpage);
		request.setAttribute("list", list);
		request.setAttribute("totalpage", totalpage);

	}
	public void databoard_insert_ok(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("UTF-8");
			String path="c:\\upload";
			String enctype="UTF-8";
			int size=100*1024*1024;
			MultipartRequest mr =new MultipartRequest(request, path,size,enctype,
					new DefaultFileRenamePolicy());
			String name = mr.getParameter("name");
			String subject = mr.getParameter("subject");
			String content = mr.getParameter("content");
			String pwd = mr.getParameter("pwd");

			DataBoardVO vo = new DataBoardVO();
			vo.setName(name);
			vo.setSubject(subject);
			vo.setContent(content);
			vo.setPwd(pwd);
			String filename = mr.getOriginalFileName("upload");
			if(filename==null){
				vo.setFilename("");
				vo.setFilesize(0);
			}
			else{
				File file = new File(path+"\\"+filename);
				vo.setFilename(filename);
				vo.setFilesize((int)file.length());
			}
			DataBoardDAO dao=new DataBoardDAO();
			dao.databoardInsert(vo);
		} catch (Exception e) {}
	}
	
	public void databoard_detail(HttpServletRequest request){
		String no = request.getParameter("no");
		String curpage = request.getParameter("page");
		DataBoardDAO dao = new DataBoardDAO();
		DataBoardVO vo = dao.databoardDetailData(Integer.parseInt(no));
		
		request.setAttribute("vo", vo);
		request.setAttribute("curpage", curpage);
		
    	List<DataBoardReplyVO> list=dao.dataBoardReplyData(Integer.parseInt(no));
    	request.setAttribute("list", list);
    	request.setAttribute("len", list.size());
	}
	
	public void databoard_update_data(HttpServletRequest request){
		String no = request.getParameter("no");
		String page = request.getParameter("page");
		DataBoardDAO dao = new DataBoardDAO();
		DataBoardVO vo = dao.databoardUpdateData(Integer.parseInt(no));
		request.setAttribute("vo", vo);
		request.setAttribute("curpage", page);
	}
    public void databoard_update_ok(HttpServletRequest request)
    {
    	try
    	{
    		request.setCharacterEncoding("UTF-8");
    	}catch(Exception ex){}
    	
    	// 데이터 받기 
    	  String name=request.getParameter("name");
	  	  String subject=request.getParameter("subject");
	  	  String content=request.getParameter("content");
	  	  String pwd=request.getParameter("pwd");
	  	  String no=request.getParameter("no");
	  	  String page=request.getParameter("page");
	  	  
	  	  DataBoardVO vo=new DataBoardVO();
	  	  vo.setNo(Integer.parseInt(no));
	  	  vo.setName(name);
	  	  vo.setSubject(subject);
	  	  vo.setContent(content);
	  	  vo.setPwd(pwd);
	  	  
	  	  // DAO로 전송 => 처리 => 결과값 => update_ok.jsp 전송 
	  	  DataBoardDAO dao = new DataBoardDAO();
	  	  boolean bCheck = dao.databoardUpdate(vo);
	  	  
	  	  request.setAttribute("bCheck", bCheck);
	  	  request.setAttribute("page", page);
	  	  request.setAttribute("no", no);
    	
    }
    
    public void login(HttpServletRequest request){
    	String id = request.getParameter("id");
    	String pwd = request.getParameter("pwd");
    	
    	DataBoardDAO dao = new DataBoardDAO();
    	String result = dao.isLogin(id, pwd);
    	
    	if(!(result.equals("NOID")||result.equals("NOPWD"))){
    		HttpSession session = request.getSession();
    		session.setAttribute("id", id);
    		session.setAttribute("name", result);
    	}
    	request.setAttribute("result", result);
    }
    
    public void reply_insert(HttpServletRequest request){
    	try {
			request.setCharacterEncoding("UTF-8");
		} catch (Exception e) {	}
    	String msg = request.getParameter("msg");
    	String bno = request.getParameter("bno");
    	String page = request.getParameter("page");
    	HttpSession session = request.getSession();
    	String id =(String)session.getAttribute("id");
    	String name =(String)session.getAttribute("name");
    	DataBoardReplyVO vo = new DataBoardReplyVO();
    	vo.setId(id);
    	vo.setName(name);
    	vo.setBno(Integer.parseInt(bno));
    	vo.setMsg(msg);
    	
    	DataBoardDAO dao=new DataBoardDAO();
    	dao.replyInsert(vo);
    	
    	request.setAttribute("bno", bno);
    	request.setAttribute("page", page);
    }
    
    public void reply_delete(HttpServletRequest request){
    	String no = request.getParameter("no");
    	String bno = request.getParameter("bno");
    	String page = request.getParameter("page");
    	
    	DataBoardDAO dao = new DataBoardDAO();
    	dao.replyDelete(Integer.parseInt(no));
    	request.setAttribute("no", bno);
    	request.setAttribute("page", page);   	
    	
    }
    public void reply_update(HttpServletRequest request){
    	try {
			request.setCharacterEncoding("UTF-8");
		} catch (Exception e) { }
    	String no = request.getParameter("no");
    	String bno = request.getParameter("bno");
    	String page = request.getParameter("page");
    	String msg = request.getParameter("msg");
    	
    	DataBoardDAO dao = new DataBoardDAO();
    	dao.replyUpdate(Integer.parseInt(no),msg);
    	request.setAttribute("no", bno);
    	request.setAttribute("page", page);   	
    	
    }
    public void databoard_delete(HttpServletRequest request)
    {
    	// detail.jsp => no,page ==> delete.jsp로 전송 
    	String no=request.getParameter("no");
    	String page=request.getParameter("page");
    	
    	// delete.jsp로 no,page를 전송 
    	request.setAttribute("no", no);
    	request.setAttribute("page", page);
    }
    
    public void databoard_delete_ok(HttpServletRequest request)
    {
    	String no=request.getParameter("no");
    	String page=request.getParameter("page");
    	String pwd=request.getParameter("pwd");
    	
    	// DB연동 
    	DataBoardDAO dao=new DataBoardDAO();
    	DataBoardVO vo=dao.databoardFileInfo(Integer.parseInt(no));
    	boolean bCheck=dao.databoard_delete(Integer.parseInt(no), pwd);
    	if(bCheck==true)
    	{
    		if(vo.getFilesize()>0)
    		{
    			try
    			{
    				File file=new File("c:\\upload\\"+vo.getFilename());
    				file.delete();
    			}catch(Exception ex){}
    		}
    	}
    	
    	//JSP 전송 
    	request.setAttribute("bCheck", bCheck);
    	request.setAttribute("page", page); // list.jsp
    }
}
