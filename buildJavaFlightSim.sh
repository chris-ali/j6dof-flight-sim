#!/bin/bash

# Builds project, runs tests, generates natives, packages all dependencies into jar file in target/
mvn package

echo "Maven build complete! Packaging build artifacts..."

# Parse the version number and project names from the pom file
versionNumber=$(grep -e '<version>' -m 1  pom.xml | awk -F '[<>]' '/version/{print $3}')
artifactId=$(grep -e '<artifactId>' -m 1  pom.xml | awk -F '[<>]' '/artifactId/{print $3}')
mavenArchiveName=$(ls target/${artifactId}-${versionNumber}-* | cut -c 8-)
finalFileName=$(grep -e '<name>' -m 1  pom.xml | awk -F '[<>]' '/name/{print $3}' | awk '{gsub(/ /,"", $0); print tolower($0)}')
buildDirectory=${finalFileName}

# Temp archive directory
mkdir ${buildDirectory}/

echo "Creating run scripts..."

# Use to run program in shell and bat scripts
runCommand="java -Djava.library.path=natives/ -jar ${finalFileName}.jar LWJGL_JAVAFX" 

echo ${runCommand} >> ${buildDirectory}/${finalFileName}.sh
echo ${runCommand} >> ${buildDirectory}/${finalFileName}.bat

# Makes shell script executable
chmod u+x ${buildDirectory}/${finalFileName}.sh

echo "Copying build artifacts from ./target/ ..."

# Move/copy/rename files as needed 
cp -f target/${mavenArchiveName} ${buildDirectory}/${finalFileName}.jar
cp -r target/natives/ ${buildDirectory}/natives/
cp -r -f Aircraft/ ${buildDirectory}/Aircraft/
cp -r -f Resources/ ${buildDirectory}/Resources/
cp -r -f SimConfig/ ${buildDirectory}/SimConfig/
cp -f Documentation.odt ${buildDirectory}/Documentation.odt
cp -f LICENSE ${buildDirectory}/LICENSE

echo "Compressing build artifacts..."

# Create an archive and remove the temp build folder 
mkdir -p Release
archiveName=${finalFileName}-v${versionNumber}.zip
zip -r -9 -q ${archiveName} ${buildDirectory}/*
mv ${archiveName} Release/${archiveName}
rm -r -f ${buildDirectory}

echo "...done!"
echo "Your build ${archiveName} is located in ./Release/"