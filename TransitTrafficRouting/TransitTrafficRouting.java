import java.io.BufferedWriter;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class TransitTrafficRouting {

    double[] trafficControlVariables = new double[6];
    double[] cost = new double[6];
    double[] latestUtilization = new double[6];
    int intensity = 0;
    int counter = 0;
    double finalcost = 0;
    int count = 0;

    double alpha = 0;
    double beta = 0;
    double gamma = 0;

    double costABC = 0;
    double costADC = 0;
    double costBCD = 0;
    double costBAD = 0;
    double costCDA = 0;
    double costCBA = 0;

    boolean flagAlpha = true;
    boolean flagBeta = true;
    boolean flagGamma = true;

    /**
     * To initialize the traffic control variables and initial values of cost
     **/
    void initializeTrafficControlVar(NetworkMatrix networkM) {
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter value of alpha(it should be less than 1) for AC via B node: ");
        double n = reader.nextDouble();
        if (n <= 1) {
            trafficControlVariables[0] = n;
            trafficControlVariables[1] = 1 - n;
        } else {
            return;
        }
        reader = new Scanner(System.in);
        System.out.println("Enter value of alpha(it should be less than 1) for BD via C node: ");
        n = reader.nextDouble();
        if (n <= 1) {
            trafficControlVariables[2] = n;
            trafficControlVariables[3] = 1 - n;
        } else {
            return;
        }
        reader = new Scanner(System.in);
        System.out.println("Enter value of alpha(it should be less than 1) for CA via D node: ");
        n = reader.nextDouble();
        if (n <= 1) {
            trafficControlVariables[4] = n;
            trafficControlVariables[5] = 1 - n;
        } else {
            return;
        }

        calculateUtilization(networkM, 0, 2);
        calculateUtilization(networkM, 1, 3);
        calculateUtilization(networkM, 2, 0);

    }

    /*
    * To Calculate the value for costs for A--> C via B and D; B --> D via A and C; C --> A via B and D.
    * */
    void calculateUtilization(NetworkMatrix networkM, int source, int destination) {
        int j = 0;
        int nextNodeValue = 0;
        double nextNodeCapacity = 0;
        LinkedHashMap<Integer, Integer> tempCapaciytMap = networkM.networkMatrix[source].adjCapacitytList;
        Iterator iterator = tempCapaciytMap.entrySet().iterator();
        if (source == 0) {
            counter = source;
        } else if (source == 1) {
            counter = source + 1;
        } else if (source == 2) {
            counter = source + 2;
        }
        while (iterator.hasNext()) {
            Map.Entry nextNode = (Map.Entry) iterator.next();
            nextNodeValue = Integer.parseInt(nextNode.getKey().toString());
            nextNodeCapacity = Double.parseDouble(nextNode.getValue().toString());

            finalcost = 0;
            findindividualUtilization(networkM, source, nextNodeValue, nextNodeCapacity, destination);
        }
    }

    /*
* To Calculate the value for utilizations for each link at a time
* */
    void findindividualUtilization(NetworkMatrix networkM, int source, int nextNodeValue, double nextNodeCapacity, int destination) {

        ArrayList<Integer> tempCommonPath = networkM.commonPath[source].adjCommonPathList.get(nextNodeValue);
        double tempUtilization = 0;
        int tempNextNode = 0;

        for (int i = 0; i < tempCommonPath.size(); i++) {
            int nextNodeTrafficControl = tempCommonPath.get(i);
            intensity = getIntensity(nextNodeTrafficControl, networkM);

            tempUtilization = ((intensity * trafficControlVariables[nextNodeTrafficControl - 1])) + tempUtilization;
        }
        tempNextNode = nextNodeValue;
        networkM.loadIntensity[source].addIntensity(nextNodeValue, tempUtilization);
        tempUtilization = tempUtilization / nextNodeCapacity;
        networkM.utililization[source].addUtilization(nextNodeValue,tempUtilization);

        finalcost = tempUtilization + finalcost;
        if (tempNextNode == destination) {
            cost[counter] = finalcost;
            counter++;
            return;
        }
        LinkedHashMap<Integer, Integer> tempCapaciytMap = networkM.networkMatrix[tempNextNode].adjCapacitytList;
        Iterator iterator1 = tempCapaciytMap.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry nextNode = (Map.Entry) iterator1.next();
            int node = Integer.parseInt(nextNode.getKey().toString());
            if (node == destination) {
                double capacity = Double.parseDouble(nextNode.getValue().toString());
                findindividualUtilization(networkM, tempNextNode, node, capacity, destination);
            }
        }
    }

    /*
     * To optimize the values of costs for each link
     */
    void findOptimalUtilization(NetworkMatrix networkM) {
        BufferedWriter br = null;
        StringBuilder sb = new StringBuilder();
        while (flagBeta || flagGamma || flagAlpha) {


            flagAlpha = false;
            costABC = cost[0];
            costADC = cost[1];
            if ((cost[0] < cost[1]) && (trafficControlVariables[1] >= .0001) && (costADC - costABC) >= .001) {
                trafficControlVariables[1] = trafficControlVariables[1] - .0001;
                trafficControlVariables[0] = 1 - trafficControlVariables[1];
                flagAlpha = true;
            } else if (cost[0] > cost[1] && trafficControlVariables[0] >= .0001 && (costABC - costADC) >= .001) {
                trafficControlVariables[0] = trafficControlVariables[0] - .0001;
                trafficControlVariables[1] = 1 - trafficControlVariables[0];
                flagAlpha = true;
            }

            flagBeta = false;
            costBCD = cost[2];
            costBAD = cost[3];
            if (cost[2] < cost[3] && trafficControlVariables[3] >= .0001 && (costBAD - costBCD) >= .001) {
                trafficControlVariables[3] = trafficControlVariables[3] - .0001;
                trafficControlVariables[2] = 1 - trafficControlVariables[3];
                flagBeta = true;
            } else if (cost[2] > cost[3] && trafficControlVariables[2] >= .0001 && (costBCD - costBAD) >= .001) {
                trafficControlVariables[2] = trafficControlVariables[2] - .0001;
                trafficControlVariables[3] = 1 - trafficControlVariables[2];
                flagBeta = true;
            }
            flagGamma = false;
            costCDA = cost[4];
            costCBA = cost[5];

            if (cost[4] < cost[5] && trafficControlVariables[5] >= .0001 && (costCBA - costCDA) >= .001) {
                trafficControlVariables[5] = trafficControlVariables[5] - .0001;
                trafficControlVariables[4] = 1 - trafficControlVariables[5];
                flagGamma = true;
            } else if (cost[4] > cost[5] && trafficControlVariables[4] >= .0001 && (costCDA - costCBA) >= .001) {
                trafficControlVariables[4] = trafficControlVariables[4] - .0001;
                trafficControlVariables[5] = 1 - trafficControlVariables[4];
                flagGamma = true;
            }
            calculateUtilization(networkM, 0, 2);
            calculateUtilization(networkM, 1, 3);
            calculateUtilization(networkM, 2, 0);
            count++;

            alpha = trafficControlVariables[0];
            beta = trafficControlVariables[2];
            gamma = trafficControlVariables[5];
        }

        calculateUtilization(networkM, 0, 2 );

        calculateUtilization(networkM, 1, 3 );

        calculateUtilization(networkM, 2, 0 );
        finalOutput(networkM);

    }
     //To retreive intensity for each source
     int getIntensity(int nextNodeTrafficControl, NetworkMatrix networkM) {
            if ((nextNodeTrafficControl) == 1 || (nextNodeTrafficControl) == 2) {
                return networkM.intensities[0];
            } else if ((nextNodeTrafficControl) == 3 || (nextNodeTrafficControl) == 4) {
                return networkM.intensities[1];
            }
            else if ((nextNodeTrafficControl) == 5|| (nextNodeTrafficControl) == 6) {
                return networkM.intensities[2];
            }
            else
                return 0;
        }
        //To print final outputs of link utilization and load, traffic control variables and cost of path
        void finalOutput(NetworkMatrix networkMatrix)
        {
            System.out.println("Final Traffic Control Values:");

            System.out.println("\n");
            System.out.println("{alpha(A-B-C)}   --> " + trafficControlVariables[0]);
            System.out.println("{1-alpha(A-D-C)} --> " + trafficControlVariables[1]);
            System.out.println("{beta(B-C-D)}    --> " + trafficControlVariables[2]);
            System.out.println("{1-beta(B-A-D)}  --> " + trafficControlVariables[3]);
            System.out.println("{gamma(C-D-A)}   --> " + trafficControlVariables[4]);
            System.out.println("{1-gammaa(C-B-A)}--> " + trafficControlVariables[5]);
            System.out.println("\n\n");;

            System.out.println("Total traffic for each link:");
            System.out.println("\n");
            System.out.println("Total traffic from A to B --> " + networkMatrix.loadIntensity[0].adjIntensitytList.get(1));
            System.out.println("Total traffic from B to C --> " + networkMatrix.loadIntensity[1].adjIntensitytList.get(2));
            System.out.println("Total traffic from C to D --> " + networkMatrix.loadIntensity[2].adjIntensitytList.get(3));
            System.out.println("Total traffic from D to A --> " + networkMatrix.loadIntensity[3].adjIntensitytList.get(0));
            System.out.println("Total traffic from B to A --> " + networkMatrix.loadIntensity[1].adjIntensitytList.get(0));
            System.out.println("Total traffic from C to B --> " + networkMatrix.loadIntensity[2].adjIntensitytList.get(1));
            System.out.println("Total traffic from D to C --> " + networkMatrix.loadIntensity[3].adjIntensitytList.get(2));
            System.out.println("Total traffic from A to D --> " + networkMatrix.loadIntensity[0].adjIntensitytList.get(3));
            System.out.println("\n\n");

            System.out.println("Final Utilization for each link:");
            System.out.println("\n");
            System.out.println("Total utilization from A to B --> " + networkMatrix.utililization[0].adjUtilizationtList.get(1));
            System.out.println("Total utilization from B to C --> " + networkMatrix.utililization[1].adjUtilizationtList.get(2));
            System.out.println("Total utilization from C to D --> " + networkMatrix.utililization[2].adjUtilizationtList.get(3));
            System.out.println("Total utilization from D to A --> " + networkMatrix.utililization[3].adjUtilizationtList.get(0));
            System.out.println("Total utilization from B to A --> " + networkMatrix.utililization[1].adjUtilizationtList.get(0));
            System.out.println("Total utilization from C to B --> " + networkMatrix.utililization[2].adjUtilizationtList.get(1));
            System.out.println("Total utilization from D to C --> " + networkMatrix.utililization[3].adjUtilizationtList.get(2));
            System.out.println("Total utilization from A to D --> " + networkMatrix.utililization[0].adjUtilizationtList.get(3));
            System.out.println("\n\n");

            System.out.println("Final Cost for all the transit paths:");

            System.out.println("\n");
            System.out.println("{uABC} --> " + cost[0]);
            System.out.println("{uADC} --> " + cost[1]);
            System.out.println("{uBCD} --> " + cost[2]);
            System.out.println("{uBAD} --> " + cost[3]);
            System.out.println("{uCDA} --> " + cost[4]);
            System.out.println("{uCBA} --> " + cost[5]);





        }
}