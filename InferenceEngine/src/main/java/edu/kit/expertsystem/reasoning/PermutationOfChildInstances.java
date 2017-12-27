package edu.kit.expertsystem.reasoning;

public class PermutationOfChildInstances {

    public ChildIndividualWithObjectPropertyFromParent[] permutatedChildren;
    public String permutationName;

    public PermutationOfChildInstances(ChildIndividualWithObjectPropertyFromParent[] permutatedChildren,
                                       String permutationName) {
        this.permutatedChildren = permutatedChildren;
        this.permutationName = permutationName;
    }

}
