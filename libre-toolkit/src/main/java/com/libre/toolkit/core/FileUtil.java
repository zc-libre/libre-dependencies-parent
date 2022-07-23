package com.libre.toolkit.core;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.annotation.CheckForNull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author ZC
 * @date 2021/12/27 23:23
 */

@Slf4j
public class FileUtil  {

	/**
	 * 创建文件件
	 * @param filePath 文件夹路径
	 */
	public static void createDirectory(String filePath) {
		Preconditions.checkNotNull(filePath, "filePath must not be null");
		Path path = Paths.get(filePath);
		createDirectory(path);
	}

	/**
	 * 创建文件件
	 * @param path 文件夹路径
	 */
	public static void createDirectory(Path path) {
		Objects.requireNonNull(path, "path must not be null");
		try {
			Files.createDirectory(path);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 文件拷贝
	 * @param sourcePath 源文件
	 * @param targetPath 目标文件
	 */
	public static void copy(String sourcePath, String targetPath) {
		try {
			Files.copy(Paths.get(sourcePath), Paths.get(targetPath));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 文件拷贝
	 * @param sourcePath 源文件
	 * @param outputStream 目标输出流
	 */
	public static void copy(String sourcePath, OutputStream outputStream) {
		try {
			Files.copy(Paths.get(sourcePath), outputStream);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}


}
