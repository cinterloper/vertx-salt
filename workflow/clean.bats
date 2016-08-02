#!/usr/bin/env bats
source workflow/mulibuild.sh 
@test "cleanup" {
  export DISABLE_BUILD_EXTENSIONS=TRUE
  ./gradlew clean
  result=$?
  [ "$result" -eq 0 ]
}

