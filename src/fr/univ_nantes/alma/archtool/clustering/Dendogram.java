package fr.univ_nantes.alma.archtool.clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.univ_nantes.alma.archtool.architectureModel.Architecture;
import fr.univ_nantes.alma.archtool.architectureModel.Component;
import fr.univ_nantes.alma.archtool.architectureModel.Connector;
import fr.univ_nantes.alma.archtool.architectureModel.Interface;
import fr.univ_nantes.alma.archtool.sourceModel.FileGlobalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.Function;
import fr.univ_nantes.alma.archtool.sourceModel.LocalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.ProgramGlobalVariable;
import fr.univ_nantes.alma.archtool.sourceModel.SourceCode;
import fr.univ_nantes.alma.archtool.sourceModel.Type;
import fr.univ_nantes.alma.archtool.sourceModel.Variable;

public class Dendogram implements Iterable<Dendogram.Node>, Cloneable
{
    /**
     * Un noeud du dendogramme.
     * 
     * <p>
     * Chaque noeud est composé d'un composant (la valeur du noeud) et de deux
     * noeud fils.
     * </p>
     */
    public class Node implements Cloneable
    {
        private Component component;

        private Node leftChild;

        private Node rightChild;

        public Node(final Component component)
        {
            this.component = component;
            this.leftChild = null;
            this.rightChild = null;
        }

        public Node(final Component component, final Node leftChild,
                final Node rightChild)
        {
            this.component = component;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        public Component getComponent()
        {
            return this.component;
        }

        public Node getLeftChild()
        {
            return this.leftChild;
        }

        public Node getRightChild()
        {
            return this.rightChild;
        }

        @Override
        public Object clone()
        {
            Node node = null;

            try
            {
                node = (Node) super.clone();

                node.component = this.component;
                
                if(this.leftChild != null && this.rightChild != null)
                {
                    node.leftChild = (Node) this.leftChild.clone();
                    node.leftChild = (Node) this.rightChild.clone();
                }
            }

            catch (CloneNotSupportedException e)
            {
                throw new RuntimeException(
                        "Dendogram.Node : clone not supported");
            }

            return node;
        }
        
        public String toString()
        {
            return "Node -> " + this.component;
        }
    }

    /**
     * Les noeuds du dendogramme.
     */
    private List<Node> nodes = new ArrayList<Node>();

    /**
     * Architecture correspondant au dendogramme.
     */
    private Architecture architecture = null; 
    
    /**
     * Initialise un nouveau dendogramme à partir d'un modèle de code source.
     * 
     * <p>
     * Les feuilles du nouveaux dendogrammes correspondent aux différentes
     * fonctions, variables globales et définitions de type de modèle de code
     * source.
     * </p>
     * 
     * @param sourceCode
     */
    public Dendogram(SourceCode sourceCode)
    {
        this.init(sourceCode);
    }

    /**
     * Retourne un composant du dendogramme.
     * 
     * @param index
     *            Index du composant à retourner
     * 
     * @return Le composant à l'index passé en paramètre ou null si l'index
     *         n'est pas valide.
     */
    public Component getComponent(final int index)
    {
        Component comp = null;

        if (index < this.nodes.size())
        {
            comp = nodes.get(index).getComponent();
        }

        else
        {
            throw new RuntimeException("Clustering.getComponent(int) : "
                    + "index passé(s) en paramètre(s) invalide(s).");
        }

        return comp;
    }

    /**
     * Génère une architecture à partir des composants du dendogramme.
     */
    public Architecture getArchitecture()
    {
        Architecture arch = null;
        
        if(this.architecture != null)
        {
            arch = this.architecture;
        }
        
        else
        {
            arch = new Architecture();
    
            for (Node node : this.nodes)
            {
                Component comp = node.getComponent();
                arch.addComponent(comp);
            }
    
            for (Component comp1 : arch.getComponents())
            {
                Set<Interface> proItfs = comp1.getProvidedInterfaces();
    
                for (Interface itf : proItfs)
                {
                    for (Component comp2 : arch.getComponents())
                    {
                        if (comp2.requiresInterface(itf))
                        {
                            Connector con = new Connector();
    
                            arch.addConnection(comp1, con, itf);
                            arch.addConnection(comp2, con, itf);
                        }
                    }
                }
            }
            
            // this.architecture = arch;
        }

        return arch;
    }

