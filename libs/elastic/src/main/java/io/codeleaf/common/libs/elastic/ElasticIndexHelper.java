package io.codeleaf.common.libs.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.IndexState;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class ElasticIndexHelper {

    private final ElasticsearchClient client;
    private final boolean readOnly;

    public ElasticIndexHelper(ElasticsearchClient client, boolean readOnly) {
        this.client = client;
        this.readOnly = readOnly;
    }

    public Set<String> listIndices() throws IOException {
        return client.indices().stats().indices().keySet();
    }

    public boolean existsIndex(String indexName) throws IOException {
        return client.indices().exists(k -> k.index(indexName)).value();
    }

    public TypeMapping getIndex(String indexName) throws IOException {
        IndexState indexState = client.indices().get(k -> k.index(indexName)).get(indexName);
        return indexState != null ? indexState.mappings() : null;
    }

    public void createIndex(String indexName, TypeMapping typeMapping) throws IOException {
        ensureInWriteMode();
        client.indices().create(k -> k.index(indexName).mappings(typeMapping));
    }

    public boolean hasDocuments(String indexName) throws IOException {
        return !client.search(b -> b.index(indexName).source(s -> s.fetch(false)), Void.class).hits().hits().isEmpty();
    }

    public boolean deleteIndex(String indexName) throws IOException {
        ensureInWriteMode();
        if (!existsIndex(indexName)) {
            return false;
        }
        if (hasDocuments(indexName)) {
            throw new IllegalStateException("Documents exists with this type! First delete those.");
        }
        client.indices().delete(d -> d.index(indexName));
        return false;
    }

    public boolean hasDocument(String indexName, String documentId) throws IOException {
        return client.get(b -> b.index(indexName).id(documentId).source(s -> s.fetch(false)), Void.class).found();
    }

    public <T> T getDocument(String indexName, String documentId, Class<T> documentType) throws IOException {
        return client.get(b -> b.index(indexName).id(documentId).source(s -> s.fetch(true)), documentType).source();
    }

    public <T> String indexDocument(String indexName, T document) throws IOException {
        ensureInWriteMode();
        return client.index(b -> b.index(indexName).document(document)).id();
    }

    public <T> void indexDocument(String indexName, String documentId, T document) throws IOException {
        ensureInWriteMode();
        client.index(b -> b.index(indexName).id(documentId).document(document));
    }

    public <T> HitsMetadata<T> searchDocuments(String indexName, int limit, Class<T> documentType) throws IOException {
        Objects.requireNonNull(indexName);
        if (limit < 0 || limit > 10_000) {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(documentType);
        return client.search(b -> b.index(indexName).size(limit), documentType).hits();
    }

    public <T> HitsMetadata<T> searchDocuments(String indexName, Query query, int limit, Class<T> documentType) throws IOException {
        Objects.requireNonNull(indexName);
        if (limit < 0 || limit > 10_000) {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(documentType);
        return client.search(b -> b.index(indexName).query(query).size(limit), documentType).hits();
    }

    public boolean removeDocument(String indexName, String documentId) throws IOException {
        ensureInWriteMode();
        return client.delete(b -> b.index(indexName).id(documentId)).result() == Result.Deleted;
    }

    private void ensureInWriteMode() {
        if (readOnly) {
            throw new IllegalStateException("We are in read-only mode!");
        }
    }
}
