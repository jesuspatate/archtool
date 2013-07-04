package fr.univ_nantes.alma.archtool.parsing.specifier;

import java.util.Map;
import java.util.Set;
import fr.univ_nantes.alma.archtool.sourceModel.ComplexType;


public class EnumSpecifier extends StructuredSpecifier
{  
    public EnumSpecifier(String name, Map<String, ComplexType> complextypes)
    {
        super(name, complextypes);
    }
    
    @Override
    public DeclarationSpecifier merge(DeclarationSpecifier specifier)
    {
        return specifier.mergeWithEnum(this);
    }
    
    @Override
    public String toString()
    {
        return String.format("enum specifier : %s", this.name);
    }
}
