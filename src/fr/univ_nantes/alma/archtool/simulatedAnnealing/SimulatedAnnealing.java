package fr.univ_nantes.alma.archtool.simulatedAnnealing;

import java.util.Random;

import fr.univ_nantes.alma.archtool.architectureModel.Architecture;
import fr.univ_nantes.alma.archtool.clustering.Clustering;
import fr.univ_nantes.alma.archtool.objective.ObjectiveFunction;
import fr.univ_nantes.alma.archtool.sourceModel.SourceCode;

/**
 * Métaheuristique de recuit simulé.
 */
public class SimulatedAnnealing
{
    /**
     * Température intiale
     */
    private static double INITIAL_TEMPERATURE = 500.0;

    /**
     * Facteur de refroidissement
     */
    private static double COOLING_RATE = 0.95;

    /**
     * Taux d'amélioration minimum entre 2 plateaux.
     * 
     * <p>
     * Lorsqu'à la fin d'un plateau, l'écart entre la meilleure solution du
     * plateau précédent et la meilleure solution actuelle est inférieur à cette
     * limite, une chance est utilisée.
     * </p>
     */
    private static double DECREASE_LIMIT = 0.01;

    /**
     * Nombre initial de chances.
     * <p>
     * Lorsque l'amélioration de bestScore obtenue à la fin d'un plateau est
     * inférieure à DECREASE_LIMIT, une chance est consommée. Le nombre de
     * chances est remis à son maximum lorsque l'amélioration redevient
     * supérieure à la limite. L'algorithme s'arrête quand toutes les chances
     * ont été consommées.
     * </p>
     * 
     * @see #DECREASE_LIMIT
     */
    private static int NB_MAX_CHANCES = 3;

    /**
     * Longueur d'un plateau.
     * 
     * <p>
     * Définit le nombre de déplacements à effectuer avant de diminuer la
     * température.
     * </p>
     */
    private static int PLATEAU_LENGTH = 100;

    /**
     * Architecture courante
     */
    private Architecture currentSolution = null;

    /**
     * Valeur de la fonction objectif pour l'architecture courante
     */
    private double currentScore = 0.0;

    /**
     * Meilleure architecture connue
     */
    private Architecture bestSolution = null;

    /**
     * Valeur de la fonction objectif pour la meilleure architecture connue
     */
    private double bestScore = 0.0;

    /**
     * Température courante
     */
    private double temperature = INITIAL_TEMPERATURE;

    /**
     * Nombre de chances restantes.
     * 
     * @see #NB_MAX_CHANCES
     */
    private int chances = NB_MAX_CHANCES;

    /**
     * Fonction objectif.
     */
    private ObjectiveFunction objFunction = new ObjectiveFunction();

    /**
     * Algorithme de clustering pour générer la solution initiale.
     */
    private Clustering clusteringAlgo = new Clustering();

    public Architecture process(SourceCode sourceCode)
    {
        this.currentSolution = this.clusteringAlgo.process(sourceCode);
        currentScore = this.objFunction.evaluate(currentSolution);

        boolean stop = false;
        
        Architecture neighbour = null;

        Random randomGen = null;

        while (stop == false)
        {
            randomGen = new Random();
            double previousBestScore = currentScore;

            int nbMoves = 0;
            
            while (nbMoves < PLATEAU_LENGTH)
            {
                neighbour = move(currentSolution);

                double neighbourScore = objFunction.evaluate(neighbour);

                double proba = acceptanceFct(neighbourScore);
                double rand = randomGen.nextDouble();

                if (rand < proba)
                {
                    this.currentSolution = neighbour;
                    this.currentScore = neighbourScore;

                    ++nbMoves;

                    if (currentScore > bestScore)
                    {
                        this.bestSolution = neighbour;
                        this.bestScore = neighbourScore;
                    }
                }
            }

            this.temperature *= SimulatedAnnealing.COOLING_RATE;

            if (stopCriterion(previousBestScore, this.bestScore))
            {
                stop = true;
            }
        }

        return bestSolution;
    }

    private Architecture move(Architecture currentSolution)
    {
        Architecture result = null;

        return result;
    }

    /**
     * Calcule la probabilité d'accepter une solution.
     */
    private double acceptanceFct(Double neighbourScore)
    {
        double proba = 0.0;

        if (neighbourScore > this.currentScore)
        {
            proba = 1.0;
        }
        else
        {
            proba = Math.exp(
                    (neighbourScore - currentScore) / this.temperature);
        }

        return proba;
    }

    private boolean stopCriterion(Double previousBestScore, Double newBestScore)
    {
        boolean stop = false;

        double decrease = 1 - (previousBestScore / newBestScore);

        if (decrease < DECREASE_LIMIT)
        {
            if (this.chances > 0)
            {
                --this.chances;
            }
            else
            {
                stop = true;
            }
        }

        return stop;
    }
}
