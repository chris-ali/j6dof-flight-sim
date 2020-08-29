/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.initializer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.chrisali.javaflightsim.RunJavaFlightSimulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Reads the project pom file lazily to programmatically determine project attributes
 */
public class PomReader {
    private static Model pomModel;

    private static final Logger logger = LogManager.getLogger(PomReader.class);

    private static void readPom() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        
        try {
            if (new File("pom.xml").exists()) {
                pomModel = reader.read(new FileReader("pom.xml"));
            } else {
                String pathToPom =  "/META-INF/maven/com.chrisali/javaflightsim/pom.xml";
                pomModel = reader.read(new InputStreamReader(RunJavaFlightSimulator.class.getResourceAsStream(pathToPom)));
            }
        } catch (IOException e) {
            logger.error("Could not find pom.xml!", e);
        } catch (XmlPullParserException e) {
            logger.error("Could not read pom.xml!", e);
        }
    }

    public static String getProjectName() {
        if (pomModel == null)
            readPom();

        return pomModel.getName();
    }

    public static String getVersionNumber() {
        if (pomModel == null)
            readPom();

        return pomModel.getVersion();
    }    
}