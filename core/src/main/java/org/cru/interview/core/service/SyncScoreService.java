package org.cru.interview.core.service;

import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

public interface SyncScoreService {
    /**
     * Saves the given {@param score} to the page that was requested.
     *
     * @param resourceResolver the subsystem resource resolver (not the request resource resolver)
     * @param resourcePath the path of the resource on which to save the score
     */
    void syncScore(
        ResourceResolver resourceResolver,
        int score,
        String resourcePath) throws RepositoryException;
}
