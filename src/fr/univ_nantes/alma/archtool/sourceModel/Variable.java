package fr.univ_nantes.alma.archtool.sourceModel;

public abstract class Variable
{
    private final String name;

    private final Type type;

    public Variable(final String name, final Type type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return this.name;
    }

    public Type getType()
    {
        return this.type;
    }

    public boolean ofType(final Type t)
    {
        // TODO Renvoyer faux si t ou le type de la variable est anonyme !!

        return t.equals(this.type);
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", this.getType().getName(), this.getName());
    }
}
