package vn.edu.epu.quanlyhoso.search.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.repository.ProjectRepository;
import vn.edu.epu.quanlyhoso.search.ProjectSearchIndexService;
import vn.edu.epu.quanlyhoso.search.dto.ProjectSearchCriteria;
import vn.edu.epu.quanlyhoso.search.dto.ProjectSearchIndexRebuildResponse;
import vn.edu.epu.quanlyhoso.search.util.ProjectLuceneDocumentMapper;
import vn.edu.epu.quanlyhoso.search.util.ProjectLuceneFieldNames;
import vn.edu.epu.quanlyhoso.search.util.VietnameseTextNormalizer;

@Service
public class ProjectSearchIndexServiceImpl implements ProjectSearchIndexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectSearchIndexServiceImpl.class);
    private static final int MAX_SEARCH_RESULTS = 10000;
    private static final String[] NORMALIZED_SEARCH_FIELDS = {
            ProjectLuceneFieldNames.TITLE_NORMALIZED,
            ProjectLuceneFieldNames.OBJECTIVE_NORMALIZED,
            ProjectLuceneFieldNames.EXPECTED_PRODUCT_NORMALIZED
    };

    private final ProjectRepository projectRepository;
    private final ProjectLuceneDocumentMapper documentMapper;
    private final Analyzer analyzer;
    private final Path indexPath;

    public ProjectSearchIndexServiceImpl(
            ProjectRepository projectRepository,
            ProjectLuceneDocumentMapper documentMapper,
            Analyzer projectSearchAnalyzer,
            Path projectLuceneIndexPath) {
        this.projectRepository = projectRepository;
        this.documentMapper = documentMapper;
        this.analyzer = projectSearchAnalyzer;
        this.indexPath = projectLuceneIndexPath;
    }

    @Override
    public ProjectSearchIndexRebuildResponse rebuildIndex() {
        LocalDateTime startedAt = LocalDateTime.now();
        ProjectSearchIndexRebuildResponse response = new ProjectSearchIndexRebuildResponse();
        response.setStartedAt(startedAt);

        List<Project> projects = projectRepository.findAll();
        try {
            ensureIndexDirectoryExists();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            // Rebuild is the recovery path when the filesystem index is missing, stale, or out of sync with MySQL.
            try (IndexWriter writer = new IndexWriter(FSDirectory.open(indexPath), config)) {
                for (Project project : projects) {
                    writer.addDocument(documentMapper.toDocument(project));
                }
                writer.commit();
            }
            response.setIndexedCount(projects.size());
            response.setSuccess(true);
            response.setMessage("Project search index rebuilt successfully");
        } catch (IOException exception) {
            LOGGER.error("Failed to rebuild project search index", exception);
            response.setIndexedCount(0);
            response.setSuccess(false);
            response.setMessage("Failed to rebuild project search index");
        }

        response.setFinishedAt(LocalDateTime.now());
        return response;
    }

    @Override
    public void indexProject(Project project) {
        try {
            ensureIndexDirectoryExists();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(FSDirectory.open(indexPath), config)) {
                writer.updateDocument(
                        new Term(ProjectLuceneFieldNames.ID, String.valueOf(project.getId())),
                        documentMapper.toDocument(project));
                writer.commit();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to index project id: " + project.getId(), exception);
        }
    }

    @Override
    public void deleteProject(Integer projectId) {
        try {
            ensureIndexDirectoryExists();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(FSDirectory.open(indexPath), config)) {
                writer.deleteDocuments(new Term(ProjectLuceneFieldNames.ID, String.valueOf(projectId)));
                writer.commit();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to delete project from search index id: " + projectId, exception);
        }
    }

    @Override
    public List<Integer> searchProjectIds(ProjectSearchCriteria criteria) {
        try {
            ensureIndexDirectoryExists();
            Query query = buildQuery(criteria);
            try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indexPath))) {
                IndexSearcher searcher = new IndexSearcher(reader);
                ScoreDoc[] scoreDocs = searcher.search(query, MAX_SEARCH_RESULTS).scoreDocs;
                LOGGER.debug("Lucene project search totalHits={}", scoreDocs.length);
                List<Integer> projectIds = new ArrayList<>();
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Document document = searcher.doc(scoreDoc.doc);
                    String projectId = document.get(ProjectLuceneFieldNames.ID);
                    LOGGER.debug(
                            "Lucene project search hit id={}, titleNormalized=\"{}\", objectiveNormalized=\"{}\", expectedProductNormalized=\"{}\"",
                            projectId,
                            document.get(ProjectLuceneFieldNames.TITLE_NORMALIZED),
                            document.get(ProjectLuceneFieldNames.OBJECTIVE_NORMALIZED),
                            document.get(ProjectLuceneFieldNames.EXPECTED_PRODUCT_NORMALIZED));
                    projectIds.add(Integer.valueOf(projectId));
                }
                return projectIds;
            }
        } catch (IndexNotFoundException exception) {
            throw new IllegalStateException("Project search index does not exist. Rebuild the index first.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to search project index", exception);
        }
    }

    private Query buildQuery(ProjectSearchCriteria criteria) throws IOException {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        boolean hasClause = false;

        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            // The same Vietnamese normalization is used for query text and indexed normalized fields.
            String normalizedKeyword = VietnameseTextNormalizer.normalize(criteria.getKeyword());
            List<String> keywordTokens = analyzeKeyword(normalizedKeyword);
            for (String keywordToken : keywordTokens) {
                BooleanQuery.Builder tokenQuery = new BooleanQuery.Builder();
                for (String searchField : NORMALIZED_SEARCH_FIELDS) {
                    tokenQuery.add(new TermQuery(new Term(searchField, keywordToken)), Occur.SHOULD);
                    Query fuzzyQuery = buildFuzzyQuery(searchField, keywordToken);
                    if (fuzzyQuery != null) {
                        tokenQuery.add(fuzzyQuery, Occur.SHOULD);
                    }
                }
                builder.add(tokenQuery.build(), Occur.MUST);
            }
            hasClause = !keywordTokens.isEmpty();
            LOGGER.debug(
                    "Searching projects. originalKeyword=\"{}\", normalizedKeyword=\"{}\", tokens={}, query={}",
                    criteria.getKeyword(),
                    normalizedKeyword,
                    keywordTokens,
                    builder.build());
        }

        Query estimatedBudgetQuery = buildBudgetRangeQuery(
                ProjectLuceneFieldNames.ESTIMATED_BUDGET,
                criteria.getMinEstimatedBudget(),
                criteria.getMaxEstimatedBudget());
        if (estimatedBudgetQuery != null) {
            builder.add(estimatedBudgetQuery, Occur.MUST);
            hasClause = true;
        }

        Query approvedBudgetQuery = buildBudgetRangeQuery(
                ProjectLuceneFieldNames.APPROVED_BUDGET,
                criteria.getMinApprovedBudget(),
                criteria.getMaxApprovedBudget());
        if (approvedBudgetQuery != null) {
            builder.add(approvedBudgetQuery, Occur.MUST);
            hasClause = true;
        }

        return hasClause ? builder.build() : new MatchAllDocsQuery();
    }

    private Query buildBudgetRangeQuery(String fieldName, BigDecimal minBudget, BigDecimal maxBudget) {
        if (minBudget == null && maxBudget == null) {
            return null;
        }
        // Lucene LongPoint needs integer values, so decimal budgets are scaled consistently before range querying.
        long minValue = minBudget == null ? Long.MIN_VALUE : documentMapper.toBudgetLong(minBudget);
        long maxValue = maxBudget == null ? Long.MAX_VALUE : documentMapper.toBudgetLong(maxBudget);
        return LongPoint.newRangeQuery(fieldName, minValue, maxValue);
    }

    private List<String> analyzeKeyword(String keyword) throws IOException {
        List<String> tokens = new ArrayList<>();
        try (TokenStream tokenStream = analyzer.tokenStream(ProjectLuceneFieldNames.TITLE_NORMALIZED, keyword)) {
            CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(termAttribute.toString());
            }
            tokenStream.end();
        }
        return tokens;
    }

    private Query buildFuzzyQuery(String searchField, String keywordToken) {
        int tokenLength = keywordToken.length();
        if (tokenLength < 3) {
            return null;
        }
        // Fuzzy search handles small typos, but short tokens are skipped to avoid noisy matches.
        int maxEdits = tokenLength >= 7 ? 2 : 1;
        return new FuzzyQuery(new Term(searchField, keywordToken), maxEdits);
    }

    private void ensureIndexDirectoryExists() throws IOException {
        Files.createDirectories(indexPath);
    }
}
