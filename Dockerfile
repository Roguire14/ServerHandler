FROM openjdk:21-jdk-slim

WORKDIR /server

RUN echo "eula=true" > eula.txt && \
    echo '[ \
        { \
           "uuid": "8ceca3a8-3632-43d5-8f39-a3206378924b", \
           "name": "Roguire14", \
           "level": 4, \
           "bypassesPlayerLimit": true \
        } \
        ]' > ops.json && \
    echo 'allow-nether=false\nonline-mode=false' > server.properties && \
    echo 'settings:\n  allow-end: false\n' > bukkit.yml && \
    echo 'settings:\n  bungeecord: true' > spigot.yml

RUN mkdir plugins && \
    mkdir config
COPY paper-1.21.4-214.jar /server/server.jar
COPY ViaRewind-4.0.4.jar /server/plugins
COPY ViaBackwards-5.2.1.jar /server/plugins
COPY ViaVersion-5.2.1.jar /server/plugins
COPY target/ServerHandler-1.0-SNAPSHOT.jar /server/plugins
COPY world /server/world

EXPOSE 25565

CMD ["java", "-Xms1G", "-Xmx2G", "-jar", "server.jar"]