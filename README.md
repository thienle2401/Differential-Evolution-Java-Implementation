# Differential Evolution

Mathematic Optimisation is everywhere, from business to engineering design, from planning your holiday to your daily routine. 

**Differential Evolution (DE)** is a method that optimise a problem by trying to improve a candidate solution but does not use the gradient of the problem. Therefore, this method does not require for the optimisation problem to be the differentiable as is required by classic/ genetic optimization methods such as gradient descent and quasi-newton methods. Interested user can read more about **DE** at: [https://en.wikipedia.org/wiki/Differential_evolution](https://en.wikipedia.org/wiki/Differential_evolution).

## Getting Started

Define your own fitness/ cost function. In this implementation, **Rastrigin** function is used as fitness function but you can use your own function and edit in "**fitFunction()**" function .
This method is supposed to find a best candidate after specified number of iterations. However, the user can use specify lower/ upper bounds for each dimension (attribute, feature) in this implementation. 

Before initialise the optimising process, these are three parameters that the user need to specify and further investigation or trails may be needed to identify usable settings and good combination that yields good peformance. 

* **Population size**
* **Crossover probability**
* **Differential Weight**
* **Number of iteration**

### Running the code

The following is specific example to run the algorithm to find a most optimised combination of three dimensions (each with given lower/ upper bounds) that has maximised the value of the fitness function. 

```java
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

DEnative de = new DEnative();
System.out.println("Best combination found us: " + de.optimise(dimensionList));     
```

The populuations are saved in _data_ folder. The user can observe how the population evoluting after each iteration. 

## Acknowledgments

This development and the research has been supported by the UK Engineering, and Physical Sciences Research Council (EPSRC) grant EP/M016870/1.