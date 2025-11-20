package com.cmliy.springweb.service;

import com.cmliy.springweb.exception.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 🖼️ 图片服务 - Image Service
 *
 * 这个服务类处理图片的上传、存储、缩略图生成和管理功能。
 * 支持多种图片格式，自动生成缩略图，提供安全的文件命名。
 *
 * @author Claude
 * @since 2025-01-20
 */
@Slf4j
@Service
public class ImageService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageService.class);

    /**
     * 📁 图片存储根目录
     */
    @Value("${app.image.storage.path:./uploads/images}")
    private String imageStoragePath;

    /**
     * 📁 缩略图存储目录
     */
    @Value("${app.image.thumbnail.path:./uploads/thumbnails}")
    private String thumbnailPath;

    /**
     * 📊 允许的图片格式
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    /**
     * 📏 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 🖼️ 缩略图尺寸
     */
    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;

    /**
     * 🏷️ 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * 📋 初始化方法
     *
     * 确保存储目录存在，如果不存在则创建。
     */
    public void initialize() {
        try {
            createDirectoryIfNotExists(imageStoragePath);
            createDirectoryIfNotExists(thumbnailPath);
            log.info("图片存储目录初始化完成: {}", imageStoragePath);
            log.info("缩略图存储目录初始化完成: {}", thumbnailPath);
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
        // 🔍 验证文件
        validateImageFile(file);

        try {
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

            // 🖼️ 生成缩略图
            String thumbnailFilename = generateThumbnail(imagePath, safeFilename, category);

            // 📊 获取文件信息
            long fileSize = Files.size(imagePath);
            String imageUrl = buildImageUrl(category, safeFilename);
            String thumbnailUrl = buildThumbnailUrl(category, thumbnailFilename);

            // 📋 返回上传结果
            return new ImageUploadResult(
                    imageUrl,
                    thumbnailUrl,
                    safeFilename,
                    thumbnailFilename,
                    fileSize,
                    file.getContentType(),
                    LocalDateTime.now()
            );

        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new ImageUploadException("图片上传失败: " + e.getMessage(), e);
        }
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
     * 删除原图和对应的缩略图。
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

            // 🗑️ 删除缩略图
            String thumbnailFilename = getThumbnailFilename(filename);
            Path thumbnailFilePath = Paths.get(thumbnailPath, category, thumbnailFilename);
            Files.deleteIfExists(thumbnailFilePath);
            log.info("缩略图删除成功: {}", thumbnailFilePath);

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

            String thumbnailFilename = getThumbnailFilename(filename);
            boolean thumbnailExists = Files.exists(
                    Paths.get(thumbnailPath, category, thumbnailFilename)
            );

            return new ImageFileInfo(
                    filename,
                    thumbnailFilename,
                    fileSize,
                    contentType,
                    lastModified,
                    thumbnailExists
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

            // 🧹 清理缩略图
            Path thumbnailRootPath = Paths.get(thumbnailPath);
            if (Files.exists(thumbnailRootPath)) {
                deletedCount += (int) Files.walk(thumbnailRootPath)
                        .filter(path -> !Files.isDirectory(path))
                        .filter(path -> isFileOlderThan(path, cutoffTime))
                        .peek(path -> log.info("删除过期缩略图: {}", path))
                        .map(path -> {
                            try {
                                return Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.error("删除缩略图文件失败: {}", path, e);
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
     * 🖼️ 生成缩略图
     *
     * @param imagePath 原图路径
     * @param filename 原文件名
     * @param category 图片类别
     * @return 缩略图文件名
     */
    private String generateThumbnail(Path imagePath, String filename, String category) throws IOException {
        String thumbnailFilename = getThumbnailFilename(filename);
        Path categoryThumbnailPath = Paths.get(thumbnailPath, category);
        createDirectoryIfNotExists(categoryThumbnailPath.toString());
        Path thumbnailPath = categoryThumbnailPath.resolve(thumbnailFilename);

        // 📖 读取原图
        BufferedImage originalImage = ImageIO.read(imagePath.toFile());
        if (originalImage == null) {
            throw new ImageUploadException("无法读取原图: " + imagePath);
        }

        // 🖼️ 生成缩略图
        BufferedImage thumbnail = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);

        // 💾 保存缩略图
        String formatName = getFileExtension(filename);
        ImageIO.write(thumbnail, formatName, thumbnailPath.toFile());

        log.info("缩略图生成成功: {}", thumbnailPath);
        return thumbnailFilename;
    }

    /**
     * 🏷️ 获取缩略图文件名
     *
     * @param filename 原文件名
     * @return 缩略图文件名
     */
    private String getThumbnailFilename(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "thumb_" + filename;
        }
        String name = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex + 1);
        return "thumb_" + name + "." + extension;
    }

    /**
     * 🔗 构建图片URL
     *
     * @param category 图片类别
     * @param filename 文件名
     * @return 图片URL
     */
    private String buildImageUrl(String category, String filename) {
        return String.format("/uploads/images/%s/%s", category, filename);
    }

    /**
     * 🔗 构建缩略图URL
     *
     * @param category 图片类别
     * @param filename 缩略图文件名
     * @return 缩略图URL
     */
    private String buildThumbnailUrl(String category, String filename) {
        return String.format("/uploads/thumbnails/%s/%s", category, filename);
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
        private final String thumbnailUrl;
        private final String filename;
        private final String thumbnailFilename;
        private final long fileSize;
        private final String contentType;
        private final LocalDateTime uploadTime;

        public ImageUploadResult(String imageUrl, String thumbnailUrl, String filename,
                               String thumbnailFilename, long fileSize, String contentType,
                               LocalDateTime uploadTime) {
            this.imageUrl = imageUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.filename = filename;
            this.thumbnailFilename = thumbnailFilename;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.uploadTime = uploadTime;
        }

        // Getter方法
        public String getImageUrl() { return imageUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public String getFilename() { return filename; }
        public String getThumbnailFilename() { return thumbnailFilename; }
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
     * 📋 图片文件信息类
     */
    public static class ImageFileInfo {
        private final String filename;
        private final String thumbnailFilename;
        private final long fileSize;
        private final String contentType;
        private final LocalDateTime lastModified;
        private final boolean thumbnailExists;

        public ImageFileInfo(String filename, String thumbnailFilename, long fileSize,
                           String contentType, LocalDateTime lastModified, boolean thumbnailExists) {
            this.filename = filename;
            this.thumbnailFilename = thumbnailFilename;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.lastModified = lastModified;
            this.thumbnailExists = thumbnailExists;
        }

        // Getter方法
        public String getFilename() { return filename; }
        public String getThumbnailFilename() { return thumbnailFilename; }
        public long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
        public LocalDateTime getLastModified() { return lastModified; }
        public boolean isThumbnailExists() { return thumbnailExists; }

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