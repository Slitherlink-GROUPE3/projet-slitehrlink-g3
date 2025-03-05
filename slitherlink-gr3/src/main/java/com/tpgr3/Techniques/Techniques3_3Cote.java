public class Techniques3_3Cote {

    public boolean estApplicable(){
        int i, j;
        for(i = 1; i < hauteur; i+=2){
            for(j = 1; j < largeur; j+=2){
                if((matrice[i][j] == 3 && matrice[i][j+2] == 3) || (matrice[i][j] == 3 && matrice[i+2][j] == 3))
                    return true;
            }
        }
        return false;
    }

}