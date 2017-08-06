# Command Annotations
This is a simple framework for Bukkit/Spigot, to register commands using annotations. The Javadoc can be found [here](http://docs.mrten.nl/command-annotations/).

### Maven
You can use this framework using Maven, by adding the following to your pom.xml:
```XML
<repositories>
    <repository>
        <id>mrten-repo</id>
        <url>http://repo.mrten.nl/content/groups/public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.mrten</groupId>
        <artifactId>command-annotations</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
Then shade the framework into your own plugin.