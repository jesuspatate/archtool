package fr.univ_nantes.alma.archtool.architectureModel;

import java.util.HashSet;
import java.util.Set;

public class Configuration
{
    private Set<Connection> connections;
    
    public boolean addConnection(Component comp, Connector con, Interface itf)
    {
        boolean done = false;
        
        if(comp.providesInterface(itf))
        {
            done = this.connections.add(new Connection(comp, con, itf));
        }
        
        return done;
    }

    /**
     * Retourne l'ensemble des connexions auxquelles participe un composant.
     * 
     * @param comp
     *            Le composant recherché
     */
    public Set<Connection> getConnections(Component comp)
    {
        Set<Connection> connections = new HashSet<Connection>();

        for (Connection c : this.connections)
        {
            if (c.isParticipant(comp))
            {
                connections.add(c);
            }
        }

        return connections;
    }
}
