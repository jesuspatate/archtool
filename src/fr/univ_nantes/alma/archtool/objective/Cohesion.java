package fr.univ_nantes.alma.archtool.objective;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.univ_nantes.alma.archtool.architectureModel.Component;
import fr.univ_nantes.alma.archtool.architectureModel.Connector;
import fr.univ_nantes.alma.archtool.architectureModel.Interface;
import fr.univ_nantes.alma.archtool.coa.COA;
import fr.univ_nantes.alma.archtool.sourceModel.Call;
import fr.univ_nantes.alma.archtool.sourceModel.FileGlobalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.Function;
import fr.univ_nantes.alma.archtool.sourceModel.LocalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.ProgramGlobalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.Type;
import fr.univ_nantes.alma.archtool.sourceModel.Variable;

public class Cohesion
{
    /**
     * Bonus accordé pour la mesure de la cohésion lorsque deux éléments
     * appartiennent au même fichier.
     */
    static final double BONUS_SAME_FILE = 1.5;

    /**
     * Valeur maximale de cohésion entre 2 fonctions.
     */
    static final double MAX_COHESION_FCT_FCT = 6.0;

    private final COA coa;

    public Cohesion(final COA coa)
    {
        this.coa = coa;
    }

    /**
     * Évalue la cohésion entre 2 interfaces.
     */
    public double interfacesCohesion(Interface itf1, Interface itf2)
    {
        double result = 0.0;

        Set<Function> functions1 = this.coa.getInterfaceFunctions(itf1);
        Set<Function> functions2 = this.coa.getInterfaceFunctions(itf2);

        Set<Variable> variables1 = this.coa.getInterfaceVariables(itf1);
        Set<Variable> variables2 = this.coa.getInterfaceVariables(itf2);

        Set<Type> types1 = this.coa.getInterfaceTypes(itf1);
        Set<Type> types2 = this.coa.getInterfaceTypes(itf2);

        int nbPairs = 0;
        
        for (Function fct1 : functions1)
        {
            for (Function fct2 : functions2)
            {
                result += cohesion(fct1, fct2);
                ++nbPairs;
            }

            for (Variable var2 : variables2)
            {
                result += cohesion(fct1, var2);
                ++nbPairs;
            }

            for (Type t2 : types2)
            {
                result += cohesion(fct1, t2);
                ++nbPairs;
            }
        }

        for (Function fct2 : functions2)
        {
            for (Variable var1 : variables1)
            {
                result += cohesion(fct2, var1);
                ++nbPairs;
            }

            for (Type t1 : types1)
            {
                result += cohesion(fct2, t1);
                ++nbPairs;
            }
        }
        
        result /= nbPairs;
        
        return result;
    }

