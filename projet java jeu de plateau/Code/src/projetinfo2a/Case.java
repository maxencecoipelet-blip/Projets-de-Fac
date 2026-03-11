package projetinfo2a;
public abstract class Case {
    private String texture;
    private int ligne;
    private int colonne;
    private boolean visible;


    public String getTexture(){
        return this.texture;
    }

    public void setTexture(String texture){
        this.texture=texture;
    }

    public int getLigne(){
        return this.ligne;
    }

    public int getColonne(){
        return this.colonne;
    }

    public void setVisible(boolean b){
        this.visible=b;
    }

    public Case(int i, int j){
        this.ligne = i;
        this.colonne = j;
    }

    }

