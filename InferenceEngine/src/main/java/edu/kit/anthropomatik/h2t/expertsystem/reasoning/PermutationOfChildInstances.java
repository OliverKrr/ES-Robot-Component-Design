package edu.kit.anthropomatik.h2t.expertsystem.reasoning;

public class PermutationOfChildInstances {

    public ChildIndividualWithObjectPropertyFromParent[] permutatedChildren;
    public String permutationName;

    PermutationOfChildInstances(ChildIndividualWithObjectPropertyFromParent[] permutatedChildren, String
            permutationName) {
        this.permutatedChildren = permutatedChildren;
        this.permutationName = permutationName;
    }

}
