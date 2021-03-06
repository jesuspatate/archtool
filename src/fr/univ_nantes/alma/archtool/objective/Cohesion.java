package fr.univ_nantes.alma.archtool.objective;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.univ_nantes.alma.archtool.architectureModel.Component;
import fr.univ_nantes.alma.archtool.architectureModel.Connector;
import fr.univ_nantes.alma.archtool.architectureModel.Facade;
import fr.univ_nantes.alma.archtool.architectureModel.Interface;
import fr.univ_nantes.alma.archtool.sourceModel.Call;
import fr.univ_nantes.alma.archtool.sourceModel.ComplexType;
import fr.univ_nantes.alma.archtool.sourceModel.Function;
import fr.univ_nantes.alma.archtool.sourceModel.GlobalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.LocalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.PrimitiveType;
import fr.univ_nantes.alma.archtool.sourceModel.Variable;

public class Cohesion
{
    /**
     * Bonus accordé pour la mesure de la cohésion lorsque deux éléments
     * appartiennent au même fichier.
     */
    static final double BONUS_SAME_FILE = 1.2;

    /**
     * Poids de la cohésion sur les appels communs dans le calcul de la cohésion
     * entre 2 fonctions.
     */
    static final double WEIGHT_FCT_COHESION_CALLS = 1.0;

    /**
     * Poids de la cohésion sur les variables globales communes dans le calcul
     * de la cohésion entre 2 fonctions.
     */
    static final double WEIGHT_FCT_COHESION_GLOBALS = 1.0;

    /**
     * Poids de la cohésion sur les variables locales communes dans le calcul de
     * la cohésion entre 2 fonctions.
     */
    static final double WEIGHT_FCT_COHESION_LOCALS = 1.0;

    /**
     * Poids de la cohésion sur les types communs dans le calcul de la cohésion
     * entre 2 fonctions.
     */
    static final double WEIGHT_FCT_COHESION_TYPES = 1.0;

    /**
     * Poids de la cohésion sur les arguments communs dans le calcul de la
     * cohésion entre 2 fonctions.
     */
    static final double WEIGHT_FCT_COHESION_ARGS = 1.0;

    /**
     * Longueur minimale du nom d'une variable pour qu'elle soit prise en compte
     * pour la cohésion entre fonction sur les variables locales.
     */
    static final int VARNAME_MIN_LEN = 3;

    private static final Similarity similarity = new Similarity();

    /**
     * Évalue la cohésion entre 2 interfaces d'un composant.
     * 
     * @return Un double entre 0.0 et 100.0
     */
    public double interfacesCohesion(Interface itf1, Interface itf2)
    {
        double result = 0.0;

        Set<Function> functions1 = itf1.getFunctions();
        Set<Function> functions2 = itf2.getFunctions();

        Set<GlobalVariable> variables1 = itf1.getGlobalVariables();
        Set<GlobalVariable> variables2 = itf2.getGlobalVariables();

        Set<ComplexType> types1 = itf1.getComplexTypes();
        Set<ComplexType> types2 = itf2.getComplexTypes();

        int nbPairs = 0;

        for (Function fct1 : functions1)
        {
            for (Function fct2 : functions2)
            {
                result += cohesion(fct1, fct2);
                ++nbPairs;
            }

            for (GlobalVariable var2 : variables2)
            {
                result += cohesion(fct1, var2);
                ++nbPairs;
            }

            for (ComplexType t2 : types2)
            {
                result += cohesion(fct1, t2);
                ++nbPairs;
            }
        }

        for (Function fct2 : functions2)
        {
            for (GlobalVariable var1 : variables1)
            {
                result += cohesion(fct2, var1);
                ++nbPairs;
            }

            for (ComplexType t1 : types1)
            {
                result += cohesion(fct2, t1);
                ++nbPairs;
            }
        }

        if (nbPairs > 0)
        {
            result /= nbPairs;
        }

        return result;
    }

