package demineur;



public class Cellule 
{
    private boolean mine;

    // Seul le contenu de la cellule est visible pour le joueur.
    private String content;

    // Nombre de mines voisines
    private int mineVoisin;

    
    //----------------------------------------------------------//

    public Cellule()
    {
        mine = false;
        content = "";
        mineVoisin = 0;
    }


    
    //-------------GETTERS AND SETTERS----------------------------//
    public boolean getMine()
    {
        return mine;
    }

    public void setMine(boolean mine)
    {
        this.mine = mine;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getMineVoisin()
    {
        return mineVoisin;
    }

    public void setMineVoisin(int mineVoisin)
    {
        this.mineVoisin = mineVoisin;
    }

    //-------------------------------------------------------------//
}
