package me.luucka.parkour;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class ParkourPluginLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("sonatype", "default", "https://oss.sonatype.org/content/groups/public/").build());
        resolver.addRepository(new RemoteRepository.Builder("codemc", "default", "https://repo.codemc.io/repository/maven-snapshots/").build());
        resolver.addRepository(new RemoteRepository.Builder("jitpack.io", "default", "https://jitpack.io").build());

        resolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-yaml:4.1.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-sync:4.10.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("net.wesjd:anvilgui:1.7.0-SNAPSHOT"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("dev.jorel:commandapi-bukkit-shade:9.0.3"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.LuuckA21:paper-gui:1.0.0"), null));

        classpathBuilder.addLibrary(resolver);
    }
}
