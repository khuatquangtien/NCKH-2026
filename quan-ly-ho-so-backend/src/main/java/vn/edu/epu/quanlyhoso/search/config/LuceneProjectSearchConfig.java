package vn.edu.epu.quanlyhoso.search.config;

import java.nio.file.Path;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LuceneProjectSearchConfig {

    @Bean
    public Analyzer projectSearchAnalyzer() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                StandardTokenizer tokenizer = new StandardTokenizer();
                TokenStream tokenStream = new LowerCaseFilter(tokenizer);
                tokenStream = new ASCIIFoldingFilter(tokenStream);
                return new TokenStreamComponents(tokenizer, tokenStream);
            }
        };
    }

    @Bean
    public Path projectLuceneIndexPath(
            @Value("${app.lucene.project-index-path:./data/lucene/projects}") String indexPath) {
        return Path.of(indexPath);
    }
}