    /**
     * Retourne le nombre de composants du dendogramme.
     * 
     * <p>
     * ATTENTION : ne revoit pas le nombre total de noeuds du dendogramme mais
     * le nombre de noeuds qui n'ont pas de parent. Deux composants qui ont été
     * regroupés ne forment plus qu'un seul noeud.
     * </p>
     * 
     * @return Le nombre de noeuds du dendogramme qui n'ont pas de parent.
     */
    public int size()
    {
        return this.nodes.size();
    }

    /**
     * Génère un nouveau composant en fusionnant deux composants existants
     * 
     * <p>
     * ATTENTION : le dendogramme n'est pas modifié. Pour
     * </p>
     * 
     * @param idxChild1
     *            Index du premier noeud à regrouper
     * @param idxChild2
     *            Index du second noeud à regrouper
     * 
     * @return Le noeud composant généré
     */
    public Dendogram clusterNodes(final int idxChild1, final int idxChild2)
    {
        Dendogram dendo = (Dendogram) this.clone();
        
        if (idxChild1 < dendo.nodes.size() && idxChild2 < dendo.nodes.size())
        {
            Node childNode1 = dendo.nodes.get(idxChild1);
            Node childNode2 = dendo.nodes.get(idxChild2);
            
            Component childComp1 = childNode1.getComponent();
            Component childComp2 = childNode2.getComponent();

            Component clusterComp = new Component();

            clusterComp.addFunctions(childComp1.getFunctions());
            clusterComp.addFunctions(childComp2.getFunctions());
            clusterComp.addVariables(childComp1.getVariables());
            clusterComp.addVariables(childComp2.getVariables());
            clusterComp.addTypes(childComp1.getTypes());
            clusterComp.addTypes(childComp2.getTypes());

            dendo.updateRequiredInterfaces(clusterComp, idxChild1, idxChild2);
            dendo.updateProvidedInterfaces(clusterComp, idxChild1, idxChild2);

            Node clusterNode = new Node(clusterComp, childNode1, childNode2);

            dendo.nodes.remove(childNode1);
            dendo.nodes.remove(childNode2);
            dendo.nodes.add(clusterNode);
        }

        else
        {
            throw new RuntimeException("Clustering.clusterNodes(int, int) : "
                    + "index passé(s) en paramètre(s) invalide(s).");
        }

        return dendo;
    }

    /**
     * Crée un nouveau dendogramme dans lequel un noeud a été remplacé par ses
     * fils.
     * 
     * <p>
     * Les fils d'un noeud à l'index <em>i</em> seront insérés aux index
     * <em>i</em> et <em>i</em> + 1.
     * </p>
     * 
     * @param index
     *            L'index du noeud à séparer
     */
    public Dendogram splitNode(final int index)
    {
        Dendogram dendo = null;

        if (index < this.nodes.size())
        {
            Node node = this.nodes.get(index);

            if (node.getLeftChild() != null && node.getRightChild() != null)
            {
                Node leftChild = node.getLeftChild();
                Node rightChild = node.getRightChild();

                dendo = (Dendogram) this.clone();

                dendo.nodes.remove(index);
                dendo.nodes.add(index, leftChild);
                dendo.nodes.add((index + 1), rightChild);
            }
        }

        else
        {
            throw new RuntimeException("Clustering.splitNode(int) : "
                    + "index passé en paramètre invalide.");
        }

        return dendo;
    }

