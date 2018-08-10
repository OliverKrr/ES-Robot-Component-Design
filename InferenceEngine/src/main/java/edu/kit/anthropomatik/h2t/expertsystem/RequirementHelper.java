/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem;

import edu.kit.anthropomatik.h2t.expertsystem.generated.Vocabulary;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.*;
import openllet.owlapi.OWLGenericTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;

import java.util.ArrayList;
import java.util.List;

public class RequirementHelper {

    private static final Logger logger = LogManager.getLogger(RequirementHelper.class);

    private OWLGenericTools genericTool;
    private MyOWLHelper helper;

    RequirementHelper(OWLGenericTools genericTool, MyOWLHelper helper) {
        this.genericTool = genericTool;
        this.helper = helper;
    }

    public List<Requirement> getRequirements(OWLClass forClass) {
        long startTime = System.currentTimeMillis();
        List<Requirement> requirements = new ArrayList<>();

        genericTool.getOntology().subClassAxiomsForSubClass(forClass).filter(topAxiom -> topAxiom.getSuperClass()
                .objectPropertiesInSignature().anyMatch(Vocabulary.OBJECT_PROPERTY_HASHUMANOIDROBOTCOMPONENTPROPERTY::equals))
                .forEach(topAxiom -> topAxiom.getSuperClass().classesInSignature().forEach(clas -> genericTool
                        .getOntology().subClassAxiomsForSubClass(clas).forEach(axiom -> axiom
                                .componentsWithoutAnnotations().filter(component -> component instanceof
                                        OWLObjectHasValue && Vocabulary.OBJECT_PROPERTY_HASREQUIREMENT.equals((
                                                (OWLObjectHasValue) component).getProperty())).forEach(component ->
                                        requirements.add(parseRequirement(((OWLObjectHasValue) component).getFiller()
                                                .asOWLNamedIndividual()))))));

        requirements.sort((req1, req2) -> {
            int sortCat = req1.category.orderPosition - req2.category.orderPosition;
            if (sortCat == 0) {
                return req1.orderPosition - req2.orderPosition;
            }
            return sortCat;
        });
        logger.debug("Time needed for get Requirements: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        return requirements;
    }

    public List<RequirementDependencyCheckbox> getRequirementDependencies(List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        List<RequirementDependencyCheckbox> requirementDependencies = new ArrayList<>();

        genericTool.getReasoner().instances(Vocabulary.CLASS_REQUIREMENTDEPENDENCYCHECKBOX).forEach(dep -> {
            RequirementDependencyCheckbox requirementDependencyCheckbox = new RequirementDependencyCheckbox();

            genericTool.getReasoner().objectPropertyValues(dep, Vocabulary.OBJECT_PROPERTY_DEPENDSFROMREQUIREMENT)
                    .findAny().ifPresent(depFrom -> requirements.stream().filter(req -> depFrom.getIRI().getIRIString
                    ().equals(req.individualIRI)).findAny().ifPresent(req -> requirementDependencyCheckbox.from = req));
            genericTool.getReasoner().objectPropertyValues(dep, Vocabulary.OBJECT_PROPERTY_DEPENDSTOREQUIREMENT)
                    .findAny().ifPresent(depFrom -> requirements.stream().filter(req -> depFrom.getIRI().getIRIString
                    ().equals(req.individualIRI)).findAny().ifPresent(req -> requirementDependencyCheckbox.to = req));
            genericTool.getReasoner().dataPropertyValues(dep, Vocabulary.DATA_PROPERTY_DISPLAYONVALUEBOOLEAN).findAny
                    ().ifPresent(literal -> requirementDependencyCheckbox.displayOnValue = literal.parseBoolean());

            if (requirementDependencyCheckbox.from != null && requirementDependencyCheckbox.to != null) {
                requirementDependencies.add(requirementDependencyCheckbox);
            }
        });
        logger.debug("Time needed for get RequirementDependencies: " + (System.currentTimeMillis() - startTime) /
                1000.0 + "s");
        return requirementDependencies;
    }

    private Requirement parseRequirement(OWLNamedIndividual reqIndi) {
        return genericTool.getReasoner().types(reqIndi, true).map(type -> {
            if (Vocabulary.CLASS_TEXTFIELDMINMAXREQUIREMENT.equals(type)) {
                TextFieldMinMaxRequirement textReq = new TextFieldMinMaxRequirement();
                parseCommonRequirement(textReq, reqIndi);

                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISFOREXPERTMODEMAX)
                        .findAny().ifPresent(obProp -> textReq.enableMax = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISFOREXPERTMODEMIN)
                        .findAny().ifPresent(obProp -> textReq.enableMin = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary
                        .DATA_PROPERTY_HASSCALEFROMONTOLOGYTOUI).findAny().ifPresent(obProp -> textReq
                        .scaleFromOntologyToUI = helper.parseValueToDouble(obProp));
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISINTEGERVALUE)
                        .findAny().ifPresent(obProp -> textReq.isIntegerValue = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDEFAULTVALUEMIN)
                        .findAny().ifPresent(obProp -> textReq.defaultMin = helper.parseValueToDouble(obProp));
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDEFAULTVALUEMAX)
                        .findAny().ifPresent(obProp -> textReq.defaultMax = helper.parseValueToDouble(obProp));
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ALLOWOPTIMIZATION)
                        .findAny().ifPresent(obProp -> textReq.allowOptimization = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASUSERWEIGHT).findAny
                        ().ifPresent(obProp -> textReq.userWeight = helper.parseValueToInteger(obProp));

                genericTool.getOntology().dataPropertyAssertionAxioms(reqIndi).forEach(propAxiom -> propAxiom
                        .dataPropertiesInSignature().forEach(dataProp -> genericTool.getReasoner()
                                .superDataProperties(dataProp).forEach(supDataProp -> {
                    if (Vocabulary.DATA_PROPERTY_HASREQVALUEMIN.equals(supDataProp)) {
                        textReq.minIRI = dataProp.getIRI().getIRIString();
                    } else if (Vocabulary.DATA_PROPERTY_HASREQVALUEMAX.equals(supDataProp)) {
                        textReq.maxIRI = dataProp.getIRI().getIRIString();
                    } else if (Vocabulary.DATA_PROPERTY_HASPROPERTY.equals(supDataProp)) {
                        textReq.resultIRI = dataProp.getIRI().getIRIString();
                    }
                })));
                return textReq;
            } else if (Vocabulary.CLASS_TEXTFIELDREQUIREMENT.equals(type)) {
                TextFieldRequirement textReq = new TextFieldRequirement();
                parseCommonRequirement(textReq, reqIndi);
                parseRequirementType(textReq, reqIndi);

                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISFOREXPERTMODE)
                        .findAny().ifPresent(obProp -> textReq.enable = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary
                        .DATA_PROPERTY_HASSCALEFROMONTOLOGYTOUI).findAny().ifPresent(obProp -> textReq
                        .scaleFromOntologyToUI = helper.parseValueToDouble(obProp));
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISINTEGERVALUE)
                        .findAny().ifPresent(obProp -> textReq.isIntegerValue = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDEFAULTVALUE)
                        .findAny().ifPresent(obProp -> textReq.defaultValue = helper.parseValueToDouble(obProp));

                genericTool.getOntology().dataPropertyAssertionAxioms(reqIndi).forEach(propAxiom -> propAxiom
                        .dataPropertiesInSignature().forEach(dataProp -> genericTool.getReasoner()
                                .superDataProperties(dataProp).forEach(supDataProp -> {
                    if (Vocabulary.DATA_PROPERTY_HASREQVALUE.equals(supDataProp)) {
                        textReq.reqIri = dataProp.getIRI().getIRIString();
                    } else if (Vocabulary.DATA_PROPERTY_HASPROPERTY.equals(supDataProp)) {
                        textReq.resultIRI = dataProp.getIRI().getIRIString();
                    }
                })));
                return textReq;
            } else if (Vocabulary.CLASS_CHECKBOXREQUIREMENT.equals(type)) {
                CheckboxRequirement textReq = new CheckboxRequirement();
                parseCommonRequirement(textReq, reqIndi);

                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISFOREXPERTMODE)
                        .findAny().ifPresent(obProp -> textReq.enable = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDEFAULTVALUE)
                        .findAny().ifPresent(obProp -> textReq.defaultValue = obProp.parseBoolean());

                genericTool.getOntology().dataPropertyAssertionAxioms(reqIndi).forEach(propAxiom -> propAxiom
                        .dataPropertiesInSignature().forEach(dataProp -> genericTool.getReasoner()
                                .superDataProperties(dataProp).forEach(supDataProp -> {
                    if (Vocabulary.DATA_PROPERTY_HASREQVALUE.equals(supDataProp)) {
                        textReq.reqIri = dataProp.getIRI().getIRIString();
                    } else if (Vocabulary.DATA_PROPERTY_HASPROPERTY.equals(supDataProp)) {
                        textReq.resultIRI = dataProp.getIRI().getIRIString();
                    }
                })));
                return textReq;
            } else if (Vocabulary.CLASS_DROPDOWNREQUIREMENT.equals(type)) {
                DropdownRequirement textReq = new DropdownRequirement();
                parseCommonRequirement(textReq, reqIndi);

                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_ISFOREXPERTMODE)
                        .findAny().ifPresent(obProp -> textReq.enable = obProp.parseBoolean());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDEFAULTVALUE)
                        .findAny().ifPresent(obProp -> textReq.defaultValue = obProp.getLiteral());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASSEPARATOR).findAny
                        ().ifPresent(obProp -> textReq.separator = obProp.getLiteral());
                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDROPDOWNVALUES)
                        .findAny().ifPresent(obProp -> textReq.values = obProp.getLiteral().split(textReq.separator));

                genericTool.getOntology().dataPropertyAssertionAxioms(reqIndi).forEach(propAxiom -> propAxiom
                        .dataPropertiesInSignature().forEach(dataProp -> genericTool.getReasoner()
                                .superDataProperties(dataProp).forEach(supDataProp -> {
                    if (Vocabulary.DATA_PROPERTY_HASREQVALUE.equals(supDataProp)) {
                        textReq.reqIri = dataProp.getIRI().getIRIString();
                    } else if (Vocabulary.DATA_PROPERTY_HASPROPERTY.equals(supDataProp)) {
                        textReq.resultIRI = dataProp.getIRI().getIRIString();
                    }
                })));
                return textReq;
            } else if (Vocabulary.CLASS_REQUIREMENTONLYFORSOLUTION.equals(type)) {
                RequirementOnlyForSolution textReq = new RequirementOnlyForSolution();
                parseCommonRequirement(textReq, reqIndi);

                genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary
                        .DATA_PROPERTY_HASSCALEFROMONTOLOGYTOUI).findAny().ifPresent(obProp -> textReq
                        .scaleFromOntologyToUI = helper.parseValueToDouble(obProp));

                genericTool.getOntology().dataPropertyAssertionAxioms(reqIndi).forEach(propAxiom -> propAxiom
                        .dataPropertiesInSignature().forEach(dataProp -> genericTool.getReasoner()
                                .superDataProperties(dataProp).forEach(supDataProp -> {
                    if (Vocabulary.DATA_PROPERTY_HASPROPERTY.equals(supDataProp)) {
                        textReq.resultIRI = dataProp.getIRI().getIRIString();
                    }
                })));
                return textReq;
            } else {
                throw new RuntimeException("Requirement type unknown: " + type);
            }
        }).findAny().get();
    }

    private void parseCommonRequirement(Requirement req, OWLNamedIndividual reqIndi) {
        req.category = new Category();
        genericTool.getReasoner().objectPropertyValues(reqIndi, Vocabulary.OBJECT_PROPERTY_HASCATEGORY).forEach(cate
                -> {
            genericTool.getReasoner().dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASDISPLAYNAME).findAny()
                    .ifPresent(obProp -> req.category.displayName = obProp.getLiteral());
            genericTool.getReasoner().dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASTOPIC).findAny().ifPresent
                    (obProp -> req.category.topic = obProp.getLiteral());
            genericTool.getReasoner().dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASORDERPOSITION).findAny()
                    .ifPresent(obProp -> req.category.orderPosition = helper.parseValueToInteger(obProp));
        });

        req.individualIRI = reqIndi.getIRI().getIRIString();

        genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDISPLAYNAME).findAny()
                .ifPresent(obProp -> req.displayName = obProp.getLiteral());
        genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASDESCRIPTION).findAny()
                .ifPresent(obProp -> req.description = obProp.getLiteral());
        genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASUNIT).findAny().ifPresent
                (obProp -> req.unit = obProp.getLiteral());
        genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_HASORDERPOSITION).findAny()
                .ifPresent(obProp -> req.orderPosition = helper.parseValueToInteger(obProp));
        genericTool.getReasoner().dataPropertyValues(reqIndi, Vocabulary.DATA_PROPERTY_SHOWDEFAULTINRESULTS).findAny
                ().ifPresent(obProp -> req.showDefaultInResults = obProp.parseBoolean());
    }

    private void parseRequirementType(TextFieldRequirement textReq, OWLNamedIndividual reqIndi) {
        genericTool.getReasoner().objectPropertyValues(reqIndi, Vocabulary.OBJECT_PROPERTY_HASREQUIREMENTTYPE)
                .forEach(reqType -> genericTool.getReasoner().types(reqType).forEach(typeOfReqType -> {
            if (Vocabulary.CLASS_REQUIREMENTTYPEEXACT.equals(typeOfReqType)) {
                textReq.requirementType = RequirementType.EXACT;
            } else if (Vocabulary.CLASS_REQUIREMENTTYPEMIN.equals(typeOfReqType)) {
                textReq.requirementType = RequirementType.MIN;
            } else if (Vocabulary.CLASS_REQUIREMENTTYPEMAX.equals(typeOfReqType)) {
                textReq.requirementType = RequirementType.MAX;
            }
        }));
    }

}
