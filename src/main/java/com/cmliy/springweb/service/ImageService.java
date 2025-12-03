package com.cmliy.springweb.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cmliy.springweb.exception.ImageUploadException;

import lombok.extern.slf4j.Slf4j;

/**
 * 🖼️ 图片服务 - Image Service
 *
 * 这个服务类处理图片的上传、存储、缩略图生成和管理功能。
 * 支持多种图片格式，自动生成缩略图，提供安全的文件命名。

 */
@Slf4j
@Service
public class ImageService extends BaseService {

    /**
     * 📁 图片存储根目录
     */
    @Value("${app.image.storage.path:./uploads/images}")
    private String imageStoragePath;

    
    /**
     * 📊 允许的图片格式
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    /**
     * 📏 最大文件大小（5MB）
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    
    /**
     * 🏷️ 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * 🏷️ 图片文件专用时间戳格式化器
     */
    private static final DateTimeFormatter IMAGE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 📋 初始化方法
     *
     * 确保存储目录存在，如果不存在则创建。
     */
    public void initialize() {
        try {
            createDirectoryIfNotExists(imageStoragePath);
            log.info("图片存储目录初始化完成: {}", imageStoragePath);
        } catch (IOException e) {
            log.error("初始化图片存储目录失败", e);
            throw new ImageUploadException("初始化图片存储目录失败", e);
        }
    }

    /**
     * 📤 上传图片
     *
     * 处理图片上传，生成安全的文件名，保存原图并生成缩略图。
     *
     * @param file 上传的文件
     * @param category 图片类别（如：products, users等）
     * @return 图片URL信息
     */
    public ImageUploadResult uploadImage(MultipartFile file, String category) {
        return executeWithLogAndIO("上传图片", () -> {
            // 🔍 验证文件
            validateImageFile(file);

            // 🏷️ 生成安全的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String safeFilename = String.format("%s_%s_%s.%s", category, timestamp, uuid, fileExtension);

            // 📁 构建存储路径
            Path categoryPath = Paths.get(imageStoragePath, category);
            createDirectoryIfNotExists(categoryPath.toString());
            Path imagePath = categoryPath.resolve(safeFilename);

            // 💾 保存原图
            file.transferTo(imagePath.toFile());
            log.info("原图保存成功: {}", imagePath);

            // 📊 获取文件信息
            long fileSize = Files.size(imagePath);
            String imageUrl = buildImageUrl(category, safeFilename);

            // 📋 返回上传结果
            return new ImageUploadResult(
                    imageUrl,
                    safeFilename,
                    fileSize,
                    file.getContentType(),
                    LocalDateTime.now()
            );
        }, file.getOriginalFilename(), category);
    }

    /**
     * 📤 批量上传图片
     *
     * 处理多个图片的上传，为每个图片生成缩略图。
     *
     * @param files 上传的文件数组
     * @param category 图片类别
     *return 上传结果列表
     */
    public List<ImageUploadResult> uploadImages(MultipartFile[] files, String category) {
        return Arrays.stream(files)
                .filter(file -> !file.isEmpty())
                .map(file -> uploadImage(file, category))
                .toList();
    }

    /**
     * 🗑️ 删除图片
     *
     * 删除原图。
     *
     * @param category 图片类别
     * @param filename 文件名
     */
    public void deleteImage(String category, String filename) {
        try {
            // 🗑️ 删除原图
            Path imagePath = Paths.get(imageStoragePath, category, filename);
            Files.deleteIfExists(imagePath);
            log.info("原图删除成功: {}", imagePath);

        } catch (IOException e) {
            log.error("删除图片失败: category={}, filename={}", category, filename, e);
            throw new ImageUploadException("删除图片失败: " + e.getMessage(), e);
        }
    }

    /**
     * 🗑️ 批量删除图片
     *
     * 删除指定类别的所有图片和缩略图。
     *
     * @param category 图片类别
     * @param filenames 文件名列表
     */
    public void deleteImages(String category, List<String> filenames) {
        filenames.forEach(filename -> deleteImage(category, filename));
    }

    /**
     * 🔄 更新图片
     *
     * 删除旧图片，上传新图片。
     *
     * @param oldCategory 旧图片类别
     * @param oldFilename 旧文件名
     * @param newFile 新文件
     * @param newCategory 新图片类别
     * @return 新图片的上传结果
     */
    public ImageUploadResult updateImage(String oldCategory, String oldFilename,
                                         MultipartFile newFile, String newCategory) {
        // 🗑️ 删除旧图片
        deleteImage(oldCategory, oldFilename);

        // 📤 上传新图片
        return uploadImage(newFile, newCategory);
    }

