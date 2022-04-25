package demineur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.util.Pair;



public class Tableau 
{
    private int nbreDeMine;	
    private Cellule cells[][];

    private int lignes;
    private int colonnes;

        
    //---------------------------------------------//
    
    public Tableau(int nbreDeMine, int r, int c)
    {
        this.lignes = r;
        this.colonnes = c;
        this.nbreDeMine = nbreDeMine;

        cells = new Cellule[lignes][colonnes];

        // Étape 1 : Créez d'abord un tableau avec des cellules vides.
        creerCelVides();         

        // Étape 2 : Ensuite, les mines sont placées aléatoirement dans les cellules.
        setMines();

        // Étape 3 : Définir ensuite le nombre de mines voisins
        // pour chaque cellule.
        setNbreMineVoisin();
    }


    //------------------------------------------------------------------//
    //ETAPE 1//
    public void creerCelVides()
    {
        for (int x = 0; x < colonnes; x++)
        {
            for (int y = 0; y < lignes; y++)
            {
                cells[x][y] = new Cellule();
            }
        }
    }

    //------------------------------------------------------------------//
    //ETAPE 2//
    public void setMines()
    {
        int x,y;
        boolean uneMine;
        int minePresent = 0;                

        while (minePresent != nbreDeMine)
        {
            // Générer une coordonnée x aléatoire (entre 0 et colonnes)
            x = (int)Math.floor(Math.random() * colonnes);

            // Générer une coordonnée y aléatoire (entre 0 et lignes)
            y = (int)Math.floor(Math.random() * lignes);

            uneMine = cells[x][y].getMine();

            if(!uneMine)
            {		
                cells[x][y].setMine(true);
                minePresent++;	
            }			
        }
    }
    //------------------------------------------------------------------//

    //------------------------------------------------------------------//
    //ETAPE 3//
    public void setNbreMineVoisin()
    {	
        for(int x = 0 ; x < colonnes ; x++) 
        {
            for(int y = 0 ; y < lignes ; y++) 
            {
                cells[x][y].setMineVoisin(calculateNeighbours(x,y));                        
            }
        }
    }
    //------------------------------------------------------------------//	




    //------------------------------------------------//        

    // Calcule le nombre de mines environnantes ("voisins")
    public int calculateNeighbours(int xCo, int yCo)
    {
        int neighbours = 0;

        
        for(int x=makeValidCoordinateX(xCo - 1); x<=makeValidCoordinateX(xCo + 1); x++) 
        {
            
            for(int y=makeValidCoordinateY(yCo - 1); y<=makeValidCoordinateY(yCo + 1); y++) 
            {
                
                if(x != xCo || y != yCo)
                    if(cells[x][y].getMine())  
                        neighbours++;
            }
        }

        return neighbours;
    }

    //------------------------------------------------------------------//	

    
    public int makeValidCoordinateX(int i)
    {
        if (i < 0)
            i = 0;
        else if (i > colonnes-1)
            i = colonnes-1;

        return i;
    }	
    
    
    public int makeValidCoordinateY(int i)
    {
        if (i < 0)
            i = 0;
        else if (i > lignes-1)
            i = lignes-1;

        return i;
    }	
    
    //------------------------------------------------------------------//	        

    //-------------DATA BASE------------------------//
    
    // pour vérifier s'il y a un jeu sauvegarder ou non
    public boolean checkSave()
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        boolean saveExists = false;