    /**
     * Évalue la cohésion entre 2 façaces d'un connecteur.
     * 
     * @return Un double entre 0.0 et 100.0
     */
    public double facadesCohesion(Facade itf1, Facade itf2)
    {
        double result = 0.0;

        Set<Function> functions1 = itf1.getFunctions();
        Set<Function> functions2 = itf2.getFunctions();

        Set<GlobalVariable> variables1 = itf1.getGlobalVariables();
        Set<GlobalVariable> variables2 = itf2.getGlobalVariables();

        Set<ComplexType> types1 = itf1.getComplexTypes();
        Set<ComplexType> types2 = itf2.getComplexTypes();

        int nbPairs = 0;

        for (Function fct1 : functions1)
        {
            for (Function fct2 : functions2)
            {
                result += cohesion(fct1, fct2);
                ++nbPairs;
            }

            for (GlobalVariable var2 : variables2)
            {
                result += cohesion(fct1, var2);
                ++nbPairs;
            }

            for (ComplexType t2 : types2)
            {
                result += cohesion(fct1, t2);
                ++nbPairs;
            }
        }

        for (Function fct2 : functions2)
        {
            for (GlobalVariable var1 : variables1)
            {
                result += cohesion(fct2, var1);
                ++nbPairs;
            }

            for (ComplexType t1 : types1)
            {
                result += cohesion(fct2, t1);
                ++nbPairs;
            }
        }

        if (nbPairs > 0)
        {
            result /= nbPairs;
        }

        return result;
    }

