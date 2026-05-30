#GET VERSIONING PHASE FROM EXEC COMMAND
PHASE=$1

#EXTRACT PROJECT VERSION FROM POM
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

#DECOMPOSE VERSION UNIT VALUES
IFS='.' read -r MAJOR MINOR PATCH <<< "$VERSION"

#INCREMENT VERSION ACCORDING REQUIRED VERSIONING PHASE
case "$PHASE" in
    MAJOR)
        MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0
        ;;
    MINOR)
        MINOR=$((MINOR + 1)); PATCH=0
        ;;
    PATCH)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo "No valid phase option to determine a new version"
		exit 1
        ;;
esac

#BUILD NEW VERSION STRING VALUE
UPDATED_VERSION="$MAJOR.$MINOR.$PATCH"

echo "UPDATING VERSION FROM $VERSION TO $UPDATED_VERSION"

#UPDATE POM.XML FILE WITH THE NEW VERSION VALUE
mvn versions:set -DnewVersion=$UPDATED_VERSION
mvn versions:commit

echo "UPDATED_VERSION=$UPDATED_VERSION" >> $GITHUB_OUTPUT