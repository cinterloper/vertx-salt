#!/usr/bin/env bats
source workflow/mulibuild.sh 
@test "build kvdn base" {
  export DISABLE_BUILD_EXTENSIONS=TRUE
  build
  result=$?
  [ "$result" -eq 0 ]
}

