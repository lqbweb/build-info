package org.jfrog.gradle.plugin.artifactory

import org.gradle.api.Project
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask
import org.jfrog.gradle.plugin.artifactory.task.DeployTask

import static org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask.ARTIFACTORY_PUBLISH_TASK_NAME
import static org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask.DEPLOY_TASK_NAME

/**
 * @author Lior Hasson
 */
class ArtifactoryPlugin extends ArtifactoryPluginBase {
    @Override
    protected ArtifactoryPluginConvention createArtifactoryPluginConvention(Project project) {
        return new ArtifactoryPluginConvention(project)
    }

    @Override
    protected ArtifactoryTask createArtifactoryPublishTask(Project project) {
        def result = project.getTasks().create(ARTIFACTORY_PUBLISH_TASK_NAME, ArtifactoryTask.class)
        result.setDescription('''AddS artifacts and generates build-info to be later deployed to Artifactory,''')
        return result
    }

    @Override
    protected DeployTask createArtifactoryDeployTask(Project project) {
        def result = project.getTasks().create(DEPLOY_TASK_NAME, DeployTask.class)
        result.setDescription('''Deploys artifacts and build-info to Artifactory''')
        return result
    }
}