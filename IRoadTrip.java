import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

/* @author Cora Schmidt
@date 12/5/23
IRoadTrip is a Java program that helps users find the shortest path between the capitals of two countries, 
considering the distances between them. The program reads input files containing information about country borders, 
capital distances, and country names to build a graph representation of the relationships between countries.*/

//terminal call: java IRoadTrip borders.txt capdist.csv state_name.tsv

class IRoadTrip {

    // Strings to store names of files
    String borders; // statename
    String capdist; // stateid, statenum
    String state_name; // statenum, stateid, statename
    // start and destination country ids
    String country1;
    String country2;
    // declare hashmaps to store data
    HashMap<String, String> name2id; // connects country names to ids
    HashMap<String, Integer> distances; // uses country names as keys and km between capitols as value
    HashMap<String, ArrayList<String>> adjacents; // hashmap of stateids from borders file
    HashMap<String, HashMap<String, Integer>> bighash;
    // boolean to stop program
    boolean stop = false;

    // IRoadTrip constructor interprets data and builds graph
    public IRoadTrip(String[] args) throws FileNotFoundException {
        // stores file names from args
        borders = args[0]; // "borders.txt"; //
        capdist = args[1]; // "capdist.csv"; //
        state_name = args[2]; // "state_name.tsv"; //

        // create data structuresfrom input files

        // read statename file and make HashMap "name2id" using country names as keys
        // and their corresponding stateids as values
        buildname2id(state_name);

        // read capdist file and make HashMap "distances" with Strings of all capitol
        // distances using country names as key and km as value
        builddistances(capdist);

        // create HashMap "adjacents" of stateids from borders file
        // key is the stateid of the country before the "=" and value
        // is an arraylist of the stateids of the countries after the "="
        buildadjacents(borders);

        // build HashMap "bighash" with stateids as keys and a HashMap
        // "smallhash" as value. smallhash holds all the bordering country's stateids
        // as keys and km distance between the capitols as values
        buildbigHash(adjacents);
    }

    // build HashMap "bighash" with stateids as keys and an arraylist of HashMaps
    // "smallhash"s as values. smallhash holds a stateid as key and km distance
    // between captitols of countries the keys represent as a value
    void buildbigHash(HashMap<String, ArrayList<String>> adjacents) {
        bighash = new HashMap<String, HashMap<String, Integer>>();
        // there will be some repeat mainc four countries w >1 name
        for (String mainc : name2id.values()) {
            HashMap<String, Integer> smallhash = new HashMap<String, Integer>(); // will be value in bighash
            ArrayList<String> borderingcountries = adjacents.get(mainc);
            // build ArrayList value
            if (borderingcountries == null) { // no countries border mainc
                smallhash = null;
            } else { // there are bordering countries
                for (String c : borderingcountries) { // build smallhashes
                    String dkey = mainc + c; // key for distances HashMap
                    Integer km = distances.get(dkey); // km between capitols
                    // System.out.println(dkey + " " + km);
                    smallhash.put(c, km);
                }
            }
            bighash.put(mainc, smallhash);
        }
    }

    // prints bighash (testing purposes only)
    void printBigHash() {
        for (String key : bighash.keySet()) {
            System.out.println();
            System.out.println();
            System.out.println("main country: " + key);
            if (bighash.get(key) != null) {
                System.out.println("bordering countries and km");
                for (String c : bighash.get(key).keySet()) {
                    System.out.print(c + " " + bighash.get(key).get(c) + " ");
                }
                System.out.println();
            }
        }
    }

