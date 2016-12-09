package newbee.morningGlory.http.servlets.sys;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.servlets.sys.bsh.HtmlOutput;
import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellServlet extends HttpServlet {

	private static final long serialVersionUID = -1114221785937976679L;
	private static final Map<String, String> codes = new LinkedHashMap<String, String>();
	static {
		codes.put("查询玩家对象(角色名)", "sophia.mmorpg.MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(\"玩家角色名\");");

		codes.put("查询在线玩家数量", "sophia.mmorpg.MMORPGContext.getPlayerComponent().getPlayerManager().getOnlineTotalCount();");
		
		codes.put("查询缓存玩家数量", "sophia.mmorpg.MMORPGContext.getPlayerComponent().getPlayerManager().getCachePlayerCount();");

		codes.put("查询Ref数据", "sophia.game.GameRoot.getGameRefObjectManager().getManagedObject(\"RefId\");");

		codes.put("查看系统信息", "newbee.morningGlory.GameApp.getSystemInfo();");
		
		codes.put("查看ObjectPool", "sophia.foundation.util.ObjectPool.trace();");
		
		codes.put("查看RecyclePool", "sophia.mmorpg.utils.RecyclePool.tracePools();");
		
		codes.put("查看对象池", "newbee.morningGlory.GameApp.traceInstances();");
		
		codes.put("查看场景信息", "sophia.mmorpg.MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(\"SceneRefId\");");
		
		codes.put("查看场景出错怪物信息", "sophia.mmorpg.MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(\"SceneRefId\").getMonsterMgrComponent().getErrorMonster();");
		
		codes.put("传送玩家回城", "sophia.mmorpg.MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(\"玩家角色名\").getPlayerSceneComponent().goHome();");
		
		codes.put("添加消息过滤", "sophia.mmorpg.communication.SocketActionEventListerComponent.addMessageFilter(messageId);");
		
		codes.put("添加消息范围过滤,注意是闭区间[]", "sophia.mmorpg.communication.SocketActionEventListerComponent.addMessageFilterRange(minMessageId, maxMessageId);");
		
		codes.put("移除消息过滤", "sophia.mmorpg.communication.SocketActionEventListerComponent.removeMessageFilter(messageId);");
		
		codes.put("移除消息范围过滤,注意是闭区间[]", "sophia.mmorpg.communication.SocketActionEventListerComponent.removeMessageFilterRange(minMessageId, maxMessageId);");
		
		codes.put("输出当前过滤消息", "sophia.mmorpg.communication.SocketActionEventListerComponent.printMessageFilter();");
		
		codes.put("获取玩家的IP", "sophia.mmorpg.MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(\"玩家角色名\").getConnection().getIP();");
		
		codes.put("包速检测开关", "sophia.foundation.communication.core.impl.MessageDecoderFilter.setSpeedingFilterEnable(true);");
		
		codes.put("心跳包检测最大容忍次数限制", "sophia.mmorpg.communication.SocketActionEventListerComponent.packetThrottler.setMaxTolerantPacketLimit(limitTimes);");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");

		String code = req.getParameter("code");
		String depth = req.getParameter("depth");

		if (code == null || "".equals(code)) {
			showEdit(req, resp);
		} else {
			code = new String(code.getBytes("ISO-8859-1"), "utf-8");
			depth = new String(depth.getBytes("ISO-8859-1"), "utf-8");

			code = URLDecoder.decode(code, "utf-8");
			depth = URLDecoder.decode(depth, "utf-8");

			showObjTree(req, resp, code, depth);
		}
	}

	private void showObjTree(HttpServletRequest req, HttpServletResponse resp, String code, String depth) throws ServletException, IOException {

		// private static List<Object> objectChecker = new ArrayList<Object>();
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html");

		if (code == null || code.length() == 0) {
			resp.sendRedirect("/bsh");
			return;
		}
		Interpreter inter = new Interpreter();
		try {
			inter.eval("import java.*;");
			inter.eval("import sophia.*;");
			inter.eval("import com.*;");
			inter.eval("import org.*;");

			Object o = inter.eval(code);
			resp.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
			resp.getWriter().print("<head>");
			resp.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
			resp.getWriter().print("</head>");

			resp.getWriter().print("<body><pre>");

			resp.getWriter().println("<b>" + code + "</b>");

			if (o == null) {
				resp.getWriter().print("查询对象为空！");
			} else {
				resp.getWriter().println("<b>" + o.getClass().getSimpleName() + depth + "</b>");

				Object obj = o;
				for (String field : depth.split("\\.")) {
					if (field == null || "".equals(field))
						continue;
					try {
						obj = HtmlOutput.toList(obj).get(field);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				resp.getWriter().println("<b>" + obj.getClass().getSimpleName() + "</b>");

				resp.getWriter().print(HtmlOutput.prettyPrint(HtmlOutput.toJsonHtml(obj, code, depth)));
				HtmlOutput.clear();
			}
			resp.getWriter().print("</pre></body></html>");
		} catch (EvalError e) {
			e.printStackTrace(resp.getWriter());
		}
	}

	private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
		resp.getWriter().print("<head>");
		resp.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
		resp.getWriter().print("</head>");
		resp.getWriter().print("<body>");
		resp.getWriter().print("<form action=# method=POST >");
		resp.getWriter().print("NewBee BSH调试器 v1.0 (输入调试指令)：</br><textarea type=text rows='10' style='width:100%' name='code'></textarea></br>");
		resp.getWriter().print("<input type=submit value=' 执行 ' >");
		resp.getWriter().print("</form>");
		resp.getWriter().print("<div>输入调试指令 Examples:</div>");
		resp.getWriter().print("<div>");
		resp.getWriter().print("<textarea type=text style='width:100%; height:500px;'>");
		for (Entry<String, String> entry : codes.entrySet()) {
			resp.getWriter().print("//" + entry.getKey() + "\r\n");
			resp.getWriter().print(entry.getValue() + "\r\n");
		}

		resp.getWriter().print("</textarea>");
		resp.getWriter().print("</div>");
		resp.getWriter().print("</body></html>");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// private static List<Object> objectChecker = new ArrayList<Object>();
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html");
		String code = req.getParameter("code");

		if (code == null || code.length() == 0) {
			resp.sendRedirect("/bsh");
			return;
		}
		code = code.trim();

		Interpreter inter = new Interpreter();
		try {
			inter.eval("import java.*;");
			inter.eval("import sophia.*;");
			inter.eval("import com.*;");
			inter.eval("import org.*;");

			Object o = inter.eval(code);
			resp.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
			resp.getWriter().print("<head>");
			resp.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
			resp.getWriter().print("</head>");
			resp.getWriter().print("<body><pre>");
			resp.getWriter().println("<b>" + code + "</b>");
			if (o == null) {
				resp.getWriter().println("执行成功,返回 NULL");
			} else {

				resp.getWriter().println("<b>" + o.getClass().getSimpleName() + "</b>");

				if (o instanceof CharSequence) {
					resp.getWriter().print(o);
				} else {
					resp.getWriter().print(HtmlOutput.prettyPrint(HtmlOutput.toJsonHtml(o, code, "")));
				}
				HtmlOutput.clear();
			}
			resp.getWriter().print("</pre></body></html>");
		} catch (Exception e) {
			e.printStackTrace(resp.getWriter());
			e.printStackTrace();
		}
	}
}
