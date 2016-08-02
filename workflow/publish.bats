#!/usr/bin/env bats
source workflow/mulibuild.sh 
@test "publish to maven and github" {
  export DISABLE_BUILD_EXTENSIONS=TRUE
  publish
  result=$?
  [ "$result" -eq 0 ]
}

