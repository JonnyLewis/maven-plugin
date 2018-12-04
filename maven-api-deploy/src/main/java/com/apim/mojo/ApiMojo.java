package com.apim.mojo;

import com.apim.service.ApiGeneration;
import com.apim.service.PluginPropertyValues;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "deploy")
public class ApiMojo extends AbstractMojo {

    @Component
    MavenProject mavenProject;
    @Parameter(required = true)
    private String host;
    @Parameter(required = false,defaultValue = "0")
    private int offset;
    @Parameter(required = true)
    private String username;
    @Parameter(required = true)
    private String password;
    @Parameter(required = true)
    private String apiname;
    @Parameter(required = false)
    private String description;
    @Parameter(required = true)
    private String context;
    @Parameter(required = true)
    private String version;
    @Parameter(required = true)
    private String apipath;
    @Parameter(required = true)
    private String type;
    @Parameter(required = true)
    private List<String> transports;
    @Parameter(required = true)
    private List<String> tiers;
    @Parameter(required = true)
    private String visibility;
    @Parameter(required = true)
    private String production;
    @Parameter(required = true)
    private String sandbox;
    @Parameter(required = true)
    private String gateway;

    public void execute() throws MojoExecutionException, MojoFailureException {
        PluginPropertyValues.HOST = getHost();
        PluginPropertyValues.OFFSET = getOffset();
        PluginPropertyValues.USERNAME = getUsername();
        PluginPropertyValues.PASSWORD = getPassword();
        PluginPropertyValues.APINAME = getApiname();
        PluginPropertyValues.DESCRIPTION = getDescription();
        PluginPropertyValues.CONTEXT = getContext();
        PluginPropertyValues.VERSION = getVersion();
        PluginPropertyValues.APIPATH = getApipath();
        PluginPropertyValues.TYPE = getType();
        PluginPropertyValues.TRANSPORTS = getTransports();
        PluginPropertyValues.TIERS = getTiers();
        PluginPropertyValues.VISIBILITY = getVisibility();
        PluginPropertyValues.PRODUCTION = getProduction();
        PluginPropertyValues.SANDBOX = getSandbox();
        PluginPropertyValues.GATEWAY = getGateway();

        try {
            ClassLoader loader = makeClassLoader(mavenProject.getCompileClasspathElements());
            ApiGeneration.deploy(loader);
        } catch (Exception e) {
            e.printStackTrace();
            getLog().error(e.getMessage());
        }
    }

    /**
     * Make Classloader.
     *
     * @param paths
     * @return
     */
    private ClassLoader makeClassLoader(List<String> paths) {
        List<URL> pathUrls = new ArrayList<>();
        paths.forEach((path) -> {
            try {
                pathUrls.add(new File(path).toURI().toURL());
            } catch (Exception e) {
                getLog().error(e.getMessage());
            }

        });
        URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);
        URLClassLoader urlClassLoader = new URLClassLoader(urlsForClassLoader);
        return urlClassLoader;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiname() {
        return apiname;
    }

    public void setApiname(String apiname) {
        this.apiname = apiname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApipath() {
        return apipath;
    }

    public void setApipath(String apipath) {
        this.apipath = apipath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTransports() {
        return transports;
    }

    public void setTransports(List<String> transports) {
        this.transports = transports;
    }

    public List<String> getTiers() {
        return tiers;
    }

    public void setTiers(List<String> tiers) {
        this.tiers = tiers;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getSandbox() {
        return sandbox;
    }

    public void setSandbox(String sandbox) {
        this.sandbox = sandbox;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }
}