    // create HashMap "adjacents" of stateids from borders file
    // key is the stateid of the country before the "=" and value
    // is an arraylist of the stateids of the countries after the "="
    void buildadjacents(String borders) throws FileNotFoundException {
        File ff = new File(borders);
        Scanner scan = new Scanner(ff);
        adjacents = new HashMap<String, ArrayList<String>>(); // HashMap adjacents of adjacent country state ids
        String mainc; // this will be the key
        while (scan.hasNextLine()) {
            String nextline = scan.nextLine();
            nextline = nextline.trim();
            String[] arz = nextline.split("="); // array before and after "="
            // process the country that will be the key "mainc" before the "="
            mainc = arz[0].trim(); // will be a key in adjacents
            ArrayList<String> bordering = new ArrayList<String>(); // will be the corresponding value in adjacents
            // in this if else assign mainc
            if (!name2id.containsKey(mainc)) { // country is not in state_names file and so not in name2id HashMap
                if (mainc.contains("(")) { // search for pesky "()"
                    String[] names = mainc.split("\\(");
                    // get rid of ")" parenthesis and white space
                    String name1 = names[0].trim();
                    String name2 = names[1].substring(0, names[1].length() - 1).trim();
                    // search name2id now for names before and in ()
                    if (!name2id.containsKey(name1) && !name2id.containsKey(name2)) {
                        mainc = "ignore"; // mainc is an unrecognized country, we will ignore it
                    } else if (name2id.containsKey(name1)) {
                        mainc = name2id.get(name1);
                    } else if (name2id.containsKey(name2)) {
                        mainc = name2id.get(name2);
                    }
                } else { // mainc is an unrecognized country
                    mainc = "ignore";
                }
            } else { // country is in name2id yoohoo!
                mainc = name2id.get(mainc); // key for adjacents is mainc
                // System.out.println("main country: " + mainc);
            }
            // process the countries after the "=", add mainc and bordering to adjacents
            if (arz.length < 2) { // if there are no bordering countries
                // System.out.println("there are no bordering countries for this country: " +
                // mainc);
                bordering = null;
            } else { // there are bordering countries
                String[] arn = arz[1].split(";");
                for (String x : arn) { // for example x is " China 91 km"
                    // isolate country name as n, ex. n = China
                    String[] elem = x.split(" ");
                    String n = "";
                    for (int i = 0; i < elem.length - 3; i++) {
                        n += elem[i] + " ";
                    }
                    n += elem[elem.length - 3]; // this is to avoid a space at the end of the country
                    n = n.trim();
                    // check for () and name2id (aka is recognized in state_names or needs alias)
                    if (!name2id.containsKey(n)) { // country is not in state_names file and so not in name2id
                        if (n.contains("(")) { // search for pesky "()"
                            String[] namess = n.split("\\(");
                            // get rid of ")" parenthesis and white space
                            String name11 = namess[0].trim();
                            String name22 = namess[1].substring(0, namess[1].length() - 1).trim();
                            // search name2id now for names before and in ()
                            if (!name2id.containsKey(name11) && !name2id.containsKey(name22)) { //
                                n = "ignore"; // unrecognized bordering, ignore
                            } else if (name2id.containsKey(name11)) {
                                n = name2id.get(name11);
                            } else if (name2id.containsKey(name22)) {
                                n = name2id.get(name22);
                            }
                        } else {
                            n = "ignore";// unrecognized bordering country, ignore
                        }
                    } else { // country is in name2id yoohoo!
                        n = name2id.get(n);
                    }
                    if (n != "ignore") {
                        bordering.add(n); // adds country to ArrayList for the mainc
                    }
                }
            }
            if (mainc != "ignore") {
                adjacents.put(mainc, bordering);
            }
        }
    }

    // print adjacents (testing purposes only)
    void printadjacents() {
        for (String item : adjacents.keySet()) {
            String key = item;
            System.out.println("main country: " + key);
            ArrayList<String> value = new ArrayList<String>();
            value = adjacents.get(item);
            if (value != null) {
                for (String c : value) {
                    System.out.println(c);
                }
            }
            System.out.println();
        }
    }

