package fr.univ_nantes.alma.archtool.sourceModel;

public class GlobalVariable extends Variable
{
    private final boolean isStatic;
    private final File sourceFile;

    public GlobalVariable(final String name, final Type type, boolean isStatic,
            final File sourceFile)
    {
        super(name, type);
        this.isStatic = isStatic;
        this.sourceFile = sourceFile;
    }

    public File getSourceFile()
    {
        return this.sourceFile;
    }
    
    public boolean isStatic()
    {
        return this.isStatic;
    }
}