export GPG_TTY=$(tty)
proxychains4 mvn deploy -DskipTests -P sonatype-oss-release