#!/usr/bin/env bash

set -e
echo "TRAVIS_TAG          : $TRAVIS_TAG"
echo "TRAVIS_BRANCH       : $TRAVIS_BRANCH"
echo "TRAVIS_PULL_REQUEST : $TRAVIS_PULL_REQUEST"
echo "Publishing archives for branch $TRAVIS_BRANCH"
rm -rf build

EXIT_STATUS=0

./gradlew clean check --stacktrace || EXIT_STATUS=$?

if (( $EXIT_STATUS > 0))
then
  exit $EXIT_STATUS
fi

# Only publish if the branch is on master, and it is not a PR
if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]; then

  echo "Publishing archives for branch $TRAVIS_BRANCH"

  if [[ -n $TRAVIS_TAG ]]; then
      echo "Pushing build to Bintray"
      ./gradlew :rest-doc:bintrayUpload || EXIT_STATUS=$?
  fi

  if (( $EXIT_STATUS > 0))
  then
     exit $EXIT_STATUS
  fi

  ./gradlew clean asciidoctor --stacktrace || EXIT_STATUS=$?

  if (( $EXIT_STATUS > 0))
  then
     exit $EXIT_STATUS
  fi

  git config --global user.name "$GIT_NAME"
  git config --global user.email "$GIT_EMAIL"
  git config --global credential.helper "store --file=~/.git-credentials"
  echo "https://$GH_TOKEN:@github.com" > ~/.git-credentials

  git clone https://${GH_TOKEN}@github.com/${TRAVIS_REPO_SLUG}.git -b gh-pages gh-pages --single-branch > /dev/null
  cd gh-pages

  # If this is the master branch then update the snapshot
  if [[ $TRAVIS_BRANCH == 'master' ]]; then

    mkdir -p snapshot
    cp -r ../docs/build/asciidoc/html5/. ./snapshot/
    git add snapshot/*

    mkdir -p sample
    cp -r ../examples/grails-docs-app/build/asciidoc/html5/. ./sample/
    git add sample/*

  fi

  # If there is a tag present then this becomes the latest
  if [[ -n $TRAVIS_TAG ]]; then
    mkdir -p latest
    cp -r ../docs/build/asciidoc/html5/. ./latest/
    git add latest/*
  fi

  if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' ]]; then
      git commit -a -m "Updating docs for Travis build: https://travis-ci.org/$TRAVIS_REPO_SLUG/builds/$TRAVIS_BUILD_ID"
      git push origin HEAD
  fi

  cd ..
  rm -rf gh-pages
fi

exit $EXIT_STATUS