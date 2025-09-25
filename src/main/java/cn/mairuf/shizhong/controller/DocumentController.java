package cn.mairuf.shizhong.controller;

import cn.mairuf.shizhong.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文档 controller
 *
 * @author 阿麦
 * @date 2025-09-25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    /**
     * 文档处理 service
     */
    private final DocumentService documentService;

    /**
     * 上传文档
     *
     * @param file 文件
     *
     * @return {@link ResponseEntity }<{@link String }>
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            // 文件验证
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("上传的文件不能为空");
            }

            if (file.getSize() == 0) {
                return ResponseEntity.badRequest().body("上传的文件大小不能为0字节");
            }

            documentService.processDocument(file);
            return ResponseEntity.ok("文档上传并处理成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("文档处理失败: " + e.getMessage());
        }
    }

    /**
     * 搜索文件内容
     *
     * @param query      查询文本
     * @param maxResults 最大结果
     *
     * @return {@link ResponseEntity }<{@link List }<{@link String }>> 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchDocuments(
            @RequestParam("query") String query,
            @RequestParam(value = "maxResults", defaultValue = "5") int maxResults) {
        try {
            List<String> results = documentService.search(query, maxResults);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of("搜索失败: " + e.getMessage()));
        }
    }

    /**
     * 获取存储状态
     *
     * @return {@link ResponseEntity }<{@link Map }<{@link String }, {@link Object }>>
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = documentService.getStorageStats();
        return ResponseEntity.ok(stats);
    }

}
