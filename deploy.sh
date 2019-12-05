export GPG_TTY=$(tty)
#proxychains4 mvn clean deploy -DskipTests -P sonatype-oss-release
mvn clean deploy -DskipTests -P sonatype-oss-release
