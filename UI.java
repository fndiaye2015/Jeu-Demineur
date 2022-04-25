package demineur;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


public class UI extends JFrame
{
    // Les boutons
    private JButton[][] buttons;
    
    // Nombre de boutons dans la grille
    private int ligne;
    private int colonne;
    
    // Labels 
    private JLabel minesLabel;
    private int mines;
    
    private JLabel timePassedLabel;    
    private Thread timer;
    private int tpsEcoule;
    private boolean stopTimer;
    
    // Paramètres du cadre
    private final String FRAME_TITLE = "Jeu Démineur ~ Projet Cours de JAVA";
    
    private int FRAME_WIDTH = 520;
    private int FRAME_HEIGHT = 550;
    private int FRAME_LOC_X = 430;
    private int FRAME_LOC_Y = 50;

    // Icons
    private Icon mineRouge;
    private Icon mine;
    private Icon drapeau;
    private Icon carreau;
    
    
    // Barre de menu et éléments
    
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem newGame;
    private JMenuItem statistics;
    private JMenuItem exit;

    
    
    //---------------------------------------------------------------//
    public UI(int r, int c, int m)
    {                
        this.ligne = r;
        this.colonne = c;
        
        buttons = new JButton [ligne][colonne];

        
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setTitle(FRAME_TITLE);
        setLocation(FRAME_LOC_X, FRAME_LOC_Y);
               
      

        JPanel gameBoard;        
        JPanel tmPanel;        
        JPanel scorePanel;
        
        //----------------GAME BOARD---------------------//
        
        gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(ligne,colonne,0,0));
        