    /**
     * Crée les feuilles du dendogramme.
     * 
     * <p>
     * Méthode appelée à la création du dendogramme.
     * </p>
     * 
     * @param sourceCode
     *            Le modèle de code source sur lequel doit être construit le
     *            dendogramme.
     */
    private void init(SourceCode sourceCode)
    {
        this.extractFunctions(sourceCode);
        this.extractVariables(sourceCode);
        this.extractTypes(sourceCode);

        this.processInterfaces();
    }

    /**
     * Crée des noeuds du dendogramme à partir des fonctions d'un modèle de code
     * source.
     * 
     * @param sourceCode
     *            Un modèle de code source
     */
    private void extractFunctions(final SourceCode sourceCode)
    {
        for (final Function fct : sourceCode.getFunctions())
        {
            final Component comp = new Component();
            comp.addFunction(fct);

            final Dendogram.Node node = new Node(comp);
            this.nodes.add(node);
        }
    }

    /**
     * Crée des noeuds du dendogramme à partir des variables globales d'un
     * modèle de code source.
     * 
     * @param sourceCode
     *            Un modèle de code source
     */
    private void extractVariables(final SourceCode sourceCode)
    {
        for (final Variable var : sourceCode.getFileGlobals())
        {
            final Component comp = new Component();
            comp.addVariable(var);

            final Dendogram.Node node = new Node(comp);
            this.nodes.add(node);
        }
    }

    /**
     * Crée des noeuds du dendogramme à partir des définitions de type d'un
     * modèle de code source.
     * 
     * @param sourceCode
     *            Un modèle de code source
     */
    private void extractTypes(final SourceCode sourceCode)
    {
        for (final Type type : sourceCode.getTypes())
        {
            final Component comp = new Component();
            comp.addType(type);

            final Dendogram.Node node = new Node(comp);
            this.nodes.add(node);
        }
    }

    /**
     * Fonction appelée à la création du dendogramme pour initialiser les
     * interfaces des composants.
     */
    private void processInterfaces()
    {
        // At this time, a node is a function, a variable or a type

        for (int idxNode = 0 ; idxNode < this.nodes.size() ; ++idxNode)
        {
            final Component comp = this.nodes.get(idxNode)
                    .getComponent();

            // Function
            if (comp.getFunctions().isEmpty() == false)
            {
                this.processInterfacesFct(idxNode);
            }

            // Variable
            if (comp.getVariables().isEmpty() == false)
            {
                this.processInterfacesVar(idxNode);
            }

            // type
            if (comp.getTypes().isEmpty() == false)
            {
                this.processInterfacesType(idxNode);
            }
        }
    }

    /**
     * Fonction appelée à la création du dendogramme pour initialiser les
     * interfaces liées à la fonction d'un composant
     * 
     * <p>
     * Recherche les appels à la fonction fournie par un composant. Une
     * interface requise est créée pour chaque composant faisant appel à cette
     * fonction. Une interface fournie est créée pour le composant qui contient
     * la fonction en question.
     * </p>
     * 
     * @param idxNode
     *            Index du noeud du dendogramme dont le composant fournit la
     *            fonction en question
     * 
     * @see #processInterfaces()
     */
    private void processInterfacesFct(final int idxNode)
    {
        if (idxNode < this.nodes.size())
        {
            final Node node = this.nodes.get(idxNode);
            final Component comp = node.getComponent();

            if (comp.getFunctions().size() == 1)
            {
                final Function fct = comp.getFunctions().iterator().next();

                final Interface interfaceFct = new Interface();
                interfaceFct.addFunction(fct);

                int nbCallers = 0;

                for (int idx = 0 ; idx < this.nodes.size() ; ++idx)
                {
                    if (idx != idxNode)
                    {
                        final Component comp2 = this.nodes.get(idx)
                                .getComponent();

                        // If compo2 contains no function, it cannot call fct
                        if (comp2.getFunctions().isEmpty() == false)
                        {
                            final Function fctComp2 = comp2.getFunctions()
                                    .iterator().next();

                            if (fctComp2.calls(fct))
                            {
                                comp2.addRequiredInterface(interfaceFct);
                                nbCallers++;
                            }
                        }
                    }
                }

                if (nbCallers > 0)
                {
                    comp.addProvidedInterface(interfaceFct);
                }
            }

            else
            {
                throw new RuntimeException(
                        "Erreur interne (processInterfacesFct) - "
                                + "le noeud en paramètre ne contient pas de fonction.");
            }
        }

        else
        {
            throw new RuntimeException(
                    "Erreur interne (processInterfacesFct) - "
                            + "l'index passé en paramètre est incorrect.");
        }
    }

