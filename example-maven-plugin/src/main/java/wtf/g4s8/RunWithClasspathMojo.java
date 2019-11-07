package wtf.g4s8;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST)
public class RunWithClasspathMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Component
    private RepositorySystem repos;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository lrep;

    @Parameter(defaultValue = "false", property = "skipTests")
    private boolean skip;

    public void execute() throws MojoExecutionException {
        if (this.skip) {
            this.getLog().info("Tests are skipped");
            return;
        }

        // resolve all dependencies for current project
        final Set<Artifact> artifacts = this.repos.resolve(
            new ArtifactResolutionRequest()
                .setArtifact(this.project.getArtifact())
                .setLocalRepository(this.lrep)
                .setRemoteRepositories(this.project.getRemoteArtifactRepositories())
                .setResolveTransitively(true)
        ).getArtifacts();

        // convert to classpath URLs
        final List<Object> classpath = new ArrayList<>(artifacts.size());
        try {
            for (final Artifact artifact : artifacts) {
                final URL url = artifact.getFile().toURI().toURL();
                classpath.add(url);
            }
            classpath.add(new File(this.project.getBuild().getOutputDirectory()).toURI().toURL());
            classpath.add(new File(this.project.getBuild().getTestOutputDirectory()).toURI().toURL());
        } catch (final MalformedURLException err) {
            throw new MojoExecutionException("Failed to resolve classpath entry", err);
        }

        if (this.getLog().isInfoEnabled()) {
            classpath.forEach(entry -> this.getLog().info("Resolved classpath entry : " + entry.toString()));
        }

        // working with the classpath ...
    }
}
