
# Introduction 
Simple Maven plugin to publish API in API Manager.

# Prerequisites
The Maven plugin is compatible with WSO2 API Manager 2.6.0

# Usage
You can download the source code of the plugin and install it in your local repository. Otherwise, provide the following plugin repository.

```
<pluginRepositories>
		<pluginRepository>
			<id>wso2</id>
			<url>https://bitbucket.org/anupamgogoi/maven-artifacts/raw/master/</url>
		</pluginRepository>
</pluginRepositories>
```

Configuration is straightforward as shown below,
```
<plugin>
   <groupId>com.apim</groupId>
   <artifactId>maven-api-deploy</artifactId>
   <version>1.0.0</version>
   <configuration>
      <host>localhost</host>
      <username>admin</username>
      <password>admin</password>
      <apiname>Demo</apiname>
      <description>demo app</description>
      <context>/demo</context>
      <version>1.0.1</version>
      <apipath>blog.swagger</apipath>
      <type>HTTP</type>
      <transports>
         <param>HTTP</param>
      </transports>
      <tiers>
         <param>Gold</param>
      </tiers>
      <visibility>PUBLIC</visibility>
      <production>http://localhost:8080</production>
      <sandbox>http://localhost:8080</sandbox>
      <gateway>Production and Sandbox</gateway>
   </configuration>
</plugin>
```
