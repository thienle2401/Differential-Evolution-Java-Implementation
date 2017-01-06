/* 
 * Copyright (c) 2016, University of Reading. Authors: Thien le, Frederic Stahl
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package uk.ac.rdg.bdata.differentialevolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;

public class DE {
    
    // list of generated populations
    List<Individual> population;
    
    // populiation size to generate at the begining 
    int POPULATION_SIZE = 200;
    
    // crossover probability [0,1]
    double CROSSOVER_PROBABILITY = 0.4;
    
    // differential weight [0,2]
    double DIFFERENTIAL_WEIGHT = 1.2d;

    // number of iteration
    int ITERATION_NO = 50;
    
    // random number generator
    Random random;
    
    PrintWriter pw;  
    
    public static void main(String[] args){
        
        // define lower/ upper bounds for each required dimensions

        // define lower/ upper bounds for 1st dimension
        double[] dimension1Bounds = new double[2];
        dimension1Bounds[0] = 1.0d;
        dimension1Bounds[1] = 10.0d;
        
        // define lower/ upper bounds for 2nd dimension
        double[] dimension2Bounds = new double[2];
        dimension2Bounds[0] = 80.0d;
        dimension2Bounds[1] = 160.0d;
        
        // define lower/ upper bounds for 3rd dimension
        double[] dimension3Bounds = new double[2];
        dimension3Bounds[0] = 100.0d;
        dimension3Bounds[1] = 200.0d;

        // add all dimension to a list, and this will be passed to DE
        List<double[]> dimensionList = new LinkedList<>();
        
        dimensionList.add(dimension1Bounds);
        dimensionList.add(dimension2Bounds);
        dimensionList.add(dimension3Bounds);
        
        DE de = new DE();
        
        // start optimising process and return the best candiate after number of spcecified iteration
        System.out.println("Best combination found: " + de.optimise(dimensionList));             
    }
    
    // fitness function
    public double fitFunction(Individual aCandidate){
       
        // Rastrigin function
        double value = 10.0d * aCandidate.dataValue.length;
        
        for(int i = 0; i < aCandidate.dataValue.length; i++){
            value = value + Math.pow(aCandidate.dataValue[i], 2.0) - 10.0 * Math.cos(2 * Math.PI * aCandidate.dataValue[i]); 
        }
        
//        double[] values = aCandidate.dataValue; 
//        double value = 100 * Math.pow(Math.pow(values[0], 2) - values[1], 2) + Math.pow(1 - values[0], 2);
        
        return value;
    }
        
    // DE constructor
    public DE(){ 
        random = new Random();
        population = new LinkedList<>();
    }
    
    public Individual optimise(List<double[]> dimensionList){
         
        // generate population up to the define limit
        for(int i = 0; i < POPULATION_SIZE; i++){
            Individual individual = new Individual(dimensionList);
            population.add(individual);
            
        }       
                
       // try more than one iteration 
       for(int iterationCount = 0; iterationCount < ITERATION_NO; iterationCount++){
           
            try {
                pw = new PrintWriter(new File("data/popoluation_" + Integer.toString(iterationCount) +".csv"));
            } catch (FileNotFoundException ex) {
                
            }    
            
            
            for(int n = 0; n < dimensionList.size(); n++){      
                pw.write("v" + Integer.toString(n));
                pw.write(",");
            }
        
            pw.write("fValue");

            pw.write("\n");
        
            for (Individual individual : population) {
                pw.write(individual.toString());
                pw.write(",");
                pw.write(Double.toString(fitFunction(individual)));
                pw.write("\n");
            }
            
            pw.flush();
            int loop = 0;
        
            // main loop for evolution
            while(loop < population.size()){       

            Individual original = null;
            Individual candidate = null;
            boolean boundsHappy;

            do{
                boundsHappy = true;
                // pick an agent from the the population
                int x = loop;
                int a,b,c = -1;

                // pick three random agents from the population
                // make sure that they are not identical to selected agent from
                // the population 

                do{
                    a = random.nextInt(population.size());
                }while(x == a);
                do{
                    b = random.nextInt(population.size());
                }while(b==x || b==a);
                do{
                    c = random.nextInt(population.size());
                }while(c == x || c == a || c == b);

                // create three agent individuals
                Individual individual1 = population.get(a);
                Individual individual2 = population.get(b);
                Individual individual3 = population.get(c);

                // create a noisy random candidate
                Individual noisyRandomCandicate = new Individual(dimensionList);

                // mutation process
                // if an element of the trial parameter vector is
                // found to violate the bounds after mutation and crossover, it is reset in such a way that the bounds
                // are respected (with the specific protocol depending on the implementation)
                for(int n = 0; n < dimensionList.size(); n++){     
                    noisyRandomCandicate.dataValue[n] = (individual1.dataValue[n] + DIFFERENTIAL_WEIGHT * (individual2.dataValue[n] - individual3.dataValue[n]));               
                }           

                // Create a trial candicate 
                original = population.get(x);
                candidate = new Individual(dimensionList);

                // copy values from original agent to the candidate agent
                for(int n = 0; n < dimensionList.size(); n++){             
                    candidate.dataValue[n] = original.dataValue[n];
                }  

                // crossver process with the selected individual
                // pick a random dimension, which defintely takes the value from the noisy random candidate
                int R = random.nextInt(dimensionList.size());

                for(int n = 0; n < dimensionList.size(); n++){

                    double crossoverProbability = random.nextDouble();

                    if(crossoverProbability < CROSSOVER_PROBABILITY || n == R){
                        candidate.dataValue[n] = noisyRandomCandicate.dataValue[n];
                    }

                }

                // check here if the trial candiate satisfies bounds for each value
                for(int n = 0; n < dimensionList.size(); n++){ 
                    if(candidate.dataValue[n] < dimensionList.get(n)[0] || candidate.dataValue[n] > dimensionList.get(n)[1]){
                       boundsHappy = false;
                    }
                }

            }while(boundsHappy == false);

                //see if the candidate is better than original, if so replace it
                if(fitFunction(original) < fitFunction(candidate)){
                        population.remove(original);
                        population.add(candidate);     
                }
                loop++;
            }        
        }
        
       Individual bestFitness = new Individual(dimensionList);
   
       // selecting the final best agent from the the population
       for(int i = 0; i < population.size(); i++){
           Individual individual = population.get(i);
           
            if(fitFunction(bestFitness) < fitFunction(individual)){
                
               try {
                   bestFitness = (Individual) individual.clone();
               } catch (CloneNotSupportedException ex) {
                  
               }
            }
       }
       
        
       return bestFitness;
    }
            
    public class Individual implements Cloneable{
      
        // each element a value from valid range for a given dimension
        double[] dataValue;
        
        public Individual(List<double[]> dimensionIn){
            int noDimension = dimensionIn.size();       
            // initialse data vector
            dataValue = new double[noDimension];
                 
            // for each dimension, create corresponding data point between given range
            for(int dimensionIndex = 0; dimensionIndex < noDimension; dimensionIndex++){
                
                double dimensionLowerBound = dimensionIn.get(dimensionIndex)[0];
                double dimensionUpperBound = dimensionIn.get(dimensionIndex)[1];        
                
                DoubleStream valueGenerator = random.doubles(dimensionLowerBound, dimensionUpperBound);
                
                dataValue[dimensionIndex] = valueGenerator.iterator().nextDouble();
            }
        }
        @Override
        public String toString(){
            
            String string = "";
            
            for (int i = 0; i < dataValue.length; i++) {
                string += Double.toString(dataValue[i]);
                
                if((i + 1) != dataValue.length){
                    string += ",";
                }
            }
            
            return string;
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    

}

