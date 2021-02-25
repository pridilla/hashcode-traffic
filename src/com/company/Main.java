package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}

class Street {
    Intersection source;
    Intersection target;

    TrafficLight trafficLight = TrafficLight.RED;
    int l;

    void switchRed () {
        trafficLight = TrafficLight.RED;
    }

    void switchGreen () {
        trafficLight = TrafficLight.GREEN;
        for (Street street : target.inStreets) {
            street.switchRed();
        }
    }
}

class Intersection {
    List<Street> inStreets = new ArrayList<>();
    List<Street> outStreets = new ArrayList<>();
}

enum TrafficLight {
    RED, GREEN
}

class Pair {

}