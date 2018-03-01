# An Ontology-Based Expert System to Support the Design of Humanoid Robot Components

## Abstract

Humanoid robot design is a complex and challenging task, which requires interdisciplinary mechatronic expert knowledge to find a trade-off for contradictory requirements. This repository presents a framework for expert systems, which generate design solutions for humanoid robot components based on technical requirements. In a multi-stage reasoning process, the expert system generates, combines and discards different partial solutions based on a flexible rule set provided by domain experts. These expert rules cover different structural and subcomponent options, which result in multiple solutions. The use of ontologies as a knowledge base for rules and components ensures an easy expandability. The current framework is implemented for highly-integrated sensor-actuator units (SA units).

## Usage of released Expert System

1. Go to "release" tab (next to commits/branches/contributor/MIT)
2. Download "Start_ExpertSystem.bat" and "ES-UserInterface-1.0.0-jar-with-dependencies.jar"
3. Install java (1.8) (mind fitting 32/64 bit version)
4. (Optional) Open Start_ExpertSystem.bat and adjust Xms/Xmx to your system's memory (has to be a multiply of 1024m)
5. Start expert system by double click on Start_ExpertSystem.bat (on linux change file to Start_ExpertSystem.sh)

### Adapt Ontologies of released Expert System

1. Open ES-UserInterface-1.0.0-jar-with-dependencies.jar with winrar/7-zip(/other)
2. Copy/Extract SA_Component_Ontology.owl and/or SA_Reasoning_Ontology.owl to the same dir where the jar is located
3. Adapt ontology (e.g. with Protege editor)
4. Restart expert system

## Usage of latest/own Expert System

1. Clone or download/unzip this repository
2. Install java (1.8) (mind fitting 32/64 bit version)
3. Download/unzip maven and set maven-{version}/bin to your PATH environmental variable
4. If your system is not Windows 64 bit:
    * Open UserInterface/pom.xml with an editor
    * Comment/Remove org.eclipse.swt.win32.win32.x86_64
    * Uncomment your fitting org.eclipse.swt
5. Open cmd or bash in root directory of this repository
6. Execute "mvn clean package"
    * If maven or java is not found:
        * Set JAVA_HOME environmental variable
        * Add paths of the bin directories (of maven/java) to your PATH environmental variable
7. (Optional) Open Start_ExpertSystem.bat and adjust Xms/Xmx to your system's memory (has to be a multiply of 1024m)
8. Start expert system by double click on Start_ExpertSystem.bat (on linux change file to Start_ExpertSystem.sh)