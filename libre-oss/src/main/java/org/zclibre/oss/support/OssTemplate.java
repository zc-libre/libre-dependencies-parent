package org.zclibre.oss.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.zclibre.oss.config.OssProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodyFromInputStreamConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * aws-s3
 *
 */
@RequiredArgsConstructor
public class OssTemplate implements InitializingBean, DisposableBean {

	private static final Duration MAX_PRESIGN_DURATION = Duration.ofDays(7);

	private static final Duration MIN_PRESIGN_DURATION = Duration.ofSeconds(1);

	private static final long MIN_MULTIPART_PART_SIZE = 5 * 1024 * 1024L;

	private static final int MAX_MULTIPART_PARTS = 10_000;

	private final OssProperties ossProperties;

	@Getter
	private S3Client s3Client;

	@Getter
	private S3AsyncClient s3AsyncClient;

	@Getter
	private S3Presigner s3Presigner;

	@Getter
	private S3TransferManager transferManager;

	/**
	 * 创建bucket
	 * @param bucketName bucket名称
	 */
	public void createBucket(String bucketName) {
		if (!bucketExists(bucketName)) {
			CreateBucketRequest request = buildCreateBucketRequest(bucketName);
			s3Client.createBucket(request);
		}
	}

	/**
	 * 检查bucket是否存在
	 * @param bucketName bucket名称
	 * @return 是否存在
	 */
	private boolean bucketExists(String bucketName) {
		try {
			HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucketName).build();
			s3Client.headBucket(request);
			return true;
		}
		catch (NoSuchBucketException e) {
			return false;
		}
		catch (S3Exception e) {
			if (e.statusCode() == 404) {
				return false;
			}
			throw e;
		}
	}

	/**
	 * 获取全部bucket
	 * @return bucket列表
	 */
	public List<Bucket> getAllBuckets() {
		ListBucketsResponse response = s3Client.listBuckets();
		return response.buckets();
	}

	/**
	 * 根据名称获取bucket
	 * @param bucketName bucket名称
	 * @return bucket
	 */
	public Optional<Bucket> getBucket(String bucketName) {
		return getAllBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
	}

	/**
	 * 删除bucket
	 * @param bucketName bucket名称
	 */
	public void removeBucket(String bucketName) {
		DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(bucketName).build();
		s3Client.deleteBucket(request);
	}

	/**
	 * 根据文件前缀查询文件
	 * @param bucketName bucket名称
	 * @param prefix 前缀
	 * @return 文件列表
	 */
	public List<S3Object> getAllObjectsByPrefix(String bucketName, String prefix) {
		ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).prefix(prefix).build();

		return s3Client.listObjectsV2Paginator(request).contents().stream().collect(Collectors.toList());
	}

	/**
	 * 获取文件外链，只用于下载
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param minutes 过期时间，单位分钟,请注意该值必须小于7天
	 * @return url
	 */
	public String getObjectURL(String bucketName, String objectName, int minutes) {
		return getObjectURL(bucketName, objectName, Duration.ofMinutes(minutes));
	}

	/**
	 * 获取文件外链，只用于下载
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param expires 过期时间,请注意该值必须小于7天
	 * @return url
	 */
	public String getObjectURL(String bucketName, String objectName, Duration expires) {
		validatePresignDuration(expires);

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(objectName).build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(expires)
			.getObjectRequest(getObjectRequest)
			.build();

		return s3Presigner.presignGetObject(presignRequest).url().toString();
	}

	/**
	 * 获取文件上传外链，只用于上传
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param minutes 过期时间，单位分钟,请注意该值必须小于7天
	 * @return url
	 */
	public String getPutObjectURL(String bucketName, String objectName, int minutes) {
		return getPutObjectURL(bucketName, objectName, Duration.ofMinutes(minutes));
	}

	/**
	 * 获取文件上传外链，只用于上传
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param expires 过期时间,请注意该值必须小于7天
	 * @return url
	 */
	public String getPutObjectURL(String bucketName, String objectName, Duration expires) {
		validatePresignDuration(expires);

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(objectName).build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(expires)
			.putObjectRequest(putObjectRequest)
			.build();

		return s3Presigner.presignPutObject(presignRequest).url().toString();
	}

	/**
	 * 获取文件URL（公共访问）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return url
	 */
	public String getObjectURL(String bucketName, String objectName) {
		GetUrlRequest request = GetUrlRequest.builder().bucket(bucketName).key(objectName).build();
		return s3Client.utilities().getUrl(request).toExternalForm();
	}

	/**
	 * 获取文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 文件响应流
	 */
	public ResponseInputStream<GetObjectResponse> getObject(String bucketName, String objectName) {
		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectName).build();
		return s3Client.getObject(request);
	}

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @throws IOException IOException
	 */
	public void putObject(String bucketName, String objectName, InputStream stream) throws IOException {
		putObject(bucketName, objectName, "application/octet-stream", stream);
	}

	/**
	 * 上传文件 指定 contextType
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @param contextType 文件类型
	 * @throws IOException IOException
	 */
	public void putObject(String bucketName, String objectName, String contextType, InputStream stream)
			throws IOException {
		Path tempFile = Files.createTempFile("libre-oss-upload-", ".tmp");
		try {
			Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(objectName)
				.contentType(contextType)
				.build();
			s3Client.putObject(request, RequestBody.fromFile(tempFile));
		}
		finally {
			Files.deleteIfExists(tempFile);
		}
	}

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @param size 大小
	 * @param contextType 类型
	 * @return 上传结果
	 */
	public PutObjectResponse putObject(String bucketName, String objectName, InputStream stream, long size,
			String contextType) {
		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(objectName)
			.contentType(contextType)
			.contentLength(size)
			.build();

		return s3Client.putObject(request, RequestBody.fromInputStream(stream, size));
	}

	/**
	 * 获取文件元信息
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 文件元信息
	 */
	public HeadObjectResponse getObjectInfo(String bucketName, String objectName) {
		HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(objectName).build();
		return s3Client.headObject(request);
	}

	/**
	 * 删除文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 */
	public void removeObject(String bucketName, String objectName) {
		DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(objectName).build();
		s3Client.deleteObject(request);
	}

	/**
	 * 列出所有对象
	 * @param bucketName bucket名称
	 * @return 对象列表
	 */
	public List<S3Object> listAllObjects(String bucketName) {
		ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();

		return s3Client.listObjectsV2Paginator(request).contents().stream().collect(Collectors.toList());
	}

	// ==================== 大文件上传相关方法 ====================

	/**
	 * 初始化分片上传
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 分片上传ID
	 */
	public String initiateMultipartUpload(String bucketName, String objectName) {
		return initiateMultipartUpload(bucketName, objectName, "application/octet-stream");
	}

	/**
	 * 初始化分片上传
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param contentType 文件类型
	 * @return 分片上传ID
	 */
	public String initiateMultipartUpload(String bucketName, String objectName, String contentType) {
		CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
			.bucket(bucketName)
			.key(objectName)
			.contentType(contentType)
			.build();

		CreateMultipartUploadResponse response = s3Client.createMultipartUpload(request);
		return response.uploadId();
	}

	/**
	 * 上传分片
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param uploadId 分片上传ID
	 * @param partNumber 分片号（从1开始）
	 * @param inputStream 分片数据流
	 * @param partSize 分片大小
	 * @return 分片上传结果
	 */
	public CompletedPart uploadPart(String bucketName, String objectName, String uploadId, int partNumber,
			InputStream inputStream, long partSize) {
		UploadPartRequest request = UploadPartRequest.builder()
			.bucket(bucketName)
			.key(objectName)
			.uploadId(uploadId)
			.partNumber(partNumber)
			.build();

		UploadPartResponse response = s3Client.uploadPart(request, RequestBody.fromInputStream(inputStream, partSize));

		return CompletedPart.builder().partNumber(partNumber).eTag(response.eTag()).build();
	}

	/**
	 * 完成分片上传
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param uploadId 分片上传ID
	 * @param completedParts 已完成的分片列表
	 * @return 完成上传响应
	 */
	public CompleteMultipartUploadResponse completeMultipartUpload(String bucketName, String objectName,
			String uploadId, List<CompletedPart> completedParts) {
		CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
			.parts(completedParts)
			.build();

		CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
			.bucket(bucketName)
			.key(objectName)
			.uploadId(uploadId)
			.multipartUpload(completedMultipartUpload)
			.build();

		return s3Client.completeMultipartUpload(request);
	}

	/**
	 * 取消分片上传
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param uploadId 分片上传ID
	 */
	public void abortMultipartUpload(String bucketName, String objectName, String uploadId) {
		AbortMultipartUploadRequest request = AbortMultipartUploadRequest.builder()
			.bucket(bucketName)
			.key(objectName)
			.uploadId(uploadId)
			.build();

		s3Client.abortMultipartUpload(request);
	}

	/**
	 * 列出已上传的分片
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param uploadId 分片上传ID
	 * @return 已上传的分片列表
	 */
	public List<Part> listParts(String bucketName, String objectName, String uploadId) {
		List<Part> parts = new ArrayList<>();
		Integer marker = null;
		boolean truncated;
		do {
			ListPartsRequest request = ListPartsRequest.builder()
				.bucket(bucketName)
				.key(objectName)
				.uploadId(uploadId)
				.partNumberMarker(marker)
				.build();
			ListPartsResponse response = s3Client.listParts(request);
			parts.addAll(response.parts());
			marker = response.nextPartNumberMarker();
			truncated = Boolean.TRUE.equals(response.isTruncated());
		}
		while (truncated);
		return parts;
	}

	/**
	 * 列出所有未完成的分片上传
	 * @param bucketName bucket名称
	 * @return 未完成的分片上传列表
	 */
	public List<MultipartUpload> listMultipartUploads(String bucketName) {
		List<MultipartUpload> uploads = new ArrayList<>();
		String keyMarker = null;
		String uploadIdMarker = null;
		boolean truncated;
		do {
			ListMultipartUploadsRequest request = ListMultipartUploadsRequest.builder()
				.bucket(bucketName)
				.keyMarker(keyMarker)
				.uploadIdMarker(uploadIdMarker)
				.build();
			ListMultipartUploadsResponse response = s3Client.listMultipartUploads(request);
			uploads.addAll(response.uploads());
			keyMarker = response.nextKeyMarker();
			uploadIdMarker = response.nextUploadIdMarker();
			truncated = Boolean.TRUE.equals(response.isTruncated());
		}
		while (truncated);
		return uploads;
	}

	/**
	 * 便捷的分片上传方法（自动处理分片逻辑）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param inputStream 输入流
	 * @param totalSize 总大小
	 * @param partSize 分片大小（建议5MB以上）
	 * @return 完成上传响应
	 * @throws IOException IO异常
	 */
	public CompleteMultipartUploadResponse uploadLargeFileInParts(String bucketName, String objectName,
			InputStream inputStream, long totalSize, long partSize) throws IOException {
		return uploadLargeFileInParts(bucketName, objectName, inputStream, totalSize, partSize,
				"application/octet-stream");
	}

	/**
	 * 便捷的分片上传方法（自动处理分片逻辑）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param inputStream 输入流
	 * @param totalSize 总大小
	 * @param partSize 分片大小（建议5MB以上）
	 * @param contentType 文件类型
	 * @return 完成上传响应
	 * @throws IOException IO异常
	 */
	public CompleteMultipartUploadResponse uploadLargeFileInParts(String bucketName, String objectName,
			InputStream inputStream, long totalSize, long partSize, String contentType) throws IOException {
		validateMultipartArguments(totalSize, partSize);

		// 1. 初始化分片上传
		String uploadId = initiateMultipartUpload(bucketName, objectName, contentType);

		List<CompletedPart> completedParts = new ArrayList<>();

		try {
			byte[] buffer = new byte[(int) partSize];
			int partNumber = 1;
			long uploadedBytes = 0;

			while (uploadedBytes < totalSize) {
				// 计算当前分片的实际大小
				long currentPartSize = Math.min(partSize, totalSize - uploadedBytes);

				// 读取分片数据
				int bytesRead;
				int totalBytesRead = 0;
				while (totalBytesRead < currentPartSize && (bytesRead = inputStream.read(buffer, totalBytesRead,
						(int) (currentPartSize - totalBytesRead))) != -1) {
					totalBytesRead += bytesRead;
				}

				if (totalBytesRead == 0) {
					throw new IOException("输入流数据不足，无法读取到声明的 totalSize: " + totalSize);
				}

				// 上传分片
				try (InputStream partStream = new ByteArrayInputStream(buffer, 0, totalBytesRead)) {
					CompletedPart completedPart = uploadPart(bucketName, objectName, uploadId, partNumber, partStream,
							totalBytesRead);
					completedParts.add(completedPart);
				}

				uploadedBytes += totalBytesRead;
				partNumber++;
			}

			if (uploadedBytes != totalSize) {
				throw new IOException("输入流数据不完整，期望: " + totalSize + " 实际: " + uploadedBytes);
			}

			// 2. 完成分片上传
			return completeMultipartUpload(bucketName, objectName, uploadId, completedParts);

		}
		catch (Exception e) {
			// 如果出现异常，取消分片上传
			try {
				abortMultipartUpload(bucketName, objectName, uploadId);
			}
			catch (Exception abortException) {
				// 记录取消失败的异常，但不覆盖原始异常
				e.addSuppressed(abortException);
			}
			if (e instanceof IOException ioException) {
				throw ioException;
			}
			throw (RuntimeException) e;
		}
	}

	// ==================== Transfer Manager 高级上传方法 ====================

	/**
	 * 上传大文件（自动分片）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param filePath 本地文件路径
	 * @return 上传结果
	 */
	public CompletedFileUpload uploadLargeFile(String bucketName, String objectName, Path filePath) {
		UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
			.putObjectRequest(req -> req.bucket(bucketName).key(objectName))
			.source(filePath)
			.build();

		FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
		return fileUpload.completionFuture().join();
	}

	/**
	 * 上传大文件（自动分片）带进度监听
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param filePath 本地文件路径
	 * @param enableLogging 是否启用日志记录进度
	 * @return 上传结果
	 */
	public CompletedFileUpload uploadLargeFile(String bucketName, String objectName, Path filePath,
			boolean enableLogging) {
		UploadFileRequest.Builder requestBuilder = UploadFileRequest.builder()
			.putObjectRequest(req -> req.bucket(bucketName).key(objectName))
			.source(filePath);

		if (enableLogging) {
			requestBuilder.addTransferListener(LoggingTransferListener.create());
		}

		FileUpload fileUpload = transferManager.uploadFile(requestBuilder.build());
		return fileUpload.completionFuture().join();
	}

	/**
	 * 上传大文件（自动分片）带自定义配置
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param filePath 本地文件路径
	 * @param contentType 文件类型
	 * @param enableLogging 是否启用日志记录进度
	 * @return 上传结果
	 */
	public CompletedFileUpload uploadLargeFile(String bucketName, String objectName, Path filePath, String contentType,
			boolean enableLogging) {
		UploadFileRequest.Builder requestBuilder = UploadFileRequest.builder()
			.putObjectRequest(req -> req.bucket(bucketName).key(objectName).contentType(contentType))
			.source(filePath);

		if (enableLogging) {
			requestBuilder.addTransferListener(LoggingTransferListener.create());
		}

		FileUpload fileUpload = transferManager.uploadFile(requestBuilder.build());
		return fileUpload.completionFuture().join();
	}

	/**
	 * 异步上传大文件（自动分片）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param filePath 本地文件路径
	 * @return 异步上传结果
	 */
	public CompletableFuture<CompletedFileUpload> uploadLargeFileAsync(String bucketName, String objectName,
			Path filePath) {
		UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
			.putObjectRequest(req -> req.bucket(bucketName).key(objectName))
			.source(filePath)
			.build();

		FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
		return fileUpload.completionFuture();
	}

	/**
	 * 异步上传大文件（自动分片）带进度监听
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param filePath 本地文件路径
	 * @param enableLogging 是否启用日志记录进度
	 * @return 异步上传结果
	 */
	public CompletableFuture<CompletedFileUpload> uploadLargeFileAsync(String bucketName, String objectName,
			Path filePath, boolean enableLogging) {
		UploadFileRequest.Builder requestBuilder = UploadFileRequest.builder()
			.putObjectRequest(req -> req.bucket(bucketName).key(objectName))
			.source(filePath);

		if (enableLogging) {
			requestBuilder.addTransferListener(LoggingTransferListener.create());
		}

		FileUpload fileUpload = transferManager.uploadFile(requestBuilder.build());
		return fileUpload.completionFuture();
	}

	/**
	 * 从 InputStream 上传大文件（自动分片）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param inputStream 输入流
	 * @param contentLength 内容长度
	 * @return 上传结果
	 */
	public CompletedUpload uploadLargeFile(String bucketName, String objectName, InputStream inputStream,
			long contentLength) {
		return uploadLargeFile(bucketName, objectName, inputStream, contentLength, "application/octet-stream");
	}

	/**
	 * 从 InputStream 上传大文件（自动分片）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param inputStream 输入流
	 * @param contentLength 内容长度
	 * @param contentType 文件类型
	 * @return 上传结果
	 */
	public CompletedUpload uploadLargeFile(String bucketName, String objectName, InputStream inputStream,
			long contentLength, String contentType) {
		UploadRequest uploadRequest = UploadRequest.builder()
			.putObjectRequest(
					req -> req.bucket(bucketName).key(objectName).contentType(contentType).contentLength(contentLength))
			.requestBody(AsyncRequestBody.fromInputStream(AsyncRequestBodyFromInputStreamConfiguration.builder()
				.inputStream(inputStream)
				.contentLength(contentLength)
				.build()))
			.build();

		Upload upload = transferManager.upload(uploadRequest);
		return upload.completionFuture().join();
	}

	/**
	 * 异步从 InputStream 上传大文件（自动分片）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param inputStream 输入流
	 * @param contentLength 内容长度
	 * @return 异步上传结果
	 */
	public CompletableFuture<CompletedUpload> uploadLargeFileAsync(String bucketName, String objectName,
			InputStream inputStream, long contentLength) {
		return uploadLargeFileAsync(bucketName, objectName, inputStream, contentLength, "application/octet-stream");
	}

	/**
	 * 异步从 InputStream 上传大文件（自动分片）
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param inputStream 输入流
	 * @param contentLength 内容长度
	 * @param contentType 文件类型
	 * @return 异步上传结果
	 */
	public CompletableFuture<CompletedUpload> uploadLargeFileAsync(String bucketName, String objectName,
			InputStream inputStream, long contentLength, String contentType) {
		UploadRequest uploadRequest = UploadRequest.builder()
			.putObjectRequest(
					req -> req.bucket(bucketName).key(objectName).contentType(contentType).contentLength(contentLength))
			.requestBody(AsyncRequestBody.fromInputStream(AsyncRequestBodyFromInputStreamConfiguration.builder()
				.inputStream(inputStream)
				.contentLength(contentLength)
				.build()))

			.build();

		Upload upload = transferManager.upload(uploadRequest);
		return upload.completionFuture();
	}

	/**
	 * 上传目录（批量上传）
	 * @param bucketName bucket名称
	 * @param directoryPath 本地目录路径
	 * @return 上传结果
	 */
	public CompletedDirectoryUpload uploadDirectory(String bucketName, Path directoryPath) {
		UploadDirectoryRequest uploadDirectoryRequest = UploadDirectoryRequest.builder()
			.source(directoryPath)
			.bucket(bucketName)
			.build();

		DirectoryUpload directoryUpload = transferManager.uploadDirectory(uploadDirectoryRequest);
		return directoryUpload.completionFuture().join();
	}

	/**
	 * 上传目录（批量上传）带前缀
	 * @param bucketName bucket名称
	 * @param directoryPath 本地目录路径
	 * @param keyPrefix S3对象键前缀
	 * @return 上传结果
	 */
	public CompletedDirectoryUpload uploadDirectory(String bucketName, Path directoryPath, String keyPrefix) {
		UploadDirectoryRequest uploadDirectoryRequest = UploadDirectoryRequest.builder()
			.source(directoryPath)
			.bucket(bucketName)
			.s3Prefix(keyPrefix)
			.build();

		DirectoryUpload directoryUpload = transferManager.uploadDirectory(uploadDirectoryRequest);
		return directoryUpload.completionFuture().join();
	}

	/**
	 * 异步上传目录（批量上传）
	 * @param bucketName bucket名称
	 * @param directoryPath 本地目录路径
	 * @return 异步上传结果
	 */
	public CompletableFuture<CompletedDirectoryUpload> uploadDirectoryAsync(String bucketName, Path directoryPath) {
		UploadDirectoryRequest uploadDirectoryRequest = UploadDirectoryRequest.builder()
			.source(directoryPath)
			.bucket(bucketName)
			.build();

		DirectoryUpload directoryUpload = transferManager.uploadDirectory(uploadDirectoryRequest);
		return directoryUpload.completionFuture();
	}

	@Override
	public void afterPropertiesSet() {
		String endpoint = ossProperties.getEndpoint();
		Assert.hasText(endpoint, "endpoint must not be null");

		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ossProperties.getAccessKey(),
				ossProperties.getSecretKey());

		S3ClientBuilder clientBuilder = S3Client.builder()
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.endpointOverride(URI.create(endpoint))
			.forcePathStyle(ossProperties.getPathStyleAccess());

		// 设置区域，如果没有指定则使用默认区域
		String region = ossProperties.getRegion();
		if (region != null && !region.isEmpty()) {
			clientBuilder.region(Region.of(region));
		}
		else {
			clientBuilder.region(Region.US_EAST_1);
		}

		this.s3Client = clientBuilder.build();

		// 创建异步客户端（启用 multipart 以支持 TransferManager 自动分片）
		this.s3AsyncClient = S3AsyncClient.builder()
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.endpointOverride(URI.create(endpoint))
			.forcePathStyle(ossProperties.getPathStyleAccess())
			.multipartEnabled(true)
			.region(region != null && !region.isEmpty() ? Region.of(region) : Region.US_EAST_1)
			.build();

		// 创建预签名URL生成器（配置 pathStyle 以匹配 S3Client）
		this.s3Presigner = S3Presigner.builder()
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.endpointOverride(URI.create(endpoint))
			.serviceConfiguration(
					S3Configuration.builder().pathStyleAccessEnabled(ossProperties.getPathStyleAccess()).build())
			.region(region != null && !region.isEmpty() ? Region.of(region) : Region.US_EAST_1)
			.build();

		// 创建传输管理器
		this.transferManager = S3TransferManager.builder().s3Client(s3AsyncClient).build();
	}

	@Override
	public void destroy() {
		if (this.transferManager != null) {
			this.transferManager.close();
		}
		if (this.s3Presigner != null) {
			this.s3Presigner.close();
		}
		if (this.s3AsyncClient != null) {
			this.s3AsyncClient.close();
		}
		if (this.s3Client != null) {
			this.s3Client.close();
		}
	}

	private void validatePresignDuration(Duration expires) {
		Assert.notNull(expires, "expires must not be null");
		Assert.isTrue(expires.compareTo(MIN_PRESIGN_DURATION) >= 0, "expires must be greater than 0");
		Assert.isTrue(expires.compareTo(MAX_PRESIGN_DURATION) <= 0, "expires must be less than or equal to 7 days");
	}

	private void validateMultipartArguments(long totalSize, long partSize) {
		Assert.isTrue(totalSize > 0, "totalSize must be greater than 0");
		Assert.isTrue(partSize > 0, "partSize must be greater than 0");
		Assert.isTrue(partSize <= Integer.MAX_VALUE, "partSize must be less than or equal to Integer.MAX_VALUE");
		long expectedPartCount = (totalSize + partSize - 1) / partSize;
		Assert.isTrue(expectedPartCount <= MAX_MULTIPART_PARTS,
				"part count must be less than or equal to " + MAX_MULTIPART_PARTS);
		if (expectedPartCount > 1) {
			Assert.isTrue(partSize >= MIN_MULTIPART_PART_SIZE,
					"partSize must be at least 5 MiB when multipart has more than one part");
		}
	}

	private CreateBucketRequest buildCreateBucketRequest(String bucketName) {
		CreateBucketRequest.Builder builder = CreateBucketRequest.builder().bucket(bucketName);
		String endpoint = ossProperties.getEndpoint();
		String region = ossProperties.getRegion();
		if (isAwsS3Endpoint(endpoint) && region != null && !region.isEmpty() && !Region.US_EAST_1.id().equals(region)) {
			try {
				builder.createBucketConfiguration(CreateBucketConfiguration.builder()
					.locationConstraint(BucketLocationConstraint.fromValue(region))
					.build());
			}
			catch (IllegalArgumentException ignored) {
				// 自定义 endpoint 或新 region 场景，交给服务端自行处理
			}
		}
		return builder.build();
	}

	private boolean isAwsS3Endpoint(String endpoint) {
		try {
			String host = URI.create(endpoint).getHost();
			return host != null && host.endsWith("amazonaws.com");
		}
		catch (Exception ignored) {
			return false;
		}
	}

}
