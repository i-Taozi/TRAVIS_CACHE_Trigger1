package org.review_board.ereviewboard.subclipse.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.team.core.RepositoryProvider;
import org.review_board.ereviewboard.core.ReviewboardDiffMapper;
import org.review_board.ereviewboard.core.model.FileDiff;
import org.review_board.ereviewboard.core.model.Repository;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.svnclientadapter.utils.SVNUrlUtils;

/**
 * The <tt>ReviewboardToSvnMapper</tt> maps between various Reviewboard items and their SVN correspondents
 * 
 * @author Robert Munteanu
 * 
 */
public class ReviewboardToSvnMapper {

    public IProject findProjectForRepository(Repository codeRepository, TaskRepository taskRepository, ReviewboardDiffMapper diffMapper) {

        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        List<IProject> candidates = new ArrayList<IProject>();

        for (IProject project : workspace.getRoot().getProjects()) {

            SVNTeamProvider svnProvider = (SVNTeamProvider) RepositoryProvider.getProvider(project, SVNProviderPlugin.getTypeId());

            if (svnProvider == null)
                continue;

            ISVNLocalResource projectSvnResource = SVNWorkspaceRoot.getSVNResourceFor(project);
            String svnRepositoryPath = projectSvnResource.getRepository().getRepositoryRoot().toString();

            if (codeRepository.getPath().equals(svnRepositoryPath))
                candidates.add(project);
        }

        if (candidates.isEmpty())
            return null;

        if (candidates.size() == 1)
            return candidates.get(0);
        
        // multiple choice - use the latest diff revision to match based on files
        projects: for (IProject project : candidates) {

            Integer latestDiffRevisionId = diffMapper.getLatestDiffRevisionId();

            if (latestDiffRevisionId == null)
                break;

            ISVNLocalResource projectSvnResource = SVNWorkspaceRoot.getSVNResourceFor(project);
            String projectRelativePath = SVNUrlUtils.getRelativePath(projectSvnResource.getRepository().getRepositoryRoot(), projectSvnResource.getUrl(), true);
            if ( !projectRelativePath.endsWith("/") )
                projectRelativePath += "/";

            for (FileDiff fileDiff : diffMapper.getFileDiffs(latestDiffRevisionId.intValue()))
                if (!fileDiff.getDestinationFile().startsWith(projectRelativePath))
                    continue projects;
            
            return project;

        }
        return null;
    }

}
