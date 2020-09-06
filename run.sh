#!/usr/bin/sh
##var1="val1"
##var2="val2"
##export -p > test.txt
if [[ $# -eq 0 ]]
  then
    echo "No arguments supplied"
    exit
fi
PROPERTY_FILE=release.version
RELEASE_VERSION=$(cat $PROPERTY_FILE)
echo "$RELEASE_VERSION"
MAJOR_VERSION=$(echo "$RELEASE_VERSION" | cut -c3-4)
MINOR_VERSION=$(echo "$RELEASE_VERSION" | cut -c6-8)
PATCH_VERSION=$(echo "$RELEASE_VERSION" | cut -c10-11)
MAJOR_VERSION=$((MAJOR_VERSION))
MINOR_VERSION=$((MINOR_VERSION))
PATCH_VERSION=$((PATCH_VERSION))
echo "---"
RELEASE_TYPE=$1
case "$RELEASE_TYPE" in
   "major" ) MAJOR_VERSION=$((MAJOR_VERSION + 1));;
   "minor" ) MINOR_VERSION=$((MINOR_VERSION + 1));;
   "patch" ) PATCH_VERSION=$((PATCH_VERSION + 1));;
esac
echo $MAJOR_VERSION
echo $MINOR_VERSION
echo $PATCH_VERSION
echo "------------------"
RELEASE_VERSION=$(printf "D-%02d.%03d.%02d" $MAJOR_VERSION $MINOR_VERSION $PATCH_VERSION)
echo "$RELEASE_VERSION"
git checkout -b "release/$RELEASE_VERSION"
git tag "release/$RELEASE_VERSION"
git push origin --tags