    /**
     * Évalue la cohésion interne d'un composant.
     * 
     * @param comp
     *            Le composant à évaluer
     * 
     * @return Un double entre 0.0 et 100.0
     */
    public double internalCohesion(final Component comp)
    {
        double result = 0.0;

        Set<Function> compFcts = comp.getFunctions();
        Set<GlobalVariable> compVars = comp.getGlobalVariables();
        Set<ComplexType> compTypes = comp.getComplexTypes();
        int nbEntities = compFcts.size() + compVars.size() + compTypes.size();

        if (nbEntities > 1)
        {
            if (compFcts.size() > 0)
            {
                int n = 0;

                if (compFcts.size() > 1)
                {
                    result += cohesionFcts(compFcts);
                    ++n;
                }

                if (compVars.size() > 0)
                {
                    result += cohesionFctsVars(compFcts, compVars);
                    ++n;
                }

                if (compTypes.size() > 0)
                {
                    result += cohesionFctsTypes(compFcts, compTypes);
                    ++n;
                }

                result /= n;
            }

            else if (compVars.size() > 0 && compTypes.size() > 0)
            {
                result += cohesionVarsTypes(compVars, compTypes);
            }
        }

        else if (nbEntities > 0)
        {
            result = 100.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion interne d'une interface.
     * 
     * @param itf
     *            L'interface à évaluer
     * 
     * @return Un double entre 0.0 et 100.0
     */
    public double internalCohesion(final Interface itf)
    {
        double result = 0.0;

        Set<Function> itfFcts = itf.getFunctions();
        Set<GlobalVariable> itfVars = itf.getGlobalVariables();
        Set<ComplexType> itfTypes = itf.getComplexTypes();
        int nbEntities = itfFcts.size() + itfVars.size() + itfTypes.size();

        if (nbEntities > 1)
        {
            if (itfFcts.size() > 0)
            {
                int n = 0;

                if (itfFcts.size() > 1)
                {
                    result += cohesionFcts(itfFcts);
                    ++n;
                }

                if (itfVars.size() > 0)
                {
                    result += cohesionFctsVars(itfFcts, itfVars);
                    ++n;
                }

                if (itfTypes.size() > 0)
                {
                    result += cohesionFctsTypes(itfFcts, itfTypes);
                    ++n;
                }

                result /= n;
            }

            else if (itfVars.size() > 0 && itfTypes.size() > 0)
            {
                result += cohesionVarsTypes(itfVars, itfTypes);
            }
        }

        else if (nbEntities > 0)
        {
            result = 100.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion interne d'un connecteur.
     * 
     * @param con
     *            Le connecteur à évaluer
     * 
     * @return Un double entre 0.0 et 100.0
     */
    public double internalCohesion(final Connector con)
    {
        double result = 0.0;

        Set<Function> conFcts = con.getFunctions();
        Set<GlobalVariable> conVars = con.getGlobalVariables();
        Set<ComplexType> conTypes = con.getComplexTypes();
        int nbEntities = conFcts.size() + conVars.size() + conTypes.size();

        if (nbEntities > 1)
        {
            if (conFcts.size() > 0)
            {
                int n = 0;

                if (conFcts.size() > 1)
                {
                    result += cohesionFcts(conFcts);
                    ++n;
                }

                if (conVars.size() > 0)
                {
                    result += cohesionFctsVars(conFcts, conVars);
                    ++n;
                }

                if (conTypes.size() > 0)
                {
                    result += cohesionFctsTypes(conFcts, conTypes);
                    ++n;
                }

                result /= n;
            }

            else if (conVars.size() > 0 && conTypes.size() > 0)
            {
                result += cohesionVarsTypes(conVars, conTypes);
            }
        }

        else if (nbEntities > 0)
        {
            result = 100.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion interne d'une interface.
     * 
     * @param itf
     *            L'interface à évaluer
     * 
     * @return Un double entre 0.0 et 100.0
     */
    public double internalCohesion(final Facade fcd)
    {
        double result = 0.0;

        Set<Function> fcdFcts = fcd.getFunctions();
        Set<GlobalVariable> fcdVars = fcd.getGlobalVariables();
        Set<ComplexType> fcdTypes = fcd.getComplexTypes();
        int nbEntities = fcdFcts.size() + fcdVars.size() + fcdTypes.size();

        if (nbEntities > 1)
        {
            if (fcdFcts.size() > 0)
            {
                int n = 0;

                if (fcdFcts.size() > 1)
                {
                    result += cohesionFcts(fcdFcts);
                    ++n;
                }

                if (fcdVars.size() > 0)
                {
                    result += cohesionFctsVars(fcdFcts, fcdVars);
                    ++n;
                }

                if (fcdTypes.size() > 0)
                {
                    result += cohesionFctsTypes(fcdFcts, fcdTypes);
                    ++n;
                }

                result /= n;
            }

            else if (fcdVars.size() > 0 && fcdTypes.size() > 0)
            {
                result += cohesionVarsTypes(fcdVars, fcdTypes);
            }
        }

        else if (nbEntities > 0)
        {
            result = 100.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion moyenne d'un ensemble de fonctions.
     * 
     * @param fcts
     *            Un ensemble de fonctions d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesionFcts(final Set<Function> fcts)
    {
        double result = 0.0;
        double sum = 0.0;

        Function[] fctArray = new Function[fcts.size()];
        fcts.toArray(fctArray);

        if (fctArray.length > 0)
        {
            for (int i = 0; i < (fctArray.length - 1); ++i)
            {
                for (int j = (i + 1); j < fctArray.length; ++j)
                {
                    final Function f1 = fctArray[i];
                    final Function f2 = fctArray[j];

                    sum += this.cohesion(f1, f2);
                }
            }

            result = sum / (fctArray.length * (fctArray.length - 1) / 2);
        }

        else
        {
            result = 1.0;
        }

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre entre un ensemble de fonctions et un
     * ensemble de variables globales.
     * 
     * @param fcts
     *            Un ensemble de fonctions d'un modèle de code source
     * @param vars
     *            Un ensemble de variables globales d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesionFctsVars(final Set<Function> fcts,
            final Set<GlobalVariable> vars)
    {
        double result = 0.0;
        double sum = 0.0;

        for (Function fct : fcts)
        {
            for (GlobalVariable var : vars)
            {
                sum += this.cohesion(fct, var);
            }
        }

        double nbPairs = fcts.size() * vars.size();

        if (nbPairs > 0)
        {
            result = sum / nbPairs;
        }

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre un ensemble de fonctions et un ensemble de types.
     * 
     * @param fcts
     *            Un ensemble de fonctions d'un modèle de code source
     * @param types
     *            Un ensemble de types d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesionFctsTypes(final Set<Function> fcts, final Set<ComplexType> types)
    {
        double result = 0.0;
        double sum = 0.0;;

        for (Function fct : fcts)
        {
            for (ComplexType type : types)
            {
                sum += this.cohesion(fct, type);
            }
        }

        double nbPairs = fcts.size() * types.size();

        if (nbPairs > 0)
        {
            result = sum / nbPairs;
        }

        return result;
    }

    /**
     * Évalue la cohésion moyenne entre entre un ensemble de variables globales et un
     * ensemble de types.
     * 
     * @param fcts
     *            Un ensemble de variables globales d'un modèle de code source
     * @param vars
     *            Un ensemble de types d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesionVarsTypes(final Set<GlobalVariable> vars, Set<ComplexType> types)
    {
        double result = 0.0;
        double sum = 0.0;

        for (Variable var : vars)
        {
            for (ComplexType t : types)
            {
                if (var.ofType(t))
                {
                    ++sum;
                }
            }
        }

        double nbPairs = vars.size() * types.size();

        if (nbPairs > 0)
        {
            result = 100.0 * sum / nbPairs;
        }

        return result;
    }

    /**
     * Évalue la cohésion de 2 fonctions.
     * 
     * <p>
     * La cohésion de 2 fonctions est évaluée sur la moyenne des pourcentages :
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
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesion(final Function f1, final Function f2)
    {
        double result = 0.0;

        double cc = this.cohesionCalls(f1, f2);
        double cg = this.cohesionGlobalVars(f1, f2);
        double cl = this.cohesionLocalVars(f1, f2);
        double ct = this.cohesionTypes(f1, f2);
        double ca = this.cohesionArguments(f1, f2);

        result += WEIGHT_FCT_COHESION_CALLS * cc;
        result += WEIGHT_FCT_COHESION_GLOBALS * cg;
        result += WEIGHT_FCT_COHESION_LOCALS * cl;
        result += WEIGHT_FCT_COHESION_TYPES * ct;
        result += WEIGHT_FCT_COHESION_ARGS * ca;

        if (f1.getSourceFile() != null && f2.getSourceFile() != null)
        {
            if (f1.getSourceFile().equals(f2.getSourceFile()))
            {
                result *= BONUS_SAME_FILE;
            }
        }

        result /=
                WEIGHT_FCT_COHESION_CALLS + WEIGHT_FCT_COHESION_GLOBALS
                        + WEIGHT_FCT_COHESION_LOCALS
                        + WEIGHT_FCT_COHESION_TYPES + WEIGHT_FCT_COHESION_ARGS;

        result = (result > 100.0) ? 100.0 : result;

        // System.out.println(f1.getName() + " - " + f2.getName() + " : "
        // + "CC=" + cc + " CG=" + cg + " CL=" + cl + " CT=" + ct
        // + " CA=" + ca + " -- " + result); // DBG

        return result;
    }

    /**
     * Évalue la cohésion entre une fonction et une variable globale.
     * 
     * <p>
     * La cohésion entre une fonction f et une variable globale au programme
     * (resp. au fichier) v est égale au pourcentage d'accès à v sur le nombre
     * total d'accès à des variables globales.
     * </p>
     * 
     * @param fct
     *            Une fonction d'un modèle de code source
     * @param var
     *            Une variable d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesion(final Function fct, GlobalVariable var)
    {
        double result = 0.0;
        double total = 0.0; // Total accesses
        double nbAccessToVar = 0.0;

        Map<GlobalVariable, Integer> fctVars = fct.getCoreGlobalVariables();

        for (Integer n : fctVars.values())
        {
            total += n;
        }

        if (fctVars.containsKey(var))
        {
            nbAccessToVar = fctVars.get(var);

            // Same file ?
            if (fct.getSourceFile() != null && var.getSourceFile() != null)
            {
                if (fct.getSourceFile().equals(var.getSourceFile()))
                {
                    nbAccessToVar *= BONUS_SAME_FILE;
                }
            }
        }

        if (total > 0)
        {
            result = 100.0 * nbAccessToVar / total;
        }

        return result;
    }

    /**
     * Évalue la cohésion entre une fonction et un type.
     * 
     * <p>
     * La cohésion entre une fonction <em>f</em> et un type <em>t</em> est égal
     * au pourcentage d'utilisations de <em>t</em> par <em>f</em> sur le nombre
     * total d'utilisations de l'ensemble des types utilisés par <em>f</em>.
     * </p>
     * 
     * @param fct
     *            Une fonction d'un modèle de code source
     * @param type
     *            Un type d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     */
    private double cohesion(final Function fct, ComplexType type)
    {
        double result = 0.0;
        double nbUsesOfType = 0.0;
        double total = 0.0; // Total uses of types

        Map<ComplexType, Integer> usedTypes = fct.getTotalComplexTypes();

        for (ComplexType t : usedTypes.keySet())
        {
            if (t.equals(type))
            {
                nbUsesOfType += usedTypes.get(t);
            }

            total += usedTypes.get(t);
        }

        // Same file ?
        if (fct.getSourceFile() != null && type.getSourceFile() != null)
        {
            if (fct.getSourceFile().equals(type.getSourceFile()))
            {
                nbUsesOfType *= BONUS_SAME_FILE;
            }
        }

        if (total > 0)
        {
            result = 100.0 * nbUsesOfType / total;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le pourcentage de variables globales au programme accédées en
     * commun sur le nombre total de variables globales au programme accédées
     * dans les deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionGlobalVars(Function f1, Function f2)
    {
        double result = 0.0;

        Map<GlobalVariable, Integer> globalVars1 = f1.getCoreGlobalVariables();
        Map<GlobalVariable, Integer> globalVars2 = f2.getCoreGlobalVariables();

        Set<GlobalVariable> globalVarsTotal = new HashSet<GlobalVariable>();
        Set<GlobalVariable> globalVarsCommon = new HashSet<GlobalVariable>();

        for (GlobalVariable var : globalVars1.keySet())
        {
            globalVarsTotal.add(var);
        }

        for (GlobalVariable var : globalVars2.keySet())
        {
            boolean newVar = globalVarsTotal.add(var);

            if (newVar == false)
            {
                globalVarsCommon.add(var);
            }
        }

        if (globalVarsTotal.size() > 0)
        {
            result = (100.0 * globalVarsCommon.size()) / globalVarsTotal.size();
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le pourcentage de variables locales similaires entre les deux
     * fonctions sur le nombre total de variables locales utilisées dans les
     * deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionLocalVars(Function f1, Function f2)
    {
        double result = 0.0;
        double nbCommon = 0;

        Map<LocalVariable, Integer> localVars1 = f1.getLocalVariables();
        Map<LocalVariable, Integer> localVars2 = f2.getLocalVariables();

        for (LocalVariable var1 : localVars1.keySet())
        {
            for (LocalVariable var2 : localVars2.keySet())
            {
                if (var1.getName().length() >= VARNAME_MIN_LEN
                        && var2.getName().length() >= VARNAME_MIN_LEN)
                {
                    if (var1.getType().equals(var2.getType()))
                    {
                        if (similarity.similar(var1.getName(), var2.getName()))
                        {
                            ++nbCommon;
                        }
                    }
                }
            }
        }

        double nbPairs = localVars1.size() * localVars2.size();

        if (nbPairs > 0)
        {
            result = 100.0 * nbCommon / nbPairs;
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le pourcentage de types utilisés en commun dans les deux
     * fonctions sur le nombre total de types utilisés dans les deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionTypes(Function f1, Function f2)
    {
        double result = 0.0;

        Map<ComplexType, Integer> usedTypesFct1 = f1.getTotalComplexTypes();
        Map<ComplexType, Integer> usedTypesFct2 = f2.getTotalComplexTypes();

        Set<ComplexType> usedTypesTotal = new HashSet<ComplexType>();
        Set<ComplexType> usedTypesCommon = new HashSet<ComplexType>();

        for (ComplexType t : usedTypesFct1.keySet())
        {
            if (t.equals(PrimitiveType.voidType) == false
                    && t.equals(ComplexType.anonymousType) == false)
            {
                usedTypesTotal.add(t);
            }
        }

        for (ComplexType t : usedTypesFct2.keySet())
        {
            if (t.equals(PrimitiveType.voidType) == false
                    && t.equals(ComplexType.anonymousType) == false)
            {
                boolean newType = usedTypesTotal.add(t);

                if (newType == false)
                {
                    usedTypesCommon.add(t);
                }
            }
        }

        if (usedTypesTotal.size() > 0)
        {
            result = (100.0 * usedTypesCommon.size()) / usedTypesTotal.size();
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le pourcentage d'appels de fonction communs dans les deux
     * fonctions sur le nombre total d'appels dans les deux fonctions. Les
     * paramètres des appels ne sont pas pris en compte.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionCalls(Function f1, Function f2)
    {
        double result = 0.0;

        Set<Call> calls1 = f1.getTotalCalls();
        Set<Call> calls2 = f2.getTotalCalls();

        Set<Function> fctCalledTotal = new HashSet<Function>();
        Set<Function> fctCalledCommon = new HashSet<Function>();

        for (Call call : calls1)
        {
            fctCalledTotal.add(call.getFunction());
        }

        for (Call call : calls2)
        {
            boolean added = false;

            added = fctCalledTotal.add(call.getFunction());

            if (added == false)
            {
                fctCalledCommon.add(call.getFunction());
            }
        }

        if (fctCalledTotal.size() > 0)
        {
            result = (100.0 * fctCalledCommon.size()) / fctCalledTotal.size();
        }

        return result;
    }

    /**
     * Méthode appelée pour le calcul de la cohésion entre 2 fonctions.
     * 
     * <p>
     * Calcule le pourcentage de paramètres similaires entre les deux fonctions
     * sur l'ensemble des paramètres des deux fonctions.
     * </p>
     * 
     * @param f1
     *            Une fonction d'un modèle de code source
     * @param f2
     *            Une autre fonction d'un modèle de code source
     * 
     * @return Un double entre 0.0 et 100.0
     * 
     * @see #cohesion(Function, Function)
     */
    private double cohesionArguments(Function f1, Function f2)
    {
        double result = 0.0;
        double nbCommon = 0;

        Set<LocalVariable> argsFct1 = f1.getArguments();
        Set<LocalVariable> argsFct2 = f2.getArguments();
        Set<LocalVariable> args = new HashSet<LocalVariable>();

        for (LocalVariable arg1 : argsFct1)
        {
            for (LocalVariable arg2 : argsFct2)
            {
                if (arg1.getType().equals(arg2.getType()))
                {
                    if (similarity.similar(arg1.getName(), arg2.getName()))
                    {
                        ++nbCommon;
                    }
                }

                args.add(arg1);
                args.add(arg2);
            }
        }

        if (args.size() > 0)
        {
            result = 100.0 * nbCommon / args.size();
        }

        return result;
    }
}
