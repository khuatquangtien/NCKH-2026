package vn.edu.epu.quanlyhoso.paper.repository;

import vn.edu.epu.quanlyhoso.common.utils.DBConnection;
import vn.edu.epu.quanlyhoso.paper.entity.JournalRanking;
import vn.edu.epu.quanlyhoso.paper.entity.Paper;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class PaperDaoImpl - triển khai các phương thức tương tác với database
 * Chịu trách nhiệm mở kết nối và thực thi các câu lệnh SQL
 */
public class PaperDaoImpl implements PaperDao {

    @Override
    public void insertPaper(Paper paper) {
        String sql = "INSERT INTO papers (title, journal_name, publication_year, issn_isbn, journal_ranking, status, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, paper.getTitle());
            statement.setString(2, paper.getJournalName());
            statement.setInt(3, paper.getPublicationYear());
            statement.setString(4, paper.getIssnIsbn());
            statement.setString(5, paper.getJournalRanking().name());
            statement.setString(6, paper.getStatus().name());
            statement.setInt(7, paper.getCreatorId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePaper(Paper paper) {
        String sql = "UPDATE papers SET title = ?, journal_name = ?, publication_year = ?, issn_isbn = ?, journal_ranking = ? WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, paper.getTitle());
            statement.setString(2, paper.getJournalName());
            statement.setInt(3, paper.getPublicationYear());
            statement.setString(4, paper.getIssnIsbn());
            statement.setString(5, paper.getJournalRanking().name());
            statement.setInt(6, paper.getId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePaper(int id) {
        String sql = "DELETE FROM papers WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Paper findPaperById(int id) {
        String sql = "SELECT * FROM papers WHERE id = ?";
        Paper paper = null;
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                paper = mapResultSetToPaper(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return paper;
    }

    @Override
    public List<Paper> findAllPapers() {
        String sql = "SELECT * FROM papers";
        List<Paper> papers = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                papers.add(mapResultSetToPaper(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return papers;
    }

    @Override
    public void updatePaperStatus(int id, PaperStatus status) {
        String sql = "UPDATE papers SET status = ? WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, status.name());
            statement.setInt(2, id);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ánh xạ dữ liệu từ ResultSet sang đối tượng Paper
     */
    private Paper mapResultSetToPaper(ResultSet resultSet) throws SQLException {
        Paper paper = new Paper();
        
        paper.setId(resultSet.getInt("id"));
        paper.setTitle(resultSet.getString("title"));
        paper.setJournalName(resultSet.getString("journal_name"));
        paper.setPublicationYear(resultSet.getInt("publication_year"));
        paper.setIssnIsbn(resultSet.getString("issn_isbn"));
        paper.setJournalRanking(JournalRanking.valueOf(resultSet.getString("journal_ranking")));
        paper.setStatus(PaperStatus.valueOf(resultSet.getString("status")));
        paper.setCreatorId(resultSet.getInt("creator_id"));
        paper.setCreatedAt(resultSet.getTimestamp("created_at"));
        
        return paper;
    }
}
