build() {
  for ver in 3.3.2 3.2.1
  do
    export VERTX_VERSION=$ver 
    ./gradlew clean shadowJar
 done
}
publish() {
  for ver in 3.3.2 3.2.1
  do
    VERTX_VERSION=$ver ./gradlew clean shadowJar clientsDistTar clientsDistZip publish
 done
}
release() {
  for ver in 3.3.2 3.2.1
  do
    VERTX_VERSION=$ver ./gradlew clean shadowJar clientsDistTar clientsDistZip githubRelease
 done
}
