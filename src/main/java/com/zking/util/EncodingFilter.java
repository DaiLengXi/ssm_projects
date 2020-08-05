package com.zking.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 涓枃涔辩爜澶勭悊
 * 
 */
public class EncodingFilter implements Filter {

	private String encoding = "UTF-8";// 榛樿瀛楃闆�

	public EncodingFilter() {
		super();
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// 涓枃澶勭悊蹇呴』鏀惧埌 chain.doFilter(request, response)鏂规硶鍓嶉潰
		res.setContentType("text/html;charset=" + this.encoding);
		if (req.getMethod().equalsIgnoreCase("post")) {
			req.setCharacterEncoding(this.encoding);
		} else {
			Map map = req.getParameterMap();// 淇濆瓨鎵�鏈夊弬鏁板悕=鍙傛暟鍊�(鏁扮粍)鐨凪ap闆嗗悎
			Set set = map.keySet();// 鍙栧嚭鎵�鏈夊弬鏁板悕
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				String[] values = (String[]) map.get(name);// 鍙栧嚭鍙傛暟鍊糩娉細鍙傛暟鍊间负涓�涓暟缁刔
				for (int i = 0; i < values.length; i++) {
					values[i] = new String(values[i].getBytes("ISO-8859-1"),
							this.encoding);
				}
			}
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		String s = filterConfig.getInitParameter("encoding");// 璇诲彇web.xml鏂囦欢涓厤缃殑瀛楃闆�
		if (null != s && !s.trim().equals("")) {
			this.encoding = s.trim();
		}
	}

}
