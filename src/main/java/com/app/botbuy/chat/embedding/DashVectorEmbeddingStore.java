package com.app.botbuy.chat.embedding;

import com.aliyun.dashvector.DashVectorClient;
import com.aliyun.dashvector.DashVectorCollection;
import com.aliyun.dashvector.models.Doc;
import com.aliyun.dashvector.models.Vector;
import com.aliyun.dashvector.models.requests.DeleteDocRequest;
import com.aliyun.dashvector.models.requests.QueryDocRequest;
import com.aliyun.dashvector.models.requests.UpsertDocRequest;
import com.aliyun.dashvector.models.responses.Response;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DashVectorEmbeddingStore implements EmbeddingStore<TextSegment> {
    private final DashVectorClient client;
    private final String collectionName;
    private final Double queryMatchMaxScore;
    private final int queryMatchMaxResults;

    public DashVectorEmbeddingStore(DashVectorClient client, String collectionName, Double queryMatchMaxScore, int queryMatchMaxResults) {
        this.client = client;
        this.collectionName = collectionName;
        this.queryMatchMaxScore = queryMatchMaxScore;
        this.queryMatchMaxResults = queryMatchMaxResults;
    }

    @Override
    public String add(Embedding embedding) {
        String id = UUID.randomUUID().toString();
        this.add(id, embedding);
        return id;
    }

    @Override
    public void add(String id, Embedding embedding) {
        this.addInternal(id, embedding, null);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        String id = UUID.randomUUID().toString();
        this.addInternal(id, embedding, textSegment);
        return id;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        List<String> ids = generateRandomIds(embeddings.size());
        this.addAllInternal(ids, embeddings, embedded);
        return ids;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<String> ids = generateRandomIds(embeddings.size());
        this.addAllInternal(ids, embeddings, null);
        return ids;
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        String filterString = request.filter() != null ? FilterParser.parse(request.filter()) : null;
        Vector queryVector = Vector.builder().value(request.queryEmbedding().vectorAsList()).build();
        QueryDocRequest.Builder qb = QueryDocRequest.builder()
                .vector(queryVector)
                .topk(queryMatchMaxResults)
                .includeVector(true);
        if (filterString != null) {
            qb.filter(filterString);
        }
        Response<List<Doc>> query = this.client.get(this.collectionName).query(qb.build());
        if (!query.isSuccess()) {
            throw new RuntimeException("DashVectorEmbeddingStore 查询失败: " + query.getMessage());
        }
        List<EmbeddingMatch<TextSegment>> matched = filterQueryResult(query.getOutput());
        log.debug("向量检索匹配: {}", matched);
        return new EmbeddingSearchResult<>(matched);
    }

    private List<EmbeddingMatch<TextSegment>> filterQueryResult(List<Doc> docs) {
        List<EmbeddingMatch<TextSegment>> matched = new ArrayList<>();
        for (Doc doc : docs) {
            Double score = (double) doc.getScore();
            if (this.queryMatchMaxScore.compareTo(score) < 0) {
                continue;
            }
            Embedding matchedEmbedding = Embedding.from((List<Float>) doc.getVector().getValue());
            Map<String, Object> fields = doc.getFields();
            String text = (String) fields.get("text");
            Map<String, Object> metadata = new HashMap<>();
            if (fields.get("index") != null) {
                metadata.put("index", fields.get("index"));
            }
            TextSegment textSegment = new TextSegment(text, Metadata.from(metadata));
            matched.add(new EmbeddingMatch<>(score, doc.getId(), matchedEmbedding, textSegment));
        }
        if (!matched.isEmpty()) {
            return List.of(matched.get(0));
        }
        return matched;
    }

    private void addInternal(String id, Embedding embedding, TextSegment textSegment) {
        this.addAllInternal(Collections.singletonList(id), Collections.singletonList(embedding),
                textSegment == null ? null : Collections.singletonList(textSegment));
    }

    private void addAllInternal(List<String> ids, List<Embedding> embeddings, List<TextSegment> textSegments) {
        List<Doc> docs = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            Embedding embedding = embeddings.get(i);
            TextSegment textSegment = textSegments.get(i);
            Vector vector = Vector.builder().value(embedding.vectorAsList()).build();
            Map<String, Object> metadataMap = textSegment.metadata().toMap();
            if (metadataMap.get("index") != null && metadataMap.get("id") != null) {
                id = metadataMap.get("id").toString();
            }
            Doc.Builder docBuilder = Doc.builder()
                    .id(id)
                    .vector(vector)
                    .field("index", metadataMap.get("index") != null ? Integer.valueOf(metadataMap.get("index").toString()) : 0)
                    .field("text", textSegment.text());
            if (metadataMap.get("channel_type") != null) {
                docBuilder.field("channel_type", metadataMap.get("channel_type"));
            }
            if (metadataMap.get("business_type") != null) {
                docBuilder.field("business_type", metadataMap.get("business_type"));
            }
            if (metadataMap.get("consult_issue_type") != null) {
                docBuilder.field("consult_issue_type", metadataMap.get("consult_issue_type"));
            }
            docs.add(docBuilder.build());
        }
        UpsertDocRequest request = UpsertDocRequest.builder().docs(docs).build();
        DashVectorCollection dashVectorCollection = this.client.get(this.collectionName);
        Response<Void> response = dashVectorCollection.upsert(request);
        if (!response.isSuccess()) {
            throw new RuntimeException("DashVectorEmbeddingStore 写入失败: " + response.getMessage());
        }
    }

    private List<String> generateRandomIds(int size) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(UUID.randomUUID().toString());
        }
        return ids;
    }

    public void deleteById(String id) {
        DashVectorCollection dashVectorCollection = this.client.get(this.collectionName);
        DeleteDocRequest deleteRequest = DeleteDocRequest.builder().id(id).build();
        Response<Void> response = dashVectorCollection.delete(deleteRequest);
        if (!response.isSuccess()) {
            throw new RuntimeException("DashVectorEmbeddingStore 删除失败: " + response.getMessage());
        }
        log.info("已删除向量文档 id={}", id);
    }
}
