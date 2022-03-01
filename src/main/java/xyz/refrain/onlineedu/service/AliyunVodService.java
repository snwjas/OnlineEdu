package xyz.refrain.onlineedu.service;

import cn.hutool.core.io.file.FileNameUtil;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.config.properties.AliyunProperties;

import java.io.InputStream;
import java.util.List;

/**
 * Aliyun Vod Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class AliyunVodService {

	@Autowired
	private DefaultAcsClient vodClient;

	@Autowired
	private AliyunProperties aliyunProperties;

	/**
	 * 上传视频
	 *
	 * @return 返回视频Id
	 */
	public String uploadVideo(MultipartFile file) {
		try {
			//accessKeyId, accessKeySecret
			//fileName：上传文件原始名称，01.03.09.mp4
			String fileName = file.getOriginalFilename();
			//title：上传之后显示名称
			String title = FileNameUtil.mainName(fileName);
			//inputStream：上传文件输入流
			InputStream inputStream = file.getInputStream();
			UploadStreamRequest request = new UploadStreamRequest(
					aliyunProperties.getAccessKeyId(), aliyunProperties.getAccessKeySecret(),
					title, fileName, inputStream);

			UploadVideoImpl uploader = new UploadVideoImpl();
			UploadStreamResponse response = uploader.uploadStream(request);
			if (response.isSuccess()) {
				return response.getVideoId();
			}
		} catch (Exception e) {
			log.info("上传视频失败", e.getCause());
		}
		return null;
	}

	/**
	 * 获取视频信息
	 */
	public GetPlayInfoResponse getVideoInfo(String videoId) {
		// 创建获取视频地址request和response
		GetPlayInfoRequest request = new GetPlayInfoRequest();
		// 向request对象里面设置视频ID
		request.setVideoId(videoId);
		// 调用初始化对象里面的方法，传递request，获取数据
		try {
			return vodClient.getAcsResponse(request);
		} catch (Exception e) {
			log.info("获取视频信息失败", e.getCause());
			return null;
		}
	}

	/**
	 * 获取视频播放地址
	 *
	 * @return 视频播放地址
	 */
	public String getPlayUrl(String videoId) {
		// 创建获取视频地址request和response
		GetPlayInfoRequest request = new GetPlayInfoRequest();
		// 向request对象里面设置视频ID
		request.setVideoId(videoId);
		// 调用初始化对象里面的方法，传递request，获取数据
		try {
			GetPlayInfoResponse response = vodClient.getAcsResponse(request);
			for (GetPlayInfoResponse.PlayInfo playInfo : response.getPlayInfoList()) {
				return playInfo.getPlayURL();
			}
		} catch (Exception e) {
			log.info("获取视频播放地址失败", e.getCause());
		}
		return null;
	}

	/**
	 * 获取视频凭证
	 * 当音频被加密时，无法直接通过地址访问，此时可以通过视频凭证访问，视频凭证由阿里云API生成
	 *
	 * @return 视频凭证
	 */
	public String getPlayAuth(String videoId) {
		// 创建初始化对象
		GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
		// 向request对象设置视频ID值
		request.setVideoId(videoId);
		// 调用初始化对象里的方法得到凭证
		try {
			GetVideoPlayAuthResponse response = vodClient.getAcsResponse(request);
			return response.getPlayAuth();
		} catch (Exception e) {
			log.info("获取视频凭证失败", e.getCause());
		}
		return null;
	}

	/**
	 * 删除视频
	 */
	public boolean deleteVideos(List<String> videoIdList) {
		try {
			//创建删除视频request对象
			DeleteVideoRequest request = new DeleteVideoRequest();
			//videoIdList值转换成 1,2,3
			String videoIds = StringUtils.join(videoIdList.toArray(), ",");
			System.out.println(videoIds);
			//向request设置视频id
			request.setVideoIds(videoIds);
			//调用初始化对象的方法实现删除
			vodClient.getAcsResponse(request);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("删除视频失败", e.getCause());
		}
		return false;
	}


}
