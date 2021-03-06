package com.lbcom.dadelion.bigbin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lbcom.dadelion.bigbin.model.BZUser;
import com.lbcom.dadelion.bigbin.service.BZUserService;
import com.lbcom.dadelion.common.StringUtil;
import com.lbcom.dadelion.common.base.BaseResource;
/**
 * 
 * @description 用户注册登录页面
 * @author  liubin
 * @date 2015年12月27日 上午9:17:36 
 * @version 1.0 
 * @param
 * @return
 */
@Controller
public class LoginController extends BaseResource<BZUser>{

	@Resource
	BZUserService user_ser;

	/**
	 * 登录主页面
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/login.do")
	public ModelAndView Logindex(HttpServletRequest request) throws IOException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String ip = IpGet.getIpAddress(request);
		map.put("ip", ip);
		return new ModelAndView("bigbin/login/login/login.html", map);
	}
	/**
	 * 404页面
	 */
	@RequestMapping("/404.do")
	public ModelAndView Error(){
		return new ModelAndView("bigbin/login/error/404.html");
	}
	
	/**
	 * 登录成功后的主页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/mainview.do")
	public ModelAndView Mainview(HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("bigbin/homeview/index.html", map);
	}
	
	/**
	 * 从session里面获取登录的用户名
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/showUsername.do")
	public void showLogin(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject jsonObject = new JSONObject();
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		if (StringUtil.isNotNullOrEmpty(username)) {
			jsonObject.put("username", username);
		}else {
			jsonObject.put("username", "");
		}
		response.getWriter().print(jsonObject);
	}
	
	/**
	 * 登录验证
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/login_check.do")
	public void Login(HttpServletRequest request,HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(30 * 60);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		jsonObject.put("success", "1");
   		map.put("password", password);
	    if (StringUtil.isNotNullOrEmpty(username)) {
			if(username.contains("@")){
		   		map.put("email", username);
			}else {
		   		map.put("username", username);
			}
		}

   		List<BZUser> result = null;
	    try {
	    	result = user_ser.registerCheck(map);
	    } catch (Exception e) {
	    	jsonObject.put("msg", e.getMessage());
		}
   	    if (StringUtil.isNULLOrEmpty(result)) {
	   	     jsonObject.put("success", "0");
		   	 jsonObject.put("msg", "用户名或密码错误，请重试！");
   	    }else {
			session.setAttribute("username", username);
			session.setAttribute("xlh", result.get(0).getXlh());
			BZUser zUser = result.get(0);
			jsonObject.put("meg", zUser.getYhqx());
		}
	    response.getWriter().print(jsonObject);
	}	
}
