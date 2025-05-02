FROM openjdk:21-jdk-slim

WORKDIR /server

COPY server/ .

RUN echo '[ \
        { \
           "uuid": "8ceca3a8-3632-43d5-8f39-a3206378924b", \
           "name": "Roguire14", \
           "level": 4, \
           "bypassesPlayerLimit": true \
        } \
        ]' > ops.json && \
    echo 'allow-nether=false\nonline-mode=false\nenforce-secure-profile=false' > server.properties && \
    echo 'settings:\n  allow-end: false\n' > bukkit.yml && \
    echo 'settings:\n  bungeecord: true' > spigot.yml

COPY target/ServerHandler-1.0-SNAPSHOT.jar /server/plugins

EXPOSE 25565

CMD ["java", "-Xms1G", "-Xmx2G", "-jar", "server.jar"]