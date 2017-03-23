import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class NetworkMatrix {
    LinkedHashMap<Integer, Integer> adjCapacitytList = new LinkedHashMap<>();
    LinkedHashMap<Integer, String> inputNodes = new LinkedHashMap<>();
    LinkedHashMap<Integer, ArrayList<Integer>> adjCommonPathList = new LinkedHashMap<>();
    LinkedHashMap<Integer, Double> adjUtilizationtList = new LinkedHashMap<>();
    LinkedHashMap<Integer, Double> adjIntensitytList = new LinkedHashMap<>();


    int[] intensities = new int[]{4,3,2};
    int weight = 0;
    int vertex =0;

    int w = 0;
    double v =0;

    int w1=0;
    double v1=0;
    ArrayList<Integer> commonPathList = new ArrayList<>();

    int node =0;

    NetworkMatrix[] networkMatrix;
    NetworkMatrix[] commonPath;
    NetworkMatrix[] utililization;
    NetworkMatrix[] loadIntensity;
    NetworkMatrix() {
    }
    NetworkMatrix(int size) {
        networkMatrix = new NetworkMatrix[size];
        commonPath = new NetworkMatrix[size];
        utililization = new NetworkMatrix[8];
        loadIntensity = new NetworkMatrix[8];
     for ( int i = 0; i < size; i++ ) {
            networkMatrix[i] = new NetworkMatrix();
            commonPath[i] = new NetworkMatrix();
        }
        for ( int i = 0; i < 8; i++ ) {
            utililization[i] = new NetworkMatrix();
        }
        for ( int i = 0; i < 8; i++ ) {
            loadIntensity[i] = new NetworkMatrix();
        }

    }
    void addCapacity(int vertex, int weight)
    {
        this.adjCapacitytList.put(vertex,weight);
    }

    void addCommonPath(int vertex, ArrayList<Integer> commonPathList) {this.adjCommonPathList.put(vertex, commonPathList);}

    void addUtilization(int v, double w)
    {
        this.adjUtilizationtList.put(v,w);
    }
    void addIntensity(int v1, double w1)
    {
        this.adjIntensitytList.put(v1,w1);
    }

    public static void main(String srgs[])
    {
        int sizeOfGraph = 4;

        NetworkMatrix nMatrix = new NetworkMatrix(sizeOfGraph);
        ArrayList<Integer> trafficControl = new ArrayList<>();
        TransitTrafficRouting transitTrafficRouting = new TransitTrafficRouting();
        int source = 0;

        nMatrix.inputNodes.put(0,"A");
        nMatrix.inputNodes.put(1,"B");
        nMatrix.inputNodes.put(2,"C");

        //Storing the capacity of the nodes
        nMatrix.networkMatrix[0].addCapacity(1, 3);
        nMatrix.networkMatrix[0].addCapacity(3, 2);
        nMatrix.networkMatrix[1].addCapacity(2, 5);
        nMatrix.networkMatrix[1].addCapacity(0, 4);
        nMatrix.networkMatrix[2].addCapacity(3, 1);
        nMatrix.networkMatrix[2].addCapacity(1, 3);
        nMatrix.networkMatrix[3].addCapacity(0, 5);
        nMatrix.networkMatrix[3].addCapacity(2, 6);

        trafficControl.add(1);
        nMatrix.commonPath[0].addCommonPath(1, trafficControl);

        trafficControl = new ArrayList<>();
        trafficControl.add(2);
        trafficControl.add(4);

        nMatrix.commonPath[0].addCommonPath(3, trafficControl);


        trafficControl = new ArrayList<>();

        trafficControl.add(1);
        trafficControl.add(3);
        nMatrix.commonPath[1].addCommonPath(2, trafficControl);

        trafficControl = new ArrayList<>();
        trafficControl.add(4);
        trafficControl.add(6);

        nMatrix.commonPath[1].addCommonPath(0, trafficControl);

        trafficControl = new ArrayList<>();
        trafficControl.add(4);
        trafficControl.add(6);

        nMatrix.commonPath[1].addCommonPath(0, trafficControl);

        trafficControl = new ArrayList<>();
        trafficControl.add(6);
        nMatrix.commonPath[2].addCommonPath(1, trafficControl);


        trafficControl = new ArrayList<>();
        trafficControl.add(3);
        trafficControl.add(5);
        nMatrix.commonPath[2].addCommonPath(3, trafficControl);

        trafficControl = new ArrayList<>();
        trafficControl.add(5);
        nMatrix.commonPath[3].addCommonPath(0, trafficControl);

        trafficControl = new ArrayList<>();
        trafficControl.add(2);
        nMatrix.commonPath[3].addCommonPath(2, trafficControl);

        transitTrafficRouting.initializeTrafficControlVar(nMatrix);
        transitTrafficRouting.findOptimalUtilization(nMatrix);
    }

}

