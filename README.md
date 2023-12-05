# CS 245 (Fall 2023) - Assignment 3 - IRoadTrip

The main data structures used in my implementation os IRoadTrip are: 
    HashMap<String, String> name2id: connects countries names to their stateid
    HashMap<String, Integer> distances: country stateids combined as keys and km between capitols as value
    HashMap<String, ArrayList<String>> adjacents: stateid of a country as key and list of stateids of countries that border it as values
    HashMap<String, HashMap<String, Integer>> bighash: stateid of country as key and hashmap (smallhash) as value. smallhash has stateids
      of the countries that border the country of the key in the bighash, and the distance between their capitols as values

    These data structures are built when the constructor of IRoadTrip is called. It employs helper methods