        for( int y=0 ; y<ligne ; y++ ) 
        {
            for( int x=0 ; x<colonne ; x++ ) 
            {
                
                buttons[x][y] = new JButton("");

                
                buttons[x][y].setName(Integer.toString(x) + "," + Integer.toString(y));
                buttons[x][y].setFont(new Font("Serif", Font.BOLD, 24));
                
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.black, 1, true));

                
                gameBoard.add(buttons[x][y]);
            }
        }
        //-----------------------------------------------//
                
                
        //-------------TEMPS et MINE------------------------//
        
        JPanel timePassedPanel = new JPanel();
        timePassedPanel.setLayout(new BorderLayout(10,0));
        
        // Initialiser le temps passé.
        this.timePassedLabel = new JLabel ("  0  " , SwingConstants.CENTER);
        timePassedLabel.setFont(new Font("Serif", Font.BOLD, 20));
                
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        
        timePassedLabel.setBorder(loweredetched);
        timePassedLabel.setBackground(new Color(110,110,255));
        timePassedLabel.setForeground(Color.white);
        timePassedLabel.setOpaque(true);
        
        JLabel iT = new JLabel("",SwingConstants.CENTER);
        iT.setIcon(new ImageIcon(getClass().getResource("/resources/clock.png"))); 

        timePassedPanel.add(iT, BorderLayout.WEST);
        timePassedPanel.add(timePassedLabel, BorderLayout.CENTER);
        timePassedPanel.setOpaque(false);
        
        this.tpsEcoule = 0;
        this.stopTimer = true;

        
        JPanel minesPanel = new JPanel();
        minesPanel.setLayout(new BorderLayout(10,0));
        
        
        // Initialiser les mines.
        this.minesLabel = new JLabel ("  0  " , SwingConstants.CENTER);
        minesLabel.setFont(new Font("Serif", Font.BOLD, 20));
        minesLabel.setBorder(loweredetched);
        minesLabel.setBackground(new Color(110,110,255));
        minesLabel.setForeground(Color.white);
        
        minesLabel.setOpaque(true);
        setMines(m);
        
        JLabel mT = new JLabel("", SwingConstants.CENTER);
        mT.setIcon(new ImageIcon(getClass().getResource("/resources/mine.png")));

        minesPanel.add(minesLabel, BorderLayout.WEST);
        minesPanel.add(mT, BorderLayout.CENTER);
        minesPanel.setOpaque(false);
        
        // Build the "tmPanel".
        tmPanel = new JPanel();
        tmPanel.setLayout(new BorderLayout(0,20));
        
        tmPanel.add(timePassedPanel, BorderLayout.WEST);
        tmPanel.add(minesPanel, BorderLayout.EAST);
        tmPanel.setOpaque(false);
        
        //--------------------------------------------//
                        
        
        //------------------Menu--------------------------//
        menuBar = new JMenuBar();
        
        gameMenu = new JMenu("Partie");
         
        newGame = new JMenuItem("   Nouvelle Partie");
        statistics = new JMenuItem("   Statistiques");
        exit = new JMenuItem("   Quitter");

        newGame.setName("Nouvelle Partie");
        statistics.setName("Statistiques");
        exit.setName("Quitter");

        gameMenu.add(newGame);
        gameMenu.add(statistics);
        gameMenu.add(exit);
        
        menuBar.add(gameMenu);   

        //----------------------------------------------------//
               
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout(0,10));
        p.add(gameBoard, BorderLayout.CENTER);
        p.add(tmPanel, BorderLayout.SOUTH);
    
 
        p.setBorder(BorderFactory.createEmptyBorder(60, 60, 14, 60));        
        p.setOpaque(false);
      
        
        setLayout(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/resources/2.jpg")));
        
        add(background);        
        
        background.setLayout(new BorderLayout(0,0));
        
        background.add(menuBar,BorderLayout.NORTH);
        background.add(p, BorderLayout.CENTER);        
        
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/mine.png")));
               
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
	
    //-----------------------------------------------------------------//

    //-----------------------Related to Timer------------------------//
    
    // Starts the timer
    public void startTimer()
    {        
        stopTimer = false;
        
        timer = new Thread() {
                @Override
                public void run()
                {
                    while(!stopTimer)
                    {
                        tpsEcoule++;

                        // Update the time passed label.
                        timePassedLabel.setText("  " + tpsEcoule + "  ");

                        // Wait 1 second.
                        try{
                            sleep(1000); 
                        }
                        catch(InterruptedException ex){}
                    }
                }
        };                

       timer.start();
    }

    
    public void interruptTimer()
    {
        stopTimer = true;
                
        try 
        {
            if (timer!= null)
                timer.join();
        } 
        catch (InterruptedException ex) 
        {

        }        
    }
    
    public void resetTimer()
    {
        tpsEcoule = 0;
        timePassedLabel.setText("  " + tpsEcoule + "  ");        
    }

    public void setTimePassed(int t)
    {
        tpsEcoule = t;
        timePassedLabel.setText("  " + tpsEcoule + "  ");                
    }
    
    //-----------------------------------------------------------//
    
    
    public void initGame()
    {
        hideAll();
        enableAll();
    }
    
    //------------------HELPER FUNCTIONS-----------------------//

    //Makes buttons clickable
    public void enableAll()
    {
        for( int x=0 ; x<colonne ; x++ ) 
        {
            for( int y=0 ; y<ligne ; y++ ) 
            {
                buttons[x][y].setEnabled(true);
            }
        }
    }

    //Makes buttons non-clickable
    public void disableAll()
    {
        for( int x=0 ; x<colonne ; x++ ) 
        {
            for( int y=0 ; y<ligne ; y++ ) 
            {
                buttons[x][y].setEnabled(false);
            }
        }
    }


    //Resets the content of all buttons
    public void hideAll()
    {
        for( int x=0 ; x<colonne ; x++ ) 
        {
            for( int y=0 ; y<ligne ; y++ ) 
            {
                buttons[x][y].setText("");                
                buttons[x][y].setBackground(new Color(0,103,200));
                buttons[x][y].setIcon(carreau);                
            }
        }
    }

    
    //---------------Définir les listeners--------------------------//
    
    public void setButtonListeners(Partie game)
    {
        addWindowListener(game);
    
        // Définir les listeners pour tous les boutons de la grille dans le gameBoard
        for( int x=0 ; x<colonne ; x++ ) 
        {
            for( int y=0 ; y<ligne ; y++ ) 
            {
                buttons[x][y].addMouseListener(game);
            }
        }
        
        // Définir les listeners pour les éléments de menu dans la barre de menu
       newGame.addActionListener(game);
       statistics.addActionListener(game);
       exit.addActionListener(game);

       newGame.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
       exit.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
       statistics.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));       
    }
    
    
    //-----------------GETTERS AND SETTERS--------------------//
    
    public JButton[][] getButtons()
    {
        return buttons;
    }
    
    public int getTempsEcoule()
    {
        return tpsEcoule;
    }    


    //----------------------Définir la vue------------------------------//
    
    public static void setLook(String look)
    {
        try {

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (look.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        } catch (Exception ex) { }            
    }

    //-------------------------------------------------------------//
    
    public void setMines(int m)
    {
        mines = m;
        minesLabel.setText("  " + Integer.toString(m) + "  ");
    }
    
    public void incMines()
    {
        mines++;
        setMines(mines);
    }
    
    public void decMines()
    {
        mines--;
        setMines(mines);
    }
    
    public int getMines()
    {
        return mines;
    }
            
    //--------------------Les Icons----------------------------//
    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) 
    {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }    
    
    public void setIcons()
    {
       //---------------------Définir les Icons-----------------------------//

        int bOffset = buttons[0][1].getInsets().left;
        int bWidth = buttons[0][1].getWidth();
        int bHeight = buttons[0][1].getHeight();
        
        ImageIcon d;
        
        d = new ImageIcon(getClass().getResource("/resources/redmine.png"));                
        mineRouge =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        

        d = new ImageIcon(getClass().getResource("/resources/mine.png"));                
        mine =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        
        
        d = new ImageIcon(getClass().getResource("/resources/flag.png"));                
        drapeau =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        
        
        d = new ImageIcon(getClass().getResource("/resources/tile.png"));                
        carreau =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        
                
        //-------------------------------------------------------//
        
    }
    
    public Icon getIconMine()
    {
        return mine;
    }

    public Icon getIconRedMine()
    {
        return mineRouge;
    }
    
    public Icon getIconFlag()
    {
        return drapeau;
    }
    
    public Icon getIconTile()
    {
        return carreau;       
    }        
    
    
    //---------------------------------------------------------------------//
    public void setCouleurText(JButton b)
    {
        if (b.getText().equals("1"))
            b.setForeground(Color.blue);
        else if (b.getText().equals("2"))
            b.setForeground(new Color(76,153,0));
        else if (b.getText().equals("3"))
            b.setForeground(Color.red);
        else if (b.getText().equals("4"))
            b.setForeground(new Color(153,0,0));
        else if (b.getText().equals("5"))
            b.setForeground(new Color(153,0,153));
        else if (b.getText().equals("6"))
            b.setForeground(new Color(96,96,96));
        else if (b.getText().equals("7"))
            b.setForeground(new Color(0,0,102));
        else if (b.getText().equals("8"))
            b.setForeground(new Color(153,0,76));        
    }
    //------------------------------------------------------------------------//
    
    
}
