package org.nessrev.scriptorium.elasticSearch.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.elasticSearch.model.BookDocument;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Hit;


import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public void indexBook(Book book) {
        BookDocument doc = BookDocument.builder()
                .id(book.getId())
                .name(book.getName())
                .description(book.getDescription())
                .isPublic(book.getIsPublic())
                .build();
        try {
            elasticsearchClient.index(i -> i
                    .index("books")
                    .id(String.valueOf(doc.getId()))
                    .document(doc));
        } catch (IOException e) {
            throw new RuntimeException("Error indexing a book", e);
        }
    }

    public List<BookDocument> search(String keyword) {
        try {
            Query queryStringQuery = Query.of(q -> q
                    .queryString(qs -> qs
                            .query("*" + keyword + "*")));

            Query isPublicFilter = Query.of(q -> q
                    .term(t -> t
                            .field("isPublic")
                            .value(true)));

            Query boolQuery = Query.of(q -> q
                    .bool(b -> b
                            .must(queryStringQuery)
                            .filter(isPublicFilter)));

            SearchResponse<BookDocument> response = elasticsearchClient.search(s -> s
                            .index("books")
                            .query(boolQuery),
                    BookDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Error searching a book", e);
        }
    }

    public void delete(Book book) {
        try {
            elasticsearchClient.delete(d -> d
                    .index("books")
                    .id(String.valueOf(book.getId())));
        } catch (IOException e) {
            throw new RuntimeException("Error deleting a book", e);
        }
    }
}

