package vn.edu.epu.quanlyhoso.search;

import java.util.List;

import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.search.dto.ProjectSearchCriteria;
import vn.edu.epu.quanlyhoso.search.dto.ProjectSearchIndexRebuildResponse;

public interface ProjectSearchIndexService {

    ProjectSearchIndexRebuildResponse rebuildIndex();

    void indexProject(Project project);

    void deleteProject(Integer projectId);

    List<Integer> searchProjectIds(ProjectSearchCriteria criteria);
}