    // read statename file and make HashMap "name2id" using country names as keys
    // and their corresponding stateids as values
    void buildname2id(String state_name) throws FileNotFoundException {
        File file = new File(state_name);
        Scanner scan = new Scanner(file);
        // create HashMap that connects country names to ids
        name2id = new HashMap<String, String>();
        while (scan.hasNextLine()) {
            String nextline = scan.nextLine();
            // vet for current countries, add them to HashMap name2id
            if (nextline.contains("2020-12-31")) {
                String[] arr = nextline.split("\t");
                String name;
                String id = arr[1];
                // if statement for countries w/ multiple names
                if (arr[2].contains("(")) {
                    String[] names = arr[2].split("\\(");
                    name = names[0].trim();
                    // get rid of ")" parenthesis and white space
                    String name2 = names[1].substring(0, names[1].length() - 1).trim();
                    // add names and id to HashMap name2id
                    name2id.put(name, id);
                    name2id.put(name2, id);
                } else {
                    name = arr[2].trim();
                    // add name and id to HashMap name2id
                    name2id.put(name, id);
                }
            }
        }
        // country names that do not align file to file
        name2id.put("Bahamas, The", "BHM");
        name2id.put("Cabo Verde", "CAP");
        name2id.put("Bosnia and Herzegovina", "BOS");
        name2id.put("Congo, Democratic Republic of the", "DRC");
        name2id.put("Congo, Republic of", "CON");
        name2id.put("Czechia", "CZR");
        name2id.put("Eswatini", "SWA");
        name2id.put("Gambia, The", "GAM");
        name2id.put("Germany", "GFR");
        name2id.put("Italy", "ITA"); // had a "/" in state_name.tsv
        name2id.put("Sardinia", "ITA");
        name2id.put("Korea, North", "PRK");
        name2id.put("Korea, South", "ROK");
        name2id.put("North Macedonia", "MAC");
        name2id.put("United States", "USA");
        name2id.put("Vietnam", "DRV");
        name2id.put("Suriname", "SUR");
        name2id.put("Tanzania", "TAZ"); // had a "/" in state_name.tsv
        name2id.put("Tanganyika", "TAZ");
        name2id.put("UK", "UK");
        name2id.put("United Kingdom", "UK");
        name2id.put("East Timor", "ETM");
        name2id.put("Timor-Leste", "ETM");

        // weird comma corrections / alternate name corrections
        name2id.put("The Bahamas", "BHM");
        name2id.put("Republic of Congo", "CON");
        name2id.put("Democratic Republic of Congo", "DRC");
        name2id.put("The United States", "USA");
        name2id.put("US", "USA");
        name2id.put("USA", "USA");
        name2id.put("The United Kingdom", "UK");

        // not in capdist file, must pull out
        name2id.remove("South Sudan");
        name2id.remove("Kosovo");
    }

    // read capdist file and make HashMap "distances" with Strings of all capitol
    // distances using country names as key and km as value
    void builddistances(String capdist) throws FileNotFoundException {
        distances = new HashMap<String, Integer>();
        File f = new File(capdist);
        Scanner scan = new Scanner(f);
        scan.nextLine();
        while (scan.hasNextLine()) {
            String nextline = scan.nextLine();
            String[] ar = nextline.split(",");
            String both = ar[1] + ar[3];
            Integer km = Integer.parseInt(ar[4]);
            distances.put(both, km);
        }
    }

    /*
     * This function provides the shortest path distance between the capitals of the
     * two countries passed as arguments. If either of the countries does not exist
     * or if the countries do not share a land border, this function returns a
     * value of -1.
     */
    public int getDistance(String country1, String country2) {
        // use bighash to check they border eachother & return km between capitols
        if (bighash.containsKey(country1) && bighash.get(country1).containsKey(country2)) {
            Integer km = bighash.get(country1).get(country2);
            int r = km; // return value as int
            return r;
        }
        return -1;
    }

    /*
     * This function determines and returns a path between the two countries passed
     * as arguments. This path starts in country1 and ends in country 2. If
     * either of the countries does not exist or if there is no path between the
     * countries, the function returns an empty List . Each
     * element of the list is a String representing one step in a longer path
     * in the format: starting_country --> ending_country (DISTANCE_IN_KM.), eg:
     */
    public List<String> findPath(String country1, String country2) {
        // perform dijkstra's, recieve map of parent countries for shortest paths
        HashMap<String, String> parents = algorithm(country1);
        boolean nopath = false; // boolean when path is impossible
        List<String> path = new ArrayList<String>(); // make list of countries travelled to

        if (country1.equals(country2)) { // start and destination are the same
            String country = returname(country1);
            path.add(country + " --> " + country + " (0 km)");
        }

        String current = country2;
        while (current != null && !current.equals(country1)) {
            // get names of countries to print
            String name1 = returname(parents.get(current));
            String name2 = returname(current);
            if (name1 == null) { // islands as start or destination country
                nopath = true;
                break; // no need to keep adding to path, there is no path
            }
            // string of countries and distance between them
            path.add(name1 + " --> " + name2 + " (" + distances.get(parents.get(current) + current) + " km)");
            current = parents.get(current); // assign next current
        }
        Collections.reverse(path); // path goes from start to finish
        if (nopath) { // if islands, return empty list
            path.clear();
            return path;
        }
        return path;
    }

