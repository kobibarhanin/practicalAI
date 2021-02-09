package com.graphColoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class Graph {

    private boolean adjMatrix[][];
    public int numVertices;

    public Graph(int numVertices) {
        this.numVertices = numVertices;
        adjMatrix = new boolean[numVertices][numVertices];
        this.buildHardcoded(numVertices);
    }

    public Graph(String path) {
        this.buildFromFile(path);
    }

    public void addEdge(int i, int j) {
        adjMatrix[i][j] = true;
        adjMatrix[j][i] = true;
    }

    public void removeEdge(int i, int j) {
        adjMatrix[i][j] = false;
        adjMatrix[j][i] = false;
    }

    public boolean isEdge(int i, int j) {
        return adjMatrix[i][j];
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < numVertices; i++) {
            s.append(i + ": ");
            for (boolean j : adjMatrix[i]) {
                s.append((j?1:0) + " ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public void printGraph(){
        System.out.println(toString());
    }

    public ArrayList<Integer> getNeighborhood(int source){
        ArrayList<Integer> neighborhood = new ArrayList<Integer>();

        for (int i=0; i<numVertices; i++){
            if(isEdge(source,i)&&(source!=i)){
                neighborhood.add(i);
            }
        }

        return neighborhood;
    }

    public boolean checkNeighborhood(ArrayList<Integer> coloringVector, int vertice){

        ArrayList<Integer> neighborhood = new ArrayList<Integer>();
        neighborhood=this.getNeighborhood(vertice);

        for(int j=0; j<neighborhood.size(); j++){
            if(neighborhood.get(j)<coloringVector.size()) {
                if (coloringVector.get(neighborhood.get(j)) == coloringVector.get(vertice) && neighborhood.get(j) != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void buildFromFile(String path){

        BufferedReader br = null;
        FileReader fr = null;
        try {

            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String sCurrentLine = br.readLine();;

            while (sCurrentLine.equals("") || sCurrentLine.charAt(0)!='p')
                sCurrentLine = br.readLine();

            String [] splitNodesNum = sCurrentLine.split("\\s+");
            int nodesNum = Integer.parseInt(splitNodesNum[2]);
            this.numVertices=nodesNum;

            this.adjMatrix = new boolean[numVertices][numVertices];

            while ((sCurrentLine = br.readLine()) != null) {
                sCurrentLine = sCurrentLine.substring(2);
                String [] splitted = sCurrentLine.split("\\s+");
                int ver1 = Integer.parseInt(splitted[0]);
                int ver2 = Integer.parseInt(splitted[1]);
                this.addEdge(ver1-1,ver2-1);

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    public void buildHardcoded(int numVertices){

        if(numVertices==3) {
            this.addEdge(0, 1);
            this.addEdge(1, 2);
        }


        if(numVertices==4){
            this.addEdge(0, 1);
            this.addEdge(0, 2);
            this.addEdge(1, 2);
            this.addEdge(2, 0);
            this.addEdge(2, 3);
        }

        if(numVertices==6){
            this.addEdge(0, 1);
            this.addEdge(0, 2);
            this.addEdge(1, 2);
            this.addEdge(4, 3);
            this.addEdge(2, 3);
            this.addEdge(4, 1);
            this.addEdge(5, 3);
            this.addEdge(4, 5);
        }

        if(numVertices==8){
            this.addEdge(0, 1);
            this.addEdge(0, 2);
            this.addEdge(0, 3);
            this.addEdge(0, 4);
            this.addEdge(1, 2);
            this.addEdge(1, 4);
            this.addEdge(1, 7);
            this.addEdge(2, 3);
            this.addEdge(2, 5);
            this.addEdge(2, 6);
            this.addEdge(3, 6);
            this.addEdge(3, 4);
            this.addEdge(3, 7);
            this.addEdge(3, 6);
            this.addEdge(4, 7);

            this.addEdge(2, 7);
            this.addEdge(0, 7);
            this.addEdge(3, 5);
            this.addEdge(1, 6);
        }

    }

    public int getMostConstrainedVertice(int location){

        class node_constraints{
            int node;
            int constraints;

            public node_constraints(int node, int constraints) {
                this.node = node;
                this.constraints = constraints;
            }
        }

        class ConstraintsComparator implements Comparator<node_constraints> {
            @Override
            public int compare(node_constraints o1, node_constraints o2) {
                return (o1.constraints<o2.constraints) ? 1:-1;
            }
        }

        ArrayList<node_constraints> constraintsVector = new ArrayList<node_constraints>();


        int accumulator=0;
        for(int i=0; i<numVertices; i++){
            for(int j=0; j<numVertices; j++){
                if(adjMatrix[i][j]==true){
                    accumulator++;
                }
            }
            constraintsVector.add(new node_constraints(i,accumulator));
            accumulator=0;
        }

        constraintsVector.sort(new ConstraintsComparator());
        return constraintsVector.get(location).node;
    }

    public static void main(String args[]) {

        Graph g = new Graph(4);

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        g.addEdge(2, 3);

        System.out.print(g.toString());
        /* Outputs
           0: 0 1 1 0
           1: 1 0 1 0
           2: 1 1 0 1
           3: 0 0 1 0
          */

        ArrayList<Integer> neighborhood = new ArrayList<Integer>();

        neighborhood = g.getNeighborhood(1);
        neighborhood = g.getNeighborhood(2);
        neighborhood = g.getNeighborhood(3);
    }

}