package xyz.refrain.onlineedu.service;

import cn.hutool.core.io.file.FileNameUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.config.properties.AliyunOssProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Aliyun Oss Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class AliyunOssService {

	public static final String IllegalCharsInFileName = "[\\\\/:*?\"<>|]";

	@Autowired
	private OSS ossClient;

	@Autowired
	private AliyunOssProperties ossProperties;


	/**
	 * 上传文件
	 *
	 * @param file 文件
	 * @return 文件访问路径，失败返回空字符串
	 */
	public String upload(MultipartFile file) {
		try {
			String fileKey = getFileKey(file.getOriginalFilename());
			PutObjectResult putObjectResult = ossClient.putObject(
					ossProperties.getBucketName(),
					fileKey,
					file.getInputStream()
			);

			return ossProperties.getAccessDomain() + "/" + fileKey;
		} catch (Exception e) {
			log.error("上传文件失败", e.getCause());
		}
		return "";
	}

	/**
	 * 根据 url 删除oss文件
	 */
	public boolean delete(String ossFileUrl) {
		if (!StringUtils.hasText(ossFileUrl)) {
			return false;
		}
		String fileKey = getOssFileKey(ossFileUrl);
		try {
			ossClient.deleteObject(ossProperties.getBucketName(), fileKey);
			return true;
		} catch (Exception e) {
			log.error("删除文件失败", e.getCause());
		}
		return false;
	}

	/**
	 * 根据 url 获得 oss 文件 key
	 */
	public String getOssFileKey(String ossFileUrl) {
		if (!StringUtils.hasText(ossFileUrl)) {
			return "";
		}
		int i = ossFileUrl.lastIndexOf('/');
		if (i > 0) {
			return ossFileUrl.substring(i + 1);
		}
		return "";
	}

	/**
	 * 获得保存在 oss 的文件key
	 * 格式 yyyyMMdd-fileName-uuid.ext
	 */
	private String getFileKey(String fileName) {

		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		String uuid = org.apache.commons.lang3.StringUtils.remove(UUID.randomUUID().toString(), '-');

		String mainName = FileNameUtil.mainName(fileName);
		mainName = mainName.replaceAll(IllegalCharsInFileName, "");
		String extName = FileNameUtil.extName(fileName);

		String newFileName = mainName + "-" + uuid + "." + extName;

		return date + "-" + newFileName;
	}

}