    /**
     * 📋 检查图片是否存在
     *
     * @param category 图片类别
     * @param filename 文件名
     * @return 图片存在返回true，否则返回false
     */
    public boolean imageExists(String category, String filename) {
        Path imagePath = Paths.get(imageStoragePath, category, filename);
        return Files.exists(imagePath);
    }

    /**
     * 📋 获取图片文件信息
     *
     * @param category 图片类别
     * @param filename 文件名
     * @return 图片文件信息，不存在时返回null
     */
    public ImageFileInfo getImageInfo(String category, String filename) {
        try {
            Path imagePath = Paths.get(imageStoragePath, category, filename);
            if (!Files.exists(imagePath)) {
                return null;
            }

            long fileSize = Files.size(imagePath);
            String contentType = Files.probeContentType(imagePath);
            LocalDateTime lastModified = Files.getLastModifiedTime(imagePath)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            return new ImageFileInfo(
                    filename,
                    fileSize,
                    contentType,
                    lastModified
            );

        } catch (IOException e) {
            log.error("获取图片信息失败: category={}, filename={}", category, filename, e);
            return null;
        }
    }

    /**
     * 🧹 清理过期图片
     *
     * 删除指定时间之前的所有图片。
     *
     * @param cutoffTime 截止时间
     * @return 删除的图片数量
     */
    public int cleanupOldImages(LocalDateTime cutoffTime) {
        try {
            Path rootPath = Paths.get(imageStoragePath);
            int deletedCount = 0;

            if (Files.exists(rootPath)) {
                deletedCount += (int) Files.walk(rootPath)
                        .filter(path -> !Files.isDirectory(path))
                        .filter(path -> isFileOlderThan(path, cutoffTime))
                        .peek(path -> log.info("删除过期图片: {}", path))
                        .map(path -> {
                            try {
                                return Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.error("删除文件失败: {}", path, e);
                                return false;
                            }
                        })
                        .count();
            }

            log.info("清理过期图片完成，删除数量: {}", deletedCount);
            return deletedCount;

        } catch (IOException e) {
            log.error("清理过期图片失败", e);
            throw new ImageUploadException("清理过期图片失败: " + e.getMessage(), e);
        }
    }

    // ==================== 🔧 私有方法 ====================