    /**
     * Fonction appelée à la création du dendogramme pour initialiser les
     * interfaces liées à une variable d'un composant
     * 
     * <p>
     * Recherche les accès à une variable fournie par un composant. Une
     * interface requise est créée pour chaque composant qui accède à cette
     * fonction. Une interface fournie est créée pour le composant qui contient
     * la variable en question.
     * </p>
     * 
     * @param idxNode
     *            Index du noeud du dendogramme dont le composant fournit la
     *            variable en question
     * 
     * @see #processInterfaces()
     */
    private void processInterfacesVar(final int idxNode)
    {
        final Component compo1 = this.nodes.get(idxNode).getComponent();
        final Variable var = compo1.getVariables().iterator().next();

        final Interface interfaceVar = new Interface();
        interfaceVar.addVariable(var);

        int nbAccessors = 0;

        for (int idx = 0 ; idx < this.nodes.size() ; ++idx)
        {
            final Component compo2 = this.nodes.get(idx).getComponent();

            // If compo2 contains no function, it cannot access var
            if (compo2.getFunctions().isEmpty() == false)
            {
                final Function fct2 = compo2.getFunctions().iterator().next();

                final Map<FileGlobalVariable, Integer> fileGlobalsFct = fct2
                        .getFileGlobals();
                final Map<ProgramGlobalVariable, Integer> progGlobalsFct = fct2
                        .getProgramGlobals();

                if (fileGlobalsFct.containsKey(var)
                        || progGlobalsFct.containsKey(var))
                {
                    compo2.addRequiredInterface(interfaceVar);
                    nbAccessors++;
                }
            }
        }

        if (nbAccessors > 0)
        {
            compo1.addProvidedInterface(interfaceVar);
        }
    }

    /**
     * Fonction appelée à la création du dendogramme pour initialiser les
     * interfaces liées à un type d'un composant
     * 
     * <p>
     * Recherche les utilisation d'un type fourni par un composant. Une
     * interface requise est créée pour chaque composant qui utilise ce type
     * (contient une fonction qui utilise une variable de ce type ou une
     * variable globale de ce type) Une interface fournie est créée pour le
     * composant qui contient le type en question.
     * </p>
     * 
     * @param idxNode
     *            Index du noeud du dendogramme dont le composant fournit le
     *            type en question
     * 
     * @see #processInterfaces()
     */

    private void processInterfacesType(final int idxNode)
    {
        final Component compo1 = this.nodes.get(idxNode).getComponent();
        final Type t = compo1.getTypes().iterator().next();

        final Interface interfaceType = new Interface();
        interfaceType.addType(t);

        int nbUsers = 0;

        for (int idx = 0 ; idx < this.nodes.size() ; ++idx)
        {
            if (idx != idxNode)
            {
                final Component compo2 = this.nodes.get(idx)
                        .getComponent();

                // Compo2 contains a function that can use a variable of type t
                if (compo2.getFunctions().isEmpty() == false)
                {
                    final Function fct2 = compo2.getFunctions().iterator()
                            .next();

                    // Types of arguments

                    final Set<LocalVariable> args = fct2.getArguments();
                    final Iterator<LocalVariable> itArgs = args.iterator();
                    boolean found = false;

                    while (itArgs.hasNext() && (found == false))
                    {
                        final LocalVariable arg = itArgs.next();

                        if (arg.getType().equals(t))
                        {
                            found = true;
                            compo2.addRequiredInterface(interfaceType);
                            nbUsers++;
                        }
                    }

                    // Types used in the body

                    final Map<Type, Integer> typesFct = fct2.getUsedTypes();

                    if (typesFct.containsKey(t))
                    {
                        compo2.addRequiredInterface(interfaceType);
                        nbUsers++;
                    }
                }

                // Compo2 contains a variable of type t
                else if (compo2.getVariables().isEmpty() == false)
                {
                    final Variable var = compo2.getVariables().iterator()
                            .next();

                    if (var.getType().equals(t))
                    {
                        compo2.addRequiredInterface(interfaceType);
                        nbUsers++;
                    }
                }
            }
        }

        if (nbUsers > 0)
        {
            compo1.addProvidedInterface(interfaceType);
        }
    }

