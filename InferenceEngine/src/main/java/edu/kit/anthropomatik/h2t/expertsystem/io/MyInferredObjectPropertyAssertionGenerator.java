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
package edu.kit.anthropomatik.h2t.expertsystem.io;

import edu.kit.anthropomatik.h2t.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.semanticweb.owlapi.model.parameters.Imports.INCLUDED;

public class MyInferredObjectPropertyAssertionGenerator extends MyInferredGenerator<OWLPropertyAssertionAxiom<?, ?>> {

    private OWLGenericTools genericTool;
    private AtomicBoolean isStopped = new AtomicBoolean(false);

    MyInferredObjectPropertyAssertionGenerator(OWLGenericTools genericTool) {
        this.genericTool = genericTool;
    }

    @Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                             Set<OWLPropertyAssertionAxiom<?, ?>> result) {
        if (isStopped.get()) {
            return;
        }
        genericTool.getOntology().objectPropertiesInSignature(INCLUDED)
                // take only data props which have no other children -> filer unnecessary
                .filter(p -> genericTool.getOntology().objectSubPropertyAxiomsForSuperProperty(p).count() == 0 &&
                        !Vocabulary.OBJECT_PROPERTY_HASREQUIREMENT.equals(p)).forEach(p -> reasoner
                .objectPropertyValues(entity, p).forEach(v -> result.add(dataFactory
                        .getOWLObjectPropertyAssertionAxiom(p, entity, v))));
    }

    @Override
    public String getLabel() {
        return "My object property assertions (property values)";
    }

    @Override
    public void reset() {
        isStopped.set(false);
    }

    @Override
    public void stop() {
        isStopped.set(true);
    }
}