export GPG_TTY=$(tty)
mvn deploy -DskipTests -P sonatype-oss-release