    /**
     * 🔍 验证图片文件
     *
     * @param file 上传的文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ImageUploadException("上传的文件为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageUploadException("文件大小超过限制，最大允许" + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ImageUploadException("不支持的文件类型，仅支持: " + ALLOWED_IMAGE_TYPES);
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ImageUploadException("文件不是有效的图片格式");
            }
        } catch (IOException e) {
            throw new ImageUploadException("读取图片文件失败", e);
        }
    }

    /**
     * 📁 创建目录（如果不存在）
     *
     * @param path 目录路径
     */
    private void createDirectoryIfNotExists(String path) throws IOException {
        Path directoryPath = Paths.get(path);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            log.info("创建目录: {}", directoryPath);
        }
    }

    /**
     * 📄 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名（小写）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "jpg"; // 默认扩展名
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    
    
    /**
     * 🔗 构建图片URL
     *
     * @param category 图片类别
     * @param filename 文件名
     * @return 图片URL（带时间戳参数防止缓存）
     */
    private String buildImageUrl(String category, String filename) {
        // 添加时间戳参数防止浏览器缓存
        long timestamp = System.currentTimeMillis();
        return String.format("/api/uploads/images/%s/%s?t=%d", category, filename, timestamp);
    }

    
    /**
     * ⏰ 检查文件是否过期
     *
     * @param filePath 文件路径
     * @param cutoffTime 截止时间
     * @return 过期返回true，否则返回false
     */
    private boolean isFileOlderThan(Path filePath, LocalDateTime cutoffTime) {
        try {
            LocalDateTime fileTime = Files.getLastModifiedTime(filePath)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            return fileTime.isBefore(cutoffTime);
        } catch (IOException e) {
            log.error("检查文件时间失败: {}", filePath, e);
            return false;
        }
    }

    // ==================== 📋 内部类 ====================

    /**
     * 📤 图片上传结果类
     */
    public static class ImageUploadResult {
        private final String imageUrl;
        private final String filename;
        private final long fileSize;
        private final String contentType;
        private final LocalDateTime uploadTime;

        public ImageUploadResult(String imageUrl, String filename, long fileSize, String contentType,
                               LocalDateTime uploadTime) {
            this.imageUrl = imageUrl;
            this.filename = filename;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.uploadTime = uploadTime;
        }

        // Getter方法
        public String getImageUrl() { return imageUrl; }
        public String getFilename() { return filename; }
        public long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
        public LocalDateTime getUploadTime() { return uploadTime; }

        /**
         * 📊 获取格式化的文件大小
         */
        public String getFormattedFileSize() {
            if (fileSize < 1024) {
                return fileSize + " B";
            } else if (fileSize < 1024 * 1024) {
                return String.format("%.1f KB", fileSize / 1024.0);
            } else {
                return String.format("%.1f MB", fileSize / (1024.0 * 1024));
            }
        }
    }

    /**
     * 📦 上传商品图片（使用商品ID+image+时间戳命名规则）
     *
     * @param file 上传的文件
     * @param productId 商品ID
     * @return 图片上传结果
     */
    public ImageUploadResult uploadProductImage(MultipartFile file, Long productId) {
        return executeWithLogAndIO("上传商品图片", () -> {
            // 🔍 验证文件
            validateImageFile(file);

            // 🏷️ 使用商品ID+image+时间戳的命名规则
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(IMAGE_TIMESTAMP_FORMATTER);
            String safeFilename = productId + "image" + timestamp + "." + fileExtension;

            // 📁 构建存储路径
            Path categoryPath = Paths.get(imageStoragePath, "products");
            createDirectoryIfNotExists(categoryPath.toString());
            Path imagePath = categoryPath.resolve(safeFilename);

            // 💾 保存新图片
            file.transferTo(imagePath.toFile());
            log.info("商品图片保存成功: {}", imagePath);

            // 📊 获取文件信息
            long fileSize = Files.size(imagePath);
            String imageUrl = buildImageUrl("products", safeFilename);

            // 📋 返回上传结果
            return new ImageUploadResult(
                    imageUrl,
                    safeFilename,
                    fileSize,
                    file.getContentType(),
                    LocalDateTime.now()
            );

        }, file.getOriginalFilename(), productId);
    }

    /**
     * 🗑️ 软删除商品图片（重命名为删除格式）
     *
     * @param productId 商品ID
     * @param currentFilename 当前图片文件名
     */
    public void softDeleteProductImage(Long productId, String currentFilename) {
        executeWithLogAndIO("软删除商品图片", () -> {
            if (currentFilename == null || currentFilename.isEmpty()) {
                return; // 没有图片需要删除
            }

            // 提取文件扩展名
            String fileExtension = getFileExtension(currentFilename);
            String timestamp = LocalDateTime.now().format(IMAGE_TIMESTAMP_FORMATTER);
            String deleteFilename = productId + "del" + timestamp + "." + fileExtension;

            Path categoryPath = Paths.get(imageStoragePath, "products");
            Path oldImagePath = categoryPath.resolve(currentFilename);
            Path newImagePath = categoryPath.resolve(deleteFilename);

            if (Files.exists(oldImagePath)) {
                Files.move(oldImagePath, newImagePath);
                log.info("图片软删除成功: {} -> {}", oldImagePath, newImagePath);
            } else {
                log.warn("要删除的图片文件不存在: {}", oldImagePath);
            }
        });
    }

    /**
     * 📋 图片文件信息类
     */
    public static class ImageFileInfo {
        private final String filename;
        private final long fileSize;
        private final String contentType;
        private final LocalDateTime lastModified;

        public ImageFileInfo(String filename, long fileSize, String contentType, LocalDateTime lastModified) {
            this.filename = filename;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.lastModified = lastModified;
        }

        // Getter方法
        public String getFilename() { return filename; }
        public long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
        public LocalDateTime getLastModified() { return lastModified; }

        /**
         * 📊 获取格式化的文件大小
         */
        public String getFormattedFileSize() {
            if (fileSize < 1024) {
                return fileSize + " B";
            } else if (fileSize < 1024 * 1024) {
                return String.format("%.1f KB", fileSize / 1024.0);
            } else {
                return String.format("%.1f MB", fileSize / (1024.0 * 1024));
            }
        }
    }
}