package cn.mairuf.shizhong.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档处理 service
 *
 * @author 阿麦
 * @date 2025-09-25
 */
@Service
public class DocumentService {

    // 初始化本地嵌入模型
    private final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    // 连接本地 Chroma 向量库
    private final EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
            .baseUrl("http://localhost:18005")
            .collectionName("shizhong_kb")
            .build();

    // 分块策略：每个文本块的最大字符数, 相邻文本块之间的最大重叠字符数
    private final DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);

    /**
     * 处理文档
     *
     * @param file 文件
     *
     * @throws Exception 异常
     */
    public void processDocument(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            // 用 Tika 提取纯文本
            Tika tika = new Tika();
            String text = tika.parseToString(is);

            System.out.println("提取文本长度: " + text.length());

            // 构建 Document
            Document document = Document.from(text);

            // 分块
            List<TextSegment> segments = splitter.split(document);
            System.out.println("分块数量: " + segments.size());

            // 向量化 + 存储
            for (TextSegment segment : segments) {
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            }

            System.out.println("向量存储完成，共处理 " + segments.size() + " 个片段");
        }
    }

    /**
     * 检索相关文本片段
     */
    public List<String> search(String query, int maxResults) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(0.65) // 相似度阈值
                .build();
        return embeddingStore.search(request).matches().stream()
                .map(result -> {
                    System.out.println("匹配分数: " + result.score());
                    return result.embedded().text();
                })
                .toList();
    }

    /**
     * 验证向量存储状态
     */
    public Map<String, Object> getStorageStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 尝试执行一个简单的搜索来验证连接
            embeddingModel.embed("test");
            // 如果不抛出异常，说明连接正常
            stats.put("status", "connected");
            stats.put("collection", "shizhong_kb");
        } catch (Exception e) {
            stats.put("status", "error");
            stats.put("message", e.getMessage());
        }
        return stats;
    }

}