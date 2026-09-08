package vn.edu.epu.quanlyhoso.search.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import vn.edu.epu.quanlyhoso.project.entity.Project;

@Component
public class ProjectLuceneDocumentMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectLuceneDocumentMapper.class);
    private static final int BUDGET_SCALE = 2;

    public Document toDocument(Project project) {
        Document document = new Document();
        String normalizedTitle = VietnameseTextNormalizer.normalize(project.getTitle());

        document.add(new StringField(ProjectLuceneFieldNames.ID, String.valueOf(project.getId()), Store.YES));
        document.add(new TextField(ProjectLuceneFieldNames.TITLE, safeText(project.getTitle()), Store.NO));
        document.add(new TextField(ProjectLuceneFieldNames.OBJECTIVE, safeText(project.getObjective()), Store.NO));
        document.add(new TextField(
                ProjectLuceneFieldNames.EXPECTED_PRODUCT,
                safeText(project.getExpectedProduct()),
                Store.NO));
        // Store normalized copies beside original fields so unaccented Vietnamese queries can match accented text.
        document.add(new TextField(ProjectLuceneFieldNames.TITLE_NORMALIZED, normalizedTitle, Store.YES));
        document.add(new TextField(
                ProjectLuceneFieldNames.OBJECTIVE_NORMALIZED,
                VietnameseTextNormalizer.normalize(project.getObjective()),
                Store.YES));
        document.add(new TextField(
                ProjectLuceneFieldNames.EXPECTED_PRODUCT_NORMALIZED,
                VietnameseTextNormalizer.normalize(project.getExpectedProduct()),
                Store.YES));

        LOGGER.debug(
                "Indexing project id={}, originalTitle=\"{}\", normalizedTitle=\"{}\"",
                project.getId(),
                project.getTitle(),
                normalizedTitle);

        if (project.getStatus() != null) {
            document.add(new StringField(ProjectLuceneFieldNames.STATUS, project.getStatus().name(), Store.NO));
        }
        if (project.getFaculty() != null) {
            document.add(new StringField(
                    ProjectLuceneFieldNames.FACULTY,
                    normalizeExactValue(project.getFaculty()),
                    Store.NO));
        }

        Long estimatedBudget = toBudgetLong(project.getEstimatedBudget());
        if (estimatedBudget != null) {
            document.add(new LongPoint(ProjectLuceneFieldNames.ESTIMATED_BUDGET, estimatedBudget));
        }

        Long approvedBudget = toBudgetLong(project.getApprovedBudget());
        if (approvedBudget != null) {
            document.add(new LongPoint(ProjectLuceneFieldNames.APPROVED_BUDGET, approvedBudget));
        }

        return document;
    }

    public String normalizeExactValue(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    public Long toBudgetLong(BigDecimal budget) {
        if (budget == null) {
            return null;
        }
        // MySQL keeps budgets as BigDecimal; Lucene range search stores the same value as a scaled long.
        return budget.setScale(BUDGET_SCALE, RoundingMode.HALF_UP)
                .movePointRight(BUDGET_SCALE)
                .longValue();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }
}
