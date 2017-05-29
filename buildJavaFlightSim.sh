#!/bin/bash

#After Maven build:
#	- Move jar w/dependencies and natives folder out of target/
#	- Rename jar to javaflightsimulator.jar
#	- Create shell/batch script with folloeing command: java -Djava.library.path=natives/ -jar javaflightsimulator.jar
#	- For shell script need sudo chmod u+x javaflightsimulator.sh
#	- Compress Aircraft/ SimConfig/ Resources/ natives/ Documentation.docx #javaflightsimulator.jar, javaflightsimulator.bat and javaflightsimulator.sh

# Use to run in shell and bat scripts
runCommand="java -Djava.library.path=natives/ -jar javaflightsimulator.jar" 

echo ${runCommand} >> javaflightsimulator.sh

# Makes shell script executable
chmod u+x javaflightsimulator.sh

echo ${runCommand} >> javaflightsimulator.bat

# Find a jar with dependencies inside target/ 
cd target
oldFileName=`ls javaflightsim-*-jar-*`
cd ..

# Parse the version number from the jar file name
versionNumber="v0.1a"

if [[ "${oldFileName}" =~ [([\w]+)-(v[0-9]\.[0-9][a-zA-Z]+)([\S]+).jar] ]]; then 
	versionNumber=${BASH_REMATCH[2]}
fi

archiveName="javaflightsim-${versionNumber}"
newFileName="javaflightsimulator.jar"

# Temp archive directory
mkdir build/

# Move/copy files as needed 
cp -f target/${oldFileName} build/${newFileName}

cp -r -f target/natives/ natives/
cp -r target/natives/ build/natives/

cp -r -f Aircraft/  build/Aircraft/
cp -r -f Resources/ build/Resources/
cp -r -f SimConfig/ build/SimConfig/
cp -f Documentation.docx build/Documentation.docx
cp -f LICENSE build/LICENSE
cp javaflightsimulator.sh build/javaflightsimulator.sh
cp javaflightsimulator.bat build/javaflightsimulator.bat

# Create an archive and remove the temp build folder 
zip -r -9 -q ${archiveName}.zip build/*
rm -r -f build