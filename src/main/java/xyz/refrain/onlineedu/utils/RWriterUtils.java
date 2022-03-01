package xyz.refrain.onlineedu.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 返回 响应 结果 工具
 */
public class RWriterUtils {

	private static final String DEFAULT_ENCODING = "utf-8";


	public static void writeTextHtml(ServletResponse response, String textHtml) {
		writeString(response, "text/html", DEFAULT_ENCODING, textHtml);
	}

	public static void writeJson(ServletResponse response, String jsonString) {
		writeString(response, "application/json", DEFAULT_ENCODING, jsonString);
	}

	public static <R> void writeJson(ServletResponse response, R r) {
		try {
			writeJson(response, JsonUtils.objectToJson(r));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public static void writeString(ServletResponse response, String contentType, String encoding, String string) {
		try {
			response.setContentType(contentType + ";charset=" + encoding);
			response.getWriter().write(string);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
