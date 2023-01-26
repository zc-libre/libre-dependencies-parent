package com.libre.oss;


import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.libre.oss.support.OssTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author: Libre
 * @Date: 2022/5/14 3:16 PM
 */
@SpringBootTest
public class OssTests {

	@Autowired
	private OssTemplate template;
	/**
	 * 上传文件
	 * 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
	 *
	 * @param file 资源
	 * @return R(bucketName, filename)
	 */
//	@PostMapping("/upload")
//	public R upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
//		template.putObject("video-spider", file.getOriginalFilename(), file.getInputStream());
//		return R.status(true);
//	}

	@Test
	void listFile() {
		List<S3ObjectSummary> allObjects = template.getAllObjects("libre");
		allObjects.forEach(System.out::println);
	}

}
