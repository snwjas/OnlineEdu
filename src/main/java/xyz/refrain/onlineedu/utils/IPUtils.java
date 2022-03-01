package xyz.refrain.onlineedu.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * IPv4 工具类
 */
public class IPUtils {

	/**
	 * 检查 IPv4 是否合法 正则表达式 Pattern 对象
	 */
	private static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private static final Pattern PATTERN = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

	/**
	 * 获取 HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
				.map(r -> ((ServletRequestAttributes) r).getRequest())
				.orElse(null);
	}

	/**
	 * IPv4地址是否有效
	 */
	public static boolean isIPv4Valid(String ipv4) {
		return ipv4 != null && PATTERN.matcher(ipv4).matches();
	}

	/**
	 * Int类型ip转为String类型
	 */
	public static String intToIpv4(int intIp) {
		int octet3 = (intIp >>> 24) % 256;
		int octet2 = (intIp >>> 16) % 256;
		int octet1 = (intIp >>> 8) % 256;
		// 负数时后8位处理
		int last = intIp % 256;
		int octet0 = last < 0 ? last + 256 : last;
		return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
	}

	/**
	 * String类型ip转为Int类型
	 */
	public static int ipv4ToInt(String ipv4) {
		if (isIPv4Valid(ipv4)) {
			String[] octets = ipv4.split("\\.");
			return (Integer.parseInt(octets[0]) << 24)
					+ (Integer.parseInt(octets[1]) << 16)
					+ (Integer.parseInt(octets[2]) << 8)
					+ Integer.parseInt(octets[3]);
		}
		return 0;
	}

	/**
	 * 是否为内网IP
	 */
	public static boolean isIPv4Private(String ipv4) {
		int intIp = ipv4ToInt(ipv4);
		return (intIp >= 167772160 && intIp <= 184549375)
				|| (intIp >= -1408237568 && intIp <= -1407188993)
				|| (intIp >= -1062731776 && intIp <= -1062666241);
		// return intIp >= ipv4ToInt("10.0.0.0") && intIp <= ipv4ToInt("10.255.255.255")
		// 		|| intIp >= ipv4ToInt("172.16.0.0") && intIp <= ipv4ToInt("172.31.255.255")
		// 		|| intIp >= ipv4ToInt("192.168.0.0") && intIp <= ipv4ToInt("192.168.255.255");
	}

	/**
	 * 获取请求主机IP地址
	 * 如果通过代理进来，则透过防火墙获取真实IP地址
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (!isIPv4Valid(ip)) {
			String[] checkedHeaders = new String[]{
					"Proxy-Client-IP", "WL-Proxy-Client-IP"
					, "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
			for (String checkedHeader : checkedHeaders) {
				if (!isIPv4Valid(ip)) {
					ip = request.getHeader(checkedHeader);
				}
			}
			if (!isIPv4Valid(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (String s : ips) {
				String strIp = s.trim();
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	public static String getIpAddress1(HttpServletRequest request) {
		String ip;
		boolean found = false;
		if ((ip = request.getHeader("x-forwarded-for")) != null) {
			String[] ips = ip.split(",");
			for (String s : ips) {
				ip = s.trim();
				if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			// 获得ip地址
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
