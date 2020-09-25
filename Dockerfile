# Start from GridGain Professional image.
FROM gridgain/community:latest

# Set config uri for node.
ENV CONFIG_URI Bitset-server.xml

# Copy optional libs.
ENV OPTION_LIBS ignite-rest-http

# Update packages and install maven.
RUN set -x \
    && apk add --no-cache \
        openjdk8

RUN apk --update add \
    maven \
    && rm -rfv /var/cache/apk/*

# Append project to container.
ADD . Bitset

# Build project in container.
RUN mvn -f Bitset/pom.xml clean package -DskipTests

# Copy project jars to node classpath.
RUN mkdir $IGNITE_HOME/libs/Bitset && \
   find Bitset/target -name "*.jar" -type f -exec cp {} $IGNITE_HOME/libs/Bitset \;