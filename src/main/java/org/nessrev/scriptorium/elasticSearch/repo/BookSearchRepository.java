package org.nessrev.scriptorium.elasticSearch.repo;

import org.nessrev.scriptorium.elasticSearch.model.BookDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, Long> {
}