    // return the name of a country given the id
    String returname(String id) {
        for (Map.Entry<String, String> entry : name2id.entrySet()) {
            if (entry.getValue().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // performs Dijsktra's algorithm with String passed in as starting country and
    // returns a HashMap<String, Integer> of all the shortest pathways to each
    // country
    HashMap<String, String> algorithm(String country1) {
        // stores countries and lowest distance to them
        HashMap<String, Integer> pathways = new HashMap<String, Integer>();
        // stores which countries have been visited
        HashSet<String> visited = new HashSet<String>();
        // adjacent countries priority queue in order of lowest travel distance
        PriorityQueue<String> nextcountries = new PriorityQueue<String>(Comparator.comparing(pathways::get));
        // map for parent countries in pathways
        HashMap<String, String> keeptrack = new HashMap<String, String>();
        // put all countries in pathways with distance infinity
        for (String country : bighash.keySet()) {
            pathways.put(country, Integer.MAX_VALUE);
        }
        // first country starts with distance 0
        pathways.put(country1, 0);
        nextcountries.add(country1);
        // the algorithm
        while (!nextcountries.isEmpty()) {
            String current = nextcountries.poll(); // poll lowest available country
            if (visited.contains(current)) {
                continue;
            } // check and add to visited
            if (visited.contains(this.country2)) {
                break; // path to destination has already been found
            }
            visited.add(current);
            // cycle through countries adjacent to current
            if (/* bighash.containsKey(current) && */ bighash.get(current) != null) { // no islands allowed
                for (String adj : bighash.get(current).keySet()) {
                    if (!visited.contains(adj)) { // if not visited
                        Integer kmbetween = bighash.get(current).get(adj); // get distance between current and adjacent
                        int kmfromcurr = pathways.get(current) + kmbetween; // add to total distance to get to adj
                        if (kmfromcurr < pathways.get(adj)) { // if km from current is smaller
                            pathways.put(adj, kmfromcurr); // update pathways
                            keeptrack.put(adj, current); // update parent country information
                            nextcountries.add(adj); // add adj to priority queue
                        }
                    }
                }
            }
        }
        // pathways should now has all the smallest distances to each country and
        // keeptrack has kept track of the corresponding pathways
        return keeptrack;
    }

    // takes names from user, makes sure they're valid, assigns country1 and
    // country2 String variables as starting and destination countries' stateids
    // to navigate the big hashmap with <3
    public void acceptUserInput() {
        Scanner scanin = new Scanner(System.in);
        System.out.println("(enter 'stop' to stop at any time)");
        System.out.println("Enter your starting country: ");
        String start = scanin.nextLine();
        // loop for invalid input
        while (!start.equals("stop")
                && (!name2id.containsKey(start) || start.equals("Kosovo") || start.equals("South Sudan"))) {
            System.out.println("that is not a recognized country");
            System.out.println("Enter your starting country: ");
            start = scanin.nextLine();
        }
        if (start.equals("stop")) {
            stop = true; // set boolean main checks to call this method
        } else {
            country1 = name2id.get(start); // set instance variable
            System.out.println("Enter your destination country: ");
            String dest = scanin.nextLine();
            // loop for invalid input
            while (!dest.equals("stop")
                    && (!name2id.containsKey(dest) || dest.equals("Kosovo") || dest.equals("South Sudan"))) {
                System.out.println("that is not a recognized country");
                System.out.println("Enter your destination country: ");
                dest = scanin.nextLine();
            }
            if (dest.equals("stop")) {
                stop = true; // set boolean main checks to call this method
            } else {
                country2 = name2id.get(dest); // set instance variable
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        IRoadTrip a3 = new IRoadTrip(args);
        while (!a3.stop) {
            System.out.println();
            a3.acceptUserInput();
            if (!a3.stop) {
                List<String> path = a3.findPath(a3.country1, a3.country2);
                if (path.isEmpty()) {
                    System.out.println("no path found");
                } else {
                    System.out.println();
                    for (String travel : path) {
                        System.out.println(travel);
                    }
                }
            }
        }
        System.out.println("all done!");
    }
}
