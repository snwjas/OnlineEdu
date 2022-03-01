package xyz.refrain.onlineedu.utils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
@Component
public class VideoUtil {

	/**
	 * 获取视频时长(时分秒)
	 */
	public static String ReadVideoTimeMs(MultipartFile file) {
		Encoder encoder = new Encoder();
		long ms = 0;
		try {
			// 获取文件类型
			String fileName = file.getContentType();
			// 获取文件后缀
			String pref = fileName.indexOf("/") != -1 ? fileName.substring(fileName.lastIndexOf("/") + 1,
					fileName.length()) : null;
			String prefix = "." + pref;
			// 用uuid作为文件名，防止生成的临时文件重复
			final File excelFile = File.createTempFile(UUID.randomUUID().toString().replace("-", ""), prefix);
			// MultipartFile to File
			file.transferTo(excelFile);
			MultimediaInfo m = encoder.getInfo(excelFile);
			ms = m.getDuration();
			//程序结束时，删除临时文件
			VideoUtil.deleteFile(excelFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;

		String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
		String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
		String strSecond = second < 10 ? "0" + second : "" + second;//秒
		if (strHour.equals("00")) {
			return strMinute + ":" + strSecond;
		} else {
			return strHour + ":" + strMinute + ":" + strSecond;
		}
	}

	/**
	 * 删除文件
	 */
	private static void deleteFile(File... files) {
		for (File file : files) {
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