    /**
     * Évalue la cohésion interne d'un composant.
     * 
     * @param comp
     *            Le composant à évaluer
     *            
     * @return Un double entre 0.0 et 1.0    
     */
    public double componentInternalCohesion(final Component comp)
    {
        double result = 0.0;

        int nbFcts = this.coa.getComponentFunctions(comp).size();
        int nbVars = this.coa.getComponentVariables(comp).size();
        int nbTypes = this.coa.getComponentTypes(comp).size();

        if (nbFcts + nbVars + nbTypes > 1)
        {
            if (nbFcts > 0)
            {
                int n = 0;

                if (nbFcts > 1)
                {
                    result += internalCohesionCompFct(comp);
                    ++n;
                }

                if (nbVars > 0)
                {
                    result += internalCohesionCompFctVar(comp);
                    ++n;
                }

                if (nbTypes > 0)
                {
                    result += internalCohesionCompFctType(comp);
                    ++n;
                }

                result /= n;
            }

            else if (nbVars > 0 && nbTypes > 0)
            {
                result += internalCohesionCompVarType(comp);
            }
        }

        else
        {
            result = 1.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion interne d'une interface.
     * 
     * @param itf
     *            L'interface à évaluer
     *            
     * @return Un double entre 0.0 et 1.0            
     */
    public double interfaceInternalCohesion(final Interface itf)
    {
        double result = 0.0;

        int nbFcts = this.coa.getInterfaceFunctions(itf).size();
        int nbVars = this.coa.getInterfaceVariables(itf).size();
        int nbTypes = this.coa.getInterfaceTypes(itf).size();

        if (nbFcts + nbVars + nbTypes > 1)
        {
            if (nbFcts > 0)
            {
                int n = 0;

                if (nbFcts > 1)
                {
                    result += internalCohesionItfFct(itf);
                    ++n;
                }

                if (nbVars > 0)
                {
                    result += internalCohesionItfFctVar(itf);
                    ++n;
                }

                if (nbTypes > 0)
                {
                    result += internalCohesionItfFctType(itf);
                    ++n;
                }

                result /= n;
            }

            else if (nbVars > 0 && nbTypes > 0)
            {
                result += internalCohesionItfVarType(itf);
            }
        }

        else
        {
            result = 1.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion interne d'un connecteur.
     * 
     * @param con
     *            Le connecteur à évaluer
     * 
     * @return Un double entre 0.0 et 1.0
     */
    public double connectorInternalCohesion(final Connector con)
    {
        double result = 0.0;

        int nbFcts = this.coa.getConnectorFunctions(con).size();
        int nbVars = this.coa.getConnectorVariables(con).size();
        int nbTypes = this.coa.getConnectorTypes(con).size();

        if (nbFcts + nbVars + nbTypes > 1)
        {
            if (nbFcts > 0)
            {
                int n = 0;

                if (nbFcts > 1)
                {
                    result += internalCohesionConFct(con);
                    ++n;
                }

                if (nbVars > 0)
                {
                    result += internalCohesionConFctVar(con);
                    ++n;
                }

                if (nbTypes > 0)
                {
                    result += internalCohesionConFctType(con);
                    ++n;
                }

                result /= n;
            }

            else if (nbVars > 0 && nbTypes > 0)
            {
                result += internalCohesionConVarType(con);
            }
        }

        else
        {
            result = 1.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion moyenne des fonctions d'un composant.
     * 
     * @param comp
     *            Le composant évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionCompFct(final Component comp)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> functions = this.coa.getComponentFunctions(comp);
        int nbFunctions = functions.size();

        if (nbFunctions > 0)
        {

            Function[] fctArray = new Function[nbFunctions];
            this.coa.getComponentFunctions(comp).toArray(fctArray);

            for (int i = 0 ; i < (nbFunctions - 1) ; ++i)
            {
                for (int j = (i + 1) ; j < nbFunctions ; ++j)
                {
                    final Function f1 = fctArray[i];
                    final Function f2 = fctArray[j];

                    sum += this.cohesion(f1, f2);
                }
            }

            result = sum / (nbFunctions * (nbFunctions - 1) / 2);
        }

        else
        {
            result = 1.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion moyenne des fonctions d'une interface.
     * 
     * @param itf
     *            L'interface évaluée
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionItfFct(final Interface itf)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> functions = this.coa.getInterfaceFunctions(itf);
        int nbFunctions = functions.size();

        Function[] fctArray = new Function[nbFunctions];
        this.coa.getInterfaceFunctions(itf).toArray(fctArray);

        for (int i = 0 ; i < (nbFunctions - 2) ; ++i)
        {
            for (int j = (i + 1) ; j < (nbFunctions - 1) ; ++j)
            {
                final Function f1 = fctArray[i];
                final Function f2 = fctArray[j];

                sum += this.cohesion(f1, f2);
            }
        }

        result = sum / (nbFunctions * (nbFunctions - 1) / 2);

        return result;
    }

    /**
     * Évalue la cohésion moyenne des fonctions d'un connecteur.
     * 
     * @param con
     *            Le connecteur évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionConFct(final Connector con)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> functions = this.coa.getConnectorFunctions(con);
        int nbFunctions = functions.size();

        Function[] fctArray = new Function[nbFunctions];
        this.coa.getConnectorFunctions(con).toArray(fctArray);

        for (int i = 0 ; i < (nbFunctions - 2) ; ++i)
        {
            for (int j = (i + 1) ; j < (nbFunctions - 1) ; ++j)
            {
                final Function f1 = fctArray[i];
                final Function f2 = fctArray[j];

                sum += this.cohesion(f1, f2);
            }
        }

        result = sum / (nbFunctions * (nbFunctions - 1) / 2);

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre les fonctions et les variables d'un
     * composant.
     * 
     * @param comp
     *            Le composant évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionCompFctVar(final Component comp)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> compFcts = this.coa.getComponentFunctions(comp);
        Set<Variable> compVars = this.coa.getComponentVariables(comp);

        for (Function fct : compFcts)
        {
            for (Variable var : compVars)
            {
                sum += this.cohesion(fct, var);
            }
        }

        double nbPairs = compFcts.size() * compVars.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre les fonctions et les variables d'une
     * interface.
     * 
     * @param itf
     *            L'interface évaluée
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionItfFctVar(final Interface itf)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> itfFcts = this.coa.getInterfaceFunctions(itf);
        Set<Variable> itfVars = this.coa.getInterfaceVariables(itf);

        for (Function fct : itfFcts)
        {
            for (Variable var : itfVars)
            {
                sum += this.cohesion(fct, var);
            }
        }

        double nbPairs = itfFcts.size() * itfVars.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre les fonctions et les variables d'un
     * connecteur.
     * 
     * @param con
     *            Le connecteur évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionConFctVar(final Connector con)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> conFcts = this.coa.getConnectorFunctions(con);
        Set<Variable> conVars = this.coa.getConnectorVariables(con);

        for (Function fct : conFcts)
        {
            for (Variable var : conVars)
            {
                sum += this.cohesion(fct, var);
            }
        }

        double nbPairs = conFcts.size() * conVars.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre les fonctions et les types d'un
     * composant.
     * 
     * @param comp
     *            Le composant évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionCompFctType(Component comp)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> compFcts = this.coa.getComponentFunctions(comp);
        Set<Type> compTypes = this.coa.getComponentTypes(comp);

        for (Function fct : compFcts)
        {
            for (Type type : compTypes)
            {
                sum += this.cohesion(fct, type);
            }
        }

        double nbPairs = compFcts.size() * compTypes.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre les fonctions et les types d'une
     * interface.
     * 
     * @param itf
     *            L'interface évaluée
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionItfFctType(Interface itf)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> itfFcts = this.coa.getInterfaceFunctions(itf);
        Set<Type> itfTypes = this.coa.getInterfaceTypes(itf);

        for (Function fct : itfFcts)
        {
            for (Type type : itfTypes)
            {
                sum += this.cohesion(fct, type);
            }
        }

        double nbPairs = itfFcts.size() * itfTypes.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre les fonctions et les types d'un
     * connecteur.
     * 
     * @param comp
     *            Le connecteur évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionConFctType(Connector con)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Function> conFcts = this.coa.getConnectorFunctions(con);
        Set<Type> conTypes = this.coa.getConnectorTypes(con);

        for (Function fct : conFcts)
        {
            for (Type type : conTypes)
            {
                sum += this.cohesion(fct, type);
            }
        }

        double nbPairs = conFcts.size() * conTypes.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Evalue la cohésion moyenne entre les variables et les types d'un
     * composant.
     * 
     * <p>
     * Calcule le ration de paires (<em>v</em>,<em>t</em>) telles que la
     * variable <em>v</em> est de type <em>t</em>.
     * </p>
     * 
     * @param comp
     *            Le composant évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionCompVarType(Component comp)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Variable> compVars = this.coa.getComponentVariables(comp);
        Set<Type> compTypes = this.coa.getComponentTypes(comp);

        for (Variable var : compVars)
        {
            for (Type t : compTypes)
            {
                if (var.ofType(t))
                {
                    ++sum;
                }
            }
        }

        double nbPairs = compVars.size() * compTypes.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Evalue la cohésion moyenne entre les variables et les types d'une
     * interface.
     * 
     * <p>
     * Calcule le ration de paires (<em>v</em>,<em>t</em>) telles que la
     * variable <em>v</em> est de type <em>t</em>.
     * </p>
     * 
     * @param itf
     *            L'interface évaluée
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionItfVarType(Interface itf)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Variable> compVars = this.coa.getInterfaceVariables(itf);
        Set<Type> compTypes = this.coa.getInterfaceTypes(itf);

        for (Variable var : compVars)
        {
            for (Type t : compTypes)
            {
                if (var.ofType(t))
                {
                    ++sum;
                }
            }
        }

        double nbPairs = compVars.size() * compTypes.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Evalue la cohésion moyenne entre les variables et les types d'un
     * connecteur.
     * 
     * <p>
     * Calcule le ration de paires (<em>v</em>,<em>t</em>) telles que la
     * variable <em>v</em> est de type <em>t</em>.
     * </p>
     * 
     * @param con
     *            Le connecteur évalué
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double internalCohesionConVarType(Connector con)
    {
        double result = 0.0;
        double sum = 0.0;

        Set<Variable> compVars = this.coa.getConnectorVariables(con);
        Set<Type> compTypes = this.coa.getConnectorTypes(con);

        for (Variable var : compVars)
        {
            for (Type t : compTypes)
            {
                if (var.ofType(t))
                {
                    ++sum;
                }
            }
        }

        double nbPairs = compVars.size() * compTypes.size();

        result = sum / nbPairs;

        return result;
    }

    /**
     * Évalue la cohésion de 2 fonctions.
     * 
     * <p>
     * La cohésion de 2 fonctions est évaluée sur la moyenne des ratios :
     * <ul>
     * <li>de variables globales communes</li>
     * <li>de variables locales communes</li>
     * <li>de types communs</li>
     * <li>d'appels de fonction communs</li>
     * </ul>
     * et sur l'appartenance à un même fichier.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double cohesion(final Function f1, final Function f2)
    {
        double result = 0.0;

        result += this.cohesionPGVars(f1, f2);
        result += this.cohesionFGVars(f1, f2);
        result += this.cohesionLocalVars(f1, f2);
        result += this.cohesionTypes(f1, f2);
        result += this.cohesionCalls(f1, f2);
        result += this.cohesionArguments(f1, f2);

        if (f1.getSourceFile().equals(f2.getSourceFile()))
        {
            result *= BONUS_SAME_FILE;
        }

        result /= MAX_COHESION_FCT_FCT;

        result = (result > 1.0) ? 1.0 : result;

        return result;
    }

    /**
     * Évalue la cohésion entre une fonction et une variable globale.
     * 
     * <p>
     * La cohésion entre une fonction f et une variable globale au programme
     * (resp. au fichier) v est évaluée sur le ratio des accès à v sur le nombre
     * total d'accès à des variables globales au programme (resp. au fichier).
     * Si v à une portée limitée à un fichier, l'appartenance au même fichier
     * est aussi prise en compte.
     * </p>
     * 
     * @param fct
     *            Une fonction d'un modèle de code source
     * @param var
     *            Une variable d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double cohesion(final Function fct, Variable var)
    {
        double result = 0.0;
        double total = 0.0; // Total accesses
        double nbAccessToVar = 0.0;

        Map<ProgramGlobalVariable, Integer> pgVars = fct.getProgramGlobals();
        Map<ProgramGlobalVariable, Integer> fgVars = fct.getProgramGlobals();

        // Program global variable
        if (pgVars.containsKey(var))
        {
            nbAccessToVar = pgVars.get(var);

            for (Integer n : pgVars.values())
            {
                total += n;
            }
        }

        // File global variable
        else if (fgVars.containsKey(var))
        {
            FileGlobalVariable v = (FileGlobalVariable) var;

            nbAccessToVar = fgVars.get(var);

            for (Integer n : fgVars.values())
            {
                total += n;
            }

            // Same file ?
            if (fct.getSourceFile().equals(v.getSourceFile()))
            {
                nbAccessToVar *= BONUS_SAME_FILE;
            }
        }

        result = nbAccessToVar / total;

        return result;
    }

    /**
     * Évalue la cohésion entre une fonction et un type.
     * 
     * <p>
     * La cohésion entre une fonction <em>f</em> et un type <em>t</em> est égal
     * au ratio du nombre d'utilisations de <em>t</em> par <em>f</em> sur le
     * nombre total d'utilisations de l'ensemble des types utilisés par
     * <em>f</em>.
     * </p>
     * 
     * @param fct
     *            Une fonction d'un modèle de code source
     * @param type
     *            Un type d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     */
    private double cohesion(final Function fct, Type type)
    {
        double result = 0.0;
        double nbUsesOfType = 0.0;
        double total = 0.0; // Total uses of types

        // Types used in the body

        Map<Type, Integer> usedTypes = fct.getUsedTypes();

        for (Type t : usedTypes.keySet())
        {
            if (t.equals(type))
            {
                nbUsesOfType += usedTypes.get(t);
            }

            total += usedTypes.get(t);
        }

        // Arguments

        Set<LocalVariable> args = fct.getArguments();

        for (LocalVariable arg : args)
        {
            if (arg.ofType(type))
            {
                ++nbUsesOfType;
            }

            ++total;
        }

        result = nbUsesOfType / total;

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le ratio des variables globales au programme accédées en commun
     * sur le nombre total de variables globales au programme accédées dans les
     * deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionPGVars(Function f1, Function f2)
    {
        double result = 0.0;
        double nbPGVars = 0;
        double nbCommon = 0;

        Map<ProgramGlobalVariable, Integer> pgVars1 = f1.getProgramGlobals();
        Map<ProgramGlobalVariable, Integer> pgVars2 = f2.getProgramGlobals();

        for (ProgramGlobalVariable var : pgVars1.keySet())
        {
            if (pgVars2.containsKey(var))
            {
                ++nbCommon;
            }
        }

        nbPGVars = pgVars1.size() + pgVars2.size() - nbCommon;

        if (nbPGVars > 0)
        {
            result = nbCommon / nbPGVars;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le ratio des variables globales à un fichier accédées en commun
     * sur le nombre total de variables globales à un fichier accédées dans les
     * deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionFGVars(Function f1, Function f2)
    {
        double result = 0.0;
        double nbFGVars = 0;
        double nbCommon = 0;

        Map<FileGlobalVariable, Integer> fgVars1 = f1.getFileGlobals();
        Map<FileGlobalVariable, Integer> fgVars2 = f2.getFileGlobals();

        for (FileGlobalVariable var : fgVars1.keySet())
        {
            if (fgVars2.containsKey(var))
            {
                ++nbCommon;
            }
        }

        nbFGVars = fgVars1.size() + fgVars2.size() - nbCommon;

        if (nbFGVars > 0)
        {
            result = nbCommon / nbFGVars;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le ratio de variables locales similaires entre les deux fonctions
     * sur le nombre total de variables locales utilisées dans les deux
     * fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionLocalVars(Function f1, Function f2)
    {
        double result = 0.0;
        double nbLocalVars = 0;
        double nbCommon = 0;

        Map<LocalVariable, Integer> localVars1 = f1.getLocals();
        Map<LocalVariable, Integer> localVars2 = f2.getLocals();

        for (LocalVariable var1 : localVars1.keySet())
        {
            for (LocalVariable var2 : localVars2.keySet())
            {
                if ((var1.getName().compareTo(var2.getName()) == 0)
                        && (var1.ofType(var2.getType())))
                {
                    ++nbCommon;
                }
            }
        }

        nbLocalVars = localVars1.size() + localVars2.size() - nbCommon;

        if (nbLocalVars > 0)
        {
            result = nbCommon / nbLocalVars;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le ratio de types utilisés en commun dans les deux fonctions sur
     * le nombre total de types utilisés dans les deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionTypes(Function f1, Function f2)
    {
        double result = 0.0;
        double nbUsedTypes = 0;
        double nbCommon = 0;

        Map<Type, Integer> usedTypes1 = f1.getUsedTypes();
        Map<Type, Integer> usedTypes2 = f2.getUsedTypes();

        for (Type t : usedTypes1.keySet())
        {
            if (usedTypes2.containsKey(t))
            {
                ++nbCommon;
            }
        }

        nbUsedTypes = usedTypes1.size() + usedTypes2.size() - nbCommon;

        if (nbUsedTypes > 0)
        {
            result = nbCommon / nbUsedTypes;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le ratio d'appels de fonction communs dans les deux fonctions sur
     * le nombre total d'appels dans les deux fonctions. Les paramètres des
     * appels ne sont pas pris en compte.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionCalls(Function f1, Function f2)
    {
        double result = 0.0;
        double nbCalls = 0;
        double nbCommon = 0;

        Set<Call> calls1 = f1.getCalls();
        Set<Call> calls2 = f2.getCalls();

        for (Call call : calls1)
        {
            Function fct = call.getFunction();

            Iterator<Call> it = calls2.iterator();
            boolean found = false;

            while (it.hasNext() && found == false)
            {
                Call c = it.next();

                if (c.getFunction().equals(fct))
                {
                    ++nbCommon;
                }
            }
        }

        nbCalls = calls1.size() + calls2.size() - nbCommon;

        if (nbCalls > 0)
        {
            result = nbCommon / nbCalls;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le ratio d'arguments similaires entre les deux fonctions sur le
     * nombre total d'arguments des deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 1.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionArguments(Function f1, Function f2)
    {
        double result = 0.0;
        double nbArgs = 0;
        double nbCommon = 0;

        Set<LocalVariable> args1 = f1.getArguments();
        Set<LocalVariable> args2 = f2.getArguments();

        for (LocalVariable var1 : args1)
        {
            for (LocalVariable var2 : args2)
            {
                if ((var1.getName().compareTo(var2.getName()) == 0)
                        && (var1.ofType(var2.getType())))
                {
                    ++nbCommon;
                }
            }
        }

        nbArgs = args1.size() + args2.size() - nbCommon;

        if (nbArgs > 0)
        {
            result = nbCommon / nbArgs;
        }

        return result;
    }
}
