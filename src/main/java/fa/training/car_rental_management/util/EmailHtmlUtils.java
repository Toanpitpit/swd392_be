package fa.training.car_rental_management.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class để xử lý HTML content cho email
 * - Normalize images
 * - Convert data:image thành CID (Content ID) - inline images
 * - Hỗ trợ TinyMCE embedded images
 */
public class EmailHtmlUtils {

    /**
     * Inline image class - lưu trữ thông tin ảnh inline
     */
    @Getter
    @AllArgsConstructor
    public static class InlineImage {
        private String contentId;
        private byte[] bytes;
        private String mimeType;
    }

    /**
     * Result class - trả về HTML đã xử lý + inline images
     */
    @Getter
    @AllArgsConstructor
    public static class ProcessResult {
        private String processedHtml;
        private Map<String, InlineImage> inlineImages;
    }

    /**
     * Normalize images để phù hợp với email clients
     * - Set max-width
     * - Loại bỏ height để giữ aspect ratio
     * - Thêm border-radius nếu cần
     *
     * @param html HTML content
     * @return Normalized HTML
     */
    public static String normalizeImgsForEmail(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }

        try {
            Document doc = Jsoup.parse(html);
            Elements imgs = doc.select("img");

            for (Element img : imgs) {
                // Set max-width để không vượt quá email container (thường 600px)
                String style = img.attr("style");
                if (style == null || style.isBlank()) {
                    style = "max-width: 100%; height: auto; display: block;";
                } else {
                    if (!style.contains("max-width")) {
                        style += "max-width: 100%;";
                    }
                    if (!style.contains("height")) {
                        style += "height: auto;";
                    }
                }
                img.attr("style", style);

                // Remove width/height attributes để tránh CSS conflict
                img.removeAttr("width");
                img.removeAttr("height");
            }

            return doc.body().html();

        } catch (Exception e) {
            // Nếu parsing thất bại, trả về HTML gốc
            return html;
        }
    }

    /**
     * Convert data:image base64 images thành CID (Content ID)
     * Các images từ TinyMCE thường là data:image format
     * Email clients hỗ trợ CID tốt hơn data URL
     *
     * @param html HTML content chứa data:image
     * @return ProcessResult chứa HTML đã xử lý + Map inline images
     */
    public static ProcessResult convertDataImagesToCid(String html) {
        if (html == null || html.isBlank()) {
            return new ProcessResult(html, new HashMap<>());
        }

        Map<String, InlineImage> inlineImages = new HashMap<>();
        String processedHtml = html;

        try {
            Document doc = Jsoup.parse(html);
            Elements imgs = doc.select("img[src^='data:image']");

            for (Element img : imgs) {
                String dataSrc = img.attr("src");

                try {
                    // Parse data:image URL
                    // Format: data:image/png;base64,iVBORw0KGgo...
                    String[] parts = dataSrc.split(",", 2);
                    if (parts.length != 2) {
                        continue;
                    }

                    String mimeTypePart = parts[0]; // data:image/png;base64
                    String base64Data = parts[1];

                    // Extract MIME type
                    String mimeType = "image/png"; // default
                    Pattern mimePattern = Pattern.compile("data:([^;]+)");
                    Matcher mimeMatcher = mimePattern.matcher(mimeTypePart);
                    if (mimeMatcher.find()) {
                        mimeType = mimeMatcher.group(1);
                    }

                    // Generate unique CID
                    String contentId = "image-" + UUID.randomUUID().toString();

                    // Decode base64 to bytes
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

                    // Create InlineImage
                    InlineImage inlineImage = new InlineImage(contentId, imageBytes, mimeType);
                    inlineImages.put(contentId, inlineImage);

                    // Replace data:image with cid:
                    img.attr("src", "cid:" + contentId);

                } catch (Exception e) {
                    // Skip this image if conversion fails
                    continue;
                }
            }

            processedHtml = doc.body().html();

        } catch (Exception e) {
            // Nếu parsing thất bại, trả về HTML gốc với empty images map
            return new ProcessResult(html, inlineImages);
        }

        return new ProcessResult(processedHtml, inlineImages);
    }

    /**
     * Extract images từ HTML - dùng để preview/validate
     *
     * @param html HTML content
     * @return List image URLs
     */
    public static java.util.List<String> extractImageUrls(String html) {
        java.util.List<String> urls = new java.util.ArrayList<>();

        if (html == null || html.isBlank()) {
            return urls;
        }

        try {
            Document doc = Jsoup.parse(html);
            Elements imgs = doc.select("img");

            for (Element img : imgs) {
                String src = img.attr("src");
                if (src != null && !src.isBlank()) {
                    urls.add(src);
                }
            }

        } catch (Exception e) {
            // Return empty list on error
        }

        return urls;
    }

    /**
     * Remove all images từ HTML
     * Dùng khi cần text-only version
     *
     * @param html HTML content
     * @return HTML without images
     */
    public static String removeAllImages(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }

        try {
            Document doc = Jsoup.parse(html);
            Elements imgs = doc.select("img");
            imgs.remove();
            return doc.body().html();

        } catch (Exception e) {
            return html;
        }
    }

    /**
     * Sanitize HTML - loại bỏ dangerous tags
     * Dùng để prevent XSS attacks
     *
     * @param html HTML content
     * @return Sanitized HTML
     */
    public static String sanitizeHtml(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }

        try {
            Document doc = Jsoup.parse(html);

            // Remove script tags
            doc.select("script").remove();
            doc.select("iframe").remove();
            doc.select("object").remove();
            doc.select("embed").remove();

            // Remove on* event handlers
            for (Element element : doc.getAllElements()) {
                for (Attribute attr : element.attributes().asList()) {
                    if (attr.getKey().toLowerCase().startsWith("on")) {
                        element.removeAttr(attr.getKey());
                    }
                }
            }

            return doc.body().html();

        } catch (Exception e) {
            return html;
        }
    }

    /**
     * Compress HTML - loại bỏ whitespace thừa
     * Để giảm kích thước email
     *
     * @param html HTML content
     * @return Compressed HTML
     */
    public static String compressHtml(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }

        // Remove comments
        html = html.replaceAll("<!--.*?-->", "");

        // Remove unnecessary whitespace
        html = html.replaceAll(">\\s+<", "><");

        // Remove newlines and tabs
        html = html.replaceAll("\\n|\\r|\\t", "");

        // Replace multiple spaces with single space
        html = html.replaceAll("\\s+", " ");

        return html.trim();
    }

    /**
     * Validate base64 string
     *
     * @param base64 Base64 string
     * @return true if valid
     */
    public static boolean isValidBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            return false;
        }

        try {
            java.util.Base64.getDecoder().decode(base64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Convert base64 to bytes
     *
     * @param base64 Base64 string
     * @return Decoded bytes
     */
    public static byte[] base64ToBytes(String base64) {
        if (base64 == null || base64.isBlank()) {
            return new byte[0];
        }

        try {
            return java.util.Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return new byte[0];
        }
    }

    /**
     * Convert bytes to base64
     *
     * @param bytes Bytes to encode
     * @return Base64 string
     */
    public static String bytesToBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
}

