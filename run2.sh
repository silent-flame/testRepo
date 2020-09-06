##### #!/usr/bin/bash
gitBranch=$(git rev-parse --abbrev-ref HEAD)
echo $gitBranch
PROPERTY_FILE=test2.txt
getProperty() {
   PROP_KEY=$1
   PROP_VALUE=`cat $PROPERTY_FILE | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE
}
var1=$(getProperty "var1")
echo $var1
var2=$(getProperty "var2")
echo $var2
# shellcheck disable=SC2046
releaseVersion=$(getProperty "release.version")
echo $releaseVersion