        try {
            String dbURL = Partie.dbPath; 
            
            connection = DriverManager.getConnection(dbURL); 
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM GAME_STATE");
            
            while(resultSet.next()) 
            {
                saveExists = true;
            }
            
            
            resultSet.close();
            statement.close();
                       
            
            connection.close();            
            
            return saveExists;
        }
        catch(SQLException sqlex)
        {
            sqlex.printStackTrace();
            return false;
        }        
    }
    
    //--------------CHARGER UN JEU SAUVEGARDÉ-----------------//
    
    
    public Pair loadSaveGame()
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String dbURL = Partie.dbPath; 
            
            connection = DriverManager.getConnection(dbURL); 
            
            //---------------------------------//
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM CELL");

            for(int x = 0 ; x < colonnes ; x++) 
            {
                for(int y = 0 ; y < lignes ; y++) 
                {                                        
                    resultSet.next();
                    
                    cells[x][y].setContent(resultSet.getString("CONTENT"));
                    cells[x][y].setMine(resultSet.getBoolean("MINE"));
                    cells[x][y].setMineVoisin(resultSet.getInt("SURROUNDING_MINES"));                    
                }
            }
            
            statement.close();
            resultSet.close();
            //----------------------------------------------------//

            //--------------------------------//
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM GAME_STATE");

            resultSet.next();
                        
            Pair p = new Pair(resultSet.getInt("TIMER"),resultSet.getInt("MINES"));
            
            
            deleteSavedGame();
            
            
            resultSet.close();
            statement.close();
                       
           
            connection.close();

            return p;
        }
        catch(SQLException sqlex)
        {
            sqlex.printStackTrace();
            return null;
        }                
    }
    
    
    //------------------------------------------------------------------------//
    public void deleteSavedGame()
    {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            String dbURL = Partie.dbPath; 
            
            connection = DriverManager.getConnection(dbURL); 

            
            //----------VIDER GAME_STATE TABLE------//
            String template = "DELETE FROM GAME_STATE"; 
            statement = connection.prepareStatement(template);
            statement.executeUpdate();
            
            //----------VIDER CELL TABLE------//
            template = "DELETE FROM CELL"; 
            statement = connection.prepareStatement(template);
            statement.executeUpdate();
            
            statement.close();
            
            
            connection.close();            
        }
        catch(SQLException sqlex)
        {
            sqlex.printStackTrace();
        }                
    }
    
           
    //--------------SAVE GAME IN DATABASE-----------//
    public void saveGame(int timer, int mines)
    {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            String dbURL = Partie.dbPath; 
            
            connection = DriverManager.getConnection(dbURL); 

            
            //--------------INSERT DATA INTO CELL TABLE-----------//            
            String template = "INSERT INTO CELL (CONTENT, MINE, SURROUNDING_MINES) values (?,?,?)";
            statement = connection.prepareStatement(template);

            for(int x = 0 ; x < colonnes ; x++) 
            {
                for(int y = 0 ; y < lignes ; y++) 
                {
                    statement.setString(1, cells[x][y].getContent());
                    statement.setBoolean(2, cells[x][y].getMine());
                    statement.setInt(3, (int)cells[x][y].getMineVoisin());                    

                    statement.executeUpdate();
                }
            }
            //--------------------------------------------------//

            
            //--------------------SAVE GAME STATE----------------------//
            template = "INSERT INTO GAME_STATE (TIMER,MINES) values (?,?)";
            statement = connection.prepareStatement(template);
            
            statement.setInt(1, timer);
            statement.setInt(2, mines);

            statement.executeUpdate();
            
            //---------------------------------------------------------//
            
            statement.close();
            
            
            connection.close();            
        }
        catch(SQLException sqlex)
        {
            sqlex.printStackTrace();
        }
        
    }
    
    
    
    //--------------------------------------------//
    //---------GETTERS AND SETTERS-------------//
    public void setNumberOfMines(int nbreDeMine)
    {
        this.nbreDeMine = nbreDeMine;
    }

    public int getNumberOfMines()
    {
        return nbreDeMine;
    }

    public Cellule[][] getCells()
    {
        return cells;
    }
    
    public int getRows()
    {
        return lignes;
    }
    
    public int getCols()
    {
        return colonnes;
    }
    //-----------------------------------------//

    public void resetBoard()
    {
        for(int x = 0 ; x < colonnes ; x++) 
        {
            for(int y = 0 ; y < lignes ; y++) 
            {
                cells[x][y].setContent("");                        
            }
        }
    }
    
}
