package fr.univ_nantes.alma.archtool.architectureModel;

import java.util.HashSet;
import java.util.Set;

import fr.univ_nantes.alma.archtool.coa.COA;
import fr.univ_nantes.alma.archtool.sourceModel.ComplexType;
import fr.univ_nantes.alma.archtool.sourceModel.Function;
import fr.univ_nantes.alma.archtool.sourceModel.GlobalVariable;

public class Connector
{
    private final Set<Facade> facades = new HashSet<Facade>();
    
    private Set<Function> cachedFunctions = null;

    private Set<GlobalVariable> cachedGlobalVariables = null;
    
    private Set<ComplexType> cachedComplexType = null;
    
    private COA coa = null;

    /**
     * On crèe ou on coupe la connection avec le coa.
     */
    public void setCOA(COA coa)
    {
        this.coa = coa;
        
        if(this.coa != null)
        {
            this.coa.addConnector(this);
                        
            for(Facade facade : this.facades)
            {
                facade.setCOA(this.coa);
            }
            
            if(this.cachedFunctions != null)
            {
                this.coa.addFunctions(this.cachedFunctions, this);
                this.cachedFunctions = null;
            }
            
            if(this.cachedComplexType != null)
            {
                this.coa.addComplexTypes(this.cachedComplexType, this);
                this.cachedComplexType = null;
            }
            
            if(this.cachedGlobalVariables != null)
            {
                this.coa.addGlobalVariables(this.cachedGlobalVariables, this);
                this.cachedGlobalVariables = null;
            }
        }
    }

    public Set<Facade> getFacades()
    {
        return new HashSet<Facade>(this.facades);
    }
    
    /**
     * On informe le coa uniquement si on le connait.
     */
    public void addFacade(Facade fcd)
    {
        if(this.coa != null)
        {
            fcd.setCOA(this.coa);
            this.coa.addFunctions(fcd.getFunctions(), this);
            this.coa.addComplexTypes(fcd.getComplexTypes(), this);
            this.coa.addGlobalVariables(fcd.getGlobalVariables(), this);
        }
        else
        {
            this.addFunctions(fcd.getFunctions());
            this.addComplexTypes(fcd.getComplexTypes());
            this.addGlobalVariables(fcd.getGlobalVariables());
        }
        
        this.facades.add(fcd);
    }

    /**
     * On informe le coa uniquement si on le connait.
     */
    public void removeFacade(Facade fcd)
    {
        this.facades.remove(fcd);
        
        if(this.coa != null)
        {
            this.coa.removeFacade(fcd);
        }
    }

    /**
     * On prend les infos du coa si on le connait sinon on fait avec les
     *  informations mises en cache.
     */
    public Set<Function> getFunctions()
    {
        Set<Function> functions = null;
        
        if(this.coa == null)
        {
            functions = this.cachedFunctions == null ?
                    new HashSet<Function>() : 
                    new HashSet<Function>(this.cachedFunctions);
        }
        else
        {
            functions = this.coa.getConnectorFunctions(this);
        }
                
        return functions;
    }

    /**
     * On prend les infos du coa si on le connait sinon on fait avec les
     *  informations mises en cache.
     */
    public Set<GlobalVariable> getGlobalVariables()
    {
        Set<GlobalVariable> globalVariables = null;
        
        if(this.coa == null)
        {
            globalVariables = this.cachedGlobalVariables == null ?
                    new HashSet<GlobalVariable>() : 
                    new HashSet<GlobalVariable>(this.cachedGlobalVariables);
        }
        else
        {
            globalVariables = this.coa.getConnectorGlobalVariables(this);
        }
                
        return globalVariables;
    }

    /**
     * On prend les infos du coa si on le connait sinon on fait avec les
     *  informations mises en cache.
     */
    public Set<ComplexType> getComplexTypes()
    {
        Set<ComplexType> complexTypes = null;
        
        if(this.coa == null)
        {
            complexTypes = this.cachedComplexType == null ?
                    new HashSet<ComplexType>() : 
                    new HashSet<ComplexType>(this.cachedComplexType);
        }
        else
        {
            complexTypes = this.coa.getConnectorComplexTypes(this);
        }
                
        return complexTypes;
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public boolean addFunction(Function fct)
    {
        boolean done;
        
        if(this.coa == null)
        {
            if(this.cachedFunctions == null)
            {
                this.cachedFunctions = new HashSet<Function>();
            }
            
            done = this.cachedFunctions.add(fct);
        }
        else
        {
            done = this.coa.addFunction(fct, this);
        }
        
        return done;
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public void addFunctions(Set<Function> fcts)
    {
        if(this.coa == null)
        {
            if(this.cachedFunctions == null)
            {
                this.cachedFunctions = new HashSet<Function>();
            }
            
            this.cachedFunctions.addAll(fcts);
        }
        else
        {
            this.coa.addFunctions(fcts, this);
        }
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public boolean addGlobalVariable(GlobalVariable var)
    {
        boolean done;
        
        if(this.coa == null)
        {
            if(this.cachedGlobalVariables == null)
            {
                this.cachedGlobalVariables = new HashSet<GlobalVariable>();
            }
            
            done = this.cachedGlobalVariables.add(var);
        }
        else
        {
            done = this.coa.addGlobalVariable(var, this);
        }
        
        return done;
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public void addGlobalVariables(Set<GlobalVariable> vars)
    {
        if(this.coa == null)
        {
            if(this.cachedGlobalVariables == null)
            {
                this.cachedGlobalVariables = new HashSet<GlobalVariable>();
            }
            
            this.cachedGlobalVariables.addAll(vars);
        }
        else
        {
            this.coa.addGlobalVariables(vars, this);
        }
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public boolean addComplexType(ComplexType t)
    {
        boolean done;
        
        if(this.coa == null)
        {
            if(this.cachedComplexType == null)
            {
                this.cachedComplexType = new HashSet<ComplexType>();
            }
            
            done = this.cachedComplexType.add(t);
        }
        else
        {
            done = this.coa.addComplexType(t, this);
        }
        
        return done;
    }
    
    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public void addComplexTypes(Set<ComplexType> types)
    {
        if(this.coa == null)
        {
            if(this.cachedComplexType == null)
            {
                this.cachedComplexType = new HashSet<ComplexType>();
            }
            
            this.cachedComplexType.addAll(types);
        }
        else
        {
            this.coa.addComplexTypes(types, this);
        }
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public boolean removeFunction(Function fct)
    {
        boolean done = false;
        
        if(this.coa == null && this.cachedFunctions != null)
        {            
            done = this.cachedFunctions.remove(fct);
        }
        else if(this.coa != null)
        {
            done = this.coa.removeFunction(fct, this);
        }
        
        return done;
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public boolean removeGlobalVariable(GlobalVariable var)
    {
        boolean done = false;
        
        if(this.coa == null && this.cachedGlobalVariables != null)
        {            
            done = this.cachedGlobalVariables.remove(var);
        }
        else if(this.coa != null)
        {
            done = this.coa.removeGlobalVariable(var, this);
        }
        
        return done;
    }

    /**
     * On informe le coa uniquement si on le connait sinon on met en cache.
     */
    public boolean removeComplexType(ComplexType t)
    {
        boolean done = false;
        
        if(this.coa == null && this.cachedComplexType != null)
        {            
            done = this.cachedComplexType.remove(t);
        }
        else if(this.coa != null)
        {
            done = this.coa.removeComplexType(t, this);
        }
        
        return done;
    }

    public void clearFacades()
    {
        for (Facade fcd : this.facades)
        {
            this.removeFacade(fcd);
        }
    }
}
