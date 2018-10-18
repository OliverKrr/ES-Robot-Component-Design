# Important
### Dokumentation / Tutorial
  * how to use the system?
  * add examples test system -> take from evaluation
  * how to extend and adapt the system?
	* usage of adapted code-generation plugin
  * -> make in wiki of github

### Load/Save sets requirements
  * add option to load/save a set of requirements (with optimization)
  * take examples from evaluation and push in git

### Documentation of rules
  * link to formulas and reverse?
  * -> refactoring of formulas
  * link to Reasoning Tree
  * link variables in formulas to data_properties
    * as annotation in ontology?

### Documentation of properties of components
  * when it is not straight-forward make annotation
  * adapt naming to catalog names (with dimensions)

### Publish code-generation adaption
  * remake with only changes without refactoring
  * remove factory field
  
### Volume calculations
  * implement rules based on given formulas

### Visualization in result window
  * refactoring -> one object per window
  * add name for structural parts (Drive/Output/Electro)
  * ComposedOutput -> bring AbsolutEncoder and SensrPCB to front
  * implement TwoSided
  * add total height, diameter and length
  * add electro diameter and length

### Reduce dependencies to ontologies
  * see TODOs in code
  * remove Compressed/Linear
  * rating of height
    * if rating is allowed for diameter it is possible that height got also changed. This should not be allowed.
    * -> use original value of diameter to compare
    * -> make own rating for height

### Analyse & Fix deletion of Axioms
  * fix special cases (see code)
  * for LengthInput there are permutations for deleted (DiameterInput, DriveBearingMatch) ones -> without delete there were less created

### Little fixes
  * add extra button to start reasoning instead of tab switching
  * if one requirement is not parsable to not continue with reasoning
  * Bug: there are no solutions with any MotorFeedback
  * TODOs in Code


# Medium

### Renaming in Ontology
  * use thickness t instead of diameter -> sync with formula documentation
  * use right units

### Display count of components in solution
  * especially for AbsolutEncoder/Screw/Bearings

### Analyse where/why gearboxes/motors are discarded while reasoning

### Small changes
  * add displayName Abbreviation for tableView

### Extend view of Requirements
  * add Requirement without field, but only name and description
  * add groups of Requirements -> Input/Output
  * allow showing pictures -> field with pictureName (without path)
    * -> where to show? on left/right side?
    * -> how to handle path -> relative to what?
  * add option to indent (length)
  * make dynamic updateSize (depending on all Requirements) -> different resolutions

### Allow preselected components
  * display available components with two rows
  * let user switch each component
  * optional: multi select possible

### Display each component
  * open as new window when clicked on the device
  * also with picture?
  * get values from ontology
  * names could come from annotations
  * -> check if all required values are inserted for the different ones

### Make performance tests -> adapt Reasoner
  * check what functions consume most of the time
  * maybe try to disable some, which are not required, or bypass them through other modeling



# Nice to have

### Add settings page
  * save/load in files -> use temp of user?
  * e.g. current order-criterion / what to display / unit to design

### Check SWRL rules
  * read rules and check if consistent and equal (text-file vs ontology)

### Automated Tests
  * read from file
  * like the log output of reqs with solution
  * differ between "has to be in solution" and "is complete solution"
  * -> maybe allow only partial solutions
  * test number of solutions

### Publish Ontologies separat from code
  * https://linkingresearch.wordpress.com/2013/05/27/how-to-properly-publish-a-vocabulary-or-ontology-in-the-web-1-of-6/

### Build as Java module
  * use Java 9
  * build own JDK
  * -> installation of Java not neccessary
  * -> fix 32/64 bit error?






