package org.cru.interview.core.service.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.cru.interview.core.service.SyncScoreService;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Calendar;

@Component
@Service(SyncScoreService.class)
public class SyncScoreServiceImpl implements SyncScoreService {
    @Override
    public void syncScore(
        final ResourceResolver resourceResolver,
        final int score,
        final String resourcePath) throws RepositoryException {

        Resource resource = resourceResolver.getResource(resourcePath);

        if (resource == null) {
            return;
        }

        Resource contentResource = resource.getChild("jcr:content");

        if (contentResource != null) {
            Node node = contentResource.adaptTo(Node.class);
            if (node != null) {
                node.setProperty("score", Integer.toString(score));

                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                Tag[] existingTags = tagManager.getTags(contentResource);
                Tag[] newTags;
                boolean hasScoreTagAlready = false;

                for (Tag existingTag : existingTags) {
                    if (existingTag.getTagID().startsWith("target-audience:scale-of-belief/")) {
                        hasScoreTagAlready = true;
                        break;
                    }
                }

                if (hasScoreTagAlready) {
                    newTags = new Tag[existingTags.length];
                } else {
                    newTags = new Tag[existingTags.length + 1];
                }

                for (int i = 0; i < existingTags.length; i++) {
                    if (existingTags[i].getTagID().indexOf("target-audience:scale-of-belief/") == -1) {
                        newTags[i] = existingTags[i];
                    }
                }

                Tag scoreTag = tagManager.resolve("target-audience:scale-of-belief/" + Integer.toString(score));
                if (scoreTag != null) {
                    newTags[newTags.length - 1] = scoreTag;
                }

                node.setProperty("contentScoreLastUpdated", Calendar.getInstance());
                node.setProperty("cq:lastModified", Calendar.getInstance());
                node.setProperty("cq:lastModifiedBy", "scale-of-belief");
                tagManager.setTags(contentResource, newTags);
            }
        }
    }
}