    /**
     * Calcule les interfaces requises d'un nouveau cluster.
     * 
     * <p>
     * Une interface requise d'un des composants fils ne doit pas être
     * transférée au cluster si l'autre composant fils la fournit.
     * </p>
     * 
     * @param cluster
     *            Le composant cluster
     * @param idxChild1
     *            L'index du premier noeud fils
     * @param idxChild2
     *            L'index du second noeud fils
     */
    private void updateRequiredInterfaces(final Component cluster,
            final int idxChild1, final int idxChild2)
    {
        final Component child1 = this.nodes.get(idxChild1)
                .getComponent();
        final Component child2 = this.nodes.get(idxChild2)
                .getComponent();
        
        for (final Interface reqI : child1.getRequiredInterfaces())
        {
            if (child2.providesInterface(reqI) == false)
            {
                cluster.addRequiredInterface(reqI);
            }
        }

        for (final Interface reqI : child2.getRequiredInterfaces())
        {
            if (child1.providesInterface(reqI) == false)
            {
                cluster.addRequiredInterface(reqI);
            }
        }
    }

    /**
     * Calcule les interfaces fournies d'un nouveau cluster.
     * 
     * <p>
     * Une interface fournie d'un des composants fils ne doit pas être
     * transférée au cluster si seul l'autre composant fils la requérais.
     * </p>
     * 
     * @param cluster
     *            L'index du noeud cluster
     * @param idxChild1
     *            L'index du premier noeud fils
     * @param idxChild2
     *            L'index du second noeud fils
     */
    private void updateProvidedInterfaces(final Component cluster,
            final int idxChild1, final int idxChild2)
    {
        final Component child1 = this.nodes.get(idxChild1)
                .getComponent();
        final Component child2 = this.nodes.get(idxChild2)
                .getComponent();

        for (final Interface proI : child1.getProvidedInterfaces())
        {
            final Iterator<Dendogram.Node> itNodes = this.nodes.iterator();
            boolean required = false;

            while (itNodes.hasNext() && (required == false))
            {
                final Component compo = itNodes.next().getComponent();

                if ((compo != child1) && (compo != child2))
                {
                    if (compo.requiresInterface(proI))
                    {
                        cluster.addProvidedInterface(proI);
                        required = true;
                    }
                }
            }
        }

        for (final Interface proI : child2.getProvidedInterfaces())
        {
            final Iterator<Dendogram.Node> itNodes = this.nodes.iterator();
            boolean required = false;

            while (itNodes.hasNext() && (required == false))
            {
                final Component compo = itNodes.next().getComponent();

                if ((compo != child1) && (compo != child2))
                {
                    if (compo.requiresInterface(proI))
                    {
                        cluster.addProvidedInterface(proI);
                        required = true;
                    }
                }
            }
        }
    }

    @Override
    public Iterator<Node> iterator()
    {
        return nodes.iterator();
    }

    @Override
    public Object clone()
    {
        Dendogram dendogram = null;

        try
        {
            dendogram = (Dendogram) super.clone();

            dendogram.nodes = new ArrayList<Dendogram.Node>();

            for (Node node : this.nodes)
            {
                dendogram.nodes.add((Node) node.clone());
            }
            
            dendogram.architecture = null;
        }

        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException("Dendogram : clone not supported");
        }

        return dendogram;
    }
}
