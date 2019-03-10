package main;

public class Pop {

    dir[][] genotype;
    enum dir {
        up,
        down,
        left,
        right,
        self
    }

    float conc = Float.MAX_VALUE;
    float dev = Float.MAX_VALUE;

    Pop(Bean bean){
        genotype = new dir[bean.height][bean.width];
        MST.produceRandomMst(bean,genotype);
    }

    void calculateFitness(){

    }


    void printConnections(){
        for (int i = 0; i < genotype.length ; i++) {
            StringBuilder line = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            for (int j = 0; j < genotype[i].length; j++) {
                if(genotype[i][j] == dir.self){
                    line.append("X");
                    if(j != genotype[i].length-1 && genotype[i][j+1] == dir.left){
                        line.append(" <- ");
                    }else{
                        line.append("    ");
                    }
                    if(i != genotype.length-1){
                        if(genotype[i+1][j] != dir.up) {
                            line2.append("     ");
                        }else{
                            line2.append("^    ");
                        }
                    }
                }else {
                    line.append("O");
                    if(genotype[i][j] == dir.left){
                        if(j != genotype[i].length-1 && genotype[i][j+1] == dir.left){
                            line.append(" <- ");
                        }else{
                            line.append("    ");
                        }
                        if(i != genotype.length-1){
                            if(genotype[i+1][j] != dir.up) {
                                line2.append("     ");
                            }else{
                                line2.append("^    ");
                            }
                        }
                    }else if(genotype[i][j] == dir.right){
                        if(genotype[i][j+1] == dir.left){
                            line.append("<-->");
                        }else{
                            line.append(" -> ");
                        }
                        if(i != genotype.length-1){
                            if(genotype[i+1][j] != dir.up) {
                                line2.append("     ");
                            }else{
                                line2.append("^    ");
                            }
                        }
                    }else if(genotype[i][j] == dir.down){
                        if(genotype[i+1][j] == dir.up){
                            line2.append("|    ");
                        }else{
                            line2.append("v    ");
                        }
                        if(j != genotype[i].length-1){
                            if(genotype[i][j+1] != dir.left) {
                                line.append("    ");
                            }else{
                                line.append(" <- ");
                            }
                        }
                    }else if(genotype[i][j] == dir.up){
                        if(i != genotype.length-1 && genotype[i+1][j] == dir.up){
                            line2.append("^    ");
                        }else{
                            line2.append("     ");
                        }
                        if(j != genotype[i].length-1){
                            if(genotype[i][j+1] != dir.left) {
                                line.append("    ");
                            }else{
                                line.append(" <- ");
                            }
                        }
                    }
                }

            }
            System.out.println(line);
            System.out.println(line2);
        }
    }

}
