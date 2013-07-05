package fr.univ_nantes.alma.archtool.sourceModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Classe du modèle de code source représentant une fonction.
 * 
 * <p>
 * Une fonction est composée de
 * <ul>
 * <li>son nom,</li>
 * <li>son type de retour,</li>
 * <li>l'ensemble de ses arguments,</li>
 * <li>le fichier dans lequel elle est définie,
 * <li>son corps (bloc de code)</li>
 * </ul>
 * </p>
 */
public class Function
{
    private final String name;
    
    private final boolean isStatic;

    private Set<LocalVariable> arguments = null;

    private final Type returnType;

    private File sourceFile = null;

    private Block body = null;

    /**
     * Constructeur pour les fonctions dont on ne connait pas la définition.
     * 
     * @param name
     * @param sourceFile
     */
    public Function(final String name, final Type returnType)
    {
        this(name, returnType, false);
    }
    
    public Function(final String name, Type returnType, boolean isStatic)
    {
        this.name = name;
        this.returnType = returnType;
        this.isStatic = isStatic;
    }

    public Function(final String name, final Type returnType, 
            final Set<LocalVariable> arguments, final Block body,
            final File sourceFile)
    {
        this(name, returnType);
        this.arguments = arguments;
        this.body = body;
        this.sourceFile = sourceFile;
    }
    
    public Function(final String name, final Type returnType, boolean isStatic, 
            final Set<LocalVariable> arguments, final Block body,
            final File sourceFile)
    {
        this(name, returnType, isStatic);
        this.arguments = arguments;
        this.body = body;
        this.sourceFile = sourceFile;
    }

    /**
     * Retourne le nom de la fonction.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Retourne la liste des arguments de la fonction.
     */
    public Set<LocalVariable> getArguments()
    {
        return new HashSet<LocalVariable>(this.arguments);
    }

    /**
     * Retourne le type de retour de la fonction.
     */
    public Type getReturnType()
    {
        return this.returnType;
    }
    
    public boolean isStatic()
    {
        return this.isStatic;
    }    

    /**
     * Retourne le fichier source dans lequel est définie la fonction.
     */
    public File getSourceFile()
    {
        return this.sourceFile;
    }

    public void setArguments(Set<LocalVariable> arguments)
    {
        this.arguments = arguments;
    }

    public void setSourceFile(File sourceFile)
    {
        this.sourceFile = sourceFile;
    }

    public void setBody(Block body)
    {
        this.body = body;
    }

    /**
     * Renvoie l'ensemble des variables globales au programme utilisées par la
     * fonction.
     * 
     * @return Une map ayant pour clés les variables globales au programme
     *         accédées dans le corps de la fonction et pour valeurs leur nombre
     *         d'accès.
     */
    public Map<GlobalVariable, Integer> getProgramGlobals()
    {
        return this.body.getProgramGlobals();
    }

    /**
     * Renvoie l'ensemble des variables globales au fichier utilisées par la
     * fonction.
     * 
     * @return Une map ayant pour clés les variables globales au fichier
     *         accédées dans le corps de la fonction et pour valeurs leur nombre
     *         d'accès.
     */
    public Map<GlobalVariable, Integer> getFileGlobals()
    {
        return this.body.getFileGlobals();
    }

    /**
     * Renvoie l'ensemble des variables locales de la fonction.
     * 
     * <p>
     * Les arguments ne sont pas considérés comme des variables locales.
     * </p>
     * 
     * @return Une map ayant pour clés les variables locales utilisées dans le
     *         corps de la fonction et pour valeurs leur nombre d'utilisations.
     */
    public Map<LocalVariable, Integer> getLocals()
    {
        return this.body.getLocals();
    }

    /**
     * Renvoie l'ensemble des appels réalisés dans la fonction.
     * 
     * @return Un set contenant les appels réalisés dans le corps de la
     *         fonction.
     */
    public Set<Call> getCalls()
    {
        return this.body.getCalls();
    }

    public boolean calls(final Function fct)
    {
        boolean called = false;

        for (final Call call : this.getCalls())
        {
            if (call.getFunction().equals(fct))
            {
                called = true;
            }
        }

        return called;
    }

    /**
     * Renvoie l'ensemble des types utilisés par la fonction.
     * 
     * <p>
     * Les types utilisés peuvent être primitifs ou complexes. Les arguments ne
     * sont pas pris en compte.
     * </p>
     * 
     * @return Un set contenant les types utilisés dans le corps de la fonction.
     */
    public Map<Type, Integer> getUsedTypes()
    {
        return this.body.getUsedTypes();
    }
    
    @Override
    public String toString()
    {
        return String.format("Function : %s", this.name);
    }
}
